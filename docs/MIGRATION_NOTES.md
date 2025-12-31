# Migration Notes

This file is the single source for migration rules and current versions.
Update it whenever a schema or backup format changes.

## Database Schema

- Current Room schema version: 8
- Migrations are defined in:
  - `core/data/src/main/java/com/hesapgunlugu/app/core/data/local/DatabaseMigrations.kt`
  - `core/data/src/main/java/com/hesapgunlugu/app/core/data/local/AppDatabase.kt`
- Migrations are registered in:
  - `app/src/main/java/com/hesapgunlugu/app/di/AppModule.kt`

### Existing Migrations

- 1 -> 2: scheduled payments table
- 2 -> 3: `emoji` column on transactions
- 3 -> 4: performance indexes
- 4 -> 5: recurring transactions table
- 5 -> 6: notifications table
- 6 -> 7: recurring rules table
- 7 -> 8: `scheduledPaymentId` + unique index for idempotency

### How to Add a New Migration

1) Bump `version` in `AppDatabase`.
2) Add a `Migration` in `DatabaseMigrations`.
3) Register it in `AppModule` with `.addMigrations(...)`.
4) Update or add migration tests in:
   `app/src/androidTest/java/com/hesapgunlugu/app/data/local/MigrationTest.kt`
5) Update this file.

## Backup Format

- Current backup format version: 3
- Minimum supported version: 1
- Version is stored in `BackupData.version` and validated in import.
- Serialization is implemented in:
  - `core/backup/src/main/java/com/hesapgunlugu/app/core/backup/serialization/BackupSerializer.kt`

### Backup Format Rules

- Increment the format version on breaking changes.
- Add forward/backward compatibility tests for each version.
- Document any new required fields or defaults here.

## Checklist

- [ ] Schema version updated
- [ ] Migration added and registered
- [ ] Migration tests updated
- [ ] Backup format version updated (if needed)
- [ ] This file updated
