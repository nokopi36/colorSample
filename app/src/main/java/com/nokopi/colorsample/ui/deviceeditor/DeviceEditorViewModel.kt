package com.nokopi.colorsample.ui.deviceeditor

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokopi.colorsample.data.CatalogRepository
import com.nokopi.colorsample.data.DeviceFiles
import com.nokopi.colorsample.data.DeviceSave
import com.nokopi.colorsample.data.ImageImport
import com.nokopi.colorsample.data.ImageImporter
import com.nokopi.colorsample.data.ImportRejected
import com.nokopi.colorsample.data.PartSave
import com.nokopi.colorsample.data.model.DeviceId
import com.nokopi.colorsample.data.model.DisplayText
import com.nokopi.colorsample.data.model.Palette
import com.nokopi.colorsample.data.model.PaletteId
import com.nokopi.colorsample.data.model.PartId
import com.nokopi.colorsample.data.model.PartImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 編集中のレイヤー1枚。
 *
 * @property paletteId 選べる色。**null なら色を変えないレイヤー**。
 *   何枚でも持てるし、色を変える層のあいだに挟んでもよい。並び順のとおりに描かれる。
 */
data class DraftLayer(
    val partId: PartId,
    val name: String,
    val fileName: String,
    val paletteId: PaletteId?,
) {
    val isTinted: Boolean get() = paletteId != null

    /** staging に置いてあるあいだの表示用。 */
    val image: PartImage get() = PartImage.Stored(DeviceFiles.stagingRelativePath(fileName))
}

/**
 * レイヤー一覧の操作。
 *
 * ViewModel から切り離した純粋な関数にしてあるのは、並べ替えが崩れても画面を見ただけでは
 * 気づきにくいため。
 */
internal object DraftLayers {

    fun withName(layers: List<DraftLayer>, partId: PartId, name: String): List<DraftLayer> =
        layers.map { if (it.partId == partId) it.copy(name = name) else it }

    fun withPalette(
        layers: List<DraftLayer>,
        partId: PartId,
        paletteId: PaletteId,
    ): List<DraftLayer> = layers.map {
        if (it.partId == partId) it.copy(paletteId = paletteId) else it
    }

    /**
     * 色を変える／変えないを入れ替える。何枚でも「変えない」にできる。
     *
     * 「変えたあとの値」を受け取る形にしていないのは、呼び出し側で真偽値を反転し忘れても
     * 型では気づけないため（実際にそれでチップが無反応になった）。ここで反転を完結させる。
     *
     * @param defaultPaletteId 色を変える側に戻すときに割り当てるパレット。
     */
    fun toggleTinting(
        layers: List<DraftLayer>,
        partId: PartId,
        defaultPaletteId: PaletteId,
    ): List<DraftLayer> = layers.map {
        if (it.partId != partId) {
            it
        } else {
            it.copy(paletteId = if (it.isTinted) null else defaultPaletteId)
        }
    }

    /** [offset] だけ前後に動かす。端を越える指定は何もしない。 */
    fun moved(layers: List<DraftLayer>, partId: PartId, offset: Int): List<DraftLayer> {
        val index = layers.indexOfFirst { it.partId == partId }
        val target = index + offset
        if (index < 0 || target !in layers.indices) return layers
        return layers.toMutableList().apply { add(target, removeAt(index)) }
    }

    fun without(layers: List<DraftLayer>, partId: PartId): List<DraftLayer> =
        layers.filterNot { it.partId == partId }
}

data class DeviceEditorUiState(
    val isLoading: Boolean = true,
    val isNew: Boolean = true,
    val name: String = "",
    val layers: List<DraftLayer> = emptyList(),
    val palettes: List<Palette> = emptyList(),
    val isImporting: Boolean = false,
    val isSaving: Boolean = false,
) {
    val tintedLayers: List<DraftLayer> get() = layers.filter { it.isTinted }

    /**
     * 保存できるか。装具に名前があり、色を変えられるレイヤーが1枚以上あり、
     * そのすべてに名前が付いていること。色を変えないレイヤーは名前を持たないので対象外。
     */
    val canSave: Boolean
        get() = !isLoading && !isImporting && !isSaving &&
            name.isNotBlank() &&
            tintedLayers.isNotEmpty() &&
            tintedLayers.all { it.name.isNotBlank() }
}

/** 画面に一度だけ出す知らせ。 */
sealed interface EditorEvent {
    data class Rejected(val rejection: ImageImport.Rejection) : EditorEvent
    data object Saved : EditorEvent
    data object SaveFailed : EditorEvent
}

/**
 * 装具エディタ。
 *
 * 取り込んだ画像は staging に置き、保存で装具のディレクトリへ移す。既存の装具を編集する
 * ときも最初に画像を staging へ写すので、途中でやめれば元の装具は無傷のまま残る。
 */
class DeviceEditorViewModel(
    private val deviceId: DeviceId?,
    private val repository: CatalogRepository,
    private val importer: ImageImporter,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceEditorUiState())
    val uiState: StateFlow<DeviceEditorUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<EditorEvent?>(null)
    val events: StateFlow<EditorEvent?> = _events.asStateFlow()

    /** 2枚目以降の寸法を突き合わせる基準。1枚目を取り込んだ時点で決まる。 */
    private var referenceSize: ImageImport.Size? = null

    init {
        viewModelScope.launch { load() }
    }

    private suspend fun load() {
        // 前回の編集の残りをここで捨てる。
        repository.clearStaging()

        val catalog = repository.catalog.first()
        val existing = deviceId?.let { catalog.device(it) }

        if (existing == null) {
            _uiState.value = DeviceEditorUiState(
                isLoading = false,
                isNew = true,
                palettes = catalog.palettes,
            )
            return
        }

        repository.copyImagesToStaging(existing.id)

        val layers = existing.parts.map { part ->
            DraftLayer(
                partId = part.id,
                name = (part.label as? DisplayText.Literal)?.value.orEmpty(),
                fileName = (part.image as PartImage.Stored).relativePath.substringAfterLast('/'),
                paletteId = part.paletteId,
            )
        }

        _uiState.value = DeviceEditorUiState(
            isLoading = false,
            isNew = false,
            name = (existing.label as? DisplayText.Literal)?.value.orEmpty(),
            layers = layers,
            palettes = catalog.palettes,
        )
        referenceSize = null
    }

    fun updateName(value: String) = _uiState.update { it.copy(name = value) }

    fun updateLayerName(partId: PartId, value: String) = _uiState.update {
        it.copy(layers = DraftLayers.withName(it.layers, partId, value))
    }

    fun updateLayerPalette(partId: PartId, paletteId: PaletteId) = _uiState.update {
        it.copy(layers = DraftLayers.withPalette(it.layers, partId, paletteId))
    }

    fun toggleTinted(partId: PartId) = _uiState.update { state ->
        state.copy(
            layers = DraftLayers.toggleTinting(
                layers = state.layers,
                partId = partId,
                defaultPaletteId = state.palettes.first().id,
            ),
        )
    }

    fun moveLayer(partId: PartId, offset: Int) = _uiState.update {
        it.copy(layers = DraftLayers.moved(it.layers, partId, offset))
    }

    fun removeLayer(partId: PartId) = _uiState.update { state ->
        val layers = DraftLayers.without(state.layers, partId)
        // 全部消したら寸法の基準もやり直す。
        if (layers.isEmpty()) referenceSize = null
        state.copy(layers = layers)
    }

    fun importImages(uris: List<Uri>) {
        if (uris.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true) }
            for (uri in uris) {
                val partId = repository.newPartId()
                val result = importer.import(
                    uri = uri,
                    reference = referenceSize,
                    targetDirectory = repository.stagingDirectory(),
                    fileName = "${DeviceFiles.safeName(partId.value)}.png",
                )
                val imported = result.getOrNull()
                if (imported == null) {
                    val cause = result.exceptionOrNull()
                    _events.value = EditorEvent.Rejected(
                        (cause as? ImportRejected)?.rejection ?: ImageImport.Rejection.Unreadable,
                    )
                    // 1枚でも条件を外れたら以降は止める。混ざったまま進めても直せない。
                    break
                }
                referenceSize = referenceSize ?: imported.size
                _uiState.update { state ->
                    state.copy(
                        layers = state.layers + DraftLayer(
                            partId = partId,
                            name = "",
                            fileName = imported.fileName,
                            paletteId = state.palettes.first().id,
                        ),
                    )
                }
            }
            _uiState.update { it.copy(isImporting = false) }
        }
    }

    fun save() {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = runCatching {
                repository.saveDevice(
                    DeviceSave(
                        id = deviceId ?: repository.newDeviceId(),
                        name = state.name,
                        // 色を変えないレイヤーも並び順のまま含める。
                        parts = state.layers.map {
                            PartSave(
                                id = it.partId,
                                name = it.name,
                                fileName = it.fileName,
                                paletteId = it.paletteId,
                            )
                        },
                    ),
                )
            }
            _uiState.update { it.copy(isSaving = false) }
            _events.value = if (result.isSuccess) EditorEvent.Saved else EditorEvent.SaveFailed
        }
    }

    /** 保存せずに閉じるとき。取り込んだ画像を捨てる。 */
    fun discard() {
        viewModelScope.launch { repository.clearStaging() }
    }

    fun consumeEvent() {
        _events.value = null
    }
}
