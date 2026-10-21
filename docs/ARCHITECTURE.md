# System architecture

## Scope and entry points

This repository has three deployable code areas, not one unified server: an Android application module with two flavors, Firebase Cloud Functions, and an independent PHP/MySQL prototype. There is no iOS app, web build pipeline, media encoder or CDN deployment in this source tree.

`MainActivity.kt` hosts Compose and routes tabs using app state. `EtfcViewModel` constructs Room, `FirebaseSyncManager`, `AuthenticationRepository`, `EtfcRepository`, and the notification helper. Dependencies are wired manually, without a dependency-injection framework. The ViewModel exposes `StateFlow` state consumed by screens.

## Android responsibilities

| Area | Responsibility | Caveat |
| --- | --- | --- |
| `ui/screens` | Fan navigation, settings, onboarding, athlete registry and promoter panels | Large screens should be split as features mature |
| `ui/components` | Cards, player controls, chat, image picking, checkout and guards | UI access checks cannot protect server data |
| `data/repository/EtfcRepository` | Seed data, Room operations, payments/download simulations and JSON import/export | Large mixed-responsibility class |
| `AuthenticationRepository` | Firebase authentication and credential flows | Some account orchestration is also duplicated in the main repository |
| `FirebaseSyncManager` | Firestore listeners, mapping and cloud writes | Errors commonly fall back to local state |
| `UserDataManager` | Cloud profile and role operations | Needs full-UID migration and claims-based role handling |
| `data/remote/NetworkClient` | YouTube oEmbed and raw-content Retrofit clients | Release HTTP body logging was disabled during migration |
| `LocalImageStorageManager` | Copies selected images to app-internal storage | Local image URIs are not portable across devices |
| `DataConsistencyWorker` | Repairs fighter references in linked local content | Hourly periodic work is not a server synchronization guarantee |

## Local data

Room database `etfc_stream_database.db` is schema version 5. It declares 17 entities: downloads, PPV passes, predictions, favorites, fighter profiles, fighter statistics, fight record history, weight-class history, user accounts, chat messages, moderation logs, hero slides, previous videos, news articles, fighters, matches and announcements.

DAO flows drive screen state. Selected Firestore snapshots are mapped into local entities; seeded content fills empty datasets. This supports cached content browsing, not complete offline operation: Firebase authentication and YouTube playback still need network access.

`fallbackToDestructiveMigration()` can discard local data on an unsupported schema transition. Schema export is disabled. Export schemas, add explicit migrations, and decide which records are recoverable before a production database upgrade.

## Content data flows

1. A screen sends an action to the ViewModel.
2. The repository changes Room and, where implemented, requests a Firestore write.
3. Firestore snapshot listeners map remote content back to local/cache state.
4. Room/StateFlow emissions update Compose.

There is no conflict-resolution protocol or reliable outbox across local writes and cloud writes. A successful local change does not prove the cloud write succeeded. Add pending/error state and retry semantics before promising synchronized editing.

Public content import accepts a remote JSON document. Export schema `ETFC_CONTENT_SYNC_V1` includes hero slides, previous videos, news and fighters. It omits purchases, accounts, registrations and a complete relational match backup. Treat content imports as untrusted input; add schema limits, URL validation and error reporting before exposing arbitrary import URLs broadly.

## Authentication and authorization

Firebase Auth proves identity. The current Android code also has hard-coded owner email checks, prefixed/truncated user IDs, local role fields and legacy token/hash helpers. These are not proof of server authority. Local SHA-256 password hashes and generated `ETFC.*` tokens should be removed; use Firebase sessions and avoid storing passwords or derivative hashes.

The migration's server baseline requires an Admin SDK-issued `admin` custom claim. Firestore profiles use `/users/{fullFirebaseUid}`, owners can edit only profile fields, and privileged entitlements are server-only. Invite records are admin-only. `claimAdminRole` checks verified email and consumes an invite before issuing a claim. It needs expiry, rate limits, retry/recovery behavior and emulator coverage before production.

Bootstrap the first administrator from a trusted server, refresh the ID token after claim changes, and revoke claims when access is removed. Existing Android invitation methods do not yet implement this entire contract. Do not deploy these rules over existing data without the identity migration.

Athlete registrations and extra collections absent from the rules are denied by default. Define ownership, consent, retention and admin access before allowing those writes. Storage is also deny-all until a scoped upload/read policy exists.

## Video and commerce boundaries

YouTube playback uses a WebView embed. The separate arena player draws broadcast UI and exposes camera/quality state; no native HLS engine or verified multi-angle service is wired into it. `FLAG_SECURE` is a screen-capture deterrent, not end-to-end DRM.

Android PPV purchase and download operations simulate timing and progress. Their local records must not authorize a real paid stream. A production service must verify payment server-side and issue a short-lived entitlement or signed media URL. No such service exists here yet.

The PHP prototype has a separate MySQL pass schema and unauthenticated Firebase REST bridge. It does not form an end-to-end authenticated payment path with the Android app. Payment callbacks are disabled until signature checks, provider-side transaction verification, amount/currency checks, expiration, idempotency and replay protection are implemented.

## Operational boundaries

No production deployment occurs through CI. CI uses synthetic Android client configuration. Real configuration, signing keys, provider credentials and database passwords belong in environment/secret storage. Monitor cloud write failures explicitly; current fallback-to-local behavior can mask outages.

The match notification trigger checks `isLive`, while other match adapters use status-style fields. Reconcile this contract, wire FCM on Android, and verify token/topic behavior before treating notifications as complete.
