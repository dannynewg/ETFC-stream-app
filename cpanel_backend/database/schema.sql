-- =============================================================================
-- ETFC STREAMING PLATFORM - RELATIONAL DATABASE SCHEMA (MySQL / MariaDB)
-- Host: cPanel / InfinityFree MySQL Database (sql208.infinityfree.com)
-- Domain: etfcstream.rf.gd
-- Database: if0_42797288_etfc_streaming_db
-- Architecture: Relational Backend + Firebase Cloud Sync Bridge
-- =============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------------------------------
-- 1. USERS & PROFILES TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `firebase_uid` VARCHAR(128) NULL UNIQUE,
    `email` VARCHAR(191) NULL UNIQUE,
    `phone_number` VARCHAR(32) NULL UNIQUE,
    `password_hash` VARCHAR(255) NULL,
    `display_name` VARCHAR(100) NOT NULL DEFAULT 'ETFC Fan',
    `handle` VARCHAR(50) NOT NULL DEFAULT '@etfc_fan',
    `country_flag` VARCHAR(8) NOT NULL DEFAULT '🇪🇹',
    `avatar_url` TEXT NULL,
    `role` ENUM('USER', 'VIP_MEMBER', 'MODERATOR', 'ADMIN', 'SUPERADMIN') NOT NULL DEFAULT 'USER',
    `is_banned` TINYINT(1) NOT NULL DEFAULT 0,
    `muted_until` DATETIME NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_users_role` (`role`),
    INDEX `idx_users_phone` (`phone_number`),
    INDEX `idx_users_firebase` (`firebase_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 2. EVENTS TABLE (ETFC 001, ETFC 002, ETFC 003, etc.)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `events` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `event_code` VARCHAR(32) NOT NULL UNIQUE, -- e.g. 'ETFC_003'
    `event_name` VARCHAR(191) NOT NULL,       -- 'ETFC 003: Champions Collide'
    `event_edition` VARCHAR(50) NOT NULL DEFAULT 'ETFC 003',
    `venue` VARCHAR(191) NOT NULL DEFAULT 'Adwa 00 Museum Arena, Addis Ababa',
    `scheduled_at` DATETIME NOT NULL,
    `status` ENUM('SCHEDULED', 'UPCOMING', 'LIVE_NOW', 'COMPLETED', 'POSTPONED') NOT NULL DEFAULT 'UPCOMING',
    `ppv_price_etb` DECIMAL(10,2) NOT NULL DEFAULT 600.00,
    `poster_image_url` TEXT NULL,
    `hls_master_url` TEXT NULL,              -- Single HLS master stream .m3u8
    `fallback_youtube_id` VARCHAR(64) NULL DEFAULT '73P6qS14uP0',
    `description` TEXT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_events_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 3. FIGHTERS ROSTER
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fighters` (
    `id` VARCHAR(64) PRIMARY KEY,              -- e.g. 'f_sedo_01'
    `name` VARCHAR(100) NOT NULL,
    `nickname` VARCHAR(100) NOT NULL DEFAULT '',
    `division` VARCHAR(64) NOT NULL DEFAULT 'Middleweight',
    `country` VARCHAR(64) NOT NULL DEFAULT 'Ethiopia',
    `flag_emoji` VARCHAR(8) NOT NULL DEFAULT '🇪🇹',
    `wins` INT UNSIGNED NOT NULL DEFAULT 0,
    `losses` INT UNSIGNED NOT NULL DEFAULT 0,
    `draws` INT UNSIGNED NOT NULL DEFAULT 0,
    `kos` INT UNSIGNED NOT NULL DEFAULT 0,
    `submissions` INT UNSIGNED NOT NULL DEFAULT 0,
    `is_champion` TINYINT(1) NOT NULL DEFAULT 0,
    `rank_num` INT NOT NULL DEFAULT 1,
    `reach_inches` INT NOT NULL DEFAULT 72,
    `height_cm` INT NOT NULL DEFAULT 180,
    `stance` VARCHAR(32) NOT NULL DEFAULT 'Orthodox',
    `signature_move` VARCHAR(100) NOT NULL DEFAULT 'Right Overhand',
    `bio` TEXT NULL,
    `photo_url` TEXT NULL,
    `primary_color_hex` BIGINT UNSIGNED NOT NULL DEFAULT 4009015108,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 4. MATCHES / BOUTS TABLE (RELATIONAL FIXTURES & RESULTS)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `matches` (
    `id` VARCHAR(64) PRIMARY KEY,              -- e.g. 'bout_etfc003_main'
    `event_id` INT UNSIGNED NOT NULL,
    `bout_type` ENUM('MAIN_EVENT', 'CO_MAIN', 'MAIN_CARD', 'EARLY_PRELIMS') NOT NULL DEFAULT 'MAIN_CARD',
    `weight_class` VARCHAR(64) NOT NULL DEFAULT 'Middleweight',
    `match_category` ENUM('MMA', 'BOXING', 'MUAY_THAI') NOT NULL DEFAULT 'MMA',
    `match_title` VARCHAR(191) NOT NULL DEFAULT 'UNDISPUTED CHAMPIONSHIP BOUT',
    `red_fighter_id` VARCHAR(64) NOT NULL,
    `blue_fighter_id` VARCHAR(64) NOT NULL,
    `is_title_fight` TINYINT(1) NOT NULL DEFAULT 0,
    `scheduled_rounds` INT NOT NULL DEFAULT 3,
    `display_order` INT NOT NULL DEFAULT 0,
    `status` ENUM('UPCOMING', 'NEXT_UP', 'LIVE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'UPCOMING',
    `current_round` INT NOT NULL DEFAULT 1,
    `clock_seconds_remaining` INT NOT NULL DEFAULT 300,
    `winner_fighter_id` VARCHAR(64) NULL,
    `result_method` VARCHAR(191) NULL,       -- e.g. 'KO (Round 3, 2:48)'
    `red_sig_strikes` INT UNSIGNED NOT NULL DEFAULT 0,
    `blue_sig_strikes` INT UNSIGNED NOT NULL DEFAULT 0,
    `red_total_strikes` INT UNSIGNED NOT NULL DEFAULT 0,
    `blue_total_strikes` INT UNSIGNED NOT NULL DEFAULT 0,
    `red_takedowns` INT UNSIGNED NOT NULL DEFAULT 0,
    `blue_takedowns` INT UNSIGNED NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`event_id`) REFERENCES `events`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`red_fighter_id`) REFERENCES `fighters`(`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`blue_fighter_id`) REFERENCES `fighters`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 5. PPV PURCHASES & PASSES (Telebirr / Chapa / CBE)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `ppv_passes` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `order_ref` VARCHAR(64) NOT NULL UNIQUE, -- e.g. 'ETFC-TX-92837'
    `user_id` INT UNSIGNED NULL,
    `user_phone` VARCHAR(32) NOT NULL,
    `user_email` VARCHAR(191) NULL,
    `event_id` INT UNSIGNED NOT NULL,
    `payment_provider` ENUM('TELEBIRR', 'CHAPA', 'CBE_BIRR', 'MPESA', 'CASH_OFFLINE') NOT NULL DEFAULT 'TELEBIRR',
    `amount_etb` DECIMAL(10,2) NOT NULL DEFAULT 600.00,
    `currency` VARCHAR(8) NOT NULL DEFAULT 'ETB',
    `provider_tx_ref` VARCHAR(128) NULL,
    `status` ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    `pass_token` VARCHAR(128) NOT NULL UNIQUE,
    `access_granted_at` DATETIME NULL,
    `expires_at` DATETIME NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`event_id`) REFERENCES `events`(`id`) ON DELETE CASCADE,
    INDEX `idx_ppv_phone` (`user_phone`),
    INDEX `idx_ppv_token` (`pass_token`),
    INDEX `idx_ppv_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 6. LIVE ARENA REAL-TIME STREAM STATE & CHAT
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `live_stream_control` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `event_id` INT UNSIGNED NOT NULL UNIQUE,
    `is_live_broadcast_active` TINYINT(1) NOT NULL DEFAULT 0,
    `active_bout_id` VARCHAR(64) NULL,
    `live_round` INT NOT NULL DEFAULT 1,
    `round_clock_seconds` INT NOT NULL DEFAULT 300,
    `chat_slow_mode_seconds` INT NOT NULL DEFAULT 3,
    `is_chat_enabled` TINYINT(1) NOT NULL DEFAULT 1,
    `pinned_announcement` TEXT NULL,
    `viewer_count` INT UNSIGNED NOT NULL DEFAULT 0,
    `stream_health` ENUM('EXCELLENT', 'GOOD', 'DEGRADED', 'OFFLINE') NOT NULL DEFAULT 'EXCELLENT',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`event_id`) REFERENCES `events`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `live_chat_messages` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `event_id` INT UNSIGNED NOT NULL,
    `user_id` INT UNSIGNED NULL,
    `user_handle` VARCHAR(50) NOT NULL DEFAULT '@fan',
    `user_name` VARCHAR(100) NOT NULL DEFAULT 'Fan',
    `user_role` VARCHAR(32) NOT NULL DEFAULT 'USER',
    `message_text` VARCHAR(500) NOT NULL,
    `cheer_emoji` VARCHAR(16) NULL,
    `is_superfan` TINYINT(1) NOT NULL DEFAULT 0,
    `is_pinned` TINYINT(1) NOT NULL DEFAULT 0,
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
    `deleted_by_admin` VARCHAR(100) NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`event_id`) REFERENCES `events`(`id`) ON DELETE CASCADE,
    INDEX `idx_chat_event` (`event_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 7. WEBHOOK AUDIT & TELEMETRY LOGS
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `payment_webhooks_log` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `provider` VARCHAR(32) NOT NULL,
    `order_ref` VARCHAR(64) NULL,
    `headers` TEXT NULL,
    `raw_payload` LONGTEXT NOT NULL,
    `http_status` INT NOT NULL DEFAULT 200,
    `response_body` TEXT NULL,
    `ip_address` VARCHAR(64) NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- SEED INITIAL DATA (ETFC 003 FIXTURES & ETFC 002 PAST RESULTS)
-- -----------------------------------------------------------------------------
INSERT INTO `events` (`id`, `event_code`, `event_name`, `event_edition`, `venue`, `scheduled_at`, `status`, `ppv_price_etb`, `hls_master_url`)
VALUES 
(1, 'ETFC_002', 'ETFC 002: Adwa Fight Night', 'ETFC 002', 'Adwa 00 Museum, Addis Ababa', '2026-08-27 20:00:00', 'COMPLETED', 600.00, 'https://etfcstream.rf.gd/hls/etfc002_master.m3u8'),
(2, 'ETFC_003', 'ETFC 003: Champions Collide', 'ETFC 003', 'Adwa 00 Museum Arena, Addis Ababa', '2026-11-28 20:00:00', 'UPCOMING', 600.00, 'https://etfcstream.rf.gd/hls/etfc003_master.m3u8')
ON DUPLICATE KEY UPDATE `event_name`=VALUES(`event_name`);

INSERT INTO `live_stream_control` (`event_id`, `is_live_broadcast_active`, `stream_health`, `pinned_announcement`)
VALUES 
(2, 0, 'EXCELLENT', 'ETFC 003 Live broadcast scheduled for November 28, 2026 at Adwa 00 Museum Arena.')
ON DUPLICATE KEY UPDATE `pinned_announcement`=VALUES(`pinned_announcement`);

-- Superadmin Default Account
INSERT INTO `users` (`id`, `email`, `phone_number`, `display_name`, `handle`, `role`)
VALUES (1, 'dannynewg@gmail.com', '+251911000000', 'Superadmin Danny', '@etfc_owner', 'SUPERADMIN')
ON DUPLICATE KEY UPDATE `role`='SUPERADMIN';

SET FOREIGN_KEY_CHECKS = 1;
