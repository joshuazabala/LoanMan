package com.codefaucet.LoanMan.repository;

import java.awt.print.Pageable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.model.Client;

@Repository
public interface IClientRepository extends JpaRepository<Client, Long> {

    @Query(
	    "select c from Client c "
	    + "where "
	    + "("
	    + "c.clientNumber like concat(:needle, '%') "
	    + "or c.firstName like concat(:needle, '%') "
	    + "or c.middleName like concat(:needle, '%') "
	    + "or c.lastName like concat(:needle, '%')"
	    + ") "
	    + "and c.active in :statuses")
    public List<Client> search(@Param("needle") String needle, @Param("statuses") List<Boolean> statuses,
	    Pageable pageable);

    @Query("select count(distinct c.id) from Client c join c.groups g where c.active = true and g.id = :groupId")
    public Long countActiveByGroupId(@Param("groupId") Long groupId);

    public Client findByClientNumber(String clientNumber);

}
