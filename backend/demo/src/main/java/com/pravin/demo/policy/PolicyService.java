package com.pravin.demo.policy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import com.pravin.demo.common.ApiException;
import com.pravin.demo.customer.Customer;
import com.pravin.demo.customer.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class PolicyService {

	private final PolicyRepository policyRepository;
	private final CustomerRepository customerRepository;

	public PolicyService(PolicyRepository policyRepository, CustomerRepository customerRepository) {
		this.policyRepository = policyRepository;
		this.customerRepository = customerRepository;
	}

	@Transactional(readOnly = true)
	public List<PolicyResponse> list(String search, Long customerId) {
		return policyRepository.search(cleanSearch(search), customerId).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public PolicyResponse get(Long id) {
		return toResponse(findPolicy(id));
	}

	public PolicyResponse create(PolicyRequest request) {
		validate(request, null);
		Policy policy = new Policy();
		apply(request, policy);
		return toResponse(policyRepository.save(policy));
	}

	public PolicyResponse update(Long id, PolicyRequest request) {
		Policy policy = findPolicy(id);
		validate(request, id);
		apply(request, policy);
		return toResponse(policy);
	}

	public void delete(Long id) {
		Policy policy = findPolicy(id);
		policyRepository.delete(policy);
	}

	private Policy findPolicy(Long id) {
		return policyRepository.findById(id)
				.orElseThrow(() -> ApiException.notFound("Policy " + id + " was not found"));
	}

	private void apply(PolicyRequest request, Policy policy) {
		policy.setPolicyNumber(clean(request.policyNumber()).toUpperCase());
		policy.setPolicyName(clean(request.policyName()));
		policy.setPolicyType(request.policyType());
		policy.setPremiumAmount(request.premiumAmount());
		policy.setCoverageTermMonths(request.coverageTermMonths());
		policy.setEffectiveStartDate(request.effectiveStartDate());
		policy.setCustomer(findCustomer(request.customerId()));
	}

	private void validate(PolicyRequest request, Long existingId) {
		requireText(request.policyNumber(), "Policy number is required");
		requireText(request.policyName(), "Policy name is required");
		if (request.policyType() == null) {
			throw ApiException.badRequest("Policy type is required");
		}
		if (request.premiumAmount() == null || request.premiumAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw ApiException.badRequest("Premium amount must be greater than zero");
		}
		if (request.coverageTermMonths() == null || request.coverageTermMonths() <= 0) {
			throw ApiException.badRequest("Coverage term must be greater than zero months");
		}
		if (request.effectiveStartDate() == null) {
			throw ApiException.badRequest("Effective start date is required");
		}
		if (request.customerId() == null) {
			throw ApiException.badRequest("Associated customer ID is required");
		}

		findCustomer(request.customerId());

		policyRepository.findByPolicyNumberIgnoreCase(clean(request.policyNumber()))
				.filter(policy -> !Objects.equals(policy.getId(), existingId))
				.ifPresent(policy -> {
					throw ApiException.conflict("Policy number already exists");
				});
	}

	private Customer findCustomer(Long customerId) {
		return customerRepository.findById(customerId)
				.orElseThrow(() -> ApiException.notFound("Customer " + customerId + " was not found"));
	}

	private PolicyResponse toResponse(Policy policy) {
		Customer customer = policy.getCustomer();
		return new PolicyResponse(
				policy.getId(),
				policy.getPolicyNumber(),
				policy.getPolicyName(),
				policy.getPolicyType(),
				policy.getPremiumAmount(),
				policy.getCoverageTermMonths(),
				policy.getEffectiveStartDate(),
				customer.getId(),
				customer.getFirstName() + " " + customer.getLastName());
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
