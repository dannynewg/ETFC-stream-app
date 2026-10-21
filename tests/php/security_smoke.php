<?php
// No database, Firebase project or provider credentials are needed.
function check(bool $condition, string $message): void {
    if (!$condition) {
        throw new RuntimeException($message);
    }
}

$root = dirname(__DIR__, 2);
foreach (['chapa', 'telebirr'] as $provider) {
    $path = $root . '/cpanel_backend/api/webhooks/' . $provider . '.php';
    // Separate processes avoid re-defining configuration constants.
    $code =
        '$_SERVER["REQUEST_METHOD"] = "POST"; ' .
        'ob_start(); include ' . var_export($path, true) . '; ' .
        '$body = ob_get_clean(); echo json_encode([http_response_code(), json_decode($body, true)]);';
    $process = proc_open([PHP_BINARY, '-r', $code], [1 => ['pipe', 'w'], 2 => ['pipe', 'w']], $pipes);
    check(is_resource($process), 'PHP subprocess must start');
    $output = stream_get_contents($pipes[1]);
    $errors = stream_get_contents($pipes[2]);
    fclose($pipes[1]);
    fclose($pipes[2]);
    check(proc_close($process) === 0, $errors);
    $result = json_decode($output ?: '', true);
    check(is_array($result) && $result[0] === 503, $provider . ' must fail closed');
    check($result[1]['success'] === false, $provider . ' must not grant a pass');
}
putenv('SUPERADMIN_EMAIL');
putenv('ADMIN_PASSWORD_HASH');
putenv('DB_PASS');
require $root . '/cpanel_backend/config/config.php';
check(SUPERADMIN_EMAIL === '' && ADMIN_PASSWORD_HASH === '', 'No default admin login');
check(DB_PASS === '', 'No default database password');
echo "PASS: callbacks fail closed and privileged credentials have no defaults.\n";
