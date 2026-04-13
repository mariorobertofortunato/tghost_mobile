# tghost mobile

Android app (Kotlin + Jetpack Compose) for multi-chain wallet tracking.

Supported ecosystems:
- Solana
- EVM chains (Ethereum, Base, ...)
- Sui
- Tezos

## Tech Stack

- Kotlin
- Jetpack Compose
- Coroutines + Flow
- Hilt (dependency injection)
- Ktor (HTTP/WebSocket)
- Room
- Android DataStore

## Project Structure

- `app` - UI, navigation, presentation wiring
- `domain` - business models, use cases, repository contracts
- `data` - repository implementations, persistence, shared networking infrastructure
- `chain/solana` - Solana-specific logic
- `chain/evm` - EVM-specific logic
- `chain/sui` - Sui-specific logic
- `chain/tezos` - Tezos-specific logic

## Architecture

This project adopts the **Horizon Pattern** for presentation and state orchestration.

- Architecture overview: `ARCHITECTURE.md`
- Horizon article: [The Horizon Pattern: an evolution beyond classic MVI](https://medium.com/@mariorobertofortunato/the-horizon-pattern-an-evolution-beyond-classic-mvi-in-kotlin-apps-with-jetpack-compose-43be5281713a)

### Technical Documentation Index

- `ARCHITECTURE.md` - layers, dependency rules, Horizon flow, extension guidelines
- `CONTRIBUTING.md` - workflow, coding/testing requirements, PR checklist
- `SECURITY.md` - vulnerability reporting policy and security scope
- `CODE_OF_CONDUCT.md` - community behavior standards

## Requirements

- Android Studio (latest stable recommended)
- JDK 21
- Android SDK configured in Android Studio

## Getting Started

1. Clone the repository.
2. Open the project in Android Studio.
3. Sync Gradle.
4. Build debug:

```bash
./gradlew :app:assembleDebug
```

5. Run unit tests:

```bash
./gradlew test
```

## Contributing Quick Start

If you want to contribute code, use the fork workflow:

1. Fork this repository on GitHub.
2. Clone your fork locally.
3. Create a feature branch from your local `main`.
4. Push the branch to your fork.
5. Open a pull request from your fork branch to this repository `main`.

Detailed contributor rules are in `CONTRIBUTING.md`.

## Configuration Notes

- Some providers require personal API keys (for example RPC providers).
- Configure secrets locally only.
- Keep environment-specific config out of version control.

## Security Notes

- Never commit secrets, API keys, private keys, seed phrases, or credentials.
- Avoid logging sensitive payloads in production builds.
- Prefer encrypted storage for user-provided secrets.
- Report vulnerabilities through the process described in `SECURITY.md`.

## Contributing

Please read `CONTRIBUTING.md` before opening a pull request.

## License

This project is released under the MIT License.
See `LICENSE` for details.
