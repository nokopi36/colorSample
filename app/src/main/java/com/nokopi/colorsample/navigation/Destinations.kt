package com.nokopi.colorsample.navigation

import com.nokopi.colorsample.data.DeviceType

/** 装具の種類を渡すルート引数の名前。Navigation がこの名前で SavedStateHandle に入れる。 */
const val DEVICE_TYPE_ARG = "type"

object Destinations {
    const val HOME = "home"
    const val DEVICE = "device/{$DEVICE_TYPE_ARG}"

    fun device(type: DeviceType): String = "device/${type.name}"
}
