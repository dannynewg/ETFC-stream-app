# ⚔️ ETFC STREAM: cPanel Free Hosting & Firebase Relational DB Setup Guide

This guide provides step-by-step instructions for deploying the **ETFC PHP/MySQL Backend & Web Portal** onto **cPanel Free Hosting** (e.g. InfinityFree, ProFreeHost, 000webhost, or any standard cPanel web host) and connecting it with the **Firebase Project** and **Android Jetpack Compose App**.

---

## 📂 Architecture Overview

```
+-----------------------------------------------------------------------------------+
|                            ETFC UNIFIED ARCHITECTURE                              |
+-----------------------------------------------------------------------------------+
|                                                                                   |
|  [cPanel / InfinityFree Web Host]   [Firebase Cloud Engine]                       |
|  etfcstream.rf.gd                   - Cloud Firestore (Users, Passes, Bouts)      |
|  - Web Landing & Countdown          - Realtime DB (/live_arena/status)            |
|  - Webhook Relays (Telebirr/Chapa)  - Firebase Auth (Phone OTP & Email)           |
|  - MySQL Relational DB (schema.sql)                                               |
|  - Event Control Admin Portal                                                     |
|             \                                    /                                |
|              \                                  /                                 |
|               v                                v                                  |
|         +---------------------------------------------+                           |
|         |    Android Application (Jetpack Compose)   |                           |
|         |    - Fixtures (ETFC 003 in 3 months)        |                           |
|         |    - Single HLS Player Stream Engine        |                           |
|         |    - 600 ETB PPV Instant Verification       |                           |
|         +---------------------------------------------+                           |
+-----------------------------------------------------------------------------------+
```

---

## 🚀 Step 1: Set Up Free cPanel / InfinityFree Web Hosting

1. **InfinityFree Hosting Account**:
   - Host: **InfinityFree** (`sql208.infinityfree.com`)
2. **Assigned Domain**:
   - Primary domain: `https://etfcstream.rf.gd`
3. **Log into cPanel** and verify that PHP version is **7.4, 8.0, 8.1, or 8.2**.

---

## 🗄️ Step 2: Create MySQL Database & Run `schema.sql`

1. Inside cPanel / InfinityFree Control Panel, navigate to **MySQL® Databases**.
2. Your database details:
   - **DB Host**: `sql208.infinityfree.com`
   - **DB Name**: `if0_42797288_etfc_streaming_db`
   - **DB User**: `if0_42797288`
   - **DB Password**: `rpKjGIkgwDld`
3. Open **phpMyAdmin** in your control panel.
4. Select `if0_42797288_etfc_streaming_db`.
5. Click the **Import** tab at the top.
6. Choose file: `/cpanel_backend/database/schema.sql` and click **Go**.
7. Verify that the following 7 tables are created:
   - `users`
   - `events`
   - `fighters`
   - `matches`
   - `ppv_passes`
   - `live_stream_control`
   - `payment_webhooks_log`

---

## 📁 Step 3: Upload Backend Files to `public_html` / `htdocs`

Using **cPanel File Manager** or **FTP** (FileZilla):

1. Open the `htdocs/` or `public_html/` folder on your server.
2. Upload the files from the `/cpanel_backend/` folder into your web root:
   ```
   htdocs/
   ├── index.php                 (Web landing page & countdown)
   ├── .htaccess                 (CORS & security rules)
   ├── config/
   │   ├── config.php            (Database & API keys)
   │   ├── db.php                (PDO database connection)
   │   └── firebase.php          (Firebase cloud bridge)
   ├── admin/
   │   └── index.php             (Admin control dashboard)
   └── api/
       ├── events/list.php       (Fixtures & fight card endpoint)
       ├── stream/status.php     (Single HLS stream & live status)
       ├── ppv/initiate.php      (600 ETB checkout generator)
       ├── ppv/verify_pass.php   (Pass verification endpoint)
       └── webhooks/
           ├── telebirr.php      (Telebirr IPN callback)
           └── chapa.php         (Chapa payment callback)
   ```
3. `config/config.php` is pre-configured with:
   ```php
   define('DB_HOST', getenv('DB_HOST') ?: 'sql208.infinityfree.com');
   define('DB_PORT', getenv('DB_PORT') ?: '3306');
   define('DB_NAME', getenv('DB_NAME') ?: 'if0_42797288_etfc_streaming_db');
   define('DB_USER', getenv('DB_USER') ?: 'if0_42797288');
   define('DB_PASS', getenv('DB_PASS') ?: 'rpKjGIkgwDld');
   ```

---

## 🔥 Step 4: Configure Firebase Cloud Synchronization

1. In the **Firebase Console** (https://console.firebase.google.com):
   - Project: Ensure your project has **Cloud Firestore** and **Realtime Database** enabled.
2. Update `firestore.rules` (already included at `/firestore.rules`).
3. Set your Firebase Project ID inside `config/config.php`:
   ```php
   define('FIREBASE_PROJECT_ID', 'your-firebase-project-id');
   define('FIREBASE_DATABASE_URL', 'https://your-firebase-project-id-default-rtdb.firebaseio.com');
   ```
4. When a user buys a PPV pass through Telebirr or Chapa:
   - cPanel MySQL records the order as `PAID`.
   - `firebase.php` automatically patches Firestore at `/ppv_passes/{phone}` and Realtime Database at `/live_arena/status`.
   - The Android application immediately detects the pass in real-time.

---

## 💳 Step 5: Configure Payment Webhook URLs

- **Telebirr Developer Portal / Partner Merchant**:
  - Webhook URL: `https://etfcstream.rf.gd/api/webhooks/telebirr.php`
- **Chapa Dashboard -> Settings -> Webhooks**:
  - Webhook URL: `https://etfcstream.rf.gd/api/webhooks/chapa.php`
  - Secret Hash: `chapa_secret_hash_etfc` (matches `CHAPA_WEBHOOK_SECRET` in `config.php`)

---

## 🛠️ Step 6: Access the Web Admin Control Portal

1. Navigate in your browser to: `https://etfcstream.rf.gd/admin/index.php`
2. Sign in with:
   - **Email**: `dannynewg@gmail.com`
   - **Password**: `etfc2026`
3. Features available:
   - **Live Broadcast Switch**: Toggle between "Upcoming / Countdown" and "Live Broadcast".
   - **Active Round & Clock**: Control real-time match clock and rounds.
   - **Complimentary Pass Issuer**: Instantly grant 600 ETB passes by phone number.
   - **Pinned Announcement Publisher**: Send real-time announcements to all viewers.
