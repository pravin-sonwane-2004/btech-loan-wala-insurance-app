package com.pravin.demo.policy;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

	Optional<Policy> findByPolicyNumberIgnoreCase(String policyNumber);

	@Query("""
			select p from Policy p
			join fetch p.customer c
			where (:customerId is null or c.id = :customerId)
			  and (:term is null
			   or lower(p.policyNumber) like lower(concat('%', :term, '%'))
			   or lower(p.policyName) like lower(concat('%', :term, '%'))
			   or lower(c.firstName) like lower(concat('%', :term, '%'))
			   or lower(c.lastName) like lower(concat('%', :term, '%')))
			order by p.id desc
			""")
	List<Policy> search(@Param("term") String term, @Param("customerId") Long customerId);
}
