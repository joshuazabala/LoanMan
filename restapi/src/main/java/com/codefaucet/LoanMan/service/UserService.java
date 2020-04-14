package com.codefaucet.LoanMan.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.model.User;
import com.codefaucet.LoanMan.repository.IUserRepository;

@Service
public class UserService {

    @Value("${app.user.defaultPassword}")
    private String defaultPassword;
    
    @Autowired
    private IUserRepository userRepository;

    public User findById(long userId) {
	Optional<User> target = userRepository.findById(userId);
	if (target.isPresent()) {
	    return target.get();
	}
	return null;
    }

    public List<User> search(PagedSearchRequest param) {
	int profileId = (int) param.getOtherData().get("profileId");
	Sort sort = param.createSorter();
	List<User> users = userRepository.search(param.getQueryString(), profileId, param.createStatusFilter(), param.createPageable(sort));
	return users;
    }

    public Long countSearchResult(PagedSearchRequest param) {
	int profileId = (int) param.getOtherData().get("profileId");
	long count = userRepository.countSearchResult(param.getQueryString(), profileId, param.createStatusFilter());
	return count;
    }

    public void delete(long userId) {
	User user = userRepository.findById(userId).get();
	if (user.isActive()) {
	    user.setActive(false);
	    userRepository.save(user);
	}
    }
    
    public void restore(long userId) {
	User user = userRepository.findById(userId).get();
	if (!user.isActive()) {
	    user.setActive(true);
	    userRepository.save(user);
	}
    }

    public User findByUsername(String username) {
	return userRepository.findByUsername(username);
    }

    public User create(User user) {
	user.setPassword(defaultPassword);
	return userRepository.save(user);
    }

    public User edit(User user) {
	return userRepository.save(user);
    }

    public void resetPassword(long userId) {
	User user = userRepository.findById(userId).get();
	user.setPassword(defaultPassword);
	userRepository.save(user);
    }
    
}
