# ADR-006: No Firebase/Cloud Database

## Status
Accepted

## Date
2024-12-24

## Context

We needed to decide on the data persistence strategy for our finance tracking application. The options were:

1. **Local-only storage** (Room Database)
2. **Firebase Firestore** (Cloud database)
3. **Custom backend API** (REST/GraphQL)
4. **Hybrid approach** (Local + Cloud sync)

### Requirements Considered

- Privacy: Financial data is sensitive
- Offline support: Must work without internet
- Cost: Free or minimal operational cost
- Complexity: Manageable for solo developer
- GDPR/KVKK: Data protection compliance

## Decision

We decided to use **Local-only storage** with Room Database, explicitly excluding Firebase and cloud databases.

### Rationale

1. **Privacy First**
   - Financial data stays on user's device
   - No third-party access to transactions
   - Full GDPR/KVKK compliance

2. **Offline by Design**
   - Works 100% without internet
   - No sync conflicts
   - Faster operations

3. **Zero Operational Cost**
   - No server maintenance
   - No Firebase billing
   - No API costs

4. **Simplicity**
   - No authentication complexity
   - No sync logic
   - Easier debugging

## Consequences

### Positive
- ✅ Complete privacy - data never leaves device
- ✅ Always works offline
- ✅ No recurring costs
- ✅ Simpler architecture
- ✅ Faster development
- ✅ Full GDPR compliance

### Negative
- ❌ No cross-device sync
- ❌ Data lost if device is lost (mitigated by local backup)
- ❌ No real-time multi-currency rates
- ❌ No analytics/crash reporting (Crashlytics)

### Mitigations
- Local backup/restore to file
- Export to CSV/PDF for portability
- Manual currency rate input
- Custom crash logging to local storage

## Alternatives Rejected

### Firebase Firestore
- Rejected due to privacy concerns
- Cloud storage of financial data
- GDPR complexity
- Recurring costs at scale

### Custom Backend
- Rejected due to complexity
- Server maintenance overhead
- Development time
- Operational costs

## Future Considerations

If cross-device sync becomes essential:
1. Consider end-to-end encrypted sync
2. User-controlled cloud storage (Google Drive backup)
3. Optional Firebase with full encryption

