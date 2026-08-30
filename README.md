# ETFC Stream — Ethiopian Top Fight Championship Android Ecosystem

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Room](https://img.shields.io/badge/Database-Room%202.6.1-FF8F00.svg?style=flat&logo=sqlite)](https://developer.android.com/training/data-storage/room)
[![Retrofit](https://img.shields.io/badge/Networking-Retrofit%20%2B%20Moshi-009688.svg?style=flat)](https://square.github.io/retrofit/)
[![Coil](https://img.shields.io/badge/Image%20Loader-Coil%203.0-1E88E5.svg?style=flat)](https://coil-kt.github.io/coil/)

---

## 1. Overview

**ETFC Stream** is a broadcast-grade, high-concurrency live MMA streaming and fight-management Android application built for the **Ethiopian Top Fight Championship (ETFC)**.

Designed with a modern, high-contrast dark aesthetic and Material Design 3 (M3) architecture, the application delivers a premier digital fight night experience across East Africa and international combat sports audiences.

### Key Capabilities
- **Adaptive Bitrate (ABR) Live Video Player & In-App YouTube Engine**: Low-latency multi-bitrate live video playback with picture-in-picture (PiP), multi-angle camera switching, and native YouTube player integration with oEmbed dynamic metadata fetching.
- **Dynamic Featured Fight Poster Slider**: Horizontally scrolling carousel with edge-peeking cards, Coil image caching, championship belt indicators, and real-time live event countdowns.
- **Live Community Chat & Moderation Desk**: High-throughput live discussion stream featuring user mentions (`@user`), sentiment badges, real-time message filtering, and an Admin moderation suite (pin, mute, ban, slow mode).
- **Tale of the Tape & Fighter Meta-Engine**: Detailed fighter statistics, radar comparison charts, reach/height metrics, strike accuracy, and division rankings across 8 weight classes.
- **Ethiopian Mobile Payment Suite & PPV Ticketing**: Seamless integration with local payment rails (**Telebirr**, **M-Pesa**, **CBE Birr**, and **Chapa**) with cryptographic digital ticket QR codes and offline access pass verification.
- **Role-Based Access Control (RBAC) & Screen Security**: Granular user privilege enforcement (Free Fan, PPV Pass Holder, VIP Member, Ring Official, Super Admin) paired with dynamic screenshot and screen-recording prevention (`FLAG_SECURE`).
- **Offline Engine & Remote CMS Synchronization**: Offline-first caching powered by Room Database with real-time remote configuration updates from GitHub Raw JSON endpoints.

---

## 2. System Architecture

The project follows standard Android **Clean Architecture** and **MVVM (Model-View-ViewModel)** design patterns:

```
┌──────────────────────────────────────────────────────────────────────────┐
│                         Presentation Layer (UI)                         │
│   Jetpack Compose M3  •  Navigation Compose  •  Custom Canvas Overlays   │
│   (HomeScreen, LiveStreamScreen, FightCardScreen, FightersScreen, etc.)   │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │ StateFlow / Events
┌────────────────────────────────────▼─────────────────────────────────────┐
│                             ViewModel Layer                              │
│         EtfcViewModel (Single Source of State & Lifecycle Scope)         │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │ Suspend Functions / Flow Streams
┌────────────────────────────────────▼─────────────────────────────────────┐
│                            Repository Layer                              │
│                            EtfcRepository                                │
└───────────────────────┬───────────────────────────┬──────────────────────┘
                        │                           │
          ┌─────────────▼─────────────┐     ┌───────▼────────────────┐
          │     Local Data Layer      │     │   Remote Data Layer    │
          │   Room Database (v5)      │     │ Retrofit2 + Moshi      │
          │  • Fighters               │     │  • YouTube oEmbed API  │
          │  • Matches                │     │  • GitHub Raw Admin API│
          │  • Announcements          │     │  • OkHttp3 Interceptor │
          │  • Hero Slides            │     └────────────────────────┘
          │  • Video Vault            │
          │  • Chat & Moderation Logs │
          │  • User Accounts & RBAC   │
          └───────────────────────────┘
```

### Architectural Highlights
- **Single-Activity Architecture**: The application runs within `MainActivity.kt`, using `Scaffold`, `NavigationBar`, and animated Jetpack Compose screen composables.
- **Reactive State Flow**: The UI subscribes to immutable `StateFlow` streams from `EtfcViewModel`. State emissions automatically trigger optimized Compose recompositions.
- **Offline-First Persistence**: All fight data, fighter profiles, video metadata, and announcements are backed by Room SQLite tables, allowing full offline operation without a network connection.
- **Multi-Source Ingestion**: Network payloads from YouTube and GitHub Raw JSON endpoints are sanitized, parsed through Moshi adapters, mapped to domain entities, and persisted into Room.

---

## 3. Directory Structure

```
/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── EtfcDatabase.kt       # Room database definition (v5)
│   │   │   │   │   │   ├── EtfcEntities.kt       # Entity models for Room tables
│   │   │   │   │   │   └── EtfcDao.kt            # Coroutine & Flow DAO queries
│   │   │   │   │   ├── models/
│   │   │   │   │   │   ├── Models.kt             # Domain models (Bout, Fighter, WeightClass)
│   │   │   │   │   │   ├── HomeModels.kt         # HeroSlide, PreviousFightVideo, NewsArticle
│   │   │   │   │   │   ├── AuthModels.kt         # UserAccount, UserRole, PermissionMatrix
│   │   │   │   │   │   └── FighterMetaModels.kt  # Tale of the tape & radar chart models
│   │   │   │   │   ├── remote/
│   │   │   │   │   │   ├── NetworkClient.kt      # Retrofit & OkHttp singleton configuration
│   │   │   │   │   │   ├── YouTubeOEmbedApi.kt   # YouTube oEmbed REST contract & DTOs
│   │   │   │   │   │   ├── YouTubeDataMapper.kt  # URL extraction & thumbnail ladder generator
│   │   │   │   │   │   └── GitHubRawApiService.kt# GitHub Raw content fetcher
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   └── EtfcRepository.kt     # Unified data repository & sync logic
│   │   │   │   │   └── security/
│   │   │   │   │       └── SecurityManager.kt    # RBAC rules, cryptographic hashing, audit logs
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── FeaturedFightPosterSlider.kt # Coil-powered horizontal poster carousel
│   │   │   │   │   │   ├── HeroSection.kt        # Multi-tab hero banners & video vault cards
│   │   │   │   │   │   ├── VideoPlayerView.kt    # ABR video player with HUD controls
│   │   │   │   │   │   ├── YouTubeInAppPlayerView.kt # WebView-backed in-app YouTube player
│   │   │   │   │   │   ├── LiveChatComponents.kt # Real-time chat, mentions, moderation desk
│   │   │   │   │   │   ├── TaleOfTheTapeView.kt  # Head-to-head fighter stat comparison
│   │   │   │   │   │   ├── PaymentDialog.kt      # Telebirr, M-Pesa, CBE Birr payment checkout
│   │   │   │   │   │   ├── AdminPortalDialog.kt  # GitHub JSON sync & live content editor
│   │   │   │   │   │   ├── RbacPaywallOverlay.kt # Tier-based stream access restriction view
│   │   │   │   │   │   ├── SecureScreenEffect.kt # Dynamic FLAG_SECURE window enforcer
│   │   │   │   │   │   └── OfflinePlaybackDialog.kt # Offline video download & playback modal
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── HomeScreen.kt         # Featured matches, news, vault, countdown
│   │   │   │   │   │   ├── LiveStreamScreen.kt   # Live PPV broadcast, multi-camera, live chat
│   │   │   │   │   │   ├── FightCardScreen.kt    # Main card, prelims, voting predictions
│   │   │   │   │   │   ├── FightersScreen.kt     # Division rosters, search, profile details
│   │   │   │   │   │   ├── TicketsPpvScreen.kt   # Digital pass purchase, QR ticket wallet
│   │   │   │   │   │   ├── SettingsScreen.kt     # Profile, stream quality, audio commentary
│   │   │   │   │   │   └── AdminPanelScreen.kt   # Full-featured CMS: fighters, match cards, local image picker, and DB lifecycle
│   │   │   │   │   ├── theme/
│   │   │   │   │   │   ├── Color.kt              # ETFC Gold, Red Corner, Blue Corner, Obsidian
│   │   │   │   │   │   ├── Theme.kt              # Material 3 dark color scheme & typography
│   │   │   │   │   │   └── Type.kt               # Display and Body typography configurations
│   │   │   │   │   └── viewmodel/
│   │   │   │   │       └── EtfcViewModel.kt      # Central app ViewModel & reactive state
│   │   │   │   └── MainActivity.kt               # Main entry point, navigation host
│   │   │   ├── res/
│   │   │   │   ├── values/strings.xml            # Application string resources
│   │   │   │   └── mipmap-*/                     # Adaptive launcher icons
│   │   │   └── AndroidManifest.xml               # Permissions and activity registration
│   │   └── test/
│   │       └── java/com/example/ExampleRobolectricTest.kt # Robolectric unit & integration tests
│   └── build.gradle.kts                          # App-level dependencies and plugins
├── gradle/
│   └── libs.versions.toml                        # Centralized dependency catalog
├── settings.gradle.kts                           # Plugin repositories & project name
├── metadata.json                                 # AI Studio platform identification
└── README.md                                     # Project documentation
```

---

## 4. Prerequisites & Environment Setup

- **Android Studio**: Ladybug (2024.2.1+) or newer.
- **Java Development Kit (JDK)**: JDK 17 or JDK 21.
- **Android SDK Platform**: API Level 36 (Minimum SDK: 24 / Android 7.0 Nougat).
- **Build System**: Gradle 8.9+ (Kotlin DSL).

---

## 5. Compiling, Building, and Running

### 5.1 Build System Commands

> **Note**: In this environment, use `gradle` directly without `./gradlew`.

#### Check Compilation
```bash
gradle :app:compileDebugKotlin
```

#### Build Debug APK
```bash
gradle :app:assembleDebug
```
The output APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

#### Build Release APK
```bash
gradle :app:assembleRelease
```
The output APK will be located at:
`app/build/outputs/apk/release/app-release-unsigned.apk` (or signed if credentials are provided).

#### Run Unit & Robolectric Tests
```bash
gradle :app:testDebugUnitTest
```

#### Verify Roborazzi Screenshot Tests
```bash
gradle :app:verifyRoborazziDebug
```

---

## 6. Operating the App

### 6.1 User Roles & RBAC Switching
The application includes a built-in role switcher for rapid testing and demonstration of access levels:
1. Tap the **Profile / Role Badge** in the top bar.
2. Select any role to instantly preview the application under that persona:
   - **Free Fan**: Standard access to free preliminary bouts, basic chat, and public news.
   - **PPV Pass Holder**: Unlocks the 1080p Main Event PPV stream and exclusive camera angles.
   - **VIP Ringside Member**: Unlocks 4K ultra-low latency streaming, backstage locker room cameras, VIP gold chat badge, and digital fight night pass.
   - **Ring Official**: Grants access to live judge scorecard submissions and corner telemetry.
   - **Super Admin**: Unlocks the **Admin Portal** for real-time chat moderation and GitHub Remote JSON synchronization.

### 6.2 Mobile Payment & PPV Ticketing (Telebirr / M-Pesa)
1. Navigate to the **PPV Passes** tab.
2. Choose your ticket tier (*Early Bird*, *Main Card Standard*, *VIP Ringside*).
3. Select your preferred payment method: **Telebirr**, **M-Pesa**, **CBE Birr**, or **Chapa**.
4. Enter your mobile number and tap **Confirm & Pay**.
5. Once processed, an encrypted QR ticket is generated and stored in your local wallet for offline gate verification.

### 6.3 Admin CMS Portal & Full Database Lifecycle Management
The application features a dedicated **Admin Panel & Content Management System (`AdminPanelScreen.kt`)** providing full lifecycle CRUD management (Create, Read, Update, Delete) across all core combat entities:

1. **Accessing the CMS**:
   - Tap the **Settings** tab -> **Open Admin Portal**, or tap the top bar **Shield / Admin Badge** when logged in with the Admin persona.
2. **Athlete Roster Management (`🥊 Fighters` Tab)**:
   - **Local File Image Picker**: Upload high-resolution athlete profile photos directly from the device's local photo gallery or file storage using the Android system `ActivityResultContracts.GetContent()` picker (no external URLs required).
   - **Profile Customization**: Edit fighter full name, alias/nickname, nationality & flag emoji, weight division (Flyweight through Heavyweight), fighting stance (Orthodox/Southpaw/Switch), reach (inches), height (cm), comprehensive record (Wins, Losses, Draws, KOs, Submissions), signature techniques, and championship status.
   - **Cross-Screen Reactive Propagation**: Changes made to any fighter profile immediately and automatically synchronize across the entire application—including the **Home Screen Hero Slider**, **Fight Card Bout Lineups**, **Tale of the Tape Head-to-Head Compare Screen**, **Live Arena Stream HUD**, and **Fighter Roster Directory**—powered by reactive Room database flows.
3. **Fight Card & Bout Scheduling (`⚔️ Match Cards` Tab)**:
   - Schedule new bouts by selecting Red and Blue corner combatants directly from the dynamic fighter database.
   - Configure bout type (*Main Event*, *Co-Main Event*, *Featured Main Card*, *Prelims*), title championship stakes, scheduled rounds (3 or 5), arena venue, and broadcast time.
   - Upload custom fight night promotional poster images via local file picker.
   - Live result declaration: declare bout winners (Red Corner, Blue Corner, Draw/No Contest) and official stoppage method (*KO/TKO*, *Submission*, *Unanimous Decision*, *Split Decision*).
4. **Hero Banners, Archive Videos & Press Releases**:
   - Manage sliding hero banners on the Home Screen with custom promo artwork.
   - Ingest YouTube fight replays and highlight clips with automatic oEmbed metadata resolution.
   - Publish official news releases, press statements, and weigh-in announcements with local banner images.
5. **GitHub Repository Sync & JSON Engine**:
   - **Export Database to JSON**: Export the complete SQLite database state into a portable, version-controlled JSON schema.
   - **Import & Remote Ingestion**: Parse and upsert remote or local JSON payloads directly into Room tables with zero downtime.

---

## 7. Configuration & Remote JSON Schema

To update the app remotely via GitHub, host a JSON file structured according to the following schema:

```json
{
  "heroSlides": [
    {
      "id": "slide_adwa_002",
      "title": "ETFC 002: Battle of Adwa",
      "subtitle": "Historic Lightweight World Championship",
      "tag": "LIVE PPV",
      "badgeColorHex": 4025230404,
      "eventEdition": "ETFC 002",
      "eventDateOrCountdown": "October 24, 2026",
      "venueOrLocation": "Adwa 00 Museum Arena, Addis Ababa",
      "primaryCtaText": "Watch Live",
      "secondaryCtaText": "Order PPV",
      "posterImageUrl": "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=1200",
      "redFighterName": "Sedo",
      "blueFighterName": "Jhonny",
      "redFlag": "🇪🇹",
      "blueFlag": "🇪🇹",
      "hasChampionshipBelt": true,
      "weightClassText": "Lightweight (70 kg)"
    }
  ],
  "vaultVideos": [
    {
      "id": "vault_01",
      "title": "ETFC 001: Main Event Full Replay",
      "subtitle": "Sedo vs Desalegn - Spectacular KO Round 2",
      "eventName": "ETFC 001: Genesis",
      "category": "FULL_REPLAY",
      "durationText": "18:42",
      "youtubeVideoId": "0a8G81bZ7X0",
      "contentTier": "FREE_PRELIMS"
    }
  ],
  "newsArticles": [
    {
      "id": "news_adwa_tickets",
      "title": "Adwa 00 Museum Arena Confirmed for ETFC 002",
      "summary": "Over 8,000 live tickets will go on sale across Telebirr and CBE Birr.",
      "category": "PRESS_RELEASE",
      "author": "ETFC Media Desk",
      "publishedDate": "August 30, 2026",
      "isFeatured": true
    }
  ]
}
```

---

## 8. How to Extend and Implement New Features

### 8.1 Adding a New Remote API Endpoint
1. Open `app/src/main/java/com/example/data/remote/`.
2. Define your endpoint interface using Retrofit annotations:
   ```kotlin
   interface EtfcPublicApiService {
       @GET("api/v1/fighters/{id}/stats")
       suspend fun getFighterStats(@Path("id") fighterId: String): FighterStatsDto
   }
   ```
3. Register the service in `NetworkClient.kt`:
   ```kotlin
   val publicApiService: EtfcPublicApiService by lazy {
       retrofit.create(EtfcPublicApiService::class.java)
   }
   ```
4. Expose the call through `EtfcRepository.kt` and handle offline fallback caching with Room.

### 8.2 Adding a New Room Table / Entity
1. Define the `@Entity` class in `EtfcEntities.kt`:
   ```kotlin
   @Entity(tableName = "event_sponsors")
   data class SponsorEntity(
       @PrimaryKey val id: String,
       val name: String,
       val logoUrl: String,
       val tier: String
   )
   ```
2. Add the corresponding query methods in `EtfcDao.kt`:
   ```kotlin
   @Query("SELECT * FROM event_sponsors ORDER BY tier ASC")
   fun getAllSponsors(): Flow<List<SponsorEntity>>

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertSponsors(sponsors: List<SponsorEntity>)
   ```
3. Register the entity and bump the version number in `EtfcDatabase.kt`:
   ```kotlin
   @Database(
       entities = [..., SponsorEntity::class],
       version = 6,
       exportSchema = false
   )
   abstract class EtfcDatabase : RoomDatabase()
   ```

### 8.3 Creating a New Screen in Jetpack Compose
1. Create your composable screen file in `app/src/main/java/com/example/ui/screens/`.
2. Collect state from `EtfcViewModel`:
   ```kotlin
   @Composable
   fun SponsorRosterScreen(
       viewModel: EtfcViewModel,
       modifier: Modifier = Modifier
   ) {
       val uiState by viewModel.uiState.collectAsStateWithLifecycle()
       // Implement UI layout with Material 3 components
   }
   ```
3. Add the screen to the navigation destination hierarchy in `MainActivity.kt`.

---

## 9. Security & DRM Implementation

- **`FLAG_SECURE` Protection**: Controlled dynamically via `SecureScreenEffect.kt`. When high-value PPV content is playing or confidential payment credentials are displayed, the window flag `WindowManager.LayoutParams.FLAG_SECURE` is applied to prevent screen capture, screenshotting, or mirroring.
- **Cryptographic Access Pass Verification**: PPV passes and VIP tickets generate encrypted payload hashes using SHA-256 tokens linked to the user account ID and match ID.
- **Input Sanitization**: Real-time chat messages and URLs undergo strict regex validation to prevent script injection and malicious link distribution.

---

## 10. Tech Stack & Dependencies

| Category | Technology |
|---|---|
| **Language** | Kotlin 2.0.0 |
| **UI Framework** | Jetpack Compose (Material Design 3) |
| **Architecture** | MVVM + Clean Architecture |
| **Local Database** | Room SQLite 2.6.1 + KSP |
| **Networking** | Retrofit 2.11.0 + OkHttp 4.12.0 |
| **JSON Serialization**| Moshi 1.15.2 (Kotlin Reflect Factory) |
| **Image Loading** | Coil 3.0.0 (Compose AsyncImage) |
| **Concurrency** | Kotlin Coroutines & Reactive StateFlow |
| **Testing** | JUnit 4, Robolectric, Roborazzi Screenshot Suite |

---

## 11. License

Copyright © 2026 Ethiopian Top Fight Championship (ETFC). All rights reserved.
Licensed under the proprietary ETFC Digital Media Distribution license.
