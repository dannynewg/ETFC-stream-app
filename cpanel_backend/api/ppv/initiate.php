<?php
// =============================================================================
// API: POST /api/ppv/initiate.php
// Initiates Telebirr / Chapa / CBE payment for 600 ETB PPV Pass
// =============================================================================

require_once __DIR__ . '/../../config/config.php';
require_once __DIR__ . '/../../config/db.php';

setCorsHeaders();
header('Content-Type: application/json');

$input = json_decode(file_get_contents('php://input'), true) ?: $_POST;
$phone = trim($input['phone_number'] ?? $input['phone'] ?? '');
$email = trim($input['email'] ?? 'fan@etfcstream.com');
$provider = strtoupper(trim($input['provider'] ?? 'TELEBIRR'));
$eventCode = trim($input['event_code'] ?? 'ETFC_003');

if (empty($phone)) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => 'Phone number is required to generate payment checkout.'
    ]);
    exit();
}

try {
    $db = Database::getConnection();

    // 1. Get Event ID
    $eventStmt = $db->prepare("SELECT id, event_name, ppv_price_etb FROM `events` WHERE `event_code` = :code LIMIT 1");
    $eventStmt->execute([':code' => $eventCode]);
    $event = $eventStmt->fetch();

    if (!$event) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Event not found.']);
        exit();
    }

    $amount = (float)($event['ppv_price_etb'] ?: ETFC_PPV_PRICE_ETB);
    $orderRef = 'ETFC-TX-' . strtoupper(substr(md5(uniqid(rand(), true)), 0, 8));
    $passToken = 'PASS_' . bin2hex(random_bytes(16));
    $expiresAt = date('Y-m-d H:i:s', strtotime('+30 days'));

    // 2. Insert Pending Pass into Database
    $insertSql = "
        INSERT INTO `ppv_passes` 
        (`order_ref`, `user_phone`, `user_email`, `event_id`, `payment_provider`, `amount_etb`, `currency`, `status`, `pass_token`, `expires_at`)
        VALUES 
        (:order_ref, :user_phone, :user_email, :event_id, :provider, :amount, 'ETB', 'PENDING', :pass_token, :expires_at)
    ";
    $stmt = $db->prepare($insertSql);
    $stmt->execute([
        ':order_ref' => $orderRef,
        ':user_phone' => $phone,
        ':user_email' => $email,
        ':event_id' => $event['id'],
        ':provider' => in_array($provider, ['TELEBIRR', 'CHAPA', 'CBE_BIRR', 'MPESA']) ? $provider : 'TELEBIRR',
        ':amount' => $amount,
        ':pass_token' => $passToken,
        ':expires_at' => $expiresAt
    ]);

    // 3. Prepare Provider Specific Checkout URLs
    $checkoutData = [
        'order_ref' => $orderRef,
        'pass_token' => $passToken,
        'amount_etb' => $amount,
        'provider' => $provider,
        'phone_number' => $phone,
        'event_name' => $event['event_name'],
        'payment_instructions' => '',
        'checkout_url' => ''
    ];

    if ($provider === 'TELEBIRR') {
        $checkoutData['payment_instructions'] = "Send {$amount} ETB via Telebirr or authorize with USSD *127# referencing Order #{$orderRef}";
        $checkoutData['ussd_code'] = "*127*1*{$phone}*{$amount}#";
        $checkoutData['checkout_url'] = BASE_URL . "/pay/telebirr?order=" . urlencode($orderRef);
    } elseif ($provider === 'CHAPA') {
        $checkoutData['payment_instructions'] = "Pay via Chapa Online Checkout with CBE, Awash, Telebirr or Visa";
        $checkoutData['checkout_url'] = "https://checkout.chapa.co/checkout/payment/" . urlencode($orderRef);
    } else {
        $checkoutData['payment_instructions'] = "Pay {$amount} ETB to CBE Account 1000234857283 with Order reference {$orderRef}";
    }

    echo json_encode([
        'success' => true,
        'message' => 'PPV Order initiated successfully.',
        'data' => $checkoutData
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Failed to initiate order: ' . $e->getMessage()
    ]);
}
