#!/bin/bash
# Install Git Hooks
# Run this script to set up pre-commit hooks

echo "🔧 Installing Git Hooks..."

# Make scripts executable
chmod +x scripts/pre-commit.sh
chmod +x scripts/boundary-guard.sh

# Copy pre-commit hook
if [ -f ".git/hooks/pre-commit" ]; then
    echo "⚠️  Existing pre-commit hook found"
    echo "Creating backup at .git/hooks/pre-commit.backup"
    mv .git/hooks/pre-commit .git/hooks/pre-commit.backup
fi

# Create pre-commit hook
cat > .git/hooks/pre-commit << 'EOF'
#!/bin/bash
# Auto-generated pre-commit hook
# Runs scripts/pre-commit.sh

./scripts/pre-commit.sh || exit 1
EOF

chmod +x .git/hooks/pre-commit

echo "✅ Git hooks installed successfully!"
echo ""
echo "The following checks will run before each commit:"
echo "  1. Architecture boundary validation"
echo "  2. Detekt code quality checks"
echo "  3. Unit tests (affected modules)"
echo "  4. Code style verification"
echo "  5. Build verification"
echo ""
echo "To bypass (NOT recommended): git commit --no-verify"
