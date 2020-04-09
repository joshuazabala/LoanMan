package com.codefaucet.LoanMan.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.codefaucet.LoanMan.common.EnumCutoffFrequency;
import com.codefaucet.LoanMan.common.EnumCutoffStatus;
import com.codefaucet.LoanMan.dto.PaymentRequestDTO;
import com.codefaucet.LoanMan.model.Cutoff;
import com.codefaucet.LoanMan.model.Loan;
import com.codefaucet.LoanMan.repository.ICutoffRepository;
import com.codefaucet.LoanMan.repository.IPaymentRepository;
import com.codefaucet.LoanMan.repository.IPenaltyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PaymentService {

    @Value("${cutoff.monthly.start}")
    private int monthlyCutoffStart;
    
    @Value("${cutoff.semimonthly.start.first}")
    private int semiMonthlyCutoffFirstStart;
    
    @Value("${cutoff.semimonthly.start.second}")
    private int semiMonthlyCutoffSecondStart;
    
    @Autowired
    private IPaymentRepository paymentRepository;
    
    @Autowired
    private IPenaltyRepository penaltyRepository;
    
    @Autowired
    private ICutoffRepository cutoffRepository;

    /**
     * 
     * @param forCutoff
     * @param loan
     * @return
     */
    public PaymentRequestDTO computeDueAmount(Cutoff forCutoff, Loan loan) {
	PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
//	BigDecimal amortization = new BigDecimal(loan.getAmortization());
//	
//	List<Cutoff> cutoffs = cutoffRepository.listCutoffByRange(forCutoff.getFrequency(), loan.getPaymentStartCutoff(), forCutoff);
//	List<Payment> payments = paymentRepository.getPaymentsFromCutoffs(cutoffs);
//	List<Penalty> penalties = penaltyRepository.getPenaltiesFromCutoffs(cutoffs);
//	for (Cutoff cutoff : cutoffs.stream().sorted(Comparator.comparing(Cutoff::getStartDate)).collect(Collectors.toList())) {
//	    Optional<Payment> payment = payments.stream().filter(item -> item.getCutoff() == cutoff).findFirst();
//	    BigDecimal paid = new BigDecimal(payment.isPresent() ? payment.get().getAmount() : 0);
//	    if (paid.compareTo(amortization) >= 0) {
//		
//	    }
//	}
//	
//	BigDecimal totalPenalty = new BigDecimal(penalties.stream().mapToDouble(item -> item.getAmount()).sum());
//	BigDecimal totalPayable = new BigDecimal(loan.getPayable()).add(totalPenalty);
//	BigDecimal totalPaid = new BigDecimal(payments.stream().mapToDouble(item -> item.getAmount()).sum());
//	if (totalPayable.compareTo(totalPaid) <= 0) { // if payment is complete
//	    return new BigDecimal(0);
//	}
	
	return paymentRequest;
    }

    public Cutoff createCutoffFromDate(EnumCutoffFrequency frequency, LocalDate date) {
	Cutoff cutoff = cutoffRepository.findByDate(frequency, date);
	if (cutoff != null) {
	    return cutoff;
	}

	LocalDate startDate = date;
	if (frequency == EnumCutoffFrequency.MONTHLY) {
	    if (startDate.getDayOfMonth() > monthlyCutoffStart) {
		startDate = startDate.withDayOfMonth(monthlyCutoffStart);
	    } else if (startDate.getDayOfMonth() < monthlyCutoffStart) {
		
	    }
	}
	
	return null;
    }
    
    public static void main(String[] args) throws JsonProcessingException {	
	EnumCutoffFrequency frequency = EnumCutoffFrequency.MONTHLY;
	LocalDate date = LocalDate.of(2020, 2, 31);
	
	int monthlyCutoffStart = 16;
	int semiMonthlyCutoffFirstStart = 1;
	int semiMonthlyCutoffSecondStart = 16;
	
	LocalDate startDate = date;
	LocalDate endDate = date;
	if (frequency == EnumCutoffFrequency.MONTHLY) {
	    if (startDate.getDayOfMonth() > monthlyCutoffStart) {
		startDate = startDate.withDayOfMonth(monthlyCutoffStart);
	    } else if (startDate.getDayOfMonth() < monthlyCutoffStart) {
		startDate = startDate.plusMonths(-1);
		int cutoffStart = startDate.lengthOfMonth() < monthlyCutoffStart ? startDate.lengthOfMonth() : monthlyCutoffStart;
		startDate = startDate.withDayOfMonth(cutoffStart);
	    }
	    endDate = startDate.plusMonths(1).minusDays(1);
	} else { // semi-monthly
	    if (semiMonthlyCutoffSecondStart < startDate.getDayOfMonth() ||  semiMonthlyCutoffFirstStart < startDate.getDayOfMonth()) {
		
	    } else {
//		int start = semiMonthlyCutoffFirstStart > semiMonthlyCutoffSecondStart ? semiMonthlyCutoffSecondStart : semiMonthlyCutoffFirstStart;
//		startDate = startDate.plusMonths(-1);
//		start = startDate.lengthOfMonth() < start ? startDate.lengthOfMonth() : start;
//		startDate = startDate.withDayOfMonth(start);
//		int end = start > semi
	    }
	    int start = semiMonthlyCutoffSecondStart;
	    if (start > semiMonthlyCutoffFirstStart) {
		start = semiMonthlyCutoffFirstStart;
	    }
	}
	
	System.out.println("startDate: " + startDate);
	System.out.println("endDate: " + endDate);
    }
    
}
