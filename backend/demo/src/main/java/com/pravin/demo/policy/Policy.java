package com.pravin.demo.policy;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.pravin.demo.customer.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "insurance_policies", uniqueConstraints = {
		@UniqueConstraint(name = "uk_policy_number", columnNames = "policy_number")
})
public class Policy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "policy_number", nullable = false, length = 60)
	private String policyNumber;

	@Column(nullable = false, length = 120)
	private String policyName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private PolicyType policyType;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal premiumAmount;

	@Column(nullable = false)
	private Integer coverageTermMonths;

	@Column(nullable = false)
	private LocalDate effectiveStartDate;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_policy_customer"))
	private Customer customer;

	protected Policy() {
	}

	public Long getId() {
		return id;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public PolicyType getPolicyType() {
		return policyType;
	}

	public void setPolicyType(PolicyType policyType) {
		this.policyType = policyType;
	}

	public BigDecimal getPremiumAmount() {
		return premiumAmount;
	}

	public void setPremiumAmount(BigDecimal premiumAmount) {
		this.premiumAmount = premiumAmount;
	}

	public Integer getCoverageTermMonths() {
		return coverageTermMonths;
	}

	public void setCoverageTermMonths(Integer coverageTermMonths) {
		this.coverageTermMonths = coverageTermMonths;
	}

	public LocalDate getEffectiveStartDate() {
		return effectiveStartDate;
	}

	public void setEffectiveStartDate(LocalDate effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
