package com.codefaucet.LoanMan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	return cutoffRepository.findByCutoffNumber(frequency, year, month, cutoffNumber);
    }

}
