package com.nokopi.colorsample.data.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * カタログの実行時モデル。組み込み定義とユーザー定義をマージしたあとの形で、
 * 画面が直接受け取るのはこれ。永続化の形は `data/store` を参照。
 */

/** 選べる色ひとつ分。 */
@Immutable
data class ColorOption(
    val id: ColorId,
    val label: DisplayText,
    val color: Color,
) {
    val isBuiltIn: Boolean get() = id.isBuiltIn
}

/** パーツの素材ごとの色の一覧。組み込みの色にユーザーの色を足したもの。 */
@Immutable
data class Palette(
    val id: PaletteId,
    val label: DisplayText,
    val options: List<ColorOption>,
) {
    fun optionOrFirst(colorId: ColorId?): ColorOption =
        options.firstOrNull { it.id == colorId } ?: options.first()
}

/** 色を変えられるパーツ1つ分。[image] を [paletteId] のいずれかの色で tint して重ねる。 */
@Immutable
data class PartSpec(
    val id: PartId,
    val label: DisplayText,
    val image: PartImage,
    val paletteId: PaletteId,
)

/**
 * 装具1種類。
 *
 * [parts] の並びは描画順（先頭が最背面）で、色選択の表示順も兼ねる。
 * [overlay] は tint しない最前面のレイヤー（線画）。無くてもよい。
 */
@Immutable
data class Device(
    val id: DeviceId,
    val label: DisplayText,
    val thumbnail: PartImage,
    val parts: List<PartSpec>,
    val overlay: PartImage?,
) {
    val isBuiltIn: Boolean get() = id.isBuiltIn
}

/** 画面に渡すカタログ全体。 */
@Immutable
data class Catalog(
    val palettes: List<Palette>,
    val devices: List<Device>,
) {
    private val paletteById = palettes.associateBy { it.id }

    fun palette(id: PaletteId): Palette = requireNotNull(paletteById[id]) {
        "未知のパレットです: ${id.value}"
    }

    fun device(id: DeviceId): Device? = devices.firstOrNull { it.id == id }
}
