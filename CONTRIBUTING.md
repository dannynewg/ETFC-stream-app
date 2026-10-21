# Contributing

1. Read README.md and docs/ROADMAP.md. Pick a scoped task and describe acceptance criteria in an issue.
2. Branch from `main`. Keep source changes, configuration examples and documentation consistent.
3. Build/test both Android flavors when touching shared Kotlin, resources, Gradle or auth behavior. Run functions lint/build for TypeScript changes and PHP syntax/security tests for backend changes.
4. Use provider sandboxes, Firebase emulators and synthetic data. Never test payment grants against real customer orders during development.
5. Open a pull request describing the concrete problem, resulting behavior, verification evidence and remaining limits. Link the roadmap task/issue.
6. Update docs/ROADMAP.md and CHANGELOG.md when acceptance criteria are met.

Follow existing Kotlin/Compose conventions, TypeScript lint configuration and PHP style. Do not add tests that only restate implementation; focus on authorization failures, data consistency and meaningful user behavior. Keep package-lock.json and the Gradle wrapper committed; exclude dependencies and generated outputs.

Do not weaken authorization to make a test or demo pass. Explicitly document simulations, failed checks and unavailable integrations. No source license has been chosen; contributors should confirm contribution and media rights with the owner.
