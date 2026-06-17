# Plan: Restore Redeem Points Bottom Sheet in KMP WalletScreen

## Goal
Restore the missing `RedeemPointsBottomSheet` component that was deleted during the KMP migration. Users currently click "Convert to Money" but nothing happens visually.

## Background
- **Original file**: `androidApp/src/main/java/com/ovasta/sellers/presentation/profile/wallet/presentation/components/RedeemPointsBottomSheet.kt` (on main branch)
- **Status**: Deleted during KMP migration
- **ViewModel**: Already has complete logic for redeem points flow
- **ViewState**: All required state properties exist
- **String resources**: Already migrated to KMP shared resources

## Current State Analysis

### ✅ What Works
1. **WalletViewModel** (lines 51-68) handles:
   - `ConvertPoints`: Fetches minimum redeem points, validates available points, shows bottom sheet
   - `UpdateRedeemPoints`: Updates slider value as user drags
   - `ConfirmRedeemPoints`: Validates input and calls API
   - `DismissRedeemBottomSheet`: Closes sheet and resets state

2. **WalletViewState** has all required properties:
   - `showRedeemBottomSheet: Boolean` (line 14)
   - `redeemPointsInput: String` (line 15)
   - `minimumRedeemPoints: Double` (line 13)
   - `wallet?.points: Double` (via WalletResponse)

3. **String Resources** exist in KMP:
   - `Res.string.mini_redeem_message` → "أقل عدد نقاط يمكن تحويلها هو %1$s نقطه" / "Minimum redeem points: %1$s point"
   - `Res.string.point` → "نقطه" / "Point"
   - `Res.string.confirm` → "تأكيد" / "Confirm"
   - `Res.string.convert_to_money` → "تحويل لأموال" / "Convert to Money"

### ❌ What's Missing
- The `RedeemPointsBottomSheet` composable UI component
- UI rendering when `viewState.showRedeemBottomSheet == true`

## Implementation Plan

### Step 1: Create RedeemPointsBottomSheet.kt
**File**: `shared/src/commonMain/kotlin/com/ovasta/sellers/ui/screens/wallet/RedeemPointsBottomSheet.kt`

**Component Structure**:
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedeemPointsBottomSheet(
    viewState: WalletViewState,
    onAction: (WalletAction) -> Unit
)
```

**UI Elements** (from original implementation):
1. **ModalBottomSheet** with:
   - `skipPartiallyExpanded = true`
   - Rounded top corners (16.dp)
   - White background
   - Dismiss on back press/outside tap → `DismissRedeemBottomSheet`

2. **Header**:
   - Title: "Convert to Money" (`Res.string.convert_to_money`)
   - Style: `mdSemiBold`, Black
   
3. **Subtitle**:
   - Text: "Minimum redeem points: {min} point" (`Res.string.mini_redeem_message`)
   - Style: `xsMedium`, Gray
   - Uses string formatting: `stringResource(Res.string.mini_redeem_message, min.toString())`

4. **Selected Points Display**:
   - Text: "{currentValue} Point" (e.g., "100 نقطه")
   - Style: `smSemiBold`, Primary color
   - Value from: `viewState.redeemPointsInput.toIntOrNull() ?: 0`

5. **Slider** (Material 3):
   - **Value**: `currentValue.toFloat()`
   - **Range**: `0f..(steps * min).toFloat()`
   - **Steps**: `steps - 1` where `steps = availablePoints / min`
   - **Snap behavior**: `(Math.round(newValue / min) * min).coerceIn(0, steps * min)`
   - **Colors**:
     - Thumb: Primary
     - Active track: Primary
     - Inactive track: Primary @ 20% alpha
     - Active tick: Primary
     - Inactive tick: Primary @ 40% alpha
   - **OnValueChange**: Triggers `WalletAction.UpdateRedeemPoints(snapped.toString())`

6. **Labels Row** (below slider):
   - Shows all step values: `0, min, 2*min, 3*min, ..., steps*min`
   - Layout: `Row` with `Arrangement.SpaceBetween`
   - Style: `xsMedium`, Gray
   - Loop: `for (i in 0..steps) { Text("${i * min}") }`

7. **Confirm Button**:
   - Text: "Confirm" (`Res.string.confirm`)
   - Style: `smMedium`, White text
   - Enabled: `currentValue > 0`
   - Full width, 48.dp height, 12.dp corner radius
   - Primary background (disabled: Primary @ 40% alpha)
   - OnClick: `WalletAction.ConfirmRedeemPoints`

**Calculations** (from original):
```kotlin
val min = viewState.minimumRedeemPoints.toInt()
val availablePoints = (viewState.wallet?.points ?: 0.0).toInt()
val steps = if (min > 0) availablePoints / min else 0
val currentValue = viewState.redeemPointsInput.toIntOrNull() ?: 0
```

**Required Imports**:
```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.confirm
import com.ovasta.sellers.shared.resources.convert_to_money
import com.ovasta.sellers.shared.resources.mini_redeem_message
import com.ovasta.sellers.shared.resources.point
import com.ovasta.sellers.ui.theme.Primary
import com.ovasta.sellers.ui.theme.mdSemiBold
import com.ovasta.sellers.ui.theme.smMedium
import com.ovasta.sellers.ui.theme.smSemiBold
import com.ovasta.sellers.ui.theme.xsMedium
import org.jetbrains.compose.resources.stringResource
```

### Step 2: Update WalletScreen.kt
**File**: `shared/src/commonMain/kotlin/com/ovasta/sellers/ui/screens/wallet/WalletScreen.kt`

**Change**: Add bottom sheet rendering after existing dialogs (after line 131)

**Insert location**: After `WithdrawSuccessDialog` block, before tabs definition

```kotlin
// Redeem Points Bottom Sheet
if (viewState.showRedeemBottomSheet) {
    RedeemPointsBottomSheet(
        viewState = viewState,
        onAction = onAction
    )
}
```

### Step 3: Verification & Testing

**Build verification**:
1. Run: `./gradlew :shared:compileDebugKotlinAndroid`
2. Verify no compilation errors

**Manual testing checklist**:
- [ ] Click "Convert to Money" with sufficient points → Bottom sheet appears
- [ ] Click "Convert to Money" with insufficient points → Toast only, no sheet
- [ ] Slider snaps to multiples of minimum
- [ ] Labels show all step values
- [ ] Selected points display updates in real-time
- [ ] Confirm button disabled when value = 0
- [ ] Confirm triggers API call and success toast
- [ ] Dismiss on outside tap/back/swipe
- [ ] Works in Arabic (RTL) and English (LTR)
- [ ] No issues on edge-to-edge or old devices

## Files Changed Summary

### New Files (1)
- `shared/src/commonMain/kotlin/com/ovasta/sellers/ui/screens/wallet/RedeemPointsBottomSheet.kt` (~140 lines)

### Modified Files (1)
- `shared/src/commonMain/kotlin/com/ovasta/sellers/ui/screens/wallet/WalletScreen.kt` (+6 lines)

---

**Ready to execute?** Please confirm.
