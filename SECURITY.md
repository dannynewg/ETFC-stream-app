# Security

This project is under development and is not ready to process real payments or protect paid media. See the [review](docs/REVIEW.md) and [release blockers](docs/ROADMAP.md).

## Credentials

Never commit `.env`, `local.properties`, Firebase service accounts, real `google-services.json`, database passwords, provider keys, signing keystores or payment/customer records. The supplied Android Firebase example is synthetic and only suitable for build checks. Server secrets must never be packaged as Android BuildConfig values.

Migration removed embedded backend credentials and default web-admin passcodes. The owner must rotate historical database, webhook and signing secrets at the relevant service before reusing that hosted environment. Deleting a value from source does not invalidate it remotely. Existing local signing keys and configuration are ignored and were retained to avoid losing access to previous builds.

## Trust model

Firebase ID tokens and server-issued claims are authoritative. Client UI guards, flavor flags, cached roles, email comparisons, static hashes and `ETFC.*` local tokens are not a security boundary. Current Android identity/role handling needs migration before applying the restrictive rules baseline to existing data.

Payment webhooks fail closed with HTTP 503 until provider verification exists. Do not bypass this with a success flag. Storage is deny-all until a scoped policy is reviewed. Missing collection rules must not be fixed by enabling global public writes.

## Reporting

Use GitHub's private vulnerability reporting if the owner enables it. Otherwise contact the repository owner privately through their public GitHub profile to arrange a secure channel. Do not put exploit payloads, live keys, personal data or payment details in public issues. Ordinary sanitized bug reports can use the repository template.

There is no published security-support schedule or stable production release yet. Dependency updates and authorization tests should accompany each release candidate.
