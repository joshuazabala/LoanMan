package com.codefaucet.LoanMan.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.model.LoanType;

@Repository
public interface ILoanTypeRepository extends JpaRepository<LoanType, Long> {

    @Query(
	    "select lt from LoanType lt "
	    + "where "
	    + "("
	    + "lt.code like concat(:needle, '%') "
	    + "or lt.description like concat(:needle, '%')"
	    + ") "
	    + "and lt.active in :statuses")
    public List<LoanType> search(@Param("needle") String needle, @Param("statuses") List<Boolean> statuses,
	    Pageable pageable);

    @Query(
	    "select count(lt) from LoanType lt "
	    + "where "
	    + "( "
	    + "lt.code like '%' || :queryString || '%' "
	    + "or lt.description like '%' || :queryString || '%' "
	    + ") "
	    + "and lt.active in :statuses")
    public Long countSearchResult(@Param("queryString") String queryString, @Param("statuses") List<Boolean> statuses);
    
    @Query(
	    "select lt from LoanType lt "
	    + "where "
	    + "("
	    + "lt.code like concat(:needle, '%') "
	    + "or lt.description like concat(:needle, '%')"
	    + ") "
	    + "and lt.id not in :excludedIds "
	    + "and lt.active in :statuses")
    public List<LoanType> search(@Param("needle") String needle, @Param("statuses") List<Boolean> statuses,
	    @Param("excludedIds") List<Long> excludedIds, Pageable pageable);

    public LoanType findByCode(String code);

    @Query("select size(lt.loans) from LoanType lt where lt.id = :id")
    public long getLoanCount(long id);

}
