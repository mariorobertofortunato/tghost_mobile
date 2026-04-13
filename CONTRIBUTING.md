# Contributing to tghost mobile

Thanks for contributing.
This document defines workflow, quality gates, and security expectations.

## Ground Rules

- Be respectful and constructive.
- Keep changes focused and easy to review.
- Prefer small pull requests over mixed large changes.
- Follow `CODE_OF_CONDUCT.md`.

## Before You Start

1. Check existing issues/PRs to avoid duplicate work.
2. Open an issue for substantial changes (architecture, data model, security-sensitive logic).
3. Align on scope and acceptance criteria before implementation.

## Development Setup

1. Install Android Studio and JDK 21.
2. Clone and open the project.
3. Sync Gradle.
4. Build locally:

```bash
./gradlew :app:assembleDebug
```

## Architecture Alignment

- Read `ARCHITECTURE.md` before changing presentation flows.
- Keep `Event / State / Effect` contracts explicit.
- Do not move business rules into composables.
- Keep chain-specific logic in `chain/*`.

## Branch and Commit Guidelines

Suggested branch naming:

- `feat/<short-description>`
- `fix/<short-description>`
- `docs/<short-description>`
- `chore/<short-description>`

Commit message style:

```text
type(scope): short summary
```

Examples:
- `feat(solana): add rpc fallback resolver`
- `fix(data): map network timeout to domain failure`
- `docs(architecture): document horizon event flow`

## Testing and Validation

Before opening a PR, run:

```bash
./gradlew test
./gradlew :app:assembleDebug
```

If UI/network behavior changed, include concise manual test notes in the PR.

## Security Requirements

Never commit:

- API keys
- private keys
- seed phrases
- passwords/tokens/credentials

Additional requirements:

- avoid sensitive logging in production paths
- prefer encrypted storage for user-provided secrets
- if networking/auth/persistence is touched, include a security impact note in the PR

If you discover a vulnerability, do not open a public issue with exploit details.
Report privately as described in `SECURITY.md`.

## Pull Request Checklist

- [ ] Scope is focused and documented.
- [ ] Build and tests pass locally.
- [ ] No secrets/credentials are included.
- [ ] Documentation is updated when relevant.
- [ ] PR description explains why the change is needed.
- [ ] Security impact is described for sensitive changes.
