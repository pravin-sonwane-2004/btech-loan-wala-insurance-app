package com.pravin.demo.lead;

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
@RequestMapping("/api/leads")
public class LeadController {

	private final LeadService leadService;

	public LeadController(LeadService leadService) {
		this.leadService = leadService;
	}

	@GetMapping
	public List<LeadResponse> list(@RequestParam(required = false) String search,
			@RequestParam(required = false) LeadStatus status) {
		return leadService.list(search, status);
	}

	@GetMapping("/{id}")
	public LeadResponse get(@PathVariable Long id) {
		return leadService.get(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public LeadResponse create(@RequestBody LeadRequest request) {
		return leadService.create(request);
	}

	@PutMapping("/{id}")
	public LeadResponse update(@PathVariable Long id, @RequestBody LeadRequest request) {
		return leadService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		leadService.delete(id);
	}
}
