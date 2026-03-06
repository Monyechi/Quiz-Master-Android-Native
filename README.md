# Quiz Master — Android Native (Kotlin)

A faithful Android port of the [QuizMaster](https://github.com/Monyechi/QuizMaster) ASP.NET MVC web application.

## Screenshots

<p float="left">
  <img src="screenshots/login.png" width="220" alt="Login Screen" />
  <img src="screenshots/dashboard.png" width="220" alt="Student Dashboard" />
  <img src="screenshots/quiz.png" width="220" alt="Quiz Screen" />
</p>

## Features

| Feature | Web App | Android App |
|---|---|---|
| Student registration / login | ASP.NET Identity | Room + SHA-256 hash auth |
| Instructor registration / login | ASP.NET Identity | Room + SHA-256 hash auth |
| Role selection at sign-up | Razor Register page | RadioButton (Student / Instructor) |
| Instructor profile (auto-generated key) | ✅ | ✅ |
| Student profile (display name, grade) | ✅ | ✅ |
| Select / enroll with instructor by key | ✅ | ✅ (list + key entry) |
| Science / Math / History quizzes | opentdb.com (client-side JS) | opentdb.com via Retrofit |
| Easy / Medium / Hard difficulty | ✅ | ✅ |
| Quiz result screen | In-page alert | Dedicated result fragment with score & rating |
| Student → Instructor messaging | ✅ | ✅ |
| Instructor → Student messaging | ✅ | ✅ |
| Inbox (received messages) | ✅ | ✅ |
| Session management | ASP.NET cookie auth | SharedPreferences |
| Logout | ✅ | Overflow menu → Logout |

## Architecture

```
app/
 └── src/main/java/com/quizmaster/app/
      ├── data/
      │   ├── local/
      │   │   ├── entity/          # Room entities: User, Instructor, Student, Message
      │   │   ├── dao/             # DAOs for each entity
      │   │   └── QuizMasterDatabase.kt
      │   ├── remote/
      │   │   ├── api/             # TriviaApiService (Open Trivia DB)
      │   │   └── model/           # TriviaResponse, TriviaQuestion
      │   └── repository/          # AuthRepository, InstructorRepository, StudentRepository,
      │                            # MessageRepository, QuizRepository
      ├── di/
      │   └── AppModule.kt         # Hilt DI: Room, Retrofit, DAOs
      ├── ui/
      │   ├── auth/                # LoginFragment, RegisterFragment, AuthViewModel
      │   ├── student/             # Dashboard, Create/Edit Profile, SelectInstructor
      │   ├── instructor/          # Dashboard, Create/Edit Profile, adapters
      │   ├── quiz/                # QuizPicker, Quiz, QuizResult, QuizViewModel
      │   └── message/             # Inbox, ComposeMessage, MessageViewModel
      ├── util/
      │   └── SessionManager.kt    # SharedPreferences-backed session
      └── QuizMasterApp.kt         # @HiltAndroidApp
```

**Stack:** Kotlin · Jetpack Navigation · Room · Hilt · Retrofit · Coroutines · Flow · LiveData · Material 3

## Quiz Categories

| Category | Open Trivia DB ID |
|---|---|
| Science & Nature | 17 |
| Mathematics | 19 |
| History | 23 |

## Getting Started

1. Clone the repo
2. Open in Android Studio Hedgehog or later
3. Let Gradle sync
4. Run on emulator or device (min SDK 24)

## Improvements over the Web App

- Fixed the Math/History quiz routing bug (all 3 subjects + all 3 difficulties correctly wired)
- Replaced denormalized `InstructorName` string with a proper FK (`instructorId`) on `StudentEntity`
- Replaced the typo `Reciever` with `receiver` throughout
- Messages use real user IDs instead of plain-string matching
- All quiz logic runs natively (no WebView / JS bridge)
