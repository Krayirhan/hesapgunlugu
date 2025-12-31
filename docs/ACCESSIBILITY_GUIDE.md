# ♿ Accessibility Implementation Guide

## 📋 Overview

Bu proje **WCAG 2.1 Level AA** standartlarına uygun accessibility özellikleri içermektedir:
- ✅ TalkBack screen reader desteği
- ✅ Semantic descriptions
- ✅ Contrast ratio validation (4.5:1 minimum)
- ✅ Font scaling support (0.85x - 2.0x+)
- ✅ Touch target sizing (48dp minimum)
- ✅ Keyboard navigation

---

## 🎨 Accessibility Utilities

### 1️⃣ AccessibilityModifiers

**Location:** `core:ui/accessibility/AccessibilityModifiers.kt`

#### `accessibleClickable()`
Clickable element'lere semantic description ekler:

```kotlin
Icon(
    imageVector = Icons.Default.Add,
    modifier = Modifier.accessibleClickable(
        label = "Yeni işlem ekle",
        stateDescription = "Yeni işlem ekleme ekranını açar"
    )
)
```

#### `accessibleHeading()`
Heading'leri işaretler (TalkBack navigation):

```kotlin
Text(
    text = "İstatistikler",
    modifier = Modifier.accessibleHeading(),
    style = MaterialTheme.typography.headlineMedium
)
```

#### `accessibleToggle()`
Switch/Checkbox için state information:

```kotlin
Switch(
    checked = enabled,
    modifier = Modifier.accessibleToggle(
        label = "Biyometrik kilit",
        isChecked = enabled
    )
)
```

#### `accessibleValue()`
Slider/Progress için value information:

```kotlin
Slider(
    value = budget,
    modifier = Modifier.accessibleValue(
        label = "Aylık bütçe",
        currentValue = "${budget.toInt()} TL",
        valueRange = "0 TL - 50000 TL"
    )
)
```

---

### 2️⃣ ColorAccessibility

**Location:** `core:ui/accessibility/ColorAccessibility.kt`

#### Contrast Ratio Checking

```kotlin
val foreground = Color(0xFF3B82F6)
val background = Color(0xFFFFFFFF)

// Check WCAG AA compliance
val meetsAA = foreground.meetsContrastAA(background) // 4.5:1
val meetsAAA = foreground.meetsContrastAAA(background) // 7:1

// Calculate ratio
val ratio = foreground.contrastRatio(background) // Returns 3.2, 4.5, etc.
```

#### Readable Text Color

```kotlin
val backgroundColor = Color(0xFF1E293B)
val textColor = backgroundColor.readableTextColor() // Returns White or Black
```

#### Theme Validation

```kotlin
@Composable
fun MyScreen() {
    val report = validateThemeAccessibility()
    
    if (!report.isAccessible) {
        report.issues.forEach { issue ->
            Log.w("A11y", issue)
        }
    }
}
```

---

### 3️⃣ FontScaling

**Location:** `core:ui/accessibility/FontScaling.kt`

#### Adaptive Font Sizes

```kotlin
@Composable
fun MyText() {
    val fontScale = LocalDensity.current.fontScale
    
    Text(
        text = "Title",
        fontSize = 24.sp.adaptive(fontScale) // Dampens at very large scales
    )
}
```

#### Font Scale Categories

```kotlin
val fontScale = LocalDensity.current.fontScale
val category = FontScaleCategory.fromScale(fontScale)

when (category) {
    SMALL -> // 0.85x - 1.0x
    NORMAL -> // 1.0x - 1.15x
    LARGE -> // 1.15x - 1.3x
    EXTRA_LARGE -> // 1.3x - 2.0x
    ACCESSIBILITY -> // 2.0x+
}
```

#### Adaptive Spacing

```kotlin
Spacer(
    modifier = Modifier.height(
        16.dp.adaptiveSpacing(fontScale)
    )
)
```

#### Touch Targets

```kotlin
val buttonSize = 40.dp.ensureMinimumTouchTarget() // Returns 48.dp
```

---

### 4️⃣ AccessibleTheme

**Location:** `core:ui/theme/AccessibleTheme.kt`

```kotlin
AccessibleTheme(darkTheme = isDarkTheme) {
    HesapGunluguTheme(darkTheme = isDarkTheme) {
        // App content
    }
}
```

**Benefits:**
- Provides font scale via `LocalFontScale`
- Provides accessibility state via `LocalAccessibilityEnabled`
- Wraps theme with accessibility context

---

## 🧪 Testing Accessibility

### Preview with Font Scales

```kotlin
@Preview(fontScale = 1.0f, name = "Normal")
@Preview(fontScale = 1.5f, name = "Large")
@Preview(fontScale = 2.0f, name = "Extra Large")
@Composable
fun MyComponentPreview() {
    FontScaleTestComponent {
        MyComponent()
    }
}
```

### Contrast Testing

```kotlin
@Preview
@Composable
fun ContrastPreview() {
    ContrastTestComponent(
        foreground = MaterialTheme.colorScheme.primary,
        background = MaterialTheme.colorScheme.background
    ) {
        MyComponent()
    }
}
```

### UI Testing

```kotlin
@Test
fun testAccessibility() {
    composeTestRule.setContent {
        MyScreen()
    }
    
    composeTestRule
        .onNodeWithTag(AccessibilityTestTags.BUTTON)
        .assertHasClickAction()
        .assertIsDisplayed()
}
```

---

## 📱 TalkBack Testing

### Enable TalkBack
1. Settings → Accessibility → TalkBack → On
2. Or: Volume keys shortcut

### Navigation
- **Swipe Right:** Next item
- **Swipe Left:** Previous item
- **Double Tap:** Activate
- **Swipe Down-Then-Right:** Context menu

### Testing Checklist
- [ ] All clickable elements have descriptions
- [ ] Headings are announced correctly
- [ ] State changes are announced
- [ ] Images have descriptions
- [ ] Buttons describe their action
- [ ] Form fields have labels
- [ ] Error messages are announced

---

## 🎯 WCAG 2.1 Guidelines

### Level A (Minimum)
- ✅ Text alternatives for images
- ✅ Keyboard accessible
- ✅ Color not sole indicator

### Level AA (Target)
- ✅ **Contrast ratio 4.5:1** (normal text)
- ✅ **Contrast ratio 3:1** (large text 18pt+)
- ✅ Text resize up to 200%
- ✅ Touch targets 44x44dp minimum

### Level AAA (Enhanced)
- ⚠️ Contrast ratio 7:1 (normal text)
- ⚠️ Contrast ratio 4.5:1 (large text)

---

## 🔍 Common Issues & Fixes

### Issue: "Button not announced"
```kotlin
// ❌ Bad
Button(onClick = {}) {
    Icon(Icons.Default.Add, contentDescription = null)
}

// ✅ Good
Button(
    onClick = {},
    modifier = Modifier.accessibleClickable("Yeni işlem ekle")
) {
    Icon(Icons.Default.Add, contentDescription = null)
}
```

### Issue: "Low contrast"
```kotlin
// ❌ Bad
val textColor = Color.Gray
val background = Color.LightGray // Ratio: 1.5:1

// ✅ Good
val textColor = Color(0xFF1F2937) // Gray 800
val background = Color.White // Ratio: 12:1
```

### Issue: "Touch target too small"
```kotlin
// ❌ Bad
IconButton(
    modifier = Modifier.size(32.dp)
) { ... }

// ✅ Good
IconButton(
    modifier = Modifier.size(48.dp) // Or use ensureMinimumTouchTarget()
) { ... }
```

---

## 📊 Accessibility Metrics

### Test Coverage
- TalkBack navigation: **All screens**
- Contrast validation: **Automated in CI**
- Font scaling: **Previews for 1.0x, 1.5x, 2.0x**
- Touch targets: **Minimum 48dp enforced**

### Tools
- **Accessibility Scanner** (Android)
- **Lighthouse** (Web baseline)
- **axe DevTools**
- **Color contrast analyzers**

---

## 🚀 Implementation Checklist

### For Every Screen
- [ ] Add `accessibleHeading()` to titles
- [ ] Add `accessibleClickable()` to buttons/cards
- [ ] Validate color contrast with `meetsContrastAA()`
- [ ] Test with font scale 2.0x
- [ ] Test with TalkBack enabled

### For Forms
- [ ] Input fields have labels
- [ ] Error messages announced
- [ ] Success feedback provided
- [ ] Tab order logical

### For Images/Charts
- [ ] Meaningful alt text
- [ ] Complex graphics have long descriptions
- [ ] Decorative images marked as such

---

## 📚 Resources

- [Material Design Accessibility](https://m3.material.io/foundations/accessibility)
- [Android Accessibility Help](https://support.google.com/accessibility/android)
- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [WebAIM Contrast Checker](https://webaim.org/resources/contrastchecker/)

---

**Last Updated:** December 24, 2025
**Compliance Level:** WCAG 2.1 Level AA
