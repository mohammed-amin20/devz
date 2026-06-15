# devZ — Android Q&A App

A programming Q&A Android app built with **Kotlin**, **Jetpack Compose**, and **Supabase**.

## Tech Stack

| Layer | Technology |
|-------|------------|
| UI | Jetpack Compose, Material3, Coil |
| Architecture | Clean Architecture + MVI (single module) |
| DI | Dagger Hilt (KSP) |
| Backend | Supabase (PostgREST + Storage) |
| Local | DataStore Preferences |
| Build | AGP 8.10.1, Kotlin 2.3.20, Gradle 8.11.1 |
| Min SDK | 26 |
| Target SDK | 36 |

## Features

- **Splash** — animated splash with auto-navigation based on auth state
- **Onboarding** — 3-page horizontal pager (first-time users)
- **Auth** — login/signup with PostgREST-based auth (username + password)
- **Question Feed** — paginated feed with search, category tabs, pull-to-refresh
- **Question Details** — full question view with answers, voting, code blocks
- **Add/Edit Question** — form with title, body, code, language, tags
- **Profile** — user profile with avatar, bio, stats, skills, questions/answers tabs
- **Edit Profile** — update name, bio, avatar, skills, social links
- **Notifications** — notification list

## Architecture

```
Screen → ViewModel (MVI) → Repository (interface) → RepositoryImpl → Supabase
                               │
                          Domain models
                          Error/Result sealed classes
```

- **MVI**: sealed `Action` → `onAction()` dispatch → `StateFlow<State>`
- **Errors**: sealed `Result<D, Error>` with `Success`/`Error` variants
- **UiText**: sealed class for string resources / dynamic strings, resolved via `@Composable asString()`

## Quick Start

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Lint check
./gradlew lint
```

Requires **JDK 17+** and Android SDK 36.

## Project Structure

```
com.mohamed.devz/
├── DevZApp.kt                  # @HiltAndroidApp
├── MainActivity.kt             # @AndroidEntryPoint
├── navigation/                 # Routes + NavHost
├── ui/theme/                   # Dark theme (Color, Theme, Type)
└── feature/
    ├── splash/
    ├── onboarding/
    ├── authentication/         # Login / SignUp
    ├── core/                   # Domain models, repos, data sources, DI
    ├── question/               # Feed, details, add/edit, syntax highlighter
    ├── profile/                # View profile, edit profile
    └── notification/
```

## Supabase Tables

- `Account` — user profiles with tech stack and social links
- `Question` — questions with tags, language type, likes/answers count
- `Answer` — answers with voting and acceptance
- `LanguageType` — programming language categories
- `Notification` / `NotificationType` — user notifications

## License

Internal project.
