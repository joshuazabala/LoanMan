package com.codefaucet.LoanMan.controller;

import com.codefaucet.LoanMan.common.LoggingHelper;
import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.common.PagedSearchResponse;
import com.codefaucet.LoanMan.common.RequestContainer;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loan")
public class LoanController {

    @Autowired
    private LoanService loanService;

    private final Logger logger = LogManager.getLogger();

    @Autowired
    private LoggingHelper loggingHelper;

    @Autowired
    private ClientService clientService;
    
    @Autowired
    private LoanTypeService loanTypeService;
    
    @PostMapping("/search")
    public PagedSearchResponse<LoanDTO> search(@RequestBody PagedSearchRequest param) {
	logger.debug("search | param: " + loggingHelper.asString(param));

	PagedSearchResponse<LoanDTO> response = new PagedSearchResponse<LoanDTO>();
	try {
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
		    dto.setLoanType(item.getLoanType().getCode() + " - " + item.getLoanType().getDescription());
		    dto.setLoanTypeId(item.getLoanType().getId());
		}
		dtos.add(dto);
	    });

	    return response.successful(dtos, Util.getTotalPage(totalCount, param.getPageSize()));
	} catch (Exception ex) {
	    logger.error("search | Error: " + ex.getMessage(), ex);

	    return response.failed(ex.getMessage());
	}
    }

    @PostMapping("/findById")
    public LoanDTO findById(@RequestBody Map<String, Long> param) {
	logger.debug("findById | param: " + loggingHelper.asString(param));

	long id = param.get("id");
	Loan loan = id == 0 ? new Loan() : loanService.findById(id);

	LoanDTO dto = new LoanDTO(loan.getId(), loan.getStatus(), loan.getPrincipal(), loan.getPayable(),
		loan.getAmortization(), loan.getLoanDate(), loan.getPaymentStartDate(), loan.getRemarks());
	if (loan.getClient() != null) {
	    dto.setClient(loan.getClient().getName());
	    dto.setClientId(loan.getClient().getId());
	}
	if (loan.getLoanType() != null) {
	    dto.setLoanType(loan.getLoanType().getCode() + " - " + loan.getLoanType().getDescription());
	    dto.setLoanTypeId(loan.getLoanType().getId());
	}
	return dto;
    }
    
    @PostMapping("/save")
    public ResponseContainer<LoanDTO> save(@RequestBody RequestContainer<LoanDTO> param) {
	logger.debug("findById | param: " + loggingHelper.asString(param));
	
	ResponseContainer<LoanDTO> response = new ResponseContainer<LoanDTO>();
	try {
	    LoanDTO dto = param.getContent();
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
	    
	    Loan loan = dto.getId() == 0 ? new Loan() : loanService.findById(dto.getId());
	    loan.setPrincipal(dto.getPrincipal());
	    loan.setPayable(dto.getPayable());
	    loan.setAmortization(dto.getAmortization());
	    loan.setRemarks(dto.getRemarks());
	    
	    if (loan.getClient() == null || loan.getClient().getId() != dto.getClientId()) {
		Client client = clientService.findById(dto.getClientId());
		loan.setClient(client);
	    }
	    
	    if (loan.getLoanType() == null || loan.getLoanType().getId() != dto.getLoanTypeId()) {
		LoanType loanType = loanTypeService.findById(dto.getLoanTypeId());
		loan.setLoanType(loanType);
	    }
	    
	    loan = loanService.save(loan);
	    dto.setId(loan.getId());
	    dto.setClient(loan.getClient().getName());
	    dto.setLoanType(loan.getLoanType().getCode() + " - " + loan.getLoanType().getDescription());
	    
	    return response.successful(dto);
	} catch (Exception ex) {
	    logger.error("save | Error: " + ex.getMessage(), ex);
	    
	    return response.failed(ex.getMessage());
	}
    }

    @PostMapping("/delete")
    public ResponseContainer<Boolean> delete(@RequestBody RequestContainer<Long> param) {
	logger.debug("findById | param: " + loggingHelper.asString(param));
	
	ResponseContainer<Boolean> response = new ResponseContainer<Boolean>();
	long id = param.getContent();
	try {
	    long paymentCount = loanService.getPaymentCount(id);
	    if (paymentCount > 0) {
		return response.failed("This loan already has payments.");
	    }
	    loanService.delete(id);

	    return response.successful(true);
	} catch (Exception ex) {
	    logger.error("delete | Error: " + ex.getMessage(), ex);
	    
	    return response.failed(ex.getMessage());
	}
    }
    
}
