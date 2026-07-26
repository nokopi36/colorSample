package com.nokopi.colorsample.data.store

import kotlinx.serialization.Serializable

/**
 * ユーザーが追加したぶんだけを保存する形。
 *
 * 実行時モデル (`data/model`) と分けてあるのは、あちらがリソースID
 * (`DisplayText.Res` / `PartImage.Bundled`) を持つため。リソースIDはビルドごとに
 * 変わりうるので保存してはいけない。こちら側は文字列と数値だけで完結させる。
 *
 * @property version 保存形式のバージョン。読み込み時の移行判断に使う。
 */
@Serializable
data class StoredCatalog(
    val version: Int = CURRENT_VERSION,
    val colors: List<StoredColor> = emptyList(),
    val devices: List<StoredDevice> = emptyList(),
    /** ユーザーが作った色グループ。ここに載っているものだけ削除できる。 */
    val palettes: List<StoredPalette> = emptyList(),
    /** 組み込みグループの改名。パレットID -> 付け直した名前。 */
    val paletteNames: Map<String, String> = emptyMap(),
    /** 一覧から外した色。組み込みの色を「消す」のはこれで表す。 */
    val hiddenColors: List<HiddenColor> = emptyList(),
) {
    companion object {
        const val CURRENT_VERSION = 1
        val EMPTY = StoredCatalog()
    }
}

/** ユーザーが作った色グループ。 */
@Serializable
data class StoredPalette(val id: String, val name: String)

/**
 * 一覧から外した色。
 *
 * 組み込みの色はコード上の定義を消せないので、こうして「見せない」ことで削除を表す。
 * 定義自体は残るのでいつでも戻せるし、アプリを更新しても壊れない。
 *
 * **グループ単位**である点が要。組み込みの色は複数グループで共有されており
 * （黒は9グループ、白は8グループ）、色だけを指定して消すと他のグループからも消えてしまう。
 */
@Serializable
data class HiddenColor(val paletteId: String, val colorId: String)

/**
 * ユーザーが追加した色。
 *
 * @property paletteId どの素材のパレットに足すか。組み込みのパレットIDを指す。
 * @property argb 不透明前提の ARGB。
 */
@Serializable
data class StoredColor(
    val id: String,
    val paletteId: String,
    val name: String,
    val argb: Int,
)

/**
 * ユーザーが作った装具。画像そのものは `filesDir/devices/<id>/` に置き、ここには名前だけ持つ。
 *
 * @property parts 描画順（先頭が最背面）。色を変えないレイヤーもここに含まれる。
 */
@Serializable
data class StoredDevice(
    val id: String,
    val name: String,
    val parts: List<StoredPart> = emptyList(),
)

/**
 * @property paletteId 選べる色。null なら色を変えないレイヤー。
 */
@Serializable
data class StoredPart(
    val id: String,
    val name: String,
    val fileName: String,
    val paletteId: String? = null,
)
