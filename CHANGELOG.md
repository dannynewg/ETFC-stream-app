# Changelog

## Unreleased — repository migration, 2026-10-21

- Reviewed Android fan/promoter flavors, Room/Firebase adapters, YouTube playback, PHP/MySQL prototype and Firebase functions.
- Replaced aspirational documentation with source-based feature status, architecture, setup, API reference, review and a task-based roadmap.
- Removed generated Android caches/build outputs, editor/assistant metadata, obsolete repair scripts, exact duplicate source posters and generic demo tests.
- Restored Gradle wrapper scripts/JAR and pinned the distribution checksum; switched debug signing to the standard generated debug key.
- Excluded real local configuration, environment values, signing material and generated dependencies from Git.
- Removed embedded backend credentials and demo admin passcodes; disabled unverified payment webhooks; reduced release network logging.
- Added restrictive Firebase rules baseline, missing rule/index files, synthetic Android Firebase config, CI, PHP security smoke tests and issue/PR templates.
- Documented remaining identity-contract, payment, playback, migration, privacy and deployment blockers. No production deployment or payment enablement is part of this change.
- Replaced the existing GitHub file tree while retaining private visibility and prior history, as requested by the owner.
- Fixed CI SDK setup to avoid a retired package. Source, PHP and Firebase checks passed on GitHub; Android verification is tracked separately in docs/REVIEW.md.
