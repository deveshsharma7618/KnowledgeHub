# KnowledgeHub API

KnowledgeHub is a Spring Boot REST API for authenticated workspaces and document storage.

## Authentication

`POST /api/v1/auth/signup` and `POST /api/v1/auth/login` return an access token and a refresh token.

- Access tokens are signed JWTs with a 15-minute lifetime.
- The SHA-256 hash of every access token is stored in Redis with the same TTL as the JWT.
- Protected requests require `Authorization: Bearer <access-token>`.
- The JWT filter checks Redis before validating the JWT signature, subject, expiration, and token type.
- A missing Redis entry, malformed token, expired token, or invalid signature returns `401` with an `ErrorResponse`.
- Refresh tokens are stored in PostgreSQL as SHA-256 hashes.
- Signup and login revoke all previously active refresh tokens for the user before storing the newly issued refresh token.

## Error response

All handled API errors use the following JSON shape:

```json
{
  "title": "Invalid access token",
  "status": 401,
  "detail": "Access token is invalid or expired"
}
```

The same `ErrorResponse` schema is exposed for documented `400`, `401`, `409`, and `500` responses in Swagger UI.

## API documentation

Run the application and open:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Local development dependencies are PostgreSQL, Redis, and MinIO. Their connection settings are in `src/main/resources/application.yaml`.

## Testing

Run the test suite with:

```powershell
./mvnw.cmd test
```

The tests cover authentication and token issuance, JWT validation, Redis-backed access-token checks, service behavior, repository persistence, controller responses, and global error handling.
