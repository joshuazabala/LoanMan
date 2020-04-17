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

import com.codefaucet.LoanMan.common.EnumCutoffFrequency;
import com.codefaucet.LoanMan.common.EnumCutoffStatus;
import com.codefaucet.LoanMan.common.LoggingHelper;
import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.common.PagedSearchResponse;
import com.codefaucet.LoanMan.common.ResponseContainer;
import com.codefaucet.LoanMan.dto.CutoffDTO;
import com.codefaucet.LoanMan.model.Cutoff;
import com.codefaucet.LoanMan.service.CutoffService;

@RestController
@RequestMapping("/api/cutoff")
public class CutoffController {

    @Autowired
    private CutoffService cutoffService;

    private final Logger logger = LoggerFactory.getLogger(CutoffController.class);

    @Autowired
    private LoggingHelper loggingHelper;

    @GetMapping("/findById/{cutoffId}")
    public CutoffDTO findById(@PathVariable long cutoffId) {
	logger.debug("findById | cutoffId: " + cutoffId);

	Cutoff cutoff = cutoffId == 0 ? new Cutoff() : cutoffService.findById(cutoffId);
	CutoffDTO dto = new CutoffDTO(cutoff.getId(), cutoff.isActive(), cutoff.getStatus(), cutoff.getFrequency(),
		cutoff.getStartDate(), cutoff.getEndDate(), cutoff.getYear(), cutoff.getMonth(),
		cutoff.getCutoffNumber(), cutoff.getRemarks());
	return dto;
    }

    @GetMapping("/delete/{cutoffId}")
    public ResponseContainer<Void> delete(@PathVariable long cutoffId) {
	logger.debug("delete | cutoffId: " + cutoffId);

	ResponseContainer<Void> response = new ResponseContainer<Void>();
	Cutoff cutoff = cutoffService.findById(cutoffId);
	if (cutoff.getStatus() == EnumCutoffStatus.POSTED) {
	    return response.failed("Cutoff is posted.");
	}
	if (cutoff.isActive()) {
	    cutoffService.delete(cutoff);
	}

	return response.successful();
    }

    @GetMapping("/restore/{cutoffId}")
    public ResponseContainer<Void> restore(@PathVariable long cutoffId) {
	logger.debug("restore | cutoffId: " + cutoffId);

	ResponseContainer<Void> response = new ResponseContainer<Void>();
	Cutoff cutoff = cutoffService.findById(cutoffId);
	if (cutoff.isActive()) {
	    return response.successful();
	}
	Cutoff duplicateCutoff = cutoffService.findByCutoffNumber(cutoff.getFrequency(), cutoff.getYear(),
		cutoff.getMonth(), cutoff.getCutoffNumber());
	if (duplicateCutoff != null && duplicateCutoff.getId() != cutoff.getId()) {
	    return response.failed(
		    "An active cutoff already exists for " + cutoffService.createCutoffDisplay(duplicateCutoff) + ".");
	}
	cutoffService.restore(cutoff);

	return response.successful();
    }

    @PostMapping("/create")
    public ResponseContainer<CutoffDTO> create(@RequestBody CutoffDTO dto) {
	logger.debug("create | dto: " + loggingHelper.asString(dto));

	ResponseContainer<CutoffDTO> response = new ResponseContainer<CutoffDTO>();

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

	Cutoff duplicateCutoff = cutoffService.findByCutoffNumber(dto.getFrequency(), dto.getYear(), dto.getMonth(),
		dto.getCutoffNumber());
	if (duplicateCutoff != null) {
	    return response.failed(
		    "An active cutoff already exists for " + cutoffService.createCutoffDisplay(duplicateCutoff) + ".");
	}

	Cutoff cutoff = new Cutoff(dto.getFrequency(), dto.getStartDate(), dto.getEndDate(), dto.getYear(),
		dto.getMonth(), dto.getCutoffNumber(), dto.getRemarks());
	cutoff = cutoffService.create(cutoff);
	dto = new CutoffDTO(cutoff.getId(), cutoff.isActive(), cutoff.getStatus(), cutoff.getFrequency(),
		cutoff.getStartDate(), cutoff.getEndDate(), cutoff.getYear(), cutoff.getMonth(),
		cutoff.getCutoffNumber(), cutoff.getRemarks());

	return response.successful(dto);
    }

    @PostMapping("/edit")
    public ResponseContainer<CutoffDTO> edit(@RequestBody CutoffDTO dto) {
	logger.debug("edit | dto: " + loggingHelper.asString(dto));

	ResponseContainer<CutoffDTO> response = new ResponseContainer<CutoffDTO>();

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

	Cutoff duplicateCutoff = cutoffService.findByCutoffNumber(dto.getFrequency(), dto.getYear(), dto.getMonth(),
		dto.getCutoffNumber());
	if (duplicateCutoff != null && duplicateCutoff.getId() != dto.getId()) {
	    return response.failed(
		    "An active cutoff already exists for " + cutoffService.createCutoffDisplay(duplicateCutoff) + ".");
	}

	Cutoff cutoff = duplicateCutoff != null ? duplicateCutoff : cutoffService.findById(dto.getId());
	cutoff.setFrequency(dto.getFrequency());
	cutoff.setStartDate(dto.getStartDate());
	cutoff.setEndDate(dto.getEndDate());
	cutoff.setYear(dto.getYear());
	cutoff.setMonth(dto.getMonth());
	cutoff.setCutoffNumber(dto.getCutoffNumber());
	cutoff.setRemarks(dto.getRemarks());
	cutoff = cutoffService.edit(cutoff);
	dto = new CutoffDTO(cutoff.getId(), cutoff.isActive(), cutoff.getStatus(), cutoff.getFrequency(),
		cutoff.getStartDate(), cutoff.getEndDate(), cutoff.getYear(), cutoff.getMonth(),
		cutoff.getCutoffNumber(), cutoff.getRemarks());

	return response.successful(dto);
    }

    @GetMapping("/post/{cutoffId}")
    public ResponseContainer<Void> post(@PathVariable long cutoffId) {
	logger.debug("post| cutoffId: " + cutoffId);

	ResponseContainer<Void> response = new ResponseContainer<Void>();
	Cutoff cutoff = cutoffService.findById(cutoffId);
	if (cutoff.getStatus() == EnumCutoffStatus.POSTED) {
	    return response.successful();
	}
	cutoffService.post(cutoff);

	return response.successful();
    }

    @GetMapping("/unpost/{cutoffId}")
    public ResponseContainer<Void> unpost(@PathVariable long cutoffId) {
	logger.debug("unpost| cutoffId: " + cutoffId);

	ResponseContainer<Void> response = new ResponseContainer<Void>();
	Cutoff cutoff = cutoffService.findById(cutoffId);
	if (cutoff.getStatus() == EnumCutoffStatus.DRAFT) {
	    return response.successful();
	}
	cutoffService.unpost(cutoff);

	return response.successful();
    }

    @PostMapping("/search")
    public PagedSearchResponse<CutoffDTO> search(@RequestBody PagedSearchRequest param) {
	logger.debug("search | param: " + loggingHelper.asString(param));

	PagedSearchResponse<CutoffDTO> response = new PagedSearchResponse<CutoffDTO>();
	List<Cutoff> cutoffs = cutoffService.search(param);
	List<CutoffDTO> dtos = new ArrayList<CutoffDTO>();
	cutoffs.forEach(item -> {
	    CutoffDTO dto = new CutoffDTO(item.getId(), item.isActive(), item.getStatus(), item.getFrequency(),
		    item.getStartDate(), item.getEndDate(), item.getYear(), item.getMonth(), item.getCutoffNumber(),
		    item.getRemarks());
	    dtos.add(dto);
	});

	return response.successful(dtos, 0);
    }

}
