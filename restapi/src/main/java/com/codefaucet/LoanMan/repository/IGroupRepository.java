package com.codefaucet.LoanMan.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codefaucet.LoanMan.model.Group;

@Repository
public interface IGroupRepository extends JpaRepository<Group, Long> {

    @Query(
	    "select g from Group g "
	    + "where "
	    + "("
	    + "g.name like '%' || :queryString || '%' "
	    + "or g.description like '%' || :queryString || '%'"
	    + ") "
	    + "and g.active in :statuses "
	    + "and (:groupTypeId = 0 or g.type.id = :groupTypeId)")
    public List<Group> search(@Param("queryString") String queryString, @Param("statuses") List<Boolean> statuses,
	    @Param("groupTypeId") int groupTypeId, Pageable pageable);

    @Query(
	    "select count(1) from Group g "
	    + "where "
	    + "("
	    + "g.name like '%' || :queryString || '%' "
	    + "or g.description like '%' || :queryString || '%'"
	    + ") "
	    + "and g.active in :statuses "
	    + "and (:groupTypeId = 0 or g.type.id = :groupTypeId)")
    public long countSearchResult(String queryString, List<Boolean> statuses, int groupTypeId);
    
    @Query(
	    "select g from Group g "
	    + "where "
	    + "("
	    + "g.name like '%' || :queryString || '%' "
	    + "or g.description like '%' || :queryString || '%'"
	    + ") "
	    + "and g.active in :statuses "
	    + "and g.type.id = :groupTypeId "
	    + "and g.id not in :excludedIds")
    public List<Group> search(@Param("queryString") String queryString, @Param("statuses") List<Boolean> statuses,
	    @Param("groupTypeId") Long groupTypeId, @Param("excludedIds") List<Long> excludedIds, Pageable pageable);
    
    public Group findByName(String name);

    @Query("select size(g.clients) from Group g where g.id = :id")
    public long getMemberCount(long id);

}
