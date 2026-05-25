# KMP UX Parity Implementation Plan (v2)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Restore ALL 58 UI/UX features lost during the KMP migration so every screen matches the pre-KMP experience on Android and iOS with no regressions.

**Architecture:** Set up composeResources (strings EN+AR, 47 drawables, 8 fonts) → fix all screens → restore platform features (SDP/SSP, iOS toasts, back press) → clean up dead code and inconsistencies → verify.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform 1.8.2, compose.components.resources, Material 3, Koin, Intuit SDP/SSP

---

## Phase 0: Infrastructure (Tasks 1-4)
### composeResources setup — foundation for everything

### Task 0.1: Configure composeResources in Gradle

**Files:**
- Modify: `shared/build.gradle.kts` — after `android {}` block

Add:
```kotlin
compose.resources {
    publicResClass = true
    packageOfResClass = "com.ovasta.sellers.resources"
    generateResClass = always
}
```

- [ ] Verify: `./gradlew :shared:compileKotlinAndroid` → BUILD SUCCESSFUL

**Commit:**
```bash
git add shared/build.gradle.kts
git commit -m "chore: configure composeResources for shared module"
```

### Task 0.2: Migrate String Resources (EN + AR)

**Files:**
- Create: `shared/src/commonMain/composeResources/values/strings.xml` (copy `app/src/main/res/values/strings.xml`)
- Create: `shared/src/commonMain/composeResources/values-ar/strings.xml` (copy `app/src/main/res/values-ar/strings.xml`)

- [ ] Verify: `./gradlew :shared:compileKotlinAndroid` → generates `Res.string.*` accessors

**Commit:**
```bash
git add shared/src/commonMain/composeResources/values/
git commit -m "feat: migrate string resources (EN + AR) to composeResources"
```

### Task 0.3: Migrate Drawable Resources (47 XML vectors)

**Files:**
- Copy all 47 XML files from `app/src/main/res/drawable/` to `shared/src/commonMain/composeResources/drawable/`

**IMPORTANT:** Open each XML file and verify it uses only `<vector>` + `<path>` with hardcoded hex colors (e.g., `#FF000000`). If any references `@color/xxx`, replace with hardcoded value. Compose Multiplatform does not support Android resource references in XML drawables.

- [ ] Verify: `./gradlew :shared:compileKotlinAndroid` → generates `Res.drawable.*` accessors

**Commit:**
```bash
git add shared/src/commonMain/composeResources/drawable/
git commit -m "feat: migrate 47 vector drawables to composeResources"
```

### Task 0.4: Migrate Font Resources (8 files) + Update Typography

**Files:**
- Copy 8 fonts from `app/src/main/res/font/` to `shared/src/commonMain/composeResources/font/`
- Modify: `shared/src/commonMain/kotlin/com/ovasta/sellers/ui/theme/Type.kt`
- Modify: `shared/src/commonMain/kotlin/com/ovasta/sellers/base/styles.kt`

**Type.kt** — replace with Almarai font family:
```kotlin
package com.ovasta.sellers.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ovasta.sellers.resources.Res
import com.ovasta.sellers.resources.almarai_bold
import com.ovasta.sellers.resources.almarai_extra_bold
import com.ovasta.sellers.resources.almarai_light
import com.ovasta.sellers.resources.almarai_regular

val AlmaraiFontFamily: FontFamily
    @Composable
    get() = FontFamily(
        Font(Res.font.almarai_light, FontWeight.Light),
        Font(Res.font.almarai_regular, FontWeight.Normal),
        Font(Res.font.almarai_bold, FontWeight.Bold),
        Font(Res.font.almarai_extra_bold, FontWeight.ExtraBold)
    )

val Typography: Typography
    @Composable
    get() = Typography(
        bodyLarge = TextStyle(
            fontFamily = AlmaraiFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        )
    )
```

**styles.kt** — replace ALL `FontFamily.Default` with `AlmaraiFontFamily` + `@Composable` on every getter (11 styles). See original plan for full code.

- [ ] Verify: `./gradlew :shared:compileKotlinAndroid` → BUILD SUCCESSFUL

**Commit:**
```bash
git add shared/src/commonMain/composeResources/font/
git add shared/src/commonMain/kotlin/com/ovasta/sellers/ui/theme/Type.kt
git add shared/src/commonMain/kotlin/com/ovasta/sellers/base/styles.kt
git commit -m "feat: migrate Almarai fonts to composeResources and apply to all styles"
```

---

## Phase 1: Critical Bugs (Tasks 5-8)
### Memory corruption, crashes, blocking UX issues

### Task 1.1: Fix Invalid Hex Color in ComposableColor.kt

**Issue #1:** `val White = Color(0xFFFEFFFFF)` has 9 hex digits — crashes at runtime.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/base/ComposableColor.kt:32`

- [ ] Change `Color(0xFFFEFFFFF)` → `Color(0xFFFFFFFF)`

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/base/ComposableColor.kt
git commit -m "fix: correct invalid hex color White (9 digits → 8 digits)"
```

### Task 1.2: Add System Back Press Handler

**Issue #6:** Android physical back button exits app instead of navigating custom backstack.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/App.kt`

Add to `AppNavHost`:
```kotlin
import androidx.activity.compose.BackHandler

// Inside AppNavHost, after CompositionLocalProvider:
BackHandler(
    enabled = backStack.size > 1
) {
    navigator.pop()
}
```

- [ ] Add `implementation(libs.activity.compose)` to `shared/build.gradle.kts` commonMain dependencies (if not already present)

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/App.kt
git commit -m "fix: add system back press handler for custom navigation"
```

### Task 1.3: Fix Potential NPE in OrderHistoryContent

**Issue #7:** `courierMobile!!` can throw NPE if courier data changes between null check and click.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/profile/orderhistory/presentation/components/OrderHistoryContent.kt:224`

- [ ] Change:
```kotlin
IconButton(
    onClick = { onCallCourier(courierMobile!!) },
```
to:
```kotlin
IconButton(
    onClick = { courierMobile?.let { onCallCourier(it) } },
```

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/profile/orderhistory/presentation/components/OrderHistoryContent.kt
git commit -m "fix: prevent NPE in courier call button"
```

### Task 1.4: Fix CreateOrder Confirm Dialog — Empty Text + Missing Delivery Timing UI

**Issues #2, #3, #4, #5, #12, #13:** Confirm dialog shows blank text. Delivery timing toggle, date picker, and time picker entirely missing. `isSubmitting` not consumed.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/createOrder/presentation/components/CreateOrderContent.kt`

- [ ] **Step A:** Add delivery timing toggle (`Row` with `FilterChip` or `SegmentedButton` for "Now" / "Later")
- [ ] **Step B:** Add date field (visible when `deliveryTiming == LATER`) with `OutlinedTextField` for date input
- [ ] **Step C:** Add time field (visible when `deliveryTiming == LATER`) with `OutlinedTextField` for time input
- [ ] **Step D:** Wire submit button `enabled` to `viewState.isValid() && !viewState.isSubmitting`
- [ ] **Step E:** Populate confirm dialog with `stringResource()` values, add `iconPainter = painterResource(Res.drawable.ic_confirm)`
- [ ] **Step F:** Add `imePadding()`, `statusBarsPadding()`, `navigationBarsPadding()`

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/createOrder/data/model/CreateOrderRequest.kt`

- [ ] **Step G:** Add `deliveryTiming`, `scheduledDate`, `scheduledTime` fields to data request model
- [ ] **Step H:** Update `ICreateOrderRepository`, `CreateOrderRepository`, `ICreateOrderRemoteDataSource`, `CreateOrderRemoteDataSource` to pass these fields to API

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/createOrder/
git commit -m "fix: restore CreateOrder delivery timing UI, confirm dialog, and data layer"
```

---

## Phase 2: Core Components (Tasks 9-13)
### Shared base components — fixes cascade to all screens

### Task 2.1: Fix SplashScreen — Restore Logo

**Issue #24:** SplashScreen shows empty white box.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/auth/splash/SplashScreen.kt`

Add `Image(painter = painterResource(Res.drawable.logo), ...)` centered in the Box.

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/auth/splash/SplashScreen.kt
git commit -m "fix: restore splash screen logo"
```

### Task 2.2: Fix CenteredTextAppBar — Restore Back Arrow Icon

**Issue #35:** Back button is literal `"<"` text character.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/base/CenteredTextAppBar.kt`

Replace `Text("<")` with `Icon(painter = painterResource(Res.drawable.ic_arrow_back), contentDescription = "Back")`.

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/base/CenteredTextAppBar.kt
git commit -m "fix: restore back arrow icon in CenteredTextAppBar"
```

### Task 2.3: Update BaseDialog — Painter Icon Support

**Issue:** `BaseDialog` uses `ImageVector?` but drawables from composeResources return `Painter`.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/base/components/sharedComposable/BaseDialog.kt`

- [ ] Change `icon: ImageVector?` → `iconPainter: Painter?`
- [ ] Change `Icon(imageVector = icon, ...)` → `Icon(painter = iconPainter, ...)`

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/base/components/sharedComposable/BaseDialog.kt
git commit -m "refactor: update BaseDialog to use Painter instead of ImageVector for icons"
```

### Task 2.4: Fix BaseScreen — Error Icon, Localized Strings, Dialog Overlap

**Issues #37, #38, #39:** Error dialog has no icon, hardcoded English strings, can overlap with loading dialog.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/base/components/sharedComposable/BaseScreen.kt`

- [ ] Add `iconPainter = painterResource(Res.drawable.alert_circle_red)` to error dialog
- [ ] Replace hardcoded `"Error"`, `"An unexpected error occurred"`, `"Dismiss"` with `stringResource()` calls
- [ ] Add mutual exclusion: show error dialog OR loading, not both simultaneously

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/base/components/sharedComposable/BaseScreen.kt
git commit -m "fix: restore error icon, localized strings, and fix dialog overlap in BaseScreen"
```

### Task 2.5: Fix Theme — Disconnect Template Colors, Fix BrandColor Duplicate

**Issues #11, #12:** Theme uses unused `Purple40/PurpleGrey80` template colors. `BrandColor` duplicates `Primary`.

**Files:**
- `shared/src/commonMain/kotlin/com/ovasta/sellers/ui/theme/Color.kt`
- `shared/src/commonMain/kotlin/com/ovasta/sellers/ui/theme/Theme.kt`
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/createOrder/presentation/components/CreateOrderContent.kt:30`

- [ ] Replace all `Purple40`, `PurpleGrey40`, `Pink40`, `Purple80`, `PurpleGrey80`, `Pink80` with the actual brand colors from `ComposableColor.kt`
- [ ] Replace `private val BrandColor = Color(0xFF006D98)` with import of shared `Primary`

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/ui/theme/
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/createOrder/presentation/components/CreateOrderContent.kt
git commit -m "fix: use brand colors in theme, remove duplicate BrandColor"
```

---

## Phase 3: All Screens (Tasks 14-19)
### Screen-by-screen string, icon, and behavior fixes

### Task 3.1: Fix LoginScreen — Strings, Password Toggle, Keyboard

**Issues #25, #26:** Placeholder text, missing password toggle, no `imePadding()`.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/auth/login/presentation/LoginScreen.kt`

Replacements:
| Old | New |
|-----|-----|
| `"placeholder_text"` (×6) | `stringResource(Res.string.xxx)` |
| Empty `trailingIcon = {}` | `IconButton` with `ic_visibility_on`/`ic_visibility_off` |
| Root Column | Add `.imePadding()` |
| Bottom Box | Add `.navigationBarsPadding()` |

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/auth/login/presentation/LoginScreen.kt
git commit -m "fix: restore LoginScreen strings, password toggle, imePadding"
```

### Task 3.2: Fix SellerHomeContent + HomeScreen — Strings, Icons, Pull-to-Refresh, Courier Call

**Issues #15, #16, #30, #32, #49:** Empty texts, dead pull-to-refresh, courier call is empty lambda, `OrderClicked` is TODO stub, `StatusBadge` uses `StatusTag`.

**Files:**
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/presentation/HomeScreen.kt`
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/presentation/components/SellerHomeContent.kt`

- [ ] **HomeScreen:** Wrap in `PullToRefreshBox` using `viewState.isRefreshing` and `HomeScreenActions.RefreshHome`
- [ ] **SellerHomeContent:** Replace all `""` texts with `stringResource()` (AppBar, orders header, create order button, empty state, cancel dialog, order ID, prices, cancel button, status badges)
- [ ] Wire `onCallCourier = {}` → `onCallCourier = { phone -> openPhoneDialer(phone) }`
- [ ] Replace private `StatusBadge` usage with reusable `StatusTag` (or populate status texts properly)
- [ ] Remove unused `floatingActionButton = {}`
- [ ] In `HomeViewModel.kt:70-71`: implement `OrderClicked` navigation (replace `// TODO: navigate to order details` with actual navigation to order detail screen)

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/
git commit -m "fix: restore HomeScreen strings, icons, pull-to-refresh, courier call, order detail nav"
```

### Task 3.3: Fix LogoutDialog — Strings

**Issue #36:** Empty dialog with no text.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/presentation/components/LogoutDialog.kt`

Replace all `""` with `stringResource()` — title=`Res.string.logout`, message=`Res.string.logout_message`, button=`Res.string.ok`.

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/presentation/components/LogoutDialog.kt
git commit -m "fix: restore LogoutDialog strings"
```

### Task 3.4: Fix ProfileContent — Strings, Icons, Display Points + HomeInfo

**Issues #17, #18, #40:** Raw string keys, `Icons.Default`, `points` not displayed, `homeInfo` not consumed.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/profile/profile/presentation/components/ProfileContent.kt`

- [ ] Replace all raw keys with `stringResource()` (profile, wallet, last_orders, logout, yes, no, etc.)
- [ ] Replace `Icons.Default.ExitToApp` → `painterResource(Res.drawable.ic_logout)`
- [ ] Replace `Icons.Default.Person` → `painterResource(Res.drawable.ic_profile_circle)` (use `Image` composable)
- [ ] Add `"${viewState.walletBalance} EGP"` → `stringResource(Res.string.price_currency, ...)`
- [ ] Display `viewState.points` and user email on profile
- [ ] Add `.statusBarsPadding()` to Column

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/profile/profile/
git commit -m "fix: restore ProfileContent strings, icons, points display, statusBarsPadding"
```

### Task 3.5: Fix WalletContent + RedeemPointsBottomSheet — Strings, Icons, Format Date

**Issues #20, #21, #41, #42, #43, #44, #51:** All raw string keys, `"EGP"` hardcoded, `"point"` hardcoded, `formatDate()` is Arabic-only, dead `SimpleDateFormat` imports, `redeemPointsError` never rendered.

**Files:**
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/profile/wallet/presentation/components/WalletContent.kt`
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/profile/wallet/presentation/components/RedeemPointsBottomSheet.kt`

- [ ] Replace ALL raw string keys with `stringResource()` (withdraw, confirm, back, success, ok, the_points, wallet_balance, convert_to_money, etc.)
- [ ] Replace `"${amount} EGP"` with `stringResource(Res.string.price_currency, amount.toString())`
- [ ] Replace `"${amount} point"` with `"${amount} ${stringResource(Res.string.point)}"`
- [ ] Replace `Icons.Default.CheckCircle` → `painterResource(Res.drawable.ic_confirm)`
- [ ] In `formatDate()`: use `kotlinx.datetime` or a platform expect/actual for locale-aware date formatting instead of hardcoded Arabic month names
- [ ] Remove dead imports (`SimpleDateFormat`, `Locale`, `TimeZone`)
- [ ] Display `viewState.redeemPointsError` in `RedeemPointsBottomSheet` error text

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/profile/wallet/
git commit -m "fix: restore WalletContent strings, icons, locale-aware date formatting, error display"
```

### Task 3.6: Fix OrderHistoryContent + OrdersScreen — Strings, Icons, Pull-to-Refresh, Data Display

**Issues #19, #31, #42:** Empty texts, Material icons, pull-to-refresh not wired, missing 4 data fields.

**Files:**
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/profile/orderhistory/presentation/OrdersScreen.kt`
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/profile/orderhistory/presentation/components/OrderHistoryContent.kt`

- [ ] **OrdersScreen:** Wrap in `PullToRefreshBox` using `viewState.isRefreshing`
- [ ] Replace all raw string keys with `stringResource()`
- [ ] Replace `Icons.Default.*` with `painterResource(Res.drawable.xxx)` (ic_location, ic_call, ic_delivery_fees, ic_total_price, ic_delivery_agent)
- [ ] Replace `InfoRow(icon: ImageVector, ...)` to `InfoRow(iconPainter: Painter, ...)`
- [ ] Replace `"${order.deliveryPrice} EGP"` with `stringResource(Res.string.price_currency, ...)`
- [ ] Add display of `order.createdAt`, `order.deliveredAt`, `order.collectionAmount`
- [ ] Add cancel button when `order.canCancelOrder == true`
- [ ] Add `.statusBarsPadding()` to Scaffold

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/profile/orderhistory/
git commit -m "fix: restore OrderHistoryContent strings, icons, pull-to-refresh, data fields"
```

---

## Phase 4: Navigation & App-Level (Tasks 20-23)
### Global UX fixes

### Task 4.1: Fix NavigationAction — Button Labels + Test Tags

**Issues #33, #34:** Three action buttons with `text = ""`, duplicate test tag on WhatsApp.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/base/components/sharedComposable/NavigationAction.kt`

- [ ] Set `text = stringResource(Res.string.directions)`, `Res.string.contact`, `Res.string.whatsapp`
- [ ] Fix duplicate `testTag("contactButton_...")` on WhatsApp → `testTag("whatsappButton_...")`

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/base/components/sharedComposable/NavigationAction.kt
git commit -m "fix: restore NavigationAction button labels and fix duplicate test tag"
```

### Task 4.2: Fix App Bottom Navigation — Strings, Icons, Custom Styling

**Issue #53, #55:** Hardcoded `"Home"`/`"Profile"`, generic Material icons, RTL not handled.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/App.kt`

- [ ] Replace `"Home"` → `stringResource(Res.string.home)`, `"Profile"` → `stringResource(Res.string.profile)`
- [ ] Replace `Icons.Default.Home` → `painterResource(Res.drawable.ic_home)`
- [ ] Replace `Icons.Default.Person` → `painterResource(Res.drawable.ic_profile)`
- [ ] Change `BottomNavItem.icon: ImageVector` → `iconPainter: Painter`
- [ ] Add `.height(64.dp)` to NavigationBar
- [ ] Restore custom selected indicator (Surface + RoundedCornerShape + tinted background)
- [ ] Add `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)` in `App()` composable

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/App.kt
git commit -m "fix: restore bottom navigation strings, icons, styling, and RTL support"
```

### Task 4.3: Restore Screen Transition Animations

**Issue #54:** No animated transitions between screens.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/App.kt`

Wrap `when (route)` in `AppScreenContent` with:
```kotlin
AnimatedContent(
    targetState = route,
    transitionSpec = {
        slideInHorizontally { width -> width } + fadeIn() togetherWith
            slideOutHorizontally { width -> -width } + fadeOut()
    },
    label = "screen_transition"
) { currentRoute -> ... }
```

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/App.kt
git commit -m "feat: restore screen transition animations"
```

### Task 4.4: Fix LaunchedEffect Over-fetching in App.kt

**Issue #14:** `LaunchedEffect(backStack.size)` re-fetches home data on every navigation.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/App.kt:119`

- [ ] Change `LaunchedEffect(backStack.size)` → `LaunchedEffect(route)` with check for `route is AppRoute.Home`

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/App.kt
git commit -m "fix: prevent home data reload on every navigation event"
```

---

## Phase 5: Platform Differences (Tasks 24-26)
### Android/iOS parity fixes

### Task 5.1: Fix iOS Toast — Use Non-Blocking Overlay Instead of Modal

**Issue #8:** `showPlatformToast()` on iOS uses `UIAlertController` (blocking modal dialog), not a toast overlay.

**File:** `shared/src/iosMain/kotlin/com/ovasta/sellers/platform/PlatformActions.kt`

- [ ] Replace `UIAlertController` implementation with a non-blocking overlay approach (e.g., `UIView.animate` to show/hide a label, or use a custom toast view that auto-dismisses)

**Commit:**
```bash
git add shared/src/iosMain/kotlin/com/ovasta/sellers/platform/PlatformActions.kt
git commit -m "fix: iOS toast uses non-blocking overlay instead of modal dialog"
```

### Task 5.2: Fix iOS openWhatsApp — Add Fallback Path

**Issue:** iOS `openWhatsApp` has no fallback if `wa.me` fails.

**File:** `shared/src/iosMain/kotlin/com/ovasta/sellers/platform/PlatformActions.kt`

- [ ] Add `api.whatsapp.com/send?phone=` fallback (matching Android's pattern)
- [ ] Add `showPlatformToast("WhatsApp not installed")` if both fail

**Commit:**
```bash
git add shared/src/iosMain/kotlin/com/ovasta/sellers/platform/PlatformActions.kt
git commit -m "fix: add WhatsApp fallback path on iOS"
```

### Task 5.3: Fix Android SDP/SSP — Use Intuit Library for Screen-Density Scaling

**Issue #9:** `sdp()`/`ssp()` on Android are identity functions, not screen-density-aware.

**File:** `shared/src/androidMain/kotlin/com/ovasta/sellers/platform/PlatformActions.kt:28-34`

Replace with Intuit library resource lookup:
```kotlin
actual fun sdp(value: Int): Dp {
    val context = get<Context>(Context::class.java)
    val resourceId = context.resources.getIdentifier(
        "_${value}sdp", "dimen", context.packageName
    )
    return if (resourceId != 0) context.resources.getDimension(resourceId).dp
    else value.dp
}

actual fun ssp(value: Int): TextUnit {
    val context = get<Context>(Context::class.java)
    val resourceId = context.resources.getIdentifier(
        "_${value}ssp", "dimen", context.packageName
    )
    return if (resourceId != 0) context.resources.getDimension(resourceId).sp
    else value.sp
}
```

**Commit:**
```bash
git add shared/src/androidMain/kotlin/com/ovasta/sellers/platform/PlatformActions.kt
git commit -m "fix: restore SDP/SSP screen-density-aware scaling on Android"
```

---

## Phase 6: Component Cleanup & Dead Code (Tasks 27-29)
### Eliminate inconsistencies and orphaned code

### Task 6.1: Remove Duplicate InfoRow + Fix StatusBadge

**Issues #15, #16:** Private `InfoRow` duplicates reusable `InfoRow.kt`. Private `StatusBadge` should use `StatusTag`.

**Files:**
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/presentation/components/SellerHomeContent.kt`
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/presentation/components/InfoRow.kt`

- [ ] Remove private `InfoRow` function in `SellerHomeContent.kt` (lines 382-391), replace all calls with the reusable `InfoRow` from `InfoRow.kt`
- [ ] Remove private `StatusBadge` function in `SellerHomeContent.kt` (lines 358-379), replace with `StatusTag` from `StatusTag.kt` passing both `statusId` and the proper `statusName`

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/
git commit -m "refactor: replace duplicate InfoRow and StatusBadge with reusable components"
```

### Task 6.2: Wire ToastResourceHandler — Use StringResourceProvider

**Issue #10:** `ToastEventHandler` does naive string key → display text conversion instead of using `StringResourceProvider`.

**File:** `shared/src/commonMain/kotlin/com/ovasta/sellers/base/ext/ToastEventHandler.kt:14-19`

- [ ] Update `ResourceToastEvent` handler to call `StringResourceProvider.getString(event.stringId, ...)` instead of manual key transformation
- [ ] Ensure `StringResourceProvider` is injectable via Koin in the composable scope

**Commit:**
```bash
git add shared/src/commonMain/kotlin/com/ovasta/sellers/base/ext/ToastEventHandler.kt
git commit -m "fix: wire ResourceToastEvent to use StringResourceProvider instead of naive key conversion"
```

### Task 6.3: Remove Dead Code — Unused State, Empty Actions, Dead Files

**Issues #47, #48, #49, #50:** `isLogoutDialogVisible`, `ChangeLogoutDialogStatus`, `ProfileViewModel.ChangeLogoutDialogStatus -> {}`, empty `OrderNotesSection.kt`.

**Files:**
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/presentation/HomeViewState.kt` — remove `isLogoutDialogVisible` (managed locally)
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/presentation/HomeAction.kt` — remove `ChangeLogoutDialogStatus`
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/presentation/HomeViewModel.kt` — remove `ChangeLogoutDialogStatus` handler
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/profile/profile/presentation/ProfileViewModel.kt` — remove empty `ChangeLogoutDialogStatus -> {}`
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/createOrder/presentation/components/OrderNotesSection.kt` — delete empty file
- `shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/presentation/components/LogoutDialog.kt` — remove file (replaced by inline BaseDialog usage in ProfileContent)

**Commit:**
```bash
git rm shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/createOrder/presentation/components/OrderNotesSection.kt
git rm shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/presentation/components/LogoutDialog.kt
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/presentation/HomeViewState.kt
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/presentation/HomeAction.kt
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/home/presentation/HomeViewModel.kt
git add shared/src/commonMain/kotlin/com/ovasta/sellers/presentation/profile/profile/presentation/ProfileViewModel.kt
git commit -m "cleanup: remove dead state, empty actions, and unused files"
```

---

## Phase 7: Final Verification (Task 30)

### Task 7.1: Build and Manual Testing

- [ ] **Full Android build:** `./gradlew clean :app:assembleDebug` → BUILD SUCCESSFUL
- [ ] **iOS build:** `./gradlew :shared:compileKotlinIosArm64` → BUILD SUCCESSFUL

**Manual testing checklist (verify on emulator/device):**
- [ ] Splash shows logo
- [ ] Login shows all strings, password toggle, keyboard doesn't overlap
- [ ] Home shows strings, icons, pull-to-refresh, courier call works
- [ ] Create Order shows labels, delivery timing toggle, date/time fields, confirm dialog has text and icon, submit disables during loading
- [ ] Profile shows proper icons, wallet balance formatted, points displayed
- [ ] Wallet shows all strings, withdraw dialog has icon, tab labels localized
- [ ] Order History shows strings, icons, pull-to-refresh, all data fields (createdAt, deliveredAt, collectionAmount, cancel)
- [ ] Bottom nav shows localized labels, custom styling, selected indicator
- [ ] Back arrow icon everywhere
- [ ] Error dialogs show alert icon, localized text
- [ ] RTL layout direction works
- [ ] Screen transitions animate
- [ ] Almarai font renders
- [ ] System back button navigates back
- [ ] No overlapping dialogs

**Commit:**
```bash
git add -A
git commit -m "chore: KMP UX parity complete"
```
