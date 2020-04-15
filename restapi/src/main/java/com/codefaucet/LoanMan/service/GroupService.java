package com.codefaucet.LoanMan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.model.Group;
import com.codefaucet.LoanMan.repository.IGroupRepository;

@Service
public class GroupService {

    @Autowired
    private IGroupRepository groupRepository;

    public long countSearchResult(PagedSearchRequest param) {
	int groupTypeId = (int) param.getOtherData().get("groupTypeId");
	return groupRepository.countSearchResult(param.getQueryString(), param.createStatusFilter(), groupTypeId);
    }

    public List<Group> search(PagedSearchRequest param) {
	Sort sort = param.createSorter();
	int groupTypeId = (int) param.getOtherData().get("groupTypeId");
	return groupRepository.search(param.getQueryString(), param.createStatusFilter(), groupTypeId, param.createPageable(sort));
    }

    public Group findById(long id) {
	return groupRepository.findById(id).get();
    }

    public Group findByName(String name) {
	return groupRepository.findByName(name);
    }

    public long getMemberCount(long id) {
	return groupRepository.getMemberCount(id);
    }

    public void delete(long groupId) {
	Group group = groupRepository.findById(groupId).get();
	if (group.isActive()) {
	    group.setActive(false);
	    groupRepository.save(group);
	}
    }
    
    public void restore(long groupId) {
	Group group = groupRepository.findById(groupId).get();
	if (!group.isActive()) {
	    group.setActive(true);
	    groupRepository.save(group);
	}
    }

    public Group create(Group group) {
	return groupRepository.save(group);
    }
    
    public Group edit(Group group) {
	return groupRepository.save(group);
    }
    
}
