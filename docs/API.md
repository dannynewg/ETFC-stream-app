# API and data contracts

This documents the checked-in prototype, not a guaranteed production API. PHP routes do not consistently enforce HTTP methods, request schemas, authentication or safe error redaction. See the review before deploying.

## PHP endpoints

Paths are relative to the configured backend origin. JSON is the primary body format; some endpoints fall back to form fields.

| Intended method / path | Input | Result |
| --- | --- | --- |
| `GET /api/events/list.php` | None | `success`, Unix timestamp and `data` containing `events`, joined `bouts`, `stream`, `ppv_price_etb`, `currency` |
| `GET /api/stream/status.php` | None | `is_live`, `stream_url`, fallback YouTube ID, active bout, round, clock, slow mode, announcement, viewer count, health, server time |
| `POST /api/ppv/initiate.php` | `phone_number` (or `phone`), optional `email`, `provider`, `event_code` | Inserts `PENDING` MySQL pass; returns order reference, pass token, price, provider and demonstration checkout instructions |
| `POST /api/ppv/verify_pass.php` | Any of `phone_number`/`phone`, `pass_token`, `email`; optional `event_code` | Lookup of a `PAID` row with pass details; current implementation is not a secure entitlement verifier |
| `POST /api/webhooks/chapa.php` | Provider callback body | HTTP 503, `success: false`; disabled |
| `POST /api/webhooks/telebirr.php` | Provider callback body | HTTP 503, `success: false`; disabled |

Initiation defaults to provider `TELEBIRR` and event `ETFC_003`. Recognized provider labels are `TELEBIRR`, `CHAPA`, `CBE_BIRR`, and `MPESA`; labels do not imply working gateway integrations. A blank phone produces 400, unknown event 404, and database failures 500. PPV price is read from the event with a 600 ETB fallback.

Example for a future local backend test (requires database configuration):

```json
{
  "phone_number": "+251900000000",
  "email": "fan@example.com",
  "provider": "CHAPA",
  "event_code": "ETFC_003"
}
```

Do not use real customer data while testing. Pass verification currently permits phone/email-based discovery, does not enforce expiry in the query, and reuses named placeholders under native PDO prepares. Stream URLs are not access-controlled. These are tracked release blockers.

## Firebase functions

| Export | Trigger | Contract |
| --- | --- | --- |
| `verifyPaymentWebhook` | HTTPS | Always HTTP 503; no pass mutations |
| `onMatchStatusChanged` | Firestore update of `matches/{matchId}` | Sends FCM topic `live_events` when `isLive` changes from false to true; optional `matchTitle` is used in title |
| `claimAdminRole` | Callable HTTPS | Verified authenticated email plus nonempty `inviteCode`; consumes `admin_invites/{email}` if `code` matches, writes admin metadata, sets `admin: true` custom claim |

Callers of `claimAdminRole` must refresh their Firebase ID token. The client does not yet complete this claim-based invitation contract. Admin invite revocation must also revoke custom claims; deleting an invitation alone does not revoke an already-issued claim. Match adapters need a shared status schema before notification delivery can be considered complete.

## Firestore collections

| Collection | Source usage | Current rule policy |
| --- | --- | --- |
| `fighters`, `matches` | Fight card and athlete content | Public read, admin-claim write |
| `hero_slides`, `previous_fight_videos`, `news_announcements` | Home/CMS content | Public read, admin-claim write |
| `users` | Profiles and role metadata | Full-UID owner/admin read; owner edits limited profile fields |
| `admin_invites` | Team invitation records | Admin-claim read/write; trusted callable consumes invite |
| `arena_chat` | Chat snapshots and writes | Public read; authenticated sender UID on creation; admin update, owner/admin delete |
| `ppv_passes` | Intended entitlement records | Full-UID owner/admin read; server SDK writes only |
| `announcements`, `athlete_registrations` | Additional Android adapter collections | Denied by default pending scoped policies |

The client uses additional/legacy identity and role shapes; the table describes the checked-in restrictive baseline. Chat creation validates sender UID only, not a full message schema or rate limits. Privacy-sensitive data must never go in public content collections.

## Content import/export

`EtfcRepository.exportToGitHubJson()` exports:

```json
{
  "version": 1,
  "schema": "ETFC_CONTENT_SYNC_V1",
  "exportedAt": 0,
  "heroSlides": [],
  "previousVideos": [],
  "newsArticles": [],
  "fighters": []
}
```

Array objects follow the mappings in `EtfcRepository.kt`. This example shows top-level shape, not a full validation schema. Match import helpers and alternative payload handling exist; a stable versioned JSON Schema and round-trip contract tests remain needed. No private accounts, payment logs or application records should be exported to a public GitHub content URL.
