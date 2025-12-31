# GitHub Secrets Konfigürasyonu

Bu dokümantasyon, projenin GitHub Actions workflow'larının doğru çalışması için gerekli olan tüm secret'ların nasıl oluşturulup tanımlanacağını açıklar.

## Gerekli Secrets

### Release İmzalama (Signing)

#### 1. RELEASE_KEYSTORE_BASE64
**Açıklama**: Release APK/AAB imzalama için kullanılan keystore dosyasının Base64 encoded hali.

**Oluşturma Adımları**:
```bash
# 1. Keystore oluştur (yoksa)
keytool -genkey -v -keystore release.keystore -alias your-key-alias -keyalg RSA -keysize 2048 -validity 10000

# 2. Base64 encode et
# Windows PowerShell:
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore")) | Out-File keystore.b64 -Encoding ascii

# Linux/macOS:
base64 -i release.keystore -o keystore.b64

# 3. keystore.b64 içeriğini kopyala ve GitHub Secret olarak ekle
```

**GitHub Secret Ekleme**:
- Repository → Settings → Secrets and variables → Actions → New repository secret
- Name: `RELEASE_KEYSTORE_BASE64`
- Value: keystore.b64 dosyasının içeriği

#### 2. KEYSTORE_PASSWORD
**Açıklama**: Keystore dosyasının şifresi.

**Değer**: Keystore oluştururken belirlediğiniz "keystore password"

#### 3. KEY_ALIAS
**Açıklama**: Keystore içindeki key'in alias adı.

**Değer**: Keystore oluştururken `-alias` parametresine verdiğiniz değer (örn: `your-key-alias`)

#### 4. KEY_PASSWORD
**Açıklama**: Key'in (alias) şifresi.

**Değer**: Keystore oluştururken belirlediğiniz "key password"

---

### Billing Backend

#### 5. BILLING_BACKEND_URL
**Açıklama**: Satın alma doğrulama için kullanılacak backend API URL'i.

**Önerilen Format**: `https://api.yourdomain.com/v1/billing`

**Gereksinimler**:
- HTTPS zorunlu (HTTP kabul edilmez)
- SSL/TLS sertifikası geçerli olmalı
- HTTPS certificate pinning için sertifika bilgisi koda eklenmeli

**Örnek Değer**: `https://api.HesapGunlugu.com/v1/billing/verify`

#### 6. BILLING_BACKEND_API_KEY
**Açıklama**: Backend API'ye erişim için authentication key.

**Oluşturma**:
```bash
# Güçlü bir API key oluştur (PowerShell)
$apiKey = [System.Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
Write-Output $apiKey

# Linux/macOS
openssl rand -base64 32
```

**Güvenlik Notları**:
- API key minimum 32 karakter olmalı
- Yalnızca backend ve mobil uygulama arasında kullanılmalı
- Düzenli olarak rotate edilmeli (6-12 ay)
- Backend'de rate limiting ve IP whitelist uygulanmalı

---

## Secret'ları GitHub'a Ekleme

### Adım 1: Repository Ayarları
1. GitHub repository'nize gidin
2. **Settings** → **Secrets and variables** → **Actions**

### Adım 2: Her Secret'ı Ekle
Her secret için:
1. **New repository secret** butonuna tıklayın
2. Name alanına secret adını girin (yukarıdaki listeden)
3. Value alanına secret değerini yapıştırın
4. **Add secret** butonuna tıklayın

### Adım 3: Doğrulama
Secret'lar eklendikten sonra GitHub Actions workflow'larında şu şekilde kullanılabilir:
```yaml
env:
  BILLING_BACKEND_URL: ${{ secrets.BILLING_BACKEND_URL }}
  BILLING_BACKEND_API_KEY: ${{ secrets.BILLING_BACKEND_API_KEY }}
```

---

## Güvenlik En İyi Uygulamalar

### ✅ Yapılması Gerekenler
- Secret'ları asla kodda hard-code etmeyin
- `.gitignore` içinde `*.keystore`, `*.jks`, `local.properties` olduğundan emin olun
- Production ve staging için farklı secret'lar kullanın
- Secret'ları düzenli olarak rotate edin
- Minimum privilege principle uygulayın

### ❌ Yapılmaması Gerekenler
- Secret'ları commit etmeyin
- Secret'ları log'lamayın
- Secret'ları public dosyalarda saklamayın
- Weak password/key kullanmayın

---

## Workflow Kullanımı

### Release Workflow
[.github/workflows/release.yml](.github/workflows/release.yml) dosyası secret'ları kullanır:
```yaml
- name: Decode keystore
  run: |
    echo "${{ secrets.RELEASE_KEYSTORE_BASE64 }}" | base64 --decode > release.keystore

- name: Build signed release
  env:
    KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
    KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
    KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
```

### Macrobenchmark Workflow
[.github/workflows/macrobenchmark.yml](.github/workflows/macrobenchmark.yml) dosyası billing secret'larını kullanır:
```yaml
env:
  BILLING_BACKEND_URL: ${{ secrets.BILLING_BACKEND_URL }}
  BILLING_BACKEND_API_KEY: ${{ secrets.BILLING_BACKEND_API_KEY }}
```

---

## Sorun Giderme

### "Secret not found" Hatası
- Secret adını doğru yazdığınızdan emin olun (case-sensitive)
- Secret'ın repository veya organization level'da eklendiğini kontrol edin
- Workflow dosyasındaki syntax'ın doğru olduğundan emin olun

### "Invalid keystore format" Hatası
- Base64 encoding'in doğru yapıldığından emin olun
- Decode işleminin düzgün çalıştığını kontrol edin
- Keystore dosyasının bozulmadığını doğrulayın

### "API authentication failed" Hatası
- BILLING_BACKEND_API_KEY'in doğru olduğundan emin olun
- Backend sunucusunun erişilebilir olduğunu kontrol edin
- API key'in backend'de tanımlı olduğunu doğrulayın

---

## Checklist

Tüm secret'lar tanımlanmadan önce:

- [ ] Release keystore oluşturuldu
- [ ] Keystore Base64 encode edildi
- [ ] RELEASE_KEYSTORE_BASE64 eklendi
- [ ] KEYSTORE_PASSWORD eklendi
- [ ] KEY_ALIAS eklendi
- [ ] KEY_PASSWORD eklendi
- [ ] Backend API endpoint hazır
- [ ] BILLING_BACKEND_URL eklendi
- [ ] BILLING_BACKEND_API_KEY oluşturuldu ve eklendi
- [ ] Tüm secret'lar GitHub Actions'da test edildi
- [ ] Release build başarılı
- [ ] Billing verification çalışıyor

---

## İletişim

Secret konfigürasyonu ile ilgili sorunlar için:
- GitHub Issues açın
- DevOps ekibi ile iletişime geçin
- Dokümantasyonu güncel tutun
