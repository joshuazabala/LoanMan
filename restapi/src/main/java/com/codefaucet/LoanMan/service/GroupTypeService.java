package com.codefaucet.LoanMan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	Sort sort = Sort.by(Order.asc("code"));
	List<GroupType> groupTypes = groupTypeRepository.search(param.getQueryString(), param.createStatusFilter(), param.createPageable(sort));
	return groupTypes;
    }
    
    public Long countSearchResult(PagedSearchRequest param) {
	return groupTypeRepository.countSearchResult(param.getQueryString(), param.createStatusFilter());
    }

    public GroupType findGroupTypeById(long id) {
	return groupTypeRepository.findById(id).get();
    }

    public GroupType findGroupTypeByCode(String code) {
	return groupTypeRepository.findByCode(code);
    }

    public GroupType save(GroupType groupType) {
	return groupTypeRepository.save(groupType);
    }

    public void deleteGroupTypeById(long id) {
	GroupType groupType = groupTypeRepository.findById(id).get();
	groupTypeRepository.delete(groupType);
    }
    
}
