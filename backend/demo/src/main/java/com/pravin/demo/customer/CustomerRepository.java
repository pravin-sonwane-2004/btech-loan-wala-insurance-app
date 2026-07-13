package com.pravin.demo.customer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Optional<Customer> findByEmailIgnoreCase(String email);

	@Query("""
			select c from Customer c
			where :term is null
			   or lower(c.firstName) like lower(concat('%', :term, '%'))
			   or lower(c.lastName) like lower(concat('%', :term, '%'))
			   or lower(c.email) like lower(concat('%', :term, '%'))
			   or lower(c.phoneNumber) like lower(concat('%', :term, '%'))
			order by c.id desc
			""")
	List<Customer> search(@Param("term") String term);
}
