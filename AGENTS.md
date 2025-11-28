# Repository Guidelines

## 回复语言

总是使用中文回答，除非用户指定使用其它语言回答时才使用中文以外的语言进行回答

## Project Structure & Module Organization
- Single Android app module `app/` with Kotlin sources under `app/src/main/java/com/example/calculate_temputer` and XML layouts in `app/src/main/res/layout`.
- Entry point `MainActivity.kt` wires a `ViewPager2` and bottom navigation through `MainPagerAdapter`; feature fragments live in `ui/calculator`, `ui/weather`, and `ui/profile`.
- Android resources follow standard buckets (`values`, `drawable`, `mipmap`, `xml`); add new strings to `res/values/strings.xml` and keep layout files snake_case.
- Tests live in `app/src/test` (unit) and `app/src/androidTest` (instrumentation).

## Build, Test, and Development Commands
- `./gradlew assembleDebug` — build a debug APK; use `installDebug` to push to a connected device/emulator.
- `./gradlew test` — run JVM unit tests in `app/src/test`.
- `./gradlew connectedAndroidTest` — run instrumentation tests on a device/emulator.
- `./gradlew lint` — run Android Lint; fix findings or document deferrals.
- `./gradlew clean` — clear build outputs when you hit caching issues.

## Coding Style & Naming Conventions
- Kotlin source uses 4-space indentation and idiomatic Kotlin (nullable handling, scoped functions when clear).
- Classes/Fragments/Activities are PascalCase; functions and properties are camelCase; constants UPPER_SNAKE_CASE.
- Layout XML filenames snake_case (e.g., `fragment_weather.xml`); IDs are also snake_case and scoped to their layout.
- Keep resources centralized: colors in `res/values/colors.xml`, themes in `res/values/themes.xml`/`values-night`.

## Testing Guidelines
- Favor fast JVM tests for logic; add Android tests when UI or platform 
- APIs are involved.
- Name tests with intent: `FunctionName_condition_expectedResult`; mirror package structure under `app/src/test`.
- Ensure new features ship with either unit coverage or an instrumentation check; prefer deterministic test data.

## Commit & Pull Request Guidelines
- Use short, imperative commit subjects (e.g., "Add weather tab state sync"); keep scope focused and include body when rationale is non-obvious.
- Before opening a PR: summarize changes, link related issues/requirements, list test commands run, and attach UI screenshots for visual tweaks.
- Keep PRs small and cohesive; note any known limitations or follow-ups.

## Security & Configuration Tips
- Do not commit secrets or API keys; keep local-only values in `local.properties` or environment variables.
- If adding network calls later, centralize endpoints/config in a single Kotlin object and gate debug tooling behind `BuildConfig.DEBUG`.
