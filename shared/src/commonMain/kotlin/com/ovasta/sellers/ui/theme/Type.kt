package com.ovasta.sellers.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.bold_new
import com.ovasta.sellers.shared.resources.medium_new
import com.ovasta.sellers.shared.resources.regular_new
import com.ovasta.sellers.shared.resources.semibold_new
import org.jetbrains.compose.resources.Font

// Font families loaded from composeResources
val MediumFontFamily: FontFamily
    @Composable get() = FontFamily(Font(Res.font.medium_new))

val RegularFontFamily: FontFamily
    @Composable get() = FontFamily(Font(Res.font.regular_new))

val SemiBoldFontFamily: FontFamily
    @Composable get() = FontFamily(Font(Res.font.semibold_new))

val BoldFontFamily: FontFamily
    @Composable get() = FontFamily(Font(Res.font.bold_new))

// Text styles (replacing sdp/ssp with fixed sp values)
val xsRegular: TextStyle
    @Composable get() = TextStyle(
        color = BaseWhite,
        fontFamily = RegularFontFamily,
        fontSize = 13.sp,
    )

val xsMedium: TextStyle
    @Composable get() = TextStyle(
        color = Gray500,
        fontFamily = MediumFontFamily,
        fontSize = 13.sp,
    )

val smNormal: TextStyle
    @Composable get() = TextStyle(
        color = Gray800,
        fontFamily = RegularFontFamily,
        fontSize = 15.sp,
    )

val smMedium: TextStyle
    @Composable get() = TextStyle(
        color = Gray700,
        fontSize = 15.sp,
        fontFamily = MediumFontFamily,
    )

val smSemiBold: TextStyle
    @Composable get() = TextStyle(
        color = Gray800,
        fontFamily = SemiBoldFontFamily,
        fontSize = 15.sp,
    )

val mdRegular: TextStyle
    @Composable get() = TextStyle(
        color = Gray500,
        fontSize = 18.sp,
        fontFamily = RegularFontFamily,
    )

val mdMedium: TextStyle
    @Composable get() = TextStyle(
        color = Gray600,
        fontFamily = MediumFontFamily,
        fontSize = 18.sp,
    )

val mdSemiBold: TextStyle
    @Composable get() = TextStyle(
        color = Gray800,
        fontFamily = SemiBoldFontFamily,
        fontSize = 18.sp,
    )

val lgMedium: TextStyle
    @Composable get() = TextStyle(
        color = Gray900,
        fontFamily = MediumFontFamily,
        fontSize = 20.sp,
    )

val lgSemiBold: TextStyle
    @Composable get() = TextStyle(
        color = Gray800,
        fontFamily = SemiBoldFontFamily,
        fontSize = 20.sp,
    )

val xlSemiBold: TextStyle
    @Composable get() = TextStyle(
        color = BLACK,
        fontSize = 22.sp,
        fontFamily = SemiBoldFontFamily,
    )
