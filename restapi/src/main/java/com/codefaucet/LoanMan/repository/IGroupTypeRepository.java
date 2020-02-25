package com.codefaucet.LoanMan.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.model.GroupType;

@Repository
public interface IGroupTypeRepository extends JpaRepository<GroupType, Long> {

    @Query(
	    "select gt from GroupType gt "
	    + "where "
	    + "("
	    + "gt.code like '%' || :queryString || '%' "
	    + "or gt.name like '%' || :queryString || '%'"
	    + ") "
	    + "and gt.active in :statuses")
    public List<GroupType> search(@Param("queryString") String queryString, @Param("statuses") List<Boolean> statuses,
	    Pageable pageable);

    @Query(
	    "select gt from GroupType gt "
	    + "where "
	    + "("
	    + "gt.code like '%' || :queryString || '%' "
	    + "or gt.name like '%' || :queryString || '%'"
	    + ") "
	    + "and gt.active in :statuses"
	    + "and gt.id not in :excludedIds")
    public List<GroupType> search(@Param("queryString") String queryString, @Param("statuses") List<Boolean> statuses,
	    @Param("excludedIds") List<Long> excludedIds, Pageable pageable);
    
    public GroupType findByCode(String code);

    @Query("select size(gt.groups) from GroupType gt where gt = :groupType")
    public Long countActiveGroups(@Param("groupType") GroupType groupType);

}
