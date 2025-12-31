#!/bin/bash
# Boundary Guard - Clean Architecture Enforcement
# Run before commit to ensure architectural boundaries are respected

set -e

echo "🔍 Architecture Boundary Audit"
echo "================================"

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

VIOLATIONS=0

# Check 1: Feature → Data import
echo -n "📦 Feature → Data import check... "
if grep -r "import com\.hesapgunlugu\.app\.core\.data" feature/**/*.kt 2>/dev/null; then
    echo -e "${RED}❌ FAILED${NC}"
    echo -e "${RED}Feature modules are importing core.data classes!${NC}"
    echo "Violation: Feature modules should only depend on core:domain"
    echo ""
    VIOLATIONS=$((VIOLATIONS + 1))
else
    echo -e "${GREEN}✅ PASS${NC}"
fi

# Check 2: Feature → Data dependency
echo -n "📦 Feature → Data dependency check... "
if grep -r 'project(":core:data")' feature/**/build.gradle.kts 2>/dev/null; then
    echo -e "${RED}❌ FAILED${NC}"
    echo -e "${RED}Feature modules have core:data dependency!${NC}"
    echo "Violation: Feature modules should only depend on core:domain"
    echo ""
    VIOLATIONS=$((VIOLATIONS + 1))
else
    echo -e "${GREEN}✅ PASS${NC}"
fi

# Check 3: Room schema directory
echo -n "📂 Room schema directory check... "
if [ ! -d "core/data/schemas" ]; then
    echo -e "${RED}❌ FAILED${NC}"
    echo -e "${RED}Room schema directory missing!${NC}"
    echo "Run: ./gradlew :core:data:kspDebugKotlin"
    echo ""
    VIOLATIONS=$((VIOLATIONS + 1))
else
    echo -e "${GREEN}✅ PASS${NC}"
fi

# Check 4: Single NavHost
echo -n "🧭 Single NavHost check... "
NAVHOST_COUNT=$(rg -g "*.kt" "NavHost\(" app 2>/dev/null | wc -l | tr -d " ")
if [ "$NAVHOST_COUNT" -ne 1 ]; then
    echo -e "${RED}❌ FAILED ($NAVHOST_COUNT NavHost found)${NC}"
    echo -e "${RED}Should have exactly 1 NavHost!${NC}"
    echo "Violation: Multiple navigation sources detected"
    echo ""
    VIOLATIONS=$((VIOLATIONS + 1))
else
    echo -e "${GREEN}✅ PASS${NC}"
fi

# Check 5: Domain → Data check (only androidTest allowed)
echo -n "🔬 Domain → Data test check... "
DOMAIN_MAIN_VIOLATIONS=$(grep -r "import com\.hesapgunlugu\.app\.core\.data" core/domain/src/main/**/*.kt 2>/dev/null | wc -l | tr -d ' ')
if [ "$DOMAIN_MAIN_VIOLATIONS" -gt 0 ]; then
    echo -e "${RED}❌ FAILED${NC}"
    echo -e "${RED}Domain main code importing core.data!${NC}"
    echo "Violation: Only androidTest can import core.data"
    grep -r "import com\.hesapgunlugu\.app\.core\.data" core/domain/src/main/**/*.kt 2>/dev/null
    echo ""
    VIOLATIONS=$((VIOLATIONS + 1))
else
    echo -e "${GREEN}✅ PASS${NC}"
fi

# Check 6: No circular dependencies
echo -n "🔄 Circular dependency check... "
# Check if data depends on feature modules (should never happen)
DATA_TO_FEATURE=$(grep -r 'project(":feature:' core/data/build.gradle.kts 2>/dev/null | wc -l | tr -d ' ')
if [ "$DATA_TO_FEATURE" -gt 0 ]; then
    echo -e "${RED}❌ FAILED${NC}"
    echo -e "${RED}Data layer depending on feature modules!${NC}"
    echo "Violation: Circular dependency detected"
    echo ""
    VIOLATIONS=$((VIOLATIONS + 1))
else
    echo -e "${GREEN}✅ PASS${NC}"
fi

# Check 7: UI module usage
echo -n "🎨 Core:UI usage check... "
# Ensure features use core:ui correctly
FEATURE_WITHOUT_UI=$(find feature -name "build.gradle.kts" -exec grep -L 'project(":core:ui")' {} \; 2>/dev/null | wc -l | tr -d ' ')
if [ "$FEATURE_WITHOUT_UI" -gt 0 ]; then
    echo -e "${YELLOW}⚠️ WARNING${NC}"
    echo -e "${YELLOW}Some feature modules don't depend on core:ui${NC}"
    echo "This may be intentional for non-UI features"
    echo ""
else
    echo -e "${GREEN}✅ PASS${NC}"
fi

# Final Result
echo ""
echo "================================"
if [ $VIOLATIONS -eq 0 ]; then
    echo -e "${GREEN}✅ All boundary checks passed!${NC}"
    echo ""
    exit 0
else
    echo -e "${RED}❌ Found $VIOLATIONS boundary violation(s)!${NC}"
    echo ""
    echo "Fix the violations above before committing."
    echo ""
    echo "Clean Architecture Rules:"
    echo "  • feature → core:domain (✅ allowed)"
    echo "  • feature → core:ui (✅ allowed)"
    echo "  • feature → core:data (❌ forbidden)"
    echo "  • core:data → core:domain (✅ allowed)"
    echo "  • core:domain → core:data (❌ forbidden, except androidTest)"
    echo ""
    exit 1
fi
