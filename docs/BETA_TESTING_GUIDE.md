# Internal Beta Testing Guide

## Overview

This guide outlines the beta testing process for HesapGunlugu before Play Store release.

## Beta Test Phases

### Phase 1: Internal Testing (Week 1)
**Duration**: 3-5 days  
**Testers**: Development team (3-5 people)  
**Focus**: Critical bugs, crashes, core functionality

**Test Areas**:
- [ ] App installation and startup
- [ ] Transaction CRUD operations
- [ ] Database integrity
- [ ] Navigation flows
- [ ] Authentication (biometric)
- [ ] Data backup/restore
- [ ] Memory leaks
- [ ] Battery consumption

### Phase 2: Closed Alpha (Week 1)
**Duration**: 3-4 days  
**Testers**: Friends & family (10-20 people)  
**Focus**: User experience, edge cases

**Test Areas**:
- [ ] Real-world usage scenarios
- [ ] Different device types
- [ ] Various Android versions (API 26-36)
- [ ] Different screen sizes
- [ ] Accessibility features
- [ ] Language/locale support

### Phase 3: Open Beta (Week 2)
**Duration**: 7-14 days  
**Testers**: Public volunteers (100-500 people)  
**Focus**: Scalability, diverse scenarios, feedback

**Test Areas**:
- [ ] Large transaction volumes
- [ ] Long-term usage patterns
- [ ] Network conditions
- [ ] Regional variations
- [ ] Feature requests
- [ ] Performance metrics

## Beta Distribution Channels

### 1. Google Play Internal Testing
```
Play Console → Testing → Internal testing → Create new release
```
- Upload beta APK/AAB
- Add tester emails
- Testers receive invitation link
- Instant updates via Play Store

### 2. Firebase App Distribution
```
Firebase Console → App Distribution → Release
```
- Upload beta APK
- Add tester groups
- Push notifications for updates
- In-app feedback widget

### 3. Direct APK Distribution
- Host on secure server
- Share download link
- Manual installation
- Less recommended (security concerns)

## Beta Build Configuration

### Build Commands

```powershell
# Build beta APK
.\gradlew assembleBeta

# Build beta AAB
.\gradlew bundleBeta

# Install beta on device
.\gradlew installBeta
```

### Beta Build Features

✅ **Enabled in Beta**:
- Crashlytics reporting
- Analytics tracking
- Performance monitoring
- Debug logs
- Feedback mechanism
- ProGuard/R8 optimization
- APK signing

❌ **Disabled in Beta**:
- Strict mode errors
- Development toasts
- Mock data
- Test API endpoints

## Test Plan

### Critical Path Testing

**User Registration & Onboarding**
1. First launch
2. Permission requests
3. Onboarding flow
4. Initial setup

**Transaction Management**
1. Add income
2. Add expense
3. Edit transaction
4. Delete transaction
5. Category selection
6. Date picker
7. Amount input validation

**Budget Tracking**
1. Create budget
2. Monitor spending
3. Overspending alerts
4. Budget adjustment

**Scheduled Payments**
1. Add recurring payment
2. Payment reminders
3. Mark as paid
4. Edit schedule
5. Delete payment

**Statistics & Reports**
1. View charts
2. Filter by date
3. Export data
4. Category breakdown

**Settings & Security**
1. Enable biometric auth
2. Change theme
3. Backup data
4. Restore data
5. Clear data

### Edge Cases

- [ ] Empty states (no transactions)
- [ ] Large numbers (millions)
- [ ] Special characters in input
- [ ] Date boundaries (year change)
- [ ] Network interruption during sync
- [ ] Low storage scenarios
- [ ] Battery saver mode
- [ ] App backgrounding/foregrounding
- [ ] Device rotation
- [ ] System language change

### Performance Benchmarks

| Metric | Target | Acceptable | Unacceptable |
|--------|--------|------------|--------------|
| Cold start | < 1.5s | < 2.5s | > 3s |
| Transaction load | < 100ms | < 200ms | > 300ms |
| Chart render | < 200ms | < 400ms | > 500ms |
| Database query | < 50ms | < 100ms | > 150ms |
| Memory usage | < 100MB | < 150MB | > 200MB |
| Battery drain | < 2%/hr | < 4%/hr | > 5%/hr |

## Feedback Collection

### In-App Feedback
```kotlin
// Beta feedback button
FloatingActionButton(
    onClick = { showFeedbackDialog() }
) {
    Icon(Icons.Default.Feedback, "Send Feedback")
}
```

### Crash Reports
- Automatic via Crashlytics
- Include device info, stack trace
- Group similar crashes
- Prioritize by impact

### Survey Questions
1. How easy was it to add a transaction? (1-5)
2. Did you encounter any bugs? (Yes/No + description)
3. What feature would you like to see added?
4. How likely are you to recommend this app? (NPS 0-10)
5. Any other feedback?

## Bug Tracking

### Priority Levels

**P0 - Critical** (Fix immediately)
- App crashes on launch
- Data loss
- Security vulnerabilities
- Payment issues

**P1 - High** (Fix before public release)
- Feature not working
- Major UI issues
- Performance problems
- Accessibility blockers

**P2 - Medium** (Fix in next update)
- Minor UI glitches
- Feature improvements
- Non-critical bugs

**P3 - Low** (Backlog)
- Enhancement requests
- Nice-to-have features
- Cosmetic issues

### Bug Report Template

```markdown
**Title**: [Brief description]

**Priority**: P0/P1/P2/P3

**Steps to Reproduce**:
1. 
2. 
3. 

**Expected Behavior**:

**Actual Behavior**:

**Device Info**:
- Model: 
- Android Version:
- App Version:

**Screenshots/Logs**:

**Additional Context**:
```

## Success Criteria

✅ **Ready for Public Release**:
- [ ] Zero P0 bugs
- [ ] Zero P1 bugs
- [ ] < 0.1% crash rate
- [ ] > 4.0 average rating from beta testers
- [ ] All critical paths tested
- [ ] Performance benchmarks met
- [ ] Positive feedback from 80% of testers
- [ ] Accessibility compliance verified
- [ ] Security audit passed

## Beta Testing Schedule

### Week 1
- **Day 1-2**: Internal testing setup
- **Day 3-5**: Internal team testing
- **Day 6-7**: Fix critical bugs

### Week 2
- **Day 1**: Deploy to closed alpha
- **Day 2-4**: Alpha testing
- **Day 5-7**: Bug fixes and improvements

### Week 3 (if needed)
- **Day 1**: Deploy to open beta
- **Day 2-14**: Public beta testing
- **Day 15**: Final bug fixes

### Week 4
- **Day 1-2**: Final QA
- **Day 3**: Submit to Play Store
- **Day 4-7**: Play Store review
- **Day 7+**: Public release

## Tools & Resources

### Testing Tools
- **Crashlytics**: Crash reporting
- **Firebase Analytics**: User behavior
- **Firebase Performance**: App performance
- **Play Console**: Beta distribution
- **Firebase App Distribution**: Alternative distribution
- **Android Studio Profiler**: Performance testing

### Communication
- **Slack/Discord**: Beta tester group
- **Google Forms**: Feedback surveys
- **GitHub Issues**: Bug tracking
- **Trello/Jira**: Project management

### Documentation
- Beta tester guide
- Known issues list
- FAQ
- Release notes

## Tester Onboarding

### Welcome Email Template

```
Subject: Welcome to HesapGunlugu Beta Program!

Hi [Name],

Thank you for joining the HesapGunlugu beta testing program! Your feedback will help us create the best finance tracking app.

**What to Test**:
- Core features (add/edit transactions)
- Budget tracking
- Statistics and reports
- Overall user experience

**How to Install**:
1. Click this link: [Beta Link]
2. Accept the invitation
3. Install from Play Store
4. Start testing!

**How to Give Feedback**:
- In-app feedback button
- Email: beta@HesapGunlugu.com
- Survey: [Survey Link]

**Timeline**:
Beta testing runs for 2 weeks. We'll release updates every 2-3 days based on your feedback.

**Rewards** (Optional):
- First 50 testers get 1 year Premium free
- Top 10 contributors get lifetime Premium

Questions? Reply to this email or join our Slack: [Slack Link]

Happy testing!
HesapGunlugu Team
```

## Post-Beta Actions

After beta completion:
1. Analyze all feedback
2. Fix remaining bugs
3. Implement quick wins
4. Update documentation
5. Prepare release notes
6. Submit to Play Store
7. Thank beta testers
8. Offer rewards (if applicable)

## Graduation Criteria

Before moving from beta to production:
- [ ] 100+ active beta testers
- [ ] 500+ testing sessions
- [ ] 7+ days average retention
- [ ] All P0/P1 bugs fixed
- [ ] Performance targets met
- [ ] Positive sentiment (>80%)
- [ ] Legal compliance verified
- [ ] Privacy policy reviewed
- [ ] Terms of service finalized
