# Product Requirements Document

## Mahila-Shakti Unnati

**Product Type:** Android mobile application  
**Domain:** Self-Help Group micro-finance ledger  
**Target Users:** Women-led Self-Help Groups, SHG leaders, field coordinators, NGO workers, and rural finance facilitators  
**Platform:** Android  
**Primary Market:** India  
**Default Phone Format:** `+91XXXXXXXXXX`

---

## 1. Product Overview

Mahila-Shakti Unnati is a digital ledger application designed for women-led Self-Help Groups. The app replaces physical register books with a simple mobile-first system for managing members, weekly savings, loans, repayment status, reports, and member summaries.

The product acts as a practical "digital accountant" for SHGs. It helps users track member contributions, calculate group savings, prevent duplicate or risky loans, export reports, and maintain transparent financial records.

---

## 2. Problem Statement

Many Self-Help Groups still manage savings and loan records in handwritten registers. This creates several real-world problems:

- Records can be lost, damaged, or hard to verify.
- Weekly savings entries are error-prone.
- Loan eligibility and unpaid loans are difficult to track manually.
- Members may dispute payment history due to lack of clear records.
- Leaders often need to prepare summaries manually for meetings.
- Digital literacy barriers make complex financial tools hard to adopt.

Mahila-Shakti Unnati solves these problems by offering a simple Android app focused on the actual workflow of SHG savings and loans.

---

## 3. Goals

### Product Goals

- Digitize member and savings records for SHGs.
- Make weekly savings tracking fast and reliable.
- Prevent new loans when a member already has an unpaid loan.
- Show simple interest and total payable amounts clearly.
- Provide quick summaries for WhatsApp sharing and PDF reports.
- Support Indian mobile number format consistently using `+91`.
- Keep the app simple enough for low digital-literacy users.

### User Goals

- Add and manage SHG members easily.
- Record weekly savings as Paid or Pending.
- Track active and completed loans.
- View total group savings and loan amounts.
- Share member summaries without manual writing.
- Use the app confidently during SHG meetings.

---

## 4. Non-Goals

The first production version will not focus on:

- Full banking integration.
- Automated UPI collections.
- Credit bureau reporting.
- Advanced accounting ledgers.
- Multi-branch enterprise administration.
- Real-time chat between members.
- Complex role-based permission systems.

These may be considered in future versions after core ledger workflows are stable.

---

## 5. Target Users

### Primary User: SHG Leader

The SHG leader maintains member records, savings entries, and loan information. She needs a quick and trustworthy system that works during weekly meetings.

### Secondary User: Field Coordinator

The coordinator may help multiple groups review records, export reports, and verify savings or loan status.

### Tertiary User: SHG Member

Members may not operate the app directly, but they benefit from transparent summaries and payment history.

---

## 6. Key User Journeys

### 6.1 Registration

1. User opens the app.
2. User registers using an Indian phone number with default `+91`.
3. User chooses OTP or password-based registration.
4. App validates that the phone number has exactly 10 digits after `+91`.
5. App prevents duplicate phone registration.
6. User completes registration and reaches the home/dashboard screen.

### 6.2 Login

1. User opens the app.
2. Phone number field defaults to `+91`.
3. User enters the 10-digit mobile number.
4. User logs in using OTP or password.
5. App verifies the number and allows access.

### 6.3 Add Member

1. User opens the dashboard.
2. User enters member name and phone number.
3. Phone number defaults to `+91`.
4. App validates the member name and phone number.
5. App prevents duplicate member phone numbers.
6. Member appears in the member list.

### 6.4 Record Weekly Savings

1. User opens a member detail page.
2. User enters savings amount.
3. User selects week.
4. User selects status as Paid or Pending.
5. App saves the entry in Room database.
6. Total savings updates immediately.

### 6.5 Add Loan

1. User opens a member detail page.
2. User enters loan amount and date.
3. App checks whether the member has an unpaid loan.
4. If unpaid loan exists, app blocks the new loan.
5. If no unpaid loan exists, app saves the loan.
6. App displays interest and total payable amount.

### 6.6 Share Member Summary

1. User opens member detail page.
2. User taps WhatsApp summary share.
3. App creates a clean text summary.
4. User shares it through WhatsApp or another sharing app.

### 6.7 Export Reports

1. User opens dashboard or reports section.
2. User generates a PDF or export file.
3. App includes savings, members, and loan information.
4. User can open or share the report.

---

## 7. Core Features

### 7.1 Authentication

**Requirements**

- Support phone-based OTP authentication.
- Support password login where enabled.
- Default phone input should be `+91`.
- Validate phone as Indian mobile number: `+91` plus 10 digits.
- Prevent duplicate registration.
- Show clear error messages for invalid or duplicate phone numbers.

**Acceptance Criteria**

- User cannot submit a phone number shorter or longer than 10 digits after `+91`.
- Pasted values such as `9876543210` should normalize to `+919876543210`.
- Pasted values such as `+919876543210` should remain valid.
- Duplicate phone registration should show a warning.

### 7.2 Member Directory

**Requirements**

- Add member name and phone number.
- Optionally support member photo.
- Edit member details.
- View member list.
- Search members by name or phone.
- Prevent duplicate phone numbers.

**Acceptance Criteria**

- New member appears immediately after saving.
- Invalid phone number blocks save.
- Duplicate phone number blocks save.
- Edited member phone number is normalized to `+91XXXXXXXXXX`.

### 7.3 Savings Management

**Requirements**

- Add weekly savings amount.
- Add week label.
- Select status: Paid or Pending.
- Show savings history per member.
- Update total paid savings instantly.

**Acceptance Criteria**

- Paid savings affect total savings.
- Pending savings are stored but do not incorrectly inflate paid savings.
- Savings entries persist after app restart.

### 7.4 Loan Tracker

**Requirements**

- Add member loan amount.
- Track disbursement date.
- Show loan status as Paid or Pending.
- Prevent new loan if member has unpaid loan.
- Calculate simple interest and total payable.

**Acceptance Criteria**

- App blocks a new loan when unpaid loan exists.
- Marking a loan as paid allows a future loan.
- Interest and total payable display correctly.

### 7.5 Reports and Export

**Requirements**

- Generate PDF report.
- Share PDF report.
- Export data in a clean format.
- Import CSV data with quoted and escaped fields handled correctly.

**Acceptance Criteria**

- PDF opens successfully on device.
- Shared report contains readable SHG summary.
- CSV import handles commas, quotes, and multiline quoted fields.

### 7.6 WhatsApp Summary

**Requirements**

- Generate member summary text.
- Include member name, phone, total paid savings, and report label.
- Use Android share intent.

**Acceptance Criteria**

- User can share the summary through WhatsApp.
- Summary text is clean and understandable.

### 7.7 Language Support

**Requirements**

- Provide language selector.
- Support English and Kannada resources where available.
- Avoid duplicate or unused string resources.

**Acceptance Criteria**

- Changing language updates visible text.
- Missing translations should not crash the app.

### 7.8 Cloud Sync Hooks

**Requirements**

- Provide sync-to-cloud and fetch-from-cloud actions.
- Show sync status.
- Keep Room database as local source for offline use.

**Acceptance Criteria**

- User sees syncing, success, and error states.
- Local app remains usable if cloud sync fails.

---

## 8. Data Requirements

### Member

- ID
- Name
- Phone number
- Photo URI
- User ID
- Join date
- Active status
- Created timestamp
- Updated timestamp

### Savings

- ID
- Member ID
- Amount
- Week
- Date
- Status: Paid or Pending
- User ID
- Created timestamp
- Updated timestamp
- Deleted status

### Loan

- ID
- Member ID
- Principal amount
- Disbursement date
- Due date
- Paid status
- User ID
- Created timestamp
- Updated timestamp
- Deleted status

---

## 9. Functional Requirements

- App must work on Android devices.
- App must store core data locally using Room.
- App must update dashboard totals automatically using observable data.
- App must validate all required fields before saving.
- App must normalize mobile numbers to `+91XXXXXXXXXX`.
- App must prevent duplicate member phone numbers.
- App must prevent duplicate user registration by phone number.
- App must block new loans for members with unpaid loans.
- App must support report sharing.
- App must support member summary sharing.

---

## 10. Non-Functional Requirements

### Usability

- Interface should be simple, readable, and meeting-friendly.
- Primary actions should be easy to find.
- Error messages should be clear and practical.

### Reliability

- Savings, members, and loans must persist after app restart.
- Local database should remain usable offline.
- Import/export should avoid data corruption.

### Performance

- Member list and dashboard totals should update quickly.
- Common actions should feel instant on entry-level Android phones.

### Security

- OTP authentication should use Firebase Phone Auth.
- Sensitive configuration should not be hardcoded in source files.
- User data should be scoped by authenticated user where applicable.

### Localization

- User-facing text should use string resources.
- Translation keys should be consolidated to reduce maintenance overhead.

---

## 11. Success Metrics

- User can register and log in without support.
- User can add a member in under 30 seconds.
- User can record weekly savings in under 20 seconds.
- Dashboard total savings updates immediately after a paid entry.
- App blocks 100% of attempted duplicate active loans for the same member.
- Reports and WhatsApp summaries are shareable without manual editing.
- Phone numbers are stored consistently as `+91XXXXXXXXXX`.

---

## 12. Edge Cases

- User pastes phone number without `+91`.
- User pastes phone number with spaces or hyphens.
- User tries to delete `+91` from phone field.
- User adds duplicate member phone number.
- User registers with an already registered phone number.
- User adds savings with blank week.
- User adds zero or negative savings amount.
- User tries to add loan while unpaid loan exists.
- User imports CSV containing commas, quotes, or line breaks.
- Cloud sync fails while local data exists.

---

## 13. Release Criteria

The app is ready for release when:

- Debug and release builds compile successfully.
- Registration and login work on a real Android device.
- Member add/edit flows work with default `+91`.
- Savings entries save correctly with Paid/Pending status.
- Loan prevention works for unpaid loans.
- Interest and total payable are visible in loan tracker.
- PDF export and WhatsApp sharing work.
- CSV import/export handles escaped data correctly.
- Basic language switching works.
- No critical crash occurs during main user journeys.

---

## 14. Future Enhancements

- Role-based access for SHG leader and field coordinator.
- Member passbook view.
- Monthly meeting summary.
- UPI payment reference tracking.
- Backup and restore.
- Advanced analytics for savings trends.
- Loan eligibility score based on savings history.
- Offline-first conflict resolution for cloud sync.
- Voice-assisted entry for low-literacy users.
- More Indian language support.

---

## 15. Product Opinion

Mahila-Shakti Unnati has strong real-world potential because it focuses on an actual operational pain point: SHG ledger management. The most important strength is that the app does not try to become a full banking product too early. It stays close to the everyday workflow of adding members, recording savings, tracking loans, and sharing summaries.

For real-world adoption, the app should prioritize trust, simplicity, and consistency over advanced features. The most valuable production improvements would be stronger backup, clearer member history, safer authentication setup, and a more polished meeting-time dashboard. If these areas are handled well, the app can be genuinely useful for small SHGs, NGO demos, and rural finance training.

