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
) {
    companion object {
        const val CURRENT_VERSION = 1
        val EMPTY = StoredCatalog()
    }
}

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
 * Phase 2 で使う。Phase 1 の時点では常に空。
 */
@Serializable
data class StoredDevice(
    val id: String,
    val name: String,
    val parts: List<StoredPart> = emptyList(),
    /** tint しない最前面レイヤーのファイル名。無くてもよい。 */
    val overlayFileName: String? = null,
)

@Serializable
data class StoredPart(
    val id: String,
    val name: String,
    val fileName: String,
    val paletteId: String,
)
