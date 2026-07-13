package com.pravin.demo.customer;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import com.pravin.demo.common.BadRequestException;
import com.pravin.demo.common.ConflictException;
import com.pravin.demo.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class CustomerService {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

	private final CustomerRepository customerRepository;

	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Transactional(readOnly = true)
	public List<CustomerResponse> list(String search) {
		return customerRepository.search(cleanSearch(search)).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public CustomerResponse get(Long id) {
		return toResponse(findCustomer(id));
	}

	public CustomerResponse create(CustomerRequest request) {
		validate(request, null);
		Customer customer = new Customer();
		apply(request, customer);
		return toResponse(customerRepository.save(customer));
	}

	public CustomerResponse update(Long id, CustomerRequest request) {
		Customer customer = findCustomer(id);
		validate(request, id);
		apply(request, customer);
		return toResponse(customer);
	}

	public void delete(Long id) {
		Customer customer = findCustomer(id);
		customerRepository.delete(customer);
	}

	private Customer findCustomer(Long id) {
		return customerRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Customer " + id + " was not found"));
	}

	private void apply(CustomerRequest request, Customer customer) {
		customer.setFirstName(clean(request.firstName()));
		customer.setLastName(clean(request.lastName()));
		customer.setEmail(clean(request.email()).toLowerCase());
		customer.setPhoneNumber(clean(request.phoneNumber()));
		customer.setDateOfBirth(request.dateOfBirth());
		customer.setAccountStatus(request.accountStatus());
	}

	private void validate(CustomerRequest request, Long existingId) {
		requireText(request.firstName(), "First name is required");
		requireText(request.lastName(), "Last name is required");
		requireText(request.email(), "Email is required");
		requireText(request.phoneNumber(), "Phone number is required");
		if (!EMAIL_PATTERN.matcher(clean(request.email())).matches()) {
			throw new BadRequestException("Email format is invalid");
		}
		if (request.dateOfBirth() == null) {
			throw new BadRequestException("Date of birth is required");
		}
		if (request.dateOfBirth().isAfter(LocalDate.now())) {
			throw new BadRequestException("Date of birth cannot be in the future");
		}
		if (request.accountStatus() == null) {
			throw new BadRequestException("Account status is required");
		}

		customerRepository.findByEmailIgnoreCase(clean(request.email()))
				.filter(customer -> !Objects.equals(customer.getId(), existingId))
				.ifPresent(customer -> {
					throw new ConflictException("Email already belongs to another customer");
				});
	}

	private CustomerResponse toResponse(Customer customer) {
		return new CustomerResponse(
				customer.getId(),
				customer.getFirstName(),
				customer.getLastName(),
				customer.getEmail(),
				customer.getPhoneNumber(),
				customer.getDateOfBirth(),
				customer.getAccountStatus());
	}

	private String clean(String value) {
		return value == null ? "" : value.trim();
	}

	private String cleanSearch(String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
	}

	private void requireText(String value, String message) {
		if (!StringUtils.hasText(value)) {
			throw new BadRequestException(message);
		}
	}
}
