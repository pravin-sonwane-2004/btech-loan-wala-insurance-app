package com.pravin.demo.policy;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

	private final PolicyService policyService;

	public PolicyController(PolicyService policyService) {
		this.policyService = policyService;
	}

	@GetMapping
	public List<PolicyResponse> list(@RequestParam(required = false) String search,
			@RequestParam(required = false) Long customerId) {
		return policyService.list(search, customerId);
	}

	@GetMapping("/{id}")
	public PolicyResponse get(@PathVariable Long id) {
		return policyService.get(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PolicyResponse create(@RequestBody PolicyRequest request) {
		return policyService.create(request);
	}

	@PutMapping("/{id}")
	public PolicyResponse update(@PathVariable Long id, @RequestBody PolicyRequest request) {
		return policyService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		policyService.delete(id);
	}
}
