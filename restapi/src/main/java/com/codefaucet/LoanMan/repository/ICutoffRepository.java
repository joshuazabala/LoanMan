package com.codefaucet.LoanMan.repository;

import java.time.LocalDate;
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

    @Query("select c from Cutoff c where (:year = 0 or c.year = :year) and c.status in :cutoffStatusFilter and c.frequency in :frequencyFilter and c.active in :statusFilter")
    List<Cutoff> search(int year, List<EnumCutoffStatus> cutoffStatusFilter, List<EnumCutoffFrequency> frequencyFilter, List<Boolean> statusFilter, 
	    Pageable pageable);

    @Query("select c from Cutoff c where c.startDate = (select max(c2.startDate) from Cutoff c2 where c2.frequency = :frequency)")
    Cutoff getLastPostedCutoff(EnumCutoffFrequency frequency);

    @Query("select c from Cutoff c where c.active = true and c.frequency = :frequency and c.year = :year and month = :month and cutoffNumber = :cutoffNumber")
    Cutoff findByCutoffNumber(EnumCutoffFrequency frequency, int year, int month, int cutoffNumber);

    @Query("select c from Cutoff c where c.active = true and c.frequency = :frequency and c.startDate >= :date and c.endDate <= :date")
    Cutoff findByDate(EnumCutoffFrequency frequency, LocalDate date);

}
