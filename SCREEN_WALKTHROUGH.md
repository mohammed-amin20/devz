# DevZ — Screen Walkthrough

A step-by-step guide covering every screen in the app, what it does, and what it contains.

---

## Navigation Overview

```
Splash
  ├─ (first time) → Onboarding → Auth → Home
  ├─ (not first, not logged in) → Auth → Home
  ├─ (logged in) → Home
  └─ (logged in but isBanned) → Banned
                        │
                   Home (5 tabs)
        ┌───────┬──────┼──────┬──────┐
        │       │      │      │      │
       Feed   Add   Notifs   Jobs  Profile
        │                              │
   Question                        Company
   Details                       Dashboard*
        │                        (*company accounts
    Add/Edit                     see this instead
    Question                     of the 5 tabs)

Deep links (FCM pushes):
  questionId → QuestionDetails, actorId → Profile,
  jobId → JobDetail, reportId → ManageReports
```

There are **21 routes** (sealed interface `Route`):

| Route | Type | Parameters | Purpose |
|-------|------|------------|---------|
| `Splash` | `data object` | — | Entry point, auto-redirect |
| `Onboarding` | `data object` | — | First-time user intro |
| `Auth` | `data object` | — | Login / Sign-up container |
| `Home` | `data object` | — | Main shell (5 tabs) or company dashboard |
| `QuestionDetails` | `data class` | `id: Int` | View a single question + answers |
| `AddEditQuestion` | `data class` | `id: Int?` | Create (null) or edit (non-null) a question |
| `EditProfile` | `data object` | — | Edit current user's profile |
| `Profile` | `data class` | `accountId: Int` | User/company profile (`ProfileHostScreen`) |
| `JobDetail` | `data class` | `id: Int` | Job posting + apply sheet |
| `AdminDashboard` | `data object` | — | Admin stat cards + menu |
| `ManageUsers` | `data object` | — | Admin: user moderation |
| `ManageQuestions` | `data object` | — | Admin: hide/delete questions |
| `ManageAnswers` | `data object` | — | Admin: hide/delete answers |
| `ManageAnnouncements` | `data object` | — | Admin: global announcements |
| `PendingApproval` | `data object` | — | Company awaiting admin approval |
| `PostJob` | `data object` | — | Company: create job posting |
| `ManageJobs` | `data object` | — | Admin: approve/reject jobs |
| `ManageCompanies` | `data object` | — | Admin: approve companies |
| `ManageReports` | `data object` | — | Admin: content reports moderation |
| `Banned` | `data object` | — | Suspended-account screen |
| `CompanyJobDetail` | `data class` | `id: Int` | Company: job + applicants list |

All forward navigations pop the backstack first — no back-navigation to auth/onboarding/splash.

---

## 1. Splash Screen

**File:** `feature/splash/presentation/SplashScreen.kt`

**Purpose:** Entry point that determines where the user should go.

**What it contains:**
- Animated logo (fade-in / scale animation)
- `SplashViewModel` with `SplashAction.DecideWhereToGoAction`

**How it works:**
1. On launch, ViewModel reads DataStore keys:
   - `is_first_time` (boolean, default `true`)
   - `is_logged_in` (boolean, default `false`)
2. Emits one of 4 navigation events via `SharedFlow`:
   - `NavigateToOnboarding` — first-time user
   - `NavigateToAuth` — returning user, not logged in
   - `NavigateToHome` — returning user, logged in
   - `NavigateToBanned` — logged in but `isBanned`
3. Screen collects the event in `LaunchedEffect` and navigates accordingly, popping the splash from backstack.

**Key UI elements:**
- Animated logo text "DEVZ"
- No interactive controls (auto-navigates)

---

## 2. Onboarding Screen

**File:** `feature/onboarding/presentation/OnboardingScreen.kt`

**Purpose:** Introduces the app to first-time users with 3 informational pages.

**What it contains:**
- `HorizontalPager` with 3 pages
- Dot indicators
- "Skip" button (top-right)
- "Next" / "Get Started" button (bottom)

**Pages (components):**
| Component | Content |
|-----------|---------|
| `FirstScreen.kt` | Welcome page — app logo, tagline "Your Hub for Code & Community" |
| `SecondScreen.kt` | Features page — highlights: ask questions, share code, get answers |
| `ThirdScreen.kt` | Get Started page — final call-to-action |

**How it works:**
1. User swipes or taps "Next" through pages
2. On last page, "Get Started" appears
3. On finish: `OnboardingViewModel.finish()` → sets `is_first_time = false` in DataStore → navigates to Auth

---

## 3. Auth Screen (Login / Sign-Up)

**File:** `feature/authentication/presentation/AuthScreen.kt`

**Purpose:** Container that toggles between Login and Sign-Up forms.

**What it contains:**
- Animated gradient background (center)
- Toggle indicator showing "Login" or "Sign Up"
- Renders either `LoginScreen` (index 0) or `SignUpScreen` (index 1)

**Navigation callbacks (wired in DevzNavHost):**
- `onLoginSuccess` → Home
- `onRegisterSuccess` → Home
- `onCompanyRegisterSuccess` → PendingApproval (company accounts)
- `onBanned` → Banned

---

### 3a. Login Screen

**File:** `feature/authentication/presentation/components/login_screen/presentation/LoginScreen.kt`

**Purpose:** Authenticate returning users.

**Fields:**
| Field | State | Action |
|-------|-------|--------|
| Email | `LoginState.email` | `LoginAction.EmailChanged(value)` |
| Password | `LoginState.password` | `LoginAction.PasswordChanged(value)` |

**How it works:**
1. User fills in email + password
2. Taps "Login" → `LoginAction.LoginClicked(onSuccess)`
3. ViewModel calls `AccountRepository.getByUsernameAndPassword()`
4. On success: sets `is_logged_in = true` + saves `current_account_id` in DataStore → navigates to Home (or PendingApproval if the account is a pending company)
5. On error: displays error via `LoginState.error: UiText?`

---

### 3b. Sign-Up Screen

**File:** `feature/authentication/presentation/components/signup_screen/presentation/SignUpScreen.kt`

**Purpose:** Register new users (developer or company accounts).

**Fields:**
| Field | State | Action |
|-------|-------|--------|
| Full Name | `SignUpState.fullName` | `SignUpAction.FullNameChanged(value)` |
| Username | `SignUpState.username` | `SignUpAction.UsernameChanged(value)` |
| Email | `SignUpState.email` | `SignUpAction.EmailChanged(value)` |
| Password | `SignUpState.password` | `SignUpAction.PasswordChanged(value)` |
| Confirm Password | `SignUpState.confirmPassword` | `SignUpAction.ConfirmPasswordChanged(value)` |

**How it works:**
1. User fills all 5 fields
2. Validates passwords match (hardcoded `UiText.DynamicString`)
3. Taps "Register" → `SignUpAction.RegisterClicked(onSuccess)`
4. ViewModel calls `AccountRepository.insert()` (plaintext password stored directly)
5. On success: sets `is_logged_in = true` + saves `current_account_id` → navigates to Home, or PendingApproval when registering as a company

---

### 3c. Banned Screen

**File:** `feature/authentication/presentation/BannedScreen.kt`

**Purpose:** Shown when the logged-in account `isBanned` — "account suspended" message with no exit.

---

## 4. Home Screen (5 Tabs)

**File:** `navigation/components/home/HomeScreen.kt`

**Purpose:** Main app shell with bottom navigation. Contains 5 tabs.

**Bottom tabs:**
| Index | Icon | Label | Screen |
|-------|------|-------|--------|
| 0 | `Icons.Rounded.Home` | Feed | `ViewQuestionsScreen` |
| 1 | `Icons.Rounded.Add` | Add | routes to `AddEditQuestion(null)` |
| 2 | `Icons.Rounded.Notifications` | Notifications | `NotificationsScreen` (badge = unread count) |
| 3 | `Icons.Rounded.WorkHistory` | Jobs | `JobsScreen` |
| 4 | `Icons.Rounded.Person` | Profile | `ProfileScreen` (own profile) |

**Key behavior:**
- No FAB — "Add" is a tab.
- `BackHandler`: back on a non-Feed tab returns to Feed (index 0).
- `HomeViewModel` exposes `selectedIndex`, `currentAccountId`, `unreadCount`, `accountType`.
- **Company accounts** (`account_type == "company"`): HomeScreen renders `CompanyDashboardScreen` instead of the 5-tab shell.
- Bottom bar hides while a fullscreen image or dialog is open.

---

### 4a. Feed Tab (View Questions)

**File:** `feature/question/presentation/view_questions/ViewQuestionsScreen.kt`

**Purpose:** Paginated question feed with search, two feeds, infinite scroll.

**State:** `ViewQuestionsState` — questions, pinnedQuestions, isLoading, isLoadingMore, error, searchQuery, selectedTab, bookmarkedIds, hasMore, isPro, reportTarget

**What it contains:**

| Component | File | Purpose |
|-----------|------|---------|
| Search bar | (inline) | Debounced search against title/description/code/tags |
| Feed tabs | (inline) | "For You" / "Following" `PrimaryTabRow` + `HorizontalPager` |
| Question feed | `QuestionCard.kt` | Infinite-scroll `LazyColumn` of question cards |
| Pull-to-refresh | (inline) | Swipe down to refresh |
| Report sheet | `ReportSheet.kt` | Report a question from the card menu |

**Feed structure:**
- **For You**: pinned questions (`pinned_until` not expired) shown on top, then all questions; for non-Pro users an ad banner appears every 5th question.
- **Following**: questions from followed users; empty state "Follow developers to see their questions here."
- Pagination: `PAGE_SIZE = 10`, `currentPage`/`hasMore`; loads next page when scrolled near bottom.

**QuestionCard components:**
- Author avatar + name
- Question title
- Truncated body text
- Code preview snippet
- Tag chips
- Relative time ("2h ago")
- Likes count + Answers count
- Bookmark icon (in-memory, not persisted)
- Overflow menu → Report

**How it works:**
1. On init: loads all `Account` + `LanguageType` into caches
2. Loads first page (10 items) sorted by `created_at DESC`
3. On scroll to bottom: loads next page
4. On search: debounced ~300ms, resets and searches
5. Pull-to-refresh: resets and reloads

---

### 4b. Notifications Tab

**File:** `feature/notification/presentation/NotificationsScreen.kt`

**Purpose:** List of user notifications + "Mark all read".

**How it works:**
1. Loads notifications from `NotificationRepository.getAllByAccountId()`
2. Ordered by `created_at DESC`
3. Tapping a notification navigates to `QuestionDetails` (deep link)
4. Notification types: `ACCEPTED`, `UPVOTE`, `LIKE`, `ANSWER`, `FOLLOWER`

**Key UI elements:**
- `DevzBrandHeader()` + "Mark all read" `TextButton` (shown only when unread exist) — right-aligned via `Arrangement.SpaceBetween`
- System announcements (`senderType == "system"` / `isGlobal`): cyan-tinted card, 📢 `Campaign` icon, "System" badge
- Unread items: bold text + blue dot (not applied to system announcements)
- Empty state

---

### 4c. Profile Tab (Own Profile)

**File:** `feature/profile/presentation/view_profile/ProfileScreen.kt`

**Purpose:** Display the current user's profile. Also used for viewing other users' profiles via `Route.Profile(accountId)`.

**State:** `ProfileState` — profile data, questions, answers, loading, error

**What it contains:**

| Component | File | Purpose |
|-----------|------|---------|
| Avatar + Name | (inline) | Profile image, full name, username |
| Bio | (inline) | User biography |
| Tech Stack | (inline) | Skill chips |
| Stats | `StatCard.kt` | Questions count, answers count, accepted rate, points |
| Follow button | (inline) | Follow/Unfollow (only on other users' profiles) |
| Followers/Following | (inline) | Counts + dialog (`FollowListDialog.kt`) |
| Tabs | (inline) | "Questions" / "Answers" |
| Question tab | `ProfileQuestionCard.kt` | User's questions |
| Answer tab | `ProfileAnswerCard.kt` | User's answers |
| Empty state | `EmptyTabContent.kt` | Shown when no questions/answers |
| Admin panel | (inline) | Entry to `AdminDashboard` (admins only) |

**How it works:**
1. Determines if viewing own profile or another user's
2. Loads account + questions + answers from repositories
3. Follow/unfollow toggles via `ProfileAction.ToggleFollow` (+1/−1 points to target)
4. Followers/following dialogs show list of users with profile navigation
5. Edit button navigates to `EditProfile` (own profile only)
6. Logout via confirmation dialog

---

## 5. Question Details Screen

**File:** `feature/question/presentation/question_details/QuestionDetailsScreen.kt`
**Composable:** `QuestionDetailScreen`

**Purpose:** Full question view with answers, voting, and answer input.

**State:** `QuestionDetailsState` — question (QuestionDetailUiModel), answers, answerText, isLoading, isPosting, error, …

**What it contains:**

| Component | File | Purpose |
|-----------|------|---------|
| Top bar | `TopBar.kt` | Back button, share |
| Breadcrumb | `Breadcrumb.kt` | Navigation path: Home > Question |
| Question content | `QuestionContent.kt` | Title, body, code, tags, author, stats |
| Code block | `CodeBlock.kt` | Syntax-highlighted code with copy button |
| Tag chip | `TagChip.kt` | Language tag display |
| Action pills | `ActionPill.kt` | Like, answer count display |
| Answer card | `AnswerCard.kt` | Answer with voting, accept button |
| Answer input bar | `AnswerInputBar.kt` | Text field + post button at bottom |

**Actions:**
| Action | Effect |
|--------|--------|
| `LoadQuestion(id)` | Loads question + answers from repositories |
| `AnswerTextChanged(value)` | Updates answer input text |
| `PostAnswer(onSuccess)` | Inserts answer, refreshes list, +1 point to question owner |
| `ToggleLike` | Like/unlike question, +1/-1 to question owner |
| `ToggleAnswerVote(answerId)` | Upvote/remove vote on answer, +1/-1 to answer author |
| `AcceptAnswer(answerId)` | Accept answer, +3 to author, -3 from previous accepted author |
| `PinQuestion` | Pin/unpin a question (admin) |
| `ShowReport(target)` | Opens `ReportSheet` for the question/answer |

**How it works:**
1. On mount: `LoadQuestion(questionId)` fires
2. Question content renders with syntax-highlighted code
3. Answers sorted newest-first (`createdAt DESC`)
4. Like button toggles heart icon + adjusts `likesCount` + `likedAccountIds`
5. Answer voting updates `votedIds` CSV
6. Accept button visible only to question owner (hidden on own answers)
7. Accepting switches: sets `accepted = true` on one, `false` on other
8. Like/vote/answer/accepted each fire a DB `Notification` + FCM push to the owner/author

**Code block features:**
- Syntax highlighting per language (Kotlin, JavaScript, Python, Generic)
- Copy button → clipboard + "Code copied!" Toast
- Traffic light dots styling

---

## 6. Add / Edit Question Screen

**File:** `feature/question/presentation/add_edit_question/AddEditQuestionScreen.kt`

**Purpose:** Create a new question or edit an existing one.

**State:** `AddEditQuestionState` — title, body, code, selectedLangTypeId, tags, tagInput, showTagInput, languageTypes, isLoading, isEdit, editQuestionId, error

**What it contains:**

| Component | File | Purpose |
|-----------|------|---------|
| Default field label | `DefaultFieldLabel.kt` | Label + optional badge |
| Title field | (inline) | Question title |
| Body field | (inline) | Question description |
| Code editor | `CodeEditorField.kt` | Code input with syntax highlighting |
| Language dropdown | `LanguageDropdownField.kt` | Select programming language |
| Tag input | (inline) | Add/remove tag chips |
| Publish button | (inline) | Submit or update |

**Modes:**
- **Create** (`id = null`): empty form, "Post" button
- **Edit** (`id = non-null`): pre-filled form, "Save" button

**How it works:**
1. In edit mode: loads existing question via `LoadQuestion(id)`
2. User fills/edits fields
3. Tags stored as comma-separated string in `Question.tags`
4. On publish: calls `QuestionRepository.insert()` (create) or `update()` (edit)
5. On success: navigates back

---

## 7. Edit Profile Screen

**File:** `feature/profile/presentation/edit_profile/EditProfileScreen.kt`

**Purpose:** Edit the current user's profile information.

**State:** `EditProfileState` — all form fields, isLoading, error (UiText?)

**What it contains:**

| Component | File | Purpose |
|-----------|------|---------|
| Section header | `SectionHeader.kt` | Section dividers |
| Avatar | (inline) | Profile photo, tap to upload |
| Full name field | (inline) | Editable name |
| Username field | (inline) | Editable username |
| Bio field | (inline) | Biography text area |
| Tech Stack | `SkillChip.kt` | Add/remove skills as chips |
| GitHub field | `SocialField.kt` | GitHub URL input |
| LinkedIn field | `SocialField.kt` | LinkedIn URL input |
| Website field | `SocialField.kt` | Website URL input |
| Governance toggle | `GovernanceToggle.kt` | Deactivate account (no-op) |

**How it works:**
1. On init: loads current account data and pre-fills all fields
2. Photo upload: picks image → uploads to Supabase Storage (`images` bucket) → updates `imageUrl`
3. On save: calls `AccountRepository.update()` with all fields
4. On logout: clears DataStore (`is_logged_in`, `current_account_id`) → navigates to Auth

**Actions:**
| Action | Effect |
|--------|--------|
| `UpdateField(field, value)` | Updates a specific field in state |
| `UploadPhoto(uri)` | Uploads image to Supabase Storage |
| `Save` | Persists all fields via repository |
| `Logout` | Clears session, navigates to Auth |
| `DeactivateAccount` | Currently a no-op |

---

## 8. Jobs Tab

**File:** `feature/job/presentation/jobs_screen/JobsScreen.kt`

**Purpose:** Browse approved job postings.

**What it contains:**
- `DevzBrandHeader()` at the top
- Search bar (by title/company)
- Job-type filter (full-time / part-time / remote / contract)
- Job cards (company name, title, salary range, type, posted time)
- Tap a job → `JobDetail`
- Admin: "Manage reports" entry

---

## 9. Job Detail Screen

**File:** `feature/job/presentation/job_detail/JobDetailScreen.kt`

**Purpose:** Full job posting view + apply flow.

**State:** `JobDetailUiState` — job, company info, hasApplied, applying, error

**What it contains:**
- Company logo + name + description
- Job title, salary range, job type, posted time
- "Apply" button → `ModalBottomSheet` with:
  - Cover letter
  - Email
  - WhatsApp number
- Submit → `JobApplicationRepository.insert()`

**Rules:**
- One proposal per user per job (`hasApplied` guard).
- After applying, the Apply button becomes disabled/"Applied".

---

## 10. Post Job Screen (Company)

**File:** `feature/job/presentation/post_job/PostJobScreen.kt`

**Purpose:** Company creates a job posting.

**What it contains:**
- Job title, description, salary range, job type
- Submit → `JobPostingRepository.insert()` with `status = "pending"`
- Posting requires admin approval before appearing in the Jobs tab.

---

## 11. Company Dashboard

**File:** `feature/company/presentation/company_dashboard/CompanyDashboardScreen.kt`

**Purpose:** Landing screen for company accounts (replaces the 5-tab shell in HomeScreen).

**What it contains:**
- Header "Company" + edit-profile icon + logout icon
- Company logo/profile card
- Tabs: company jobs + applications (HorizontalPager)
- Job cards → `CompanyJobDetail`
- "+ Post Job" → `PostJob`
- Edit-profile icon → `EditCompanyProfileOverlay`

---

## 12. Company Profile (via Route.Profile)

**Files:** `feature/company/presentation/company_profile/ProfileHostScreen.kt`, `CompanyProfileScreen.kt`

**Purpose:** `ProfileHostScreen` dispatches `Route.Profile(accountId)`:
- `account_type == "company"` → `CompanyProfileScreen` (public company profile: logo, name, description, website, jobs)
- otherwise → developer `ProfileScreen`

---

## 13. Company Job Detail

**File:** `feature/company/presentation/company_job_detail/CompanyJobDetailScreen.kt`

**Purpose:** Company views one of its job postings + the list of applicants.

**What it contains:**
- Job details (title, description, salary, type, status)
- Applicants list — each shows applicant name/avatar, cover letter, email, WhatsApp
- Tap applicant → `Profile(accountId)`

---

## 14. Pending Approval Screen

**File:** `feature/company/presentation/PendingApprovalScreen.kt`

**Purpose:** Shown right after a company registers. "Company pending approval" interstitial — the company must wait for an admin to approve its profile (`subscription_status` → "active") before using the app.

---

## 15. Admin Dashboard

**File:** `feature/admin/presentation/admin_dashboard/AdminDashboardScreen.kt`

**Purpose:** Admin home with stats + menu into all moderation screens.

**What it contains:**
- Stat cards: **USERS**, **QUESTIONS**, **ANSWERS**, **BANNED**
- Menu entries → `ManageUsers`, `ManageQuestions`, `ManageAnswers`, `ManageAnnouncements`, `ManageJobs`, `ManageCompanies`, `ManageReports` 🚩

---

## 16. Admin Manage Screens

| Screen | File | Purpose |
|--------|------|---------|
| Manage Users | `manage_users/ManageUsersScreen.kt` | List all accounts; ban/unban, promote/demote admin |
| Manage Questions | `manage_questions/ManageQuestionsScreen.kt` | List questions; hide/delete |
| Manage Answers | `manage_answers/ManageAnswersScreen.kt` | List answers; hide/delete |
| Manage Announcements | `manage_announcements/ManageAnnouncementsScreen.kt` | Compose + push a global/system announcement (DB notification to all users + FCM) |
| Manage Jobs | `manage_jobs/ManageJobsScreen.kt` | Approve/reject pending job postings |
| Manage Companies | `manage_companies/ManageCompaniesScreen.kt` | Approve company profiles (pending → active) |
| Manage Reports | `manage_reports/ManageReportsScreen.kt` | Report moderation (see below) |

### Manage Reports

**Files:** `manage_reports/ManageReportsScreen.kt`, `ManageReportsViewModel.kt`, `ManageReportsAction.kt`, `ManageReportsState.kt`

**What it contains:**
- Filter chips: **All / Pending / Reviewed / Dismissed**
- Report cards (reported type, target title, reporter, reason, status)
- Tap a card → detail `ModalBottomSheet`:
  - **Dismiss report** → status "dismissed"
  - **Delete content** → hide/delete the reported question/answer/job
  - **Ban user** → `Account.update(isBanned = true)`
- Accessible via the Jobs tab "Manage reports" entry (admin) or the FCM deep link `reportId`.

---

## 17. Content Reporting (end users)

**Files:** `feature/report/presentation/ReportSheet.kt`, `ReportViewModel.kt`, `ReportTarget.kt`

**Purpose:** Report questions, answers, jobs, or users.

**What it contains:**
- `ReportTarget` sealed interface: `Question(target, id, title)`, `Answer`, `JobPosting`, `User`
- `ModalBottomSheet` with reason chips: **Spam, Harassment, Inappropriate, Other** + optional details
- Submit → `ReportRepository.insert()` (status "pending")

**How it works (ReportViewModel):**
1. Checks for an existing report by the same reporter + target (client-side dedupe) — blocked if already reported
2. Inserts the `Report` row
3. Notifies all admins in real time: DB `Notification` row (type "report") + FCM push to each admin, message like "New {type} report by @{reporter}"

---

## Points System Summary

Every points transaction in the app:

| Trigger | Target | Delta | Where |
|---------|--------|-------|-------|
| Someone follows you | You (followed) | **+1** | `ProfileViewModel.toggleFollow()` |
| Someone unfollows you | You (followed) | **-1** | `ProfileViewModel.toggleFollow()` |
| Someone likes your question | You (question owner) | **+1** | `QuestionDetailsViewModel.toggleLike()` |
| Someone unlikes your question | You (question owner) | **-1** | `QuestionDetailsViewModel.toggleLike()` |
| Someone upvotes your answer | You (answer author) | **+1** | `QuestionDetailsViewModel.toggleAnswerVote()` |
| Someone removes upvote | You (answer author) | **-1** | `QuestionDetailsViewModel.toggleAnswerVote()` |
| Someone answers your question | You (question owner) | **+1** | `QuestionDetailsViewModel.postAnswer()` |
| Your answer is accepted | You (answer author) | **+3** | `QuestionDetailsViewModel.acceptAnswer()` |
| Your answer is un-accepted (switched) | You (old answer author) | **-3** | `QuestionDetailsViewModel.acceptAnswer()` |

Like/upvote point changes only apply when the actor isn't the owner/author.

---

## Data Flow Per Screen

```
Splash           DataStore (is_first_time, is_logged_in, isBanned) → SharedFlow event → navigate
Onboarding       DataStore (is_first_time = false)
Auth             AccountRepository (getByUsernameAndPassword / insert) → DataStore
Banned           Route.Banned (static screen)
Feed             QuestionRepository.getAll() → paginated LazyColumn (For You / Following)
QuestionDetails  QuestionRepository.getById() + AnswerRepository.getByQuestionId()
AddEditQuestion  QuestionRepository.insert() / update()
Profile          AccountRepository.getById() + QuestionRepository + AnswerRepository
EditProfile      AccountRepository.update() + Supabase Storage (image upload)
Notifications    NotificationRepository.getAllByAccountId()
Jobs             JobPostingRepository.getApproved()
JobDetail        JobPostingRepository.getById() + JobApplicationRepository.insert()
PostJob          JobPostingRepository.insert() (status pending)
CompanyDashboard JobPostingRepository + JobApplicationRepository (company's own)
CompanyProfile   CompanyProfileRepository (public company page)
CompanyJobDetail JobApplicationRepository (applicants per job)
AdminDashboard   all repositories (stat counts)
ManageReports    ReportRepository (list/filter) + AccountRepository (ban) + Question/Answer/JobPosting repos (delete)
ReportSheet      ReportRepository.insert() + admins notified (NotificationRepository + FcmPushSender)
```
