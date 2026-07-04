# Fullstack E-Commerce App

A Spring Boot–based e-commerce backend built to demonstrate production-style REST API design, layered architecture, and secure authentication. This project is being developed as a portfolio piece to showcase backend engineering skills.

---

## 🚀 Features

### ✅ Implemented
- **Catalog Module**
  - Category CRUD operations
  - Product CRUD operations
  - DTO-based request/response layer
  - Pagination and sorting support
  - Custom exception handling (global exception handler)
  - Image upload for products
- **Security (in progress)**
  - Spring Security integration
  - JWT-based authentication

### 🔜 Planned
- Role-based authorization (Admin / Customer)
- Cart and Order management
- Payment integration
- PostgreSQL migration (from H2)
- Redis caching
- Dockerization
- API documentation (Swagger/OpenAPI)

---

## 🛠️ Tech Stack

| Layer            | Technology                  |
|-------------------|------------------------------|
| Language          | Java                        |
| Framework         | Spring Boot                 |
| Security          | Spring Security, JWT        |
| Persistence       | Spring Data JPA, Hibernate   |
| Database          | H2 (dev) → PostgreSQL (planned) |
| Build Tool        | Maven                       |

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

### H2 Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: *(fill in your configured URL)*

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
- How pagination/sorting was implemented
- How the custom exception handling works
- Approach to JWT auth (token generation, filter chain, etc.)
- Any tricky bugs solved (e.g., cascade behavior, FK constraint issues) — worth a short "Challenges & Learnings" section

---

## 📸 Screenshots / Demo

> Coming soon.

---

## 🗺️ Roadmap

- [ ] Complete JWT authentication
- [ ] Add role-based authorization
- [ ] Cart & Order modules
- [ ] Migrate to PostgreSQL
- [ ] Add Docker support
- [ ] Deploy (Render/Railway/AWS)

---

## 👤 Author

**Abhishek**
Backend developer transitioning into Java/Spring Boot roles.
[GitHub](https://github.com/NexusPrime217)
