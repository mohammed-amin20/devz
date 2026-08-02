# DevZ — Android Q&A + Job Board App for Developers

A feature-rich question-and-answer platform with a built-in job board, built with **Kotlin** + **Jetpack Compose** and backed by **Supabase**. Users can ask coding questions, provide answers, vote, bookmark, edit their profile, browse and apply for jobs, register as a company, and receive push notifications. Admins get a full dashboard to manage users, content, jobs, companies, and send system-wide announcements.

---

## Architecture

### Clean Architecture + MVI (single-module)

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  Composable Screen → ViewModel (MVI) → UiState / UiEvent    │
│                                            ↓                 │
├─────────────────────────────────────────────────────────────┤
│                    Domain Layer                              │
│  Repository interfaces · Domain models · Error / Result     │
│                                            ↓                 │
├─────────────────────────────────────────────────────────────┤
│                    Data Layer                                │
│  RepositoryImpl · RemoteDataSource (Supabase PostgREST)     │
│  DataStore (UserPreferences) · Mappers (↔ Domain models)    │
└─────────────────────────────────────────────────────────────┘
```

### MVI Pattern (every feature)

Each feature follows a uniform MVI contract:

| File                | Role                                    |
|---------------------|-----------------------------------------|
| `XxxAction.kt`      | Sealed interface — user intents         |
| `XxxState.kt`       | Data class — `StateFlow<UiState>`       |
| `XxxViewModel.kt`   | `onAction()` dispatcher, repo calls     |
| `XxxScreen.kt`      | Composable — collects state, fires actions |

- **Action** — sealed interface with `data object` for intents, `data class` for input changes, optional `onSuccess: () -> Unit` for navigation callbacks.
- **State** — data class holding form fields, `isLoading: Boolean`, `error: UiText?`.
- **Events** (one-shot) — `SharedFlow`, collected in `LaunchedEffect` (used in Splash).
- **ViewModel** — single `onAction(action)` entry point dispatching via `when`.

---

## Tech Stack

| Category            | Technology                                                    |
|---------------------|---------------------------------------------------------------|
| Language            | Kotlin 2.3.20 (language version 1.9)                         |
| UI                  | Jetpack Compose (BOM 2026.03.01), Material3 1.4.0            |
| DI                  | Dagger Hilt 2.51.1 (KSP, no kapt)                            |
| Backend             | Supabase (PostgREST + Storage) via supabase-kt 3.6.0         |
| HTTP client         | Ktor Android                                                  |
| Local storage       | DataStore Preferences                                         |
| Image loading       | Coil (Compose + OkHttp)                                       |
| Fonts               | Google Fonts (Inter, Space Grotesk)                           |
| Serialization       | Kotlinx Serialization JSON                                    |
| Push notifications  | Firebase Cloud Messaging (per-device + topic)                 |
| Image upload        | Supabase Storage (`images` bucket)                            |
| Build               | AGP 8.10.1, Gradle 8.11.1                                    |
| Min / Target SDK    | 26 / 36                                                      |
| Navigation          | Compose Navigation with type-safe `@Serializable` routes      |

**Declared but unused:** `androidx.room.ktx` (no Room compiler, database, entities, or DI wiring).

---

## Project Structure

All 199 source files live under `app/src/main/java/com/mohamed/devz/`.

```
com.mohamed.devz/
├── DevZApp.kt                           # @HiltAndroidApp, creates notification channel
├── MainActivity.kt                      # @AndroidEntryPoint, deep link handling
│
├── navigation/
│   ├── Route.kt                         # @Serializable sealed interface — 21 routes
│   ├── DevzNavHost.kt                   # NavHost wiring all routes
│   └── components/home/
│       ├── HomeScreen.kt                # Bottom nav shell (Feed/Jobs/Notifications/Profile)
│       └── HomeViewModel.kt             # Exposes currentAccountId + accountType for cross-tab nav
│
├── ui/theme/
│   ├── Color.kt                         # Dark palette (CyanPrimary, DevzCard, Q* colors)
│   ├── Theme.kt                         # DevzTheme — dark-only, transparent bars
│   └── Type.kt                          # Inter (body), Space Grotesk (titles)
│
└── feature/
    ├── splash/presentation/             # Animated splash, DataStore-driven routing
    ├── onboarding/presentation/         # 3-page HorizontalPager with dot indicators
    │
    ├── authentication/presentation/
    │   ├── AuthScreen.kt                # Toggle container (Login/SignUp)
    │   └── components/
    │       ├── login_screen/presentation/   # Login form
    │       └── signup_screen/presentation/  # Sign-up form (5 fields + company toggle)
    │
    ├── core/
    │   ├── domain/
    │   │   ├── model/                   # 9 domain models (Account, Question, Answer,
    │   │   │                            #  LanguageType, Notification, NotificationType,
    │   │   │                            #  JobPosting, JobApplication, CompanyProfile)
    │   │   ├── repository/              # 9 repository interfaces
    │   │   └── util/                    # Error, Result, FcmTokenUtil
    │   ├── data/
    │   │   ├── data_source/
    │   │   │   ├── remote/              # DevZRemoteDataSource (impl), FCM service
    │   │   │   └── local/               # UserPreferences (DataStore), FcmPushSender
    │   │   ├── model/                   # @Serializable data models (snake_case mapping)
    │   │   ├── mapper/                  # toDomain() / toData() extensions
    │   │   └── repository/              # 9 repository impls
    │   ├── presentation/util/           # UiText, TimeFormatter
    │   └── di/CoreModule.kt             # Hilt @Module — provides all singletons
    │
    ├── question/presentation/
    │   ├── view_questions/              # Paginated feed, search, category tabs, tech-stack
    │   │                                #  filtering, bookmarks
    │   ├── question_details/            # Question + answers, voting, code blocks
    │   ├── add_edit_question/           # Create/edit form with picker, tags, code editor
    │   └── util/                        # Syntax highlighting tokenizer
    │
    ├── profile/presentation/
    │   ├── view_profile/                # Stats, questions/answers tabs
    │   └── edit_profile/                # Form fields, photo upload, social links, skills
    │
    ├── notification/presentation/       # Notification list — user + system announcements
    │
    ├── job/presentation/
    │   ├── jobs_screen/                 # Job board: browse, filter by type/remote
    │   ├── job_detail/                  # Job details + apply with cover letter
    │   └── post_job/                    # Company-side job posting form
    │
    ├── company/presentation/
    │   ├── PendingApprovalScreen.kt     # Shown after company sign-up
    │   └── company_dashboard/           # Company profile, subscription badge, job listings
    │
    └── admin/presentation/
        ├── admin_dashboard/             # Stat cards grid
        ├── manage_users/                # Ban / unban users
        ├── manage_questions/            # Delete questions
        ├── manage_answers/              # Delete answers
        ├── manage_announcements/        # Create system-wide announcements
        ├── manage_jobs/                 # Approve / reject job postings
        └── manage_companies/            # Activate / deactivate company subscriptions
```

### Package quirk

`feature/authentication/presentation/components/login_screen/presentation/` — the word `presentation` appears twice in the path.

---

## Navigation

```
Splash → Onboarding (if first time) → Auth
                                        ├─ Login → Home
                                        │           ├─ Feed tab
                                        │           ├─ Jobs tab
                                        │           ├─ Notifications tab
                                        │           └─ Profile tab
                                        │               ├─ Developer → ProfileScreen
                                        │               ├─ Company  → CompanyDashboard
                                        │               └─ Admin   → AdminDashboard button
                                        ├─ SignUp (developer) → Home
                                        └─ SignUp (company) → PendingApproval → Home
                               Banned check after login → BannedScreen (blocked)
```

| Route                   | Type                           | Parameters          |
|-------------------------|--------------------------------|---------------------|
| Splash                  | `data object`                  | —                   |
| Onboarding              | `data object`                  | —                   |
| Auth                    | `data object`                  | —                   |
| Home                    | `data object`                  | —                   |
| QuestionDetails         | `data class`                   | `id: Int`           |
| AddEditQuestion         | `data class`                   | `id: Int?`          |
| EditProfile             | `data object`                  | —                   |
| Profile                 | `data class`                   | `accountId: Int`    |
| Jobs                    | `data object`                  | —                   |
| JobDetail               | `data class`                   | `id: Int`           |
| CompanyJobDetail        | `data class`                   | `id: Int`           |
| AdminDashboard          | `data object`                  | —                   |
| ManageUsers             | `data object`                  | —                   |
| ManageQuestions         | `data object`                  | —                   |
| ManageAnswers           | `data object`                  | —                   |
| ManageAnnouncements     | `data object`                  | —                   |
| ManageJobs              | `data object`                  | —                   |
| ManageCompanies         | `data object`                  | —                   |
| PendingApproval         | `data object`                  | —                   |
| PostJob                 | `data object`                  | —                   |
| Banned                  | `data object`                  | —                   |

- All forward navigations **pop the backstack first** to prevent back-navigation to auth/onboarding/splash.
- IDs are `Int` throughout the navigation chain.
- Deep links from FCM notifications navigate directly to `QuestionDetails`.
- Admin panel is accessible from the profile tab when user has `isAdmin = true`.
- Company dashboard replaces the profile tab content when `accountType = "company"`.

---

## Implemented Features

### 1. Splash Screen
- Animated splash logo.
- Reads DataStore (`is_first_time`, `is_logged_in`) to route to Onboarding, Auth, or Home.

### 2. Onboarding (3 pages)
- HorizontalPager with "Next" / "Get Started" buttons and skip.
- Marks `is_first_time = false` on finish.

### 3. Authentication
- **Login** — username + password via `AccountRepository.getByUsernameAndPassword()`.
- **Sign-up** — full name (or company name), username, email, password, confirm password.
- **Company toggle** — switch between developer and company registration; company mode shows an additional company name field.
- Both set `is_logged_in` + `current_account_id` in DataStore on success.
- **Ban check** — if `isBanned = true`, redirects to BannedScreen and prevents app access.
- Plaintext password comparison via PostgREST query (no Supabase Auth).

### 4. Question Feed
- Paginated (10 per page) with infinite scroll.
- Pull-to-refresh.
- Search bar with debounced queries against title/description/code/tags.
- Category tabs (All, by language type).
- **Tech-stack filtering** — filter questions by tech-stack tags using `ilike` matching; fixed tab bar with swipe animation.
- Local-only bookmarking (in-memory `Set<Int>`).
- Each card shows: title, truncated body, code preview, tags, author avatar, time ago, likes/answers count.
- **Edited badge** — cards show "✎ Edited · X ago" when the question has an `updated_at` newer than its creation.

### 5. Question Details
- Full question content with syntax-highlighted code blocks.
- Answers list with vote buttons (`voted_ids` tracking).
- Answer input bar at the bottom (auto-expanding).
- **Code attachment on answers** — fullscreen code editor with syntax highlighting when answering.
- Like/unlike question.
- Author profile navigation.
- Breadcrumb navigation.
- **Edit button** — shown only to the question's author; navigates to the Add/Edit form.

### 6. Add / Edit Question
- Title, description, code fields.
- Language type dropdown (populated from `LanguageType` table).
- Tag input (comma-separated, stored as comma-separated string in DB).
- Create mode (`id = null`) vs. edit mode (`id = non-null`).
- **Edit mode** — reachable from the author's Edit button on the question details screen; preserves likes/votes (does not reset `likesCount`/`answersCount`) and writes `updated_at`.

### 7. Profile
- **View profile** — avatar, name, bio, tech stack, stats (questions, answers, likes, Pro badge).
- Tabbed display of the user's questions, answers, and (own profile only) job applications.
- **Edit profile** — all fields editable, photo upload to Supabase Storage, skill chips, social links (GitHub, LinkedIn, website).
- **Admin panel** button visible when `isAdmin = true`.

### 8. Notifications
- Notification list fetched from Supabase, ordered by recency.
- **System announcements** — notifications with `senderType = "system"` render with cyan-tinted background, 📢 icon, bold "📢 devZ" header, and "System" badge; user notifications render as before.
- Firebase Cloud Messaging for push notifications (per-device + topic `"announcements"`).
- Notification channel created on app startup.
- Deep link from notification tap → question details.
- Runtime permission request (Android 13+).

### 9. Syntax Highlighting
- Custom tokenizer for Kotlin, JavaScript, Python.
- Brace-based and Python-indent formatting.

### 10. Error Handling
- Unified `Result<D, E>` sealed interface.
- `Error` sealed interface: `NotFound`, `Conflict`, `Unauthorized`, `Network`, `Storage`, `Unknown(msg)`.
- `Error.toUIText()` maps each variant to user-friendly `UiText.DynamicString`.
- All repository impls catch `PostgrestRestException`, `IOException`, generic `Exception`.

### 11. Monetization
- **Pro badges** — users with `isPro = true` get a Pro badge on their profile, question cards, and next to Pro authors and job applicants.
- **Ad banners** — in-feed advertisement placeholders.
- **Pin-to-top** — Pro users can pin questions to the top of the feed.

### 12. Job Board
- **Browse jobs** — approved job postings displayed in a paginated feed with company name, title, salary range, job type badges.
- **Filter** — filter by job type (full-time, part-time, contract, remote) and search by title/company.
- **Job detail** — full job description with apply button.
- **Apply** — submit application with cover letter; stored in `JobApplication` table.
- **My Applications** — tab on the developer's **own profile** showing their submitted applications with status; load errors surface instead of a silent empty state; tapping an application opens the job detail.

### 13. Company Registration & Dashboard
- **Company sign-up** — toggle in registration form, inserts `Account` with `accountType = "company"` + `CompanyProfile` with `subscriptionStatus = "pending"`.
- **Pending approval** — after sign-up, company users see a PendingApproval screen before accessing Home.
- **Company dashboard** — replaces the profile tab for company accounts; shows company info, subscription status badge (pending/active/inactive), and list of their job postings; swipeable bottom-nav tabs with an animated indicator and pull-to-refresh.
- **Company accounts cannot answer questions** — the answer input is hidden for company accounts.
- **Post new job** — button to create a job posting; disabled if subscription is not active; status defaults to "pending" pending admin approval.

### 14. Admin Panel
- **Dashboard** — stat cards grid showing counts (users, questions, answers, announcements, jobs, companies).
- **Manage Users** — list all accounts, ban/unban toggle.
- **Manage Questions** — list all questions, delete inappropriate content.
- **Manage Answers** — list all answers, delete.
- **Manage Announcements** — create system announcements that are inserted into the `Notification` table with `senderType = "system"`, `isGlobal = true`; FCM push sent to topic `"announcements"`; **delete** removes both the global announcement row and every per-user copy (not just marking them read).
- **Manage Jobs** — list all job postings, approve/reject with FCM push notification to the posting company.
- **Manage Companies** — list all company profiles, activate/deactivate subscription with FCM push notification.

### 15. System Announcements
- Admin-published announcements stored in the `Notification` table (no separate table).
- Notification query fetches `user_id = X OR is_global = true` so system announcements appear in every user's feed.
- Rendered with distinct styling: cyan background, 📢 megaphone icon, "📢 devZ" header, "System" badge chip.
- Global FCM topic push to `"announcements"`; users subscribe on login via `FcmTokenUtil`.

---

## Data Models

| Domain Model     | Fields (key ones)                                                                     | Supabase Table       |
|------------------|---------------------------------------------------------------------------------------|----------------------|
| Account          | id, username, fullName, email, password, imageUrl, bio, techStack, githubUrl,          | Account              |
|                  | linkedInUrl, websiteUrl, points, fcmToken, followerIds, followingIds,                 |                      |
|                  | isBanned, isAdmin, isPro, accountType                                                  |                      |
| Question         | id, title, description, code, likesCount, answersCount, tags, langTypeId, accountId,  | Question             |
|                  | createdAt, updatedAt, likedAccountIds, isHidden, pinnedUntil                          |                      |
| Answer           | id, description, accepted, votedIds, questionId, accountId, createdAt                 | Answer               |
| LanguageType     | id, type                                                                              | LanguageType         |
| Notification     | id, typeId, userId, actorId, questionId, answerId, type, message, isRead, createdAt,  | Notification         |
|                  | senderType, isGlobal                                                                  |                      |
| NotificationType | id, type                                                                              | NotificationType     |
| JobPosting       | id, companyName, title, description, salaryRange, jobType, status, createdAt,         | JobPosting           |
|                  | accountId                                                                             |                      |
| JobApplication   | id, jobId, applicantId, coverLetter, status, createdAt, email, whatsapp               | JobApplication       |
| CompanyProfile   | id, userId, companyName, logoUrl, website, description, subscriptionStatus,           | CompanyProfile       |
|                  | subscriptionExpiry, createdAt, bio, location, industry, twitterUrl, isVerified        |                      |

- Data models (`@Serializable`) use `@SerialName` for snake_case → camelCase mapping.
- Domain models are plain Kotlin data classes.
- Mappers are extension functions: `DataAccount.toDomain()`, `DomainAccount.toData()`, etc.

---

## Known Quirks & Technical Notes

- **Plaintext passwords** — auth is stubbed via PostgREST `WHERE username = ? AND password = ?`. No hashing, no Supabase Auth plugin.
- **Room dependency** — `androidx.room.ktx` is in `build.gradle.kts` but has no compiler (no KSP), no database, no entities, no wiring. It is unused.
- **Dark-only theme** — `DevzTheme` hardcodes `darkTheme = true`. No light mode support.
- **Bookmarks** — stored in-memory only (`bookmarkedIds: Set<Int>`). Not persisted across sessions.
- **Supabase credentials** — loaded from `local.properties` via `BuildConfig`, not in a secrets manager.
- **Package quirk** — `feature/authentication/presentation/components/login_screen/presentation/` has a duplicated `presentation` directory.
- **Image upload** — uses `images` bucket in Supabase Storage; public URL returned directly.
- **CompanyProfile DB mapping** — uses `user_id` column (not `account_id`) to reference the Account table.
- **Account table extras** — includes `fcm_token`, `account_type`, `is_banned`, `is_admin`, `is_pro`, `points`, `follower_ids`, `following_ids` columns beyond the original model.
- **Database migrations** — the `sql/` folder holds one-off Supabase migrations that must be applied manually:
  - `001_company_profile_fields.sql` — adds company profile columns (incl. a `rating` column no longer used by the UI).
  - `002_question_updated_at.sql` — adds `Question.updated_at`; **required** for the edit-question flow (edits serialize the whole row).

---

## Build & Run

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Lint
./gradlew lint
```

Requires a `local.properties` file at the project root with:
```
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key
```

---

## Screen Walkthrough

For a **step-by-step breakdown of every screen** — what each does, what UI components it contains, and how the user interacts with it — see [`SCREEN_WALKTHROUGH.md`](SCREEN_WALKTHROUGH.md).
op