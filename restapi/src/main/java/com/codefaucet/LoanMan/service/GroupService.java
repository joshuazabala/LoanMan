package com.codefaucet.LoanMan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
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
	Sort sort = Sort.by(Order.asc("code"));
	int groupTypeId = (int) param.getOtherData().get("groupTypeId");
	return groupRepository.search(param.getQueryString(), param.createStatusFilter(), groupTypeId, param.createPageable(sort));
    }

    public Group findById(long id) {
	return groupRepository.findById(id).get();
    }

    public Group findByCode(String code) {
	return groupRepository.findByCode(code);
    }

    public Group save(Group group) {
	return groupRepository.save(group);
    }

    public void deleteById(long id) {
	Group model = groupRepository.findById(id).get();
	groupRepository.delete(model);
    }

    public long getMemberCount(long id) {
	return groupRepository.getMemberCount(id);
    }
    
}
