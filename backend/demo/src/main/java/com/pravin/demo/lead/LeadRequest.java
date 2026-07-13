package com.pravin.demo.lead;

public record LeadRequest(
		String prospectName,
		String contactInfo,
		String referralSource,
		LeadStatus leadStatus,
		String assignedAgentName) {
}
