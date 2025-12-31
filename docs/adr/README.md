# Architecture Decision Records (ADR)

This directory contains Architecture Decision Records for this project.

## What is an ADR?

An Architecture Decision Record (ADR) is a document that captures an important architectural decision made along with its context and consequences.

## Index

| ID | Title | Status | Date |
|----|-------|--------|------|
| [ADR-001](001-clean-architecture.md) | Clean Architecture | Accepted | 2024-12-01 |
| [ADR-002](002-hilt-dependency-injection.md) | Hilt for Dependency Injection | Accepted | 2024-12-01 |
| [ADR-003](003-room-database.md) | Room for Local Storage | Accepted | 2024-12-01 |
| [ADR-004](004-compose-ui.md) | Jetpack Compose for UI | Accepted | 2024-12-01 |
| [ADR-005](005-coroutines-flow.md) | Coroutines & Flow for Async | Accepted | 2024-12-01 |
| [ADR-006](006-no-firebase.md) | No Firebase/Cloud Database | Accepted | 2024-12-24 |
| [ADR-008](ADR-008-single-session-and-boundaries.md) | Single Session Source and Boundary Enforcement | Accepted | 2025-01-01 |

## Template

Use the following template for new ADRs:

```markdown
# ADR-XXX: Title

## Status
Proposed | Accepted | Deprecated | Superseded

## Context
What is the issue that we're seeing that is motivating this decision or change?

## Decision
What is the change that we're proposing and/or doing?

## Consequences
What becomes easier or more difficult to do because of this change?
```

