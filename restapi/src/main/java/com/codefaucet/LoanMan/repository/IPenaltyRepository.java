package com.codefaucet.LoanMan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.model.Cutoff;
import com.codefaucet.LoanMan.model.Penalty;

@Repository
public interface IPenaltyRepository extends JpaRepository<Penalty, Long> {

    @Query("select sum(p.amount) from Penalty p where p.loan.id = :loanId")
    double getTotalPenalty(long loanId);

    @Query("select p from Payment p where p.cutoff in :cutoffs")
    List<Penalty> getPenaltiesFromCutoffs(List<Cutoff> cutoffs);

}
