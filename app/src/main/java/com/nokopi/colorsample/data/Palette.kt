package com.nokopi.colorsample.data

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.nokopi.colorsample.R

/**
 * 選択できる色ひとつ分。
 *
 * [color] はパーツ画像への tint と、選択リストに出す丸チップの両方に使う。
 * 以前は tint 用の hex 文字列 (`CustomColor`) と見本用の drawable (`res/drawable` の shape XML) が
 * 別々に定義されていて index で対応づけていたため、`beigerose` のように
 * 見本 (#e8d8ca) と実際の tint (#e8d3ca) がずれていた。実際に塗られる側の値に一本化してある。
 */
@Immutable
data class ColorOption(
    @StringRes val labelRes: Int,
    val color: Color,
)

private fun option(@StringRes labelRes: Int, hex: Long) = ColorOption(labelRes, Color(hex))

// 各色の定義。名前は元の CustomColor のものを引き継いでいる。
private val WHITE = option(R.string.color_white, 0xFFFFFFFF)
private val BLACK = option(R.string.color_black, 0xFF000000)
private val RED = option(R.string.color_red, 0xFFFF0000)
private val BLUE = option(R.string.color_blue, 0xFF0000FF)
private val GREEN = option(R.string.color_green, 0xFF008000)
private val YELLOW = option(R.string.color_yellow, 0xFFFFFF00)
private val ORANGE = option(R.string.color_orange, 0xFFFF8C00)
private val PINK = option(R.string.color_pink, 0xFFF5B2B2)
private val LIGHT_SKY_BLUE = option(R.string.color_light_blue, 0xFF87CEFA)
private val LIGHT_GREEN = option(R.string.color_yellow_green, 0xFF7CFC00)
private val MEDIUM_SLATE_BLUE = option(R.string.color_purple, 0xFF7B68EE)
private val DEEP_ROYAL_PURPLE = option(R.string.color_purple, 0xFF47266E)
private val MIDNIGHT_BLUE = option(R.string.color_navy, 0xFF001E43)
private val SEA_GREEN = option(R.string.color_green, 0xFF2E8B57)
private val ZENITH_BLUE = option(R.string.color_blue, 0xFF4496D3)
private val AQUAMARINE = option(R.string.color_mint, 0xFF7FFFD4)
private val BEIGE_ROSE = option(R.string.color_beige, 0xFFE8D3CA)
private val VANILLA = option(R.string.color_beige, 0xFFE8C59C)
private val SANDY_BROWN = option(R.string.color_beige, 0xFFF4A460)
private val HATIMITU = option(R.string.color_beige, 0xFFFDDEA5)
private val YUOU = option(R.string.color_beige, 0xFFF9C89B)
private val KIKUTINASHI = option(R.string.color_yellow, 0xFFFFDB4F)
private val MUMEI = option(R.string.color_pink, 0xFFFFCCCB)
private val ROSE_PINK = option(R.string.color_pink, 0xFFF19CA7)
private val TETUGURO = option(R.string.color_brown, 0xFF281A14)
private val KOGETYA = option(R.string.color_brown, 0xFF6F4B3E)

/**
 * パーツの素材ごとに選べる色の一覧。並び順は移行前の `ChangeColors` の定義順そのまま。
 */
enum class Palette(val options: List<ColorOption>) {
    /** 革ベルト類 */
    LEATHER(
        listOf(
            WHITE, KIKUTINASHI, RED, HATIMITU, MUMEI, DEEP_ROYAL_PURPLE,
            MIDNIGHT_BLUE, BLUE, SEA_GREEN, TETUGURO, BLACK,
        ),
    ),

    /** プレーリーくんのプラスチック */
    PLASTIC(listOf(WHITE, RED, BLUE, GREEN, BLACK)),

    /** ナイトブレース・SLB・Aブレース・ポーゴスティックのプラスチック */
    ORTHOSIS_PLASTIC(
        listOf(WHITE, RED, YUOU, ROSE_PINK, BLUE, LIGHT_SKY_BLUE, GREEN, KOGETYA, BLACK),
    ),

    /** スポンジ */
    SPONGE(
        listOf(WHITE, SANDY_BROWN, BLACK, ORANGE, PINK, ZENITH_BLUE, AQUAMARINE, LIGHT_GREEN),
    ),

    /** プレーリーくんのスポンジ */
    PL_SPONGE(listOf(WHITE, ORANGE, PINK, ZENITH_BLUE, AQUAMARINE, LIGHT_GREEN, BLACK)),

    /** プレーリーくんのバンド */
    BAND(listOf(PINK, BLUE, LIGHT_SKY_BLUE, BLACK)),

    /** 糸 */
    STRING(
        listOf(
            WHITE, YELLOW, ORANGE, RED, BEIGE_ROSE, MUMEI, MEDIUM_SLATE_BLUE,
            MIDNIGHT_BLUE, BLUE, LIGHT_SKY_BLUE, GREEN, LIGHT_GREEN, TETUGURO, BLACK,
        ),
    ),

    /** ボタン */
    BUTTON(
        listOf(WHITE, KIKUTINASHI, RED, VANILLA, MUMEI, BLUE, GREEN, TETUGURO, BLACK),
    ),

    /** フェルト・靴底・カンなど白黒のみのパーツ */
    WHITE_BLACK(listOf(WHITE, BLACK)),
}
