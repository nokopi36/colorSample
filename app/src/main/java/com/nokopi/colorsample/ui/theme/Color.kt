package com.nokopi.colorsample.ui.theme

import androidx.compose.ui.graphics.Color

// 旧 colors.xml の buff 系をブランドカラーとして引き継いだもの。
internal val Buff100 = Color(0xFFDFCDA8)
internal val Buff200 = Color(0xFFCAAC71)
internal val Buff400 = Color(0xFFAD7900)

internal val BuffLight = Color(0xFFE8D2A6)
internal val BuffDarkPrimary = Color(0xFFEBC77E)

internal val OnBuffDark = Color(0xFF3D2A00)
internal val Brown900 = Color(0xFF241A08)
internal val Brown700 = Color(0xFF5A4520)

internal val NeutralLightSurface = Color(0xFFFFFBF3)
internal val NeutralLightSurfaceVariant = Color(0xFFEFE3D0)
internal val NeutralDarkSurface = Color(0xFF17130C)
internal val NeutralDarkSurfaceVariant = Color(0xFF4C4539)
internal val NeutralDarkOnSurface = Color(0xFFEAE1D4)
internal val NeutralDarkOnSurfaceVariant = Color(0xFFD0C4B4)

internal val OutlineLight = Color(0xFF837567)
internal val OutlineDark = Color(0xFF9E9081)

/**
 * 配色プレビューの下地。
 *
 * このアプリは装具の色を確かめるためのものなので、プレビューの背景だけは
 * ダークテーマでも変えず、常に同じ明るさで色を見比べられるようにしている。
 */
val PreviewCanvas = Color(0xFFFFFFFF)
val PreviewCanvasContent = Color(0xFF1B1B1B)
val PreviewCanvasOutline = Color(0xFFD6CCBE)
