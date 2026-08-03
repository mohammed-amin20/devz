# devz — Android Q&A app (Kotlin + Compose + Supabase)

## Quick start

```bash
./gradlew assembleDebug            # build debug APK
./gradlew test                      # unit tests (JVM)
./gradlew connectedAndroidTest      # instrumented tests (device/emulator)
./gradlew lint                      # lint check
```

Note: Gradle must run with a JDK 21 (e.g. Android Studio's JBR). If the daemon reports "Failed connecting to the daemon in 4 retries", just retry. If `:app:kspDebugKotlin` fails with "Unable to delete file", run `.\gradlew.bat --stop` and re-run.

## Full file tree (215 Kotlin source files)

```
com.mohamed.devz/
├── DevZApp.kt                         # @HiltAndroidApp
├── MainActivity.kt                    # @AndroidEntryPoint; reads FCM extras (pendingQuestionId, pendingActorId,
│                                      #   pendingJobId, pendingReportId) and forwards them as deep-link routes
│
├── navigation/
│   ├── Route.kt                       # sealed interface, @Serializable — 21 routes (Splash, Onboarding, Auth,
│   │                                  #   Home, QuestionDetails(id), AddEditQuestion(id?), EditProfile,
│   │                                  #   Profile(accountId), JobDetail(id), AdminDashboard, ManageUsers,
│   │                                  #   ManageQuestions, ManageAnswers, ManageAnnouncements, PendingApproval,
│   │                                  #   PostJob, ManageJobs, ManageCompanies, ManageReports, Banned,
│   │                                  #   CompanyJobDetail(id))
│   ├── DevzNavHost.kt                 # NavHost with all composable routes; splash pops backstack before forward nav;
│   │                                  #   deep links: QuestionDetails←questionId, Profile←actorId, JobDetail←jobId,
│   │                                  #   ManageReports←reportId
│   └── components/home/
│       ├── HomeScreen.kt              # Bottom nav shell with 5 tabs (Feed, Add, Notifications, Jobs, Profile)
│       └── HomeViewModel.kt           # Owns tab selection + unread-notification badge count
│
├── ui/theme/
│   ├── Color.kt                       # Dark theme palette (CyanPrimary, TextWhite, DevzCard, DevzInput, Q* colors)
│   ├── Theme.kt                       # DevzTheme with dark-only Material3 scheme
│   └── Type.kt                        # Inter (body), Space Grotesk (titles) via google fonts
│
└── feature/
    ├── splash/presentation/
    │   ├── SplashAction.kt            # sealed interface { DecideWhereToGoAction }
    │   ├── SplashViewModel.kt         # MVI: onAction → decideWhereToGo() → emits SplashEvent via SharedFlow
    │   └── SplashScreen.kt            # Animated splash, collects splashEvent in LaunchedEffect, 3 navigation lambdas
    │
    ├── onboarding/presentation/
    │   ├── OnboardingAction.kt        # sealed interface { FinishAction }
    │   ├── OnboardingViewModel.kt     # MVI: onAction → finish() → setNotFirstTime()
    │   ├── OnboardingScreen.kt        # 3-page HorizontalPager, "Get Started"/"Next" button, skip button, dot indicators
    │   └── components/ (FirstScreen, SecondScreen, ThirdScreen)
    │
    ├── authentication/presentation/
    │   ├── AuthScreen.kt              # Toggle container: index 0→LoginScreen, 1→SignUpScreen
    │   ├── BannedScreen.kt            # Shown when account.isBanned — "account suspended" message
    │   └── components/
    │       ├── login_screen/presentation/  # LoginAction, LoginViewModel, LoginState, LoginScreen
    │       └── signup_screen/presentation/ # SignUpAction, SignUpViewModel, SignUpState, SignUpScreen
    │
    ├── core/
    │   ├── domain/
    │   │   ├── model/                 # 10 models: Account, Question, Answer, LanguageType, Notification,
    │   │   │                          #   NotificationType, JobPosting, JobApplication, CompanyProfile, Report
    │   │   ├── repository/            # 10 repositories: Account, Question, Answer, LanguageType, Notification,
    │   │   │                          #   NotificationType, UserPreferences, JobPosting, JobApplication, CompanyProfile,
    │   │   │                          #   Report (interfaces; Result<..., Error>-based)
    │   │   └── util/
    │   │       ├── Error.kt           # sealed interface: NotFound, Conflict, Unauthorized, Network, Storage (data object),
    │   │       │                      #   Unknown(val message: String); Error.toUIText() maps to user-friendly strings
    │   │       ├── Result.kt          # sealed interface Result<D, E : DomainError> { Success(data), Error(error, data?) }
    │   │       └── FcmTokenUtil.kt    # Persists/reads FCM token in DataStore
    │   │
    │   ├── data/
    │   │   ├── data_source/
    │   │   │   ├── remote/
    │   │   │   │   ├── DevZRemoteDataSource.kt     # interface with 10 inner table interfaces (Account, Question,
    │   │   │   │   │                                #   LanguageType, Answer, Notification, NotificationType,
    │   │   │   │   │                                #   JobPosting, JobApplication, CompanyProfile, Report)
    │   │   │   │   ├── DevZRemoteDataSourceImpl.kt # Postgrest queries via supabase-kt DSL
    │   │   │   │   └── DevzFirebaseMessagingService.kt # FCM message receiver; saves token via FcmTokenUtil
    │   │   │   └── local/
    │   │   │       ├── UserPreferences.kt          # observeIsFirstTime, observeIsLoggedIn, observeCurrentAccountId,
    │   │   │       │                                #   setNotFirstTime, setLoggedIn, setAccountId, setLoggedOut, clearAccountId
    │   │   │       ├── UserPreferencesImpl.kt      # UserPreferencesManager (@Singleton, DataStore "user_prefs",
    │   │   │       │                                #   3 keys: is_first_time, is_logged_in, current_account_id)
    │   │   │       └── FcmPushSender.kt            # Server-side FCM push via Ktor + Firebase service account
    │   │   │                                        #   (key loaded from R.raw.fcm_service_account, caches OAuth token)
    │   │   ├── model/                              # @Serializable data classes with @SerialName (snake_case → camelCase)
    │   │   │                                       #   — same 10 models as domain
    │   │   ├── mapper/                             # toDomain()/toData() per model (AccountMapper, QuestionMapper, …)
    │   │   └── repository/                         # 10 *Impl classes; catch PostgrestRestException/IOException/Exception
    │   │
    │   ├── presentation/
    │   │   ├── components/ (DevzBrandHeader, ProBadge)
    │   │   └── util/ (UiText, TimeFormatter, TimestampFormatter)
    │   └── di/CoreModule.kt            # @Module @InstallIn(SingletonComponent): provides SupabaseClient (from
    │                                   #   BuildConfig.SUPABASE_URL/ANON_KEY), Postgrest, Storage, DevZRemoteDataSource,
    │                                   #   all 10 repositories, UserPreferences, FcmPushSender
    │
    ├── question/presentation/
    │   ├── view_questions/
    │   │   ├── ViewQuestionsAction.kt     # LoadNextPage, Refresh, BookmarkToggled, SearchQueryChanged, TabSelected,
    │   │   │                               #   ShowReport(target), DismissReport
    │   │   ├── ViewQuestionsState.kt      # questions, pinnedQuestions, isLoading, isLoadingMore, error, searchQuery,
    │   │   │                               #   selectedTab, bookmarkedIds, hasMore, isPro, reportTarget
    │   │   ├── ViewQuestionsViewModel.kt  # MVI: "For You" + "Following" feeds, paginated, pinned questions on top,
    │   │   │                               #   ad banner every 5 items for non-Pro, caches Account + LanguageType
    │   │   ├── QuestionFeedUiModel.kt     # id, title, body, code, tags, authorName, authorAvatar, timeAgo, likes,
    │   │   │                               #   answers, isBookmarked, langTypeId
    │   │   ├── ViewQuestionsScreen.kt     # Search bar, "For You"/"Following" PrimaryTabRow + HorizontalPager,
    │   │   │                               #   pull-to-refresh, infinite scroll, ReportSheet
    │   │   └── components/QuestionCard.kt
    │   ├── question_details/
    │   │   ├── QuestionDetailsAction.kt   # LoadQuestion, AnswerTextChanged, PostAnswer, ShowReport/DismissReport,
    │   │   │                               #   PinQuestion, ShowCodeEditor/PrefillAnswerCode/AnswerCodeChanged
    │   │   ├── QuestionDetailsState.kt    # question (QuestionDetailUiModel), answers, answerText, isLoading, …
    │   │   ├── QuestionDetailsViewModel.kt# MVI: loads question + answers, posts answer, accepts answers,
    │   │   │                               #   like/vote, award points (see Points system), pin questions, report
    │   │   ├── QuestionDetailsScreen.kt   # Wired with ViewModel, question content + answer input bar + error/retry UI
    │   │   └── components/ (AnswerCard, AnswerInputBar, Breadcrumb, CodeBlock, QuestionContent, TagChip, TopBar,
    │   │                   ActionPill)
    │   ├── add_edit_question/
    │   │   ├── AddEditQuestionAction.kt   # LoadQuestion, TitleChanged, BodyChanged, CodeChanged, LanguageSelected,
    │   │   │                               #   TagInputChanged, AddTag, RemoveTag, ShowTagInput, Publish
    │   │   ├── AddEditQuestionState.kt
    │   │   ├── AddEditQuestionViewModel.kt# MVI: loads language types, loads question for edit, publishes/updates
    │   │   ├── AddEditQuestionScreen.kt
    │   │   └── components/ (CodeEditorField, DefaultFieldLabel, LanguageDropdownField)
    │   └── util/ (SyntaxLanguage, Token, tokenize, IndentationFormatter)
    │
    ├── profile/presentation/
    │   ├── view_profile/
    │   │   ├── ProfileAction.kt, ProfileState.kt, ProfileViewModel.kt, ProfileScreen.kt
    │   │   ├── util/ (ProfileUiModel, ProfileJobApplicationUiModel, ProfileFollowerUiModel, ProfileAnswerUiModel,
    │   │   │          ProfileQuestionUiModel)
    │   │   └── components/ (StatCard, ProfileQuestionCard, ProfileAnswerCard, FollowListDialog, …)
    │   └── edit_profile/
    │       ├── EditProfileAction.kt, EditProfileViewModel.kt, EditProfileState.kt
    │       └── components/ (GovernanceToggle, SectionHeader, SkillChip, SocialField)
    │
    ├── notification/presentation/
    │   ├── NotificationsScreen.kt      # Notification list; devZ brand header + "Mark all read"; system announcements
    │   │                               #   styled with cyan-tinted card + 📢 + "System" badge; unread bold/blue dot
    │   └── NotificationsViewModel.kt   # MVI: loads notifications, marks single/all read; injects NotificationRepository
    │                                   #   + UserPreferencesRepository
    │
    ├── report/presentation/
    │   ├── ReportTarget.kt             # sealed interface: Question(target, id, title), Answer, JobPosting, User
    │   ├── ReportAction.kt             # ReasonSelected, DetailsChanged, Submit, Dismiss
    │   ├── ReportState.kt
    │   ├── ReportViewModel.kt          # MVI: dedupes (already-reported check), inserts Report, notifies all admins
    │   │                               #   (DB notifications + FCM push each) with "New {type} report by @{reporter}"
    │   ├── ReportSheet.kt              # ModalBottomSheet with REPORT_REASONS = ["Spam","Harassment","Inappropriate","Other"]
    │   └── ReportsScreen.kt            # (unused/stub) — reports live in admin ManageReports
    │
    ├── job/presentation/
    │   ├── jobs_screen/ (JobsScreen.kt, JobsViewModel.kt)     # devZ brand header, search, job-type filter,
    │   │                                                      #   apply → JobDetail, admin "Manage reports" entry
    │   ├── job_detail/ (JobDetailScreen.kt, JobDetailViewModel.kt)  # job details, apply bottom sheet (cover letter,
    │   │                                                      #   email, WhatsApp) → JobApplication insert, one proposal
    │   │                                                      #   per user
    │   └── post_job/ (PostJobScreen.kt, PostJobViewModel.kt)  # create JobPosting (status "pending", needs admin approval)
    │
    ├── company/presentation/
    │   ├── PendingApprovalScreen.kt    # "company pending approval" interstitial (Route.PendingApproval)
    │   ├── company_dashboard/ (CompanyDashboardScreen.kt, CompanyDashboardViewModel.kt, CompanyDashboardState.kt,
    │   │                        CompanyDashboardAction.kt)   # company's jobs + applications; shown in HomeScreen
    │   │                                                      #   when the logged-in account is a company
    │   ├── company_profile/ (ProfileHostScreen.kt, CompanyProfileScreen.kt, CompanyProfileViewModel.kt, …)
    │   │                                                      # Route.Profile host: dispatches to CompanyProfileScreen
    │   │                                                      #   for "company" accounts, else the developer ProfileScreen
    │   ├── company_job_detail/ (CompanyJobDetailScreen.kt, CompanyJobDetailViewModel.kt, …)  # job + applicants list
    │   └── edit_company_profile/ (EditCompanyProfileOverlay.kt, EditCompanyProfileViewModel.kt, …)  # overlay launched
    │                                                          #   from CompanyDashboardScreen (no separate route)
    │
    ├── admin/presentation/
    │   ├── admin_dashboard/ (AdminDashboardScreen.kt, AdminDashboardViewModel.kt)  # stat cards (USERS, QUESTIONS,
    │   │                                                    #   ANSWERS, BANNED) + menu: Users, Questions, Answers,
    │   │                                                    #   Announcements, Jobs, Companies, Reports 🚩
    │   ├── manage_users/ (ManageUsersScreen.kt, ManageUsersViewModel.kt)
    │   ├── manage_questions/ (ManageQuestionsScreen.kt, ManageQuestionsViewModel.kt)
    │   ├── manage_answers/ (ManageAnswersScreen.kt, ManageAnswersViewModel.kt)
    │   ├── manage_announcements/ (ManageAnnouncementsScreen.kt, ManageAnnouncementsViewModel.kt)  # global/system
    │   │                                                    #   notifications pushed to all users
    │   ├── manage_jobs/ (ManageJobsScreen.kt, ManageJobsViewModel.kt)      # approve/reject pending job postings
    │   ├── manage_companies/ (ManageCompaniesScreen.kt, ManageCompaniesViewModel.kt)  # approve company profiles
    │   └── manage_reports/ (ManageReportsScreen.kt, ManageReportsViewModel.kt, ManageReportsAction.kt,
    │                        ManageReportsState.kt)          # filter chips All/Pending/Reviewed/Dismissed, report cards,
    │                                                        #   detail bottom sheet: Dismiss report / Delete content / Ban user
    │
    └── banned/  (handled via Route.Banned → BannedScreen in authentication/presentation)
```

## Architecture

### Clean Architecture (single-module)

```
Composable Screen → ViewModel (MVI) → Repository (interface) → RepositoryImpl → Remote/Local Data Source
                         │                                                   │
                    domain/util/                                        data/model/
                    Error, Result                                       @Serializable
                    UiText (presentation/util/)                         data/mapper/
                                                                        toDomain()/toData()
```

### UiText (presentation/util/UiText.kt)

- **Sealed class `UiText`** with two variants: `DynamicString(value: String)` and `StringResource(resId: Int, args: List<Any>)`.
- **`@Composable fun asString(): String`** resolves to the string value. Call `.asString()` inside composables (e.g. `Text(text = it.asString(), ...)`).
- Error display: `result.error.toUIText()` in ViewModels, `.asString()` in screens.

### Error handling

- **`Error`** (`domain/util/Error.kt`): sealed interface — `NotFound`, `Conflict`, `Unauthorized`, `Network`, `Storage` as `data object`, `Unknown(val message: String)`.
- **`Result<D, E : Error>`** (`domain/util/Result.kt`): `Success(data)` or `Error(error, data?)`.
- **`Error.toUIText()`**: extension mapping each variant to a friendly `UiText.DynamicString`; only `Unknown` passes its message through.
- **Repository catch pattern**: every repo impl catches `PostgrestRestException` (status routing → NotFound/Conflict/Unauthorized/Unknown), `IOException` → Network, generic `Exception` → Unknown.
- **ViewModel consume pattern**: `when (result) { is Result.Success -> … ; is Result.Error -> _uiState.update { it.copy(error = result.error.toUIText()) } }`.

### MVI pattern (unified across all ViewModels)

1. **Action** — sealed interface (separate file). Data objects for intent-only, `data class(value: …)` for field changes, `onSuccess: () -> Unit` for submissions.
2. **State** — `data class` with fields + `isLoading` + `error: UiText?`, exposed as `StateFlow` via `MutableStateFlow.asStateFlow()`.
3. **Events** — one-shot SharedFlow (only SplashViewModel uses this).
4. **`onAction(action)`** — single entry dispatching via `when`.
5. **Screen** — `viewModel: XViewModel = hiltViewModel()`, `val uiState by viewModel.uiState.collectAsState()`.

| Feature | Actions file | State | Events | Submit target |
|---------|--------------|-------|--------|---------------|
| Splash | `SplashAction.kt` | — | SharedFlow | decideWhereToGo() |
| Onboarding | `OnboardingAction.kt` | — | — | finish() → setNotFirstTime() |
| Login | `LoginAction.kt` | LoginState | onSuccess | AccountRepository.getByUsernameAndPassword() |
| SignUp | `SignUpAction.kt` | SignUpState | onSuccess | AccountRepository.insert() |
| ViewQuestions | `ViewQuestionsAction.kt` | ViewQuestionsState | — | QuestionRepository.getAll() |
| QuestionDetails | `QuestionDetailsAction.kt` | QuestionDetailsState | onSuccess | Question/Answer repos + points |
| AddEditQuestion | `AddEditQuestionAction.kt` | AddEditQuestionState | onSuccess | QuestionRepository.insert()/update() |
| EditProfile | `EditProfileAction.kt` | EditProfileState | — | AccountRepository.update() |
| Profile | `ProfileAction.kt` | ProfileState | — | Account/Question/Answer repos |
| Notifications | inline in `NotificationsViewModel.kt` | NotificationsUiState | — | NotificationRepository.getAllByAccountId() |
| Report | `ReportAction.kt` | ReportState | onSuccess | ReportRepository.insert() + admin notify |
| Jobs | `JobsViewModel.kt` | JobsUiState | — | JobPostingRepository.getApproved() |
| JobDetail | `JobDetailViewModel.kt` | JobDetailUiState | — | JobApplicationRepository.insert() |
| PostJob | `PostJobViewModel.kt` | PostJobUiState | — | JobPostingRepository.insert() |
| CompanyDashboard | `CompanyDashboardViewModel.kt` | — | — | JobPosting/JobApplication repos |
| CompanyProfile | `CompanyProfileViewModel.kt` | — | — | CompanyProfileRepository |
| CompanyJobDetail | `CompanyJobDetailViewModel.kt` | — | — | JobApplicationRepository |
| EditCompanyProfile | `EditCompanyProfileViewModel.kt` | — | — | CompanyProfileRepository.update() |
| AdminDashboard | `AdminDashboardViewModel.kt` | — | — | all repos (stat counts) |
| ManageUsers | `ManageUsersViewModel.kt` | — | — | AccountRepository |
| ManageQuestions/Answers | `Manage{Questions,Answers}ViewModel.kt` | — | — | Question/Answer repos (hide/delete) |
| ManageAnnouncements | `ManageAnnouncementsViewModel.kt` | — | — | Notification insert (global) + FCM |
| ManageJobs | `ManageJobsViewModel.kt` | — | — | JobPosting approve/reject |
| ManageCompanies | `ManageCompaniesViewModel.kt` | — | — | CompanyProfile approve |
| ManageReports | `ManageReportsAction.kt` | ManageReportsState | — | Report + Account repos |

### Navigation flow

```
Splash (via DataStore)
  ├─ isFirstTime=true  → Onboarding → Auth → Home
  ├─ not first & not logged-in → Auth → Home
  ├─ logged-in → Home
  └─ logged-in but isBanned → Banned

Deep links (from FCM pushes):
  questionId → QuestionDetails, actorId → Profile, jobId → JobDetail, reportId → ManageReports
```

- Routes are `@Serializable` sealed interface `Route`; `DevzNavHost` uses type-safe `composable<Route.X>`.
- `toRoute()` for parameterized routes; IDs are `Int` throughout.
- Each forward navigation pops the backstack first (no back-navigation to previous steps): `navController.apply { popBackStack(); navigate(Route.X) }`.

### Bottom navigation (HomeScreen)

5 tabs, no FAB: **Feed**, **Add** (routes to AddEditQuestion), **Notifications** (badge = unread count), **Jobs**, **Profile**. Tab state + unread badge live in `HomeViewModel`. When the logged-in account is a company (`account_type == "company"`), HomeScreen renders `CompanyDashboardScreen` instead of the 5-tab shell.

### Preferences (DataStore)

- `UserPreferences` interface (data) → `UserPreferencesRepository` (domain) → `UserPreferencesRepositoryImpl` wraps in `Result<Unit, Error>`.
- `UserPreferencesManager` (`@Singleton`): `preferencesDataStore(name = "user_prefs")`, keys: `is_first_time` (default true), `is_logged_in` (default false), `current_account_id` (default 0).
- `current_account_id` saved in LoginViewModel.login() and SignUpViewModel.register() right after setLoggedIn().
- `FcmTokenUtil` stores the FCM token under a separate DataStore key.

### Backend (Supabase)

- **Client**: `createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.ANON_KEY)` with `Postgrest` + `Storage` plugins in `CoreModule`. Credentials come from `BuildConfig` (debug constants; not secrets-managed).
- **Auth plugin** removed — auth is stubbed via PostgREST direct queries (plaintext password comparison).
- **10 tables**: `Account`, `Question`, `Answer`, `LanguageType`, `Notification`, `NotificationType`, `JobPosting`, `JobApplication`, `CompanyProfile`, `Report` — mapped via `@SerialName` to snake_case.
- **Storage bucket**: `images` for profile/company photo uploads.
- **Migrations**: `sql/001…004` — 004 adds the `Report` table (must be applied in Supabase for reporting + admin pushes).
- **FCM**: push notifications sent from the app itself via `FcmPushSender` (Ktor) using a Firebase service-account JSON stored in `res/raw/fcm_service_account.json`. `DevzFirebaseMessagingService` receives tokens/messages.

### DI (Dagger Hilt)

- **KSP** annotation processing (no kapt). Compiler: `libs.hilt.android.compiler`.
- **Module**: `CoreModule` (`@Module @InstallIn(SingletonComponent::class)`).
- **Provides**: SupabaseClient, Postgrest, Storage, DevZRemoteDataSource, 10 repositories, UserPreferences, FcmPushSender. All interface→impl bindings via `@Provides @Singleton` (no `@Binds`).
- Uses `jakarta.inject.Inject` (not `javax`).

### Points system

- **Account.points**: `Int`, incremented/decremented via `AccountRepository.addPoints()`.
- Question owner gets **+1** when their question receives a new answer.
- Accepted answer author gets **+3** on accept, **−3** on unaccept.
- Follow action gives target **+1**, unfollow **−1**.
- Like (question) and upvote (answer) give owner/author **+1**; unlike/unvote **−1** — only when the actor isn't the owner, and these also fire DB `Notification` + FCM push.

### Data models

| Domain | Data (@Serializable) | Notable @SerialName / fields |
|--------|---------------------|------------------------------|
| Account | Account | username, full_name, image_url, tech_stack, github_url, linkedin_url, website_url; + points, fcm_token, follower_ids, following_ids, is_banned, is_admin, is_main_admin, is_pro, account_type ("developer"/"company"), phone_number |
| Question | Question | likes_count, answers_count, lang_type_id, account_id, created_at, updated_at, liked_account_ids, is_hidden, pinned_until |
| Answer | Answer | voted_ids, question_id, account_id, created_at, + code |
| LanguageType | LanguageType | id, type (identical) |
| Notification | Notification | **user_id** (not account_id), type_id, actor_id, question_id, answer_id, message, is_read, sender_type, is_global |
| NotificationType | NotificationType | id, type (identical) |
| JobPosting | JobPosting | company_name, salary_range, job_type, status ("approved"/"pending"), account_id |
| JobApplication | JobApplication | job_id, applicant_id, cover_letter, status ("pending"/"accepted"/"rejected"), email, whatsapp |
| CompanyProfile | CompanyProfile | user_id, company_name, logo_url, subscription_status ("pending"/"active"), is_verified |
| Report | Report | reporter_id, reported_type ("question"/"answer"/"job"/"user"), reported_id, reason, details, status ("pending"/"reviewed"/"dismissed") |

Mappers are `toDomain()`/`toData()` extension functions with import aliases (`as DataAccount`/`as DomainAccount`).

## Build config

| Setting | Value |
|---------|-------|
| AGP | 8.10.1 |
| Kotlin | 2.3.20 (languageVersion=1.9) |
| Gradle | 8.11.1 |
| compileSdk / targetSdk | 36 |
| minSdk | 26 |
| JVM target | 11 |
| Compose BOM | 2026.03.01 |
| Material3 | 1.4.0 |
| Supabase BOM | 3.6.0 |
| Hilt | 2.51.1 |
| KSP | 1.5.30-1.0.0 |

## Known issues & quirks

- **Account domain model** stores plaintext `password` — insecure PostgREST-based auth (no Supabase Auth).
- **Package quirk**: `feature/authentication/presentation/components/login_screen/presentation/` — `presentation` appears twice.
- **All screens** use dark-only theme (no light mode support in `Theme.kt`).
- **Local fonts** (Inter, Space Grotesk) from `res/font/` — loaded via Google Fonts Compose library.
- **EditProfileViewModel `DeactivateAccount`** action is a no-op (no confirmation dialog).
- **ReportsScreen.kt** is a stub — reports live in admin `ManageReports`.
- **Room** is NOT used (removed) despite `androidx.room` being listed nowhere; `proguard-rules.pro` was deleted.
- **QuestionCard preview** uses a hardcoded `QuestionFeedUiModel` — not a live preview.
- **Feed ads**: non-Pro users see a fake ad banner every 5th question; Pro (isPro) users don't. Not persisted, purely visual.
- **Report** duplicates are blocked client-side by a reporter+target check in `ReportViewModel` (no DB unique constraint).

## Conventions for new code

- MVI: sealed `Action` interface in a separate file, `onAction(action)` entry, `StateFlow<State>` with `_uiState` backing.
- Error strings: use `result.error.toUIText()` — never hardcode error strings in ViewModels (validation errors may use `UiText.DynamicString("…")`).
- Error display in composables: `.asString()`.
- Repos: catch `PostgrestRestException` → status routing, `IOException` → Network, `Exception` → Unknown. Only `Unknown` carries a message.
- Navigation: `navController.apply { popBackStack(); navigate(Route.X) }`. IDs are `Int`. Deep-link extras from FCM handled in MainActivity → DevzNavHost.
- Screens: `viewModel: XViewModel = hiltViewModel()` as default param; `val uiState by viewModel.uiState.collectAsState()`.
- Imports: `jakarta.inject.Inject`.
- Pagination: ViewModel tracks `currentPage`, `hasMore`, `PAGE_SIZE = 10`; data source uses `.range().order().decodeList()`.
- Question feed: `ViewQuestionsViewModel` loads `Account` + `LanguageType` caches, builds `QuestionFeedUiModel` via `toFeedUiModel()`.
- Bookmark: local-only `bookmarkedIds: Set<Int>` in state, not persisted.
- Colors: Q-prefixed colors (`QBg`, `QPrimary`, `QOutline`, `QOnSurface`, `QOnSurfaceVariant`) in `ui/theme/Color.kt`.
- AddEdit tags: comma-separated `String` in DB (`Question.tags`); converted to/from `List<String>` in ViewModel.
- **Brand headers**: use the shared `DevzBrandHeader()` (from `feature/core/presentation/components/BrandHeader.kt`) for the top-of-screen title on major tabs (Feed, Notifications, Jobs) instead of plain `Text` — keep "Mark all read" style actions right-aligned via `Arrangement.SpaceBetween`.
- **FCM push**: to notify a user server-side, build a `Notification` DB row and send via `FcmPushSender` using the target account's `fcmToken` + `FcmTokenUtil`.
