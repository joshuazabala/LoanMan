package com.codefaucet.LoanMan.controller;

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
import com.codefaucet.LoanMan.common.StringHelper.CharSet;
import com.codefaucet.LoanMan.common.Util;
import com.codefaucet.LoanMan.dto.LoanTypeDTO;
import com.codefaucet.LoanMan.model.LoanType;
import com.codefaucet.LoanMan.service.LoanTypeService;

@RestController
@RequestMapping("/api/loantype")
public class LoanTypeController {
    
    @Autowired
    private LoanTypeService loanTypeService;
    
    private final Logger logger = LoggerFactory.getLogger(LoanTypeController.class);
    
    @Autowired
    private LoggingHelper loggingHelper;
    
    @GetMapping("/findById/{loanTypeId}")
    public LoanTypeDTO findById(@PathVariable long loanTypeId) {
	logger.debug("findById | loanTypeId: " + loanTypeId);
	
	LoanType loanType = loanTypeId == 0 ? new LoanType() : loanTypeService.findById(loanTypeId);
	LoanTypeDTO dto = new LoanTypeDTO(loanType.getId(), loanType.isActive(), loanType.getPaymentFrequency(), loanType.getName(), loanType.getDescription());
	return dto;
    }
    
    @GetMapping("/delete/{loanTypeId}")
    public void delete(@PathVariable long loanTypeId) {
	logger.debug("delete | loanTypeId: " + loanTypeId);
	
	loanTypeService.delete(loanTypeId);
    }
    
    @GetMapping("/restore/{loanTypeId}")
    public void restore(@PathVariable long loanTypeId) {
	logger.debug("restore | loanTypeId: " + loanTypeId);
	
	loanTypeService.restore(loanTypeId);
    }
    
    @PostMapping("/create")
    public ResponseContainer<LoanTypeDTO> create(@RequestBody LoanTypeDTO dto) {
	logger.debug("create | dto: " + loggingHelper.asString(dto));
	
	ResponseContainer<LoanTypeDTO> response = new ResponseContainer<LoanTypeDTO>();
	
	// basic validation
	if (StringHelper.isNullOrEmpty(dto.getName())) {
	    response.getErrorMap().put("name", "Name can't be empty.");
	}
	if (!response.getErrorMap().isEmpty()) {
	    return response.failed();
	}
	dto.setName(dto.getName().trim());
	dto.setDescription(dto.getDescription() == null ? "" : dto.getDescription().trim());
	
	LoanType duplicate = loanTypeService.findByName(dto.getName());
	if (duplicate != null) {
	    response.getErrorMap().put("name", "Name is already in use.");
	    return response.failed();
	}
	
	LoanType loanType = new LoanType(dto.getPaymentFrequency(), dto.getName(), dto.getDescription());
	loanType = loanTypeService.create(loanType);
	
	dto = new LoanTypeDTO(loanType.getId(), loanType.isActive(), loanType.getPaymentFrequency(), loanType.getName(), loanType.getDescription());
	return response.successful(dto);
    }
    
    @PostMapping("/edit")
    public ResponseContainer<LoanTypeDTO> edit(@RequestBody LoanTypeDTO dto) {
	ResponseContainer<LoanTypeDTO> response = new ResponseContainer<LoanTypeDTO>();
	
	// basic validation
	if (StringHelper.isNullOrEmpty(dto.getName())) {
	    response.getErrorMap().put("name", "Name can't be empty.");
	}
	if (!response.getErrorMap().isEmpty()) {
	    return response.failed();
	}
	dto.setName(dto.getName().trim());
	dto.setDescription(dto.getDescription() == null ? "" : dto.getDescription().trim());
	
	LoanType duplicate = loanTypeService.findByName(dto.getName());
	if (duplicate != null && duplicate.getId() != dto.getId()) {
	    response.getErrorMap().put("name", "Name is already in use.");
	    return response.failed();
	}
	
	LoanType loanType = duplicate == null ? loanTypeService.findById(dto.getId()) : duplicate;
	loanType.setName(dto.getName());
	loanType.setDescription(dto.getDescription());
	loanType.setPaymentFrequency(dto.getPaymentFrequency());
	loanType = loanTypeService.create(loanType);
	
	dto = new LoanTypeDTO(loanType.getId(), loanType.isActive(), loanType.getPaymentFrequency(), loanType.getName(), loanType.getDescription());
	return response.successful(dto);
    }
    
    @PostMapping("/search")
    public PagedSearchResponse<LoanTypeDTO> search(@RequestBody PagedSearchRequest param) {
	logger.debug("search | param: " + loggingHelper.asString(param));

	PagedSearchResponse<LoanTypeDTO> response = new PagedSearchResponse<LoanTypeDTO>();
	List<LoanType> loanTypes = loanTypeService.search(param);
	Long totalCount = loanTypeService.countSearchResult(param);

	loanTypes.forEach(item -> {
	    LoanTypeDTO dto = new LoanTypeDTO(item.getId(), item.isActive(), item.getPaymentFrequency(), item.getName(), item.getDescription());
	    response.getContent().add(dto);
	});
	response.setTotalPageCount(Util.getTotalPage(totalCount, param.getPageSize()));
	response.setColumnSorting(param.getColumnSorting());

	return response;
    }
    
}
