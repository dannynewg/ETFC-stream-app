<?php
// =============================================================================
// ETFC STREAMING PLATFORM - WEB ADMIN PORTAL
// File: /cpanel_backend/admin/index.php
// Theme: ETFC Dark Gold Octagon Interface
// =============================================================================

require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/db.php';
require_once __DIR__ . '/../config/firebase.php';

session_start();

// Simple Admin Authentication Check
$isAdmin = isset($_SESSION['etfc_admin_auth']) && $_SESSION['etfc_admin_auth'] === true;
$message = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';

    if ($action === 'login') {
        $email = trim($_POST['email'] ?? '');
        $password = trim($_POST['password'] ?? '');
        // Default master passcode for demo or check DB
        if ($email === SUPERADMIN_EMAIL && ($password === 'etfc2026' || $password === 'admin')) {
            $_SESSION['etfc_admin_auth'] = true;
            $_SESSION['admin_email'] = $email;
            $isAdmin = true;
        } else {
            $message = 'Invalid Admin credentials.';
        }
    } elseif ($action === 'logout') {
        session_destroy();
        header('Location: index.php');
        exit();
    } elseif ($isAdmin && $action === 'update_stream_control') {
        $db = Database::getConnection();
        $isLive = isset($_POST['is_live']) ? 1 : 0;
        $liveRound = (int)($_POST['live_round'] ?? 1);
        $clockSeconds = (int)($_POST['clock_seconds'] ?? 300);
        $pinned = trim($_POST['pinned_announcement'] ?? '');
        $hlsUrl = trim($_POST['hls_master_url'] ?? DEFAULT_HLS_STREAM_URL);

        $stmt = $db->prepare("
            UPDATE `live_stream_control` 
            SET `is_live_broadcast_active` = :is_live,
                `live_round` = :round,
                `round_clock_seconds` = :clock,
                `pinned_announcement` = :pinned
            WHERE `event_id` = 2
        ");
        $stmt->execute([
            ':is_live' => $isLive,
            ':round' => $liveRound,
            ':clock' => $clockSeconds,
            ':pinned' => $pinned
        ]);

        // Sync to Firebase Realtime Database
        FirebaseSync::updateRealtimeArenaStatus([
            'is_live' => (bool)$isLive,
            'round' => $liveRound,
            'clock_seconds' => $clockSeconds,
            'hls_url' => $hlsUrl,
            'announcement' => $pinned
        ]);

        $message = 'Stream state updated and synchronized to Firebase Cloud!';
    } elseif ($isAdmin && $action === 'grant_manual_pass') {
        $phone = trim($_POST['phone'] ?? '');
        if (!empty($phone)) {
            $db = Database::getConnection();
            $orderRef = 'MANUAL-' . strtoupper(substr(md5(uniqid()), 0, 6));
            $passToken = 'PASS_COMP_' . bin2hex(random_bytes(8));
            $expiresAt = date('Y-m-d H:i:s', strtotime('+30 days'));

            $stmt = $db->prepare("
                INSERT INTO `ppv_passes` 
                (`order_ref`, `user_phone`, `event_id`, `payment_provider`, `amount_etb`, `status`, `pass_token`, `access_granted_at`, `expires_at`)
                VALUES 
                (:order_ref, :phone, 2, 'CASH_OFFLINE', 600.00, 'PAID', :token, NOW(), :expires_at)
            ");
            $stmt->execute([
                ':order_ref' => $orderRef,
                ':phone' => $phone,
                ':token' => $passToken,
                ':expires_at' => $expiresAt
            ]);

            FirebaseSync::syncPpvPassToFirestore($phone, [
                'order_ref' => $orderRef,
                'user_phone' => $phone,
                'status' => 'PAID',
                'pass_token' => $passToken,
                'amount_etb' => 600.00,
                'expires_at' => $expiresAt
            ]);

            $message = "Complimentary Pass granted to {$phone}!";
        }
    }
}

$streamStatus = null;
$recentPasses = [];
$totalPaidPasses = 0;

if ($isAdmin) {
    $db = Database::getConnection();
    $streamStmt = $db->query("SELECT * FROM `live_stream_control` WHERE `event_id` = 2 LIMIT 1");
    $streamStatus = $streamStmt->fetch();

    $passesStmt = $db->query("SELECT * FROM `ppv_passes` ORDER BY `id` DESC LIMIT 10");
    $recentPasses = $passesStmt->fetchAll();

    $countStmt = $db->query("SELECT COUNT(*) FROM `ppv_passes` WHERE `status` = 'PAID'");
    $totalPaidPasses = $countStmt->fetchColumn();
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ETFC Admin Portal | Stream & PPV Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #0A0A0C; color: #FFFFFF; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .etfc-gold { color: #FAB818; }
        .bg-etfc-card { background-color: #16171C; border: 1px solid rgba(250, 184, 24, 0.2); border-radius: 12px; }
        .bg-etfc-inner { background-color: #0D0E12; border: 1px solid rgba(255,255,255,0.08); }
        .btn-etfc-gold { background: linear-gradient(135deg, #FAB818, #DF9E0B); color: #000; font-weight: 800; border: none; }
        .btn-etfc-gold:hover { background: #DF9E0B; color: #000; }
        .badge-live { background-color: #EF4444; color: #FFF; font-weight: 800; }
    </style>
</head>
<body class="p-4">
<div class="container" style="max-width: 1000px;">
    
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom border-secondary">
        <div>
            <h2 class="etfc-gold fw-black mb-0">⚔️ ETFC ADMIN CONTROL PORTAL</h2>
            <small class="text-secondary">Host: etfcstream.rf.gd • Event: ETFC 003: Champions Collide</small>
        </div>
        <?php if ($isAdmin): ?>
            <div>
                <span class="badge bg-success me-2">Admin: <?php echo htmlspecialchars($_SESSION['admin_email'] ?? 'Superadmin'); ?></span>
                <form method="POST" class="d-inline">
                    <input type="hidden" name="action" value="logout">
                    <button class="btn btn-sm btn-outline-danger">Sign Out</button>
                </form>
            </div>
        <?php endif; ?>
    </div>

    <?php if (!empty($message)): ?>
        <div class="alert alert-warning"><?php echo htmlspecialchars($message); ?></div>
    <?php endif; ?>

    <?php if (!$isAdmin): ?>
        <!-- Login Form -->
        <div class="row justify-content-center">
            <div class="col-md-5">
                <div class="bg-etfc-card p-4">
                    <h4 class="etfc-gold fw-bold mb-3">Admin Sign In</h4>
                    <form method="POST">
                        <input type="hidden" name="action" value="login">
                        <div class="mb-3">
                            <label class="form-label text-secondary small">Admin Email</label>
                            <input type="email" name="email" class="form-control bg-dark text-white border-secondary" value="dannynewg@gmail.com" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-secondary small">Password</label>
                            <input type="password" name="password" class="form-control bg-dark text-white border-secondary" placeholder="Enter admin passcode" required>
                            <small class="text-muted">Default: etfc2026</small>
                        </div>
                        <button type="submit" class="btn btn-etfc-gold w-100 py-2">AUTHENTICATE</button>
                    </form>
                </div>
            </div>
        </div>
    <?php else: ?>
        <!-- Admin Dashboard Content -->
        <div class="row g-4">
            
            <!-- Live Broadcast Control -->
            <div class="col-md-7">
                <div class="bg-etfc-card p-4 h-100">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="etfc-gold fw-bold mb-0">📺 Live Broadcast Control</h4>
                        <?php if ($streamStatus && $streamStatus['is_live_broadcast_active']): ?>
                            <span class="badge badge-live animate-pulse">● LIVE BROADCASTING</span>
                        <?php else: ?>
                            <span class="badge bg-secondary">OFFLINE / COUNTDOWN</span>
                        <?php endif; ?>
                    </div>

                    <form method="POST">
                        <input type="hidden" name="action" value="update_stream_control">
                        
                        <div class="form-check form-switch mb-3 p-3 bg-etfc-inner rounded">
                            <input class="form-check-input" type="checkbox" name="is_live" id="isLiveSwitch" <?php echo ($streamStatus && $streamStatus['is_live_broadcast_active']) ? 'checked' : ''; ?>>
                            <label class="form-check-label fw-bold ms-2" for="isLiveSwitch">
                                Enable Live Broadcast Mode (Triggers Live Player in Mobile App)
                            </label>
                        </div>

                        <div class="row mb-3">
                            <div class="col-6">
                                <label class="form-label text-secondary small">Active Round</label>
                                <input type="number" name="live_round" class="form-control bg-dark text-white border-secondary" value="<?php echo $streamStatus['live_round'] ?? 1; ?>" min="1" max="5">
                            </div>
                            <div class="col-6">
                                <label class="form-label text-secondary small">Round Clock (Seconds)</label>
                                <input type="number" name="clock_seconds" class="form-control bg-dark text-white border-secondary" value="<?php echo $streamStatus['round_clock_seconds'] ?? 300; ?>">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label text-secondary small">Single Master HLS Stream URL (.m3u8)</label>
                            <input type="url" name="hls_master_url" class="form-control bg-dark text-white border-secondary" value="<?php echo DEFAULT_HLS_STREAM_URL; ?>">
                        </div>

                        <div class="mb-3">
                            <label class="form-label text-secondary small">Pinned Banner Announcement</label>
                            <textarea name="pinned_announcement" rows="2" class="form-control bg-dark text-white border-secondary"><?php echo htmlspecialchars($streamStatus['pinned_announcement'] ?? ''); ?></textarea>
                        </div>

                        <button type="submit" class="btn btn-etfc-gold w-100 py-2">UPDATE LIVE ARENA & SYNC CLOUD</button>
                    </form>
                </div>
            </div>

            <!-- PPV Pass Stats & Quick Grant -->
            <div class="col-md-5">
                <div class="bg-etfc-card p-4 mb-4">
                    <h5 class="etfc-gold fw-bold mb-3">🎟️ 600 ETB PPV Passes</h5>
                    <div class="d-flex justify-content-between p-3 bg-etfc-inner rounded mb-3">
                        <span class="text-secondary">Active Paid Passes:</span>
                        <span class="fw-bold etfc-gold fs-5"><?php echo $totalPaidPasses; ?></span>
                    </div>

                    <h6 class="text-white fw-bold mb-2 small">Grant Complimentary Pass:</h6>
                    <form method="POST">
                        <input type="hidden" name="action" value="grant_manual_pass">
                        <div class="input-group">
                            <input type="text" name="phone" class="form-control bg-dark text-white border-secondary" placeholder="+251911..." required>
                            <button type="submit" class="btn btn-etfc-gold">Grant</button>
                        </div>
                    </form>
                </div>

                <div class="bg-etfc-card p-4">
                    <h6 class="etfc-gold fw-bold mb-3">Recent Transactions</h6>
                    <div class="small">
                        <?php foreach ($recentPasses as $pass): ?>
                            <div class="d-flex justify-content-between border-bottom border-dark py-1">
                                <span><?php echo htmlspecialchars($pass['user_phone']); ?></span>
                                <span class="badge <?php echo $pass['status'] === 'PAID' ? 'bg-success' : 'bg-warning'; ?>">
                                    <?php echo $pass['payment_provider']; ?> (<?php echo $pass['status']; ?>)
                                </span>
                            </div>
                        <?php endforeach; ?>
                    </div>
                </div>
            </div>

        </div>
    <?php endif; ?>

</div>
</body>
</html>
