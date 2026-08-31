# ⚔️ ETFC STREAM: cPanel Free Hosting & Firebase Relational DB Setup Guide

This guide provides step-by-step instructions for deploying the **ETFC PHP/MySQL Backend & Web Portal** onto **cPanel Free Hosting** (e.g. InfinityFree, ProFreeHost, 000webhost, or any standard cPanel web host) and connecting it with the **Firebase Project** and **Android Jetpack Compose App**.

---

## 📂 Architecture Overview

```
+-----------------------------------------------------------------------------------+
|                            ETFC UNIFIED ARCHITECTURE                              |
+-----------------------------------------------------------------------------------+
|                                                                                   |
|  [cPanel Web Host]                  [Firebase Cloud Engine]                       |
|  etfcstream.freedev.app             - Cloud Firestore (Users, Passes, Bouts)      |
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

## 🚀 Step 1: Set Up Free cPanel Web Hosting

1. **Register for Free cPanel Hosting**:
   - Recommended options: **InfinityFree.com**, **ProFreeHost.com**, or **000webhost.com**.
2. **Assign Your Domain / Subdomain**:
   - Add your custom domain `etfcstream.freedev.app` or use the assigned free subdomain (e.g. `etfcstream.epizy.com`).
3. **Log into cPanel** and verify that PHP version is **7.4, 8.0, 8.1, or 8.2**.

---

## 🗄️ Step 2: Create MySQL Database & Run `schema.sql`

1. Inside cPanel, navigate to **MySQL® Databases** (or **MySQL Database Wizard**).
2. Create a new database: e.g. `etfc_streaming_db`.
3. Create a new user: e.g. `etfc_db_user` with a secure password (e.g. `ETFC_Strong_Pass_2026!`).
4. **Add User to Database** and grant **ALL PRIVILEGES**.
5. Return to cPanel Home and open **phpMyAdmin**.
6. Select your newly created `etfc_streaming_db`.
7. Click the **Import** tab at the top.
8. Choose file: `/cpanel_backend/database/schema.sql` and click **Go**.
9. Verify that the following 7 tables are created:
   - `users`
   - `events`
   - `fighters`
   - `matches`
   - `ppv_passes`
   - `live_stream_control`
   - `payment_webhooks_log`

---

## 📁 Step 3: Upload Backend Files to `public_html`

Using **cPanel File Manager** or **FTP** (FileZilla):

1. Open the `public_html/` folder on your server.
2. Upload the files from the `/cpanel_backend/` folder into your web root:
   ```
   public_html/
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
3. Edit `config/config.php` and fill in your MySQL credentials:
   ```php
   define('DB_HOST', 'localhost'); // Or your cPanel MySQL host (e.g. sql104.epizy.com)
   define('DB_NAME', 'your_cpanel_dbname');
   define('DB_USER', 'your_cpanel_dbuser');
   define('DB_PASS', 'your_cpanel_dbpassword');
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
  - Webhook URL: `https://etfcstream.freedev.app/api/webhooks/telebirr.php`
- **Chapa Dashboard -> Settings -> Webhooks**:
  - Webhook URL: `https://etfcstream.freedev.app/api/webhooks/chapa.php`
  - Secret Hash: `chapa_secret_hash_etfc` (matches `CHAPA_WEBHOOK_SECRET` in `config.php`)

---

## 🛠️ Step 6: Access the Web Admin Control Portal

1. Navigate in your browser to: `https://etfcstream.freedev.app/admin/index.php`
2. Sign in with:
   - **Email**: `dannynewg@gmail.com`
   - **Password**: `etfc2026`
3. Features available:
   - **Live Broadcast Switch**: Toggle between "Upcoming / Countdown" and "Live Broadcast".
   - **Active Round & Clock**: Control real-time match clock and rounds.
   - **Complimentary Pass Issuer**: Instantly grant 600 ETB passes by phone number.
   - **Pinned Announcement Publisher**: Send real-time announcements to all viewers.
