# Progress and release roadmap

Baseline: **21 October 2026**. Checked items are completed source/migration work; unchecked items need implementation or verification. No completion percentage is claimed. GitHub issue templates provide a consistent way to turn each task into an assigned, reviewable issue.

## Migration

- [x] MIG-01 — Inspect the source tree and distinguish working code from simulations.
- [x] MIG-02 — Remove build/cache/editor state, obsolete patch scripts, duplicate source posters and generic demo tests.
- [x] MIG-03 — Add extensive README, architecture, setup, backend/API, review and security documentation.
- [x] MIG-04 — Restore the complete Gradle wrapper and pin the distribution checksum.
- [x] MIG-05 — Exclude local Firebase configuration, signing keys, environment values and generated dependencies.
- [x] MIG-06 — Add CI plus issue/PR templates and a maintained progress checklist.
- [ ] MIG-07 — Confirm first Android CI build/lint/unit-test run passes for both flavors. Acceptance: exact run URL and successful task results recorded in REVIEW.md.

## Security and identity — release blocking

- [x] SEC-01 — Remove embedded PHP database/signing secrets and demo web-admin passwords; require environment configuration and password hashes. Disable unverified payment callbacks; stop release HTTP payload logging.
- [ ] SEC-02 — Unify accounts around full Firebase UIDs and server admin claims. Acceptance: fan cannot query other profiles, change roles or grant entitlements; admin operations succeed; token refresh and revocation work; no owner-email bypass; emulator allow/deny tests pass.
- [ ] SEC-03 — Remove local password hashing and custom session-token generation. Acceptance: only Firebase-issued credentials authorize requests and no password derivative is stored locally.
- [ ] SEC-04 — Define athlete registration, announcements, chat and media policies. Acceptance: ownership, consent, retention, field validation and abusive request limits are tested.
- [ ] SEC-05 — Harden the web admin. Acceptance: CSRF protection, secure session cookies, HTTPS, login throttling and audit redaction verified.
- [ ] SEC-06 — Rotate historical database, webhook and signing secrets in the original hosting environment. Acceptance: old values no longer authorize access. Repository cleanup cannot rotate remote credentials.
- [ ] SEC-07 — Complete admin invite lifecycle. Acceptance: expiration, claim revocation, concurrent redemption and recovery from partial failure are tested.

## Payments and entitlements — release blocking

- [ ] PAY-01 — Select provider contracts and implement real sandbox checkout creation. Acceptance: provider returns valid session tied to authenticated UID, amount, currency and event.
- [ ] PAY-02 — Replace disabled callbacks with authenticated verification. Acceptance: forged, missing-signature, wrong-amount, replayed and duplicate events cannot grant access; provider verification and atomic idempotency tested.
- [ ] PAY-03 — Replace local simulated purchase grants with server entitlements. Acceptance: Android cannot unlock a paid stream by changing local data; expiry/refunds/revocation enforced.
- [ ] PAY-04 — Repair pass lookup and identity mapping. Acceptance: full-UID ownership, expiry, unique native-PDO parameters and privacy controls tested; phone/email cannot disclose bearer tokens.

## Playback and downloads

- [ ] VID-01 — Wire a real HLS playback engine and stream service. Acceptance: real source, buffering/recovery, background lifecycle, measured quality switching and device playback verified.
- [ ] VID-02 — Replace simulated network/stats/camera data. Acceptance: measurements and controls correspond to actual services; unavailable controls are clearly hidden or labeled.
- [ ] VID-03 — Implement real offline replay downloads and rights controls. Acceptance: valid local files, interrupted-download recovery, storage limits, expiry and playback tests.
- [ ] VID-04 — Protect paid stream URLs. Acceptance: unauthorized viewer cannot obtain a usable paid-media URL; signed URL expiry is enforced server-side.

## Data and maintainability

- [ ] DATA-01 — Export Room schemas and implement migrations. Acceptance: user data survives supported upgrades with migration tests; no destructive fallback in release.
- [ ] DATA-02 — Add cloud outbox/conflict handling. Acceptance: failed writes show pending/error state and retry safely; local success cannot masquerade as cloud completion.
- [ ] DATA-03 — Authenticate the PHP/Firebase bridge and unify pass schema. Acceptance: end-to-end sandbox order creates exactly one UID-owned entitlement; no open Firebase rules.
- [ ] DATA-04 — Version content JSON with validation and round-trip tests; use portable media URLs.
- [ ] DATA-05 — Split the large repository, ViewModel and admin screen into feature responsibilities; consolidate duplicated authentication code.
- [ ] DATA-06 — Align FCM trigger fields with Android match models and implement Android subscription/receipt tests.

## Product and release verification

- [ ] REL-01 — Verify both flavors on physical devices and emulators, API 24 through current target, offline/reconnect and rotation.
- [ ] REL-02 — Accessibility and localization pass: TalkBack, contrast, text scaling, translations and empty/error states.
- [ ] REL-03 — Verify event dates, venues, prices, fighter records and rights to distribute all images/media.
- [ ] REL-04 — Choose an appropriate source license and publish privacy, data retention, refund and support policies.
- [ ] REL-05 — Configure reproducible signing, store metadata, backups, monitoring and release rollback.
- [ ] REL-06 — Run payment sandbox, emulator authorization and streaming/load acceptance tests before enabling paid access.

## Updating progress

Create one issue per scoped task, reference its ID, add an owner and acceptance criteria, and link implementation PRs. Move a checkbox only after the stated evidence exists. Record completed user-visible changes in CHANGELOG.md and refresh implementation status when a simulation becomes a verified integration.
