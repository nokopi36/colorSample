package com.nokopi.colorsample.ui.scheme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokopi.colorsample.data.CatalogRepository
import com.nokopi.colorsample.data.model.SavedScheme
import com.nokopi.colorsample.data.model.SchemeId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 保存した配色の一覧。
 *
 * 参照先の装具が消えている・非表示になっている配色は [com.nokopi.colorsample.data.CatalogMerger]
 * の時点で落ちているので、ここに届くものはすべて開ける。
 */
class SavedSchemesViewModel(
    private val repository: CatalogRepository,
) : ViewModel() {

    val schemes: StateFlow<List<SavedScheme>> = repository.catalog
        .map { it.schemes }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = emptyList(),
        )

    fun rename(id: SchemeId, name: String) {
        viewModelScope.launch { repository.renameScheme(id, name) }
    }

    fun delete(id: SchemeId) {
        viewModelScope.launch { repository.deleteScheme(id) }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
