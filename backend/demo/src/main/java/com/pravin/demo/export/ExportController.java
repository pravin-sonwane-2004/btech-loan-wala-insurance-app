package com.pravin.demo.export;

import java.util.List;

import com.pravin.demo.customer.CustomerResponse;
import com.pravin.demo.customer.CustomerService;
import com.pravin.demo.lead.LeadResponse;
import com.pravin.demo.lead.LeadService;
import com.pravin.demo.policy.PolicyResponse;
import com.pravin.demo.policy.PolicyService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final CustomerService customerService;
    private final PolicyService policyService;
    private final LeadService leadService;

    public ExportController(CustomerService customerService,
                            PolicyService policyService,
                            LeadService leadService) {
        this.customerService = customerService;
        this.policyService = policyService;
        this.leadService = leadService;
    }

    @GetMapping
    public ResponseEntity<byte[]> exportAll() {
        StringBuilder csv = new StringBuilder();

        // --- CUSTOMERS ---
        csv.append("\n=== CUSTOMERS ===\n");
        csv.append("ID,First Name,Last Name,Email,Phone,DOB,Status\n");
        List<CustomerResponse> customers = customerService.list(null);
        for (CustomerResponse c : customers) {
            csv.append(c.id()).append(",")
               .append(escapeCsv(c.firstName())).append(",")
               .append(escapeCsv(c.lastName())).append(",")
               .append(escapeCsv(c.email())).append(",")
               .append(escapeCsv(c.phoneNumber())).append(",")
               .append(c.dateOfBirth()).append(",")
               .append(c.accountStatus()).append("\n");
        }

        // --- POLICIES ---
        csv.append("\n=== POLICIES ===\n");
        csv.append("ID,Policy Number,Policy Name,Type,Premium,Term (Months),Start Date,Customer ID,Customer Name\n");
        List<PolicyResponse> policies = policyService.list(null, null);
        for (PolicyResponse p : policies) {
            csv.append(p.id()).append(",")
               .append(escapeCsv(p.policyNumber())).append(",")
               .append(escapeCsv(p.policyName())).append(",")
               .append(p.policyType()).append(",")
               .append(p.premiumAmount()).append(",")
               .append(p.coverageTermMonths()).append(",")
               .append(p.effectiveStartDate()).append(",")
               .append(p.customerId()).append(",")
               .append(escapeCsv(p.customerName())).append("\n");
        }

        // --- LEADS ---
        csv.append("\n=== LEADS ===\n");
        csv.append("ID,Prospect Name,Contact Info,Referral Source,Status,Assigned Agent\n");
        List<LeadResponse> leads = leadService.list(null, null);
        for (LeadResponse l : leads) {
            csv.append(l.id()).append(",")
               .append(escapeCsv(l.prospectName())).append(",")
               .append(escapeCsv(l.contactInfo())).append(",")
               .append(escapeCsv(l.referralSource())).append(",")
               .append(l.leadStatus()).append(",")
               .append(escapeCsv(l.assignedAgentName())).append("\n");
        }

        byte[] bytes = csv.toString().getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "insurance_data.csv");

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}