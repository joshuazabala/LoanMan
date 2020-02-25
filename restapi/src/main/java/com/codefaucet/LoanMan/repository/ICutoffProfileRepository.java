package com.codefaucet.LoanMan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.model.CutoffProfile;

@Repository
public interface ICutoffProfileRepository extends JpaRepository<CutoffProfile, Long> {

}
