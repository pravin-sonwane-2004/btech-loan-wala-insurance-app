package com.pravin.demo.customer;

import java.time.LocalDate;

public record CustomerRequest(
		String firstName,
		String lastName,
		String email,
		String phoneNumber,
		LocalDate dateOfBirth,
		AccountStatus accountStatus) {
}
