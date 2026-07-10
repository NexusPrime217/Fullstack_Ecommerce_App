# Fullstack E-Commerce App

A Spring Boot–based e-commerce backend built to demonstrate production-style REST API design, layered architecture, and secure authentication. This project is being developed as a portfolio piece to showcase backend engineering skills.

---

## 🚀 Features

### ✅ Implemented

- **Authentication & Authorization**
  - JWT-based authentication with HTTP-only cookies
  - Role-based access control (Admin / User / Seller)
  - Sign up, sign in, sign out endpoints
  - Password hashing with BCrypt
  - Environment variable-based secret key (no hardcoded secrets)

- **Catalog Module**
  - Category and Product CRUD
  - Pagination and sorting on all list endpoints
  - Product image upload
  - Input validation on all request DTOs
  - Keyword-based product search
  - Filter products by category

- **Cart Module**
  - Add product to cart (auto-creates cart on first add)
  - View cart for logged-in user
  - Update product quantity (+1 / -1, auto-removes at 0)
  - Remove product from cart

- **Address Module**
  - Create, view, update, delete addresses
  - Users can only update/delete their own addresses (IDOR protection)

- **Order Module**
  - Place order from cart (creates order, records payment details, deducts stock, clears cart)
  - Payment gateway fields supported (pg name, payment ID, status, response message)

- **Cross-cutting**
  - Global exception handler (`@RestControllerAdvice`)
  - Custom exceptions (`ResourceNotFoundException`, `APIException`)
  - DTO-based request/response layer (entities never exposed directly)
  - PostgreSQL database

### 🔜 Planned
- Swagger / OpenAPI documentation
- Docker support
- Deployment (Render / Railway / AWS)
- Redis caching

### ☁️ Deployment
Deployed on AWS Elastic Beanstalk with Amazon RDS (PostgreSQL) as the
database. RDS access is restricted to the Beanstalk security group only.
Instance is currently stopped to avoid costs — see Getting Started to run locally.

### 🔜 Planned
- Redis caching
- Dockerization
- API documentation (Swagger/OpenAPI)

---

## 🛠️ Tech Stack

| Layer            | Technology |
|-------------------|------------|
| Language          | Java       |
| Framework         | Spring Boot |
| Security          | Spring Security, JWT |
| Persistence       | Spring Data JPA, Hibernate |
| Database          | PostgreSQL |
| Build Tool        | Maven      |

---

## 📂 Project Structure

```
src/main/java/.../
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Spring Data JPA repositories
├── entity/         # JPA entities
├── dto/            # Request/response DTOs
├── exception/      # Custom exceptions + global handler
├── security/       # JWT + Spring Security config
└── config/         # App-level configuration
```

---

## ⚙️ Getting Started

### Prerequisites
- JDK 21+
- Maven 3.8+

### Run locally
```bash
git clone https://github.com/NexusPrime217/Fullstack_Ecommerce_App.git
cd Fullstack_Ecommerce_App
./mvnw spring-boot:run
```


### Environment Variables

Configure the following environment variables before running the application:

| Variable   | Description                        |
|------------|------------------------------------|
| JWTSECRET  | Secret key used to sign JWT tokens |
| DBPASSWORD | Password for accessing database    |

For IntelliJ:
Run → Edit Configurations → Environment Variables

Example:
*JWTSECRET*=your-long-random-secret
---

## 📡 API Endpoints

> more information to be added

| Method | Endpoint                | Description         | Auth Required |
|--------|--------------------------|----------------------|----------------|
| GET    | `/api/products`          | List products (paginated) | No |
| POST   | `/api/products`          | Create product       | Yes (Admin) |
| GET    | `/api/categories`        | List categories      | No |
| POST   | `/api/auth/login`        | Authenticate user, returns JWT | No |

---

## 🧩 Key Design Decisions

- Why DTOs instead of exposing entities directly
  >Entities contain JPA annotations, bidirectional relationships, and fields like passwords or internal IDs that should never leave the server. Exposing them directly risks accidentally leaking sensitive data and tightly couples the API contract to the database schema. DTOs give full control over what each endpoint returns — for example, CartDTO returns a flat list of ProductDTO objects rather than exposing the CartItem join entity to the client.

- How pagination/sorting was implemented
  >All list endpoints (products, categories) accept pageNumber, pageSize, sortBy, and sortDir as query parameters with sensible defaults defined in AppConstants. Spring Data's Pageable and Page<T> handle the actual DB query, and results are wrapped in a response object (ProductResponse, CategoryResponse) that includes metadata like total pages and total elements alongside the data.
- How the custom exception handling works
  >A single @RestControllerAdvice class intercepts all exceptions across the application. Custom exceptions (ResourceNotFoundException for missing entities, APIException for business rule violations) are thrown from the service layer and mapped to appropriate HTTP status codes and a consistent JSON error structure. This keeps controllers and services free of try-catch boilerplate.
- Approach to JWT auth (token generation, filter chain, etc.)
  >On sign-in, a JWT is generated and sent to the client as an HTTP-only cookie rather than in the response body. This means the token is automatically attached to every subsequent request by the browser without JavaScript needing to handle it. A custom filter (AuthTokenFilter) intercepts each request, extracts the token from the cookie, validates it, and sets the authentication in Spring's SecurityContext. The secret key is loaded from an environment variable, not hardcoded.
- Any tricky bugs solved (e.g., cascade behavior, FK constraint issues) — worth a short "Challenges & Learnings" section
  - >IDOR vulnerability in address update — The initial updateAddress implementation accepted an addressId from the URL and fetched it directly, meaning any authenticated user could modify another user's address by guessing the ID. Fixed by adding findByAddressIdAndUserId in the repository, so the query only succeeds if the address belongs to the currently logged-in user.
  - >RDS security group configuration — After deploying on Elastic Beanstalk, the app couldn't reach the RDS instance. Fixed by configuring the RDS security group to allow inbound traffic only from the Elastic Beanstalk security group rather than opening it to the public internet.
---

## 📸 Screenshots / Demo

> Coming soon.

---

## 🗺️ Roadmap

- [✅] Complete JWT authentication
- [✅] Add role-based authorization
- [✅] Cart & Order modules
- [✅] Migrate to PostgreSQL
- [✅] Deploy (Render/Railway/AWS)
-  [ ] Add Docker support

---

## 👤 Author

**Abhishek**
Backend developer transitioning into Java/Spring Boot roles.
[GitHub](https://github.com/NexusPrime217)
