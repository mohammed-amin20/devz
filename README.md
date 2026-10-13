<div align="center">

<picture>
  <source media="(prefers-color-scheme: light)" srcset="docs/hero-light.svg"/>
  <img src="docs/hero-dark.svg" alt="devZ — Ask. Answer. Grow." width="100%"/>
</picture>

<img src="https://readme-typing-svg.demolab.com?font=Space+Grotesk&weight=600&size=30&duration=3200&pause=900&color=44D8F1&center=true&vCenter=true&width=680&height=70&lines=Ask.+Answer.+Grow.;A+Q%26A+platform+for+developers.;Ask+questions+%C2%B7+ship+answers+%C2%B7+land+jobs." alt="Ask. Answer. Grow."/>

<a href="#-screenshots"><img alt="Screens" src="https://img.shields.io/badge/Screens-28-44D8F1?style=for-the-badge&logo=android&logoColor=white"></a>
<a href="#-architecture"><img alt="Clean Architecture" src="https://img.shields.io/badge/Architecture-Clean_+_MVI-00BCD4?style=for-the-badge&logo=dependencyci&logoColor=white"></a>
<a href="#-tech-stack"><img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.3.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"></a>
<a href="#-tech-stack"><img alt="Jetpack Compose" src="https://img.shields.io/badge/Compose-2026.05-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"></a>
<a href="#-tech-stack"><img alt="Supabase" src="https://img.shields.io/badge/Supabase-3.6.0-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white"></a>
<a href="#-tech-stack"><img alt="Firebase" src="https://img.shields.io/badge/Firebase_FCM-FFCA28?style=for-the-badge&logo=firebase&logoColor=black"></a>
<a href="#-getting-started"><img alt="Min SDK" src="https://img.shields.io/badge/Min_SDK-26-3DDC84?style=for-the-badge&logo=android&logoColor=white"></a>
<a href="#-screenshots"><img alt="Bilingual" src="https://img.shields.io/badge/Bilingual-English_%C2%B7_AR-00BCD4?style=for-the-badge&logo=googletranslate&logoColor=white"></a>
<a href="#-screenshots"><img alt="Themes" src="https://img.shields.io/badge/Themes-Dark_%C2%B7_Light-455A64?style=for-the-badge&logo=materialdesign&logoColor=white"></a>

<a href="https://github.com/mohammed-amin20/devz/stargazers"><img alt="Stars" src="https://img.shields.io/github/stars/mohammed-amin20/devz?style=for-the-badge&logo=github&color=FFD700"></a>
<a href="https://github.com/mohammed-amin20/devz/network/members"><img alt="Forks" src="https://img.shields.io/github/forks/mohammed-amin20/devz?style=for-the-badge&logo=github&color=44D8F1"></a>
<a href="https://github.com/mohammed-amin20/devz/issues"><img alt="Issues" src="https://img.shields.io/github/issues/mohammed-amin20/devz?style=for-the-badge&logo=github&color=FF7A59"></a>
<img alt="Last commit" src="https://img.shields.io/github/last-commit/mohammed-amin20/devz?style=for-the-badge&logo=git&color=44D8F1">
<img alt="Repo size" src="https://img.shields.io/github/repo-size/mohammed-amin20/devz?style=for-the-badge&color=00BCD4">

<br/>

<p>
<b>devZ</b> is a mobile Q&amp;A platform with a built-in job board — ask coding<br/>
questions, earn reputation, follow great developers, hire and get hired.
</p>

<p>
🌐 <b>English</b> &amp; <b>العربية</b> &nbsp;•&nbsp; 🌗 <b>Dark</b> &amp; <b>Light</b> &nbsp;•&nbsp; 📱 Material&nbsp;3 · Compose
</p>

<a href="#-screenshots">Screenshots</a> ·
<a href="#-features">Features</a> ·
<a href="#-architecture">Architecture</a> ·
<a href="#-tech-stack">Tech&nbsp;Stack</a> ·
<a href="#-getting-started">Setup</a>

</div>

---

## ⚡ At a glance

| 🖼️ Screens | 🧩 Kotlin files | 🗄️ Tables | 🧭 Routes | 🛠️ Migrations | 🧪 Test suites |
|:---:|:---:|:---:|:---:|:---:|:---:|
| **28** | **239** | **10** | **22** | **4** | **25** |

> Every screenshot in this README is rendered straight from the real Compose UI by an
> automated **Robolectric + Roborazzi** test — **28 screens × dark & light = 56 images**,
> all shown in [Screenshots](#-screenshots). See [`docs/screenshots/`](docs/screenshots/README.md).

---

## 🎯 What is devZ?

A full-featured developer community app, built as a single-module Android project on
**Clean Architecture + MVI**. It ships three connected products in one:

- 🧠 **A Q&A knowledge base** — post questions with syntax-highlighted code, answer, vote, accept, bookmark, and climb a reputation ladder.
- 💼 **A job board** — companies post roles, developers apply with a cover letter; every posting is admin-approved.
- 🛡️ **A moderation suite** — admins manage users, content, announcements, jobs, companies, and content reports, with real-time push alerts.

---

## ✨ Features

<table>
<tr>
<td width="50%" valign="top">

**🧠 Q&A**
- Two feeds — **For You** & **Following** — with pinned questions on top
- Debounced search across title, body, code & tags
- Infinite scroll + pull-to-refresh
- Custom **syntax highlighting** (Kotlin, JS, Python, generic) + one-tap code copy
- Likes, upvotes, **accept best answer**, bookmarks
- **Gamification** — points for every meaningful interaction

**👤 Profiles & Social**
- Skill chips, tech stack, social links, live stats
- Tabbed question / answer history
- Follow / unfollow with follower & following dialogs
- **Pro badges** — ad-free feed for `is_pro` users

</td>
<td width="50%" valign="top">

**💼 Jobs & Companies**
- Browse & filter approved jobs, apply with a cover letter
- **Company accounts** with a dedicated dashboard
- Post jobs, review applicants, approve proposals
- Company approval workflow (pending → approved)

**🛡️ Admin & Moderation**
- Dashboard with live stat cards
- Manage users · questions · answers · announcements · jobs · companies · reports
- Report questions, answers, jobs or users; dedup enforced client-side
- Each report pings **every admin** (DB notification + FCM push)
- Dismiss · delete content · ban user

**🔔 Platform**
- Real-time **FCM** push (answers, likes, votes, follows, announcements)
- **Deep links** straight to the relevant screen
- Mark-all-read, system announcements, type-aware icons

</td>
</tr>
</table>

---

## 📱 Screenshots

<div align="center">

<sub>Every screen in <b>both themes</b> — <b>28 screens × dark &amp; light = 56 renders</b>. Click any image to open it full-size.</sub>

<br/><br/>

<b>⭐ Featured</b>

<table>
<tr>
<td align="center"><a href="docs/screenshots/feed-content-dark.png"><img src="docs/screenshots/feed-content-dark.png" width="230" alt="Feed"/></a><br/><sub><b>Feed</b><br/>For You + Following, pinned on top</sub></td>
<td align="center"><a href="docs/screenshots/question-details-content-dark.png"><img src="docs/screenshots/question-details-content-dark.png" width="230" alt="Question details"/></a><br/><sub><b>Question details</b><br/>accepted answer + voting</sub></td>
<td align="center"><a href="docs/screenshots/company-dashboard-content-dark.png"><img src="docs/screenshots/company-dashboard-content-dark.png" width="230" alt="Company dashboard"/></a><br/><sub><b>Company dashboard</b><br/>jobs + applicants</sub></td>
<td align="center"><a href="docs/screenshots/admin-dashboard-content-dark.png"><img src="docs/screenshots/admin-dashboard-content-dark.png" width="230" alt="Admin dashboard"/></a><br/><sub><b>Admin dashboard</b><br/>live moderation stats</sub></td>
</tr>
</table>

</div>

### 🚀 Splash & Onboarding

| Screen | 🌙 Dark | ☀️ Light |
|:---|:---:|:---:|
| **Splash**<br/><sub>animated splash</sub> | [<img src="docs/screenshots/splash-content-dark.png" width="200" alt="Splash dark"/>](docs/screenshots/splash-content-dark.png) | [<img src="docs/screenshots/splash-content-light.png" width="200" alt="Splash light"/>](docs/screenshots/splash-content-light.png) |
| **Onboarding**<br/><sub>pager, page 1</sub> | [<img src="docs/screenshots/onboarding-content-dark.png" width="200" alt="Onboarding dark"/>](docs/screenshots/onboarding-content-dark.png) | [<img src="docs/screenshots/onboarding-content-light.png" width="200" alt="Onboarding light"/>](docs/screenshots/onboarding-content-light.png) |

### 🔐 Authentication

| Screen | 🌙 Dark | ☀️ Light |
|:---|:---:|:---:|
| **Login**<br/><sub>empty form</sub> | [<img src="docs/screenshots/login-content-dark.png" width="200" alt="Login dark"/>](docs/screenshots/login-content-dark.png) | [<img src="docs/screenshots/login-content-light.png" width="200" alt="Login light"/>](docs/screenshots/login-content-light.png) |
| **Sign up**<br/><sub>empty form</sub> | [<img src="docs/screenshots/signup-content-dark.png" width="200" alt="Sign up dark"/>](docs/screenshots/signup-content-dark.png) | [<img src="docs/screenshots/signup-content-light.png" width="200" alt="Sign up light"/>](docs/screenshots/signup-content-light.png) |
| **Banned**<br/><sub>suspended account</sub> | [<img src="docs/screenshots/banned-content-dark.png" width="200" alt="Banned dark"/>](docs/screenshots/banned-content-dark.png) | [<img src="docs/screenshots/banned-content-light.png" width="200" alt="Banned light"/>](docs/screenshots/banned-content-light.png) |

### 💬 Feed & Questions

| Screen | 🌙 Dark | ☀️ Light |
|:---|:---:|:---:|
| **Feed**<br/><sub>For You + pinned, bottom nav</sub> | [<img src="docs/screenshots/feed-content-dark.png" width="200" alt="Feed dark"/>](docs/screenshots/feed-content-dark.png) | [<img src="docs/screenshots/feed-content-light.png" width="200" alt="Feed light"/>](docs/screenshots/feed-content-light.png) |
| **Question details**<br/><sub>Q + accepted answer</sub> | [<img src="docs/screenshots/question-details-content-dark.png" width="200" alt="Question details dark"/>](docs/screenshots/question-details-content-dark.png) | [<img src="docs/screenshots/question-details-content-light.png" width="200" alt="Question details light"/>](docs/screenshots/question-details-content-light.png) |
| **Add / Edit Question**<br/><sub>create</sub> | [<img src="docs/screenshots/add-edit-question-content-dark.png" width="200" alt="Add / edit question dark"/>](docs/screenshots/add-edit-question-content-dark.png) | [<img src="docs/screenshots/add-edit-question-content-light.png" width="200" alt="Add / edit question light"/>](docs/screenshots/add-edit-question-content-light.png) |

### 👤 Profile & Settings

| Screen | 🌙 Dark | ☀️ Light |
|:---|:---:|:---:|
| **View profile**<br/><sub>own profile, bottom nav</sub> | [<img src="docs/screenshots/profile-content-dark.png" width="200" alt="Profile dark"/>](docs/screenshots/profile-content-dark.png) | [<img src="docs/screenshots/profile-content-light.png" width="200" alt="Profile light"/>](docs/screenshots/profile-content-light.png) |
| **Admin profile**<br/><sub>+ Admin Panel entry</sub> | [<img src="docs/screenshots/admin-profile-content-dark.png" width="200" alt="Admin profile dark"/>](docs/screenshots/admin-profile-content-dark.png) | [<img src="docs/screenshots/admin-profile-content-light.png" width="200" alt="Admin profile light"/>](docs/screenshots/admin-profile-content-light.png) |
| **Edit profile**<br/><sub>filled</sub> | [<img src="docs/screenshots/edit-profile-content-dark.png" width="200" alt="Edit profile dark"/>](docs/screenshots/edit-profile-content-dark.png) | [<img src="docs/screenshots/edit-profile-content-light.png" width="200" alt="Edit profile light"/>](docs/screenshots/edit-profile-content-light.png) |
| **Settings**<br/><sub>theme + language</sub> | [<img src="docs/screenshots/settings-content-dark.png" width="200" alt="Settings dark"/>](docs/screenshots/settings-content-dark.png) | [<img src="docs/screenshots/settings-content-light.png" width="200" alt="Settings light"/>](docs/screenshots/settings-content-light.png) |

> 🛠️ Admins get an extra **Admin Panel** entry on their profile → Admin Dashboard.

### 🔔 Notifications & Jobs

| Screen | 🌙 Dark | ☀️ Light |
|:---|:---:|:---:|
| **Notifications**<br/><sub>mixed read/unread, bottom nav</sub> | [<img src="docs/screenshots/notifications-content-dark.png" width="200" alt="Notifications dark"/>](docs/screenshots/notifications-content-dark.png) | [<img src="docs/screenshots/notifications-content-light.png" width="200" alt="Notifications light"/>](docs/screenshots/notifications-content-light.png) |
| **Job detail**<br/><sub>approved job</sub> | [<img src="docs/screenshots/job-detail-content-dark.png" width="200" alt="Job detail dark"/>](docs/screenshots/job-detail-content-dark.png) | [<img src="docs/screenshots/job-detail-content-light.png" width="200" alt="Job detail light"/>](docs/screenshots/job-detail-content-light.png) |
| **Post job**<br/><sub>filled form</sub> | [<img src="docs/screenshots/post-job-content-dark.png" width="200" alt="Post job dark"/>](docs/screenshots/post-job-content-dark.png) | [<img src="docs/screenshots/post-job-content-light.png" width="200" alt="Post job light"/>](docs/screenshots/post-job-content-light.png) |

### 🏢 Company

| Screen | 🌙 Dark | ☀️ Light |
|:---|:---:|:---:|
| **Company dashboard**<br/><sub>jobs + applications</sub> | [<img src="docs/screenshots/company-dashboard-content-dark.png" width="200" alt="Company dashboard dark"/>](docs/screenshots/company-dashboard-content-dark.png) | [<img src="docs/screenshots/company-dashboard-content-light.png" width="200" alt="Company dashboard light"/>](docs/screenshots/company-dashboard-content-light.png) |
| **Company profile**<br/><sub>verified, own</sub> | [<img src="docs/screenshots/company-profile-content-dark.png" width="200" alt="Company profile dark"/>](docs/screenshots/company-profile-content-dark.png) | [<img src="docs/screenshots/company-profile-content-light.png" width="200" alt="Company profile light"/>](docs/screenshots/company-profile-content-light.png) |
| **Company job detail**<br/><sub>applicants</sub> | [<img src="docs/screenshots/company-job-detail-content-dark.png" width="200" alt="Company job detail dark"/>](docs/screenshots/company-job-detail-content-dark.png) | [<img src="docs/screenshots/company-job-detail-content-light.png" width="200" alt="Company job detail light"/>](docs/screenshots/company-job-detail-content-light.png) |
| **Edit company profile**<br/><sub>filled</sub> | [<img src="docs/screenshots/edit-company-profile-content-dark.png" width="200" alt="Edit company profile dark"/>](docs/screenshots/edit-company-profile-content-dark.png) | [<img src="docs/screenshots/edit-company-profile-content-light.png" width="200" alt="Edit company profile light"/>](docs/screenshots/edit-company-profile-content-light.png) |
| **Pending approval**<br/><sub>company interstitial</sub> | [<img src="docs/screenshots/pending-approval-content-dark.png" width="200" alt="Pending approval dark"/>](docs/screenshots/pending-approval-content-dark.png) | [<img src="docs/screenshots/pending-approval-content-light.png" width="200" alt="Pending approval light"/>](docs/screenshots/pending-approval-content-light.png) |

### 🛡️ Admin

| Screen | 🌙 Dark | ☀️ Light |
|:---|:---:|:---:|
| **Admin dashboard**<br/><sub>stat cards</sub> | [<img src="docs/screenshots/admin-dashboard-content-dark.png" width="200" alt="Admin dashboard dark"/>](docs/screenshots/admin-dashboard-content-dark.png) | [<img src="docs/screenshots/admin-dashboard-content-light.png" width="200" alt="Admin dashboard light"/>](docs/screenshots/admin-dashboard-content-light.png) |
| **Manage users**<br/><sub>list</sub> | [<img src="docs/screenshots/manage-users-content-dark.png" width="200" alt="Manage users dark"/>](docs/screenshots/manage-users-content-dark.png) | [<img src="docs/screenshots/manage-users-content-light.png" width="200" alt="Manage users light"/>](docs/screenshots/manage-users-content-light.png) |
| **Manage questions**<br/><sub>list</sub> | [<img src="docs/screenshots/manage-questions-content-dark.png" width="200" alt="Manage questions dark"/>](docs/screenshots/manage-questions-content-dark.png) | [<img src="docs/screenshots/manage-questions-content-light.png" width="200" alt="Manage questions light"/>](docs/screenshots/manage-questions-content-light.png) |
| **Manage answers**<br/><sub>list</sub> | [<img src="docs/screenshots/manage-answers-content-dark.png" width="200" alt="Manage answers dark"/>](docs/screenshots/manage-answers-content-dark.png) | [<img src="docs/screenshots/manage-answers-content-light.png" width="200" alt="Manage answers light"/>](docs/screenshots/manage-answers-content-light.png) |
| **Manage announcements**<br/><sub>list</sub> | [<img src="docs/screenshots/manage-announcements-content-dark.png" width="200" alt="Manage announcements dark"/>](docs/screenshots/manage-announcements-content-dark.png) | [<img src="docs/screenshots/manage-announcements-content-light.png" width="200" alt="Manage announcements light"/>](docs/screenshots/manage-announcements-content-light.png) |
| **Manage jobs**<br/><sub>pending approval</sub> | [<img src="docs/screenshots/manage-jobs-content-dark.png" width="200" alt="Manage jobs dark"/>](docs/screenshots/manage-jobs-content-dark.png) | [<img src="docs/screenshots/manage-jobs-content-light.png" width="200" alt="Manage jobs light"/>](docs/screenshots/manage-jobs-content-light.png) |
| **Manage companies**<br/><sub>approval list</sub> | [<img src="docs/screenshots/manage-companies-content-dark.png" width="200" alt="Manage companies dark"/>](docs/screenshots/manage-companies-content-dark.png) | [<img src="docs/screenshots/manage-companies-content-light.png" width="200" alt="Manage companies light"/>](docs/screenshots/manage-companies-content-light.png) |
| **Manage reports**<br/><sub>report list</sub> | [<img src="docs/screenshots/manage-reports-content-dark.png" width="200" alt="Manage reports dark"/>](docs/screenshots/manage-reports-content-dark.png) | [<img src="docs/screenshots/manage-reports-content-light.png" width="200" alt="Manage reports light"/>](docs/screenshots/manage-reports-content-light.png) |

---

## 🏗️ Architecture

devZ is a **single-module Clean Architecture** app with a **uniform MVI contract** in every feature.

```mermaid
flowchart TB
    subgraph P["🧩 Presentation"]
        direction LR
        Screen["Composable Screen"] --> VM["ViewModel · onAction()"] --> State["StateFlow · UiState"]
    end
    subgraph D["🧠 Domain"]
        direction LR
        Repo["Repository interfaces"] --- Model["Domain models"] --- RE["Result · Error"]
    end
    subgraph DT["🗄️ Data"]
        direction LR
        Impl["RepositoryImpl"] --> RDS["RemoteDataSource · Supabase PostgREST"]
        Impl --> Pref["DataStore Preferences"]
        Map["Mappers · toDomain / toData"] -.-> Impl
    end
    VM --> Repo
    Repo -. implemented by .-> Impl
```

Each feature follows the same four-file shape:

| File | Role |
|------|------|
| `XxxAction.kt` | Sealed interface — user intents (`data object` / `data class`) |
| `XxxState.kt` | Data class — `isLoading`, `error: UiText?`, exposed as `StateFlow` |
| `XxxViewModel.kt` | Single `onAction()` dispatcher → repository calls |
| `XxxScreen.kt` | Composable — collects state, fires actions |

<details>
<summary><b>More architecture notes</b></summary>

<br/>

- **Result & Error** — repositories return `Result<D, E : Error>`; errors map to user text via `Error.toUIText()` so ViewModels never hardcode copy.
- **Repository catch pattern** — `PostgrestRestException` → status routing (NotFound / Conflict / Unauthorized), `IOException` → Network, generic `Exception` → Unknown.
- **One-shot events** — a `SharedFlow` (used by Splash) collected in `LaunchedEffect`.
- **DI** — Dagger Hilt (KSP, no kapt); a single `CoreModule` provides Supabase, Postgrest, Storage, the 10 repositories, `UserPreferences`, and `FcmPushSender`.
- **Stateless content composables** — every screen splits a `hiltViewModel()` wrapper from a pure `*Content(state, callbacks)` composable, which is what makes the automated screenshot gallery possible.
- **Preferences** — DataStore (`is_first_time`, `is_logged_in`, `current_account_id`) + a separate FCM-token store.

</details>

---

## 🛠️ Tech Stack

<div align="center">

<img src="https://skillicons.dev/icons?i=kotlin,androidstudio,gradle,firebase,supabase,ktor,git,github,githubactions" alt="Tech stack"/>

</div>

| Category | Technology |
|----------|-----------|
| Language | **Kotlin 2.3.21** (KSP 2.3.7) |
| UI | **Jetpack Compose** (BOM 2026.05.01) + **Material 3** 1.4.0 |
| Architecture | **Clean Architecture + MVI** (single module) |
| DI | **Dagger Hilt 2.59** (KSP) |
| Backend | **Supabase** (PostgREST + Storage) via supabase-kt 3.6.0 |
| Networking | **Ktor Client** 3.5.0 |
| Push | **Firebase Cloud Messaging** 25.1.0 (+ server-side sends via Ktor) |
| Images | Coil 3.4.0 (Compose + OkHttp) |
| Local storage | DataStore Preferences 1.2.1 |
| Serialization | kotlinx.serialization 1.11.0 |
| Navigation | Compose Navigation — type-safe `@Serializable` routes |
| Screenshot testing | Roborazzi 1.76 + Robolectric 4.17 |
| Build | AGP 9.0.1 · Gradle · JDK 21 |
| Min / Target SDK | **26 / 36** |

---

## 🗄️ Data Model

```mermaid
erDiagram
    ACCOUNT ||--o{ QUESTION : asks
    ACCOUNT ||--o{ ANSWER : writes
    ACCOUNT ||--o{ NOTIFICATION : receives
    ACCOUNT ||--o{ JOB_POSTING : posts
    ACCOUNT ||--o{ COMPANY_PROFILE : owns
    ACCOUNT ||--o{ REPORT : files
    QUESTION ||--o{ ANSWER : has
    LANGUAGE_TYPE ||--o{ QUESTION : classifies
    NOTIFICATION_TYPE ||--o{ NOTIFICATION : classifies
    JOB_POSTING ||--o{ JOB_APPLICATION : receives
```

| Table | Key fields |
|-------|-----------|
| `Account` | id, username, fullName, email, imageUrl, bio, techStack, points, fcmToken, followerIds, followingIds, isBanned, isAdmin, isPro, accountType, phoneNumber |
| `Question` | id, title, description, code, tags, likesCount, answersCount, langTypeId, accountId, createdAt, updatedAt, likedAccountIds, isHidden, pinnedUntil |
| `Answer` | id, description, accepted, votedIds, questionId, accountId, createdAt, code |
| `LanguageType` | id, type |
| `Notification` | id, typeId, **userId**, actorId, questionId, answerId, message, isRead, senderType, isGlobal |
| `NotificationType` | id, type |
| `JobPosting` | id, companyName, title, description, salaryRange, jobType, status, createdAt, accountId |
| `JobApplication` | id, jobId, applicantId, coverLetter, status, createdAt, email, whatsapp |
| `CompanyProfile` | id, userId, companyName, logoUrl, website, description, subscriptionStatus, isVerified, location, industry |
| `Report` | id, reporterId, reportedType, reportedId, reason, details, status, createdAt |

**Migrations** — apply in order from the Supabase SQL editor:

| # | File | Purpose |
|---|------|---------|
| 001 | `sql/001_company_profile_fields.sql` | Company profile fields on `Account` |
| 002 | `sql/002_question_updated_at.sql` | `updated_at` column + trigger on `Question` |
| 003 | `sql/003_unique_job_application.sql` | One application per user per job |
| 004 | `sql/004_report.sql` | `Report` table — content reporting + admin alerts |

---

## 🎮 Points System

| Trigger | Points |
|---------|:------:|
| Someone likes your question | **+1** |
| Someone unlikes your question | **−1** |
| Someone upvotes your answer | **+1** |
| Someone removes their upvote | **−1** |
| Someone answers your question | **+1** |
| Your answer is accepted | **+3** |
| Your answer is un-accepted | **−3** |
| Someone follows you | **+1** |
| Someone unfollows you | **−1** |

---

<details>
<summary><b>🏢 Company workflow</b></summary>

<br/>

1. A user signs up and selects **Company** as their account type.
2. They land on a **Pending Approval** screen until an admin reviews them.
3. An admin approves them via **Manage Companies**.
4. Once approved, the company sees a **Company Dashboard** instead of the 5-tab home — their posted jobs and received applications.
5. Posting a job creates a `pending` posting that needs a **separate admin approval** before it appears in the Jobs feed.

</details>

<details>
<summary><b>📢 Content reporting</b></summary>

<br/>

- Report a **question**, **answer**, **job**, or **user** from a bottom sheet with reasons (Spam, Harassment, Inappropriate, Other).
- Duplicate reports from the same reporter for the same target are blocked client-side.
- Each report inserts a `Report` row and fires a **notification + FCM push** to **every admin**.
- Admins triage in **Manage Reports** (filter chips: All / Pending / Reviewed / Dismissed) and can **dismiss**, **delete content**, or **ban the user**.

</details>

---

## 🧭 Navigation

```mermaid
flowchart TD
    Splash -->|first launch| Onboarding --> Auth
    Splash -->|returning / logged out| Auth
    Splash -->|logged in| Home
    Splash -->|banned| Banned
    Auth --> Home
    Home --> Feed --> QuestionDetails
    QuestionDetails --> Profile
    Home --> Add --> AddEditQuestion
    Home --> Notifications
    Home --> Jobs --> JobDetail
    Home --> ProfileTab["Profile"] --> EditProfile
    Auth -->|company| PendingApproval --> CompanyDashboard --> CompanyJobDetail
    ProfileTab --> AdminDashboard --> Manage
```

FCM notifications deep-link to **QuestionDetails**, **Profile**, **JobDetail**, or **ManageReports**.

---

## 🚀 Getting Started

<details open>
<summary><b>Prerequisites</b></summary>

<br/>

- Android Studio Hedgehog or newer
- **JDK 21** (e.g. Android Studio's bundled JBR)
- A **Supabase** project (schema above)
- A **Firebase** project with FCM enabled

</details>

<details open>
<summary><b>Setup</b></summary>

<br/>

**1. Clone**
```bash
git clone https://github.com/mohammed-amin20/devz.git
cd devz
```

**2. Add Supabase credentials** — create `local.properties` in the project root:
```properties
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key
```
> Read at build time and exposed as `BuildConfig.SUPABASE_URL` / `BuildConfig.ANON_KEY`.

**3. Add Firebase config** — drop your `google-services.json` into `app/`.

**4. Add the FCM service account** — place the service-account JSON at
`app/src/main/res/raw/fcm_service_account.json` (used by `FcmPushSender` for
server-side pushes).

**5. Apply migrations** — run `sql/001…004` in order in the Supabase SQL editor.

**6. Build & run**
```bash
./gradlew assembleDebug
```

</details>

---

## 🧪 Testing

```bash
./gradlew test                    # unit tests (JVM)
./gradlew connectedAndroidTest    # instrumented tests (device/emulator)
./gradlew lint                    # lint
```

Unit tests (**JUnit 4 + MockK + kotlinx-coroutines-test + Turbine**) cover Splash / Login / SignUp,
the feed, question details (like / vote / accept / points), add-edit question, profile & edit
profile (follow / points), notifications, data mappers, and domain utilities.

### 📸 Regenerating the screenshots

```bash
./gradlew :app:testDebugUnitTest --tests "*ScreenshotGalleryTest*"
```

Renders all **28 screens in dark & light** straight from the real Compose UI via
**Robolectric + Roborazzi** and writes them to [`docs/screenshots/`](docs/screenshots/README.md).
On first run Robolectric downloads its Android runtime jar (a few minutes). Details and the
full table live in [`docs/screenshots/README.md`](docs/screenshots/README.md).

---

## 📁 Project Structure

<details>
<summary><b>Expand the tree</b></summary>

<br/>

```
app/src/main/java/com/mohamed/devz/
├── DevZApp.kt                    # @HiltAndroidApp + notification channel
├── MainActivity.kt               # @AndroidEntryPoint + FCM deep links
├── navigation/
│   ├── Route.kt                  # 22 type-safe @Serializable routes
│   ├── DevzNavHost.kt            # NavHost wiring
│   └── components/home/          # HomeScreen (5 tabs) · HomeViewModel
├── ui/theme/                     # Color.kt · Theme.kt · Type.kt
└── feature/
    ├── splash/ · onboarding/
    ├── authentication/           # Login · SignUp · Banned
    ├── core/
    │   ├── domain/               # 10 models · 10 repository interfaces · Result/Error
    │   ├── data/                 # RemoteDataSource · DataStore · Mappers · FcmPushSender
    │   ├── presentation/         # DevzBrandHeader · ProBadge · UiText
    │   └── di/                   # CoreModule (Supabase client + repositories)
    ├── question/                 # view_questions · question_details · add_edit_question
    ├── profile/                  # view_profile · edit_profile
    ├── notification/ · report/
    ├── job/                      # jobs_screen · job_detail · post_job
    ├── company/                  # dashboard · profile · job_detail · edit · pending_approval
    └── admin/                    # dashboard · manage users/questions/answers/announcements/jobs/companies/reports
```

</details>

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

Released under the **MIT License** — see [`LICENSE`](LICENSE).

---

<div align="center">

<sub>Built with Kotlin, Compose &amp; ☕</sub>

**Made with ❤️ in Gaza 🇵🇸**

</div>
