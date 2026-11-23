# Invoice Manager

A Spring Boot backend for managing invoices, built on plain JDBC (`NamedParameterJdbcTemplate`) against MySQL, with role-based user accounts.

## Tech stack

- Java 17, Spring Boot 4.1.2
- Spring Web MVC, Spring Data JDBC
- MySQL 8.4 (via Docker)
- Lombok

## Prerequisites

- Java 17+
- Docker (for MySQL)
- No local Maven install needed — use the included wrapper (`./mvnw`)

## Getting started

1. Start MySQL:
   ```
   docker-compose up -d
   ```
2. Run the app (defaults to the `dev` profile):
   ```
   ./mvnw spring-boot:run
   ```
3. The API is available at `http://localhost:8080`.

On first run, the `dev` profile points at the MySQL container started above (`invoicemanager`/`invoicemanager`, database `invoicemanager`). The schema is defined in `src/main/resources/schema.sql`; it is **not** run automatically (`spring.sql.init.mode: never`) to avoid wiping data on every restart — apply it manually against the database when you need a fresh schema.

## Profiles

Configuration is split by environment:

- `application.yml` — shared settings (port, Jackson config). `spring.profiles.active` is resolved at build time from a Maven property.
- `application-dev.yml` — local MySQL datasource, `ddl-auto: update`, verbose SQL logging.
- `application-prod.yml` — datasource read from `DB_URL`/`DB_USERNAME`/`DB_PASSWORD` env vars, `ddl-auto: validate`, no SQL logging. (No prod server exists yet — this is scaffolding.)

The Maven `dev` profile is active by default, so `./mvnw spring-boot:run` just works. To build a prod-targeted jar:
```
./mvnw clean package -Pprod
```

## API

### `POST /user/register`

Registers a new user, assigns the default `ROLE_USER` role, and creates an account-verification record.

```
curl -X POST http://localhost:8080/user/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Doe","email":"jane.doe@example.com","password":"Password123!"}'
```

Required fields: `firstName`, `lastName`, `email`, `password`.

Responses use a consistent `HttpResponse` envelope (`timeStamp`, `statusCode`, `httpStatus`, `message`, `data`). Errors (e.g. duplicate email) come back as a structured `4xx`/`5xx` via a global `@RestControllerAdvice`, not a generic Spring error page.

## Roles

Seeded in `schema.sql` on schema creation:

| Role | Permissions |
|---|---|
| `ROLE_USER` | `user:read`, `customer:read` |
| `ROLE_MANAGER` | `user:read`, `user:update`, `customer:read`, `customer:update` |
| `ROLE_ADMIN` | `user:read`, `user:create`, `user:update`, `customer:read`, `customer:create`, `customer:update` |
| `ROLE_SYSADMIN` | all of the above, plus `user:delete`, `customer:delete`, `system:manage` |
