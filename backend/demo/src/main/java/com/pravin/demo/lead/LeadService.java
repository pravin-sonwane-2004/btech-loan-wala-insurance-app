package com.pravin.demo.lead;

import java.util.List;

import com.pravin.demo.common.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class LeadService {

	private final LeadRepository leadRepository;

	public LeadService(LeadRepository leadRepository) {
		this.leadRepository = leadRepository;
	}

	@Transactional(readOnly = true)
	public List<LeadResponse> list(String search, LeadStatus status) {
		return leadRepository.search(cleanSearch(search), status).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public LeadResponse get(Long id) {
		return toResponse(findLead(id));
	}

	public LeadResponse create(LeadRequest request) {
		validate(request);
		SalesLead lead = new SalesLead();
		apply(request, lead);
		return toResponse(leadRepository.save(lead));
	}

	public LeadResponse update(Long id, LeadRequest request) {
		SalesLead lead = findLead(id);
		validate(request);
		apply(request, lead);
		return toResponse(lead);
	}

	public void delete(Long id) {
		SalesLead lead = findLead(id);
		leadRepository.delete(lead);
	}

	private SalesLead findLead(Long id) {
		return leadRepository.findById(id)
				.orElseThrow(() -> ApiException.notFound("Lead " + id + " was not found"));
	}

	private void apply(LeadRequest request, SalesLead lead) {
		lead.setProspectName(clean(request.prospectName()));
		lead.setContactInfo(clean(request.contactInfo()));
		lead.setReferralSource(clean(request.referralSource()));
		lead.setLeadStatus(request.leadStatus());
		lead.setAssignedAgentName(clean(request.assignedAgentName()));
	}

	private void validate(LeadRequest request) {
		requireText(request.prospectName(), "Prospect name is required");
		requireText(request.contactInfo(), "Contact info is required");
		requireText(request.referralSource(), "Referral source is required");
		if (request.leadStatus() == null) {
			throw ApiException.badRequest("Lead status is required");
		}
		requireText(request.assignedAgentName(), "Assigned agent name is required");
	}

	private LeadResponse toResponse(SalesLead lead) {
		return new LeadResponse(
				lead.getId(),
				lead.getProspectName(),
				lead.getContactInfo(),
				lead.getReferralSource(),
				lead.getLeadStatus(),
				lead.getAssignedAgentName());
	}

	private String clean(String value) {
		return value == null ? "" : value.trim();
	}

	private String cleanSearch(String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
	}

	private void requireText(String value, String message) {
		if (!StringUtils.hasText(value)) {
			throw ApiException.badRequest(message);
		}
	}
}
