<?php
// Fail closed until provider verification and reconciliation are implemented.
require_once __DIR__ . "/../../config/config.php";
setCorsHeaders();
header("Content-Type: application/json");
http_response_code(503);
echo json_encode(["success" => false, "message" => "Payment verification is not configured."]);
