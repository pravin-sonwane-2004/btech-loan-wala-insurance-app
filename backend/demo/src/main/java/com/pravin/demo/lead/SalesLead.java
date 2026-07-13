package com.pravin.demo.lead;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "leads")
public class SalesLead {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String prospectName;

	@Column(nullable = false, length = 180)
	private String contactInfo;

	@Column(nullable = false, length = 120)
	private String referralSource;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private LeadStatus leadStatus;

	@Column(nullable = false, length = 120)
	private String assignedAgentName;

	protected SalesLead() {
	}

	public Long getId() {
		return id;
	}

	public String getProspectName() {
		return prospectName;
	}

	public void setProspectName(String prospectName) {
		this.prospectName = prospectName;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String getReferralSource() {
		return referralSource;
	}

	public void setReferralSource(String referralSource) {
		this.referralSource = referralSource;
	}

	public LeadStatus getLeadStatus() {
		return leadStatus;
	}

	public void setLeadStatus(LeadStatus leadStatus) {
		this.leadStatus = leadStatus;
	}

	public String getAssignedAgentName() {
		return assignedAgentName;
	}

	public void setAssignedAgentName(String assignedAgentName) {
		this.assignedAgentName = assignedAgentName;
	}
}
