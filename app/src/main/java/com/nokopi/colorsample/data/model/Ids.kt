package com.nokopi.colorsample.data.model

import kotlinx.serialization.Serializable

/**
 * カタログ内の識別子。
 *
 * 色や装具をユーザーが増やせるようになると、「パレットの何番目」といった位置での参照は
 * 使えなくなる（色を1つ挿しただけで既存の配色が全部ずれる）。参照はすべてこの ID で行う。
 *
 * 組み込みの定義は `builtin:` 、ユーザーが作ったものは `user:` で始める。
 * この接頭辞は編集・削除の可否の判定にも使う。
 */
private const val BUILT_IN_PREFIX = "builtin:"
private const val USER_PREFIX = "user:"

internal fun builtInId(name: String) = BUILT_IN_PREFIX + name
internal fun userId(uuid: String) = USER_PREFIX + uuid

@Serializable
@JvmInline
value class ColorId(val value: String) {
    val isBuiltIn: Boolean get() = value.startsWith(BUILT_IN_PREFIX)
}

/**
 * 色グループの識別子。
 *
 * 組み込みのグループは `leather` のように接頭辞を持たない。すでに出回っている保存データ
 * （装具のパーツ）がこの ID を参照しているため変えられないので、判定は
 * 「ユーザー定義かどうか」の向きで行う。色や装具の [ColorId.isBuiltIn] とは逆になる。
 */
@Serializable
@JvmInline
value class PaletteId(val value: String) {
    val isUserDefined: Boolean get() = value.startsWith(USER_PREFIX)
}

@Serializable
@JvmInline
value class PartId(val value: String)

@Serializable
@JvmInline
value class DeviceId(val value: String) {
    val isBuiltIn: Boolean get() = value.startsWith(BUILT_IN_PREFIX)
}

/**
 * 保存した配色の識別子。
 *
 * 配色はユーザーが作るものしかないので、接頭辞での組み込み判定は要らない。
 */
@Serializable
@JvmInline
value class SchemeId(val value: String)
