package com.pravin.demo.policy;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PolicyResponse(
		Long id,
		String policyNumber,
		String policyName,
		PolicyType policyType,
		BigDecimal premiumAmount,
		Integer coverageTermMonths,
		LocalDate effectiveStartDate,
		Long customerId,
		String customerName) {
}
