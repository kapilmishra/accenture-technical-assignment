# Holiday Service Architecture

## System Architecture

The Holiday Service is built using a clean architecture approach with the following layers:

```
+------------------+
|   Adapter Layer  |
|  +------------+  |
|  | Controller |  |
|  +------------+  |
+------------------+
         |
+------------------+
| Application Layer|
|  +------------+  |
|  | Use Cases  |  |
|  +------------+  |
+------------------+
         |
+------------------+
|   Domain Layer   |
|  +------------+  |
|  | Interfaces |  |
|  +------------+  |
|  +------------+  |
|  |  Models    |  |
|  +------------+  |
+------------------+
         |
+------------------+
| Infrastructure   |
|  +------------+  |
|  | Adapters   |  |
|  +------------+  |
+------------------+
```

### Component Details

#### 1. Adapter Layer
- **HolidayController**: REST API endpoints for holiday operations
  - Handles input validation and request/response mapping
  - Implements Swagger/OpenAPI documentation
  - Key endpoints:
    - GET `/api/holidays/last-N/{countryCode}/{count}`: Get recent holidays
    - GET `/api/holidays/not-weekends`: Get holidays not on weekends
    - GET `/api/holidays/common`: Get common holidays between countries
- **GlobalExceptionHandler**: Centralized exception handling
  - Handles validation errors
  - Handles API errors
  - Returns standardized error responses

#### 2. Application Layer
- **HolidayUseCaseImpl**: Core business logic implementation
  - Implements the `HolidayUseCase` interface
  - Handles:
    - Holiday data processing
    - Date calculations
    - Weekend filtering
    - Common holiday identification
  - Implements concurrent operations for better performance
- **Exceptions**: Application-specific exceptions
  - `HolidayApiException`: Custom exception for API errors

#### 3. Domain Layer
- **Models**:
  - `Holiday`: Represents holiday information
    - date
    - name
    - localName
    - countryCode
    - fixed
    - global
    - counties
    - launchYear
    - types
  - `CommonHolidayInfo`: Represents common holidays between countries
    - date
    - localName1
    - localName2
- **Interfaces**:
  - `HolidayUseCase`: Core business operations contract
    - getMostRecentHolidays
    - getHolidaysNotOnWeekends
    - getCommonHolidays
- **Gateways**:
  - `HolidayApiClient`: Interface for external API communication
- **Exceptions**:
  - `HolidayApiException`: Domain-specific exception for API errors

#### 4. Infrastructure Layer
- **Adapters**:
  - `HolidayApiClientImpl`: External API integration implementation
    - Manages communication with the holiday API
    - Handles error cases and retries
    - Implements proper error handling and logging
- **Configuration**:
  - `HolidayApiProperties`: External API configuration
    - Base URL configuration
    - API endpoint configuration

### External Integrations

#### Holiday API
- Base URL: Configurable through `holiday.api.baseUrl`
- Endpoint: `/PublicHolidays/{year}/{countryCode}`
- Response: Array of holiday objects
- Error Handling: Custom `HolidayApiException`

### Error Handling

#### Global Exception Handler
- Handles various exceptions:
  - `HolidayApiException`: External API errors
  - `HttpClientErrorException`: HTTP client errors
  - `ConstraintViolationException`: Validation errors
  - `MethodArgumentNotValidException`: Method argument validation errors
- Returns standardized error responses with:
  - HTTP status code
  - Error message
  - Request path
  - Timestamp

### Configuration

#### Properties
- `holiday.api.baseUrl`: External API base URL
- Validation constraints:
  - Country codes: 2 uppercase letters
  - Year range: 2000-2100
  - Minimum country codes list size: 1

### Testing Strategy

#### Unit Tests
- Controller tests with validation
- Use case tests with mocked dependencies
- Gateway tests with mocked external API

#### Integration Tests
- API endpoint tests
- End-to-end flow tests
- Error handling tests

#### Test Configuration
- Mock external API responses
- Test-specific properties
- Validation test cases