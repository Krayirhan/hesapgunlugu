# Maintainability Metrics

This document defines how to track maintainability.

## Bug Rate

Definition: bugs per change.

- Numerator: count of confirmed bug tickets.
- Denominator: count of merged changes in the same time window.

## MTTR (Mean Time To Repair)

Definition: average time from bug open to fix merged.

Formula:

```
MTTR = sum(fix_time_hours) / bug_count
```

## Data Source

Track in `docs/metrics/bug_metrics.csv` and update weekly.

Required columns:
- date
- period
- changes
- bugs
- bug_rate
- mttr_hours

## Targets

- Bug rate trending down release-over-release
- MTTR < 48 hours
