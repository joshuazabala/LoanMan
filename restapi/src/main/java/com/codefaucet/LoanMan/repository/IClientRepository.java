package com.codefaucet.LoanMan.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.model.Client;

@Repository
public interface IClientRepository extends JpaRepository<Client, String> {

    @Query(
	    "select c from Client c "
	    + "where "
	    + "("
	    + "c.id like concat(:queryString, '%') "
	    + "or c.firstName like concat(:queryString, '%') "
	    + "or c.middleName like concat(:queryString, '%') "
	    + "or c.lastName like concat(:queryString, '%')"
	    + ") "
	    + "and c.active in :statuses")
    public List<Client> search(@Param("queryString") String queryString, @Param("statuses") List<Boolean> statuses,
	    Pageable pageable);

    @Query("select count(distinct c.id) from Client c join c.groups g where c.active = true and g.id = :groupId")
    public Long countActiveByGroupId(@Param("groupId") Long groupId);

    @Query(
	    "select count(c) from Client c "
	    + "where "
	    + "("
	    + "c.id like concat(:queryString, '%') "
	    + "or c.firstName like concat(:queryString, '%') "
	    + "or c.middleName like concat(:queryString, '%') "
	    + "or c.lastName like concat(:queryString, '%')"
	    + ") "
	    + "and c.active in :statuses")
    public long countSearchResult(String queryString, List<Boolean> statuses);

    @Query("select size(c.loans) from Client c where c.id = :id")
    public long getLoanCount(String id);

}
