package com.codefaucet.LoanMan.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.codefaucet.LoanMan.common.EnumCutoffFrequency;
import com.codefaucet.LoanMan.common.EnumCutoffStatus;
import com.codefaucet.LoanMan.model.Cutoff;
import com.codefaucet.LoanMan.repository.ICutoffRepository;

@Service
public class CutoffService {

    @Autowired
    private ICutoffRepository cutoffRepository;

    @Value("${cutoff.monthly.start}")
    private int monthlyCutoffStart;
   
    @Value("${cutoff.semimonthly.start.first}")
    private int semiMonthlyFirstCutoffStart;
    
    @Value("${cutoff.semimonthly.start.second}")
    private int semiMonthlySecondCutoffStart;
    
    public List<Cutoff> search(int year, List<EnumCutoffStatus> statusFilter,
	    List<EnumCutoffFrequency> frequencyFilter) {
	Sort sort = Sort.by(Order.desc("startDate"));
	Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
	List<Cutoff> cutoffs = cutoffRepository.search(year, statusFilter, frequencyFilter, pageable);
	return cutoffs;
    }

    public Cutoff findById(long id) {
	return cutoffRepository.findById(id).get();
    }

    public Cutoff getLastPostedCutoff(EnumCutoffFrequency frequency) {
	return cutoffRepository.getLastPostedCutoff(frequency);
    }

    public Cutoff save(Cutoff cutoff) {
	return cutoffRepository.save(cutoff);
    }

    public void delete(Cutoff cutoff) {
	cutoffRepository.delete(cutoff);
    }

    public Cutoff findByCutoffNumber(EnumCutoffFrequency frequency, int year, int month, int cutoffNumber) {
	Cutoff cutoff = cutoffRepository.findByCutoffNumber(frequency, year, month, cutoffNumber);
	return cutoff;
    }

    public Cutoff findCutoffByDate(LocalDate date, EnumCutoffFrequency cutoffFrequency) {
	Cutoff cutoff = null;
	if (cutoffFrequency == EnumCutoffFrequency.MONTHLY) {
	    cutoff = findMonthlyCutoffByDate(date);
	} else { // semi-monthly
	    cutoff = findSemiMonthlyCutoffByDate(date);
	}
	return cutoff;
    }
    
    private Cutoff findMonthlyCutoffByDate(LocalDate date) {
	Cutoff cutoff = new Cutoff();
	cutoff.setFrequency(EnumCutoffFrequency.MONTHLY);
	
	LocalDate startDate = syncDateToCutoffStart(date, monthlyCutoffStart);
	cutoff.setStartDate(startDate);
	cutoff.setEndDate(startDate.plusMonths(1).minusDays(1));
	cutoff.setCutoffNumber(1);
	cutoff.setMonth(startDate.getMonthValue());
	cutoff.setYear(startDate.getYear());
	cutoff.setStatus(EnumCutoffStatus.DRAFT);
	
	return cutoff;
    }
    
    private LocalDate syncDateToCutoffStart(LocalDate date, int cutoffStart) {
	int startDay = date.lengthOfMonth() < cutoffStart ? date.lengthOfMonth() : cutoffStart;
	if (startDay > date.getDayOfMonth()) {
	    date = date.minusMonths(1);
	    startDay = date.lengthOfMonth() < cutoffStart ? date.lengthOfMonth() : cutoffStart;
	}
	date = date.withDayOfMonth(startDay);
	return date;
    }
    
    private Cutoff findSemiMonthlyCutoffByDate(LocalDate date) {
	Cutoff cutoff = new Cutoff();
	cutoff.setFrequency(EnumCutoffFrequency.SEMI_MONTHLY);
	
	LocalDate firstCutoffStartDate = syncDateToCutoffStart(date, semiMonthlyFirstCutoffStart);
	LocalDate secondCutoffStartDate = syncDateToCutoffStart(date, semiMonthlySecondCutoffStart);
	
	boolean useFirst = true;
	if (date.isAfter(secondCutoffStartDate) || date.isEqual(secondCutoffStartDate)) {
	    if (firstCutoffStartDate.isBefore(secondCutoffStartDate)) {
		useFirst = false;
	    }
	}
	cutoff.setCutoffNumber(useFirst ? 1 : 2);
	cutoff.setStartDate(useFirst ? firstCutoffStartDate : secondCutoffStartDate);
	cutoff.setEndDate((useFirst ? secondCutoffStartDate : firstCutoffStartDate).plusMonths(1).minusDays(1));
	cutoff.setYear(firstCutoffStartDate.getYear());
	cutoff.setMonth(firstCutoffStartDate.getMonthValue());
	
	return cutoff;
    }
    
}
