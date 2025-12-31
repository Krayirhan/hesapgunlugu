# HesapGunlugu - Finance Tracker

[![CI](https://github.com/krayirhan/hesapgunlugu/actions/workflows/ci.yml/badge.svg)](https://github.com/krayirhan/hesapgunlugu/actions/workflows/ci.yml)
[![Quality Gate](https://github.com/krayirhan/hesapgunlugu/actions/workflows/quality-gate.yml/badge.svg)](https://github.com/krayirhan/hesapgunlugu/actions/workflows/quality-gate.yml)
[![Architecture Audit](https://github.com/krayirhan/hesapgunlugu/actions/workflows/architecture-audit.yml/badge.svg)](https://github.com/krayirhan/hesapgunlugu/actions/workflows/architecture-audit.yml)
[![Coverage](https://codecov.io/gh/krayirhan/hesapgunlugu/branch/main/graph/badge.svg)](https://codecov.io/gh/krayirhan/hesapgunlugu)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=26)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Modern Android finance tracking app built with Jetpack Compose and clean, modular architecture.
This README is the single source of truth for run, contribution, and architecture guidance.

## Contents

- Features
- Architecture Overview
- How to Run
- How to Contribute
- Documentation
- Testing
- License

## Features

- Income/expense tracking with categories and budgets
- Scheduled payments and recurring transactions
- Statistics and charts
- App lock (PIN + biometrics)
- Encrypted local backup and restore
- Accessibility-first UI

## Architecture Overview

- Multi-module structure with clear boundaries
- Clean Architecture layers: presentation -> domain -> data
- Dependency flow enforced at build time

Key docs:
- ADRs: `docs/adr/README.md`
- Architecture guide: `docs/MULTI_MODULE_GUIDE.md`

## How to Run

### Prerequisites

- Android Studio Hedgehog (or newer)
- JDK 17
- Android SDK 35

### Setup

```bash
# Clone
git clone https://github.com/krayirhan/hesapgunlugu.git

# Open in Android Studio
# Sync Gradle
```

If needed, copy secrets template:

```bash
cp secrets.properties.template secrets.properties
```

### Run

```bash
./gradlew :app:installDebug
```

## How to Contribute

Read `docs/CONTRIBUTING.md` and follow the steps.

Quick path:

1. Fork the repo
2. Create a feature branch
3. Run `./gradlew detekt` and tests
4. Open a PR

## Documentation

- Contribution guide: `docs/CONTRIBUTING.md`
- KDoc standard: `docs/KDOC_STANDARD.md`
- Migration notes: `docs/MIGRATION_NOTES.md`
- ADR index: `docs/adr/README.md`
- Doc checklist: `docs/DOC_CHECKLIST.md`

## Testing

```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumentation tests
./gradlew connectedDebugAndroidTest

# Static analysis
./gradlew detekt
```

## License

MIT. See `LICENSE`.
