# MonoMart Environment Configuration

This document explains how to set up and use different environment configurations for MonoMart.

## Configuration Files

### Application Configuration Files

- `application.yml` - Base configuration with environment variables
- `application-dev.yml` - Development environment specific settings
- `application-prod.yml` - Production environment specific settings
- `application-test.yml` - Test environment specific settings

### Environment Variables File

- `environment-variables.properties` - Contains all available environment variables with default values

## Setting Up Environments

### 1. Development Environment

**Option A: Using Environment Variables**
```bash
export SPRING_PROFILES_ACTIVE=dev
export DB_PASSWORD=your_dev_password
export JWT_SECRET=your_dev_jwt_secret
```

**Option B: Using .env file (recommended)**
1. Create a `.env` file in the project root
2. Copy contents from `environment-variables.properties`
3. Modify values as needed for your local development

Example `.env` file:
```bash
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/monomart
DB_USERNAME=postgres
DB_PASSWORD=your_actual_password

# JWT Configuration
JWT_SECRET=your_secret_key_at_least_32_bytes_long

# Other settings
SPRING_PROFILES_ACTIVE=dev
LOG_LEVEL_APP=DEBUG
```

**Option C: Using IDE**
Set the active profile in your IDE:
- IntelliJ IDEA: Run Configuration → Environment Variables → `SPRING_PROFILES_ACTIVE=dev`
- VS Code: Set in launch.json or .env file

### 2. Production Environment

Set these environment variables in your production environment:

**Required Variables:**
```bash
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://your-prod-db:5432/monomart
DB_USERNAME=your_prod_user
DB_PASSWORD=your_prod_password
JWT_SECRET=your_strong_prod_jwt_secret_32_bytes_minimum
```

**Optional Variables (with sensible defaults):**
```bash
SERVER_PORT=8080
JWT_ACCESS_TOKEN_EXPIRATION=15
LOG_LEVEL_ROOT=WARN
API_DOCS_ENABLED=false
SWAGGER_UI_ENABLED=false
```

### 3. Test Environment

For running tests:
```bash
export SPRING_PROFILES_ACTIVE=test
```

Tests will use H2 in-memory database by default.

## Environment-Specific Features

### Development (dev)
- Enhanced logging for debugging
- H2 console enabled for database inspection
- Swagger UI enabled
- JPA DDL set to `update` for convenience
- Detailed SQL logging

### Production (prod)
- Optimized for performance and security
- Reduced logging levels
- Connection pooling configured
- Swagger UI disabled by default
- Context path support for reverse proxy setup
- File logging enabled

### Test (test)
- H2 in-memory database
- DDL set to `create-drop`
- Random port assignment
- Liquibase disabled
- Enhanced test logging

## Docker Compose Integration

The environment variables can be easily integrated with Docker Compose:

```yaml
# docker-compose.yml
services:
  app:
    build: .
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DB_URL=jdbc:postgresql://db:5432/monomart
      - DB_USERNAME=postgres
      - DB_PASSWORD=postgres
      - JWT_SECRET=dev-secret-change-me-please-32-bytes-minimum!
```

## Security Considerations

1. **Never commit .env files** - Add `.env` to your `.gitignore`
2. **Use strong JWT secrets** - Minimum 32 bytes for production
3. **Rotate secrets regularly** in production
4. **Use environment-specific databases** - Never use production DB for development
5. **Disable unnecessary endpoints** in production (Swagger, H2 console, etc.)

## Running the Application

### With Maven
```bash
# Development
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run

# Production
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run

# Test
SPRING_PROFILES_ACTIVE=test ./mvnw test
```

### With Java
```bash
java -Dspring.profiles.active=dev -jar target/monomart.jar
```

### With Docker
```bash
docker run -e SPRING_PROFILES_ACTIVE=prod -e DB_URL=... your-image
```

## Troubleshooting

1. **Profile not loading**: Check `SPRING_PROFILES_ACTIVE` environment variable
2. **Database connection issues**: Verify `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`
3. **JWT errors**: Ensure `JWT_SECRET` is at least 32 bytes long
4. **Missing environment variables**: Check if all required variables are set

## Available Environment Variables

See `environment-variables.properties` for a complete list of available environment variables with their default values.
