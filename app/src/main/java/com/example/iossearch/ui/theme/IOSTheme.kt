package com.example.iossearch.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Design tokens approximating Apple's Human Interface Guidelines system colors
 * and San Francisco type scale. This is an original re-implementation for
 * Android (Roboto/system default font) — it evokes the iOS visual language,
 * it does not embed Apple's SF fonts or any copyrighted asset.
 */
object IOSColors {
    // Light palette
    val SystemBackgroundLight = Color(0xFFFFFFFF)
    val SecondaryBackgroundLight = Color(0xFFF2F2F7)
    val TertiaryBackgroundLight = Color(0xFFE5E5EA)
    val LabelLight = Color(0xFF000000)
    val SecondaryLabelLight = Color(0xFF3C3C43).copy(alpha = 0.6f)
    val TertiaryLabelLight = Color(0xFF3C3C43).copy(alpha = 0.3f)
    val SeparatorLight = Color(0xFF3C3C43).copy(alpha = 0.29f)
    val SearchFieldLight = Color(0xFFE9E9EB)

    // Dark palette
    val SystemBackgroundDark = Color(0xFF000000)
    val SecondaryBackgroundDark = Color(0xFF1C1C1E)
    val TertiaryBackgroundDark = Color(0xFF2C2C2E)
    val LabelDark = Color(0xFFFFFFFF)
    val SecondaryLabelDark = Color(0xFFEBEBF5).copy(alpha = 0.6f)
    val TertiaryLabelDark = Color(0xFFEBEBF5).copy(alpha = 0.3f)
    val SeparatorDark = Color(0xFF545458).copy(alpha = 0.65f)
    val SearchFieldDark = Color(0xFF1C1C1E)

    // Accent — iOS system blue
    val SystemBlue = Color(0xFF007AFF)
    val SystemBlueDark = Color(0xFF0A84FF)
    val SystemRed = Color(0xFFFF3B30)
    val SystemGray = Color(0xFF8E8E93)
}

data class IOSPalette(
    val background: Color,
    val secondaryBackground: Color,
    val tertiaryBackground: Color,
    val label: Color,
    val secondaryLabel: Color,
    val tertiaryLabel: Color,
    val separator: Color,
    val searchField: Color,
    val accent: Color,
    val destructive: Color,
    val gray: Color
)

@Composable
fun rememberIOSPalette(darkTheme: Boolean = isSystemInDarkTheme()): IOSPalette {
    return if (darkTheme) {
        IOSPalette(
            background = IOSColors.SystemBackgroundDark,
            secondaryBackground = IOSColors.SecondaryBackgroundDark,
            tertiaryBackground = IOSColors.TertiaryBackgroundDark,
            label = IOSColors.LabelDark,
            secondaryLabel = IOSColors.SecondaryLabelDark,
            tertiaryLabel = IOSColors.TertiaryLabelDark,
            separator = IOSColors.SeparatorDark,
            searchField = IOSColors.SearchFieldDark,
            accent = IOSColors.SystemBlueDark,
            destructive = IOSColors.SystemRed,
            gray = IOSColors.SystemGray
        )
    } else {
        IOSPalette(
            background = IOSColors.SystemBackgroundLight,
            secondaryBackground = IOSColors.SecondaryBackgroundLight,
            tertiaryBackground = IOSColors.TertiaryBackgroundLight,
            label = IOSColors.LabelLight,
            secondaryLabel = IOSColors.SecondaryLabelLight,
            tertiaryLabel = IOSColors.TertiaryLabelLight,
            separator = IOSColors.SeparatorLight,
            searchField = IOSColors.SearchFieldLight,
            accent = IOSColors.SystemBlue,
            destructive = IOSColors.SystemRed,
            gray = IOSColors.SystemGray
        )
    }
}

// iOS-like type scale (San Francisco proportions), rendered in the platform's
// default sans-serif since SF Pro is Apple-licensed and not redistributable.
object IOSType {
    val LargeTitle = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default)
    val Title3 = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Default)
    val Headline = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Default)
    val Body = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.Default)
    val Callout = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.Default)
    val Subhead = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.Default)
    val Footnote = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.Default)
    val Caption = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal, fontFamily = FontFamily.Default)
}

@Composable
fun IOSSearchTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}
