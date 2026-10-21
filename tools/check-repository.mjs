// Source hygiene check, not a replacement for a dedicated secret scanner.
import fs from 'node:fs';
import path from 'node:path';
import {execFileSync} from 'node:child_process';

const root = process.cwd();
const git = (...args) => execFileSync('git', ['-c', `safe.directory=${root.replaceAll('\\', '/')}`, ...args], {encoding: 'utf8'});
const files = git('ls-files', '-z').split('\0').filter(Boolean);
if (!files.length) throw new Error('Stage the publication files before running this check.');
const forbidden = /(^|\/)(node_modules|build|\.gradle|\.kotlin|\.idea|\.antigravity|\.tools)\/|(^|\/)(\.env|local\.properties|google-services\.json)$|\.(keystore|jks|apk|aab|pem|p12)$/i;
const secretPatterns = [
  /-----BEGIN (?:RSA |EC |OPENSSH )?PRIVATE KEY-----/,
  /(?:ghp_|github_pat_)[A-Za-z0-9_]{20,}/,
  /AIza[0-9A-Za-z_-]{35}/,
  /(?:DB_PASS|JWT_SECRET_KEY|CHAPA_WEBHOOK_SECRET)['"]?\s*,\s*['"][^'"]{6,}['"]/,
];
const errors = [];
let jsonCount = 0;
let linkCount = 0;
for (const file of files) {
  if (forbidden.test(file)) errors.push(`${file}: generated or private file is staged`);
  if (/\.(jpg|png|webp|jar)$/i.test(file)) continue;
  const text = fs.readFileSync(file, 'utf8');
  if (file.endsWith('.json')) {
    try { JSON.parse(text); jsonCount++; }
    catch { errors.push(`${file}: invalid JSON`); }
  }
  if (!file.includes('.example.')) {
    text.split(/\r?\n/).forEach((line, i) => {
      if (secretPatterns.some(pattern => pattern.test(line))) errors.push(`${file}:${i + 1}: possible credential (value suppressed)`);
    });
  }
  if (file.endsWith('.md')) {
    for (const match of text.matchAll(/\[[^\]]*\]\(([^)]+)\)/g)) {
      const target = match[1].split('#')[0];
      if (!target || /^[a-z]+:/i.test(target)) continue;
      linkCount++;
      if (!fs.existsSync(path.resolve(path.dirname(file), decodeURIComponent(target)))) errors.push(`${file}: broken local link ${target}`);
    }
  }
}
if (errors.length) {
  console.error(errors.join('\n'));
  process.exitCode = 1;
} else {
  console.log(`PASS: ${files.length} publication files, ${jsonCount} JSON files, ${linkCount} local documentation links; no forbidden files or configured credential patterns.`);
}
