# KDoc Standard

This document defines the KDoc standard for the project. The goal is consistent,
useful documentation for public APIs and reusable modules.

## Scope

Write KDoc for:
- public classes, interfaces, objects, and enums
- public functions and extension functions
- public properties with non-obvious behavior
- public constructors with non-trivial parameters

Skip KDoc for:
- private/internal symbols that are self-explanatory
- trivial getters/setters

## Structure

Use the following structure:
1) One-line summary
2) Optional detail paragraph
3) Tags: @param, @return, @throws, @sample, @see (only when relevant)

## Style

- Keep the summary short and action-focused.
- Prefer active voice.
- Use backticks for code identifiers.
- Mention side effects and threading when it matters.

## Examples

```kotlin
/**
 * Imports a backup payload and replaces local data in a single transaction.
 *
 * Validates all records before any write occurs to avoid partial imports.
 *
 * @param payload parsed backup payload
 * @param replaceExisting when true, clears existing data first
 * @return summary of imported records
 * @throws IllegalArgumentException if validation fails
 * @sample com.hesapgunlugu.app.core.backup.import.BackupImporter.importFromJsonSummary
 */
suspend fun importFromJsonSummary(
    payload: BackupPayload,
    replaceExisting: Boolean
): BackupImportSummary
```

```kotlin
/**
 * Calculates monthly spending trend using the given date range.
 *
 * @param startMillis inclusive start timestamp (UTC millis)
 * @param endMillis inclusive end timestamp (UTC millis)
 * @return list of daily totals
 */
fun calculateTrend(startMillis: Long, endMillis: Long): List<DailyTotal>
```

## Review Checklist

- Summary explains the "what" in one sentence.
- Params and return values are documented if non-obvious.
- Threading, side effects, or transaction boundaries are noted.
- Examples compile or reference a real sample.
