package com.codefaucet.LoanMan.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.common.PagedSearchResponse;
import com.codefaucet.LoanMan.common.RequestContainer;
import com.codefaucet.LoanMan.common.ResponseContainer;
import com.codefaucet.LoanMan.common.StringHelper;
import com.codefaucet.LoanMan.common.StringHelper.CharSet;
import com.codefaucet.LoanMan.common.EnumResponseStatus;
import com.codefaucet.LoanMan.common.Util;
import com.codefaucet.LoanMan.controller.dto.GroupTypeDTO;
import com.codefaucet.LoanMan.model.GroupType;
import com.codefaucet.LoanMan.service.GroupTypeService;

@RestController
@RequestMapping("/api/grouptype")
public class GroupTypeController {
    
    @Autowired
    private GroupTypeService groupTypeService;
    
    private final Logger logger = LogManager.getLogger();
    
    @GetMapping("/testInsertData")
    public void testInsertData() {
	for (int a = 0; a < 129; a++) {
	    GroupType groupType = new GroupType();
	    groupType.setCode("CODE" + a);
	    groupType.setDescription(groupType.getCode() + " description");
	    groupTypeService.save(groupType);
	}
    }
    
    @PostMapping("/search")
    public PagedSearchResponse<GroupTypeDTO> search(@RequestBody PagedSearchRequest param) {
	logger.debug("search | param: " + param.toString());
	
	PagedSearchResponse<GroupTypeDTO> response = new PagedSearchResponse<GroupTypeDTO>();
	
	try {
	    List<GroupType> groupTypes = groupTypeService.search(param);
	    Long totalCount = groupTypeService.countSearchResult(param);
	    
	    groupTypes.forEach(item -> {
		GroupTypeDTO dto = new GroupTypeDTO(item.getId(), item.isActive(), item.getCode(), item.getDescription());
		response.getContent().add(dto);
	    });
	    response.setTotalPageCount(Util.getTotalPage(totalCount, param.getPageSize()));
	} catch (Exception ex) {
	    response.setStatus(EnumResponseStatus.FAILED);
	    response.setMessage(ex.getMessage());
	    logger.error("search | Error: " + ex.getMessage(), ex);
	}
	
	return response;
    }
    
    @PostMapping("/findById")
    public GroupTypeDTO findById(@RequestBody Map<String, Long> param) {
	logger.debug("findById | param: " + param);
	
	long id = param.get("id");
	GroupType groupType = id == 0 ? new GroupType() : groupTypeService.findGroupTypeById(id);
	GroupTypeDTO dto = new GroupTypeDTO(groupType.getId(), groupType.isActive(), groupType.getCode(), groupType.getDescription());
	return dto;
    }
    
    @PostMapping("/save")
    public ResponseContainer<GroupTypeDTO> save(@RequestBody RequestContainer<GroupTypeDTO> param) {
	logger.debug("save | param: " + param);
	
	ResponseContainer<GroupTypeDTO> response = new ResponseContainer<GroupTypeDTO>();
	try {
	    GroupTypeDTO dto = param.getContent();
	    
	    // basic validation
	    if (StringHelper.isNullOrEmpty(dto.getCode())) {
		response.getErrorMap().put("code", "Code can't be empty.");
	    }
	    if (!StringHelper.isInCharSet(CharSet.ALPHANUMERIC, dto.getCode().trim())) {
		String codeError = response.getErrorMap().getOrDefault("code", "");
		codeError += (codeError.isEmpty() ? "" : "<br />") + "Only alphanumeric characters are allowed.";
		response.getErrorMap().put("code", codeError);
	    }
	    if (StringHelper.isNullOrEmpty(dto.getDescription())) {
		response.getErrorMap().put("description", "Description can't be empty.");
	    }
	    if (!response.getErrorMap().isEmpty()) {
		return response.failed();
	    }
	    
	    dto.setCode(dto.getCode().trim().toUpperCase());
	    dto.setDescription(dto.getDescription().trim());
	    
	    // check if code is used by other group type
	    GroupType groupType = groupTypeService.findGroupTypeByCode(param.getContent().getCode());
	    if (groupType != null && groupType.getId() != dto.getId()) {
		response.getErrorMap().put("code", "Code '" + dto.getCode() + "' is not available.");
		return response.failed();
	    }
	    
	    // if group type was found by above query and code validation has no problem, then we already have the correct group type
	    if (groupType == null) {
		groupType = param.getContent().getId() == 0 ? new GroupType() : groupTypeService.findGroupTypeById(param.getContent().getId());
	    }
	    
	    // map values
	    groupType.setCode(dto.getCode());
	    groupType.setDescription(dto.getDescription());
	    groupType = groupTypeService.save(groupType);
	    
	    dto.setId(groupType.getId());
	    return response.successful(dto);
	} catch (Exception ex) {
	    logger.error("save | Error: " + ex.getMessage(), ex);
	    
	    return response.failed(ex.getMessage());
	}
    }
    
    @PostMapping("delete")
    public ResponseContainer<Boolean> delete(@RequestBody RequestContainer<Long> param) {
	logger.debug("delete | param: " + param);
	
	ResponseContainer<Boolean> response = new ResponseContainer<Boolean>();
	long id = param.getContent();
	try {
	    groupTypeService.deleteGroupTypeById(id);

	    return response.successful(true);
	} catch (Exception ex) {
	    logger.error("delete | Error: " + ex.getMessage(), ex);
	    
	    return response.failed(ex.getMessage());
	}
    }
    
}
