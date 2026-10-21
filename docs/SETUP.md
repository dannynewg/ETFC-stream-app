# Development setup

## Android

1. Install Android Studio with JDK 21, SDK platform 36.1 and Build Tools 36.0.0.
2. Open the repository root. Android Studio should create ignored `local.properties` with the SDK path. `JAVA_HOME` must identify the JDK root for command-line Gradle.
3. Use a separate Firebase development project. Register both Android application IDs listed in the README, enable email/password and Google providers as needed, and add the local debug certificate fingerprints.
4. Download `google-services.json` to `app/`. For build checks only, copy the synthetic `google-services.example.json` to that path. Synthetic configuration cannot support authentication or cloud data.
5. Select `userDebug` or `adminDebug`. Gradle generates the usual debug signing key. Old local debug keys are ignored and no longer required by the build.

The checked-in wrapper is Gradle 9.6.0, matching the existing project's wrapper configuration. The distribution has a pinned SHA-256 checksum. Dependency/plugin versions are retained from the imported project; Android CI is the reproducibility gate and may expose compatibility issues that require separate version alignment.

```powershell
.\gradlew.bat --version
.\gradlew.bat :app:assembleUserDebug :app:assembleAdminDebug
.\gradlew.bat :app:testUserDebugUnitTest :app:testAdminDebugUnitTest
.\gradlew.bat :app:lintUserDebug :app:lintAdminDebug
.\gradlew.bat :app:connectedUserDebugAndroidTest
```

On macOS/Linux use `./gradlew` with the same tasks. Instrumentation requires a device or emulator. The retained Robolectric tests verify role behavior and flavor-specific app labels; they do not validate real payment or stream security.

## Release signing

Supply `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD` in the release environment; the Gradle alias is `upload`. Back up the release key securely outside Git. Build with `:app:bundleUserRelease` or `:app:bundleAdminRelease` only after completing release gates. Debug signing fingerprints will differ across machines and must be registered for Google sign-in when relevant.

Never commit APKs, bundles, signing material, or private service accounts. The public repository provides source, not a signed store release.

## Firebase functions

Use Node 20 to match `functions/package.json` and a Firebase CLI version compatible with your project. Runtime support must be checked before deployment.

```powershell
Set-Location functions
npm ci
npm run lint
npm run build
Set-Location ..
firebase emulators:start --only functions,firestore
```

Select a development Firebase project explicitly. No `.firebaserc` with a production target is committed. `functions/lib` and `node_modules` are generated. Keep the lockfile committed. The `serve`, `shell`, `deploy`, and `logs` npm scripts expect the Firebase CLI on PATH.

The restrictive rules baseline and current Android identity mapping are not fully compatible. Complete roadmap SEC-02 in emulators before applying rules to an existing database. `firestore.indexes.json` starts empty; record required indexes after representative queries are validated. Storage denies all reads/writes by default.

## PHP

Use PHP 8.4 for parity with CI and enable PDO MySQL and cURL for database/cloud operations. Configure process environment values from `cpanel_backend/.env.example`; PHP does not automatically read `.env` files. See [backend instructions](BACKEND.md) for URL layout and hosting constraints.

```powershell
Get-ChildItem cpanel_backend -Filter '*.php' -Recurse -File | ForEach-Object { php -l $_.FullName }
php tests/php/security_smoke.php
```

These checks do not require a live database or payment provider.

## Troubleshooting

| Symptom | Check |
| --- | --- |
| `JAVA_HOME` / Java not found | Install JDK 21 and select it in Android Studio and your terminal |
| SDK platform missing | Install platform 36.1 and configure the SDK location |
| No matching Firebase client | Use JSON containing both application IDs, or the supplied synthetic example for builds |
| Google sign-in fails | Provider enabled, correct OAuth setup and local debug signing fingerprints |
| Permission-denied cloud writes | Full Firebase UID mapping, current ID token/custom claim and collection rules; do not open rules globally |
| Local content changes but other devices do not | Check cloud write errors and network; local success is not cloud success |
| PHP DB connection failure | Environment values, PDO MySQL, host reachability and schema import |
| Webhook returns 503 | Expected until authenticated payment verification is implemented |
| Gradle dependency/plugin resolution failure | Compare JDK, wrapper, AGP, Kotlin and KSP versions; record the exact resolution failure before changing versions |
