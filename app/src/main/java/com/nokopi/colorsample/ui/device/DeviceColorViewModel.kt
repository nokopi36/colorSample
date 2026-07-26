package com.nokopi.colorsample.ui.device

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokopi.colorsample.data.DeviceType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * 画面の状態。[selectedIndices] は [DeviceType.parts] と同じ並びで、
 * それぞれのパーツが自分のパレットの何番目の色を選んでいるかを持つ。
 */
data class DeviceColorUiState(
    val personName: String = "",
    val selectedIndices: List<Int> = emptyList(),
)

/**
 * 装具1種類分の配色状態。
 *
 * [device] は Navigation 3 のキー ([com.nokopi.colorsample.navigation.DeviceKey]) が
 * 型付きで持っている値をそのまま受け取る。ユーザーが選んだ内容だけが [SavedStateHandle] に
 * 載るので、画面回転でもプロセス kill 後の復帰でも復元される。
 *
 * Context を持たない（ラベルの解決は Composable 側の stringResource に任せている）ため、
 * 素の JUnit でテストできる。
 */
class DeviceColorViewModel(
    val device: DeviceType,
    private val handle: SavedStateHandle,
) : ViewModel() {

    private val initialSelections = IntArray(device.parts.size)

    val uiState: StateFlow<DeviceColorUiState> = combine(
        handle.getStateFlow(KEY_PERSON_NAME, ""),
        handle.getStateFlow(KEY_SELECTIONS, initialSelections),
    ) { personName, selections ->
        DeviceColorUiState(personName = personName, selectedIndices = normalize(selections))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = DeviceColorUiState(
            personName = handle.get<String>(KEY_PERSON_NAME).orEmpty(),
            selectedIndices = currentSelections().toList(),
        ),
    )

    /**
     * 保存されていた選択を画面に出せる形に整える。
     * 装具の定義が変わった後に古い保存状態から復帰しても壊れないよう、
     * 要素数とパレットの範囲をここで揃える。
     */
    private fun normalize(selections: IntArray): List<Int> =
        device.parts.mapIndexed { index, part ->
            selections.getOrElse(index) { 0 }.coerceIn(part.palette.options.indices)
        }

    fun updatePersonName(name: String) {
        handle[KEY_PERSON_NAME] = name
    }

    /** [partIndex] 番目のパーツの色を、そのパレットの [optionIndex] 番目に変える。 */
    fun selectColor(partIndex: Int, optionIndex: Int) {
        val parts = device.parts
        require(partIndex in parts.indices) { "パーツの番号が範囲外です: $partIndex" }
        require(optionIndex in parts[partIndex].palette.options.indices) {
            "色の番号が範囲外です: $optionIndex"
        }
        handle[KEY_SELECTIONS] = currentSelections().also { it[partIndex] = optionIndex }
    }

    /** 全パーツを初期色に戻す。氏名は誤タップでの入力消失を避けるため残す。 */
    fun reset() {
        handle[KEY_SELECTIONS] = IntArray(device.parts.size)
    }

    /** 保存済みの選択を、必ず要素数が合った書き換え可能なコピーとして取り出す。 */
    private fun currentSelections(): IntArray {
        val saved = handle.get<IntArray>(KEY_SELECTIONS)
        return if (saved != null && saved.size == device.parts.size) {
            saved.copyOf()
        } else {
            IntArray(device.parts.size)
        }
    }

    private companion object {
        const val KEY_PERSON_NAME = "personName"
        const val KEY_SELECTIONS = "selections"
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
