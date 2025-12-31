# Firebase Setup Guide

## ⚠️ IMPORTANT: Security Configuration

### 1. Move google-services.json to Secure Location

The `google-services.json` file contains sensitive API keys and should NOT be committed to version control.

**Action Required:**
```bash
# 1. The real google-services.json is already in .gitignore
# 2. A sample file has been created: app/google-services.json.sample
# 3. For development, copy your actual file:
cp app/google-services.json.sample app/google-services.json
# Then replace placeholders with your Firebase Console values
```

### 2. Firebase Console Setup

1. **Go to:** [Firebase Console](https://console.firebase.google.com/)
2. **Select Project:** hesapgunlugu-cecac
3. **Download fresh google-services.json:**
   - Go to Project Settings → Your apps
   - Click Android app
   - Download google-services.json
   - Place in `app/` directory (not committed)

### 3. Configure Multiple Build Variants

Create separate Firebase projects for each flavor:

```
Production (Release):
- Package: com.hesapgunlugu.app.premium
- google-services.json → app/src/premium/google-services.json

Staging (Beta):
- Package: com.hesapgunlugu.app.premium.beta
- google-services.json → app/src/beta/google-services.json

Debug:
- Package: com.hesapgunlugu.app.free.debug
- google-services.json → app/src/debug/google-services.json
```

### 4. API Key Restrictions (Security Best Practice)

In Firebase Console → Credentials:

1. **Restrict Android API Key:**
   - Application restrictions: Android apps
   - Add package names:
     - `com.hesapgunlugu.app.free`
     - `com.hesapgunlugu.app.premium`
   - Add SHA-1 fingerprints (from keystore)

2. **Enable Required APIs Only:**
   - ✅ Firebase Authentication API
   - ✅ Firebase Crashlytics API
   - ✅ Firebase Analytics API
   - ❌ Disable all others

### 5. Environment Variables (CI/CD)

For GitHub Actions / CI environment:

```yaml
# .github/workflows/ci.yml
env:
  FIREBASE_API_KEY: ${{ secrets.FIREBASE_API_KEY }}
  FIREBASE_PROJECT_ID: ${{ secrets.FIREBASE_PROJECT_ID }}
```

**GitHub Secrets to Add:**
- `FIREBASE_API_KEY`
- `FIREBASE_PROJECT_ID`
- `FIREBASE_APP_ID`
- `GOOGLE_SERVICES_JSON` (base64 encoded file content)

### 6. Verify Setup

```bash
# Build the project
./gradlew assembleDebug

# Check if Firebase is initialized
# Look for logs: "Firebase initialized successfully"
```

### 7. Security Checklist

- [ ] google-services.json added to .gitignore
- [ ] Sample file created (google-services.json.sample)
- [ ] Real file NOT committed to Git
- [ ] API keys restricted in Firebase Console
- [ ] SHA-1 fingerprints added to Firebase
- [ ] Different Firebase projects for prod/staging/dev
- [ ] CI/CD secrets configured

## 🔒 NEVER Commit These Files

```
google-services.json
local.properties
*.keystore
*.jks
apikeys.properties
secrets.properties
```

## 📞 Support

If you encounter issues:
1. Check Firebase Console for project configuration
2. Verify package names match in google-services.json
3. Ensure SHA-1 certificate is registered
4. Check Firebase debug logs in Logcat
