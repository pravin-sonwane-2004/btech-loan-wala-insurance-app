package com.pravin.demo.customer;

import java.time.LocalDate;

public record CustomerResponse(
		Long id,
		String firstName,
		String lastName,
		String email,
		String phoneNumber,
		LocalDate dateOfBirth,
		AccountStatus accountStatus) {
}
