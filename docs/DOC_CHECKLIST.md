# Documentation Accuracy Checklist

Use this checklist before releases or major refactors.

## README

- [ ] Badges point to the correct repo and workflows
- [ ] Clone URL is correct
- [ ] Prerequisites match current build requirements
- [ ] "How to run" steps work on a clean machine

## Architecture

- [ ] Module list and dependency direction are correct
- [ ] ADR index is up to date (docs/adr/README.md)

## Migrations

- [ ] Room schema version is current
- [ ] All migrations are registered in AppModule
- [ ] docs/MIGRATION_NOTES.md reflects latest changes

## KDoc

- [ ] Public APIs follow docs/KDOC_STANDARD.md
- [ ] New features include KDoc for public classes

## Onboarding

- [ ] New contributor can build and run the app in < 30 minutes
