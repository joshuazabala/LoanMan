package com.codefaucet.LoanMan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.model.Payment;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, Long> {

}
