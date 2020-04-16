package com.codefaucet.LoanMan.controller;

import java.util.ArrayList;
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
import com.codefaucet.LoanMan.dto.LoanDTO;
import com.codefaucet.LoanMan.model.Client;
import com.codefaucet.LoanMan.model.Loan;
import com.codefaucet.LoanMan.model.LoanType;
import com.codefaucet.LoanMan.service.ClientService;
import com.codefaucet.LoanMan.service.LoanService;
import com.codefaucet.LoanMan.service.LoanTypeService;

@RestController
@RequestMapping("/api/loan")
public class LoanController {

    private final Logger logger = LoggerFactory.getLogger(LoanController.class);
    
    @Autowired
    private LoanService loanService;

    @Autowired
    private LoggingHelper loggingHelper;

    @Autowired
    private ClientService clientService;
    
    @Autowired
    private LoanTypeService loanTypeService;
    
    @GetMapping("/findById/{loanId}")
    public LoanDTO findById(@PathVariable long loanId) {
	logger.debug("findById | loanId: " + loanId);

	Loan loan = loanId == 0 ? new Loan() : loanService.findById(loanId);

	LoanDTO dto = new LoanDTO(loan.getId(), loan.getStatus(), loan.getPrincipal(), loan.getPayable(),
		loan.getAmortization(), loan.getLoanDate(), loan.getPaymentStartDate(), loan.getRemarks());
	if (loan.getClient() != null) {
	    dto.setClient(loan.getClient().getName());
	    dto.setClientId(loan.getClient().getId());
	}
	if (loan.getLoanType() != null) {
	    dto.setLoanType(loan.getLoanType().getName() + " - " + loan.getLoanType().getDescription());
	    dto.setLoanTypeId(loan.getLoanType().getId());
	}
	return dto;
    }
    
    @GetMapping("/delete/{loanId}")
    public ResponseContainer<Void> delete(@PathVariable long loanId) {
	logger.debug("delete | loanId: " + loanId);
	
	ResponseContainer<Void> response = new ResponseContainer<Void>();
	loanService.delete(loanId);
	
	return response.successful();
    }
    
    @GetMapping("/restore/{loanId}")
    public ResponseContainer<Void> restore(@PathVariable long loanId) {
	logger.debug("restore | loanId: " + loanId);
	
	ResponseContainer<Void> response = new ResponseContainer<Void>();
	loanService.restore(loanId);
	
	return response.successful();
    }
    
    @PostMapping("/create")
    public ResponseContainer<LoanDTO> create(@RequestBody LoanDTO dto) {
	logger.debug("create | dto: " + loggingHelper.asString(dto));

	ResponseContainer<LoanDTO> response = new ResponseContainer<LoanDTO>();
	if (dto.getPrincipal() <= 0) {
	    response.getErrorMap().put("principal", "Invalid amount.");
	}
	if (dto.getPayable() <= 0) {
	    response.getErrorMap().put("payable", "Invalid amount.");
	}
	if (dto.getAmortization() <= 0) {
	    response.getErrorMap().put("amortization", "Invalid amount.");
	}
	if (StringHelper.isNullOrEmpty(dto.getClientId())) {
	    response.getErrorMap().put("clientId", "This field is required.");
	}
	if (dto.getLoanTypeId() == 0) {
	    response.getErrorMap().put("loanTypeId", "This field is required.");
	}
	if (dto.getLoanDate() == null) {
	    response.getErrorMap().put("loanDate", "This field is required.");
	}
	if (dto.getPaymentStartDate() == null) {
	    response.getErrorMap().put("paymentStartDate", "This field is required.");
	}

	dto.setRemarks(StringHelper.isNullOrEmpty(dto.getRemarks()) ? "" : dto.getRemarks().trim());

	Loan loan = new Loan(dto.getPrincipal(), dto.getPayable(), dto.getAmortization(), dto.getLoanDate(), dto.getPaymentStartDate(), dto.getRemarks());
	if (loan.getClient() == null || loan.getClient().getId() != dto.getClientId()) {
	    Client client = clientService.findById(dto.getClientId());
	    loan.setClient(client);
	}
	if (loan.getLoanType() == null || loan.getLoanType().getId() != dto.getLoanTypeId()) {
	    LoanType loanType = loanTypeService.findById(dto.getLoanTypeId());
	    loan.setLoanType(loanType);
	}
	loan = loanService.save(loan);
	
	dto = new LoanDTO(loan.getId(), loan.getStatus(), loan.getPrincipal(), loan.getPayable(), loan.getAmortization(), loan.getLoanDate(), loan.getPaymentStartDate(), loan.getRemarks());
	dto.setClient(loan.getClient().getName());
	dto.setClientId(loan.getClient().getId());
	dto.setLoanType(loan.getLoanType().getName());
	dto.setLoanTypeId(loan.getLoanType().getId());

	return response.successful(dto);
    }
    
    @PostMapping("/edit")
    public ResponseContainer<LoanDTO> edit(@RequestBody LoanDTO dto) {
	logger.debug("edit | dto: " + loggingHelper.asString(dto));
	
	ResponseContainer<LoanDTO> response = new ResponseContainer<LoanDTO>();
	if (dto.getPrincipal() <= 0) {
	    response.getErrorMap().put("principal", "Invalid amount.");
	}
	if (dto.getPayable() <= 0) {
	    response.getErrorMap().put("payable", "Invalid amount.");
	}
	if (dto.getAmortization() <= 0) {
	    response.getErrorMap().put("amortization", "Invalid amount.");
	}
	if (StringHelper.isNullOrEmpty(dto.getClientId())) {
	    response.getErrorMap().put("clientId", "This field is required.");
	}
	if (dto.getLoanTypeId() == 0) {
	    response.getErrorMap().put("loanTypeId", "This field is required.");
	}
	if (dto.getLoanDate() == null) {
	    response.getErrorMap().put("loanDate", "This field is required.");
	}
	if (dto.getPaymentStartDate() == null) {
	    response.getErrorMap().put("paymentStartDate", "This field is required.");
	}

	dto.setRemarks(StringHelper.isNullOrEmpty(dto.getRemarks()) ? "" : dto.getRemarks().trim());

	Loan loan = loanService.findById(dto.getId());
	loan.setPrincipal(dto.getPrincipal());
	loan.setPayable(dto.getPayable());
	loan.setAmortization(dto.getAmortization());
	loan.setLoanDate(dto.getLoanDate());
	loan.setPaymentStartDate(dto.getPaymentStartDate());
	loan.setRemarks(dto.getRemarks());
	if (loan.getClient().getId() != dto.getClientId()) {
	    Client client = clientService.findById(dto.getClientId());
	    loan.setClient(client);
	}
	if (loan.getLoanType().getId() != dto.getLoanTypeId()) {
	    LoanType loanType = loanTypeService.findById(dto.getLoanTypeId());
	    loan.setLoanType(loanType);
	}
	loan = loanService.save(loan);
	
	dto = new LoanDTO(loan.getId(), loan.getStatus(), loan.getPrincipal(), loan.getPayable(), loan.getAmortization(), loan.getLoanDate(), loan.getPaymentStartDate(), loan.getRemarks());
	dto.setClient(loan.getClient().getName());
	dto.setClientId(loan.getClient().getId());
	dto.setLoanType(loan.getLoanType().getName());
	dto.setLoanTypeId(loan.getLoanType().getId());

	return response.successful(dto);
    }
    
    @PostMapping("/search")
    public PagedSearchResponse<LoanDTO> search(@RequestBody PagedSearchRequest param) {
	logger.debug("search | param: " + loggingHelper.asString(param));

	PagedSearchResponse<LoanDTO> response = new PagedSearchResponse<LoanDTO>();
	List<Loan> loans = loanService.search(param);
	long totalCount = loanService.countSearchResult(param);

	List<LoanDTO> dtos = new ArrayList<LoanDTO>();
	loans.forEach(item -> {
	    LoanDTO dto = new LoanDTO(item.getId(), item.getStatus(), item.getPrincipal(), item.getPayable(),
		    item.getAmortization(), item.getLoanDate(), item.getPaymentStartDate(), item.getRemarks());
	    if (item.getClient() != null) {
		dto.setClient(item.getClient().getName());
		dto.setClientId(item.getClient().getId());
	    }
	    if (item.getLoanType() != null) {
		dto.setLoanType(item.getLoanType().getName());
		dto.setLoanTypeId(item.getLoanType().getId());
	    }
	    dtos.add(dto);
	});
	response.setColumnSorting(param.getColumnSorting());

	return response.successful(dtos, Util.getTotalPage(totalCount, param.getPageSize()));
    }
    
}
