package com.codefaucet.LoanMan.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.model.UserProfile;

@Repository
public interface IUserProfileRepository extends JpaRepository<UserProfile, Long> {

    @Query(
	    "select count(up) "
	    + "from UserProfile up "
	    + "where ("
	    + "up.name like concat('%', :queryString, '%') "
	    + "or up.description like concat('%', :queryString, '%')"
	    + ") "
	    + "and up.active in :statusFilter")
    Long countSearchResult(String queryString, List<Boolean> statusFilter);

    @Query(
	    "select up "
	    + "from UserProfile up "
	    + "where ("
	    + "up.name like concat('%', :queryString, '%') "
	    + "or up.description like concat('%', :queryString, '%')"
	    + ") "
	    + "and up.active in :statusFilter")
    List<UserProfile> search(String queryString, List<Boolean> statusFilter, Pageable createPageable);

    UserProfile findByName(String name);

}
