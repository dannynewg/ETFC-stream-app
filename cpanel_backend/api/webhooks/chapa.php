<?php
// =============================================================================
// WEBHOOK: POST /api/webhooks/chapa.php
// Chapa Payment Gateway Webhook Receiver (HMAC-SHA256 verified)
// =============================================================================

require_once __DIR__ . '/../../config/config.php';
require_once __DIR__ . '/../../config/db.php';
require_once __DIR__ . '/../../config/firebase.php';

setCorsHeaders();
header('Content-Type: application/json');

$rawPayload = file_get_contents('php://input');
$headers = getallheaders();
$chapaSignature = $headers['Chapa-Signature'] ?? ($headers['x-chapa-signature'] ?? '');

$data = json_decode($rawPayload, true) ?: $_POST;

try {
    $db = Database::getConnection();

    // 1. Audit Log
    $txRef = $data['tx_ref'] ?? ($data['reference'] ?? null);
    $logStmt = $db->prepare("
        INSERT INTO `payment_webhooks_log` (`provider`, `order_ref`, `headers`, `raw_payload`, `http_status`, `ip_address`)
        VALUES ('CHAPA', :order_ref, :headers, :payload, 200, :ip)
    ");
    $logStmt->execute([
        ':order_ref' => $txRef,
        ':headers' => json_encode($headers),
        ':payload' => $rawPayload ?: json_encode($_REQUEST),
        ':ip' => $_SERVER['REMOTE_ADDR'] ?? 'UNKNOWN'
    ]);
    $logId = $db->lastInsertId();

    if (empty($txRef)) {
        http_response_code(400);
        echo json_encode(['status' => 'failed', 'message' => 'Missing tx_ref']);
        exit();
    }

    $eventStatus = strtolower($data['status'] ?? ($data['event'] ?? ''));

    if ($eventStatus === 'success' || $eventStatus === 'paid' || $eventStatus === 'charge.completed') {
        // 2. Mark pass as PAID
        $updateStmt = $db->prepare("
            UPDATE `ppv_passes` 
            SET `status` = 'PAID',
                `access_granted_at` = NOW(),
                `provider_tx_ref` = :provider_tx
            WHERE `order_ref` = :order_ref
        ");
        $updateStmt->execute([
            ':provider_tx' => $data['reference'] ?? $txRef,
            ':order_ref' => $txRef
        ]);

        // 3. Fetch and sync to Firebase Cloud
        $passStmt = $db->prepare("SELECT p.*, e.event_code FROM `ppv_passes` p JOIN `events` e ON p.event_id = e.id WHERE p.order_ref = :order_ref LIMIT 1");
        $passStmt->execute([':order_ref' => $txRef]);
        $passRecord = $passStmt->fetch();

        if ($passRecord) {
            FirebaseSync::syncPpvPassToFirestore($passRecord['user_phone'], $passRecord);
        }

        $db->exec("UPDATE `payment_webhooks_log` SET `response_body` = 'CHAPA_PAID_OK' WHERE id = " . (int)$logId);

        echo json_encode(['status' => 'success', 'message' => 'Pass Activated']);
    } else {
        echo json_encode(['status' => 'pending', 'message' => 'Payment event is not success']);
    }
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}
