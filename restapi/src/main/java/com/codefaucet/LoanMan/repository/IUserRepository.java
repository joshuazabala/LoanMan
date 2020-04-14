package com.codefaucet.LoanMan.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.model.User;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    
    @Query(
	    "select u "
	    + "from User u "
	    + "where ("
	    + "u.lastName like concat('%', :queryString, '%') "
	    + "or u.firstName like concat('%', :queryString, '%') "
	    + "or u.middleName like concat('%', :queryString, '%') "
	    + "or u.username like concat('%', :queryString, '%') "
	    + "or u.contactNumber like concat('%', :queryString, '%') "
	    + "or u.emailAddress like concat('%', :queryString, '%')"
	    + ") "
	    + "and u.active in :statusFilter "
	    + "and (:profileId = 0 or u.profile.id = :profileId)")
    List<User> search(String queryString, int profileId, List<Boolean> statusFilter, Pageable pageable);

    @Query(
	    "select count(u) "
	    + "from User u "
	    + "where ("
	    + "u.lastName like concat('%', :queryString, '%') "
	    + "or u.firstName like concat('%', :queryString, '%') "
	    + "or u.middleName like concat('%', :queryString, '%') "
	    + "or u.username like concat('%', :queryString, '%') "
	    + "or u.contactNumber like concat('%', :queryString, '%') "
	    + "or u.emailAddress like concat('%', :queryString, '%')"
	    + ") "
	    + "and u.active in :statusFilter "
	    + "and (:profileId = 0 or u.profile.id = :profileId)")
    long countSearchResult(String queryString, int profileId, List<Boolean> statusFilter);

    User findByUsername(String username);

}
