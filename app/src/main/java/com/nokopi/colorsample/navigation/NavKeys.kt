package com.nokopi.colorsample.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * 遷移先。Navigation 3 では文字列ルートと引数ではなく、キーそのものが値を持つ。
 *
 * バックスタックの保存・復元にシリアライズを使うため [Serializable] が要る。
 * 装具は実体ではなく ID で指す。表示中に削除されうるので、画面側でカタログを引き直す。
 */
@Serializable
data object HomeKey : NavKey

@Serializable
data class DeviceKey(val deviceId: String) : NavKey

/**
 * 色の管理。
 *
 * @property focusPaletteId 配色画面の「色を追加」から来た場合に、そのパレットを開いた状態にする。
 */
@Serializable
data class ManageColorsKey(val focusPaletteId: String? = null) : NavKey
