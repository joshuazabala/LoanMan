package com.codefaucet.LoanMan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.model.Cutoff;

@Repository
public interface ICutoffRepository extends JpaRepository<Cutoff, Long> {

}
