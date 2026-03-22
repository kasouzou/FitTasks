# FitTasks Project Overview

FitTasks is a modern Android application designed to help users manage their tasks within specific time intervals. A key feature of the app is its ability to calculate the duration allocated to each task based on a given time range (e.g., dividing 37 minutes equally among 5 tasks).

## Technology Stack

- **Platform:** Android
- **Language:** Kotlin (Kotlin 2.2.10+)
- **UI Framework:** Jetpack Compose with Material3
- **Build System:** Gradle (Kotlin DSL) with Version Catalogs (`libs.versions.toml`)
- **Minimum SDK:** 27 (Android 8.1)
- **Target SDK:** 36

## Project Structure

- `app/`: Main application module.
  - `src/main/java/com/kasouzou/fittasks/`: Kotlin source code.
    - `MainActivity.kt`: Entry point of the application.
    - `model/`: Data models (e.g., `TaskGroup`, `TaskItem`).
    - `ui/`: Compose-based UI components.
      - `TaskListScreen.kt`: The main list view showing task groups.
      - `components/`: Reusable UI components like `TaskGroupCard`.
      - `theme/`: App-wide styling, colors, and typography.
- `gradle/`: Gradle configuration and wrapper files.
  - `libs.versions.toml`: Centralized dependency management.

## Building and Running

### Development Commands

- **Build Project:**
  ```bash
  ./gradlew assembleDebug
  ```
- **Run Unit Tests:**
  ```bash
  ./gradlew test
  ```
- **Run Android Instrumented Tests:**
  ```bash
  ./gradlew connectedAndroidTest
  ```
- **Install on Device/Emulator:**
  ```bash
  ./gradlew installDebug
  ```
- **Clean Build:**
  ```bash
  ./gradlew clean
  ```

## Development Conventions

- **UI Implementation:** Exclusively uses Jetpack Compose. Follow Material3 design guidelines.
- **Dependency Management:** All dependencies must be defined in `gradle/libs.versions.toml` and referenced using the `libs` catalog.
- **Architecture:** Follow modern Android development practices, emphasizing separation of concerns between data models and UI components.
- **Naming:** Follow standard Kotlin and Android naming conventions (PascalCase for classes, camelCase for variables/functions).
