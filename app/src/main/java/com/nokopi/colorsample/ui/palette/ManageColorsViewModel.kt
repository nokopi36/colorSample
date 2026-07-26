package com.nokopi.colorsample.ui.palette

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokopi.colorsample.data.CatalogRepository
import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.Palette
import com.nokopi.colorsample.data.model.PaletteId
import com.nokopi.colorsample.data.store.StoredColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** 削除直後に「元に戻す」を出すための控え。 */
data class DeletedColor(val stored: StoredColor, val name: String)

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

    private val _lastDeleted = MutableStateFlow<DeletedColor?>(null)
    val lastDeleted: StateFlow<DeletedColor?> = _lastDeleted.asStateFlow()

    fun addColor(paletteId: PaletteId, name: String, color: Color) {
        viewModelScope.launch { repository.addColor(paletteId, name, color) }
    }

    fun updateColor(id: ColorId, name: String, color: Color) {
        viewModelScope.launch { repository.updateColor(id, name, color) }
    }

    fun deleteColor(id: ColorId, name: String) {
        viewModelScope.launch {
            repository.deleteColor(id)?.let { _lastDeleted.value = DeletedColor(it, name) }
        }
    }

    fun undoDelete() {
        val deleted = _lastDeleted.value ?: return
        _lastDeleted.value = null
        viewModelScope.launch { repository.restoreColor(deleted.stored) }
    }

    /** 「元に戻す」の案内を出し終えたら呼ぶ。 */
    fun consumeDeleted() {
        _lastDeleted.value = null
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
