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

import com.codefaucet.LoanMan.common.EnumModuleAccessId;
import com.codefaucet.LoanMan.common.LoggingHelper;
import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.common.PagedSearchResponse;
import com.codefaucet.LoanMan.common.ResponseContainer;
import com.codefaucet.LoanMan.common.StringHelper;
import com.codefaucet.LoanMan.common.Util;
import com.codefaucet.LoanMan.dto.UserProfileDTO;
import com.codefaucet.LoanMan.model.UserProfile;
import com.codefaucet.LoanMan.service.UserProfileService;

@RestController
@RequestMapping("/api/userprofile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;
    
    @Autowired
    private LoggingHelper loggingHelper;
    
    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    
    @GetMapping("/restore/{userProfileId}")
    public void restore(@PathVariable long userProfileId) {
	logger.debug("restore | userProfileId: " + userProfileId);
	
	userProfileService.restore(userProfileId);
    }
    
    @GetMapping("/delete/{userProfileId}")
    public void delete(@PathVariable long userProfileId) {
	logger.debug("delete | userProfileId: " + userProfileId);
	
	userProfileService.delete(userProfileId);
    }
    
    @GetMapping("/findById/{userProfileId}")
    public UserProfileDTO findById(@PathVariable("userProfileId") long userProfileId) {
	logger.debug("findById | userProfileId: " + userProfileId);
	
	UserProfile userProfile = userProfileId == 0 ? new UserProfile() : userProfileService.findById(userProfileId);
	
	UserProfileDTO dto = new UserProfileDTO(userProfile.getId(), userProfile.isActive(), userProfile.getName(), userProfile.getDescription());
	if (userProfileId == 0) {
	    for (EnumModuleAccessId accessId : EnumModuleAccessId.values()) {
		dto.getModuleAccessIds().add(accessId);
	    }
	} else {
	    dto.setModuleAccessIds(userProfile.getModuleAccessIds());   
	}
	
	return dto;
    }
    
    @PostMapping("/create")
    public ResponseContainer<UserProfileDTO> create(@RequestBody UserProfileDTO dto)  {
	logger.debug("create | dto: " + loggingHelper.asString(dto));
	
	ResponseContainer<UserProfileDTO> response = new ResponseContainer<UserProfileDTO>();
	
	// basic validation
	if (StringHelper.isNullOrEmpty(dto.getName())) {
	    response.getErrorMap().put("name", "Name is required.");
	}
	if (!response.getErrorMap().isEmpty()) {
	    return response.failed();
	}
	
	// check if name is already in use
	UserProfile duplicateProfile = userProfileService.findByName(dto.getName());
	if (duplicateProfile != null) {
	    response.getErrorMap().put("name", "Name '" + dto.getName().trim() + "' is already in use.");
	    return response.failed();
	}
	
	dto.setName(dto.getName().trim());
	dto.setDescription(dto.getDescription() == null ? "" : dto.getDescription().trim());
	
	UserProfile userProfile = new UserProfile(dto.getName(), dto.getDescription());
	userProfile.getModuleAccessIds().addAll(dto.getModuleAccessIds());
	userProfile = userProfileService.create(userProfile);
	
	dto = new UserProfileDTO(userProfile.getId(), userProfile.isActive(), userProfile.getName(), userProfile.getDescription());
	dto.setModuleAccessIds(userProfile.getModuleAccessIds());
	
	return response.successful(dto);
    }
    
    @PostMapping("/edit")
    public ResponseContainer<UserProfileDTO> edit(@RequestBody UserProfileDTO dto)  {
	logger.debug("create | dto: " + loggingHelper.asString(dto));
	
	ResponseContainer<UserProfileDTO> response = new ResponseContainer<UserProfileDTO>();
	
	// basic validation
	if (StringHelper.isNullOrEmpty(dto.getName())) {
	    response.getErrorMap().put("name", "Name is required.");
	}
	if (!response.getErrorMap().isEmpty()) {
	    return response.failed();
	}
	
	// check if name is already in use
	UserProfile userProfile = userProfileService.findByName(dto.getName().trim());
	if (userProfile != null && userProfile.getId() != dto.getId()) {
	    response.getErrorMap().put("name", "Name '" + dto.getName().trim() + "' is already in use.");
	    return response.failed();
	}
	
	dto.setName(dto.getName().trim());
	dto.setDescription(dto.getDescription() == null ? "" : dto.getDescription().trim());
	
	if (userProfile == null) {
	    userProfile = userProfileService.findById(dto.getId());
	}
	
	userProfile.setName(dto.getName());
	userProfile.setDescription(dto.getDescription());
	userProfile.getModuleAccessIds().clear();
	userProfile.getModuleAccessIds().addAll(dto.getModuleAccessIds());
	userProfile = userProfileService.create(userProfile);
	
	dto = new UserProfileDTO(userProfile.getId(), userProfile.isActive(), userProfile.getName(), userProfile.getDescription());
	dto.setModuleAccessIds(userProfile.getModuleAccessIds());
	
	return response.successful(dto);
    }
    
    @PostMapping("/search")
    public PagedSearchResponse<UserProfileDTO> search(@RequestBody PagedSearchRequest param) {
	logger.debug("search | param: " + loggingHelper.asString(param));
	List<UserProfile> userProfiles = userProfileService.search(param);
	Long totalResultCount = userProfileService.countSearchResult(param);
	
	PagedSearchResponse<UserProfileDTO> result = new PagedSearchResponse<UserProfileDTO>();
	userProfiles.forEach(item -> {
	    UserProfileDTO dto = new UserProfileDTO(item.getId(), item.isActive(), item.getName(), item.getDescription());
	    dto.setModuleAccessIds(item.getModuleAccessIds());
	    result.getContent().add(dto);
	});
	result.setTotalPageCount(Util.getTotalPage(totalResultCount, param.getPageSize()));
	result.setColumnSorting(param.getColumnSorting());
	
	return result;
    }
    
}
