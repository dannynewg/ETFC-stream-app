# Backend setup and deployment boundaries

The Firebase and PHP services are separate development components. Publishing this repository does not deploy them. Do not expose the PHP prototype to real customers until the release blockers in [ROADMAP.md](ROADMAP.md) are addressed.

## PHP environment

Use PHP 8.4 with PDO MySQL and cURL. Create a development MySQL database and import `cpanel_backend/database/schema.sql` into that selected database. The schema creates eight tables: `users`, `events`, `fighters`, `matches`, `ppv_passes`, `live_stream_control`, `live_chat_messages`, and `payment_webhooks_log`. It includes sample event/admin metadata; inspect seed rows before importing into any existing database.

Supply environment variables from `cpanel_backend/.env.example` through hosting configuration or your process environment. Credentials are not embedded in `config.php`. The example file is documentation, not a bundled dotenv implementation.

Required for database operations: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASS`. Set `BASE_URL` to the externally visible origin and `APP_ENV=production` when ready to deploy. Configure a specific `CORS_ALLOWED_ORIGIN` for a browser frontend; native Android HTTP clients do not rely on CORS.

Set `SUPERADMIN_EMAIL` and a PHP `password_hash` result in `ADMIN_PASSWORD_HASH` to enable web-admin login. There is no default password. Store the generated hash in the server environment and never commit it. Configure HTTPS, secure session cookies, CSRF protection and login throttling before enabling this portal publicly. Only session-ID regeneration was added in migration; these other protections remain pending.

## URL layout

The source has `public_html/index.php` one level below `config/`, `api/`, and `admin/`. The portal uses absolute `/api/...` and `/admin/...` URLs, so simply choosing `public_html/` as the document root does not expose those endpoints. A host must map these paths explicitly while denying HTTP access to config, SQL, environment files and source metadata.

For isolated local development, a reverse proxy can map:

| Request | Source |
| --- | --- |
| `/` | `cpanel_backend/public_html/index.php` |
| `/admin/index.php` | `cpanel_backend/admin/index.php` |
| `/api/...` | `cpanel_backend/api/...` |

Keep the source layout intact because PHP `require_once` paths depend on it. No production Apache/Nginx configuration is supplied. Shared hosts differ in rewrite and environment support; the old hosting guide's claim that a flat upload would work was removed. Do not expose the repository root as an unrestricted document root.

## Payments

Both PHP payment callbacks and the Firebase `verifyPaymentWebhook` function return HTTP 503. They must remain disabled until implementation includes provider signature verification, provider-side transaction lookup, expected merchant/order/amount/currency checks, idempotent transactions, replay handling, and audit redaction.

The PHP initiation endpoint inserts a pending order and constructs demonstration checkout information. It does not call a provider's order-creation API. Android `processPpvPayment` independently simulates a local purchase. A real integration must unify these flows around authenticated Firebase identity and server-authoritative entitlements.

The existing pass lookup accepts phone/email/token and has expiry, ownership and PDO placeholder issues described in the review. Do not use it to protect paid media. Stream status currently exposes a stream URL without entitlement enforcement.

## Firebase

Use a dedicated development project and explicitly select its project ID with the Firebase CLI. Build functions with `npm ci`, `npm run lint`, `npm run build` inside `functions/`.

Before deploying rules, migrate Android user document IDs to complete Firebase UIDs and replace email-based roles with server-issued claims. Bootstrap the first admin using the Firebase Admin SDK in a trusted server environment. Invite-based elevation requires a verified email, an admin-created invite containing a `code`, and an ID-token refresh after elevation. Add expiration, revocation and recovery tests before production.

`storage.rules` denies all access. Add narrowly scoped authenticated upload policies and media validation before using Storage. `firestore.indexes.json` intentionally begins empty; it does not prove that all application queries have indexes. Realtime Database rules are not provided; do not enable public RTDB writes to make the PHP bridge work.

The PHP bridge currently sends REST writes without a bearer token or service-account OAuth flow. An API key alone is not server authorization. Its pass documents use sanitized phone numbers, while the Android/Firestore model needs authenticated UIDs. Implement an authenticated server bridge and agreed schema before relying on sync.

## Release gates

Complete the security, identity, payment, streaming and data-migration tasks in the roadmap; test using provider sandboxes and Firebase emulators; verify access with both allowed and forbidden users; confirm media rights and data retention; then perform an explicit deployment review. No CI workflow currently deploys functions, rules, PHP or Android releases.
