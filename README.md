# DigiBankJWT

A Spring Boot REST API demonstrating JWT-based authentication and role-based access control (RBAC) for a digital banking context.

---

## Tech Stack

- Java 21
- Spring Boot 3.3
- Spring Security 6
- JJWT 0.11.5
- PostgreSQL (H2 for dev/testing)
- Lombok

---

## How It Works

### Authentication Flow

1. Register a user with an email, password, and role (`CUSTOMER` or `ADMIN`)
2. Login to receive an **access token** (15 min) and a **refresh token** (24 hrs)
3. Include the access token in the `Authorization: Bearer <token>` header on protected requests
4. Use the refresh token to get a new access token when it expires

### JWT Token Structure

Each token carries:
- `sub` — user email
- `role` — `ROLE_ADMIN` or `ROLE_CUSTOMER`
- `type` — `access` or `refresh`
- `iat` / `exp` — issued at / expiration timestamps

The `JwtTokenValidationFilter` intercepts every request, validates the token, and sets the security context. Refresh tokens are explicitly rejected for API access — only `type: access` tokens are allowed through.

---

## Role-Based Access Control

Two methods are used to enforce roles:

### 1. URL-based rules in `SecurityConfig`

Configured via `authorizeHttpRequests` in the security filter chain:

```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
.requestMatchers("/api/customer/**").hasRole("CUSTOMER")
```

### 2. Method-level security with `@PreAuthorize`

Enabled via `@EnableMethodSecurity` on the config class. Used in `SharedController` for fine-grained control:

```java
@PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasRole('CUSTOMER')")
```

---

## API Endpoints

### Auth — `/auth` (public)

| Method | Endpoint         | Description                          |
|--------|------------------|--------------------------------------|
| POST   | `/auth/register` | Register a new user                  |
| POST   | `/auth/login`    | Login and receive access/refresh tokens |
| POST   | `/auth/refresh`  | Exchange a refresh token for a new access token |

#### Register request body
```json
{
  "email": "[email]",
  "password": "[password]",
  "role": "CUSTOMER"
}
```

#### Login request body
```json
{
  "email": "[email]",
  "password": "[password]"
}
```

#### Login response
```json
{
  "msg": "Hello Customer",
  "accessToken": "<jwt>",
  "refreshToken": "<jwt>"
}
```

#### Refresh request body
```json
{
  "refreshToken": "<jwt>"
}
```

---

### Admin — `/api/admin` (requires `ROLE_ADMIN`)

| Method | Endpoint                  | Description          |
|--------|---------------------------|----------------------|
| GET    | `/api/admin/manage-users` | Admin user management view |

---

### Customer — `/api/customer` (requires `ROLE_CUSTOMER`)

| Method | Endpoint                  | Description              |
|--------|---------------------------|--------------------------|
| GET    | `/api/customer/profile`   | View customer profile    |

---

### Shared — `/api/custom` (method-level security)

| Method | Endpoint                       | Allowed Roles            |
|--------|--------------------------------|--------------------------|
| GET    | `/api/custom/shared-resources` | `ADMIN` or `CUSTOMER`    |
| GET    | `/api/custom/admin`            | `ADMIN` only             |
| GET    | `/api/custom/customer`         | `CUSTOMER` only          |

---

## Configuration

`src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/jwtpoc
spring.datasource.username=postgres
spring.datasource.password=postgres

jwt.secret=<your-secret>
jwt.expiration=900000        # 15 minutes
jwt.refreshExpiration=86400000  # 24 hours
```

---

## Running the App

```bash
./mvnw spring-boot:run
```

Make sure PostgreSQL is running and the `jwtpoc` database exists, or switch the datasource to H2 for local testing.
