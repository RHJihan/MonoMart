# MonoMart

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![JWT](https://img.shields.io/badge/JWT-Authentication-red)
![Docker](https://img.shields.io/badge/Docker-Supported-blue)

MonoMart is a modern, single-vendor e-commerce backend application built with **Spring Boot 3**, **PostgreSQL**, and **JWT authentication**. It provides a complete RESTful API for managing products, categories, shopping carts, orders, and user authentication with role-based access control.

## 🌟 Features

### Core Functionality
- **User Management**: Registration, authentication, and role-based access (USER/ADMIN)
- **Product Catalog**: Full CRUD operations for products with category organization
- **Category Management**: Hierarchical product categorization
- **Shopping Cart**: Add, update, remove items with persistent cart functionality
- **Order Processing**: Create orders from cart, track order status
- **JWT Authentication**: Secure token-based authentication with refresh tokens

### Technical Highlights
- **Modern Stack**: Spring Boot 3.3.2 with Java 17
- **Database**: PostgreSQL with Liquibase migrations
- **Security**: Spring Security with JWT tokens
- **API Documentation**: Swagger/OpenAPI integration
- **Data Mapping**: MapStruct for efficient DTO mappings
- **Validation**: Bean validation with custom validators
- **CORS**: Configurable cross-origin resource sharing
- **Containerized**: Docker and Docker Compose ready

## 🏗️ Architecture

```
├── controller/          # REST API endpoints
├── service/            # Business logic layer
├── repository/         # Data access layer
├── domain/            # Entity models
├── dto/               # Data transfer objects
├── security/          # JWT and authentication
├── config/            # Application configuration
└── exception/         # Global exception handling
```

## 📋 Prerequisites

### Development Environment
- **Java 17** or higher
- **Maven 3.6+**
- **PostgreSQL 12+**

### Optional
- **Docker** and **Docker Compose** (for containerized development)
- **Postman** (for API testing)

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd MonoMart
   ```

2. **Start services with Docker Compose**
   ```bash
   docker-compose up -d
   ```
   
   This will start:
   - PostgreSQL database on port 5432
   - MonoMart application on port 8080

3. **Verify the setup**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

### Option 2: Local Development Setup

1. **Clone and navigate to project**
   ```bash
   git clone <repository-url>
   cd MonoMart
   ```

2. **Setup PostgreSQL database**
   ```bash
   # Create database
   createdb monomart
   
   # Or using psql
   psql -U postgres -c "CREATE DATABASE monomart;"
   ```

3. **Configure database connection**
   Update `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/monomart
       username: your_username
       password: your_password
   ```

4. **Set JWT secret (important for production)**
   ```bash
   export MONOMART_JWT_SECRET="your-very-strong-256-bit-secret-key-here"
   ```

5. **Build and run**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

## 🔧 Development Environment Setup

### IDE Configuration

#### IntelliJ IDEA
1. Enable annotation processing for Lombok and MapStruct
2. Install Lombok plugin
3. Set Project SDK to Java 17
4. Import Maven dependencies

#### VS Code
1. Install Java Extension Pack
2. Install Spring Boot Extension Pack
3. Configure Java 17 as default

### Database Setup

#### Using Docker (Recommended)
```bash
docker run --name postgres-monomart \
  -e POSTGRES_DB=monomart \
  -e POSTGRES_USER=monomart \
  -e POSTGRES_PASSWORD=monomart \
  -p 5432:5432 \
  -d postgres:16
```


### Environment Variables

Create `.env` file or set environment variables:

```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/monomart
SPRING_DATASOURCE_USERNAME=monomart
SPRING_DATASOURCE_PASSWORD=monomart

# JWT Configuration
MONOMART_JWT_SECRET=your-256-bit-secret-key-change-in-production

# Application Configuration
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
```

### Building the Project

```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package application
./mvnw clean package

# Skip tests during packaging
./mvnw clean package -DskipTests
```

### Running in Development Mode

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Using Maven with profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Using Java directly
java -jar target/monomart-0.0.1-SNAPSHOT.jar
```

## 🌐 API Documentation

### Swagger UI
Once the application is running, access the interactive API documentation:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### API Endpoints Overview

#### Authentication
- `POST /api/v1/auth/signup` - User registration
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/admin/login` - Admin login
- `POST /api/v1/auth/refresh` - Refresh JWT token

#### Products
- `GET /api/v1/products` - List products (paginated)
- `GET /api/v1/products/{id}` - Get product details
- `POST /api/v1/products` - Create product (Admin only)
- `PUT /api/v1/products/{id}` - Update product (Admin only)
- `DELETE /api/v1/products/{id}` - Delete product (Admin only)

#### Categories
- `GET /api/v1/categories` - List all categories
- `GET /api/v1/categories/{id}` - Get category details
- `POST /api/v1/categories` - Create category (Admin only)
- `PUT /api/v1/categories/{id}` - Update category (Admin only)
- `DELETE /api/v1/categories/{id}` - Delete category (Admin only)

#### Shopping Cart
- `GET /api/v1/cart` - Get user's cart
- `POST /api/v1/cart/items` - Add item to cart
- `PUT /api/v1/cart/items/{id}` - Update cart item
- `DELETE /api/v1/cart/items/{id}` - Remove cart item
- `DELETE /api/v1/cart` - Clear entire cart

#### Orders
- `GET /api/v1/orders` - List all orders (Admin) or user orders
- `GET /api/v1/orders/{id}` - Get order details
- `POST /api/v1/orders` - Create order from cart
- `PUT /api/v1/orders/{id}/status` - Update order status (Admin only)

### Sample API Usage

#### Register a new user
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "Passw0rd!"
  }'
```

#### Login and get JWT token
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "john",
    "password": "Passw0rd!"
  }'
```

#### Access protected endpoints
```bash
curl -X GET http://localhost:8080/api/v1/cart \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Postman Collection
Import the provided Postman collection from `postman/MonoMart.postman_collection.json` for ready-to-use API requests.


### Database Migration

```bash
# Backup database before migration
pg_dump -h your-db-host -U username -d monomart > backup.sql

# Run migrations (automatic on application startup)
# Or manually with Liquibase
./mvnw liquibase:update -Dspring.profiles.active=prod
```

### Monitoring and Health Checks

#### Health Check Endpoint
```bash
curl http://localhost:8080/actuator/health
```

#### Application Metrics
```bash
curl http://localhost:8080/actuator/info
curl http://localhost:8080/actuator/metrics
```

#### Log Configuration
Configure centralized logging in production:

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
  file:
    name: /var/log/monomart/application.log
```

## 🧪 Testing

### Running Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ProductServiceTest

# Run with coverage
./mvnw test jacoco:report
```

### Test Categories
- **Unit Tests**: Service layer business logic
- **Integration Tests**: Repository and database operations
- **Web Layer Tests**: Controller endpoints

### Test Database Configuration
Tests use H2 in-memory database by default. Configuration in `application-test.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

## 🔍 Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

#### Database Connection Issues
```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# Test connection
psql -h localhost -U monomart -d monomart
```

#### JWT Token Issues
- Ensure `MONOMART_JWT_SECRET` is set and at least 256 bits (32 characters)
- Check token expiration in application logs
- Verify token format in Authorization header: `Bearer <token>`

#### Docker Issues
```bash
# Check container logs
docker-compose logs app

# Restart services
docker-compose restart

# Clean rebuild
docker-compose down -v
docker-compose up --build
```

### Logging and Debugging

#### Enable Debug Logging
```yaml
logging:
  level:
    com.monomart: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
```

#### Application Logs Location
- Development: Console output
- Production: `/var/log/monomart/application.log`
- Docker: `docker-compose logs app`


## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

**MonoMart** - Building the future of e-commerce, one API at a time! 🚀
