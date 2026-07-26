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

/**
 * パーツの素材ごとの色の一覧。組み込みの色にユーザーの色を足し、非表示にしたものを除いたもの。
 *
 * **必ず1色以上ある。** 空を許すと [optionOrFirst] が落ちるため、
 * 作成時（最初の色を必須にする）・削除時（最後の1色は消せない）・
 * 読み込み時（空になる非表示指定は無視する）の3か所で守っている。
 */
@Immutable
data class Palette(
    val id: PaletteId,
    val label: DisplayText,
    val options: List<ColorOption>,
    /**
     * このグループで一覧から外した色。
     *
     * 保存側の指定件数ではなく**実際に効いた**ぶんが入る。全部消えてしまう指定は
     * マージ側で無視されるので、そちらを数えると「戻す」の表示が実態とずれる。
     */
    val hiddenOptions: List<ColorOption> = emptyList(),
) {
    init {
        require(options.isNotEmpty()) { "色が0件のグループは作れません: ${id.value}" }
    }

    /** ユーザーが作ったグループだけ削除・名前変更ができる。 */
    val isUserDefined: Boolean get() = id.isUserDefined

    fun optionOrFirst(colorId: ColorId?): ColorOption =
        options.firstOrNull { it.id == colorId } ?: options.first()
}

/**
 * レイヤー1枚分。
 *
 * @property paletteId 選べる色。**null なら色を変えないレイヤー**（線画や固定色の金具など）。
 *   以前は「最前面の線画」を装具に1枚だけ持てる特別枠にしていたが、それだと色を変えない層を
 *   複数持てず、tint する層のあいだに挟むこともできなかった。レイヤー側の属性にすることで
 *   [Device.parts] の並びのとおりに描けるようになる。
 */
@Immutable
data class PartSpec(
    val id: PartId,
    val label: DisplayText,
    val image: PartImage,
    val paletteId: PaletteId?,
) {
    val isTinted: Boolean get() = paletteId != null
}

/**
 * 装具1種類。
 *
 * [parts] の並びが描画順（先頭が最背面）。色を変えないレイヤーもこの並びに含まれ、
 * 位置どおりに描かれる。
 */
@Immutable
data class Device(
    val id: DeviceId,
    val label: DisplayText,
    val thumbnail: PartImage,
    val parts: List<PartSpec>,
) {
    val isBuiltIn: Boolean get() = id.isBuiltIn

    /** 色を選べるパーツだけ。配色画面の選択欄に並ぶのはこれ。 */
    val tintedParts: List<PartSpec> get() = parts.filter { it.isTinted }
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

    /**
     * そのグループを使っている装具とパーツ。
     *
     * グループを削除してよいかの判定と、拒否したときに「どれが使っているか」を
     * 伝えるために使う。空なら誰も使っていない。
     */
    fun usages(paletteId: PaletteId): List<PaletteUsage> = devices.flatMap { device ->
        device.parts.filter { it.paletteId == paletteId }
            .map { PaletteUsage(device = device, part = it) }
    }
}

/** [Catalog.usages] の結果。 */
@Immutable
data class PaletteUsage(val device: Device, val part: PartSpec)
