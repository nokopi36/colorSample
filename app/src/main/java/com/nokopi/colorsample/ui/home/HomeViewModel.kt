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

data class HomeUiState(
    val devices: List<Device> = emptyList(),
    /** ホームから外した装具。戻す操作のために内容ごと持つ。 */
    val hiddenDevices: List<Device> = emptyList(),
)

class HomeViewModel(private val repository: CatalogRepository) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.catalog
        .map { HomeUiState(devices = it.devices, hiddenDevices = it.hiddenDevices) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = HomeUiState(),
        )

    /** ユーザーが作った装具を消す。画像のディレクトリもまとめて消える。 */
    fun deleteDevice(id: DeviceId) {
        viewModelScope.launch { repository.deleteDevice(id) }
    }

    /**
     * 装具をホームから外す。組み込みの装具は削除できないのでこちらを使う。
     * 定義は消さないので、あとから戻せる。
     */
    fun hideDevice(id: DeviceId) {
        viewModelScope.launch { repository.hideDevice(id) }
    }

    fun unhideDevice(id: DeviceId) {
        viewModelScope.launch { repository.unhideDevice(id) }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
