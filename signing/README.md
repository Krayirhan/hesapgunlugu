# APK Signing Automation

This directory contains the signing configuration for automated APK signing.

## Setup Instructions

### 1. Create Keystore (First Time Only)

```powershell
keytool -genkeypair -v `
  -keystore HesapGunlugu-release.keystore `
  -alias HesapGunlugu `
  -keyalg RSA `
  -keysize 2048 `
  -validity 10000 `
  -storepass <YOUR_STORE_PASSWORD> `
  -keypass <YOUR_KEY_PASSWORD> `
  -dname "CN=HesapGunlugu, OU=Finance, O=MyCompany, L=Istanbul, ST=Istanbul, C=TR"
```

### 2. Store Keystore Securely

**Option A: Local Development**
- Place `HesapGunlugu-release.keystore` in this directory
- Add to `.gitignore` (already configured)
- Never commit to version control

**Option B: CI/CD (GitHub Actions)**
- Encode keystore to Base64:
  ```powershell
  $bytes = [System.IO.File]::ReadAllBytes("HesapGunlugu-release.keystore")
  $base64 = [System.Convert]::ToBase64String($bytes)
  $base64 | Set-Clipboard
  ```
- Add to GitHub Secrets:
  - `KEYSTORE_BASE64`: (paste base64 string)
  - `KEYSTORE_PASSWORD`: keystore password
  - `KEY_ALIAS`: `HesapGunlugu`
  - `KEY_PASSWORD`: key password

### 3. Configure local.properties

Add to `local.properties` (NOT in version control):

```properties
# Signing Configuration
signing.storeFile=../signing/HesapGunlugu-release.keystore
signing.storePassword=YOUR_STORE_PASSWORD
signing.keyAlias=HesapGunlugu
signing.keyPassword=YOUR_KEY_PASSWORD
```

### 4. Build Signed APK/AAB

```powershell
# Build signed APK
.\gradlew assembleRelease

# Build signed AAB (for Play Store)
.\gradlew bundleRelease

# Output locations:
# APK: app/build/outputs/apk/release/app-release.apk
# AAB: app/build/outputs/bundle/release/app-release.aab
```

## Security Best Practices

✅ **DO:**
- Store keystore in secure location
- Use strong passwords (minimum 8 characters)
- Backup keystore securely (you cannot recover it!)
- Use environment variables or secret management
- Rotate keys periodically (create new keystore)

❌ **DON'T:**
- Commit keystore to version control
- Share keystore password in plain text
- Use weak passwords
- Store passwords in code
- Lose your keystore (you'll need it for app updates!)

## Keystore Backup

1. **Backup keystore file**:
   ```powershell
   Copy-Item HesapGunlugu-release.keystore -Destination "C:\Secure\Backup\HesapGunlugu-release-backup.keystore"
   ```

2. **Store passwords in password manager** (1Password, LastPass, etc.)

3. **Test recovery** periodically

## Troubleshooting

### "Keystore not found"
- Check path in `local.properties`
- Verify keystore file exists
- Use absolute path if relative path fails

### "Wrong password"
- Verify store password and key password
- Check for typos in `local.properties`
- Password is case-sensitive

### "Key not found"
- Verify key alias (`HesapGunlugu`)
- List keys in keystore:
  ```powershell
  keytool -list -v -keystore HesapGunlugu-release.keystore
  ```

## CI/CD Integration

See `.github/workflows/release.yml` for automated signing in GitHub Actions.

## Google Play App Signing

Google Play offers app signing management:
1. Upload your app bundle to Play Console
2. Opt-in to App Signing by Google Play
3. Google manages your app signing key
4. You keep upload key for future updates

Benefits:
- Google stores your app signing key securely
- Separate upload key for builds
- Can reset upload key if compromised
