CODE QUALITY PLAN

Detekt baseline reduction
- Rule: baseline must only shrink, never grow.
- Flow:
  1) New detekt issues are fixed immediately (maxIssues = 0).
  2) If baseline exists, remove one file per PR or reduce by at least 10 items.
  3) No new baseline entries are accepted.

Ktlint standard
- ktlintCheck must pass on PR and release.
- Format with: ./gradlew ktlintFormat

TODO/FIXME policy
- No TODO/FIXME in code.
- Tasks moved to backlog with clear owner and ETA.
