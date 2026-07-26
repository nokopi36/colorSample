package com.nokopi.colorsample.navigation

import androidx.navigation3.runtime.NavKey
import com.nokopi.colorsample.data.DeviceType
import kotlinx.serialization.Serializable

/**
 * 遷移先。Navigation 3 では文字列ルートと引数ではなく、キーそのものが値を持つ。
 *
 * バックスタックの保存・復元にシリアライズを使うため [Serializable] が要る。
 */
@Serializable
data object HomeKey : NavKey

@Serializable
data class DeviceKey(val type: DeviceType) : NavKey
