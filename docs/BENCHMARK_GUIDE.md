# ⚡ Benchmark & Performance Testing Guide

## 📋 Overview

Bu proje **Macrobenchmark** kullanarak performans metriklerini ölçer:
- ✅ Startup time (cold, warm, hot)
- ✅ Scroll performance (jank detection)
- ✅ Navigation performance
- ✅ Frame timing
- ✅ Baseline profile generation

---

## 🏗️ Benchmark Module

**Location:** `:benchmark-macro`

### Module Structure
```
benchmark-macro/
├── build.gradle.kts
└── src/main/java/com/example/HesapGunlugu/benchmark/
    ├── StartupBenchmark.kt
    ├── ScrollBenchmark.kt
    └── NavigationBenchmark.kt
```

---

## 🚀 Running Benchmarks

### Prerequisites
1. **Release build variant** or **benchmark variant**
2. **Physical device** (emulator not recommended)
3. **Device plugged in via USB**
4. **Developer options enabled**

### Run All Benchmarks
```bash
./gradlew :benchmark-macro:connectedBenchmarkAndroidTest
```

### Run Specific Benchmark
```bash
# Startup only
./gradlew :benchmark-macro:connectedBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.hesapgunlugu.app.benchmark.StartupBenchmark

# Scroll only
./gradlew :benchmark-macro:connectedBenchmarkAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.hesapgunlugu.app.benchmark.ScrollBenchmark
```

---

## 📊 Benchmark Types

### 1️⃣ Startup Benchmark

**File:** `StartupBenchmark.kt`

**Measures:**
- Cold start (first launch after install)
- Warm start (app in background)
- Hot start (app in memory)

**Metrics:**
- `timeToInitialDisplay`: Time to first frame
- `timeToFullDisplay`: Time to fully interactive

**Tests:**
```kotlin
@Test
fun startupColdWithBaselineProfile() // With optimization
fun startupColdNoCompilation()       // Worst case
fun startupWarm()                    // Typical usage
fun startupHot()                     // Best case
```

**Example Output:**
```
startupColdWithBaselineProfile
  timeToInitialDisplay   min 245ms,   median 268ms,   max 289ms
  timeToFullDisplay      min 512ms,   median 534ms,   max 578ms
```

---

### 2️⃣ Scroll Benchmark

**File:** `ScrollBenchmark.kt`

**Measures:**
- Scroll performance on list screens
- Frame drops (jank)
- GPU/CPU time per frame

**Metrics:**
- `frameOverrunMs`: How much frames exceed 16.6ms budget
- `frameDurationCpuMs`: CPU time per frame

**Tests:**
```kotlin
@Test
fun scrollHistoryList()       // Transaction list scrolling
fun scrollStatisticsScreen()  // Charts scrolling
```

**Example Output:**
```
scrollHistoryList
  frameOverrunMs     P50   0.8ms,   P90   2.1ms,   P99   5.2ms
  frameDurationCpuMs P50   8.4ms,   P90  12.1ms,   P99  15.8ms
```

**What's Good:**
- ✅ P99 frameOverrunMs < 8ms (no visible jank)
- ⚠️ P99 frameOverrunMs 8-16ms (minor jank)
- ❌ P99 frameOverrunMs > 16ms (dropped frames)

---

### 3️⃣ Navigation Benchmark

**File:** `NavigationBenchmark.kt`

**Measures:**
- Screen transition performance
- Navigation between tabs

**Metrics:**
- Frame timing during navigation
- Transition smoothness

**Tests:**
```kotlin
@Test
fun navigationBetweenScreens() // Full navigation cycle
```

---

## 📈 Results Location

### JSON Results
```
benchmark-macro/build/outputs/androidTest-results/connected/
```

### HTML Report
```
benchmark-macro/build/reports/androidTests/connected/index.html
```

### Trace Files (Perfetto)
```
benchmark-macro/build/outputs/managed_device_android_test_additional_output/
```

---

## 🔍 Analyzing Results

### 1. Check JSON Output
```json
{
  "name": "startupColdWithBaselineProfile",
  "metrics": {
    "timeToInitialDisplay": {
      "minimum": 245.2,
      "median": 268.4,
      "maximum": 289.1
    }
  }
}
```

### 2. Open Perfetto Trace
1. Go to [ui.perfetto.dev](https://ui.perfetto.dev/)
2. Upload `.perfetto-trace` file
3. Analyze:
   - Main thread work
   - UI rendering
   - GC pauses
   - Lock contention

### 3. Compare with Baseline
```kotlin
// Set acceptable thresholds
val maxStartupTime = 500.ms
val maxFrameOverrun = 8.ms

assertTrue(
    actual = results.timeToInitialDisplay.median,
    message = "Startup too slow"
) { it < maxStartupTime }
```

---

## 🎯 Optimization Targets

### Startup Performance
| Metric | Target | Good | Needs Work |
|--------|--------|------|------------|
| Cold start (P50) | < 300ms | < 500ms | > 800ms |
| Warm start (P50) | < 200ms | < 350ms | > 500ms |
| Hot start (P50) | < 100ms | < 200ms | > 300ms |

### Scroll Performance
| Metric | Target | Good | Needs Work |
|--------|--------|------|------------|
| P99 frameOverrun | < 4ms | < 8ms | > 16ms |
| P50 frameCPU | < 8ms | < 12ms | > 14ms |

---

## 🛠️ Common Optimizations

### Reduce Startup Time
1. **Baseline Profile** (already implemented)
2. **Lazy initialization**
   ```kotlin
   val heavyObject by lazy { /* expensive init */ }
   ```
3. **App Startup library**
   ```kotlin
   class MyInitializer : Initializer<Unit> {
       override fun create(context: Context) {
           // Deferred init
       }
   }
   ```
4. **Reduce onCreate() work**
   ```kotlin
   // ❌ Bad
   override fun onCreate() {
       super.onCreate()
       initDatabase()  // Blocks main thread
       loadPreferences()
   }
   
   // ✅ Good
   override fun onCreate() {
       super.onCreate()
       lifecycleScope.launch(Dispatchers.IO) {
           initDatabase()  // Background
       }
   }
   ```

### Reduce Scroll Jank
1. **Compose stability**
   ```kotlin
   @Immutable
   data class MyData(...) // Helps recomposition
   ```
2. **LazyColumn keys**
   ```kotlin
   LazyColumn {
       items(list, key = { it.id }) { ... }
   }
   ```
3. **Remember expensive calculations**
   ```kotlin
   val expensiveValue = remember(input) {
       heavyComputation(input)
   }
   ```

### Reduce Navigation Lag
1. **Shared element transitions**
2. **Predictive back gestures**
3. **ViewModel scoping**

---

## 🔄 CI/CD Integration

### GitHub Actions
```yaml
- name: Run Benchmarks
  run: ./gradlew :benchmark-macro:connectedBenchmarkAndroidTest
  
- name: Upload Results
  uses: actions/upload-artifact@v4
  with:
    name: benchmark-results
    path: benchmark-macro/build/outputs/
```

### Performance Regression Detection
```yaml
- name: Compare with Baseline
  run: |
    python scripts/compare_benchmarks.py \
      --current benchmark-macro/build/outputs/results.json \
      --baseline benchmarks/baseline.json \
      --threshold 10%
```

---

## 📊 Baseline Profile

### What is it?
Pre-compiled code paths that are executed during app startup

### Generation
Already set up via `:baselineprofile` module

### Verify
```bash
./gradlew :baselineprofile:generateBaselineProfile
```

Output: `app/src/main/baseline-prof.txt`

---

## 🐛 Troubleshooting

### Issue: "No connected devices"
```bash
adb devices
# Make sure device is listed
```

### Issue: "Compilation mode not supported"
- Use physical device (not emulator)
- Check Android 13+ or use AOT compilation

### Issue: "Benchmark killed"
- Disable battery optimization for test apps
- Close background apps
- Use airplane mode

---

## 📚 Resources

- [Macrobenchmark Guide](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview)
- [Baseline Profiles](https://developer.android.com/topic/performance/baselineprofiles)
- [Perfetto Trace Viewer](https://ui.perfetto.dev/)
- [Frame Metrics](https://developer.android.com/topic/performance/vitals/render)

---

**Last Updated:** December 24, 2025
**Performance Target:** P99 < 8ms frame overrun, Cold start < 500ms
