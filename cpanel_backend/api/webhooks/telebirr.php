<?php
// =============================================================================
// WEBHOOK: POST /api/webhooks/telebirr.php
// Telebirr / CBePay Instant Payment Notification (IPN) Receiver
// =============================================================================

require_once __DIR__ . '/../../config/config.php';
require_once __DIR__ . '/../../config/db.php';
require_once __DIR__ . '/../../config/firebase.php';

setCorsHeaders();
header('Content-Type: application/json');

$rawPayload = file_get_contents('php://input');
$headers = json_encode(getallheaders());
$ip = $_SERVER['REMOTE_ADDR'] ?? 'UNKNOWN';

$data = json_decode($rawPayload, true) ?: $_POST;

try {
    $db = Database::getConnection();

    // 1. Audit Log Raw Webhook
    $logStmt = $db->prepare("
        INSERT INTO `payment_webhooks_log` (`provider`, `order_ref`, `headers`, `raw_payload`, `http_status`, `ip_address`)
        VALUES ('TELEBIRR', :order_ref, :headers, :payload, 200, :ip)
    ");
    $orderRef = $data['outTradeNo'] ?? $data['order_ref'] ?? ($data['orderId'] ?? null);
    $logStmt->execute([
        ':order_ref' => $orderRef,
        ':headers' => $headers,
        ':payload' => $rawPayload ?: json_encode($_REQUEST),
        ':ip' => $ip
    ]);
    $logId = $db->lastInsertId();

    if (empty($orderRef)) {
        http_response_code(400);
        echo json_encode(['code' => 400, 'message' => 'Missing outTradeNo or order_ref']);
        exit();
    }

    // 2. Telebirr Status Check ('COMPLETED', '2', 'SUCCESS', or 'PAID')
    $tradeStatus = strtoupper((string)($data['tradeStatus'] ?? $data['status'] ?? 'SUCCESS'));
    $isPaid = ($tradeStatus === 'COMPLETED' || $tradeStatus === 'SUCCESS' || $tradeStatus === '2' || $tradeStatus === 'PAID');

    if ($isPaid) {
        // 3. Mark PPV Pass as PAID
        $updateStmt = $db->prepare("
            UPDATE `ppv_passes` 
            SET `status` = 'PAID',
                `access_granted_at` = NOW(),
                `provider_tx_ref` = :tx_ref
            WHERE `order_ref` = :order_ref
        ");
        $txRef = $data['tradeNo'] ?? $data['transactionNo'] ?? 'TB-' . time();
        $updateStmt->execute([
            ':tx_ref' => $txRef,
            ':order_ref' => $orderRef
        ]);

        // 4. Fetch Updated Pass Record
        $passStmt = $db->prepare("SELECT p.*, e.event_code FROM `ppv_passes` p JOIN `events` e ON p.event_id = e.id WHERE p.order_ref = :order_ref LIMIT 1");
        $passStmt->execute([':order_ref' => $orderRef]);
        $passRecord = $passStmt->fetch();

        if ($passRecord) {
            // 5. Instantly Sync to Firebase Cloud Firestore for Android App
            FirebaseSync::syncPpvPassToFirestore($passRecord['user_phone'], $passRecord);
        }

        // Update Log
        $db->exec("UPDATE `payment_webhooks_log` SET `response_body` = 'PASS_ACTIVATED_200' WHERE id = " . (int)$logId);

        echo json_encode([
            'code' => 0,
            'message' => 'success',
            'data' => [
                'order_ref' => $orderRef,
                'status' => 'ACTIVATED'
            ]
        ]);
    } else {
        echo json_encode([
            'code' => 1,
            'message' => 'Trade status pending or not successful: ' . $tradeStatus
        ]);
    }

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'code' => 500,
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
