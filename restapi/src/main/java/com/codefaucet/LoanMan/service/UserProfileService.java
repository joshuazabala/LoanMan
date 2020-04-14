package com.codefaucet.LoanMan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.model.UserProfile;
import com.codefaucet.LoanMan.repository.IUserProfileRepository;

@Service
public class UserProfileService {

    @Autowired
    private IUserProfileRepository userProfileRepository;

    public void restore(long userProfileId) {
	UserProfile userProfile = userProfileRepository.findById(userProfileId).get();
	if (!userProfile.isActive()) {
	    userProfile.setActive(true);
	    userProfileRepository.save(userProfile);
	}
    }

    public void delete(long userProfileId) {
	UserProfile userProfile = userProfileRepository.findById(userProfileId).get();
	if (userProfile.isActive()) {
	    userProfile.setActive(false);
	    userProfileRepository.save(userProfile);
	}
    }

    public UserProfile findById(long userProfileId) {
	UserProfile userProfile = userProfileRepository.findById(userProfileId).get();
	return userProfile;
    }

    public UserProfile create(UserProfile userProfile) {
	userProfile = userProfileRepository.save(userProfile);
	return userProfile;
    }

    public List<UserProfile> search(PagedSearchRequest param) {
	Sort sort = param.createSorter();
	List<UserProfile> userProfiles = userProfileRepository.search(param.getQueryString(), param.createStatusFilter(), param.createPageable(sort));
	return userProfiles;
    }

    public Long countSearchResult(PagedSearchRequest param) {
	Long count = userProfileRepository.countSearchResult(param.getQueryString(), param.createStatusFilter());
	return count;
    }

    public UserProfile findByName(String name) {
	return userProfileRepository.findByName(name);
    }
    
}
