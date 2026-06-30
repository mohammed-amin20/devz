# DevZ — Android Q&A App for Developers

A feature-rich question-and-answer platform for developers, built with **Kotlin** + **Jetpack Compose** and backed by **Supabase**. Users can ask coding questions, provide answers, vote, bookmark, edit their profile, and receive push notifications.

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
| Push notifications  | Firebase Cloud Messaging                                      |
| Image upload        | Supabase Storage (`images` bucket)                            |
| Build               | AGP 8.10.1, Gradle 8.11.1                                    |
| Min / Target SDK    | 26 / 36                                                      |
| Navigation          | Compose Navigation with type-safe `@Serializable` routes      |

**Declared but unused:** `androidx.room.ktx` (no Room compiler, database, entities, or DI wiring).

---

## Project Structure

All 94 source files live under `app/src/main/java/com/mohamed/devz/`.

```
com.mohamed.devz/
├── DevZApp.kt                           # @HiltAndroidApp, creates notification channel
├── MainActivity.kt                      # @AndroidEntryPoint, deep link handling
│
├── navigation/
│   ├── Route.kt                         # @Serializable sealed interface — 8 routes
│   ├── DevzNavHost.kt                   # NavHost wiring all routes
│   └── components/home/
│       ├── HomeScreen.kt                # Bottom nav shell (Feed / Notifications / Profile)
│       └── HomeViewModel.kt             # Exposes currentAccountId for cross-tab nav
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
    │   ├── AuthScreen.kt                # Toggle container (Login / SignUp)
    │   └── components/
    │       ├── login_screen/presentation/   # Login form
    │       └── signup_screen/presentation/  # Sign-up form (5 fields)
    │
    ├── core/
    │   ├── domain/
    │   │   ├── model/                   # 7 domain models
    │   │   ├── repository/              # 8 repository interfaces
    │   │   └── util/                    # Error, Result, FcmTokenUtil
    │   ├── data/
    │   │   ├── data_source/
    │   │   │   ├── remote/              # DevZRemoteDataSource (impl), FCM service
    │   │   │   └── local/preferences/   # UserPreferences (DataStore)
    │   │   ├── model/                   # @Serializable data models (snake_case mapping)
    │   │   ├── mapper/                  # toDomain() / toData() extensions
    │   │   └── repository/              # 8 repository impls
    │   ├── presentation/util/           # UiText, TimeFormatter
    │   └── di/CoreModule.kt             # Hilt @Module — provides all singletons
    │
    ├── question/presentation/
    │   ├── view_questions/              # Paginated feed, search, tag tabs, bookmarks
    │   ├── question_details/            # Question + answers, voting, code blocks
    │   ├── add_edit_question/           # Create/edit form with picker, tags, code editor
    │   └── util/                        # Syntax highlighting tokenizer
    │
    ├── profile/presentation/
    │   ├── view_profile/                # Stats, questions/answers tabs
    │   └── edit_profile/                # Form fields, photo upload, social links, skills
    │
    └── notification/presentation/       # Notification list with FCM
```

### Package quirk

`feature/authentication/presentation/components/login_screen/presentation/` — the word `presentation` appears twice in the path.

---

## Navigation

```
Splash → Onboarding (if first time) → Auth → Home
                                       ↑
                              Home has 3 bottom tabs:
                              Feed · Notifications · Profile
```

| Route                   | Type                           | Parameters          |
|-------------------------|--------------------------------|---------------------|
| Splash                  | `data object`                  | —                   |
| Onboarding              | `data object`                  | —                   |
| Auth                    | `data object`                  | —                   |
| Home                    | `data object`                  | —                   |
| QuestionDetails         | `data class`                   | `id: Int`           |
| AddEditQuestion         | `data class`                   | `id: Int?` (null=create) |
| EditProfile             | `data object`                  | —                   |
| Profile                 | `data class`                   | `accountId: Int`    |

- All forward navigations **pop the backstack first** to prevent back-navigation to auth/onboarding/splash.
- IDs are `Int` throughout the navigation chain.
- Deep links from FCM notifications navigate directly to `QuestionDetails`.

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
- **Sign-up** — full name, username, email, password, confirm password.
- Both set `is_logged_in` + `current_account_id` in DataStore on success.
- Plaintext password comparison via PostgREST query (no Supabase Auth).

### 4. Question Feed
- Paginated (10 per page) with infinite scroll.
- Pull-to-refresh.
- Search bar with debounced queries against title/description/code/tags.
- Category tabs (All, by language type).
- Tag-based filtering.
- Local-only bookmarking (in-memory `Set<Int>`).
- Each card shows: title, truncated body, code preview, tags, author avatar, time ago, likes/answers count.

### 5. Question Details
- Full question content with syntax-highlighted code blocks.
- Answers list with vote buttons (`voted_ids` tracking).
- Answer input bar at the bottom.
- Like/unlike question.
- Author profile navigation.
- Breadcrumb navigation.

### 6. Add / Edit Question
- Title, description, code fields.
- Language type dropdown (populated from `LanguageType` table).
- Tag input (comma-separated, stored as comma-separated string in DB).
- Create mode (`id = null`) vs. edit mode (`id = non-null`).

### 7. Profile
- **View profile** — avatar, name, bio, tech stack, stats (questions, answers, likes).
- Tabbed display of user's questions and answers.
- **Edit profile** — all fields editable, photo upload to Supabase Storage, skill chips, social links (GitHub, LinkedIn, website).

### 8. Notifications
- Notification list fetched from Supabase, ordered by recency.
- Firebase Cloud Messaging for push notifications.
- Notification channel created on app startup.
- Deep link from notification tap → question details.
- Runtime permission request (Android 13+).

### 9. Search History
- Recent search queries persisted in `SearchHistory` table.
- Shown as chips below search bar.

### 10. Syntax Highlighting
- Custom tokenizer for Kotlin, JavaScript, Python.
- Brace-based and Python-indent formatting.

### 11. Error Handling
- Unified `Result<D, E>` sealed interface.
- `Error` sealed interface: `NotFound`, `Conflict`, `Unauthorized`, `Network`, `Storage`, `Unknown(msg)`.
- `Error.toUIText()` maps each variant to user-friendly `UiText.DynamicString`.
- All repository impls catch `PostgrestRestException`, `IOException`, generic `Exception`.

---

## Data Models

| Domain Model   | Fields (key ones)                                                                    | Supabase Table    |
|----------------|--------------------------------------------------------------------------------------|-------------------|
| Account        | id, username, fullName, email, password, imageUrl, bio, techStack, githubUrl, ...    | Account           |
| Question       | id, title, description, code, likesCount, answersCount, tags, langTypeId, accountId  | Question          |
| Answer         | id, description, accepted, votedIds, questionId, accountId                           | Answer            |
| LanguageType   | id, type                                                                             | LanguageType      |
| Notification   | id, message, type, isRead, userId, actorId, questionId, answerId                     | Notification      |
| NotificationType | id, type                                                                           | NotificationType  |
| SearchHistory  | id, query, accountId, createdAt                                                      | SearchHistory     |

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

