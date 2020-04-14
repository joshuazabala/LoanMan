package com.codefaucet.LoanMan.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codefaucet.LoanMan.common.LoggingHelper;
import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.common.PagedSearchResponse;
import com.codefaucet.LoanMan.common.RequestContainer;
import com.codefaucet.LoanMan.common.ResponseContainer;
import com.codefaucet.LoanMan.common.StringHelper;
import com.codefaucet.LoanMan.common.StringHelper.CharSet;
import com.codefaucet.LoanMan.dto.GroupDTO;
import com.codefaucet.LoanMan.common.Util;
import com.codefaucet.LoanMan.model.Group;
import com.codefaucet.LoanMan.model.GroupType;
import com.codefaucet.LoanMan.service.GroupService;
import com.codefaucet.LoanMan.service.GroupTypeService;

@RestController
@RequestMapping("/api/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupTypeService groupTypeService;

    private final Logger logger = LogManager.getLogger();
    
    @Autowired
    private LoggingHelper loggingHelper;

    @PostMapping("/search")
    public PagedSearchResponse<GroupDTO> search(@RequestBody PagedSearchRequest param) {
	logger.debug("search | param: " + loggingHelper.asString(param));

	PagedSearchResponse<GroupDTO> response = new PagedSearchResponse<GroupDTO>();
	try {
	    List<Group> groups = groupService.search(param);
	    long totalCount = groupService.countSearchResult(param);

	    List<GroupDTO> dtos = new ArrayList<GroupDTO>();
	    groups.forEach(item -> {
		GroupDTO dto = new GroupDTO(item.getId(), item.isActive(), item.getName(), item.getDescription(),
			item.getType().getId(), item.getName() + " - " + item.getDescription());
		dtos.add(dto);
	    });
	    return response.successful(dtos, Util.getTotalPage(totalCount, param.getPageSize()));
	} catch (Exception ex) {
	    logger.error("search | Error: " + ex.getMessage(), ex);

	    return response.failed(ex.getMessage());
	}
    }

    @PostMapping("/listGroupTypes")
    public Map<Long, String> listGroupTypes() {
	List<GroupType> groupTypes = groupTypeService.listGroupTypes();
	Map<Long, String> list = new HashMap<Long, String>();
	groupTypes.forEach(item -> {
	    list.put(item.getId(), item.getName() + " - " + item.getDescription());
	});
	return list;
    }
    
    @PostMapping("/findById")
    public GroupDTO findById(@RequestBody Map<String, Long> param) {
	logger.debug("findById | param: " + loggingHelper.asString(param));
	
	long id = param.get("id");
	Group group = id == 0 ? new Group() : groupService.findById(id);
	GroupType groupType = group.getType();
	long groupTypeId = groupType == null ? 0l : groupType.getId();
	String groupTypeText = groupType == null ? "" : groupType.getName() + " - " + groupType.getDescription();
	
	GroupDTO dto = new GroupDTO(group.getId(), group.isActive(), group.getName(), group.getDescription(), groupTypeId, groupTypeText);
	return dto;
    }

    @PostMapping("/save")
    public ResponseContainer<GroupDTO> save(@RequestBody RequestContainer<GroupDTO> param) {
	logger.debug("save | param: " + loggingHelper.asString(param));
	
	ResponseContainer<GroupDTO> response = new ResponseContainer<GroupDTO>();
	try {
	    GroupDTO dto = param.getContent();
	    
	    // basic validation
	    if (StringHelper.isNullOrEmpty(dto.getName())) {
		response.getErrorMap().put("name", "Name can't be empty.");
	    }
	    if (!StringHelper.isInCharSet(CharSet.ALPHANUMERIC, dto.getName().trim())) {
		String nameError = response.getErrorMap().getOrDefault("name", "");
		nameError += (nameError.isEmpty() ? "" : "<br />") + "Only alphanumeric characters are allowed.";
		response.getErrorMap().put("name", nameError);
	    }
	    if (StringHelper.isNullOrEmpty(dto.getDescription())) {
		response.getErrorMap().put("description", "Description can't be empty.");
	    }
	    if (dto.getGroupTypeId() == 0) {
		response.getErrorMap().put("groupTypeId", "Type is required.");
	    }
	    if (!response.getErrorMap().isEmpty()) {
		return response.failed();
	    }
	    
	    dto.setName(dto.getName().trim().toUpperCase());
	    dto.setDescription(dto.getDescription().trim());
	    
	    // check if name is used by another
	    Group model = groupService.findByName(param.getContent().getName());
	    if (model != null && model.getId() != dto.getId()) {
		response.getErrorMap().put("name", "Name '" + dto.getName() + "' is not available.");
		return response.failed();
	    }
	    
	    // if found by above query and name validation has no problem, then we already have the correct one
	    if (model == null) {
		model = param.getContent().getId() == 0 ? new Group() : groupService.findById(param.getContent().getId());
	    } 
	    
	    // map values
	    model.setName(dto.getName());
	    model.setDescription(dto.getDescription());
	    if (model.getType() == null || dto.getGroupTypeId() != model.getType().getId()) {
		GroupType groupType = groupTypeService.findById(dto.getGroupTypeId());
		model.setType(groupType);
	    }
	    model = groupService.save(model);
	    
	    dto.setId(model.getId());
	    dto.setGroupTypeId(model.getType().getId());
	    dto.setGroupType(model.getType().toString());
	    return response.successful(dto);
	} catch (Exception ex) {
	    logger.error("save | Error: " + ex.getMessage(), ex);
	    
	    return response.failed(ex.getMessage());
	}
    }
    
    @PostMapping("delete")
    public ResponseContainer<Boolean> delete(@RequestBody RequestContainer<Long> param) {
	logger.debug("delete | param: " + loggingHelper.asString(param));
	
	ResponseContainer<Boolean> response = new ResponseContainer<Boolean>();
	long id = param.getContent();
	try {
	    long memberCount = groupService.getMemberCount(id);
	    if (memberCount > 0) {
		return response.failed("This group has members.");
	    }
	    groupService.deleteById(id);

	    return response.successful(true);
	} catch (Exception ex) {
	    logger.error("delete | Error: " + ex.getMessage(), ex);
	    
	    return response.failed(ex.getMessage());
	}
    }
    
}
