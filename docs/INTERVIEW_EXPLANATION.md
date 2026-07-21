# Interview Explanation - Sawai Insurance Management System

## 🎯 What is this project in one line?

> A web app that lets an insurance company manage their customers, policies, and sales leads using a Java backend and a React frontend.

---

## 📦 Three Things You Can Do

| Module | What it stores | Example |
|---|---|---|
| **Customers** | People who buy insurance | Aarav Sharma, age 28, Active |
| **Policies** | Insurance plans linked to a customer | Family Health Plan, ₹18,500/year |
| **Leads** | People who might become customers | Neha Verma, called from website, NEW |

---

## 🏗️ How is the code organized? (VERY IMPORTANT TO EXPLAIN)

Every module (Customer, Policy, Lead) follows **exactly the same 4-layer pattern**:

```
[Entity]     →   [Repository]     →   [Service]     →   [Controller]
 (DB table)      (talks to DB)      (validates)      (handles HTTP)
```

### Layer 1: Entity (The Database Table)
- A Java class that maps to a MySQL table
- Each field = one column in the database
- Example: `Customer.java` has fields like `firstName`, `email`, `dateOfBirth`
- Annotations like `@Entity` and `@Column` tell Spring how to store it

### Layer 2: Repository (The Database Talker)
- An interface that extends `JpaRepository`
- Spring automatically gives you: `save()`, `findById()`, `findAll()`, `delete()`
- You just write custom queries (like search) using `@Query`
- Example: `CustomerRepository.java`

### Layer 3: Service (The Business Logic)
- Contains ALL the validation rules
- Checks: Is email valid? Is date not in future? Does customer exist?
- Calls the Repository to save/find/delete data
- Converts Entity → Response DTO before sending back
- Example: `CustomerService.java`

### Layer 4: Controller (The HTTP Endpoint)
- Maps URLs like `/api/customers` to Java methods
- `@GetMapping` → GET request
- `@PostMapping` → POST request (create)
- `@PutMapping` → PUT request (update)
- `@DeleteMapping` → DELETE request
- Example: `CustomerController.java`

### 🧠 INTERVIEW TIP: Why this pattern?
Tell the interviewer: *"Each layer has ONE responsibility. Entity = database shape. Repository = database queries. Service = business rules. Controller = HTTP. This is called Separation of Concerns. If I change how validation works, I only change the Service. If I change the database, I only change the Repository."*

---

## 🔐 How Authentication Works (Explain This Clearly)

The app uses **Token-based authentication** (NOT passwords or login).

1. Frontend sends a header: `X-Auth-Token: SAWAI_ADMIN_TOKEN_2026`
2. A **Interceptor** (middleware) catches every request BEFORE it reaches the Controller
3. The Interceptor checks: Is this token valid? What role does it have?
4. **Two roles:**
   - **ADMIN** → Can do everything (Create, Read, Update, Delete)
   - **AGENT** → Can Create, Read, Update but **NOT Delete**
5. If token is missing/wrong → `401 Unauthorized`
6. If AGENT tries to delete → `403 Forbidden`

### The Code Flow for Auth:

```
Frontend sends request with X-Auth-Token header
         ↓
TokenAuthorizationInterceptor.preHandle() runs FIRST
         ↓
Checks: Is token valid?  →  If NO → return 401
         ↓
Checks: Is method DELETE and role = AGENT?  →  If YES → return 403
         ↓
If all OK → Request continues to Controller → Service → Repository
```

### 🧠 INTERVIEW TIP: Why not use Spring Security?
Tell the interviewer: *"The assignment specifically asked for custom header-based auth. I used a HandlerInterceptor which is Spring's way of adding middleware. This is simpler than Spring Security for this case - I just check one header value."*

---

## 📝 Validation Rules (To Show You Handle Edge Cases)

### Customer Validation:
- First name, last name, email, phone, DOB, status → all required
- Email must have `@` and `.` format (e.g., abc@example.com)
- Email must be unique (no two customers with same email)
- Date of birth cannot be in the future

### Policy Validation:
- All fields required
- Premium must be > ₹0
- Coverage term must be > 0 months
- Policy number must be unique
- **The customer must already exist** before you can create their policy

### Lead Validation:
- All fields required
- No special uniqueness checks (leads can have duplicate info)

---

## ❌ Error Handling (Predictable Responses)

Every error returns a **consistent JSON format**:

```json
{
  "timestamp": "2026-07-14T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Email format is invalid",
  "path": "/api/customers"
}
```

| HTTP Status | When it happens |
|---|---|
| **400 Bad Request** | Validation fails (missing field, bad email, etc.) |
| **401 Unauthorized** | Token missing or invalid |
| **403 Forbidden** | AGENT tries to DELETE |
| **404 Not Found** | Customer/Policy/Lead ID doesn't exist |
| **409 Conflict** | Duplicate email or policy number |

---

## 💻 Frontend (React)

### What it is:
A React app built with Vite. **One main component (`App.jsx`)** that manages everything using React hooks.

### How it works:
1. **`useState` hooks** store: current module, role, records, form data, editing ID, status message
2. **`useEffect`** loads all data from backend when the app starts
3. When user clicks something → React calls the backend API → gets JSON response → updates state → React re-renders automatically
4. **`api()` helper function** sends all requests with the `X-Auth-Token` header

### Key Frontend Files:
- `index.html` → Just a `<div id="root">` and script tag
- `src/main.jsx` → React entry point (8 lines)
- `src/App.jsx` → Main React component with all logic (~200 lines)
- `src/index.css` → All styling

### Why React instead of plain JS?
- **State management** is built-in with `useState` - no manual DOM manipulation
- **Re-rendering** is automatic when state changes
- **Components** make the code reusable and organized
- **JSX** lets you write HTML-like code inside JavaScript

---

## 🎤 Interview Demo Script (Practice This)

1. "First, I start MySQL and the Spring Boot backend on port 8080"
2. "Then I start the frontend on port 5173 using `npm run dev`"
3. "I keep the role as ADMIN"
4. **Create a customer** → Fill form, click Save → Shows in table
5. **Create a policy** → Select that customer → Shows in table
6. **Create a lead** → Fill prospect info → Shows in table
7. **Search** → Type something in search box → Only matching records show
8. **Edit** → Click Edit on any record → Form fills up → Change and Save
9. **Switch to AGENT role** → Try to Delete → Backend returns 403
10. **Switch back to ADMIN** → Delete succeeds

---

## 💡 Top 5 Interview Talking Points

### 1. "I used DTOs (Request and Response records)"
*"The API sends and receives different data than what's in the database. For example, Policy Response includes customerName, but the database only stores customerId. This keeps the API flexible without changing the database."*

### 2. "Validation is in the Service layer, not the Controller"
*"Business rules belong in the Service. The Controller just passes data to the Service. If validation rules change, I only modify one place."*

### 3. "I used records in Java"
*"CustomerRequest and CustomerResponse are Java records - they automatically generate getters, constructors, equals, and toString. Less boilerplate code."*

### 4. "The interceptor ensures security even if someone bypasses the frontend"
*"Security is in the backend. Even if someone uses Postman or curl to send a DELETE request with an AGENT token, the backend still blocks it. The frontend is never trusted."*

### 5. "I used @Transactional for data consistency"
*"If something fails mid-way, the database rolls back to its previous state. No partial saves or corrupted data."*

---

## ❓ Common Interview Questions & Answers

**Q: Why did you choose Spring Boot?**
A: "It's the standard for Java web apps. It handles JSON conversion, database connections, and HTTP routing automatically. I just write the business logic."

**Q: How does the frontend communicate with the backend?**
A: "Using REST API calls. React's `fetch()` sends HTTP requests to the backend URLs. The backend returns JSON. React updates its state and re-renders automatically."

**Q: How did you handle the Customer-Policy relationship?**
A: "In the database, the Policy table has a `customer_id` foreign key. In Java, I used `@ManyToOne` annotation. When creating a policy, I check that the customer ID exists first."

**Q: What would you improve for production?**
A: "Add proper login with passwords instead of static tokens, add pagination for large datasets, use Docker for easy deployment, and add automated tests for all APIs."

**Q: Why did you use React?**
A: "React makes state management easy with hooks like `useState` and `useEffect`. The UI automatically updates when data changes - no manual DOM manipulation needed. It's also the most popular frontend framework, so it's good for my resume."

**Q: How is the React app structured?**
A: "It's a single-page app with one main component `App.jsx`. It uses `useState` for all data (records, form, editing state) and `useEffect` to load data on startup. The `api()` helper function handles all backend calls with the auth token."