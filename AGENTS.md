# devz — Android Q&A app (Kotlin + Compose + Supabase)

## Quick start

```bash
./gradlew assembleDebug            # build debug APK
./gradlew test                      # unit tests (JVM)
./gradlew connectedAndroidTest      # instrumented tests (device/emulator)
./gradlew lint                      # lint check
```

## Full file tree (88 source files)

```
com.mohamed.devz/
├── DevZApp.kt                         # @HiltAndroidApp
├── MainActivity.kt                    # @AndroidEntryPoint, setContent { DevzNavHost }
│
├── navigation/
│   ├── Route.kt                       # sealed interface, @Serializable: Splash, Onboarding, Auth, Home, QuestionDetails(id), AddEditQuestion(id?), EditProfile
│   ├── DevzNavHost.kt                 # NavHost with all 8 composable routes; splash pops backstack before forward nav
│   └── components/HomeScreen.kt       # Home screen (bottom nav shell with question list, notifications, profile tabs)
│
├── ui/theme/
│   ├── Color.kt                       # Dark theme palette (CyanPrimary, TextWhite, DevzCard, DevzInput, etc.)
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
    │       │   ├── LoginState.kt      # data class: email, password, isLoading, error: UIText?
    │       │   └── LoginScreen.kt     # Login form with animated gradient, hiltViewModel default param
    │       └── signup_screen/presentation/
    │           ├── SignUpAction.kt    # sealed interface { FullNameChanged, UsernameChanged, EmailChanged, PasswordChanged, ConfirmPasswordChanged, RegisterClicked(onSuccess) }
    │           ├── SignUpViewModel.kt # MVI: onAction → register() → AccountRepository.insert()
    │           ├── SignUpState.kt     # data class: fullName, username, email, password, confirmPassword, isLoading, error: UIText?
    │           └── SignUpScreen.kt    # Sign-up form with 5 fields, terms text, LazyColumn
    │
    ├── core/
    │   ├── domain/
    │   │   ├── model/
    │   │   │   ├── Account.kt         # id, fullName, email, password, imageUrl, bio, techStack, githubUrl, linkedInUrl, websiteUrl
    │   │   │   ├── Question.kt        # id, title, description, code, likesCount, answersCount, tags, langTypeId, accountId, createdAt
    │   │   │   ├── Answer.kt          # id, description, accepted, votedIds, questionId, accountId, createdAt
    │   │   │   ├── LanguageType.kt    # id, type
    │   │   │   ├── Notification.kt    # id, description, accountId, typeId, seen, createdAt
    │   │   │   └── NotificationType.kt# id, type
    │   │   ├── repository/
    │   │   │   ├── AccountRepository.kt          # getById, getByUsernameAndPassword, insert, update, uploadImage
    │   │   │   ├── QuestionRepository.kt         # getById, getByAccountId, getByTag, insert, update, delete
    │   │   │   ├── AnswerRepository.kt           # getById, getByQuestionId, getByAccountId, insert, update, delete
    │   │   │   ├── LanguageTypeRepository.kt     # getAll
    │   │   │   ├── NotificationRepository.kt     # insert, getAllByAccountId, update
    │   │   │   ├── NotificationTypeRepository.kt # getAll
    │   │   │   └── UserPreferencesRepository.kt  # observeIsFirstTime(), observeIsLoggedIn(), setNotFirstTime(), setLoggedIn(), setLoggedOut()
    │   │   └── util/
    │   │       ├── Error.kt           # sealed interface with 6 data objects: NotFound, Conflict, Unauthorized, Network, Storage, Unknown
    │   │       │                      # Also contains: fun Error.toUIText(): UIText (maps each error to a user-friendly message)
    │   │       └── Result.kt          # sealed interface Result<D, E : DomainError> { Success(data), Error(error, data?) }
    │   │
    │   ├── data/
    │   │   ├── data_source/
    │   │   │   ├── remote/
    │   │   │   │   ├── DevZRemoteDataSource.kt       # interface with 6 inner table interfaces (AccountTable, QuestionTable, AnswerTable, LanguageTypeTable, NotificationTable, NotificationTypeTable)
    │   │   │   │   └── DevZRemoteDataSourceImpl.kt   # Implementation: Postgrest queries via supabase-kt DSL (select, insert, update, delete, filter, decodeSingle, decodeList)
    │   │   │   └── local/preferences/
    │   │   │       ├── UserPreferences.kt            # interface: observeIsFirstTime, observeIsLoggedIn, setNotFirstTime, setLoggedIn, setLoggedOut
    │   │   │       └── UserPreferencesImpl.kt        # UserPreferencesManager (@Singleton, DataStore, preferencesDataStore("user_prefs"), two booleanPreferencesKey: is_first_time, is_logged_in)
    │   │   ├── model/                                # @Serializable data classes with @SerialName mappings (snake_case in DB → camelCase in Kotlin)
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
    │   │   └── UIText.kt               # sealed class UIText { StringValue(value), StringResource(resId, args) } with @Composable fun toUIText(): String
    │   └── di/CoreModule.kt            # @Module @InstallIn(SingletonComponent): provides SupabaseClient, Postgrest, Storage, DevZRemoteDataSource, all 7 repositories, UserPreferences
    │
    ├── question/presentation/
    │   ├── view_questions/
    │   │   ├── ViewQuestionsScreen.kt  # Feed list
    │   │   └── components/QuestionCard.kt
    │   ├── question_details/
    │   │   ├── QuestionDetailsScreen.kt
    │   │   └── components/ (AnswerCard, AnswerInputBar, Breadcrumb, CodeBlock, Colors, QuestionContent, TagChip, TopBar, ActionPill)
    │   ├── add_edit_qestion/
    │   │   ├── AddEditQuestionScreen.kt
    │   │   └── components/ (CodeEditorField, DefaultFieldLabel, LanguageDrowdownField)
    │   └── util/
    │       ├── SyntaxLanguage.kt       # Enum: JAVASCRIPT("JavScript" — typo), PYTHON("Python"), JAVA("Java"), etc.
    │       ├── Token.kt                # Sealed class: Keyword, Operator, Punctuation, etc.
    │       ├── tokenize.kt             # Character-by-character tokenizer per language
    │       └── IndentationFormatter.kt # Brace-based + Python indent formatters
    │
    ├── profile/presentation/
    │   ├── view_profile/
    │   │   ├── ProfileScreen.kt
    │   │   └── components/ (ProfileAnswerCard, ProfileQuestionCard, ProfileUiModel, StatCard)
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
                    UIText (presentation/util/)                         data/mapper/
                                                                        toDomain()/toData()
```

### Error handling

- **`Error`** (`domain/util/Error.kt`): sealed interface with 6 `data object` variants — `NotFound`, `Conflict`, `Unauthorized`, `Network`, `Storage`, `Unknown`
- **`Result<D, E : Error>`** (`domain/util/Result.kt`): sealed interface — `Success(data)` or `Error(error, data?)`. `E` is bounded by `Error` alias (`DomainError`).
- **`UIText`** (`presentation/util/UIText.kt`): sealed class — `StringValue(value: String)` for plain strings, `StringResource(resId: Int, args: List<Any>)` for localized resources. Has `@Composable fun toUIText(): String`.
- **`Error.toUIText()`** (`domain/util/Error.kt`): extension mapping each Error variant to a user-friendly `UIText.StringValue`.
- **Repository catch pattern**: Every repo impl catches `PostgrestRestException` (status-based routing → NotFound/Conflict/Unauthorized/Unknown), `IOException` → Network, generic `Exception` → Unknown.
- **ViewModel consume pattern**: `when (result) { is Result.Success -> ... ; is Result.Error -> _uiState.update { it.copy(error = result.error.toUIText()) } }`

### MVI pattern (unified across all ViewModels)

Every ViewModel follows the same architecture:

1. **Action** — sealed interface in a separate file (or inside VM for splash/onboarding). Data objects for intent-only actions, data classes carrying `value: String` for field changes, and `onSuccess: () -> Unit` for submission actions.
2. **State** — `data class` with form fields, `isLoading: Boolean`, `error: UIText?`. Exposed as `StateFlow` via `MutableStateFlow.asStateFlow()`.
3. **Events** (one-shot) — used in `SplashViewModel`: sealed interface emitted via `SharedFlow`, collected in `LaunchedEffect` in screen.
4. **`onAction(action)`** — single entry point dispatching via `when` expression.
5. **Screen** — receives `viewModel: XViewModel = hiltViewModel()` as default parameter; collects state with `.collectAsState()`; calls `viewModel.onAction(...)` from composable callbacks.

| Feature | Actions defined in              | State | Events | Submit target |
|---------|---------------------------------|-------|--------|---------------|
| Splash | Separate `SplashAction.kt`      | none | SharedFlow<SplashEvent> | decideWhereToGo() |
| Onboarding | Separate `OnboardingAction`     | none | none | finish() → setNotFirstTime() |
| Login | Separate `LoginAction.kt`       | LoginState | onSuccess callback | AccountRepository.getByUsernameAndPassword() |
| SignUp | Separate `SignUpAction.kt`      | SignUpState | onSuccess callback | AccountRepository.insert() |
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
- `toRoute()` for parameterized routes (`QuestionDetails(id)`, `AddEditQuestion(id?)`).
- `AddEditQuestion.id = null` → create mode, `non-null` → edit mode.

### Preferences (DataStore)

- **`UserPreferences`** interface (data layer) → **`UserPreferencesRepository`** interface (domain layer) → **`UserPreferencesRepositoryImpl`** wraps in `Result<Unit, Error>`.
- **`UserPreferencesManager`** (`@Singleton`, `@Inject constructor(@ApplicationContext)`): uses `preferencesDataStore(name = "user_prefs")` with 2 keys: `is_first_time` (default true), `is_logged_in` (default false).
- DataStore file path: `feature.core.data.data_source.local.preferences.UserPreferencesImpl.kt`.

### Backend (Supabase)

- **Client**: `createSupabaseClient(url, key)` with `Postgrest` + `Storage` plugins installed in `CoreModule`.
- **Auth plugin** (`gotrue-kt`) is in dependencies but **NOT installed** — auth is stubbed via PostgREST direct queries (plaintext password comparison).
- **6 tables**: `Account`, `Question`, `Answer`, `LanguageType`, `Notification`, `NotificationType` — mapped via `@SerialName` to snake_case.
- **Storage bucket**: `images` for profile photo uploads.
- **Supabase URL/anon key**: hardcoded in `CoreModule.kt` (no BuildConfig or secrets management).

### DI (Dagger Hilt)

- **KSP** annotation processing (no kapt). Compiler: `libs.hilt.android.compiler`.
- **Module**: `CoreModule` (`@Module @InstallIn(SingletonComponent::class)`).
- **Provides**: SupabaseClient, Postgrest, Storage, DevZRemoteDataSource, 7 repositories (Account, Question, Answer, LanguageType, Notification, NotificationType, UserPreferences), UserPreferences.
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
- **Most screens** still use hardcoded sample data (question list, profile, notifications, question details). Only SplashScreen, OnboardingScreen, LoginScreen, and SignUpScreen have fully wired ViewModels.
- **EditProfileScreen** has a ViewModel and Action sealed interface but the screen wiring is partial (not all fields connected to ViewModel).
- **NotificationType** field name `type` may conflict with Kotlin's `type` keyword — handled by `@SerialName`.
- **All screens** use dark-only theme (no light mode support in `Theme.kt`).
- **Local fonts** (Inter, Space Grotesk) from `res/font/` — loaded via Google Fonts Compose library.

## Conventions for new code

- MVI: sealed `Action` interface in separate file, `onAction(action)` entry point, `StateFlow<State>` with `_uiState` backing.
- Error strings: use `result.error.toUIText()` from `Error.toUIText()` — never hardcode error strings in ViewModels.
- Hardcoded validation errors (e.g., "Passwords do not match", "Invalid credentials"): use `UIText.StringValue("...")`.
- Repos: catch `PostgrestRestException` → status-based routing, `IOException` → Network, `Exception` → Unknown.
- Navigation: `navController.apply { popBackStack(); navigate(Route.X) }` for forward-only flows.
- Screens: receive `viewModel: XViewModel = hiltViewModel()` as default parameter.
- Imports: use `jakarta.inject.Inject` (not `javax`).
- Compose state: `val uiState by viewModel.uiState.collectAsState()`.
