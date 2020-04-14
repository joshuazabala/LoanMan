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
import com.codefaucet.LoanMan.common.Util;
import com.codefaucet.LoanMan.dto.UserDTO;
import com.codefaucet.LoanMan.model.User;
import com.codefaucet.LoanMan.model.UserProfile;
import com.codefaucet.LoanMan.service.UserProfileService;
import com.codefaucet.LoanMan.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;
    
    @Autowired
    private LoggingHelper loggingHelper;
    
    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    
    @GetMapping("/findById/{userId}")
    public UserDTO findById(@PathVariable long userId) {
	logger.debug("findById | userId: " + userId);
	
	User user = userId == 0 ? new User() : userService.findById(userId);
	UserDTO dto = new UserDTO(
		user.getId(), 
		user.isActive(), 
		user.getUsername(), 
		user.getFirstName(), 
		user.getMiddleName(), 
		user.getLastName(), 
		user.getContactNumber(), 
		user.getEmailAddress()
		);
	if (user.getProfile() != null) {
	    dto.setProfile(user.getProfile().getName());
	    dto.setProfileId(user.getProfile().getId());
	}
	return dto;
    }
    
    @PostMapping("/search")
    public PagedSearchResponse<UserDTO> search(@RequestBody PagedSearchRequest param) {
	logger.debug("search | param: " + loggingHelper.asString(param));
	List<User> users = userService.search(param);
	Long totalResultCount = userService.countSearchResult(param);
	
	PagedSearchResponse<UserDTO> result = new PagedSearchResponse<UserDTO>();
	users.forEach(item -> {
	    UserDTO dto = new UserDTO(
		    item.getId(), 
		    item.isActive(), 
		    item.getUsername(), 
		    item.getFirstName(), 
		    item.getMiddleName(), 
		    item.getLastName(), 
		    item.getContactNumber(), 
		    item.getEmailAddress()
		    );
	    dto.setProfile(item.getProfile().getName());
	    dto.setProfileId(item.getProfile().getId());
	    result.getContent().add(dto);
	});
	result.setTotalPageCount(Util.getTotalPage(totalResultCount, param.getPageSize()));
	result.setColumnSorting(param.getColumnSorting());
	
	return result;
    }
    
    @PostMapping("/create")
    public ResponseContainer<UserDTO> create(@RequestBody UserDTO dto) {
	logger.debug("create | dto: " + loggingHelper.asString(dto));
	
	ResponseContainer<UserDTO> response = new ResponseContainer<UserDTO>();
	
	// basic validation
	if (StringHelper.isNullOrEmpty(dto.getUsername())) {
	    response.getErrorMap().put("username", "Username is required.");
	}
	if (StringHelper.isNullOrEmpty(dto.getFirstName())) {
	    response.getErrorMap().put("firstName", "First name is required.");
	}
	if (StringHelper.isNullOrEmpty(dto.getLastName())) {
	    response.getErrorMap().put("lastname", "Last name is required.");
	}
	if (StringHelper.isNullOrEmpty(dto.getEmailAddress())) {
	    response.getErrorMap().put("emailAddress", "Email address is required.");
	}
	if (StringHelper.isNullOrEmpty(dto.getContactNumber())) {
	    response.getErrorMap().put("contactNumber", "Contact number is required.");
	}
	if (dto.getProfileId() == 0) {
	    response.getErrorMap().put("profileId", "Profile is required.");
	}
	if (!response.getErrorMap().isEmpty()) {
	    return response.failed();
	}
	
	// check if name is already in use
	User duplicateUser = userService.findByUsername(dto.getUsername());
	if (duplicateUser != null) {
	    response.getErrorMap().put("username", "Username '" + dto.getUsername().trim() + "' is already in use.");
	    return response.failed();
	}
	
	UserProfile profile = userProfileService.findById(dto.getProfileId());
	if (profile == null) {
	    response.getErrorMap().put("profileId", "Profile is required.");
	    return response.failed();
	}
	
	dto.setUsername(dto.getUsername().trim());
	dto.setFirstName(dto.getFirstName().trim());
	dto.setMiddleName(dto.getMiddleName() == null ? "" : dto.getMiddleName().trim());
	dto.setLastName(dto.getLastName().trim());
	dto.setEmailAddress(dto.getEmailAddress().trim());
	dto.setContactNumber(dto.getContactNumber().trim());
	
	User item = new User(dto.getUsername(), dto.getFirstName(), dto.getMiddleName(), dto.getLastName(), dto.getContactNumber(), dto.getEmailAddress());
	item.setProfile(profile);
	item = userService.create(item);
	
	dto = new UserDTO(
		item.getId(), 
		item.isActive(), 
		item.getUsername(), 
		item.getFirstName(), 
		item.getMiddleName(), 
		item.getLastName(), 
		item.getContactNumber(), 
		item.getEmailAddress()
		);
	return response.successful(dto);
    }
    
    @PostMapping("/edit")
    public ResponseContainer<UserDTO> edit(@RequestBody UserDTO dto) {
	logger.debug("edit | dto: " + loggingHelper.asString(dto));
	
	ResponseContainer<UserDTO> response = new ResponseContainer<UserDTO>();
	
	// basic validation
	if (StringHelper.isNullOrEmpty(dto.getUsername())) {
	    response.getErrorMap().put("username", "Username is required.");
	}
	if (StringHelper.isNullOrEmpty(dto.getFirstName())) {
	    response.getErrorMap().put("firstName", "First name is required.");
	}
	if (StringHelper.isNullOrEmpty(dto.getLastName())) {
	    response.getErrorMap().put("lastName", "Last name is required.");
	}
	if (StringHelper.isNullOrEmpty(dto.getEmailAddress())) {
	    response.getErrorMap().put("emailAddress", "Email address is required.");
	}
	if (StringHelper.isNullOrEmpty(dto.getContactNumber())) {
	    response.getErrorMap().put("contactNumber", "Contact number is required.");
	}
	if (dto.getProfileId() == 0) {
	    response.getErrorMap().put("profileId", "Profile is required.");
	}
	if (!response.getErrorMap().isEmpty()) {
	    return response.failed();
	}
	
	// check if name is already in use
	User user = userService.findByUsername(dto.getUsername());
	if (user != null && user.getId() != dto.getId()) {
	    response.getErrorMap().put("username", "Username '" + dto.getUsername().trim() + "' is already in use.");
	    return response.failed();
	}
	
	UserProfile profile = userProfileService.findById(dto.getProfileId());
	if (profile == null) {
	    response.getErrorMap().put("profileId", "Profile is required.");
	    return response.failed();
	}
	
	dto.setUsername(dto.getUsername().trim());
	dto.setFirstName(dto.getFirstName().trim());
	dto.setMiddleName(dto.getMiddleName() == null ? "" : dto.getMiddleName().trim());
	dto.setLastName(dto.getLastName().trim());
	dto.setEmailAddress(dto.getEmailAddress().trim());
	dto.setContactNumber(dto.getContactNumber().trim());
	
	if (user == null) {
	    user = userService.findById(dto.getId());
	}
	
	user.setUsername(dto.getUsername());
	user.setFirstName(dto.getFirstName());
	user.setMiddleName(dto.getMiddleName());
	user.setLastName(dto.getLastName());
	user.setEmailAddress(dto.getEmailAddress());
	user.setContactNumber(dto.getContactNumber());
	if (user.getProfile().getId() != dto.getProfileId()) {
	    user.setProfile(profile);
	}
	user = userService.edit(user);
	
	dto = new UserDTO(
		user.getId(), 
		user.isActive(), 
		user.getUsername(), 
		user.getFirstName(), 
		user.getMiddleName(), 
		user.getLastName(), 
		user.getContactNumber(), 
		user.getEmailAddress()
		);
	return response.successful(dto);
    }
    
    @GetMapping("/delete/{userId}")
    public void delete(@PathVariable long userId) {
	logger.debug("delete | userId: " + userId);
	
	userService.delete(userId);
    }
    
    @GetMapping("/restore/{userId}")
    public void restore(@PathVariable long userId) {
	logger.debug("restore | userId: " + userId);
	
	userService.restore(userId);
    }
    
    @GetMapping("/resetPassword/{userId}")
    public void resetPassword(@PathVariable long userId) {
	userService.resetPassword(userId);
    }
    
}
