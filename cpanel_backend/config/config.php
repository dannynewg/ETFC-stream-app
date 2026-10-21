<?php
// Values come from the hosting environment. No .env loader is bundled.
defined('ETFC_APP') or define('ETFC_APP', true);
define('APP_ENV', getenv('APP_ENV') ?: 'production');
define('BASE_URL', rtrim(getenv('BASE_URL') ?: 'http://localhost:8080', '/'));
define('DB_HOST', getenv('DB_HOST') ?: 'localhost');
define('DB_PORT', getenv('DB_PORT') ?: '3306');
define('DB_NAME', getenv('DB_NAME') ?: 'etfc_stream');
define('DB_USER', getenv('DB_USER') ?: '');
define('DB_PASS', getenv('DB_PASS') ?: '');
define('DB_CHARSET', 'utf8mb4');
define('FIREBASE_PROJECT_ID', getenv('FIREBASE_PROJECT_ID') ?: '');
define('FIREBASE_API_KEY', getenv('FIREBASE_API_KEY') ?: '');
define('FIREBASE_DATABASE_URL', rtrim(getenv('FIREBASE_DATABASE_URL') ?: '', '/'));
define('TELEBIRR_APP_ID', getenv('TELEBIRR_APP_ID') ?: '');
define('TELEBIRR_APP_KEY', getenv('TELEBIRR_APP_KEY') ?: '');
define('TELEBIRR_PUBLIC_KEY', getenv('TELEBIRR_PUBLIC_KEY') ?: '');
define('TELEBIRR_SHORT_CODE', getenv('TELEBIRR_SHORT_CODE') ?: '');
define('TELEBIRR_NOTIFY_URL', BASE_URL . '/api/webhooks/telebirr.php');
define('CHAPA_SECRET_KEY', getenv('CHAPA_SECRET_KEY') ?: '');
define('CHAPA_WEBHOOK_SECRET', getenv('CHAPA_WEBHOOK_SECRET') ?: '');
define('CHAPA_CALLBACK_URL', BASE_URL . '/api/webhooks/chapa.php');
define('ETFC_PPV_PRICE_ETB', 600.00);
define('DEFAULT_HLS_STREAM_URL', getenv('DEFAULT_HLS_STREAM_URL') ?: '');
define('SUPERADMIN_EMAIL', getenv('SUPERADMIN_EMAIL') ?: '');
define('ADMIN_PASSWORD_HASH', getenv('ADMIN_PASSWORD_HASH') ?: '');
define('JWT_SECRET_KEY', getenv('JWT_SECRET_KEY') ?: '');
error_reporting(E_ALL);
ini_set('display_errors', APP_ENV === 'production' ? '0' : '1');

function setCorsHeaders() {
    header('Access-Control-Allow-Origin: ' . (getenv('CORS_ALLOWED_ORIGIN') ?: 'http://localhost:8080'));
    header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
    header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    if (($_SERVER['REQUEST_METHOD'] ?? '') === 'OPTIONS') {
        http_response_code(204);
        exit();
    }
}
