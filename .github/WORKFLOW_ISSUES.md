# iOS CI/CD Workflow Issues & Solutions

> Reference file for AI assistants and developers. Update when new issues are encountered.

---

## Issue 1: Xcode Version Mismatch (Beta Selected)

**Symptom:** `Supported platforms for the buildables in the current scheme is empty`

**Root Cause:** Using `latest-stable` in `setup-xcode` action picked Xcode 26.3 beta on macOS 15 runners, which doesn't support the project's deployment targets properly.

**Fix:** Pin Xcode to a specific stable version:
```yaml
- uses: maxim-lobanov/setup-xcode@v1
  with:
    xcode-version: '16.2'
```

**Rule:** Always pin Xcode versions explicitly. Never use `latest-stable` or `latest`.

---

## Issue 2: iOS Simulator Runtime Not Installed

**Symptom:** `CompileAssetCatalog` fails with simulator runtime errors, or `Unable to find a destination matching the provided destination specifier`

**Root Cause:** macOS GitHub runners may not have the iOS simulator runtime pre-installed for the selected Xcode version.

**Fix:** Add a simulator download step before building:
```yaml
- name: Download iOS Simulator Runtime
  run: xcodebuild -downloadPlatform iOS
```

**Rule:** Always include the simulator runtime download step in iOS CI workflows.

---

## Issue 3: Missing `destination` in Fastlane build_app

**Symptom:** Build fails with platform/architecture resolution errors during `xcodebuild archive`.

**Root Cause:** Fastlane's `build_app` needs an explicit destination for device builds.

**Fix:** Add destination and disable index store in `Fastfile`:
```ruby
build_app(
  # ...existing config...
  destination: "generic/platform=iOS",
  xcargs: "COMPILER_INDEX_STORE_ENABLE=NO"
)
```

**Rule:** Always specify `destination` and disable index store in Fastlane release builds.

---

## Issue 4: Missing `actual` Implementation for iOS (KMP)

**Symptom:** `type 'Main_iosKt' has no member 'initKoin'` — the framework is found but functions are missing.

**Root Cause:** An `expect` declaration in `commonMain` had no corresponding `actual` in `iosMain`. This causes Kotlin/Native compilation to fail, producing an incomplete or empty framework. The Swift compiler then finds the module but not the expected symbols.

**Affected file (this instance):** `shared/src/commonMain/.../HttpClientFactory.kt` declared `expect fun getHttpClientEngine()` with no iOS actual.

**Fix:** Created `shared/src/iosMain/kotlin/com/ovasta/sellers/data/remote/HttpClientEngine.ios.kt`:
```kotlin
package com.ovasta.sellers.data.remote

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun getHttpClientEngine(): HttpClientEngine = Darwin.create()
```

**Rule:** After adding any `expect` declaration, immediately create `actual` implementations for ALL targets (android + ios). Run `./gradlew :shared:linkDebugFrameworkIosArm64` locally to verify iOS compilation before pushing.

**Detection:** If you see `type 'SomeKt' has no member 'functionName'` but the module imports fine (`import sharedKit` works), suspect a missing `actual` causing framework compilation failure.

---

## Issue 5: `agvtool` Warning About Missing Project

**Symptom:** `Cannot find "iosApp.xcodeproj/../NO"` warning in build logs.

**Root Cause:** `GENERATE_INFOPLIST_FILE: NO` in `project.yml` settings gets misinterpreted by agvtool as a path.

**Impact:** Cosmetic only — does not affect build success.

**Rule:** Ignore this warning. It does not cause build failures.

---

## General Rules for iOS CI

1. **Always pin tool versions** — Xcode, Ruby, Fastlane, CocoaPods.
2. **Always verify KMP framework builds** — Add a step that checks the framework exists and lists its headers after Gradle build.
3. **Check all `expect`/`actual` pairs** — Before pushing KMP changes, run:
   ```bash
   ./gradlew :shared:linkDebugFrameworkIosArm64 --stacktrace
   ```
4. **Pre-build script in project.yml** — The xcodegen project has a pre-build script that re-builds the framework. If CI already builds it, ensure both use the same configuration (Release vs Debug).
5. **Framework search paths** — `project.yml` must list ALL possible framework output paths (debug/release × arm64/simulatorArm64/x64).

---

## How to Update This File

When you encounter a new CI issue:
1. Add a new section with: **Symptom**, **Root Cause**, **Fix**, and **Rule**
2. Include the exact error message in the symptom
3. Add any prevention rules to the "General Rules" section
