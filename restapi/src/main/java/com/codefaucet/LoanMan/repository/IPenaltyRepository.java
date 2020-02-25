package com.codefaucet.LoanMan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.model.Penalty;

@Repository
public interface IPenaltyRepository extends JpaRepository<Penalty, Long> {

}
