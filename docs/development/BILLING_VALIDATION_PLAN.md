# Billing Validation Architecture - Design Document

**Document Version**: 1.0  
**Date**: 2025-12-27  
**Author**: Development Team  
**Status**: Design Phase  

---

## 📋 Executive Summary

Bu doküman, HesapGunlugu için güvenli ve fraud-resistant bir billing validation mimarisi tasarımını içerir. Şu anki client-side validation'dan server-side validation'a geçiş planı ve implementation detayları.

## 🎯 Objectives

### Primary Goals
1. **Revenue Protection**: Sahte satın alım engelleme
2. **Security**: Server-side purchase validation
3. **Compliance**: Google Play Billing best practices
4. **User Experience**: Seamless premium upgrade flow

### Success Metrics
- ✅ %100 purchase verification server-side
- ✅ <2 second verification latency
- ✅ %99.9 verification availability
- ✅ Zero revenue leak from fraud

---

## 🏗️ Current Architecture

### Client-Side Only (Current State)

```
┌─────────────────────────────────────────────────┐
│ HesapGunlugu (Android)                              │
│                                                 │
│  ┌──────────────┐    ┌────────────────────┐   │
│  │ BillingManager│───▶│Google Play Billing │   │
│  └──────────────┘    └────────────────────┘   │
│         │                      │               │
│         ▼                      ▼               │
│  ┌──────────────────────────────────────┐     │
│  │ BillingBackendVerifier               │     │
│  │ - Currently: Stub implementation     │     │
│  │ - Returns: VerificationResult(true)  │     │
│  └──────────────────────────────────────┘     │
└─────────────────────────────────────────────────┘
```

**Risks**:
- ❌ No server-side validation
- ❌ Vulnerable to reverse engineering
- ❌ Purchase token not verified with Google
- ❌ Easy to bypass with modified APK

---

## 🔐 Proposed Architecture

### Server-Side Validation (Target State)

```
┌────────────────────────────────────────────────────────────────┐
│ HesapGunlugu (Android)                                             │
│                                                                │
│  ┌──────────────┐    ┌────────────────────┐                  │
│  │ BillingManager│───▶│Google Play Billing │                  │
│  └──────┬───────┘    └────────┬───────────┘                  │
│         │                      │                               │
│         │ purchaseToken        │ Purchase Event               │
│         ▼                      │                               │
│  ┌──────────────────────────┐ │                               │
│  │ BillingBackendVerifier   │ │                               │
│  │ - HTTP POST to backend   │ │                               │
│  │ - JWT authentication     │ │                               │
│  └──────────┬───────────────┘ │                               │
└─────────────┼─────────────────┼───────────────────────────────┘
              │                 │
              │                 │ HTTPS
              ▼                 │
┌─────────────────────────────┐ │
│ Backend API (Node.js/Python)│ │
│                             │ │
│  ┌────────────────────────┐ │ │
│  │ POST /verify-purchase  │ │ │
│  │ - Validate JWT         │ │ │
│  │ - Verify purchase token│◀┘ │
│  └────────┬───────────────┘   │
│           │                   │
│           ▼                   │
│  ┌────────────────────────┐  │
│  │ Google Play Developer  │  │
│  │ API Integration        │  │
│  │ - androidpublisher v3  │  │
│  └────────┬───────────────┘  │
└────────────┼────────────────────┘
             │
             ▼
    ┌────────────────────┐
    │ Google Play API    │
    │ - Verify token     │
    │ - Check subscription│
    │ - Return status    │
    └────────────────────┘
```

---

## 📡 API Specification

### Endpoint: Verify Purchase

**Request**
```http
POST /api/v1/billing/verify-purchase
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "purchaseToken": "gkodhaocbhe...",
  "productId": "premium_monthly",
  "userId": "user_12345",
  "packageName": "com.hesapgunlugu.app"
}
```

**Response - Success**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "isValid": true,
  "subscription": {
    "startTime": 1703692800000,
    "expiryTime": 1706371200000,
    "autoRenewing": true,
    "productId": "premium_monthly"
  },
  "verifiedAt": 1703692850000
}
```

**Response - Invalid**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "isValid": false,
  "errorCode": "INVALID_TOKEN",
  "errorMessage": "Purchase token verification failed",
  "verifiedAt": 1703692850000
}
```

---

## 🔧 Implementation Plan

### Phase 1: Backend Setup (Day 1-2)

#### 1.1 Technology Stack
```yaml
Backend:
  - Runtime: Node.js 20 LTS / Python 3.11
  - Framework: Express.js / FastAPI
  - Database: PostgreSQL 15
  - Hosting: Google Cloud Run (recommended)
  - Auth: Firebase Auth JWT

Dependencies:
  - googleapis (Node.js) / google-auth (Python)
  - express-jwt / python-jose
  - pg / psycopg2
```

#### 1.2 Google Play Console Setup
```bash
# Steps:
1. Go to Google Play Console
2. Navigate to: API access
3. Create service account
4. Download JSON key file
5. Grant "View financial data" permission
6. Enable Google Play Developer API
```

#### 1.3 Environment Variables
```bash
# .env (Backend)
GOOGLE_APPLICATION_CREDENTIALS=/path/to/service-account.json
PACKAGE_NAME=com.hesapgunlugu.app
FIREBASE_PROJECT_ID=HesapGunlugu-xxxxx
JWT_SECRET=<secure-random-string>
DATABASE_URL=postgresql://user:pass@host/db
```

### Phase 2: Backend Implementation (Day 2-3)

#### 2.1 Purchase Verification Service (Node.js)
```javascript
// services/billingService.js
const { google } = require('googleapis');

class BillingService {
  constructor() {
    this.androidPublisher = google.androidpublisher('v3');
  }

  async verifyPurchase(purchaseToken, productId, packageName) {
    try {
      const auth = await google.auth.getClient({
        scopes: ['https://www.googleapis.com/auth/androidpublisher'],
      });

      const response = await this.androidPublisher.purchases.subscriptions.get({
        auth,
        packageName,
        subscriptionId: productId,
        token: purchaseToken,
      });

      const subscription = response.data;
      
      // Check if subscription is active
      const isActive = 
        subscription.paymentState === 1 && // Payment received
        subscription.expiryTimeMillis > Date.now();

      return {
        isValid: isActive,
        subscription: {
          startTime: parseInt(subscription.startTimeMillis),
          expiryTime: parseInt(subscription.expiryTimeMillis),
          autoRenewing: subscription.autoRenewing,
          productId: subscription.productId,
        },
      };
    } catch (error) {
      console.error('Purchase verification failed:', error);
      return {
        isValid: false,
        errorCode: error.code,
        errorMessage: error.message,
      };
    }
  }
}

module.exports = new BillingService();
```

#### 2.2 API Endpoint
```javascript
// routes/billing.js
const express = require('express');
const router = express.Router();
const billingService = require('../services/billingService');
const { verifyJWT } = require('../middleware/auth');

router.post('/verify-purchase', verifyJWT, async (req, res) => {
  const { purchaseToken, productId, packageName } = req.body;

  // Validate request
  if (!purchaseToken || !productId) {
    return res.status(400).json({
      isValid: false,
      errorMessage: 'Missing required fields',
    });
  }

  // Verify with Google
  const result = await billingService.verifyPurchase(
    purchaseToken,
    productId,
    packageName || process.env.PACKAGE_NAME
  );

  // Store verification result in DB (for audit trail)
  await db.query(
    'INSERT INTO purchase_verifications (user_id, purchase_token, is_valid, verified_at) VALUES ($1, $2, $3, NOW())',
    [req.user.uid, purchaseToken, result.isValid]
  );

  res.json({
    ...result,
    verifiedAt: Date.now(),
  });
});

module.exports = router;
```

### Phase 3: Android Client Integration (Day 3-4)

#### 3.1 Update BillingBackendVerifier
```kotlin
// core/premium/BillingBackendVerifier.kt
@Singleton
class BillingBackendVerifier @Inject constructor(
    private val httpClient: OkHttpClient,
    private val firebaseAuth: FirebaseAuth,
    @Named("BackendUrl") private val backendUrl: String
) {
    
    suspend fun verifyPurchase(
        purchaseToken: String,
        productId: String
    ): VerificationResult = withContext(Dispatchers.IO) {
        try {
            // Get Firebase JWT
            val idToken = firebaseAuth.currentUser?.getIdToken(false)?.await()?.token
                ?: return@withContext VerificationResult(
                    isValid = false,
                    errorMessage = "User not authenticated"
                )
            
            // Build request
            val requestBody = JSONObject().apply {
                put("purchaseToken", purchaseToken)
                put("productId", productId)
                put("packageName", BuildConfig.APPLICATION_ID)
            }
            
            val request = Request.Builder()
                .url("$backendUrl/api/v1/billing/verify-purchase")
                .header("Authorization", "Bearer $idToken")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
            
            // Execute request
            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string()
            
            if (!response.isSuccessful || responseBody == null) {
                return@withContext VerificationResult(
                    isValid = false,
                    errorMessage = "Network error: ${response.code}"
                )
            }
            
            // Parse response
            val json = JSONObject(responseBody)
            VerificationResult(
                isValid = json.getBoolean("isValid"),
                errorMessage = json.optString("errorMessage", null)
            )
            
        } catch (e: Exception) {
            Timber.e(e, "Purchase verification failed")
            VerificationResult(
                isValid = false,
                errorMessage = "Verification error: ${e.message}"
            )
        }
    }
}
```

#### 3.2 Gradle Dependencies
```kotlin
// build.gradle.kts
dependencies {
    // HTTP Client
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Firebase Auth
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    
    // JSON
    implementation("org.json:json:20231013")
}
```

---

## 🔐 Security Considerations

### 1. Authentication
- ✅ Firebase JWT token for API authentication
- ✅ Token expiry validation (1 hour)
- ✅ User ID verification

### 2. Transport Security
- ✅ HTTPS only (TLS 1.3)
- ✅ Certificate pinning (optional)
- ✅ Request signing (optional)

### 3. Rate Limiting
```javascript
// Backend rate limiting
const rateLimit = require('express-rate-limit');

const verifyPurchaseLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 10, // 10 requests per window
  message: 'Too many verification requests',
});

router.post('/verify-purchase', verifyPurchaseLimiter, ...);
```

### 4. Audit Trail
```sql
-- Database schema for audit
CREATE TABLE purchase_verifications (
  id SERIAL PRIMARY KEY,
  user_id VARCHAR(128) NOT NULL,
  purchase_token VARCHAR(512) NOT NULL,
  product_id VARCHAR(128) NOT NULL,
  is_valid BOOLEAN NOT NULL,
  error_message TEXT,
  verified_at TIMESTAMP DEFAULT NOW(),
  ip_address INET,
  INDEX idx_user_id (user_id),
  INDEX idx_verified_at (verified_at)
);
```

---

## 📊 Testing Strategy

### Unit Tests
```kotlin
// BillingBackendVerifierTest.kt
@Test
fun `verify purchase with valid token returns success`() = runTest {
    // Mock backend response
    mockWebServer.enqueue(
        MockResponse()
            .setResponseCode(200)
            .setBody("""{"isValid": true}""")
    )
    
    val result = verifier.verifyPurchase("token123", "premium_monthly")
    
    assertTrue(result.isValid)
}
```

### Integration Tests
```kotlin
// Backend integration test
@Test
fun `end to end purchase verification flow`() = runTest {
    // 1. Make purchase
    val purchase = testBillingClient.purchase("premium_monthly")
    
    // 2. Verify with backend
    val result = verifier.verifyPurchase(
        purchase.purchaseToken,
        "premium_monthly"
    )
    
    // 3. Assert
    assertTrue(result.isValid)
}
```

---

## 📈 Monitoring & Analytics

### Key Metrics
- Purchase verification success rate
- API response time (p50, p95, p99)
- Failed verification reasons
- Fraud attempt detection

### Logging
```kotlin
// Client-side logging
Timber.tag("Billing").i(
    "Purchase verification: token=${token.take(10)}..., " +
    "result=${result.isValid}, latency=${duration}ms"
)
```

### Alerts
- Verification success rate < 95%
- API latency > 3 seconds
- Unusual verification failure spike

---

## 💰 Cost Estimation

### Backend Hosting (Google Cloud Run)
- **Requests**: ~10,000/month (estimated)
- **Cost**: $0 (within free tier: 2M requests/month)

### Firebase Auth
- **Active users**: <50,000
- **Cost**: $0 (free tier)

### Google Play Developer API
- **Quota**: 200,000 requests/day
- **Cost**: $0 (free)

**Total Monthly Cost**: ~$0 (within free tiers)

---

## 🚀 Deployment Plan

### Day 1
- [ ] Setup Google Cloud project
- [ ] Create service account
- [ ] Setup Firebase Auth
- [ ] Initialize backend repository

### Day 2
- [ ] Implement backend API
- [ ] Add Google Play API integration
- [ ] Setup database
- [ ] Deploy to Cloud Run (staging)

### Day 3
- [ ] Update Android client
- [ ] Integration testing
- [ ] Security review

### Day 4
- [ ] Load testing
- [ ] Production deployment
- [ ] Monitoring setup
- [ ] Documentation

---

## ✅ Acceptance Criteria

- [ ] All purchases verified server-side
- [ ] <2s verification latency (p95)
- [ ] %99+ verification success rate
- [ ] Zero client-side fraud bypass
- [ ] Comprehensive error handling
- [ ] Audit logging complete
- [ ] Monitoring & alerts active

---

## 📚 References

- [Google Play Billing Security Best Practices](https://developer.android.com/google/play/billing/security)
- [Google Play Developer API](https://developers.google.com/android-publisher)
- [Firebase Auth Documentation](https://firebase.google.com/docs/auth)

---

**Next Steps**: Review & approval → Backend implementation → Client integration → Testing → Production release
