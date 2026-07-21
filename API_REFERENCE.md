# API Reference

All examples use the default admin token:

```text
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

## Customers

Create customer:

```http
POST /api/customers
Content-Type: application/json
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

```json
{
  "firstName": "Aarav",
  "lastName": "Sharma",
  "email": "aarav.sharma@example.com",
  "phoneNumber": "9876543210",
  "dateOfBirth": "1998-05-20",
  "accountStatus": "ACTIVE"
}
```

List or search customers:

```http
GET /api/customers?search=aarav
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

Update customer:

```http
PUT /api/customers/1
Content-Type: application/json
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

Delete customer:

```http
DELETE /api/customers/1
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

## Policies

Create policy:

```http
POST /api/policies
Content-Type: application/json
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

```json
{
  "policyNumber": "POL-1001",
  "policyName": "Family Health Secure",
  "policyType": "HEALTH",
  "premiumAmount": 18500.00,
  "coverageTermMonths": 12,
  "effectiveStartDate": "2026-08-01",
  "customerId": 1
}
```

List or search policies:

```http
GET /api/policies?search=health&customerId=1
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

Update policy:

```http
PUT /api/policies/1
Content-Type: application/json
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

Delete policy:

```http
DELETE /api/policies/1
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

## Leads

Create lead:

```http
POST /api/leads
Content-Type: application/json
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

```json
{
  "prospectName": "Neha Verma",
  "contactInfo": "neha.verma@example.com",
  "referralSource": "Website",
  "leadStatus": "NEW",
  "assignedAgentName": "Rohit Agent"
}
```

List or search leads:

```http
GET /api/leads?search=neha&status=NEW
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

Update lead:

```http
PUT /api/leads/1
Content-Type: application/json
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

Delete lead:

```http
DELETE /api/leads/1
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

## Auth Checks

Missing token:

```http
GET /api/customers
```

Expected result:

```text
401 Unauthorized
```

Agent delete attempt:

```http
DELETE /api/customers/1
X-Auth-Token: SAWAI_AGENT_TOKEN_2026
```

Expected result:

```text
403 Forbidden
```

## Export CSV

Download all records as a CSV file:

```http
GET /api/export
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

This returns a CSV file with 3 sections: Customers, Policies, and Leads.  
The frontend has a "Download CSV" button in the top bar that calls this endpoint.
```
