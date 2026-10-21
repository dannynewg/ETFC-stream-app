# Migration review and verification

Reviewed **21 October 2026**. This is a source and migration review, not a production security certification, payment integration test or broadcast capacity assessment.

## Findings addressed

| Finding | Change |
| --- | --- |
| PHP configuration and deployment guide contained embedded database/signing/webhook values | Removed values; environment-only configuration and sanitized setup documentation |
| Web admin accepted built-in demo passwords | Removed both passcodes; environment-provided password hash, no default login, session regeneration |
| Payment callbacks granted passes from unverified request fields | Replaced PHP and Firebase callbacks with explicit HTTP 503 responses; real integration remains pending |
| Original Firestore rules exposed profiles/invites and allowed unsafe self-writes | Added restrictive owner/profile-field/admin-claim baseline; explicitly documented required client identity migration |
| Firebase config referenced missing Storage/index files | Added deny-all Storage rules and explicit empty Firestore index baseline |
| Build required local debug keystores absent on a new clone | Standard tooling-generated debug signing; release credentials still external |
| Wrapper scripts/JAR were absent | Restored official Gradle 9.6.0 files and pinned distribution SHA-256 |
| HTTP payload logging enabled unconditionally | BASIC in debug, NONE in release |
| README claimed complete paid streaming, encrypted offline downloads and high concurrency | Replaced with implementation status grounded in source |
| Root contained generated state, one-off patch scripts and duplicate source posters | Removed derived/obsolete files; retained runtime assets and required resource aliases |
| Firebase package scripts lacked ESLint configuration | Added TypeScript ESLint configuration, removed unused direct Axios/Stripe dependencies and updated lockfile |

No historical Git repository existed in this local directory. An existing private `dannynewg/ETFC-stream-app` repository was found on GitHub. At the owner's instruction, its current file tree is replaced with this cleaned project and it remains private. Existing history is preserved and still contains historical credentials; rotate those values and review/sanitize history before any future public-visibility change.

## Remaining findings

| Priority / task | Evidence and consequence | Required follow-up |
| --- | --- | --- |
| Release blocker / SEC-02 | Android roles rely on owner-email checks, mutable local metadata and shortened/prefixed IDs. New rules require full Firebase UIDs and claims. | Migrate identity and authorization end to end; emulator allow/deny tests before deploying rules |
| Release blocker / PAY-01–03 | `processPpvPayment` delays then writes a local pass; PHP initiation fabricates checkout information rather than calling provider APIs. | Real sandbox checkout, verified server transaction state and server-owned entitlements |
| Release blocker / PAY-04 | PHP pass lookup accepts phone/email and returns bearer tokens; expiry is not filtered, and named parameters repeat despite native PDO prepares. | Authenticated ownership, expiry checks, unique parameters and privacy tests |
| Release blocker / VID-01, VID-04 | Arena player is Compose broadcast UI with simulated state; public stream status returns a media URL without entitlement checks. | Real HLS service/player plus server-authorized media access |
| High / SEC-03 | Legacy password SHA-256 helpers and locally generated session strings remain. | Stop persisting password derivatives; use Firebase ID tokens |
| High / SEC-04 | Athlete registrations/extra announcement collections lack scoped policies; user/chat payloads need field validation and rate limits. | Design consent, ownership, retention, moderation and upload rules |
| High / SEC-05 | PHP portal still lacks complete CSRF, login throttling and secure-cookie setup. | Harden and test before public deployment |
| High / SEC-06 | Previously embedded values could still authorize the original hosting environment. | Owner rotates them at each service; no remote credentials were changed by migration |
| High / SEC-07 | Admin invitation function consumes an invite and then sets claims in a separate operation; expiry/revocation/recovery are incomplete. | Test failure recovery and claim lifecycle; do not rely only on deleting invites |
| High / DATA-01 | Room uses destructive migration fallback with schema export disabled. | Versioned migrations and preservation tests |
| High / DATA-03 | PHP Firebase bridge lacks authenticated REST credentials and uses phone-derived document IDs. | Trusted server OAuth/Admin SDK bridge with consistent UID schema |
| Medium / VID-03 | Download progress and encrypted file paths are simulated. | Real file transfer, durable storage, lifecycle and offline playback validation |
| Medium / DATA-02 | Cloud write failures often retain local state without robust synchronization confirmation. | Outbox/retry/conflict handling and visible errors |
| Medium / DATA-06 | Notification trigger expects `isLive`; other source mappings use status fields; Android FCM receipt/topic setup is incomplete. | One event schema and a device notification test |
| Medium / REL-03 | Seeded content includes inconsistent event dates, venues, prices and synthetic statistics. | Editorial verification and media rights review |
| Medium / DATA-05 | Large UI/repository/ViewModel files mix concerns and duplicate auth orchestration. | Incremental feature extraction with behavior tests |

## Cleanup decisions

- Removed `.gradle`, `.kotlin`, root/app `build` directories: approximately **684 MB** of generated cache/build files at inspection.
- Removed `.idea`, `.vscode`, `.antigravity`, AI Studio metadata and an otherwise empty asset metadata folder.
- Removed all 11 original poster files only after verifying each SHA-256 hash matched a bundled drawable. Their originals occupied approximately 3 MB.
- Kept bundled JPEGs, icons and spelling aliases: image names are resolved dynamically by reflection and imported content, so unused-looking resources cannot be deleted safely from static reference counts alone.
- Removed obsolete `patch_*`, `fix_*`, `revert_avatar`, custom quote/bracket-check scripts and one-time image copy/cropping scripts.
- Removed the generic addition test and generic greeting screenshot test/baseline. Kept role/resource behavior and instrumentation package checks; fixed the app-label assertion for the admin flavor.
- Retained `.env`, `local.properties`, real Android Firebase configuration and existing local debug signing keys **only as ignored local files**. They are excluded from the public Git tree.
- Did not select a source license, delete signing keys, deploy rules/backends, enable payment providers, publish an APK or alter remote hosting data.

## Validation evidence

| Check | Result |
| --- | --- |
| PHP syntax (`php -l` for all 11 backend PHP files) | Passed on local PHP 8.4 |
| PHP callback/config security smoke (`php tests/php/security_smoke.php`) | Passed: both callbacks refuse grants with 503; no default admin/database credentials |
| Source hygiene, JSON parsing and local Markdown links (`node tools/check-repository.mjs`) | Passed: 178 publication files, 6 JSON files and 24 local documentation links; no configured credential patterns or forbidden files |
| Android XML parsing | Passed: all 13 source XML files parse |
| Staged diff whitespace check (`git diff --cached --check`) | Passed after normalization |
| Firebase functions dependency install, ESLint and TypeScript build | Passed locally with the lockfile; Node 24 host emitted the expected Node 20 engine warning |
| Gradle wrapper | Official wrapper retrieved; distribution checksum pinned; executable bit recorded for Unix launcher |
| Android build, lint, unit/device tests | Not run locally: Java/JDK and Android SDK unavailable; CI configured as follow-up gate |
| Firestore rules emulator authorization tests | Not run; required before deploying the restrictive baseline |
| Payment sandbox, MySQL integration and actual stream/download tests | Not run; integrations remain prototype/disabled |

Node 24 is installed locally while functions declare Node 20; CI uses the declared Node 20 runtime. A successful source check does not establish production readiness.

## GitHub verification checkpoint

The cleaned 178-file tree was uploaded to the existing private repository in commit `ad54181`. GitHub's source tree matched the local Git tree exactly. The old metadata, asset scratch folder, generic screenshot test and credential-bearing setup guide are absent from the current tree; private history was preserved at the owner's instruction.

The first CI run passed source hygiene, PHP and Firebase functions checks, but Android setup failed while requesting the retired SDK `tools` package. Commit `00fe045` changes setup to install `platform-tools`. In [CI run 2](https://github.com/dannynewg/ETFC-stream-app/actions/runs/39662399139), source, PHP and functions checks passed again. Android reached Kotlin compilation for both flavors and was still running at this checkpoint; MIG-07 remains open until its build, unit tests and lint finish successfully.

The local repository tracks `origin/main` as a shallow checkout of the migrated source. Historical credential-bearing remote commits were not downloaded into this checkout. Temporary npm dependencies, generated functions output and the migration npm cache were removed after validation.
