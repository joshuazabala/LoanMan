package com.codefaucet.LoanMan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.model.LoanType;
import com.codefaucet.LoanMan.repository.ILoanTypeRepository;

@Service
public class LoanTypeService {

    @Autowired
    private ILoanTypeRepository loanTypeRepository;
    
    public LoanType save(LoanType loanType) {
	return loanTypeRepository.save(loanType);
    }

    public List<LoanType> search(PagedSearchRequest param) {
	Sort sort = param.createSorter();
	List<LoanType> loanTypes = loanTypeRepository.search(param.getQueryString(), param.createStatusFilter(), param.createPageable(sort));
	return loanTypes;
    }

    public Long countSearchResult(PagedSearchRequest param) {
	return loanTypeRepository.countSearchResult(param.getQueryString(), param.createStatusFilter());
    }

    public LoanType findById(long id) {
	return loanTypeRepository.findById(id).get();
    }

    public LoanType findByName(String name) {
	return loanTypeRepository.findByName(name);
    }

    public void delete(long loanTypeId) {
	LoanType loanType = loanTypeRepository.findById(loanTypeId).get();
	if (loanType.isActive()) {
	    loanType.setActive(false);
	    loanTypeRepository.save(loanType);
	}
    }
    
    public void restore(long loanTypeId) {
	LoanType loanType = loanTypeRepository.findById(loanTypeId).get();
	if (!loanType.isActive()) {
	    loanType.setActive(true);
	    loanTypeRepository.save(loanType);
	}
    }

    public LoanType create(LoanType loanType) {
	return loanTypeRepository.save(loanType);
    }

    public LoanType edit(LoanType loanType) {
	return loanTypeRepository.save(loanType);
    }
    
}
