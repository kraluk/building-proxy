# Repository Guidelines

## Project Structure & Module Organization
- Source: `src/main/kotlin/io/kraluk/buildingproxy` organized by a Clean Architecture flavor:
  - `configuration/`, `shared/`, `adapter/`, `usecase/`, `domain/`.
- Resources: `src/main/resources` with Spring profiles (`application.yaml`, `application-local.yaml`).
- Tests: unit tests in `src/test/kotlin`; integration tests in `src/testIntegration/kotlin` with resources (WireMock mappings) in `src/testIntegration/resources`.
- Docs: diagrams in `docs/diagrams/`.

## Build, Test, and Development Commands
- `./gradlew build`: compile and assemble the JAR.
- `./gradlew test`: run unit tests (JUnit 5).
- `./gradlew testIntegration`: run integration tests (Testcontainers, WireMock).
- `./gradlew check`: run all verifications (includes tests and JaCoCo report).
- `./gradlew ktlintCheck | ktlintFormat`: check/auto-format Kotlin style.
- `./gradlew detekt`: static analysis using `detekt.yml`.
- `./gradlew dependencyUpdates`: view available dependency upgrades.
- Local run: `docker compose up` (WireMock + Redis), then `./gradlew bootRun --args='--spring.profiles.active=local'`.

## Coding Style & Naming Conventions
- Kotlin, Java 21, Spring Boot 3. Indentation: 2 spaces; max line length: 140 (`.editorconfig`).
- Avoid star imports; prefer explicit imports (ktlint configured).
- Packages: lowercase; classes: PascalCase; functions/props: camelCase.
- Suffix patterns: `*Controller`, `*Repository`, `*UseCase` aligned with existing packages.

## Testing Guidelines
- Frameworks: JUnit 5, Kluent, Mockito, Awaitility; integration via Testcontainers and WireMock.
- Unit tests in `src/test/kotlin/**/*Test.kt`; integration tests in `src/testIntegration/kotlin/**/*Test.kt`.
- Coverage: JaCoCo; report under `build/reports/jacoco` (via `check`).
- Prefer deterministic unit tests; for integration, pin container images in `compose.yml` and Testcontainers.

## Commit & Pull Request Guidelines
- Conventional commits observed (e.g., `chore(deps): bump spring boot to 3.5.4`). Use `feat|fix|chore|test|docs` with optional scopes.
- PRs: clear description, linked issues, before/after notes, and test evidence (logs or screenshots for `/swagger-ui/index.html`).
- Keep changes focused; include config snippets for new profiles/properties.

## Security & Configuration Tips
- Do not commit secrets; configure via Spring properties (`application-*.yaml`) or environment variables.
- For Testcontainers with Colima, set `DOCKER_HOST` and `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE` as in `README.md`.

