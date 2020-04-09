package com.codefaucet.LoanMan.controller;

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

import com.codefaucet.LoanMan.common.EnumCutoffFrequency;
import com.codefaucet.LoanMan.common.EnumCutoffStatus;
import com.codefaucet.LoanMan.common.LoggingHelper;
import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.common.PagedSearchResponse;
import com.codefaucet.LoanMan.common.RequestContainer;
import com.codefaucet.LoanMan.common.ResponseContainer;
import com.codefaucet.LoanMan.dto.CutoffDTO;
import com.codefaucet.LoanMan.model.Cutoff;
import com.codefaucet.LoanMan.service.CutoffService;

@RestController
@RequestMapping("/api/cutoff")
public class CutoffController {

    @Autowired
    private CutoffService cutoffService;

    private final Logger logger = LogManager.getLogger();

    @Autowired
    private LoggingHelper loggingHelper;

    @PostMapping("/search")
    public PagedSearchResponse<CutoffDTO> search(@RequestBody PagedSearchRequest param) {
	logger.debug("search | param: " + loggingHelper.asString(param));

	PagedSearchResponse<CutoffDTO> response = new PagedSearchResponse<CutoffDTO>();
	try {
	    int year = (int) param.getOtherData().get("year");
	    
	    List<EnumCutoffStatus> statusFilter = new ArrayList<EnumCutoffStatus>();
	    String statusesString = (String) param.getOtherData().get("statuses");
	    for (String statusString : statusesString.split(",")) {
		EnumCutoffStatus status = EnumCutoffStatus.valueOf(statusString);
		statusFilter.add(status);
	    }
	    
	    List<EnumCutoffFrequency> frequencyFilter = new ArrayList<EnumCutoffFrequency>();
	    EnumCutoffFrequency frequency = EnumCutoffFrequency.valueOf((String) param.getOtherData().get("frequency"));
	    if (frequency == EnumCutoffFrequency.UNKNOWN || frequency == EnumCutoffFrequency.MONTHLY) {
		frequencyFilter.add(EnumCutoffFrequency.MONTHLY);
	    }
	    if (frequency == EnumCutoffFrequency.UNKNOWN || frequency == EnumCutoffFrequency.SEMI_MONTHLY) {
		frequencyFilter.add(EnumCutoffFrequency.SEMI_MONTHLY);
	    }

	    List<Cutoff> cutoffs = cutoffService.search(year, statusFilter, frequencyFilter);
	    List<CutoffDTO> dtos = new ArrayList<CutoffDTO>();
	    cutoffs.forEach(item -> {
		CutoffDTO dto = new CutoffDTO(item.getId(), item.getStatus(), item.getFrequency(), item.getStartDate(), item.getEndDate(),
			item.getYear(), item.getMonth(), item.getCutoffNumber());
		dtos.add(dto);
	    });
	    
	    return response.successful(dtos, 0);
	} catch (Exception ex) {
	    logger.error("search | Error: " + ex.getMessage(), ex);

	    return response.failed(ex.getMessage());
	}
    }
    
    @PostMapping("/findById")
    public CutoffDTO findById(@RequestBody Map<String, Long> param) {
	logger.debug("findById | param: " + loggingHelper.asString(param));
	
	long id = param.get("id");
	Cutoff cutoff = id == 0 ? new Cutoff() : cutoffService.findById(id);
	CutoffDTO dto = new CutoffDTO(cutoff.getId(), cutoff.getStatus(), cutoff.getFrequency(), cutoff.getStartDate(), cutoff.getEndDate(),
		cutoff.getYear(), cutoff.getMonth(), cutoff.getCutoffNumber());
	return dto;
    }
    
    @PostMapping("/save")
    public ResponseContainer<CutoffDTO> save(@RequestBody RequestContainer<CutoffDTO> param) {
	logger.debug("save | param: " + param);
	
	ResponseContainer<CutoffDTO> response = new ResponseContainer<CutoffDTO>();
 	try {
	    CutoffDTO dto = param.getContent();
	    if (dto.getFrequency() == EnumCutoffFrequency.UNKNOWN) {
		response.getErrorMap().put("frequency", "This field is required.");
	    }
	    if (dto.getCutoffNumber() < 1 || dto.getCutoffNumber() > 2) {
		response.getErrorMap().put("cutoffNumber", "Invalid value.");
	    }
	    if (dto.getStartDate() == null) {
		response.getErrorMap().put("startDate", "This field is required.");
	    }
	    if (dto.getEndDate() == null) {
		response.getErrorMap().put("endDate", "This field is required.");
	    }
	    if (dto.getStartDate() != null && dto.getEndDate() != null) {
		if (dto.getEndDate().isBefore(dto.getStartDate()) || dto.getEndDate().isEqual(dto.getStartDate())) {
			response.getErrorMap().put("endDate", "End date can't be equal or before start date.");
		    }
	    }
	    if (dto.getMonth() < 1 || dto.getMonth() > 12) {
		response.getErrorMap().put("month", "Invalid value.");
	    }
	    if (dto.getYear() < 1) {
		response.getErrorMap().put("year", "Invalid value.");
	    }
	    if (!response.getErrorMap().isEmpty()) {
		return response.failed();
	    }
	    
	    Cutoff cutoff = cutoffService.findByCutoffNumber(dto.getFrequency(), dto.getYear(), dto.getMonth(), dto.getCutoffNumber());
	    if (cutoff != null && dto.getId() != cutoff.getId()) {
		return response.failed(
			"Cutoff for the specified frequency, year, "
			+ "month" + (dto.getFrequency() == EnumCutoffFrequency.SEMI_MONTHLY ? 
				", and cutoff number" : "") + " already exists.");
	    }
	    if (cutoff != null && cutoff.getStatus() == EnumCutoffStatus.POSTED) {
		return response.failed("Cutoff is already posted.");
	    }
	    if (cutoff == null) {
		cutoff = dto.getId() == 0 ? new Cutoff() : cutoffService.findById(dto.getId());
	    }
	    cutoff.setCutoffNumber(dto.getFrequency() == EnumCutoffFrequency.SEMI_MONTHLY ? dto.getCutoffNumber() : 1);
	    cutoff.setEndDate(dto.getEndDate());
	    cutoff.setFrequency(dto.getFrequency());
	    cutoff.setMonth(dto.getMonth());
	    cutoff.setStartDate(dto.getStartDate());
	    cutoff.setStatus(EnumCutoffStatus.DRAFT);
	    cutoff.setYear(dto.getYear());
	    cutoff = cutoffService.save(cutoff);
	    
	    dto.setId(cutoff.getId());
	    return response.successful(dto);
	} catch (Exception ex) {
	    logger.error("save | Error: " + ex.getMessage(), ex);
	    
	    return response.failed(ex.getMessage());
	}
    }
    
    @PostMapping("/setStatus")
    public ResponseContainer<Boolean> setStatus(@RequestBody Map<String, Object> param) {
	logger.debug("setStatus | param: " + loggingHelper.asString(param));
	
	ResponseContainer<Boolean> response = new ResponseContainer<Boolean>();
	try {
	    int id = (int) param.get("id");
	    Cutoff cutoff = cutoffService.findById(id);
	    
	    Cutoff lastPostedCutoff = cutoffService.getLastPostedCutoff(cutoff.getFrequency());
	    if (cutoff.getStartDate().isBefore(lastPostedCutoff.getStartDate())) {
		return response.failed("Unable to change cutoff status. There are posted ones after this.");
	    }
	    EnumCutoffStatus status = (EnumCutoffStatus) param.get("status");
	    cutoff.setStatus(status);
	    cutoff = cutoffService.save(cutoff);
	    
	    return response.successful(true);
	} catch (Exception ex) {
	    logger.error("setStatus | Error: " + ex.getMessage(), ex);
	    
	    return response.failed(ex.getMessage());
	}
    }

    @PostMapping("/delete")
    public ResponseContainer<Boolean> delete(@RequestBody Map<String, Long> param) {
	logger.debug("delete | param: " + loggingHelper.asString(param));
	
	ResponseContainer<Boolean> response = new ResponseContainer<Boolean>();
	try {
	    long id = param.get("id");
	    Cutoff cutoff = cutoffService.findById(id);
	    if (cutoff.getStatus() == EnumCutoffStatus.POSTED) {
		return response.failed("Cutoff is posted.");
	    }
	    
	    Cutoff lastPostedCutoff = cutoffService.getLastPostedCutoff(cutoff.getFrequency());
	    if (cutoff.getStartDate().isBefore(lastPostedCutoff.getStartDate())) {
		return response.failed("Unable to delete cutoff. There are posted ones after this.");
	    }
	    
	    cutoffService.delete(cutoff);
	    return response.successful(true);
	} catch (Exception ex) {
	    logger.error("delete | Error: " + ex.getMessage(), ex);
	    
	    return response.failed(ex.getMessage());
	}
    }
    
}
