# Telco Data Integration

A full-stack application for integrating, normalizing, validating, and reconciling product data from incompatible CSV and JSON feeds.

Built as a technical assessment using **Java 21, Spring Boot, React, TypeScript, and Docker**.

## 1. Assignment Overview

The goal of the assignment is to integrate two incompatible product data feeds into a unified, BI-friendly product catalogue while preserving visibility into data quality issues.

The input sources differ in their structure and conventions:

| CSV Feed | JSON Feed |
|---|---|
| Hungarian field names | English field names |
| Gross prices in HUF | Net prices with VAT rates |
| CSV format | JSON format |
| Stock quantities | Stock quantities |
| No update timestamps | Update timestamps |

The solution should normalize these differences, identify matching products, detect invalid or conflicting data, and make the results accessible to users.

### Requirements

- Import product records from CSV and JSON.
- Normalize field names, identifiers, and prices.
- Validate required fields and numeric values.
- Identify duplicate records and inconsistencies between sources.
- Provide a unified product view without silently discarding conflicting records.
- Expose the results through a REST API.
- Provide a frontend for browsing and inspecting the data.

## 2. Technology Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot |
| Persistence | Spring Data JPA, H2 |
| CSV Processing | Apache Commons CSV |
| JSON Processing | Jackson |
| API Documentation | OpenAPI 3, Swagger UI, springdoc-openapi |
| Frontend | React, TypeScript, Vite |
| UI | Bootstrap 5 |
| Testing | JUnit 5 |
| Build | Maven, npm |
| Deployment | Docker, Docker Compose, Nginx |

## 3. Architecture

The application consists of two main components:

- **Backend:** imports, normalizes, stores, validates, and reconciles product records, then exposes them through a REST API.
- **Frontend:** provides a searchable and sortable catalogue with detailed validation and conflict information.

### Data Processing Pipeline

```text
CSV Feed ──> CSV Reader ──┐
                          ├──> Normalization ──> H2 Database
JSON Feed ─> JSON Reader ─┘                             │
                                                        ▼
                                                  Validation
                                                        │
                                                        ▼
                                                 Reconciliation
                                                        │
                                                        ▼
                                                  REST API
                                                        │
                                                        ▼
                                                 React Frontend
```

The source files are imported during application startup.

Normalized source records are persisted individually. Validation and reconciliation are performed when products are retrieved.

This preserves the individual source values and makes discrepancies visible instead of automatically choosing one source as authoritative.

## 4. Data Normalization

The normalization layer converts source-specific representations into a common product model.

| Field | Description |
|---|---|
| `sku` | Normalized product identifier |
| `name` | Product name |
| `manufacturer` | Manufacturer |
| `finalPriceHuf` | Gross price in HUF |
| `stock` | Available quantity |
| `ean` | Product barcode |
| `updatedAt` | Source update timestamp, if available |
| `source` | Original feed (`CSV` or `JSON`) |

### SKU Normalization

Source-specific prefixes are removed so that equivalent identifiers can be matched.

For example:

```text
CSV:  P-1001
JSON: SKU1001

Normalized SKU: 1001
```

Records are grouped using the normalized SKU.

### Price Normalization

CSV prices are already gross prices in HUF. JSON prices are net prices and must be converted.

The calculation is:

```text
grossPrice = netPrice × (1 + vatRate)
```

Example:

```text
Net price:   10,000 HUF
VAT:         27%
Gross price: 12,700 HUF
```

Monetary calculations use `BigDecimal`, with `HALF_UP` rounding to whole HUF.

A missing VAT rate defaults to 27%.

## 5. Validation

Each normalized record is validated independently.

Supported validation errors:

| Error | Description |
|---|---|
| `MISSING_SKU` | Missing product identifier |
| `MISSING_NAME` | Missing product name |
| `MISSING_PRICE` | Missing price |
| `NEGATIVE_PRICE` | Negative price |
| `MISSING_STOCK` | Missing stock quantity |
| `NEGATIVE_STOCK` | Negative stock quantity |
| `MISSING_MANUFACTURER` | Missing manufacturer |

Invalid records are retained so users can inspect the original data quality issues.

A reconciled product is considered valid only when all its associated source records pass validation.

## 6. Reconciliation and Conflict Detection

Records with the same normalized SKU are grouped together.

The reconciliation process compares the records and reports inconsistencies.

| Conflict | Description |
|---|---|
| `DUPLICATE_SKU` | Multiple records with the same SKU from the same source |
| `STOCK_MISMATCH` | Different stock quantities |
| `PRICE_MISMATCH` | Different gross prices |
| `NAME_MISMATCH` | Different product names |
| `MANUFACTURER_MISMATCH` | Different manufacturers |

Records are compared pairwise within each SKU group.

Price comparisons use `BigDecimal.compareTo()`. Name and manufacturer comparisons are case-insensitive and ignore surrounding whitespace.

Missing values are handled by validation rather than automatically being classified as conflicts.

### Example: Stock Conflict

```text
Product SKU: 1001

CSV:
  Price: 12,700 HUF
  Stock: 15

JSON:
  Price: 12,700 HUF
  Stock: 14
```

The product is valid because both records satisfy the validation rules.

However, it has a `STOCK_MISMATCH` conflict.

**Validity and conflict status are separate concepts.** A product may be valid while still containing conflicts.

Records without a usable SKU remain separate rather than being merged into one group.

## 7. REST API

The backend exposes a REST API for retrieving reconciled products.

### `GET /products`

Returns the product catalogue, including validation results and detected conflicts.

Supported query parameters:

| Parameter | Type | Default | Description |
|---|---|---|---|
| `filter` | String | None | Search by SKU, name, or manufacturer |
| `sort` | Enum | `SKU` | Sort by SKU, name, price, or stock |
| `onlyValid` | Boolean | `false` | Return only valid products |

Supported sorting values:

- `SKU`
- `NAME`
- `PRICE`
- `STOCK`

Sorting is case-insensitive, so both `sort=price` and `sort=PRICE` are accepted.

Examples:

```http
GET /products
GET /products?filter=router
GET /products?sort=price
GET /products?onlyValid=true
GET /products?filter=router&sort=stock&onlyValid=true
```

Unsupported sorting values result in HTTP `400 Bad Request`.

### Response Structure

Each reconciled product contains:

| Field | Description |
|---|---|
| `sku` | Normalized SKU |
| `valid` | Whether all source records are valid |
| `hasConflicts` | Whether discrepancies were detected |
| `records` | Individual source records and validation results |
| `conflicts` | Detected conflicts between source records |

The response preserves all matching records instead of selecting a single source as the correct one.

## 8. Swagger / OpenAPI Documentation

The backend includes interactive API documentation using **springdoc-openapi and Swagger UI**.

The OpenAPI specification is generated automatically from the Spring MVC controllers, request models, response DTOs, and Swagger annotations.

Once the backend is running:

| Resource | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| OpenAPI YAML | http://localhost:8080/v3/api-docs.yaml |

### Using Swagger UI

1. Open the Swagger UI URL.
2. Expand the `GET /products` endpoint.
3. Select **Try it out**.
4. Enter optional parameters such as `filter`, `sort`, or `onlyValid`.
5. Select **Execute** to send the request.

Swagger UI displays the generated request, response status, response body, and documented response schemas.

The `sort` parameter is represented by the `ProductSort` enum, making the supported sorting options visible in the API documentation.

## 9. Frontend

The React frontend provides an interface for exploring the reconciled catalogue.

Implemented features include:

- Searching by SKU, product name, or manufacturer.
- Sorting by SKU, name, price, or stock.
- Filtering to show only valid products.
- Displaying validation and conflict indicators.
- Expanding product rows to inspect individual source records.
- Viewing detailed validation errors and conflicts.
- Loading, error, and empty-result states.
- Responsive layout using Bootstrap 5.

The frontend communicates with the backend through the `/products` endpoint.

In local development, Vite proxies API requests to Spring Boot. In Docker, Nginx provides the equivalent proxy configuration.

## 10. Running the Application

### Prerequisites

For local development:

- Java 21
- Node.js and npm

For containerized execution:

- Docker
- Docker Compose

### Option A: Docker Compose (Recommended)

From the project root:

```bash
docker compose up --build
```

The following services become available:

| Service | URL |
|---|---|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080/products |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |

The frontend is built using Vite and served through Nginx.

Nginx also proxies `/products` requests to the backend container.

Stop the application with:

```bash
docker compose down
```

### Option B: Local Development

Start the backend:

```bash
cd backend
./mvnw spring-boot:run
```

Start the frontend in a separate terminal:

```bash
cd frontend
npm install
npm run dev
```

The frontend development server is available at:

http://localhost:5173

The backend API is available at:

http://localhost:8080/products

Swagger UI is available at:

http://localhost:8080/swagger-ui/index.html

## 11. Testing

The backend uses JUnit 5 for automated testing.

The test suite covers the main processing components, including:

- Importing CSV and JSON records.
- Product normalization.
- Price conversion.
- Validation rules.
- Reconciliation and conflict detection.
- Integration between imported records and persisted product data.

Run the backend tests:

```bash
cd backend
./mvnw test
```

Build and lint the frontend:

```bash
cd frontend
npm run build
npm run lint
```

The REST API can also be tested interactively through Swagger UI.

## 12. Assumptions and Design Decisions

The following assumptions were made where the assignment did not specify exact business rules.

**No automatic conflict resolution.** Neither source is considered universally authoritative. Matching records are retained, and differences are reported.

**SKU-based matching.** Products are matched using normalized SKU values. EAN and manufacturer are not used as fallback identifiers.

**Default VAT.** A missing VAT rate is interpreted as 27%.

**Whole-HUF prices.** Gross prices are rounded to whole HUF.

**Missing timestamps.** CSV records have no update timestamp, so JSON records are not automatically considered newer.

**Validation versus conflicts.** Invalid values are validation errors. Differences between available source values are conflicts.

**Product validity.** A product is valid only when all associated source records are valid.

**Sorting.** When multiple records belong to one product, sorting by name, price, or stock uses the first source record.

**In-memory persistence.** H2 is used to keep the assessment self-contained. Source data is imported again at application startup.

## 13. Limitations and Possible Improvements

The implementation is intentionally scoped to the assessment.

Possible future improvements include:

- Row-level error handling for malformed input data.
- Configurable VAT rates and input locations.
- Pagination and database-level filtering for large datasets.
- Persisted reconciliation results to avoid recalculation on every request.
- Configurable conflict-resolution strategies.
- Additional matching rules using EAN or other identifiers.
- Structured API error responses.
- Extended integration and end-to-end tests.
- API versioning for future compatibility.

## 14. Summary

The solution demonstrates a practical approach to integrating incompatible data feeds while preserving transparency about data quality.

It separates data import, normalization, persistence, validation, reconciliation, and presentation into distinct responsibilities.

The REST API exposes the unified catalogue, Swagger UI provides interactive API documentation, and the React frontend allows users to explore the results.

Docker Compose makes the complete application reproducible and easy to run with a single command.