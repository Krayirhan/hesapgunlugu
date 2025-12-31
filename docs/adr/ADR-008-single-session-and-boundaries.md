# ADR-008: Single Session Source and Boundary Enforcement

Date: 2025-01-01
Status: Accepted

Context
- Session state existed in both SecurityManager and SessionManager.
- Boundary checks referenced a placeholder package and were not enforced in CI.
- Architecture changes lacked a mandatory ADR check.

Decision
- Consolidate session handling under SecurityManager as the single source of truth.
- Enforce module boundaries (feature -> domain, no feature -> data).
- Require ADR for architecture-impacting changes.

Consequences
- SessionManager removed in favor of SecurityManager API.
- Boundary guard checks are aligned with the real package namespace.
- CI will block architecture changes without an ADR.
