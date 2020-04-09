package com.codefaucet.LoanMan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.model.Cutoff;
import com.codefaucet.LoanMan.model.Payment;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select sum(p.amount) from Payment p where p.loan.id = :loanId")
    double getTotalPaid(long loanId);

    @Query("select p from Payment p where p.cutoff in :cutoffs")
    List<Payment> getPaymentsFromCutoffs(List<Cutoff> cutoffs);

}
