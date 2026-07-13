package com.pravin.demo.lead;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeadRepository extends JpaRepository<SalesLead, Long> {

	@Query("""
			select l from SalesLead l
			where (:status is null or l.leadStatus = :status)
			  and (:term is null
			   or lower(l.prospectName) like lower(concat('%', :term, '%'))
			   or lower(l.contactInfo) like lower(concat('%', :term, '%'))
			   or lower(l.referralSource) like lower(concat('%', :term, '%'))
			   or lower(l.assignedAgentName) like lower(concat('%', :term, '%')))
			order by l.id desc
			""")
	List<SalesLead> search(@Param("term") String term, @Param("status") LeadStatus status);
}
