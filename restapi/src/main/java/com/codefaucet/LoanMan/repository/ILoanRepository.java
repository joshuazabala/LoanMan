package com.codefaucet.LoanMan.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.common.EnumLoanStatus;
import com.codefaucet.LoanMan.model.Loan;

@Repository
public interface ILoanRepository extends JpaRepository<Loan, Long> {

    @Query(
	    "select l from Loan l "
	    + "where "
	    + "("
	    + "l.client.lastName like concat(:queryString, '%') "
	    + "or l.client.firstName like concat(:queryString, '%') "
	    + "or l.client.middleName like concat(:queryString, '%') "
	    + "or l.client.id like concat(:queryString, '%')"
	    + ") "
	    + "and (:loanTypeId = 0 or l.loanType.id = :loanTypeId) "
	    + "and l.status in :loanStatusFilter")
    List<Loan> search(String queryString, int loanTypeId, List<EnumLoanStatus> loanStatusFilter,
	    Pageable createPageable);

    @Query(
	    "select count(l) from Loan l "
	    + "where "
	    + "("
	    + "l.client.lastName like concat(:queryString, '%') "
	    + "or l.client.firstName like concat(:queryString, '%') "
	    + "or l.client.middleName like concat(:queryString, '%') "
	    + "or l.client.id like concat(:queryString, '%')"
	    + ") "
	    + "and (:loanTypeId = 0 or l.loanType.id = :loanTypeId) "
	    + "and l.status in :loanStatusFilter")
    long countSearchResult(String queryString, int loanTypeId, List<EnumLoanStatus> loanStatusFilter);

    @Query("select size(l.payments) from Loan l where l.id = :id")
    long getPaymentCount(long id);

}
