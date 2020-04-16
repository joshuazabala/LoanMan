package com.codefaucet.LoanMan.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.codefaucet.LoanMan.common.EnumLoanStatus;
import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.model.Loan;
import com.codefaucet.LoanMan.repository.ILoanRepository;

@Service
public class LoanService {

    @Autowired
    private ILoanRepository loanRepository;
    
    public List<Loan> search(PagedSearchRequest param) {
	Sort sort = param.createSorter();
	List<EnumLoanStatus> loanStatusFilter = new ArrayList<EnumLoanStatus>();
	String loanStatusFilterString = (String) param.getOtherData().get("loanStatuses");
	for (String loanStatusString : loanStatusFilterString.split(",")) {
	    EnumLoanStatus loanStatus = EnumLoanStatus.valueOf(loanStatusString);
	    loanStatusFilter.add(loanStatus);
	}
	int loanTypeId = (int) param.getOtherData().get("loanTypeId");
	return loanRepository.search(param.getQueryString(), loanTypeId, loanStatusFilter, param.createPageable(sort));
    }

    public long countSearchResult(PagedSearchRequest param) {
	List<EnumLoanStatus> loanStatusFilter = new ArrayList<EnumLoanStatus>();
	String loanStatusFilterString = (String) param.getOtherData().get("loanStatuses");
	for (String loanStatusString : loanStatusFilterString.split(",")) {
	    EnumLoanStatus loanStatus = EnumLoanStatus.valueOf(loanStatusString);
	    loanStatusFilter.add(loanStatus);
	}
	
	int loanTypeId = (int) param.getOtherData().get("loanTypeId");
	
	return loanRepository.countSearchResult(param.getQueryString(), loanTypeId, loanStatusFilter);
    }

    public Loan findById(long id) {
	return loanRepository.findById(id).get();
    }

    public Loan save(Loan loan) {
	return loanRepository.save(loan);
    }

    public long getPaymentCount(long id) {
	return loanRepository.getPaymentCount(id);
    }

    public void delete(long id) {
	Loan loan = loanRepository.findById(id).get();
	loanRepository.delete(loan);
    }

    public void restore(long loanId) {
	// TODO Auto-generated method stub
	
    }
    
}
