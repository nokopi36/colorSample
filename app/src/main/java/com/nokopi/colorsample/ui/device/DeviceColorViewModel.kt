package com.nokopi.colorsample.ui.device

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokopi.colorsample.data.SchemeSave
import com.nokopi.colorsample.data.model.Catalog
import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.ColorOption
import com.nokopi.colorsample.data.model.Device
import com.nokopi.colorsample.data.model.DeviceId
import com.nokopi.colorsample.data.model.Palette
import com.nokopi.colorsample.data.model.PartId
import com.nokopi.colorsample.data.model.PartSpec
import com.nokopi.colorsample.data.model.SchemeId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/** 色を選べるパーツ1つ分の、画面に出すのに必要なものが揃った状態。 */
data class PartSelection(
    val part: PartSpec,
    val palette: Palette,
    val selected: ColorOption,
)

sealed interface DeviceColorUiState {

    data object Loading : DeviceColorUiState

    /** 表示中に装具が削除された、または不正な ID で開かれた。 */
    data object NotFound : DeviceColorUiState

    /**
     * @property layers 描画順に並んだ全レイヤー。色を変えないものも含む。
     * @property selections 色を選べるパーツだけ。選択欄に並ぶのはこれ。
     * @property schemeName 保存済み配色を開いている場合のその名前。null なら未保存。
     *   上書き保存の可否と、保存ダイアログの初期値に使う。
     */
    data class Ready(
        val device: Device,
        val personName: String,
        val layers: List<PreviewLayer>,
        val selections: List<PartSelection>,
        val schemeName: String? = null,
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
 *
 * 保存済み配色から開いた場合は、その内容が初期値になる。[SavedStateHandle] の既定値を
 * **null**（＝まだ触っていない）にしてあるのが要で、`"{}"` を既定にすると「配色から来た初期状態」と
 * 「ユーザーが明示的に空にした状態」が区別できない。一度でも操作したら handle 側が勝つ。
 *
 * カタログ全体ではなく読み取り口と保存関数だけを受け取るのは、[Context] を持ち込まず
 * 素の JUnit でテストできる状態を保つため。
 */
class DeviceColorViewModel(
    private val deviceId: DeviceId,
    /** この画面はカタログを読むだけなので、リポジトリではなく読み取り口だけを受け取る。 */
    catalog: Flow<Catalog>,
    private val handle: SavedStateHandle,
    /** 一覧から配色を開いた場合のその ID。null なら新規。 */
    initialSchemeId: SchemeId? = null,
    /** 配色の保存。リポジトリ全体を渡すと [android.content.Context] が付いてくるので関数だけ受け取る。 */
    private val persistScheme: suspend (SchemeSave) -> SchemeId,
) : ViewModel() {

    val uiState: StateFlow<DeviceColorUiState> = combine(
        catalog,
        handle.getStateFlow<String?>(KEY_PERSON_NAME, null),
        handle.getStateFlow<String?>(KEY_SELECTIONS, null),
        handle.getStateFlow(KEY_SCHEME_ID, initialSchemeId?.value),
    ) { catalog, personName, selectionsJson, schemeId ->
        buildState(catalog, personName, selectionsJson, schemeId?.let(::SchemeId))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = DeviceColorUiState.Loading,
    )

    private fun buildState(
        catalog: Catalog,
        personName: String?,
        selectionsJson: String?,
        schemeId: SchemeId?,
    ): DeviceColorUiState {
        val device = catalog.device(deviceId) ?: return DeviceColorUiState.NotFound
        // 参照先が消えていても画面は開けるようにする。上書き先が無くなるだけ。
        val scheme = schemeId?.let(catalog::scheme)

        // 未操作なら保存済み配色の内容を初期値にする。触ったあとは handle が勝つ。
        val selections = selectionsJson?.let(::decode) ?: scheme?.selectionIds ?: emptyMap()

        // パーツごとに1回だけ解決し、描画用と選択欄用の両方に使う。
        // 色を変えないレイヤーは選択欄には出さないが、描画には並び順のまま含める。
        val resolved = device.parts.map { part ->
            val palette = part.paletteId?.let(catalog::palette)
            part to palette?.let {
                PartSelection(
                    part = part,
                    palette = it,
                    // 消された色を参照していたら先頭に落ちる。
                    selected = it.optionOrFirst(selections[part.id.value]?.let(::ColorId)),
                )
            }
        }

        return DeviceColorUiState.Ready(
            device = device,
            personName = personName ?: scheme?.personName.orEmpty(),
            layers = resolved.map { (part, selection) ->
                PreviewLayer(image = part.image, color = selection?.selected?.color)
            },
            selections = resolved.mapNotNull { (_, selection) -> selection },
            schemeName = scheme?.name,
        )
    }

    fun updatePersonName(name: String) {
        handle[KEY_PERSON_NAME] = name
    }

    fun selectColor(partId: PartId, colorId: ColorId) {
        val updated = effectiveSelections() + (partId.value to colorId.value)
        handle[KEY_SELECTIONS] = encode(updated)
    }

    /**
     * 全パーツを初期色に戻す。氏名は誤タップでの入力消失を避けるため残す。
     *
     * 空の Map を明示的に置く。null に戻すと保存済み配色の内容が復活してしまい、
     * 「リセット」にならない。
     */
    fun reset() {
        handle[KEY_SELECTIONS] = EMPTY_SELECTIONS_JSON
    }

    /**
     * 今の配色に名前を付けて保存する。開いているのが保存済み配色なら上書きする。
     *
     * 表示中の状態から作るので、保存済み配色を開いたまま何も触っていない場合でも
     * 見えているとおりの内容が残る。
     */
    fun saveScheme(name: String, overwrite: Boolean) {
        val state = uiState.value as? DeviceColorUiState.Ready ?: return
        viewModelScope.launch {
            val saved = persistScheme(
                SchemeSave(
                    id = if (overwrite) currentSchemeId() else null,
                    deviceId = deviceId,
                    name = name,
                    personName = state.personName,
                    selections = state.selections.associate { it.part.id to it.selected.id },
                ),
            )
            // 新規保存のあとは、そのまま続けて上書きできるようにしておく。
            handle[KEY_SCHEME_ID] = saved.value
        }
    }

    private fun currentSchemeId(): SchemeId? =
        handle.get<String>(KEY_SCHEME_ID)?.let(::SchemeId)

    /**
     * いま画面に出ている選択。
     *
     * 未操作なら handle が空なので、表示中の状態から拾う。保存済み配色を開いて1色だけ変えたとき、
     * handle の中身（空）を土台にすると**残りのパーツが配色の色を失う**。
     */
    private fun effectiveSelections(): Map<String, String> =
        handle.get<String>(KEY_SELECTIONS)?.let(::decode)
            ?: (uiState.value as? DeviceColorUiState.Ready)
                ?.selections
                ?.associate { it.part.id.value to it.selected.id.value }
            ?: emptyMap()

    private companion object {
        const val KEY_PERSON_NAME = "personName"
        const val KEY_SELECTIONS = "selections"
        const val KEY_SCHEME_ID = "schemeId"
        const val EMPTY_SELECTIONS_JSON = "{}"
        const val STOP_TIMEOUT_MILLIS = 5_000L

        val serializer = MapSerializer(String.serializer(), String.serializer())

        // SavedStateHandle は Bundle 相当なので Map をそのまま置けない。JSON 文字列にして持つ。
        fun encode(value: Map<String, String>): String = Json.encodeToString(serializer, value)

        fun decode(value: String): Map<String, String> =
            runCatching { Json.decodeFromString(serializer, value) }.getOrDefault(emptyMap())
    }
}
