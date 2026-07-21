# Sawai Insurance Management System

## What is this project?

A simple web app to manage insurance records. You can:
- **Add, view, edit, and delete** customers
- **Add, view, edit, and delete** insurance policies
- **Add, view, edit, and delete** sales leads

## Tech Stack (Simple Explanation)

| Layer | What it uses | Why |
|---|---|---|
| **Backend** | Java + Spring Boot | Handles all business logic and data |
| **Database** | MySQL | Stores all records permanently |
| **Frontend** | React + Vite | Shows the UI in the browser (components managed by React state) |
| **Auth** | Custom token header | Secures the API |

---

## How It Works (The Big Picture)

```
User clicks button in React app
         |
         v
React calls fetch() to backend API
(with X-Auth-Token header)
         |
         v
Spring Boot checks if token is valid
         |
         v
If valid -> Controller receives request
         |
         v
Service does validation + business logic
         |
         v
Repository reads/writes MySQL database
         |
         v
JSON response sent back to React
         |
         v
React re-renders with new data
```

---

## Project Structure

```
backend/demo/                        <-- All Java backend code
  src/main/java/com/pravin/demo/
    auth/                            <-- Token checking logic
    common/                          <-- All error handling in just 2 files
    config/                          <-- Setup for CORS + interceptors
    customer/                        <-- Customer module
    policy/                          <-- Policy module
    lead/                            <-- Lead module

frontend/my-react-app/               <-- React frontend (modular)
  src/main.jsx                       <-- React entry point
  src/App.jsx                        <-- Main component (state + logic)
  src/api.js                         <-- API config and fetch helper
  src/forms.jsx                      <-- Form fields for each module
  src/tables.jsx                     <-- Table headers and rows
  src/index.css                      <-- All styling
  index.html                         <-- Main HTML page

backend/demo/src/main/java/com/pravin/demo/export/
  ExportController.java              <-- CSV download endpoint
```

---

## How to Run

### Step 1: Start MySQL (Make sure MySQL is running)

### Step 2: Run Backend
```bash
cd backend/demo
set DB_USERNAME=root
set DB_PASSWORD=your_mysql_password
.\mvnw.cmd spring-boot:run
```

The backend will start at: `http://localhost:8080`

### Step 3: Run Frontend
```bash
cd frontend/my-react-app
npm install
npm run dev
```

The frontend will start at: `http://localhost:5173`

---

## API Rules

Every request must send this header:
```
X-Auth-Token: SAWAI_ADMIN_TOKEN_2026
```

| Role | Token | Can Delete? |
|---|---|---|
| ADMIN | `SAWAI_ADMIN_TOKEN_2026` | Yes |
| AGENT | `SAWAI_AGENT_TOKEN_2026` | No (gets 403 error) |

If token is missing or wrong → `401 Unauthorized`

---

## The 3 Modules (Simple Explanation)

### 1. Customers
People who buy insurance. Fields: name, email, phone, DOB, status.

### 2. Policies
Insurance plans. Each policy belongs to one customer. Fields: policy number, type, premium, term, start date.

### 3. Leads
Potential customers who haven't bought yet. Fields: prospect name, contact info, source, status, assigned agent.

---

## Key Interview Points

1. **Every module follows the same pattern**: Entity → Repository → Service → Controller
2. **Token auth is done via Interceptor** (middleware that runs before every request)
3. **Frontend is React** - uses `useState` and `useEffect` hooks, all logic in one component
4. **AGENT can't delete** - this is enforced in the backend, not just the frontend
5. **Email and policy number must be unique** (checked both in code and database)
