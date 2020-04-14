package com.codefaucet.LoanMan.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codefaucet.LoanMan.common.LoggingHelper;
import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.common.PagedSearchResponse;
import com.codefaucet.LoanMan.common.ResponseContainer;
import com.codefaucet.LoanMan.common.StringHelper;
import com.codefaucet.LoanMan.common.StringHelper.CharSet;
import com.codefaucet.LoanMan.common.Util;
import com.codefaucet.LoanMan.dto.GroupTypeDTO;
import com.codefaucet.LoanMan.model.GroupType;
import com.codefaucet.LoanMan.service.GroupTypeService;

@RestController
@RequestMapping("/api/grouptype")
public class GroupTypeController {
    
    @Autowired
    private GroupTypeService groupTypeService;
    
    private final Logger logger = LoggerFactory.getLogger(GroupTypeController.class);
    
    @Autowired
    private LoggingHelper loggingHelper;
    
    @GetMapping("/findById/{groupTypeId}")
    public GroupTypeDTO findById(@PathVariable long groupTypeId) {
	logger.debug("findById | groupTypeId: " + groupTypeId);
	
	GroupType groupType = groupTypeId == 0 ? new GroupType() : groupTypeService.findById(groupTypeId);
	GroupTypeDTO dto = new GroupTypeDTO(groupType.getId(), groupType.isActive(), groupType.getName(), groupType.getDescription());
	return dto;
    }
    
    @GetMapping("/delete/{groupTypeId}")
    public void delete(@PathVariable long groupTypeId) {
	logger.debug("delete | groupTypeId: " + groupTypeId);
	groupTypeService.delete(groupTypeId);
    }
    
    @GetMapping("/restore/{groupTypeId}")
    public void restore(@PathVariable long groupTypeId) {
	logger.debug("restore | groupTypeId: " + groupTypeId);
	groupTypeService.restore(groupTypeId);
    }
    
    @PostMapping("/search")
    public PagedSearchResponse<GroupTypeDTO> search(@RequestBody PagedSearchRequest param) {
	logger.debug("search | param: " + loggingHelper.asString(param));
	
	PagedSearchResponse<GroupTypeDTO> response = new PagedSearchResponse<GroupTypeDTO>();
	List<GroupType> groupTypes = groupTypeService.search(param);
	Long totalCount = groupTypeService.countSearchResult(param);
	    
	groupTypes.forEach(item -> {
	    GroupTypeDTO dto = new GroupTypeDTO(item.getId(), item.isActive(), item.getName(), item.getDescription());
	    response.getContent().add(dto);
	});
	response.setTotalPageCount(Util.getTotalPage(totalCount, param.getPageSize()));
	response.setColumnSorting(param.getColumnSorting());
	
	return response;
    }
    
    @PostMapping("/create")
    public ResponseContainer<GroupTypeDTO> create(@RequestBody GroupTypeDTO dto) {
	logger.debug("save | dto: " + loggingHelper.asString(dto));
	
	ResponseContainer<GroupTypeDTO> response = new ResponseContainer<GroupTypeDTO>();
	
	if (StringHelper.isNullOrEmpty(dto.getName())) {
		response.getErrorMap().put("name", "Name can't be empty.");
	}
	if (!StringHelper.isInCharSet(CharSet.ALPHANUMERIC, dto.getName().trim())) {
	    String nameError = response.getErrorMap().getOrDefault("name", "");
	    nameError += (nameError.isEmpty() ? "" : "<br />") + "Only alphanumeric characters are allowed.";
	    response.getErrorMap().put("name", nameError);
	}
	if (!response.getErrorMap().isEmpty()) {
	    return response.failed();
	}
	dto.setName(dto.getName().trim());
	dto.setDescription(dto.getDescription() == null ? "" : dto.getDescription().trim());
	
	GroupType duplicate = groupTypeService.findByName(dto.getName().trim());
	if (duplicate != null) {
	    response.getErrorMap().put("name", "Name '" + dto.getName() + "' is already in use.");
	    return response.failed();
	}
	
	GroupType groupType = new GroupType(dto.getName(), dto.getDescription());
	groupType = groupTypeService.create(groupType);
	
	dto = new GroupTypeDTO(groupType.getId(), groupType.isActive(), groupType.getName(), groupType.getDescription());
	return response.successful(dto);
    }
    
    @PostMapping("/edit")
    public ResponseContainer<GroupTypeDTO> edit(@RequestBody GroupTypeDTO dto) {
	logger.debug("edit | dto: " + loggingHelper.asString(dto));
	
	ResponseContainer<GroupTypeDTO> response = new ResponseContainer<GroupTypeDTO>();
	
	if (StringHelper.isNullOrEmpty(dto.getName())) {
		response.getErrorMap().put("name", "Name can't be empty.");
	}
	if (!StringHelper.isInCharSet(CharSet.ALPHANUMERIC, dto.getName().trim())) {
	    String nameError = response.getErrorMap().getOrDefault("name", "");
	    nameError += (nameError.isEmpty() ? "" : "<br />") + "Only alphanumeric characters are allowed.";
	    response.getErrorMap().put("name", nameError);
	}
	if (!response.getErrorMap().isEmpty()) {
	    return response.failed();
	}
	dto.setName(dto.getName().trim());
	dto.setDescription(dto.getDescription() == null ? "" : dto.getDescription().trim());
	
	GroupType duplicate = groupTypeService.findByName(dto.getName().trim());
	if (duplicate != null && duplicate.getId() != dto.getId()) {
	    response.getErrorMap().put("name", "Name '" + dto.getName() + "' is already in use.");
	    return response.failed();
	}
	
	GroupType groupType = duplicate != null ? duplicate : groupTypeService.findById(dto.getId());
	groupType.setName(dto.getName());
	groupType.setDescription(dto.getDescription());
	groupType = groupTypeService.edit(groupType);
	
	dto = new GroupTypeDTO(groupType.getId(), groupType.isActive(), groupType.getName(), groupType.getDescription());
	return response.successful(dto);
    }
    
}
