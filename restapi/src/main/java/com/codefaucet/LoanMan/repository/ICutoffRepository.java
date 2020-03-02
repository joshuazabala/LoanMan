package com.codefaucet.LoanMan.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.common.EnumCutoffFrequency;
import com.codefaucet.LoanMan.common.EnumCutoffStatus;
import com.codefaucet.LoanMan.model.Cutoff;

@Repository
public interface ICutoffRepository extends JpaRepository<Cutoff, Long> {

    @Query("select c from Cutoff c where c.year = :year and c.status in :statusFilter and c.frequency in :frequencyFilter")
    List<Cutoff> search(int year, List<EnumCutoffStatus> statusFilter, List<EnumCutoffFrequency> frequencyFilter,
	    Pageable pageable);

    @Query("select c from Cutoff c where c.startDate = (select max(c2.startDate) from Cutoff c2 where c2.frequency = :frequency)")
    Cutoff getLastPostedCutoff(EnumCutoffFrequency frequency);

    @Query("select c from Cutoff c where c.frequency = :frequency and c.year = :year and month = :month and cutoffNumber = :cutoffNumber")
    Cutoff findByCutoffNumber(EnumCutoffFrequency frequency, int year, int month, int cutoffNumber);

}
