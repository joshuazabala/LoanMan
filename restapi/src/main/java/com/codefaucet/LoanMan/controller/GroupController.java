package com.codefaucet.LoanMan.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.codefaucet.LoanMan.dto.GroupDTO;
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

    private final Logger logger = LoggerFactory.getLogger(GroupController.class);
    
    @Autowired
    private LoggingHelper loggingHelper;

    @PostMapping("/search")
    public PagedSearchResponse<GroupDTO> search(@RequestBody PagedSearchRequest param) {
	logger.debug("search | param: " + loggingHelper.asString(param));

	PagedSearchResponse<GroupDTO> response = new PagedSearchResponse<GroupDTO>();
	List<Group> groups = groupService.search(param);
	long totalCount = groupService.countSearchResult(param);

	List<GroupDTO> dtos = new ArrayList<GroupDTO>();
	groups.forEach(item -> {
	    GroupDTO dto = new GroupDTO(item.getId(), item.isActive(), item.getName(), item.getDescription());
	    if (item.getType() != null) {
		dto.setGroupType(item.getType().getName());
		dto.setGroupTypeId(item.getType().getId());
	    }
	    dtos.add(dto);
	});
	response.setTotalPageCount(Util.getTotalPage(totalCount, param.getPageSize()));
	response.setColumnSorting(param.getColumnSorting());
	
	return response.successful(dtos, Util.getTotalPage(totalCount, param.getPageSize()));
    }

    @PostMapping("/listGroupTypes")
    public Map<Long, String> listGroupTypes() {
	List<GroupType> groupTypes = groupTypeService.listGroupTypes();
	Map<Long, String> list = new HashMap<Long, String>();
	groupTypes.forEach(item -> {
	    list.put(item.getId(), item.getName());
	});
	return list;
    }
    
    @GetMapping("/findById/{groupId}")
    public GroupDTO findById(@PathVariable long groupId) {
	logger.debug("findById | groupId: " + groupId);
	
	Group group = groupId == 0 ? new Group() : groupService.findById(groupId);
	GroupDTO dto = new GroupDTO(group.getId(), group.isActive(), group.getName(), group.getDescription());
	if (group.getType() != null) {
	    dto.setGroupType(group.getType().getName());
	    dto.setGroupTypeId(group.getType().getId());
	}
	return dto;
    }

    @GetMapping("/delete/{groupId}")
    public void delete(@PathVariable long groupId) {
	logger.debug("delete | groupId: " + groupId);
	
	groupService.delete(groupId);
    }
    
    @GetMapping("/restore/{groupId}")
    public void restore(@PathVariable long groupId) {
	logger.debug("restore | groupId: " + groupId);
	
	groupService.restore(groupId);
    }
    
    @PostMapping("/create")
    public ResponseContainer<GroupDTO> create(@RequestBody GroupDTO dto) {
	logger.debug("create | dto: " + loggingHelper.asString(dto));
	
	ResponseContainer<GroupDTO> response = new ResponseContainer<GroupDTO>();
	
	if (StringHelper.isNullOrEmpty(dto.getName())) {
	    response.getErrorMap().put("name", "Name can't be empty.");
	}
	if (!StringHelper.isInCharSet(CharSet.ALPHANUMERIC, dto.getName().trim())) {
	    String nameError = response.getErrorMap().getOrDefault("name", "");
	    nameError += (nameError.isEmpty() ? "" : "<br />") + "Only alphanumeric characters are allowed.";
	    response.getErrorMap().put("name", nameError);
	}
	if (dto.getGroupTypeId() == 0) {
	    response.getErrorMap().put("groupTypeId", "This field is required.");
	}
	if (!response.getErrorMap().isEmpty()) {
	    return response.failed();
	}
	dto.setName(dto.getName().trim());
	dto.setDescription(dto.getDescription() == null ? "" : dto.getDescription().trim());
	
	Group duplicate = groupService.findByName(dto.getName());
	if (duplicate != null) {
	    response.getErrorMap().put("name", "Name '" + dto.getName() + "' is already in use.");
	    return response.failed();
	}
	
	Group group = new Group(dto.getName(), dto.getDescription());
	GroupType groupType = groupTypeService.findById(dto.getGroupTypeId());
	group.setType(groupType);
	group = groupService.create(group);
	dto = new GroupDTO(group.getId(), group.isActive(), group.getName(), group.getDescription());
	dto.setGroupType(groupType.getName());
	dto.setGroupTypeId(groupType.getId());
	
	return response.successful(dto);
    }
    
    @PostMapping("/edit")
    public ResponseContainer<GroupDTO> edit(@RequestBody GroupDTO dto) {
	logger.debug("create | dto: " + loggingHelper.asString(dto));
	
	ResponseContainer<GroupDTO> response = new ResponseContainer<GroupDTO>();
	
	if (StringHelper.isNullOrEmpty(dto.getName())) {
	    response.getErrorMap().put("name", "Name can't be empty.");
	}
	if (!StringHelper.isInCharSet(CharSet.ALPHANUMERIC, dto.getName().trim())) {
	    String nameError = response.getErrorMap().getOrDefault("name", "");
	    nameError += (nameError.isEmpty() ? "" : "<br />") + "Only alphanumeric characters are allowed.";
	    response.getErrorMap().put("name", nameError);
	}
	if (dto.getGroupTypeId() == 0) {
	    response.getErrorMap().put("groupTypeId", "This field is required.");
	}
	if (!response.getErrorMap().isEmpty()) {
	    return response.failed();
	}
	dto.setName(dto.getName().trim());
	dto.setDescription(dto.getDescription() == null ? "" : dto.getDescription().trim());
	
	Group duplicate = groupService.findByName(dto.getName());
	if (duplicate != null && duplicate.getId() != dto.getId()) {
	    response.getErrorMap().put("name", "Name '" + dto.getName() + "' is already in use.");
	    return response.failed();
	}
	
	Group group = duplicate == null ? groupService.findById(dto.getId()) : duplicate;
	group.setName(dto.getName());
	group.setDescription(dto.getDescription());
	if (dto.getGroupTypeId() != group.getType().getId()) {
	    GroupType groupType = groupTypeService.findById(dto.getGroupTypeId());
	    group.setType(groupType);
	}
	group = groupService.edit(group);
	dto = new GroupDTO(group.getId(), group.isActive(), group.getName(), group.getDescription());
	dto.setGroupType(group.getType().getName());
	dto.setGroupTypeId(group.getType().getId());
	
	return response.successful(dto);
    }
    
}
