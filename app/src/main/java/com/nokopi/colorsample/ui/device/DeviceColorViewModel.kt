package com.nokopi.colorsample.ui.device

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokopi.colorsample.data.model.Catalog
import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.ColorOption
import com.nokopi.colorsample.data.model.Device
import com.nokopi.colorsample.data.model.DeviceId
import com.nokopi.colorsample.data.model.Palette
import com.nokopi.colorsample.data.model.PartId
import com.nokopi.colorsample.data.model.PartSpec
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/** パーツ1つ分の、画面に出すのに必要なものが揃った状態。 */
data class PartSelection(
    val part: PartSpec,
    val palette: Palette,
    val selected: ColorOption,
)

sealed interface DeviceColorUiState {

    data object Loading : DeviceColorUiState

    /** 表示中に装具が削除された、または不正な ID で開かれた。 */
    data object NotFound : DeviceColorUiState

    data class Ready(
        val device: Device,
        val personName: String,
        val parts: List<PartSelection>,
    ) : DeviceColorUiState
}

/**
 * 装具1種類分の配色状態。
 *
 * 選択は「パレットの何番目か」ではなく [PartId] → [ColorId] の対応で持つ。
 * index で持つと、ユーザーが色を1つ追加しただけで既存の選択がずれてしまう。
 *
 * 選んだ内容は [SavedStateHandle] に載るので、画面回転でもプロセス kill 後の復帰でも残る。
 * 参照先の色が消えている場合（ユーザーが色を削除した）は、そのパレットの先頭に落とす。
 */
class DeviceColorViewModel(
    private val deviceId: DeviceId,
    /** この画面はカタログを読むだけなので、リポジトリではなく読み取り口だけを受け取る。 */
    catalog: Flow<Catalog>,
    private val handle: SavedStateHandle,
) : ViewModel() {

    val uiState: StateFlow<DeviceColorUiState> = combine(
        catalog,
        handle.getStateFlow(KEY_PERSON_NAME, ""),
        handle.getStateFlow(KEY_SELECTIONS, EMPTY_SELECTIONS_JSON),
    ) { catalog, personName, selectionsJson ->
        buildState(catalog, personName, decode(selectionsJson))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = DeviceColorUiState.Loading,
    )

    private fun buildState(
        catalog: Catalog,
        personName: String,
        selections: Map<String, String>,
    ): DeviceColorUiState {
        val device = catalog.device(deviceId) ?: return DeviceColorUiState.NotFound
        return DeviceColorUiState.Ready(
            device = device,
            personName = personName,
            parts = device.parts.map { part ->
                val palette = catalog.palette(part.paletteId)
                PartSelection(
                    part = part,
                    palette = palette,
                    // 消された色を参照していたら先頭に落ちる。
                    selected = palette.optionOrFirst(selections[part.id.value]?.let(::ColorId)),
                )
            },
        )
    }

    fun updatePersonName(name: String) {
        handle[KEY_PERSON_NAME] = name
    }

    fun selectColor(partId: PartId, colorId: ColorId) {
        val updated = currentSelections() + (partId.value to colorId.value)
        handle[KEY_SELECTIONS] = encode(updated)
    }

    /** 全パーツを初期色に戻す。氏名は誤タップでの入力消失を避けるため残す。 */
    fun reset() {
        handle[KEY_SELECTIONS] = EMPTY_SELECTIONS_JSON
    }

    private fun currentSelections(): Map<String, String> =
        decode(handle.get<String>(KEY_SELECTIONS) ?: EMPTY_SELECTIONS_JSON)

    private companion object {
        const val KEY_PERSON_NAME = "personName"
        const val KEY_SELECTIONS = "selections"
        const val EMPTY_SELECTIONS_JSON = "{}"
        const val STOP_TIMEOUT_MILLIS = 5_000L

        val serializer = MapSerializer(String.serializer(), String.serializer())

        // SavedStateHandle は Bundle 相当なので Map をそのまま置けない。JSON 文字列にして持つ。
        fun encode(value: Map<String, String>): String = Json.encodeToString(serializer, value)

        fun decode(value: String): Map<String, String> =
            runCatching { Json.decodeFromString(serializer, value) }.getOrDefault(emptyMap())
    }
}
