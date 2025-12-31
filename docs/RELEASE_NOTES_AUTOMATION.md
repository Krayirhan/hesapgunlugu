# Release Notes Automation

This project uses `CHANGELOG.md` as the source of truth.
Release notes are generated from the latest version section.

## Script

Use `scripts/generate-release-notes.ps1` to extract the latest release notes.

### Examples

```powershell
.\scripts\generate-release-notes.ps1 -ChangelogPath CHANGELOG.md
```

## Rules

- Keep `CHANGELOG.md` current.
- Each release must have a versioned section.
- Unreleased changes must move into a version before tagging.
