<?php
// =============================================================================
// API: GET /api/stream/status.php
// Returns Single HLS Master Playlist URL and Live Event Status
// =============================================================================

require_once __DIR__ . '/../../config/config.php';
require_once __DIR__ . '/../../config/db.php';

setCorsHeaders();
header('Content-Type: application/json');

try {
    $db = Database::getConnection();

    $stmt = $db->query("
        SELECT 
            lsc.*,
            e.event_name,
            e.event_code,
            e.hls_master_url,
            e.fallback_youtube_id,
            e.status as event_status
        FROM `live_stream_control` lsc
        JOIN `events` e ON lsc.event_id = e.id
        ORDER BY lsc.id DESC
        LIMIT 1
    ");

    $stream = $stmt->fetch();

    if (!$stream) {
        $stream = [
            'is_live_broadcast_active' => 0,
            'hls_master_url' => DEFAULT_HLS_STREAM_URL,
            'fallback_youtube_id' => '73P6qS14uP0',
            'pinned_announcement' => 'ETFC 003: Champions Collide is coming November 28, 2026.',
            'viewer_count' => 0,
            'stream_health' => 'EXCELLENT'
        ];
    }

    echo json_encode([
        'success' => true,
        'is_live' => (bool)$stream['is_live_broadcast_active'],
        'stream_url' => !empty($stream['hls_master_url']) ? $stream['hls_master_url'] : DEFAULT_HLS_STREAM_URL,
        'fallback_youtube_id' => $stream['fallback_youtube_id'] ?? '73P6qS14uP0',
        'active_bout_id' => $stream['active_bout_id'] ?? null,
        'live_round' => (int)($stream['live_round'] ?? 1),
        'clock_seconds' => (int)($stream['round_clock_seconds'] ?? 300),
        'chat_slow_mode_seconds' => (int)($stream['chat_slow_mode_seconds'] ?? 3),
        'pinned_announcement' => $stream['pinned_announcement'] ?? '',
        'viewer_count' => (int)($stream['viewer_count'] ?? 1420),
        'health' => $stream['stream_health'] ?? 'EXCELLENT',
        'server_time' => time()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Error checking stream status: ' . $e->getMessage()
    ]);
}
