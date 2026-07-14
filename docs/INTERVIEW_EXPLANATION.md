# Interview Explanation

## Short Overview

This is an insurance management system for Sawai Associates. It lets an authorized user manage customers, policies, and sales leads. The backend is a Spring Boot REST API connected to MySQL through Spring Data JPA. The frontend is a small vanilla JavaScript dashboard that calls the REST API with an `X-Auth-Token` header.

## Main Architecture

The request flow is:

```text
Frontend form or table action
  -> fetch request with X-Auth-Token
  -> WebConfig attaches TokenAuthorizationInterceptor to /api/**
  -> interceptor validates token and delete permission
  -> controller receives the HTTP request
  -> service validates business rules
  -> repository reads or writes MySQL using JPA
  -> service maps entity to response DTO
  -> controller returns JSON to frontend
```

This is easy to explain because every layer has one job.

## Backend Packages

`auth`

Handles custom token based authorization. `AuthTokenProperties` stores the admin and agent tokens. `TokenAuthorizationInterceptor` checks every `/api/**` request before the controller runs.

`config`

Connects infrastructure pieces. `WebConfig` registers CORS and the interceptor. `SecurityConfig` disables default form login/basic auth because this assignment uses custom header based authorization.

`common`

Contains shared exception classes and `ApiExceptionHandler`. This gives the frontend consistent JSON errors for bad requests, not found records, conflicts, unauthorized access, and forbidden deletes.

`customer`, `policy`, `lead`

Each module follows the same pattern:

- Entity: database table mapping
- Request record: incoming JSON shape
- Response record: outgoing JSON shape
- Repository: JPA database access
- Service: validation and business logic
- Controller: REST endpoints

## Entity Design

Customer:

- Stores first name, last name, email, phone number, date of birth, and account status.
- Email is unique.

Policy:

- Stores policy number, policy name, type, premium, term, effective date, and customer.
- Policy number is unique.
- `Policy` has a `ManyToOne` relation with `Customer`.
- The database has a `customer_id` foreign key in the policy table.

Lead:

- Stores prospect name, contact info, referral source, status, and assigned agent.
- It is independent from customers because a lead may not be converted yet.

## Authentication Flow

The assignment asks for header based authorization using `X-Auth-Token`.

1. The frontend sends `X-Auth-Token`.
2. `TokenAuthorizationInterceptor` reads the header.
3. `AuthTokenProperties.resolveRole()` converts the token to `ADMIN` or `AGENT`.
4. If the token is missing or invalid, the interceptor returns `401 Unauthorized`.
5. If the request method is `DELETE` and the role is `AGENT`, it returns `403 Forbidden`.
6. Otherwise the request continues to the controller.

Important interview point: the frontend is not trusted for security. Even if someone manually sends a DELETE request, the backend interceptor still blocks agents.

## Validation Rules

Customer validation:

- First name, last name, email, phone number, date of birth, and status are required.
- Email must have a valid format.
- Email must be unique.
- Date of birth cannot be in the future.

Policy validation:

- Policy number, policy name, type, premium, term, start date, and customer ID are required.
- Premium must be greater than zero.
- Coverage term must be greater than zero months.
- Policy number must be unique.
- Customer ID must exist before the policy is saved.

Lead validation:

- Prospect name, contact info, referral source, status, and assigned agent are required.

## Error Handling

The backend returns predictable HTTP status codes:

| Status             | Meaning                                                |
| ------------------ | ------------------------------------------------------ |
| `400 Bad Request`  | Invalid request body or failed validation              |
| `401 Unauthorized` | Missing or invalid token                               |
| `403 Forbidden`    | Agent tried to delete                                  |
| `404 Not Found`    | Entity ID does not exist                               |
| `409 Conflict`     | Duplicate unique value, such as email or policy number |

The JSON error shape is:

```json
{
  "timestamp": "2026-07-14T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Email format is invalid",
  "path": "/api/customers"
}
```

## Frontend Flow

The frontend is intentionally minimal:

- `state` stores current records, active module, selected role, filters, and edit IDs.
- `render()` rebuilds the dashboard from the current state.
- Event delegation handles button clicks, form submits, role changes, and refresh actions.
- `request()` is the common fetch helper. It adds `Content-Type` and `X-Auth-Token` headers to every API call.
- Module forms call POST for create and PUT for update.
- Table delete buttons call DELETE. If the selected role is AGENT, the backend returns 403 and the UI shows that message.

The CSS uses simple selectors, tables, and flexbox. There is no CSS grid or heavy UI framework.

## Demo Script

1. Start MySQL.
2. Run the Spring Boot backend on `http://localhost:8080`.
3. Run the Vite frontend on `http://localhost:5173`.
4. Keep role as `ADMIN`.
5. Create a customer.
6. Create a policy and select that customer.
7. Create a lead.
8. Search each module.
9. Edit one record and save it.
10. Switch role to `AGENT`.
11. Try deleting a record. Explain the backend returns 403.
12. Switch back to `ADMIN`.
13. Delete a record successfully.

## Good Interview Talking Points

- I used DTO records so the API shape is separate from the database entity.
- I kept validation in services because services are the business layer.
- I used repositories only for database access, not business decisions.
- I used a custom interceptor because the assignment specifically required middleware style token checks.
- I used CORS configuration so the Vite frontend can call the Spring Boot backend.
- I did not hardcode frontend mock data. All records come from the backend database.
- I used unique constraints at both service level and database level for important fields.

## Production Improvements

For a real production system, I would add:

- JWT or session based authentication instead of static tokens.
- Password login and user table.
- Bean Validation annotations such as `@NotBlank`, `@Email`, and `@Positive`.
- Pagination and sorting for large tables.
- Swagger or OpenAPI documentation.
- Docker Compose for MySQL and the app.
- More tests for service validation and controller endpoints.
- Audit fields such as `createdAt`, `updatedAt`, and `createdBy`.
