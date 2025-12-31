# APK Signing & Release Guide

## 🔐 Keystore Generation

### Step 1: Generate Release Keystore

```bash
# Navigate to your project root
cd c:\Users\Acer\AndroidStudioProjects\HesapGunlugu

# Generate keystore (DO NOT COMMIT THIS FILE!)
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias HesapGunlugu-release

# You will be prompted for:
# - Keystore password (min 6 characters)
# - Key password (can be same as keystore password)
# - Your name / Organization details
```

**⚠️ IMPORTANT:** 
- Save keystore password and alias in a secure password manager
- Never commit `release-keystore.jks` to Git
- Add it to `.gitignore`

### Step 2: Encode Keystore for GitHub Secrets

```bash
# On Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-keystore.jks")) | Out-File -FilePath keystore.txt

# On Linux/Mac
base64 release-keystore.jks > keystore.txt
```

## ⚙️ GitHub Secrets Configuration

Go to your GitHub repository → Settings → Secrets and variables → Actions → New repository secret

Add these 4 secrets:

| Secret Name | Value | Example |
|------------|-------|---------|
| `KEYSTORE_BASE64` | Contents of `keystore.txt` | `MIIKEgIBAzCCCc4GCSqGSIb3DQEH...` |
| `KEYSTORE_PASSWORD` | Password you entered during keytool | `MySecurePass123!` |
| `KEY_ALIAS` | Alias name | `HesapGunlugu-release` |
| `KEY_PASSWORD` | Key password (usually same as keystore) | `MySecurePass123!` |

## 📦 Build Signed APK/AAB

### Method 1: Using GitHub Actions (Recommended)

```bash
# Create a version tag
git tag v1.0.0
git push origin v1.0.0

# Workflow automatically builds and signs APK + AAB
# Download artifacts from Actions tab
```

### Method 2: Local Build

```bash
# Option A: Using gradle.properties (NOT recommended - security risk)
# Create gradle.properties in project root (add to .gitignore!)
KEYSTORE_FILE=release-keystore.jks
KEYSTORE_PASSWORD=YourPassword
KEY_ALIAS=HesapGunlugu-release
KEY_PASSWORD=YourPassword

# Build
./gradlew assemblePremiumRelease

# Option B: Using command line arguments (RECOMMENDED)
./gradlew assemblePremiumRelease `
  -Pandroid.injected.signing.store.file=release-keystore.jks `
  -Pandroid.injected.signing.store.password=YourPassword `
  -Pandroid.injected.signing.key.alias=HesapGunlugu-release `
  -Pandroid.injected.signing.key.password=YourPassword
```

### Method 3: Android Studio

1. Build → Generate Signed Bundle / APK
2. Select "APK" or "AAB"
3. Choose existing keystore: `release-keystore.jks`
4. Enter passwords and alias
5. Select build variant: `premiumRelease`
6. Click Finish

## 📁 Output Locations

```
app/build/outputs/
├── apk/
│   ├── free/release/app-free-release.apk
│   └── premium/release/app-premium-release.apk
└── bundle/
    ├── freeRelease/app-free-release.aab
    └── premiumRelease/app-premium-release.aab
```

## ✅ Verify Signature

```bash
# APK
jarsigner -verify -verbose -certs app/build/outputs/apk/premium/release/app-premium-release.apk

# AAB (first extract)
bundletool build-apks --bundle=app-premium-release.aab --output=app.apks --mode=universal
unzip app.apks
jarsigner -verify -verbose -certs universal.apk
```

## 🚀 Release Checklist

- [ ] Keystore generated and backed up securely
- [ ] GitHub Secrets configured (4 secrets)
- [ ] `.gitignore` includes `*.jks`, `*.keystore`, `keystore.txt`
- [ ] Version code incremented in `app/build.gradle.kts`
- [ ] Version name updated (e.g., `1.0.0`)
- [ ] ProGuard rules tested with release build
- [ ] Crash reporting tested (ACRA)
- [ ] Baseline profile generated
- [ ] All tests passing (`./gradlew test`)
- [ ] APK/AAB signed successfully
- [ ] Signature verified with `jarsigner`

## 🔄 Version Management

```kotlin
// app/build.gradle.kts
defaultConfig {
    versionCode = 1  // Increment for each release
    versionName = "1.0.0"  // Semantic versioning
}
```

**Versioning Rules:**
- `versionCode`: Integer, must increase with each upload to Play Store
- `versionName`: `MAJOR.MINOR.PATCH`
  - MAJOR: Breaking changes
  - MINOR: New features
  - PATCH: Bug fixes

## 📤 Google Play Store Upload

1. Build AAB: `./gradlew bundlePremiumRelease`
2. Go to [Google Play Console](https://play.google.com/console)
3. Production → Create new release
4. Upload `app-premium-release.aab`
5. Add release notes
6. Roll out to production

## 🆘 Troubleshooting

### "keystore was tampered with, or password was incorrect"
→ Wrong `KEYSTORE_PASSWORD` or `KEY_PASSWORD`

### "Keystore file does not exist"
→ Check path to `.jks` file, ensure it's in project root

### "unsigned APK"
→ Signing config not applied, verify gradle command includes signing parameters

### "duplicate entries during packaging"
→ Add packaging options to `app/build.gradle.kts`:
```kotlin
packaging {
    resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}
```

## 🔗 Resources

- [Android App Signing Guide](https://developer.android.com/studio/publish/app-signing)
- [Google Play Upload Guide](https://developer.android.com/studio/publish/upload-bundle)
- [ProGuard Rules](https://developer.android.com/build/shrink-code)
