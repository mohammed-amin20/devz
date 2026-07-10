# DevZ — Screen Walkthrough

A step-by-step guide covering every screen in the app, what it does, and what it contains.

---

## Navigation Overview

```
Splash
  ├─ (first time) → Onboarding → Auth → Home
  ├─ (not first, not logged in) → Auth → Home
  └─ (logged in) → Home
                        │
                   Home (3 tabs)
                  ┌─────┼─────┐
                  │     │     │
               Feed  Notifs  Profile
                  │             │
            Question       Edit Profile
            Details
                  │
              Add/Edit
              Question
```

There are **8 routes** (sealed interface `Route`):

| Route | Type | Parameters | Purpose |
|-------|------|------------|---------|
| `Splash` | `data object` | — | Entry point, auto-redirect |
| `Onboarding` | `data object` | — | First-time user intro |
| `Auth` | `data object` | — | Login / Sign-up container |
| `Home` | `data object` | — | Main shell with 3 bottom tabs |
| `QuestionDetails` | `data class` | `id: Int` | View a single question + answers |
| `AddEditQuestion` | `data class` | `id: Int?` | Create (null) or edit (non-null) a question |
| `EditProfile` | `data object` | — | Edit current user's profile |
| `Profile` | `data class` | `accountId: Int` | View another user's profile |

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
2. Emits one of 3 navigation events via `SharedFlow`:
   - `NavigateToOnboarding` — first-time user
   - `NavigateToAuth` — returning user, not logged in
   - `NavigateToHome` — returning user, logged in
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

**Key UI elements:**
- Horizontal swipeable pager
- 3 animated dots indicating current page
- Gradient/illustration backgrounds per page

---

## 3. Auth Screen (Login / Sign-Up)

**File:** `feature/authentication/presentation/AuthScreen.kt`

**Purpose:** Container that toggles between Login and Sign-Up forms.

**What it contains:**
- Animated gradient background (center)
- Toggle indicator showing "Login" or "Sign Up"
- Renders either `LoginScreen` (index 0) or `SignUpScreen` (index 1)

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
4. On success: sets `is_logged_in = true` + saves `current_account_id` in DataStore → navigates to Home
5. On error: displays error via `LoginState.error: UiText?`

**Key UI elements:**
- Email text field
- Password text field (obscured)
- Login button (gradient)
- Loading indicator
- Error text

---

### 3b. Sign-Up Screen

**File:** `feature/authentication/presentation/components/signup_screen/presentation/SignUpScreen.kt`

**Purpose:** Register new users.

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
5. On success: sets `is_logged_in = true` + saves `current_account_id` → navigates to Home

**Key UI elements:**
- 5 text fields in a `LazyColumn`
- "Terms of Service" text
- Register button (gradient)
- Loading indicator
- Error text

---

## 4. Home Screen (3 Tabs)

**File:** `navigation/components/home/HomeScreen.kt`

**Purpose:** Main app shell with bottom navigation. Contains 3 tabs.

**Bottom tabs:**
| Index | Icon | Label | Screen |
|-------|------|-------|--------|
| 0 | `Icons.Home` | Feed | `ViewQuestionsScreen` |
| 1 | `Icons.Notifications` | Notifications | `NotificationsScreen` |
| 2 | `Icons.Person` | Profile | `ProfileScreen` (own profile) |

**Additional UI:**
- FAB (floating action button) — navigates to `AddEditQuestion` (create mode)
- Back handler — double-tap to exit app
- Global navigation callbacks: question details, add/edit question, edit profile, other user profiles

**`HomeViewModel`** exposes:
- `selectedIndex` — current tab (StateFlow)
- `currentAccountId` — logged-in user's ID
- `unreadCount` — badge count for notifications tab

---

### 4a. Feed Tab (View Questions)

**File:** `feature/question/presentation/view_questions/ViewQuestionsScreen.kt`

**Purpose:** Paginated question feed with search and filtering.

**State:** `ViewQuestionsState` — questions, isLoading, isLoadingMore, error, searchQuery, selectedTab, bookmarkedIds, hasMore

**What it contains:**

| Component | File | Purpose |
|-----------|------|---------|
| Search bar | (inline) | Debounced search against title/description/code/tags |
| Category tabs | (inline) | "All" + one tab per `LanguageType` |
| Question feed | `QuestionCard.kt` | Infinite-scroll `LazyColumn` of question cards |
| Pull-to-refresh | (inline) | Swipe down to refresh |

**QuestionCard components:**
- Author avatar + name
- Question title
- Truncated body text
- Code preview snippet
- Tag chips
- Relative time ("2h ago")
- Likes count + Answers count
- Bookmark icon (in-memory, not persisted)

**How it works:**
1. On init: loads all `Account` + `LanguageType` into caches
2. Loads first page (10 items) sorted by `created_at DESC`
3. On scroll to bottom: loads next page (infinite scroll)
4. On tab change: resets feed, loads by `lang_type_id`
5. On search: debounced 300ms, searches title/description/code/tags
6. Pull-to-refresh: resets and reloads

**Following feed:**
- The feed initially loads questions from users the current user follows
- If not following anyone, shows empty state "Not following anyone"

---

### 4b. Notifications Tab

**File:** `feature/notification/presentation/NotificationsScreen.kt`

**Purpose:** List of user notifications.

**How it works:**
1. Loads notifications from `NotificationRepository.getAllByAccountId()`
2. Ordered by `created_at DESC`
3. Tapping a notification navigates to `QuestionDetails` (deep link)
4. Notification types: `ACCEPTED`, `UPVOTE`, `LIKE`, `ANSWER`, `FOLLOWER`

**Key UI elements:**
- Notification list items (icon, title, description, time)
- Unread indicator
- Empty state

---

### 4c. Profile Tab (Own Profile)

**File:** `feature/profile/presentation/view_profile/ProfileScreen.kt`

**Purpose:** Display the current user's profile. Also used for viewing other users' profiles via `Route.Profile(accountId)`.

**State:** `ProfileUiState` — profile data, questions, answers, loading, error

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

**How it works:**
1. Determines if viewing own profile or another user's
2. Loads account + questions + answers from repositories
3. Follow/unfollow toggles via `ProfileAction.ToggleFollow`
4. Following/followers dialogs show list of users with profile navigation
5. Edit button navigates to `EditProfile` (own profile only)
6. Settings / Logout options

---

## 5. Question Details Screen

**File:** `feature/question/presentation/question_details/QuestionDetailScreen.kt`

**Purpose:** Full question view with answers, voting, and answer input.

**State:** `QuestionDetailsState` — question, answers, answerText, isLoading, isPosting, error

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

**How it works:**
1. On mount: `LoadQuestion(questionId)` fires
2. Question content renders with syntax-highlighted code
3. Answers sorted newest-first (`createdAt DESC`)
4. Like button toggles heart icon + adjusts `likesCount`
5. Answer voting updates `votedIds` CSV
6. Accept button visible only to question owner (hidden on own answers)
7. Accepting switches: sets `accepted = true` on one, `false` on other

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

---

## Data Flow Per Screen

```
Splash           DataStore (is_first_time, is_logged_in) → SharedFlow event → navigate
Onboarding       DataStore (is_first_time = false)
Auth             AccountRepository (getByUsernameAndPassword / insert) → DataStore
Feed             QuestionRepository.getAll() → paginated LazyColumn
QuestionDetails  QuestionRepository.getById() + AnswerRepository.getByQuestionId()
AddEditQuestion  QuestionRepository.insert() / update()
Profile          AccountRepository.getById() + QuestionRepository + AnswerRepository
EditProfile      AccountRepository.update() + Supabase Storage (image upload)
Notifications    NotificationRepository.getAllByAccountId()
```
