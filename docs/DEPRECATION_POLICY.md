# Deprecation Policy

This policy defines how APIs are deprecated and removed.

## Goals

- Provide safe migration paths.
- Avoid breaking changes without notice.
- Keep public APIs clean and discoverable.

## Stages

1) Announce
   - Add KDoc: "Deprecated: use X instead."
   - Add `@Deprecated` with `ReplaceWith` when possible.
   - Document the migration in `docs/MIGRATION_NOTES.md`.

2) Grace Period
   - Minimum: 2 releases or 60 days (whichever is longer).
   - Maintain compatibility and tests.

3) Removal
   - Remove deprecated API after the grace period.
   - Update docs and changelog.

## Requirements

- Every deprecation must include:
  - Migration path
  - Timeline
  - Owner
- Breaking changes must be called out in `CHANGELOG.md`.
