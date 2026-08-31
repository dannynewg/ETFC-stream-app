<?php
// =============================================================================
// ETFC STREAMING PLATFORM - WEB LANDING PORTAL
// File: /cpanel_backend/public_html/index.php
// Domain: etfcstream.rf.gd
// =============================================================================

require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../config/db.php';

$streamIsLive = false;
try {
    $db = Database::getConnection();
    $streamStmt = $db->query("SELECT is_live_broadcast_active FROM `live_stream_control` WHERE `event_id` = 2 LIMIT 1");
    $streamRow = $streamStmt->fetch();
    $streamIsLive = (bool)($streamRow['is_live_broadcast_active'] ?? false);
} catch (Exception $e) {
    // Fallback gracefully
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ETFC STREAM | Ethiopian Top Fight Championship Official Live Stream</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --etfc-gold: #FAB818;
            --etfc-red: #EF4444;
            --etfc-blue: #3B82F6;
            --etfc-dark: #0A0A0C;
            --etfc-card: #14151B;
            --etfc-card-inner: #0E0F14;
        }
        body {
            background-color: var(--etfc-dark);
            color: #FFFFFF;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
        }
        .text-gold { color: var(--etfc-gold); }
        .bg-gold { background-color: var(--etfc-gold); color: #000; }
        .border-gold { border-color: var(--etfc-gold) !important; }
        
        .hero-banner {
            background: linear-gradient(180deg, rgba(20, 21, 27, 0.4) 0%, rgba(10, 10, 12, 1) 100%),
                        url('https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?q=80&w=1600&auto=format&fit=crop') center/cover no-repeat;
            padding: 90px 0 60px 0;
            border-bottom: 2px solid rgba(250, 184, 24, 0.3);
        }

        .countdown-box {
            background: var(--etfc-card-inner);
            border: 1px solid rgba(250, 184, 24, 0.3);
            border-radius: 12px;
            padding: 16px;
            text-align: center;
            min-width: 85px;
        }

        .card-custom {
            background-color: var(--etfc-card);
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 16px;
        }

        .btn-gold-action {
            background: linear-gradient(135deg, #FAB818, #DF9E0B);
            color: #000;
            font-weight: 900;
            border-radius: 8px;
            padding: 14px 28px;
            text-transform: uppercase;
            letter-spacing: 1px;
            border: none;
            transition: all 0.3s ease;
        }
        .btn-gold-action:hover {
            background: #E5A407;
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(250, 184, 24, 0.4);
            color: #000;
        }

        .fighter-avatar {
            width: 70px;
            height: 70px;
            border-radius: 50%;
            object-fit: cover;
            border: 3px solid;
        }
        .avatar-red { border-color: var(--etfc-red); }
        .avatar-blue { border-color: var(--etfc-blue); }
    </style>
</head>
<body>

    <!-- Top Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-black border-bottom border-dark py-3">
        <div class="container">
            <a class="navbar-brand fw-black text-gold fs-4" href="#">
                ⚔️ ETFC STREAM
            </a>
            <div class="ms-auto d-flex align-items-center gap-3">
                <a href="#fixtures" class="text-white text-decoration-none small fw-bold d-none d-md-inline">FIXTURES</a>
                <a href="#ppv-pass" class="btn btn-sm btn-gold-action py-2 px-3">GET PPV PASS • 600 ETB</a>
                <a href="/admin/index.php" class="btn btn-sm btn-outline-secondary py-2"><i class="fas fa-lock"></i></a>
            </div>
        </div>
    </nav>

    <!-- Hero Event Header -->
    <section class="hero-banner text-center">
        <div class="container" style="max-width: 900px;">
            <div class="badge bg-gold text-dark fw-black px-3 py-2 mb-3">
                ⏳ UPCOMING IN 3 MONTHS • NOVEMBER 28, 2026
            </div>
            <h1 class="display-3 fw-black text-white text-uppercase tracking-wider mb-2">
                ETFC 003: <span class="text-gold">CHAMPIONS COLLIDE</span>
            </h1>
            <p class="lead text-secondary fw-bold mb-4">
                MAIN EVENT: SEDO vs JHONNY II • UNDISPUTED MIDDLEWEIGHT TITLE<br>
                📍 Adwa 00 Museum Arena, Addis Ababa, Ethiopia
            </p>

            <!-- Real-Time Countdown -->
            <div class="d-flex justify-content-center gap-3 mb-5">
                <div class="countdown-box">
                    <h2 class="text-gold fw-black mb-0" id="days">89</h2>
                    <small class="text-muted fw-bold">DAYS</small>
                </div>
                <div class="countdown-box">
                    <h2 class="text-gold fw-black mb-0" id="hours">11</h2>
                    <small class="text-muted fw-bold">HOURS</small>
                </div>
                <div class="countdown-box">
                    <h2 class="text-gold fw-black mb-0" id="minutes">42</h2>
                    <small class="text-muted fw-bold">MINS</small>
                </div>
                <div class="countdown-box">
                    <h2 class="text-gold fw-black mb-0" id="seconds">18</h2>
                    <small class="text-muted fw-bold">SECS</small>
                </div>
            </div>

            <!-- Call to Actions -->
            <div class="d-flex justify-content-center gap-3">
                <button class="btn btn-gold-action" data-bs-toggle="modal" data-bs-target="#ppvModal">
                    <i class="fas fa-ticket-alt me-2"></i> PURCHASE PPV PASS • 600 ETB
                </button>
            </div>
        </div>
    </section>

    <!-- Main Fight Card / Fixtures Section -->
    <section class="container py-5" id="fixtures">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h3 class="text-gold fw-black mb-0">OFFICIAL ETFC 003 FIGHT CARD</h3>
                <small class="text-secondary">Official 8-Bout Schedule at Adwa 00 Museum</small>
            </div>
            <span class="badge bg-dark border border-secondary px-3 py-2">8 BOUTS CONFIRMED</span>
        </div>

        <!-- Bout 1: Main Event -->
        <div class="card-custom p-4 mb-4 border-gold">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="badge bg-gold text-dark fw-bold">MAIN EVENT • 5 ROUNDS</span>
                <span class="text-gold fw-bold small"><i class="fas fa-trophy me-1"></i> UNDISPUTED TITLE</span>
            </div>
            <div class="row align-items-center text-center">
                <div class="col-5 text-start d-flex align-items-center gap-3">
                    <div class="fighter-avatar avatar-red bg-dark d-flex align-items-center justify-content-center fw-bold text-danger fs-4">S</div>
                    <div>
                        <h4 class="fw-black mb-0 text-white">SEDO "THE VIPER" 🇪🇹</h4>
                        <small class="text-danger fw-bold">12-1-0 • Champion</small>
                    </div>
                </div>
                <div class="col-2">
                    <span class="badge rounded-circle bg-dark border border-gold p-3 text-gold fw-black fs-5">VS</span>
                </div>
                <div class="col-5 text-end d-flex align-items-center justify-content-end gap-3">
                    <div>
                        <h4 class="fw-black mb-0 text-white">JHONNY "THE WARRIOR" 🇪🇹</h4>
                        <small class="text-primary fw-bold">10-2-0 • #1 Challenger</small>
                    </div>
                    <div class="fighter-avatar avatar-blue bg-dark d-flex align-items-center justify-content-center fw-bold text-primary fs-4">J</div>
                </div>
            </div>
        </div>

        <!-- Bout 2: Co-Main Event -->
        <div class="card-custom p-4 mb-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="badge bg-danger text-white fw-bold">CO-MAIN EVENT • 3 ROUNDS</span>
                <span class="text-secondary small">Welterweight Division</span>
            </div>
            <div class="row align-items-center text-center">
                <div class="col-5 text-start d-flex align-items-center gap-3">
                    <div class="fighter-avatar avatar-red bg-dark d-flex align-items-center justify-content-center fw-bold text-danger fs-4">A</div>
                    <div>
                        <h5 class="fw-black mb-0 text-white">ABEBE "THE LION" 🇪🇹</h5>
                        <small class="text-danger fw-bold">8-1-0</small>
                    </div>
                </div>
                <div class="col-2">
                    <span class="badge rounded-circle bg-dark border border-secondary p-2 text-gold fw-bold">VS</span>
                </div>
                <div class="col-5 text-end d-flex align-items-center justify-content-end gap-3">
                    <div>
                        <h5 class="fw-black mb-0 text-white">TARIKU "LIGHTNING" 🇪🇹</h5>
                        <small class="text-primary fw-bold">7-0-0</small>
                    </div>
                    <div class="fighter-avatar avatar-blue bg-dark d-flex align-items-center justify-content-center fw-bold text-primary fs-4">T</div>
                </div>
            </div>
        </div>
    </section>

    <!-- PPV Purchase Modal -->
    <div class="modal fade" id="ppvModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content bg-dark text-white border border-gold">
                <div class="modal-header border-secondary">
                    <h5 class="modal-title text-gold fw-bold">🎟️ ETFC 003 PPV PASS (600 ETB)</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <p class="text-secondary small mb-3">Unlock full live high-definition HLS stream access, live arena chat, and multi-camera replay across your mobile app and web browser.</p>
                    
                    <form id="ppvForm">
                        <div class="mb-3">
                            <label class="form-label text-secondary small fw-bold">Phone Number (Telebirr / CBE)</label>
                            <input type="tel" id="userPhone" class="form-control bg-black text-white border-secondary" placeholder="+251911..." required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-secondary small fw-bold">Payment Method</label>
                            <select id="paymentMethod" class="form-select bg-black text-white border-secondary">
                                <option value="TELEBIRR">Telebirr (USSD / Direct)</option>
                                <option value="CHAPA">Chapa (CBE Birr, Awash, Cards)</option>
                                <option value="CBE_BIRR">Commercial Bank of Ethiopia</option>
                            </select>
                        </div>
                        <button type="button" class="btn btn-gold-action w-100 py-3 mt-2" onclick="initiatePayment()">
                            PROCEED TO PAY 600 ETB
                        </button>
                    </form>
                    <div id="paymentResult" class="mt-3 text-center small text-gold"></div>
                </div>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Target: November 28, 2026 20:00:00 EAT
        const targetDate = new Date("2026-11-28T20:00:00+03:00").getTime();

        function updateCountdown() {
            const now = new Date().getTime();
            const diff = targetDate - now;

            if (diff > 0) {
                const days = Math.floor(diff / (1000 * 60 * 60 * 24));
                const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((diff % (1000 * 60)) / 1000);

                document.getElementById('days').innerText = days;
                document.getElementById('hours').innerText = hours;
                document.getElementById('minutes').innerText = minutes;
                document.getElementById('seconds').innerText = seconds;
            }
        }
        setInterval(updateCountdown, 1000);
        updateCountdown();

        async function initiatePayment() {
            const phone = document.getElementById('userPhone').value;
            const provider = document.getElementById('paymentMethod').value;
            const resultDiv = document.getElementById('paymentResult');

            if (!phone) {
                alert('Please enter your phone number.');
                return;
            }

            resultDiv.innerText = 'Creating PPV Pass order...';

            try {
                const response = await fetch('/api/ppv/initiate.php', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ phone_number: phone, provider: provider, event_code: 'ETFC_003' })
                });
                const res = await response.json();
                if (res.success) {
                    resultDiv.innerHTML = `<strong>Order ${res.data.order_ref} Created!</strong><br>${res.data.payment_instructions}`;
                } else {
                    resultDiv.innerText = res.message;
                }
            } catch (err) {
                resultDiv.innerText = 'Error connecting to server.';
            }
        }
    </script>
</body>
</html>
