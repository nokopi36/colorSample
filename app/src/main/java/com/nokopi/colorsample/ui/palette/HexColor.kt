package com.nokopi.colorsample.ui.palette

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.util.Locale

/**
 * 16進表記と [Color] の相互変換。
 *
 * 装具の色は指定書やメーカーの色番号から「この値」と決まって渡ってくることがあるので、
 * スライダーで近づけるのではなく数値をそのまま入れられるようにしてある。
 *
 * 受け付ける形は `#RRGGBB` と `#RGB`（`#` は省略可、大文字小文字は問わない）。
 * アルファは扱わない。tint は SrcIn なので半透明にすると下の層が透けてしまい、
 * 色見本としては意味を持たないため、常に不透明にする。
 */
private val HEX_PATTERN = Regex("^#?([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$")

fun parseHexColor(text: String): Color? {
    val match = HEX_PATTERN.matchEntire(text.trim()) ?: return null
    val digits = match.groupValues[1]
    val rgb = if (digits.length == 3) {
        // #abc は #aabbcc の略記。
        digits.flatMap { listOf(it, it) }.joinToString("")
    } else {
        digits
    }
    return Color(rgb.toLong(radix = 16).toInt() or ALPHA_OPAQUE)
}

fun Color.toHexString(): String =
    String.format(Locale.US, "#%06X", toArgb() and 0xFFFFFF)

private const val ALPHA_OPAQUE = 0xFF000000.toInt()
