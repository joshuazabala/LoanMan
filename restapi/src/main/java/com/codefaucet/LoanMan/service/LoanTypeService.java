package com.codefaucet.LoanMan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
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
	Sort sort = Sort.by(Order.asc("code"));
	List<LoanType> loanTypes = loanTypeRepository.search(param.getQueryString(), param.createStatusFilter(), param.createPageable(sort));
	return loanTypes;
    }

    public Long countSearchResult(PagedSearchRequest param) {
	return loanTypeRepository.countSearchResult(param.getQueryString(), param.createStatusFilter());
    }

    public LoanType findById(long id) {
	return loanTypeRepository.findById(id).get();
    }

    public LoanType findByCode(String code) {
	return loanTypeRepository.findByCode(code);
    }

    public void deleteById(long id) {
	LoanType loanType = loanTypeRepository.findById(id).get();
	loanTypeRepository.delete(loanType);
    }

    public long getLoanCount(long id) {
	return loanTypeRepository.getLoanCount(id);
    }
    
}
