# Tech Debt Board

Single source of truth for technical debt tracking.
Every entry must have an owner and ETA.

## Rules

- One owner per item.
- ETA is required and must be realistic.
- Update status weekly.
- Close items by linking PRs.

## Board

| ID | Area | Description | Owner | ETA | Priority | Status | Link |
|----|------|-------------|-------|-----|----------|--------|------|
| TD-0001 | docs | Fix README badges/clone links (done in repo init) | @krayirhan | 2025-01-05 | P3 | done | - |
| TD-0002 | ownership | Replace CODEOWNERS placeholders | @krayirhan | 2025-01-05 | P2 | done | - |
| TD-0003 | lint | Turn lint to error-on for release/CI | @krayirhan | 2025-02-01 | P1 | planned | - |
| TD-0004 | detekt | Detekt error-on + baseline reduction plan | @krayirhan | 2025-02-15 | P1 | planned | - |
| TD-0005 | security | Production pinning with real pins | @krayirhan | 2025-02-15 | P1 | planned | - |
| TD-0006 | testing | Coverage gates (domain/data/UI) | @krayirhan | 2025-03-01 | P1 | planned | - |
| TD-0007 | perf | Macrobenchmark CI gate + targets | @krayirhan | 2025-03-01 | P2 | planned | - |

## Status Legend

- planned
- in-progress
- blocked
- done
