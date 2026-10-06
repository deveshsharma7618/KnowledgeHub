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
- Each signup or login creates an independent refresh-token session, so the same account can remain signed in on multiple devices.

`POST /api/v1/auth/refresh` rotates a refresh token. Send the refresh token in the JSON body
(`{"refreshToken":"..."}`) and the current access token in the `Authorization: Bearer ...` header.
The presented refresh token and access token are revoked before a new token pair is returned.

`POST /api/v1/auth/logout` accepts the same body and header and returns `204 No Content` after
revoking both tokens. Refresh-token records are stored as hashes and are consumed atomically to
prevent concurrent refresh-token reuse.

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

## Protected CRUD APIs

After authentication, the workspace API supports `POST`, `GET`, `PUT`, and `DELETE` at
`/api/v1/workspaces`. Workspace and document reads/writes are owner-scoped. Documents are uploaded at
`POST /api/v1/workspaces/{workspaceId}/documents` and can be listed, retrieved, and deleted under that
workspace. User profile CRUD is separate from authentication at
`GET`, `PUT`, and `DELETE /api/v1/users/{userId}` and is restricted to the authenticated user.

Uploaded filenames are treated as display metadata only. MinIO objects use server-generated UUID-based
keys, and upload metadata plus storage cleanup run inside transactional service workflows.

## Testing

Run the test suite with:

```powershell
./mvnw.cmd test
```

The tests cover authentication and token issuance, JWT validation, Redis-backed access-token checks, service behavior, repository persistence, controller responses, and global error handling.
