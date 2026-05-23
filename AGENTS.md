# devz — Android Q&A app (Kotlin + Compose + Supabase)

## Quick start

```bash
./gradlew assembleDebug            # build debug APK
./gradlew test                      # unit tests (JVM)
./gradlew connectedAndroidTest      # instrumented tests (device/emulator)
./gradlew lint                      # lint check
```

## Full file tree (92 source files)

```
com.mohamed.devz/
├── DevZApp.kt                         # @HiltAndroidApp
├── MainActivity.kt                    # @AndroidEntryPoint, setContent { DevzNavHost }
│
├── navigation/
│   ├── Route.kt                       # sealed interface, @Serializable: Splash, Onboarding, Auth, Home, QuestionDetails(id: Int), AddEditQuestion(id: Int?), EditProfile
│   ├── DevzNavHost.kt                 # NavHost with all 8 composable routes; splash pops backstack before forward nav
│   └── components/HomeScreen.kt       # Home screen (bottom nav shell with question list, notifications, profile tabs)
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
    │   └── components/
    │       ├── FirstScreen.kt         # Welcome page
    │       ├── SecondScreen.kt        # Features page
    │       └── ThirdScreen.kt         # Get started page
    │
    ├── authentication/presentation/
    │   ├── AuthScreen.kt              # Toggle container: index 0→LoginScreen, 1→SignUpScreen; animated gradient center
    │   └── components/
    │       ├── login_screen/presentation/
    │       │   ├── LoginAction.kt     # sealed interface { EmailChanged, PasswordChanged, LoginClicked(onSuccess) }
    │       │   ├── LoginViewModel.kt  # MVI: onAction → login() → AccountRepository.getByUsernameAndPassword()
    │       │   ├── LoginState.kt      # data class: email, password, isLoading, error: UiText?
    │       │   └── LoginScreen.kt     # Login form with animated gradient, hiltViewModel default param
    │       └── signup_screen/presentation/
    │           ├── SignUpAction.kt    # sealed interface { FullNameChanged, UsernameChanged, EmailChanged, PasswordChanged, ConfirmPasswordChanged, RegisterClicked(onSuccess) }
    │           ├── SignUpViewModel.kt # MVI: onAction → register() → AccountRepository.insert()
    │           ├── SignUpState.kt     # data class: fullName, username, email, password, confirmPassword, isLoading, error: UiText?
    │           └── SignUpScreen.kt    # Sign-up form with 5 fields, terms text, LazyColumn
    │
    ├── core/
    │   ├── domain/
    │   │   ├── model/
    │   │   │   ├── Account.kt         # id: Int, fullName, email, password, imageUrl, bio, techStack, githubUrl, linkedInUrl, websiteUrl
    │   │   │   ├── Question.kt        # id: Int, title, description, code, likesCount, answersCount, tags, langTypeId, accountId, createdAt
    │   │   │   ├── Answer.kt          # id: Int, description, accepted, votedIds, questionId, accountId, createdAt
    │   │   │   ├── LanguageType.kt    # id: Int, type
    │   │   │   ├── Notification.kt    # id: Int, description, accountId, typeId, seen, createdAt
    │   │   │   └── NotificationType.kt# id: Int, type
    │   │   ├── repository/
    │   │   │   ├── AccountRepository.kt          # getById, getByUsernameAndPassword, getAll, insert, update, uploadImage
    │   │   │   ├── QuestionRepository.kt         # getById, getByAccountId, getByTag, getAll(offset,limit,orderBy,asc), insert, update, delete
    │   │   │   ├── AnswerRepository.kt           # getById, getByQuestionId, getByAccountId, insert, update, delete
    │   │   │   ├── LanguageTypeRepository.kt     # getAll
    │   │   │   ├── NotificationRepository.kt     # insert, getAllByAccountId, update
    │   │   │   ├── NotificationTypeRepository.kt # getAll
    │   │   │   └── UserPreferencesRepository.kt  # observeIsFirstTime, observeIsLoggedIn, observeCurrentAccountId, setNotFirstTime, setLoggedIn, setAccountId, setLoggedOut, clearAccountId
    │   │   └── util/
    │   │       ├── Error.kt           # sealed interface: NotFound, Conflict, Unauthorized, Network, Storage (data object), Unknown(val message: String)
    │   │       │                      # Also contains: fun Error.toUIText(): UiText (maps each error to a user-friendly message)
    │   │       └── Result.kt          # sealed interface Result<D, E : DomainError> { Success(data), Error(error, data?) }
    │   │
    │   ├── data/
    │   │   ├── data_source/
    │   │   │   ├── remote/
    │   │   │   │   ├── DevZRemoteDataSource.kt       # interface with 6 inner table interfaces; QuestionTable has getAll(offset,limit,orderBy,asc), AccountTable has getAll
    │   │   │   │   └── DevZRemoteDataSourceImpl.kt   # Implementation: Postgrest queries via supabase-kt DSL
    │   │   │   └── local/preferences/
    │   │   │       ├── UserPreferences.kt            # interface: observeIsFirstTime, observeIsLoggedIn, observeCurrentAccountId, setNotFirstTime, setLoggedIn, setAccountId, setLoggedOut, clearAccountId
    │   │   │       └── UserPreferencesImpl.kt        # UserPreferencesManager (@Singleton, DataStore, preferencesDataStore("user_prefs"), 3 keys: is_first_time, is_logged_in, current_account_id)
    │   │   ├── model/                                # @Serializable data classes with @SerialName mappings (snake_case → camelCase)
    │   │   │   ├── Account.kt, Question.kt, Answer.kt, LanguageType.kt, Notification.kt, NotificationType.kt
    │   │   ├── mapper/
    │   │   │   ├── AccountMapper.kt      # DataAccount.toDomain(), DomainAccount.toData()
    │   │   │   ├── QuestionMapper.kt     # DataQuestion.toDomain(), DomainQuestion.toData()
    │   │   │   ├── AnswerMapper.kt       # DataAnswer.toDomain(), DomainAnswer.toData()
    │   │   │   ├── LanguageTypeMapper.kt # DataLanguageType.toDomain(), DomainLanguageType.toData()
    │   │   │   ├── NotificationMapper.kt # DataNotification.toDomain(), DomainNotification.toData()
    │   │   │   └── NotificationTypeMapper.kt # DataNotificationType.toDomain(), DomainNotificationType.toData()
    │   │   └── repository/
    │   │       ├── AccountRepositoryImpl.kt          # Catches PostgrestRestException, IOException, Exception → maps to Error variants
    │   │       ├── QuestionRepositoryImpl.kt         # (same pattern)
    │   │       ├── AnswerRepositoryImpl.kt           # (same pattern)
    │   │       ├── LanguageTypeRepositoryImpl.kt     # (same pattern)
    │   │       ├── NotificationRepositoryImpl.kt     # (same pattern)
    │   │       ├── NotificationTypeRepositoryImpl.kt # (same pattern)
    │   │       └── UserPreferencesRepositoryImpl.kt  # Delegates to UserPreferences interface, wraps in Result
    │   ├── presentation/util/
    │   │   └── UiText.kt               # sealed class UiText { DynamicString(value), StringResource(resId, args) } with @Composable fun asString(): String
    │   └── di/CoreModule.kt            # @Module @InstallIn(SingletonComponent): provides SupabaseClient, Postgrest, Storage, DevZRemoteDataSource, all 7 repositories, UserPreferences
    │
    ├── question/presentation/
    │   ├── view_questions/
    │   │   ├── ViewQuestionsAction.kt     # sealed interface: LoadNextPage, Refresh, BookmarkToggled, SearchQueryChanged, TabSelected
    │   │   ├── ViewQuestionsState.kt      # data class: questions, isLoading, isLoadingMore, error, searchQuery, selectedTab, bookmarkedIds, hasMore
    │   │   ├── ViewQuestionsViewModel.kt  # MVI: paginated feed, caches Account + LanguageType on init, builds QuestionFeedUiModel
    │   │   ├── QuestionFeedUiModel.kt     # data class: id, title, body, code, tags, authorName, authorAvatar, timeAgo, likes, answers, isBookmarked, langTypeId
    │   │   ├── ViewQuestionsScreen.kt     # Feed list with search bar, category tabs, pull-to-refresh, infinite scroll pagination
    │   │   └── components/QuestionCard.kt # Takes QuestionFeedUiModel
    │   ├── question_details/
    │   │   ├── QuestionDetailsAction.kt   # sealed interface: LoadQuestion(id), AnswerTextChanged(value), PostAnswer(onSuccess)
    │   │   ├── QuestionDetailsState.kt    # data class: question (QuestionDetailUiModel), answers, answerText, isLoading, isPosting, error
    │   │   ├── QuestionDetailsViewModel.kt# MVI: loads question + answers, posts answer
    │   │   ├── QuestionDetailsScreen.kt   # Wired with ViewModel, shows question content + answer input bar
    │   │   └── components/ (AnswerCard, AnswerInputBar, Breadcrumb, CodeBlock, QuestionContent, TagChip, TopBar, ActionPill)
    │   ├── add_edit_qestion/
    │   │   ├── AddEditQuestionAction.kt   # sealed interface: LoadQuestion(id), TitleChanged, BodyChanged, CodeChanged, LanguageSelected, TagInputChanged, AddTag, RemoveTag, ShowTagInput, Publish
    │   │   ├── AddEditQuestionState.kt    # data class: title, body, code, selectedLangTypeId, tags, tagInput, showTagInput, languageTypes, isLoading, isEdit, editQuestionId, error
    │   │   ├── AddEditQuestionViewModel.kt# MVI: loads language types, loads question for edit, publishes/updates
    │   │   ├── AddEditQuestionScreen.kt   # Wired with ViewModel, all form fields connected
    │   │   └── components/ (CodeEditorField, DefaultFieldLabel, LanguageDrowdownField)
    │   └── util/
    │       ├── SyntaxLanguage.kt       # Enum: KOTLIN, JAVASCRIPT("JavScript"), PYTHON, GENERIC
    │       ├── Token.kt                # Sealed class: Keyword, Operator, Punctuation, etc.
    │       ├── tokenize.kt             # Character-by-character tokenizer per language + formatCode()
    │       └── IndentationFormatter.kt # Brace-based + Python indent formatters
    │
    ├── profile/presentation/
    │   ├── view_profile/
    │   │   ├── ProfileScreen.kt          # onQuestionClick: (Int) -> Unit
    │   │   └── components/ (ProfileAnswerCard, ProfileQuestionCard, ProfileUiModel, StatCard)
    │   │       ├── ProfileUiModel.kt     # ProfileUiState(id: Int), ProfileQuestionUiModel(id: Int), ProfileAnswerUiModel(id: Int)
    │   │       └── ProfileViewModel.kt   # @HiltViewModel (stub with hardcoded data)
    │   └── edit_profile/
    │       ├── EditProfileAction.kt    # sealed interface: UpdateField, UploadPhoto, Save, Logout, etc.
    │       ├── EditProfileViewModel.kt # MVI: onAction(action) dispatch
    │       ├── EditProfileState.kt     # data class with all form fields + isLoading + error
    │       └── components/ (GovernanceToggle, SectionHeader, SkillChip, SocialField)
    │
    └── notification/presentation/
        └── NotificationsScreen.kt      # Notification list (hardcoded sample data)
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

- **File path**: `feature/core/presentation/util/UiText.kt` (lowercase 'i' in `UiText`)
- **Sealed class `UiText`** with two variants:
  - `DynamicString(value: String)` — plain string (previously `StringValue`)
  - `StringResource(resId: Int, args: List<Any>)` — localized resource
- **`@Composable fun asString(): String`** — resolves to the string value (previously `toUIText()`)
- **Usage**: `UiText.DynamicString("error message")` in ViewModels
- **Error display**: `.asString()` called inside composable: `Text(text = it.asString(), ...)`

### Error handling

- **`Error`** (`domain/util/Error.kt`): sealed interface — `NotFound`, `Conflict`, `Unauthorized`, `Network`, `Storage` as `data object` (no params), **`Unknown(val message: String)`** as `data class` with message
- **`Result<D, E : Error>`** (`domain/util/Result.kt`): sealed interface — `Success(data)` or `Error(error, data?)`. `E` is bounded by `Error` alias (`DomainError`).
- **`Error.toUIText()`** (`domain/util/Error.kt`): extension mapping each Error variant to a user-friendly `UiText.DynamicString`. Only `Unknown` passes the message through; all others use hardcoded strings.
- **Repository catch pattern**: Every repo impl catches `PostgrestRestException` (status-based routing → NotFound/Conflict/Unauthorized/Unknown), `IOException` → Network, generic `Exception` → Unknown.
- **ViewModel consume pattern**: `when (result) { is Result.Success -> ... ; is Result.Error -> _uiState.update { it.copy(error = result.error.toUIText()) } }`

### MVI pattern (unified across all ViewModels)

Every ViewModel follows the same architecture:

1. **Action** — sealed interface in a separate file. Data objects for intent-only actions, data classes carrying `value: String` for field changes, and `onSuccess: () -> Unit` for submission actions.
2. **State** — `data class` with form fields, `isLoading: Boolean`, `error: UiText?`. Exposed as `StateFlow` via `MutableStateFlow.asStateFlow()`.
3. **Events** (one-shot) — used in `SplashViewModel`: sealed interface emitted via `SharedFlow`, collected in `LaunchedEffect` in screen.
4. **`onAction(action)`** — single entry point dispatching via `when` expression.
5. **Screen** — receives `viewModel: XViewModel = hiltViewModel()` as default parameter; collects state with `.collectAsState()`; calls `viewModel.onAction(...)` from composable callbacks.

| Feature | Actions defined in              | State | Events | Submit target |
|---------|---------------------------------|-------|--------|---------------|
| Splash | Separate `SplashAction.kt`      | none | SharedFlow<SplashEvent> | decideWhereToGo() |
| Onboarding | Separate `OnboardingAction`     | none | none | finish() → setNotFirstTime() |
| Login | Separate `LoginAction.kt`       | LoginState | onSuccess callback | AccountRepository.getByUsernameAndPassword() |
| SignUp | Separate `SignUpAction.kt`      | SignUpState | onSuccess callback | AccountRepository.insert() |
| ViewQuestions | Separate `ViewQuestionsAction.kt` | ViewQuestionsState | none | QuestionRepository.getAll() |
| QuestionDetails | Separate `QuestionDetailsAction.kt` | QuestionDetailsState | onSuccess callback | QuestionRepository.getById(), AnswerRepository.insert() |
| AddEditQuestion | Separate `AddEditQuestionAction.kt` | AddEditQuestionState | onSuccess callback | QuestionRepository.insert()/update() |
| EditProfile | Separate `EditProfileAction.kt` | EditProfileState | none | AccountRepository.update() |

### Navigation flow

```
Splash (via DataStore)
  ├─ isFirstTime=true  → Onboarding → Auth → Home
  ├─ not first & not logged-in → Auth → Home
  └─ logged-in → Home

AuthScreen toggles between LoginScreen (index=0) and SignUpScreen (index=1).
Each forward navigation pops the backstack first (no back-navigation to previous steps).
```

- Routes are `@Serializable` sealed interface `Route` with data objects/classes.
- `DevzNavHost` uses `NavHost` with `composable<Route.X>` type-safe syntax.
- `toRoute()` for parameterized routes: `Route.QuestionDetails(id: Int)`, `Route.AddEditQuestion(id: Int?)`.
- `AddEditQuestion.id = null` → create mode, `non-null` → edit mode.
- **IDs are `Int` throughout the entire navigation chain** (Route, DevzNavHost, HomeScreen, Screen lambdas).

### Preferences (DataStore)

- **`UserPreferences`** interface (data layer) → **`UserPreferencesRepository`** interface (domain layer) → **`UserPreferencesRepositoryImpl`** wraps in `Result<Unit, Error>`.
- **`UserPreferencesManager`** (`@Singleton`, `@Inject constructor(@ApplicationContext)`): uses `preferencesDataStore(name = "user_prefs")` with 3 keys:
  - `is_first_time` (boolean, default `true`)
  - `is_logged_in` (boolean, default `false`)
  - `current_account_id` (int, default `0`)
- DataStore file path: `feature.core.data.data_source.local.preferences.UserPreferencesImpl.kt`.
- `current_account_id` is saved in `LoginViewModel.login()` and `SignUpViewModel.register()` immediately after `setLoggedIn()`.

### Backend (Supabase)

- **Client**: `createSupabaseClient(url, key)` with `Postgrest` + `Storage` plugins installed in `CoreModule`.
- **Auth plugin** (`gotrue-kt`) is in dependencies but **NOT installed** — auth is stubbed via PostgREST direct queries (plaintext password comparison).
- **6 tables**: `Account`, `Question`, `Answer`, `LanguageType`, `Notification`, `NotificationType` — mapped via `@SerialName` to snake_case.
- **Storage bucket**: `images` for profile photo uploads.
- **Supabase URL/anon key**: hardcoded in `CoreModule.kt` (no BuildConfig or secrets management).

### DI (Dagger Hilt)

- **KSP** annotation processing (no kapt). Compiler: `libs.hilt.android.compiler`.
- **Module**: `CoreModule` (`@Module @InstallIn(SingletonComponent::class)`).
- **Provides**: SupabaseClient, Postgrest, Storage, DevZRemoteDataSource, 7 repositories, UserPreferences.
- All repo providers bind interface → implementation using `@Provides @Singleton`. No `@Binds`.
- Uses `jakarta.inject.Inject` (not `javax`).

### Data models

| Domain | Data (@Serializable) | @SerialName differences |
|--------|---------------------|------------------------|
| Account | Account | full_name, image_url, tech_stack, github_url, linkedin_url, website_url |
| Question | Question | likes_count, answers_count, lang_type_id, account_id, created_at |
| Answer | Answer | voted_ids, question_id, account_id, created_at |
| LanguageType | LanguageType | (none — identical) |
| Notification | Notification | account_id, type_id, created_at |
| NotificationType | NotificationType | (none — identical) |

Mappers are `toDomain()`/`toData()` extension functions using import aliases (`as DataAccount`/`as DomainAccount`).

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

- **Room** (`androidx.room.ktx`) is listed in dependencies but has NO compiler (ksp), no database, no entities, no wiring in CoreModule.
- **Account domain model** stores plaintext `password` — insecure PostgREST-based auth. `gotrue-kt` is in deps but Auth plugin not installed in SupabaseClient.
- **`SyntaxLanguage.JAVASCRIPT`** has typo: label is `"JavScript"` not `"JavaScript"`.
- **Package quirk**: `feature/authentication/presentation/components/login_screen/presentation/` — `presentation` appears twice.
- **proguard-rules.pro**: exists but empty boilerplate.
- **EditProfileScreen** has a ViewModel and Action sealed interface but the screen wiring is partial (not all fields connected to ViewModel).
- **ProfileViewModel** still uses hardcoded sample data — not wired to repository.
- **NotificationsScreen** still uses hardcoded sample data — no ViewModel.
- **QuestionCard preview** uses hardcoded `QuestionFeedUiModel` — not a live preview.
- **NotificationType** field name `type` may conflict with Kotlin's `type` keyword — handled by `@SerialName`.
- **All screens** use dark-only theme (no light mode support in `Theme.kt`).
- **Local fonts** (Inter, Space Grotesk) from `res/font/` — loaded via Google Fonts Compose library.

## Conventions for new code

- MVI: sealed `Action` interface in separate file, `onAction(action)` entry point, `StateFlow<State>` with `_uiState` backing.
- Error strings: use `result.error.toUIText()` from `Error.toUIText()` — never hardcode error strings in ViewModels.
- Hardcoded validation errors (e.g., "Passwords do not match", "Invalid credentials"): use `UiText.DynamicString("...")`.
- Error display in composables: call `.asString()` on `UiText` (e.g., `Text(text = error.asString())`).
- Repos: catch `PostgrestRestException` → status-based routing, `IOException` → Network, `Exception` → Unknown. Only `Unknown` carries a constructor message; all other errors are `data object`.
- Navigation: `navController.apply { popBackStack(); navigate(Route.X) }` for forward-only flows. IDs are `Int`.
- Screens: receive `viewModel: XViewModel = hiltViewModel()` as default parameter.
- Imports: use `jakarta.inject.Inject` (not `javax`).
- Compose state: `val uiState by viewModel.uiState.collectAsState()`.
- Pagination: ViewModel tracks `currentPage`, `hasMore`, `PAGE_SIZE = 10`. Data source uses `.range().order().decodeList()`. Feed loads initially + on scroll to bottom.
- Question feed: `ViewQuestionsViewModel` loads all `Account` + `LanguageType` into caches on init, builds `QuestionFeedUiModel` via `toFeedUiModel()`.
- Bookmark: local-only via `bookmarkedIds: Set<Int>` in state, not persisted.
- Colors for question details: use Q-prefixed colors (`QBg`, `QPrimary`, `QOutline`, `QOnSurface`, `QOnSurfaceVariant`) from `ui/theme/Color.kt`.
- AddEdit tags: stored as comma-separated `String` in DB (`Question.tags`); converted to/from `List<String>` in ViewModel.
