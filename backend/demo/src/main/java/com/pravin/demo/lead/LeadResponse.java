package com.pravin.demo.lead;

public record LeadResponse(
		Long id,
		String prospectName,
		String contactInfo,
		String referralSource,
		LeadStatus leadStatus,
		String assignedAgentName) {
}
