# Holiday Service

A Spring Boot application that provides holiday information for different countries by integrating with an external holiday API.

## Features

- Get the most recent holidays for a specific country
- Find holidays that don't fall on weekends
- Compare holidays between two countries to find common dates
- Input validation for country codes and years
- Error handling
- Swagger documentation

## API Endpoints

### Get Recent Holidays
```http
GET /api/holidays/most-recent/{countryCode}/{count}
```
Retrieves the most recent holidays for a specific country.

**Parameters:**
- `countryCode`: ISO 3166-1 alpha-2 country code (e.g., US, GB, DE)
- `count`: Number of holidays to retrieve

**Validation:**
- Country code must be exactly 2 uppercase letters
- Count must be a positive number

### Get Holidays Not on Weekends
```http
GET /api/holidays/not-weekends?year={year}&countryCodes={countryCodes}
```
Returns the count of public holidays that don't fall on weekends for each country.

**Parameters:**
- `year`: Year to check holidays for (2000-2100)
- `countryCodes`: Comma-separated list of country codes

**Validation:**
- Year must be between 2000 and 2100
- At least one country code must be provided
- Each country code must be 2 uppercase letters

### Get Common Holidays
```http
GET /api/holidays/common?year={year}&countryCode1={countryCode1}&countryCode2={countryCode2}
```
Returns a list of holidays that are celebrated in both countries.

**Parameters:**
- `year`: Year to check holidays for (2000-2100)
- `countryCode1`: First country code
- `countryCode2`: Second country code

**Validation:**
- Year must be between 2000 and 2100
- Both country codes must be 2 uppercase letters

## Error Handling

The service provides standardized error responses for various scenarios:

- Invalid country codes
- Invalid year ranges
- Empty or invalid input parameters
- External API errors
- Validation errors

Example error response:
```json
{
    "timestamp": "2024-03-20T10:30:00",
    "status": 400,
    "error": "Validation error",
    "message": "Country code must be 2 uppercase letters",
    "path": "/api/holidays/most-recent/ABC/3"
}
```

## Architecture

The application follows clean architecture principles with the following layers:

- **Adapter Layer**: Controllers and exception handlers
- **Application Layer**: Use cases and business logic
- **Domain Layer**: Models, interfaces, and domain exceptions
- **Infrastructure Layer**: External API integration and configuration

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. Clone the repository:
```bash
git clone https://github.com/kapilmishra/accenture-technical-assignment.git
cd holidays-service
```

2. Build the application:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on port 8080.

### Accessing the API Documentation

Once the application is running, you can access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Testing

The application includes comprehensive test coverage:

- Unit tests for controllers, use cases, and gateways
- Integration tests for API endpoints
- Validation test cases
- Error handling tests

Run the tests using:
```bash
mvn test
```

## Configuration

The application can be configured through `application.yml`:

```yaml
holiday:
  api:
    base-url: https://date.nager.at/api/v3

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```