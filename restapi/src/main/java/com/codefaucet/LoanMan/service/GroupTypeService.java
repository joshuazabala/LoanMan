package com.codefaucet.LoanMan.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.model.GroupType;
import com.codefaucet.LoanMan.repository.IGroupTypeRepository;

@Service
public class GroupTypeService {

    @Autowired
    private IGroupTypeRepository groupTypeRepository;
    
    public List<GroupType> search(PagedSearchRequest param) {
	Sort sort = param.createSorter();
	List<GroupType> groupTypes = groupTypeRepository.search(param.getQueryString(), param.createStatusFilter(), param.createPageable(sort));
	return groupTypes;
    }
    
    public Long countSearchResult(PagedSearchRequest param) {
	return groupTypeRepository.countSearchResult(param.getQueryString(), param.createStatusFilter());
    }

    public GroupType findById(long id) {
	return groupTypeRepository.findById(id).get();
    }

    public GroupType findByName(String name) {
	return groupTypeRepository.findByName(name);
    }

    public GroupType save(GroupType groupType) {
	return groupTypeRepository.save(groupType);
    }

    public void deleteById(long id) {
	GroupType groupType = groupTypeRepository.findById(id).get();
	groupTypeRepository.delete(groupType);
    }

    public List<GroupType> listGroupTypes() {
	Sort sort = Sort.by(Order.asc("name"));
	Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
	List<Boolean> statusFilter = new ArrayList<Boolean>();
	statusFilter.add(true);
	List<GroupType> groupTypes = groupTypeRepository.search("", statusFilter, pageable);
	return groupTypes;
    }

    public void delete(long groupTypeId) {
	GroupType groupType = groupTypeRepository.findById(groupTypeId).get();
	if (groupType.isActive()) {
	    groupType.setActive(false);
	    groupTypeRepository.save(groupType);
	}
    }

    public void restore(long groupTypeId) {
	GroupType groupType = groupTypeRepository.findById(groupTypeId).get();
	if (!groupType.isActive()) {
	    groupType.setActive(true);
	    groupTypeRepository.save(groupType);
	}
    }

    public GroupType create(GroupType groupType) {
	return groupTypeRepository.save(groupType);
    }

    public GroupType edit(GroupType groupType) {
	return groupTypeRepository.save(groupType);
    }
    
}
