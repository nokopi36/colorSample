package com.nokopi.colorsample.ui.palette

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokopi.colorsample.data.CatalogRepository
import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.Palette
import com.nokopi.colorsample.data.model.PaletteId
import com.nokopi.colorsample.data.model.PaletteUsage
import com.nokopi.colorsample.data.model.SavedScheme
import com.nokopi.colorsample.data.store.StoredColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** 直前の操作を取り消せるように控えておくもの。 */
sealed interface Undoable {
    val name: String

    /**
     * ユーザーの色を削除した。
     *
     * @property affectedSchemes この削除で色が変わった保存済み配色の件数。0 なら案内に出さない。
     */
    data class ColorDeleted(
        val stored: StoredColor,
        override val name: String,
        val affectedSchemes: Int = 0,
    ) : Undoable

    /** 色を一覧から外した。 */
    data class ColorHidden(
        val paletteId: PaletteId,
        val colorId: ColorId,
        override val name: String,
    ) : Undoable
}

/** グループを削除できなかったときに出す内容。 */
data class PaletteInUse(val palette: Palette, val usages: List<PaletteUsage>)

/**
 * 削除しようとした色を、保存した配色が使っている。
 *
 * グループ削除と違って**拒否はしない**。色は正当に引退させるものなので、
 * 何が変わるかを見せたうえで進ませる。
 */
data class ColorInUse(
    val paletteId: PaletteId,
    val colorId: ColorId,
    val name: String,
    val schemes: List<SavedScheme>,
)

class ManageColorsViewModel(
    private val repository: CatalogRepository,
) : ViewModel() {

    val palettes: StateFlow<List<Palette>> = repository.catalog
        .map { it.palettes }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = emptyList(),
        )

    private val _undoable = MutableStateFlow<Undoable?>(null)
    val undoable: StateFlow<Undoable?> = _undoable.asStateFlow()

    private val _paletteInUse = MutableStateFlow<PaletteInUse?>(null)
    val paletteInUse: StateFlow<PaletteInUse?> = _paletteInUse.asStateFlow()

    private val _colorInUse = MutableStateFlow<ColorInUse?>(null)
    val colorInUse: StateFlow<ColorInUse?> = _colorInUse.asStateFlow()

    // ---- 色 ------------------------------------------------------------

    fun addColor(paletteId: PaletteId, name: String, color: Color) {
        viewModelScope.launch { repository.addColor(paletteId, name, color) }
    }

    fun updateColor(id: ColorId, name: String, color: Color) {
        viewModelScope.launch { repository.updateColor(id, name, color) }
    }

    /**
     * ユーザーが足した色を削除する。
     *
     * 保存した配色が使っている場合は、消す前に何が変わるかを見せる。非表示と違って
     * スナックバーの「元に戻す」を逃すと戻せず、色を作り直しても ID が変わるため
     * 配色の参照は復活しない。
     */
    fun deleteColor(paletteId: PaletteId, colorId: ColorId, name: String) {
        viewModelScope.launch {
            val schemes = repository.catalog.first().schemesUsing(paletteId, colorId)
            if (schemes.isEmpty()) {
                performDeleteColor(colorId, name, affectedSchemes = 0)
            } else {
                _colorInUse.value = ColorInUse(paletteId, colorId, name, schemes)
            }
        }
    }

    /** 確認ダイアログで「削除」を選んだとき。 */
    fun confirmDeleteColor() {
        val target = _colorInUse.value ?: return
        _colorInUse.value = null
        viewModelScope.launch {
            performDeleteColor(target.colorId, target.name, target.schemes.size)
        }
    }

    fun dismissColorInUse() {
        _colorInUse.value = null
    }

    private suspend fun performDeleteColor(id: ColorId, name: String, affectedSchemes: Int) {
        repository.deleteColor(id)?.let {
            _undoable.value = Undoable.ColorDeleted(it, name, affectedSchemes)
        }
    }

    /** 組み込みの色を一覧から外す。グループ単位なので他のグループには残る。 */
    fun hideColor(paletteId: PaletteId, colorId: ColorId, name: String) {
        viewModelScope.launch {
            if (repository.hideColor(paletteId, colorId)) {
                _undoable.value = Undoable.ColorHidden(paletteId, colorId, name)
            }
        }
    }

    fun unhideAll(paletteId: PaletteId) {
        viewModelScope.launch { repository.unhideAll(paletteId) }
    }

    // ---- グループ ------------------------------------------------------

    fun addPalette(name: String, firstColorName: String, firstColor: Color) {
        viewModelScope.launch { repository.addPalette(name, firstColorName, firstColor) }
    }

    fun renamePalette(id: PaletteId, name: String) {
        viewModelScope.launch { repository.renamePalette(id, name) }
    }

    /**
     * グループを削除する。使用中なら削除せず、どの装具が使っているかを [paletteInUse] に出す。
     * 黙って消すと装具のレイヤーが消えてしまうため。
     */
    fun deletePalette(palette: Palette) {
        viewModelScope.launch {
            val usages = repository.catalog.first().usages(palette.id)
            if (usages.isEmpty()) {
                repository.deletePalette(palette.id)
            } else {
                _paletteInUse.value = PaletteInUse(palette = palette, usages = usages)
            }
        }
    }

    fun dismissPaletteInUse() {
        _paletteInUse.value = null
    }

    // ---- 取り消し ------------------------------------------------------

    fun undo() {
        val target = _undoable.value ?: return
        _undoable.value = null
        viewModelScope.launch {
            when (target) {
                is Undoable.ColorDeleted -> repository.restoreColor(target.stored)
                is Undoable.ColorHidden ->
                    repository.unhideColor(target.paletteId, target.colorId)
            }
        }
    }

    /** 「元に戻す」の案内を出し終えたら呼ぶ。 */
    fun consumeUndoable() {
        _undoable.value = null
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
