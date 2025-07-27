E-Commerce API with JWT Authentication
A simple Spring Boot based e-commerce REST API that demonstrates JWT authentication and role-based authorization.
This project supports user and admin roles, allowing admins to manage products and users to manage their cart.

Features
Authentication & Authorization
User registration and login.

JWT token generation and validation.

Role-based access:

ROLE_USER → View products, manage cart.

ROLE_ADMIN → Full product management.

Product Management
Public access to GET products.

Admin access to create, update, activate/deactivate products.

Cart Management
Authenticated users can:

Add items to cart.

Update cart items.

Remove items from cart.

Exception Handling
Centralized error handling using @ControllerAdvice.

Tech Stack
Spring Boot 3 / Spring Security 6

JWT (jjwt)

Spring Data JPA / Hibernate

H2 in-memory database (for testing)

Maven

Setup Instructions
1. Clone the Repository
   bash
   Copy
   Edit
   git clone <repo-url>
   cd ecommerce-api
2. Configure Application Properties
   Default configuration (H2 database, JWT secret, etc.) is in application.yml:

yaml
Copy
Edit
spring:
datasource:
url: jdbc:h2:mem:ecommerce
driver-class-name: org.h2.Driver
username: sa
password: password
jpa:
hibernate:
ddl-auto: update
show-sql: true
h2:
console:
enabled: true
path: /h2-console

jwt:
secret: mysupersecretkeythatismorethan32charslong123
expiration: 3600000

server:
port: 8080
3. Build and Run
   bash
   Copy
   Edit
   mvn clean install
   mvn spring-boot:run
   Application will be available at:
   http://localhost:8080

API Endpoints
Authentication
POST /auth/register – Register user

POST /auth/register-admin – Register admin

POST /auth/login – Login (returns JWT token)

Products
GET /products – Public list of products

POST /products – Create product (ADMIN only)

PUT /products/{id}/activate – Activate product (ADMIN only)

Cart
POST /cart – Add item to cart (USER authenticated)

PUT /cart – Update cart item (USER authenticated)

DELETE /cart/{id} – Remove cart item (USER authenticated)

Role Behavior
Unauthorized User (No token) → 401 Unauthorized

User role accessing admin endpoint → 403 Forbidden

Expired token → 401 Unauthorized

Testing Credentials
Admin: Register via /auth/register-admin

User: Register via /auth/register

Use the token from /auth/login in Authorization header:

makefile
Copy
Edit
Authorization: Bearer <JWT_TOKEN>
H2 Console
Access H2 in-memory DB console at:
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:ecommerce

Future Enhancements
Integration with PostgreSQL or MySQL.

Add order management and payment APIs.

Improve error messages and validation.