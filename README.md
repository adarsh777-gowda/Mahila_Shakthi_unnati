# Mahila Shakti Unnati

Mahila Shakti Unnati is an Android digital ledger app for women's Self-Help Groups (SHGs). It helps SHG administrators manage members, record weekly savings, track loans, prevent duplicate unpaid loans, and share financial summaries through WhatsApp-style text exports and reports.

The app is built as a practical micro-finance workflow for small community groups that currently rely on paper registers.

## Key Features

- Member directory with member details, phone numbers, and photo support
- Weekly savings entry with week and Paid/Pending status
- Room database persistence for members, savings, and loans
- Live dashboard totals using Kotlin Flow
- Loan tracker with simple interest and total payable display
- Loan prevention rule: a member cannot receive a new loan while an unpaid loan exists
- Member summary sharing through Android share intents
- CSV import/export with quoted-field parsing
- Report screen for members, savings, loans, and meeting-related summaries
- Firebase Phone Auth based OTP flow
- Local password/phone registration support for demo usage
- Kannada/English language resources
- Practical and advanced feature sections for future SHG tools
- PDF/report generation utilities
- Cloud sync hooks using Firebase/Firestore structure

## Real-World User Flow

1. Register or log in using phone/password or OTP.
2. Open the home dashboard.
3. Add SHG members with phone details.
4. Open a member profile.
5. Add weekly savings entries with Paid or Pending status.
6. Add loans only when the member has no unpaid loan.
7. Review interest and total payable in loan history.
8. Export member summaries or generate group reports.

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Room Database
- Kotlin Coroutines and Flow
- Firebase Authentication
- Firebase Firestore
- Hilt
- Android Navigation Compose
- WorkManager
- Coil
- iText PDF

## Project Structure

```text
app/src/main/java/com/adarsh/mahilashaktiunnati/
  ai/                 AI assistant service logic
  data/               User manager, sync managers, Firestore hooks
  data/dao/           Room DAO interfaces
  data/database/      Room database and migrations
  data/entities/      Member, Savings, Loan entities
  data/repository/    Repository layer
  features/           Practical and advanced feature helpers
  ui/components/      Reusable Compose components
  ui/navigation/      Navigation routes and graph
  ui/screens/         Login, register, dashboard, member, report screens
  ui/theme/           App theme and design system
  utils/              Validation, export/import, language utilities
  viewmodel/          Auth, member, AI view models
```

## Requirements

- Android Studio
- Android SDK 35
- JDK 11
- Firebase project configured for Android package:

```text
com.adarsh.mahilashaktiunnati
```

## Firebase Phone OTP Setup

Phone OTP requires Firebase project setup outside the codebase.

1. Open Firebase Console.
2. Select project `mahila-shakti-unnati`.
3. Go to Project settings.
4. Select the Android app with package name:

```text
com.adarsh.mahilashaktiunnati
```

5. Add the debug SHA fingerprints.

Debug SHA-1:

```text
4D:B2:AA:7C:09:5F:91:75:D4:2F:E2:0B:2A:9F:73:E1:B8:B9:47:11
```

Debug SHA-256:

```text
0F:BA:CE:52:C2:99:63:EA:14:DB:25:E4:5D:66:E2:B5:91:5D:41:8A:90:6E:BF:32:32:B9:38:5E:51:D5:08:CE
```

6. Go to Authentication > Sign-in method.
7. Enable Phone provider.
8. Download the updated `google-services.json`.
9. Place it here:

```text
app/google-services.json
```

For debug builds, the app forces Firebase Phone Auth to use the reCAPTCHA flow to avoid emulator Play Integrity failures.

## Build and Run

From the project root:

```powershell
.\gradlew.bat assembleDebug
```

In Android Studio:

1. Select the `app` run configuration.
2. Select an emulator or physical Android device.
3. Click Run.

If OTP behaves unexpectedly after Firebase changes, uninstall the app from the emulator/device and run it again.

## Testing OTP

Use full Indian phone format:

```text
+91XXXXXXXXXX
```

If Firebase rejects OTP requests, check Logcat for `Auth` or `FirebaseAuth` messages. Common causes:

- Phone provider is not enabled.
- SHA-1/SHA-256 fingerprints are missing or not saved.
- Old `google-services.json` is still in the project.
- App was not reinstalled after changing Firebase config.
- Firebase quota/rate limit was reached.

## Data Model

The app stores core ledger data in Room:

- `Member`: SHG member profile and phone details
- `Savings`: weekly contribution records with week, date, amount, and status
- `Loan`: principal, repayment status, dates, interest-related fields

Room migrations are included for schema evolution.

## Important Business Rules

- Only Paid savings count toward total paid savings.
- Savings entries store both week and status.
- Loan creation is blocked if the member has any unpaid loan.
- Loan cards show interest and total payable.
- CSV import handles quoted commas, escaped quotes, and quoted newlines.

## Current Limitations

- Firebase/Firestore sync is present as hooks and utilities, but full conflict resolution is not complete.
- Demo user registration is still lightweight and should be replaced with a production-grade user profile model.
- Meeting reports are currently informational rather than backed by a dedicated meeting table.
- Some advanced/practical tools are scaffolded for future implementation.
- Production release signing fingerprints must be added to Firebase separately.

## Recommended Next Steps

- Add production release signing config and Firebase release SHA fingerprints.
- Replace demo/local user registration with full Firebase user profiles.
- Add a group model for multiple SHGs.
- Add dedicated meeting attendance records.
- Add instrumented tests for savings totals, loan prevention, CSV import/export, and Room migrations.
- Complete Firestore sync for members, savings, loans, deletes, and conflict handling.

## Build Status

Current verified command:

```powershell
.\gradlew.bat assembleDebug
```

Expected result:

```text
BUILD SUCCESSFUL
```

## License

This project is intended for educational and prototype use. Add a formal license before publishing or distributing publicly.
