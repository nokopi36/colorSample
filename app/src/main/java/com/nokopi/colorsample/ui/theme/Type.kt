package com.nokopi.colorsample.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 既定の Material3 タイポグラフィをベースに、パーツ名とボタンだけ
 * 移行前の XML（20sp / 18sp）に近い大きさへ寄せている。
 */
val ColorSampleTypography = Typography().let { base ->
    base.copy(
        titleMedium = base.titleMedium.copy(fontSize = 18.sp),
        bodyLarge = base.bodyLarge.copy(fontSize = 17.sp),
        labelLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            letterSpacing = 0.1.sp,
        ),
    )
}
