package com.codefaucet.LoanMan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.model.Loan;

@Repository
public interface ILoanRepository extends JpaRepository<Loan, Long> {

}
