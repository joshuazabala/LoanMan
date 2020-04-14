package com.codefaucet.LoanMan.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
import com.codefaucet.LoanMan.common.EnumLoanStatus;
import com.codefaucet.LoanMan.common.LoggingHelper;
import com.codefaucet.LoanMan.common.ResponseContainer;
import com.codefaucet.LoanMan.dto.PaymentDTO;
import com.codefaucet.LoanMan.model.Cutoff;
import com.codefaucet.LoanMan.model.Loan;
import com.codefaucet.LoanMan.service.CutoffService;
import com.codefaucet.LoanMan.service.LoanService;
import com.codefaucet.LoanMan.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private Logger logger = LogManager.getLogger();
    
    @Autowired
    private LoggingHelper loggingHelper;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private LoanService loanService;
    
    @Autowired
    private CutoffService cutoffService;
    
    private final DateFormat cutoffFormatter = new SimpleDateFormat("MMMM yyyy");
    
    @PostMapping("/requestPayment")
    public ResponseContainer<PaymentDTO> requestPayment(@RequestBody Map<String, Long> param) {
	logger.debug("requestPayment | param: " + loggingHelper.asString(param));

	ResponseContainer<PaymentDTO> response = new ResponseContainer<PaymentDTO>();
	try {
	    // check loan
	    long loanId = param.get("loanId");
	    Loan loan = loanService.findById(loanId);
	    if (loan.getStatus() != EnumLoanStatus.ACTIVE) {
		return response.failed("Loan is not active.");
	    }
	    
	    // check cutoff
	    long cutoffId = param.get("cutoffId");
	    Cutoff cutoff = cutoffService.findById(cutoffId);
	    if (cutoff.getStatus() == EnumCutoffStatus.POSTED) {
		return response.failed("This cutoff is already posted.");
	    }
	    if (cutoff.getStartDate().isBefore(loan.getPaymentStartDate())) {
		return response.failed("Requested cutoff is before the start of payment of selected loan.");
	    }
	    
//	    BigDecimal dueAmount = paymentService.computeDueAmount(cutoff, loan);
	    BigDecimal dueAmount = new BigDecimal(0);
	    
	    if (dueAmount.compareTo(new BigDecimal(0)) <= 0) {
		return response.failed("Payment for this loan is complete.");
	    }
	    PaymentDTO dto = new PaymentDTO();
	    dto.setAmount(dueAmount.doubleValue());
	    dto.setDate(LocalDate.now());
	    String cutoffString = cutoffFormatter.format(LocalDate.of(cutoff.getYear(), cutoff.getMonth(), 1));
	    if (cutoff.getFrequency() == EnumCutoffFrequency.SEMI_MONTHLY) {
		cutoffString += ", " + (cutoff.getCutoffNumber() == 1 ? "1st" : "2nd");
	    }
	    dto.setCutoff(cutoffString);
	    dto.setCutoffId(cutoff.getId());
	    dto.setLoan(loan.toString());
	    dto.setLoanId(loan.getId());
	    
	    return response.successful(new PaymentDTO());
	} catch (Exception ex) {
	    logger.error("requestPayment | Error: " + ex.getMessage(), ex);
	    
	    return response.failed(ex.getMessage());
	}
    }
    
}
