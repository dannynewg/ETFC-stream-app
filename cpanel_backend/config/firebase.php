<?php
// =============================================================================
// ETFC STREAMING PLATFORM - FIREBASE CLOUD SYNC BRIDGE
// File: /cpanel_backend/config/firebase.php
// =============================================================================

require_once __DIR__ . '/config.php';

class FirebaseSync {
    /**
     * Push or update a PPV pass grant to Firestore /users/{userId}/passes or /ppv_passes
     */
    public static function syncPpvPassToFirestore(string $phoneOrUid, array $passData): bool {
        $projectId = FIREBASE_PROJECT_ID;
        $docId = preg_replace('/[^a-zA-Z0-9_-]/', '_', $phoneOrUid);
        $url = "https://firestore.googleapis.com/v1/projects/{$projectId}/databases/(default)/documents/ppv_passes/{$docId}";

        $fields = [
            'orderRef' => ['stringValue' => $passData['order_ref'] ?? ''],
            'userPhone' => ['stringValue' => $passData['user_phone'] ?? ''],
            'amountEtb' => ['doubleValue' => (float)($passData['amount_etb'] ?? 600.0)],
            'status' => ['stringValue' => $passData['status'] ?? 'PAID'],
            'passToken' => ['stringValue' => $passData['pass_token'] ?? ''],
            'eventCode' => ['stringValue' => $passData['event_code'] ?? 'ETFC_003'],
            'expiresAt' => ['stringValue' => $passData['expires_at'] ?? ''],
            'updatedAt' => ['integerValue' => (string)(time() * 1000)]
        ];

        $payload = json_encode(['fields' => $fields]);

        $ch = curl_init("{$url}?updateMask.fieldPaths=status&updateMask.fieldPaths=passToken&updateMask.fieldPaths=updatedAt&updateMask.fieldPaths=expiresAt");
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'PATCH');
        curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);
        curl_setopt($ch, CURLOPT_HTTPHEADER, [
            'Content-Type: application/json'
        ]);
        curl_setopt($ch, CURLOPT_TIMEOUT, 5);

        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);

        return ($httpCode >= 200 && $httpCode < 300);
    }

    /**
     * Update Realtime Database Live Stream Status (/live_arena/status)
     */
    public static function updateRealtimeArenaStatus(array $statusData): bool {
        $rtdbUrl = FIREBASE_DATABASE_URL . "/live_arena/status.json";
        $payload = json_encode([
            'isLive' => $statusData['is_live'] ?? false,
            'activeBoutId' => $statusData['active_bout_id'] ?? 'bout_etfc003_main',
            'currentRound' => (int)($statusData['round'] ?? 1),
            'clockSeconds' => (int)($statusData['clock_seconds'] ?? 300),
            'streamUrl' => $statusData['hls_url'] ?? DEFAULT_HLS_STREAM_URL,
            'announcement' => $statusData['announcement'] ?? '',
            'updatedAt' => time() * 1000
        ]);

        $ch = curl_init($rtdbUrl);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'PATCH');
        curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);
        curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
        curl_setopt($ch, CURLOPT_TIMEOUT, 5);

        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);

        return ($httpCode >= 200 && $httpCode < 300);
    }
}
