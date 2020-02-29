package com.codefaucet.LoanMan.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codefaucet.LoanMan.common.EnumResponseStatus;
import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.common.PagedSearchResponse;
import com.codefaucet.LoanMan.common.RequestContainer;
import com.codefaucet.LoanMan.common.ResponseContainer;
import com.codefaucet.LoanMan.common.StringHelper;
import com.codefaucet.LoanMan.common.StringHelper.CharSet;
import com.codefaucet.LoanMan.common.Util;
import com.codefaucet.LoanMan.controller.dto.LoanTypeDTO;
import com.codefaucet.LoanMan.model.LoanType;
import com.codefaucet.LoanMan.service.LoanTypeService;

@RestController
@RequestMapping("/api/loantype")
public class LoanTypeController {
    
    @Autowired
    private LoanTypeService loanTypeService;
    
    private final Logger logger = LogManager.getLogger();
    
    @GetMapping("/testInsertData")
    public void testInsertData() {
	for (int a = 0; a < 129; a++) {
	    LoanType loanType = new LoanType();
	    loanType.setCode("CODE" + a);
	    loanType.setDescription(loanType.getCode() + " description");
	    loanType = loanTypeService.save(loanType);
	}
    }
    
    @PostMapping("/search")
    public PagedSearchResponse<LoanTypeDTO> search(@RequestBody PagedSearchRequest param) {
	logger.debug("search | param: " + param.toString());
	
	PagedSearchResponse<LoanTypeDTO> response = new PagedSearchResponse<LoanTypeDTO>();
	
	try {
	    List<LoanType> loanTypes = loanTypeService.search(param);
	    Long totalCount = loanTypeService.countSearchResult(param);
	    
	    loanTypes.forEach(item -> {
		LoanTypeDTO dto = new LoanTypeDTO(item.getId(), item.isActive(), item.getCode(), item.getDescription());
		response.getContent().add(dto);
	    });
	    response.setTotalPageCount(Util.getTotalPage(totalCount, param.getPageSize()));
	} catch (Exception ex) {
	    response.setStatus(EnumResponseStatus.FAILED);
	    response.setMessage(ex.getMessage());
	    logger.error("search | Error: " + ex.getMessage(), ex);
	}
	
	return response;
    }
    
    @PostMapping("/findById")
    public LoanTypeDTO findById(@RequestBody Map<String, Long> param) {
	logger.debug("findById | param: " + param);
	
	long id = param.get("id");
	LoanType loanType = id == 0 ? new LoanType() : loanTypeService.findById(id);
	LoanTypeDTO dto = new LoanTypeDTO(loanType.getId(), loanType.isActive(), loanType.getCode(), loanType.getDescription());
	return dto;
    }
    
    @PostMapping("/save")
    public ResponseContainer<LoanTypeDTO> save(@RequestBody RequestContainer<LoanTypeDTO> param) {
	logger.debug("save | param: " + param);
	
	ResponseContainer<LoanTypeDTO> response = new ResponseContainer<LoanTypeDTO>();
	try {
	    LoanTypeDTO dto = param.getContent();
	    
	    // basic validation
	    if (StringHelper.isNullOrEmpty(dto.getCode())) {
		response.getErrorMap().put("code", "Code can't be empty.");
	    }
	    if (!StringHelper.isInCharSet(CharSet.ALPHANUMERIC, dto.getCode().trim())) {
		String codeError = response.getErrorMap().getOrDefault("code", "");
		codeError += (codeError.isEmpty() ? "" : "<br />") + "Only alphanumeric characters are allowed.";
		response.getErrorMap().put("code", codeError);
	    }
	    if (StringHelper.isNullOrEmpty(dto.getDescription())) {
		response.getErrorMap().put("description", "Description can't be empty.");
	    }
	    if (!response.getErrorMap().isEmpty()) {
		return response.failed();
	    }
	    
	    dto.setCode(dto.getCode().trim().toUpperCase());
	    dto.setDescription(dto.getDescription().trim());
	    
	    // check if code is used by other loan type
	    LoanType loanType = loanTypeService.findByCode(param.getContent().getCode());
	    if (loanType != null && loanType.getId() != dto.getId()) {
		response.getErrorMap().put("code", "Code '" + dto.getCode() + "' is not available.");
		return response.failed();
	    }
	    
	    // if loan type was found by above query and code validation has no problem, then we already have the correct loan type
	    if (loanType == null) {
		loanType = param.getContent().getId() == 0 ? new LoanType() : loanTypeService.findById(param.getContent().getId());
	    }
	    
	    // map values
	    loanType.setCode(dto.getCode());
	    loanType.setDescription(dto.getDescription());
	    loanType = loanTypeService.save(loanType);
	    
	    dto.setId(loanType.getId());
	    return response.successful(dto);
	} catch (Exception ex) {
	    logger.error("save | Error: " + ex.getMessage(), ex);
	    
	    return response.failed(ex.getMessage());
	}
    }
    
    @PostMapping("delete")
    public ResponseContainer<Boolean> delete(@RequestBody RequestContainer<Long> param) {
	logger.debug("delete | param: " + param);
	
	ResponseContainer<Boolean> response = new ResponseContainer<Boolean>();
	long id = param.getContent();
	try {
	    long loanCount = loanTypeService.getLoanCount(id);
	    if (loanCount > 1) {
		return response.failed("There are existing loans with this type.");
	    }
	    
	    loanTypeService.deleteById(id);
	    return response.successful(true);
	} catch (Exception ex) {
	    logger.error("delete | Error: " + ex.getMessage(), ex);
	    
	    return response.failed(ex.getMessage());
	}
    }
    
}
