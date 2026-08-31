<?php
// =============================================================================
// ETFC STREAMING PLATFORM - CONFIGURATION SETTINGS
// File: /cpanel_backend/config/config.php
// Domain: etfcstream.freedev.app
// =============================================================================

// Prevent direct access
defined('ETFC_APP') or define('ETFC_APP', true);

// Application Environment: 'development' or 'production'
define('APP_ENV', 'production');

// Base URL of your cPanel Hosting
define('BASE_URL', 'https://etfcstream.freedev.app');

// -----------------------------------------------------------------------------
// 1. MySQL Database Credentials (Created in cPanel -> MySQL Databases)
// -----------------------------------------------------------------------------
define('DB_HOST', getenv('DB_HOST') ?: 'localhost');
define('DB_PORT', getenv('DB_PORT') ?: '3306');
define('DB_NAME', getenv('DB_NAME') ?: 'etfc_streaming_db');
define('DB_USER', getenv('DB_USER') ?: 'etfc_db_user');
define('DB_PASS', getenv('DB_PASS') ?: 'ETFC_Strong_Pass_2026!');
define('DB_CHARSET', 'utf8mb4');

// -----------------------------------------------------------------------------
// 2. Firebase Cloud Configuration (For Real-Time Cloud Sync)
// -----------------------------------------------------------------------------
define('FIREBASE_PROJECT_ID', getenv('FIREBASE_PROJECT_ID') ?: 'etfc-streaming-prod');
define('FIREBASE_API_KEY', getenv('FIREBASE_API_KEY') ?: 'AIzaSy...');
define('FIREBASE_DATABASE_URL', 'https://etfc-streaming-prod-default-rtdb.firebaseio.com');

// -----------------------------------------------------------------------------
// 3. Payment Gateway Credentials
// -----------------------------------------------------------------------------
// Telebirr / CBePay Integration
define('TELEBIRR_APP_ID', getenv('TELEBIRR_APP_ID') ?: 'YOUR_TELEBIRR_APP_ID');
define('TELEBIRR_APP_KEY', getenv('TELEBIRR_APP_KEY') ?: 'YOUR_TELEBIRR_APP_KEY');
define('TELEBIRR_PUBLIC_KEY', getenv('TELEBIRR_PUBLIC_KEY') ?: 'YOUR_TELEBIRR_PUBLIC_KEY');
define('TELEBIRR_SHORT_CODE', getenv('TELEBIRR_SHORT_CODE') ?: '100023');
define('TELEBIRR_NOTIFY_URL', BASE_URL . '/api/webhooks/telebirr.php');

// Chapa Integration
define('CHAPA_SECRET_KEY', getenv('CHAPA_SECRET_KEY') ?: 'CHASECK_TEST-...');
define('CHAPA_WEBHOOK_SECRET', getenv('CHAPA_WEBHOOK_SECRET') ?: 'chapa_secret_hash_etfc');
define('CHAPA_CALLBACK_URL', BASE_URL . '/api/webhooks/chapa.php');

// PPV Price in Ethiopian Birr
define('ETFC_PPV_PRICE_ETB', 600.00);

// Single Unified HLS Master Stream URL
define('DEFAULT_HLS_STREAM_URL', 'https://cdn.etfcstream.freedev.app/live/etfc_master.m3u8');

// Admin Superuser Email
define('SUPERADMIN_EMAIL', 'dannynewg@gmail.com');
define('JWT_SECRET_KEY', 'etfc_super_secret_jwt_sign_key_adwa_2026');

// -----------------------------------------------------------------------------
// Global Error Reporting
// -----------------------------------------------------------------------------
if (APP_ENV === 'production') {
    error_reporting(0);
    ini_set('display_errors', '0');
} else {
    error_reporting(E_ALL);
    ini_set('display_errors', '1');
}

// Global CORS Headers for Android & Web App
function setCorsHeaders() {
    header("Access-Control-Allow-Origin: *");
    header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
    header("Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With, X-ETFC-Api-Key");
    
    if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
        http_response_code(200);
        exit();
    }
}
