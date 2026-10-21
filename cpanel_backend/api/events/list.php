<?php
// =============================================================================
// API: GET /api/events/list.php
// Returns all events, fixtures, bouts, and fighter profiles in JSON
// =============================================================================

require_once __DIR__ . '/../../config/config.php';
require_once __DIR__ . '/../../config/db.php';

setCorsHeaders();
header('Content-Type: application/json');

try {
    $db = Database::getConnection();

    // 1. Fetch Events
    $eventsStmt = $db->query("SELECT * FROM `events` ORDER BY `scheduled_at` DESC");
    $events = $eventsStmt->fetchAll();

    // 2. Fetch Bouts with Fighter Details
    $boutsSql = "
        SELECT
            m.*,
            e.event_code,
            e.event_name as parent_event_name,
            rf.name as red_name, rf.nickname as red_nickname, rf.country as red_country, rf.flag_emoji as red_flag, rf.wins as red_wins, rf.losses as red_losses, rf.draws as red_draws, rf.photo_url as red_photo,
            bf.name as blue_name, bf.nickname as blue_nickname, bf.country as blue_country, bf.flag_emoji as blue_flag, bf.wins as blue_wins, bf.losses as blue_losses, bf.draws as blue_draws, bf.photo_url as blue_photo
        FROM `matches` m
        JOIN `events` e ON m.event_id = e.id
        JOIN `fighters` rf ON m.red_fighter_id = rf.id
        JOIN `fighters` bf ON m.blue_fighter_id = bf.id
        ORDER BY m.display_order ASC
    ";
    $boutsStmt = $db->query($boutsSql);
    $bouts = $boutsStmt->fetchAll();

    // 3. Fetch Stream Status
    $streamStmt = $db->query("SELECT * FROM `live_stream_control` ORDER BY `id` DESC LIMIT 1");
    $streamControl = $streamStmt->fetch() ?: [
        'is_live_broadcast_active' => 0,
        'stream_health' => 'EXCELLENT',
        'pinned_announcement' => 'ETFC 003 scheduled for November 28, 2026'
    ];

    echo json_encode([
        'success' => true,
        'timestamp' => time(),
        'data' => [
            'events' => $events,
            'bouts' => $bouts,
            'stream' => $streamControl,
            'ppv_price_etb' => ETFC_PPV_PRICE_ETB,
            'currency' => 'ETB'
        ]
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Failed to load events: ' . $e->getMessage()
    ]);
}
