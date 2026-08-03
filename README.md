<div align="center">

<img src="https://raw.githubusercontent.com/mohammed-amin20/devz/main/app/src/main/res/drawable/logo.png" width="120"/>

# devZ

### A mobile Q&A platform built for developers

*Ask. Answer. Grow.*

![Kotlin](https://img.shields.io/badge/Kotlin-2.3.20-7F52FF?style=flat&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2026.03-4285F4?style=flat&logo=jetpackcompose&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-3.6.0-3ECF8E?style=flat&logo=supabase&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase%20FCM-orange?style=flat&logo=firebase&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min%20SDK-26-green?style=flat&logo=android)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat)

</div>

---

## 📱 Screenshots

<div align="center">

### Splash & Onboarding
| Splash | Onboarding 1 | Onboarding 2 | Onboarding 3 |
|--------|--------------|--------------|--------------|
| <img src="assets/screenshots/0_splash.jpg" width="180"/> | <img src="assets/screenshots/1.1_onboarding.jpg" width="180"/> | <img src="assets/screenshots/1.2_onboarding.jpg" width="180"/> | <img src="assets/screenshots/1.3_onboarding.jpg" width="180"/> |

### Authentication
| Login | Sign Up | Banned ⚠️ |
|-------|---------|-----------|
| <img src="assets/screenshots/2.1_Login.jpg" width="180"/> | <img src="assets/screenshots/2.2_SignUp.jpg" width="180"/> | <img src="assets/screenshots/2.3_Banned.jpg" width="180"/> |

### Feed & Questions
| Feed — For You | Feed — Following | Question Details | Add / Edit Question |
|----------------|------------------|------------------|---------------------|
| <img src="assets/screenshots/3.1_feed_ForYou.jpg" width="180"/> | <img src="assets/screenshots/3.5_feed_following.jpg" width="180"/> | <img src="assets/screenshots/3.2_QuestionDetails.jpg" width="180"/> | <img src="assets/screenshots/3.3_addEditQuestion.jpg" width="180"/> |

| Code Block | Report Sheet |
|------------|--------------|
| <img src="assets/screenshots/3.4_code_block.jpg" width="180"/> | <img src="assets/screenshots/3.6_reportSheet.jpg" width="180"/> |

### Profile
| View Profile | Edit Profile | Followers / Following | Logout Dialog | Admin Profile |
|--------------|--------------|------------------------|---------------|---------------|
| <img src="assets/screenshots/4.1_profile.jpg" width="180"/> | <img src="assets/screenshots/4.2_editProfile.jpg" width="180"/> | <img src="assets/screenshots/4.3_followersDialog.jpg" width="180"/> | <img src="assets/screenshots/4.4_logoutDialog.jpg" width="180"/> | <img src="assets/screenshots/4.5_adminProfile.jpg" width="180"/> |

> Admins see an additional 🛠️ Admin Panel button on their own profile → Admin Dashboard.

### Notifications
| Notifications | System Announcement |
|---------------|---------------------|
| <img src="assets/screenshots/5.1_notification.jpg" width="180"/> | <img src="assets/screenshots/5.2_systemAnnouncemen.jpg" width="180"/> |

### Jobs
| Jobs | Job Detail | Apply Sheet | Post Job |
|------|------------|-------------|----------|
| <img src="assets/screenshots/7.1_jobs.jpg" width="180"/> | <img src="assets/screenshots/7.2_jobDetail.jpg" width="180"/> | <img src="assets/screenshots/7.3_applySheet.jpg" width="180"/> | <img src="assets/screenshots/7.4_postJob.jpg" width="180"/> |

### Company
| Company Dashboard | Company Profile | Company Job Detail | Edit Company Profile | Pending Approval |
|-------------------|-----------------|--------------------|----------------------|-------------------|
| <img src="assets/screenshots/8.1_companyDashboard.jpg" width="180"/> | <img src="assets/screenshots/8.2_companyProfile.jpg" width="180"/> | <img src="assets/screenshots/8.3_companyJobDetail.jpg" width="180"/> | <img src="assets/screenshots/8.4_editCompanyProfile.jpg" width="180"/> | <img src="assets/screenshots/8.5_pendingApproval.jpg" width="180"/> |

### Admin
| Admin Dashboard | Manage Users | Manage Questions | Manage Answers |
|-----------------|--------------|------------------|----------------|
| <img src="assets/screenshots/9.1_adminDashboard.jpg" width="180"/> | <img src="assets/screenshots/9.2_manageUsers.jpg" width="180"/> | <img src="assets/screenshots/9.3_manageQuestions.jpg" width="180"/> | <img src="assets/screenshots/9.4_manageAnswers.jpg" width="180"/> |

| Manage Announcements | Manage Jobs | Manage Companies | Manage Reports | Report Detail Sheet |
|----------------------|-------------|------------------|----------------|---------------------|
| <img src="assets/screenshots/9.5_manageAnnouncements.jpg" width="180"/> | <img src="assets/screenshots/9.6_manageJobs.jpg" width="180"/> | <img src="assets/screenshots/9.7_manageCompanies.jpg" width="180"/> | <img src="assets/screenshots/9.8_manageReports.jpg" width="180"/> | <img src="assets/screenshots/9.9_reportDetailSheet.jpg" width="180"/> |

</div>

> ⚠️ Screens marked with ⚠️ are temporary placeholders and incomplete.

---


## 🔥 Features

- **Q&A Feed** — Two feeds ("For You" / "Following") with pinned questions on top, infinite scroll, pull-to-refresh, and paginated loading
- **Search** — Debounced keyword search across question title, description, code, and tags
- **Syntax Highlighting** — Custom tokenizer supporting Kotlin, JavaScript, Python, and a generic fallback
- **Code Copy** — One-tap clipboard copy for any code block
- **Voting & Acceptance** — Upvote answers, accept the best answer, and like/unlike questions
- **Gamification** — Points system rewarding every meaningful interaction (likes, upvotes, accepted answers, follows)
- **Profiles** — Full profile with skill chips, tech stack, social links, stats, and tabbed question/answer history
- **Follow System** — Follow/unfollow other developers with followers and following dialogs
- **Jobs Board** — Browse approved job postings, filter by job type, and apply with a cover letter
- **Company Accounts** — Company profiles with a dedicated dashboard, job posting, and applicant review
- **Admin Panel** — Dashboard with stats and moderation for users, questions, answers, announcements, jobs, companies, and reports
- **Content Reporting** — Report questions, answers, jobs, or users; admins get real-time alerts and can dismiss, delete, or ban
- **Push Notifications** — Real-time FCM notifications for answers, likes, votes, new followers, and system announcements
- **Mark All Read** — One-tap clear of unread notifications
- **Deep Links** — Notification tap navigates directly to the relevant question, profile, job, or report
- **Pro Badges** — Pro users get a badge and an ad-free feed
- **Dark Theme** — Dark-only Material Design 3 UI optimized for developer aesthetics

---

## 🏗️ Architecture

devZ follows **Clean Architecture** with the **MVI (Model-View-Intent)** pattern across all features:

```
Presentation Layer
  └── Composable Screen → ViewModel (onAction) → StateFlow<UiState>
          ↓
Domain Layer
  └── Repository Interfaces · Domain Models · Result<D, E> · Error
          ↓
Data Layer
  └── RepositoryImpl · RemoteDataSource (Supabase PostgREST)
      DataStore (UserPreferences) · Mappers
```

Every feature follows a **uniform MVI contract**:

| File | Role |
|------|------|
| `XxxAction.kt` | Sealed interface — user intents |
| `XxxState.kt` | Data class — `StateFlow<UiState>` |
| `XxxViewModel.kt` | Single `onAction()` dispatcher |
| `XxxScreen.kt` | Composable — collects state, fires actions |

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin 2.3.20 |
| UI | Jetpack Compose (BOM 2026.03.01) + Material3 |
| Architecture | Clean Architecture + MVI |
| DI | Dagger Hilt 2.51.1 (KSP) |
| Backend | Supabase (PostgREST + Storage) via supabase-kt 3.6.0 |
| HTTP | Ktor Android |
| Push Notifications | Firebase Cloud Messaging |
| Server-side FCM | Ktor + Firebase service account (`res/raw/fcm_service_account.json`) |
| Image Loading | Coil (Compose + OkHttp) |
| Local Storage | DataStore Preferences |
| Serialization | Kotlinx Serialization JSON |
| Navigation | Compose Navigation (type-safe `@Serializable` routes) |
| Fonts | Inter (body) + Space Grotesk (titles) |
| Min / Target SDK | 26 / 36 |

---

## 🗄️ Database Schema

| Table | Key Fields |
|-------|-----------|
| `Account` | id, username, fullName, email, imageUrl, bio, techStack, points, fcmToken, followerIds, followingIds, isBanned, isAdmin, isPro, accountType, phoneNumber |
| `Question` | id, title, description, code, tags, likesCount, answersCount, langTypeId, accountId, createdAt, updatedAt, likedAccountIds, isHidden, pinnedUntil |
| `Answer` | id, description, accepted, votedIds, questionId, accountId, createdAt, code |
| `LanguageType` | id, type |
| `Notification` | id, typeId, userId, actorId, questionId, answerId, message, isRead, senderType, isGlobal |
| `NotificationType` | id, type |
| `JobPosting` | id, companyName, title, description, salaryRange, jobType, status, createdAt, accountId |
| `JobApplication` | id, jobId, applicantId, coverLetter, status, createdAt, email, whatsapp |
| `CompanyProfile` | id, userId, companyName, logoUrl, website, description, subscriptionStatus, isVerified, location, industry |
| `Report` | id, reporterId, reportedType, reportedId, reason, details, status, createdAt |

---

## 🎮 Points System

| Trigger | Points |
|---------|--------|
| Someone likes your question | +1 |
| Someone unlikes your question | −1 |
| Someone upvotes your answer | +1 |
| Someone removes their upvote | −1 |
| Someone answers your question | +1 |
| Your answer is accepted | +3 |
| Your answer is un-accepted | −3 |
| Someone follows you | +1 |
| Someone unfollows you | −1 |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 21+
- A Supabase project with the schema above
- A Firebase project with FCM enabled

### Setup

1. **Clone the repository**
```bash
git clone https://github.com/mohammed-amin20/devz.git
cd devz
```

2. **Add credentials** — Create a `local.properties` file in the project root:
```properties
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key
```

3. **Add Firebase config** — Place your `google-services.json` inside `app/`

4. **Add the Firebase service account** — Place your service-account JSON at `app/src/main/res/raw/fcm_service_account.json` (used for server-side push notifications)

5. **Apply the SQL migrations** — Run `sql/001…004` in the Supabase SQL editor (004 adds the `Report` table, required for content reporting + admin pushes)

6. **Build and run**
```bash
./gradlew assembleDebug
```

---

## 🧪 Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# Lint
./gradlew lint
```

Unit tests use **JUnit 4 + MockK + kotlinx-coroutines-test + Turbine** and cover:
- Splash / Login / SignUp ViewModels
- Feed (View Questions) ViewModel (load, search, bookmarks, error states)
- Question Details ViewModel (like, vote, accept, points)
- Add / Edit Question ViewModel
- Profile + Edit Profile ViewModels (follow/unfollow, points)
- Notifications ViewModel (load, mark read)
- Data mappers (Account, Question, Answer, Notification, LanguageType)
- Domain utils (Result, Error→UiText, Answer vote toggle, TimeFormatter, UiText)

---

## 📁 Project Structure

```
app/src/main/java/com/mohamed/devz/
├── DevZApp.kt                    # @HiltAndroidApp + notification channel
├── MainActivity.kt               # @AndroidEntryPoint + deep links (FCM extras)
├── navigation/
│   ├── Route.kt                  # 21 type-safe @Serializable routes
│   ├── DevzNavHost.kt            # NavHost wiring
│   └── components/home/          # HomeScreen (5 tabs) · HomeViewModel
├── ui/theme/                     # Color.kt · Theme.kt · Type.kt
└── feature/
    ├── splash/
    ├── onboarding/
    ├── authentication/           # Login · SignUp · Banned
    ├── core/
    │   ├── domain/               # 10 models · Repository interfaces · Result/Error
    │   ├── data/                 # RemoteDataSource · DataStore · Mappers · Repos · FcmPushSender
    │   ├── presentation/         # DevzBrandHeader · ProBadge · UiText
    │   └── di/                   # CoreModule (Supabase client + all repositories)
    ├── question/                 # view_questions · question_details · add_edit_question
    ├── profile/                  # view_profile · edit_profile
    ├── notification/
    ├── report/                   # ReportSheet · ReportViewModel
    ├── job/                      # jobs_screen · job_detail · post_job
    ├── company/                  # company_dashboard · company_profile · company_job_detail · edit_company_profile · pending_approval
    └── admin/                    # admin_dashboard · manage_users · manage_questions · manage_answers · manage_announcements · manage_jobs · manage_companies · manage_reports
```

---

## 🧭 Navigation

```
Splash ──► Onboarding (first time)
       └──► Auth ──► Home
                       ├── Feed Tab          ──► QuestionDetails ──► Profile
                       │                     └──► AddEditQuestion
                       ├── Add Tab           ──► AddEditQuestion
                       ├── Notifications Tab
                       ├── Jobs Tab          ──► JobDetail ──► Apply sheet
                       └── Profile Tab       ──► EditProfile
Company accounts:
  Auth (company) ──► PendingApproval ──► Home ──► CompanyDashboard
                       └──► CompanyJobDetail ──► applicants

Admin (via Profile tab or Jobs tab):
  AdminDashboard ──► ManageUsers · ManageQuestions · ManageAnswers · ManageAnnouncements
                 └──► ManageJobs · ManageCompanies · ManageReports
```

FCM notifications deep-link to **QuestionDetails**, **Profile**, **JobDetail**, or **ManageReports**.

---

## 👥 Team

| Name | Student ID |
|------|-----------|
| Mohammed Amen Ghazal | 120211235 |
| Saleem Kamel Abu Aser | 120212488 |
| Tareq Mohammed Almasry | 120210875 |

**Supervised by:** Dr. Rabhi Baraka  
**Faculty of Information Technology — Islamic University of Gaza**

---

## 📄 License

```
MIT License — feel free to use, modify, and distribute.
```

---

<div align="center">

Made with ❤️ in Gaza 🇵🇸

</div>
