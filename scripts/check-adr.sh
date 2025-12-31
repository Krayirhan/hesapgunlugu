#!/bin/bash
# ADR check - require ADR for architecture changes

set -e

MODE=${1:-auto}

if [ "$MODE" = "staged" ]; then
  CHANGED=$(git diff --cached --name-only)
  ADDED_ADR=$(git diff --cached --name-status | awk '$1 == "A" {print $2}' | grep -E '^docs/adr/ADR-.*\.md$' || true)
else
  if [ -n "$GIT_BASE_REF" ]; then
    git fetch origin "$GIT_BASE_REF" --depth=1 >/dev/null 2>&1 || true
    CHANGED=$(git diff --name-only "origin/$GIT_BASE_REF...HEAD")
    ADDED_ADR=$(git diff --name-only "origin/$GIT_BASE_REF...HEAD" | grep -E '^docs/adr/ADR-.*\.md$' || true)
  else
    CHANGED=$(git diff --name-only HEAD~1)
    ADDED_ADR=$(git diff --name-only HEAD~1 | grep -E '^docs/adr/ADR-.*\.md$' || true)
  fi
fi

if [ -z "$CHANGED" ]; then
  exit 0
fi

if echo "$CHANGED" | grep -E '(^core/|^app/|^feature/|build\.gradle\.kts$|settings\.gradle\.kts$|gradle/libs\.versions\.toml$|config/)' >/dev/null; then
  if [ -z "$ADDED_ADR" ]; then
    echo "ADR required: add docs/adr/ADR-*.md for architecture changes."
    exit 1
  fi
fi

exit 0
