<?php
// =============================================================================
// API: POST /api/ppv/verify_pass.php
// Validates whether a phone number / pass token / email has valid PPV access
// =============================================================================

require_once __DIR__ . '/../../config/config.php';
require_once __DIR__ . '/../../config/db.php';

setCorsHeaders();
header('Content-Type: application/json');

$input = json_decode(file_get_contents('php://input'), true) ?: $_POST;
$phone = trim($input['phone_number'] ?? $input['phone'] ?? '');
$passToken = trim($input['pass_token'] ?? '');
$email = trim($input['email'] ?? '');
$eventCode = trim($input['event_code'] ?? 'ETFC_003');

if (empty($phone) && empty($passToken) && empty($email)) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'has_pass' => false,
        'message' => 'Please provide phone_number, pass_token, or email to verify PPV access.'
    ]);
    exit();
}

try {
    $db = Database::getConnection();

    // Query for active paid PPV pass for this event
    $sql = "
        SELECT p.*, e.event_name, e.event_code 
        FROM `ppv_passes` p
        JOIN `events` e ON p.event_id = e.id
        WHERE p.status = 'PAID' 
          AND (e.event_code = :event_code OR :event_code = '')
          AND (
              (:phone != '' AND p.user_phone = :phone) OR
              (:token != '' AND p.pass_token = :token) OR
              (:email != '' AND p.user_email = :email)
          )
        ORDER BY p.id DESC 
        LIMIT 1
    ";

    $stmt = $db->prepare($sql);
    $stmt->execute([
        ':event_code' => $eventCode,
        ':phone' => $phone,
        ':token' => $passToken,
        ':email' => $email
    ]);

    $pass = $stmt->fetch();

    if ($pass) {
        echo json_encode([
            'success' => true,
            'has_pass' => true,
            'data' => [
                'order_ref' => $pass['order_ref'],
                'event_code' => $pass['event_code'],
                'event_name' => $pass['event_name'],
                'payment_provider' => $pass['payment_provider'],
                'amount_etb' => (float)$pass['amount_etb'],
                'pass_token' => $pass['pass_token'],
                'access_granted_at' => $pass['access_granted_at'] ?? $pass['created_at'],
                'expires_at' => $pass['expires_at']
            ],
            'message' => 'PPV Pass is VALID and active.'
        ]);
    } else {
        echo json_encode([
            'success' => true,
            'has_pass' => false,
            'message' => 'No active PPV pass found for this account. Pass purchase required (600 ETB).'
        ]);
    }
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'has_pass' => false,
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
