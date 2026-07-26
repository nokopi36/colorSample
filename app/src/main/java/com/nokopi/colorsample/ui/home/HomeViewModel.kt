package com.nokopi.colorsample.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokopi.colorsample.data.CatalogRepository
import com.nokopi.colorsample.data.model.Device
import com.nokopi.colorsample.data.model.DeviceId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CatalogRepository) : ViewModel() {

    val devices: StateFlow<List<Device>> = repository.catalog
        .map { it.devices }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = emptyList(),
        )

    /** ユーザーが作った装具を消す。画像のディレクトリもまとめて消える。 */
    fun deleteDevice(id: DeviceId) {
        viewModelScope.launch { repository.deleteDevice(id) }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
