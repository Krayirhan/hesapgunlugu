#!/bin/bash
# Pre-commit Hook for MyNewApp
# This script runs before every commit to ensure code quality

set -e

echo ">> Running pre-commit checks..."
echo "================================"

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

FAILED=0

# Check 1: Architecture Boundary Audit
echo ""
echo "1/8: Architecture Boundary Check..."
if ./scripts/boundary-guard.sh; then
    echo -e "${GREEN}OK: Boundary check passed${NC}"
else
    echo -e "${RED}FAIL: Boundary check failed${NC}"
    FAILED=1
fi

# Check 2: Detekt Lint
echo ""
echo "2/8: Running Detekt..."
if ./gradlew detekt --daemon > /dev/null 2>&1; then
    echo -e "${GREEN}OK: Detekt passed${NC}"
else
    echo -e "${RED}FAIL: Detekt found issues${NC}"
    echo -e "${YELLOW}Run: ./gradlew detekt for details${NC}"
    FAILED=1
fi

# Check 3: Unit Tests (Fast - Affected Modules Only)
echo ""
echo "3/8: Running Unit Tests..."
CHANGED_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep '\.kt$' || true)

if [ -n "$CHANGED_FILES" ]; then
    AFFECTED_MODULES=""

    if echo "$CHANGED_FILES" | grep -q "feature/home"; then
        AFFECTED_MODULES="$AFFECTED_MODULES :feature:home:testDebugUnitTest"
    fi

    if echo "$CHANGED_FILES" | grep -q "feature/settings"; then
        AFFECTED_MODULES="$AFFECTED_MODULES :feature:settings:testDebugUnitTest"
    fi

    if echo "$CHANGED_FILES" | grep -q "feature/history"; then
        AFFECTED_MODULES="$AFFECTED_MODULES :feature:history:testDebugUnitTest"
    fi

    if echo "$CHANGED_FILES" | grep -q "core/domain"; then
        AFFECTED_MODULES="$AFFECTED_MODULES :core:domain:testDebugUnitTest"
    fi

    if echo "$CHANGED_FILES" | grep -q "core/data"; then
        AFFECTED_MODULES="$AFFECTED_MODULES :core:data:testDebugUnitTest"
    fi

    if [ -n "$AFFECTED_MODULES" ]; then
        if ./gradlew $AFFECTED_MODULES --daemon --continue > /dev/null 2>&1; then
            echo -e "${GREEN}OK: Unit tests passed${NC}"
        else
            echo -e "${RED}FAIL: Unit tests failed${NC}"
            echo -e "${YELLOW}Run: ./gradlew testDebugUnitTest for details${NC}"
            FAILED=1
        fi
    else
        echo -e "${GREEN}OK: No testable modules affected${NC}"
    fi
else
    echo -e "${GREEN}OK: No Kotlin files changed${NC}"
fi

# Check 4: KtLint Format Check
echo ""
echo "4/8: Checking Kotlin code style..."
if ./gradlew ktlintCheck --daemon > /dev/null 2>&1; then
    echo -e "${GREEN}OK: KtLint passed${NC}"
else
    echo -e "${RED}FAIL: KtLint issues found${NC}"
    echo -e "${YELLOW}Run: ./gradlew ktlintFormat${NC}"
    FAILED=1
fi

# Check 5: TODO/FIXME Check
echo ""
echo "5/8: Checking TODO/FIXME..."
if rg -n "TODO|FIXME" app core feature --glob '!**/build/**' > /dev/null 2>&1; then
    echo -e "${RED}FAIL: TODO/FIXME found in code${NC}"
    rg -n "TODO|FIXME" app core feature --glob '!**/build/**'
    FAILED=1
else
    echo -e "${GREEN}OK: No TODO/FIXME in code${NC}"
fi

# Check 6: Secret Files Check
echo ""
echo "6/8: Checking for secrets and google-services.json..."
if git diff --cached --name-only | grep -E "(^|/)(google-services\.json)$" > /dev/null 2>&1; then
    echo -e "${RED}FAIL: google-services.json must stay local and untracked${NC}"
    FAILED=1
fi

if rg -n "api_key|API_KEY|password|secret" app/src/main --glob "*.kt" --glob "*.xml" 2>/dev/null | rg -v "BuildConfig|strings.xml" > /dev/null 2>&1; then
    echo -e "${RED}FAIL: Potential secrets found in source code${NC}"
    rg -n "api_key|API_KEY|password|secret" app/src/main --glob "*.kt" --glob "*.xml" | rg -v "BuildConfig|strings.xml"
    FAILED=1
else
    echo -e "${GREEN}OK: No obvious secrets detected${NC}"
fi

# Check 7: ADR Required Check
echo ""
echo "7/8: Checking ADR requirement..."
if ./scripts/check-adr.sh staged; then
    echo -e "${GREEN}OK: ADR check passed${NC}"
else
    echo -e "${RED}FAIL: ADR check failed${NC}"
    FAILED=1
fi

# Check 8: Build Verification (Quick Debug Build)
echo ""
echo "8/8: Quick build verification..."
if ./gradlew assembleFreeDebug --daemon --quiet > /dev/null 2>&1; then
    echo -e "${GREEN}OK: Build verification passed${NC}"
else
    echo -e "${RED}FAIL: Build failed${NC}"
    echo -e "${YELLOW}Run: ./gradlew assembleFreeDebug for details${NC}"
    FAILED=1
fi

# Final Result
echo ""
echo "================================"
if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}OK: All pre-commit checks passed${NC}"
    echo "Proceeding with commit..."
    exit 0
else
    echo -e "${RED}FAIL: Pre-commit checks failed${NC}"
    echo ""
    echo "Fix the issues above before committing."
    echo "To bypass (NOT recommended): git commit --no-verify"
    exit 1
fi
