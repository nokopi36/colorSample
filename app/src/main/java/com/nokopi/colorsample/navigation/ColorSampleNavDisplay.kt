package com.nokopi.colorsample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nokopi.colorsample.ui.device.DeviceColorScreen
import com.nokopi.colorsample.ui.device.DeviceColorViewModel
import com.nokopi.colorsample.ui.home.HomeScreen

@Composable
fun ColorSampleNavDisplay(
    versionName: String,
    onOpenPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey> = rememberNavBackStack(HomeKey),
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            // ViewModel を画面ごとに閉じ込める。バックスタックから外れた時点で clear される。
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<HomeKey> {
                HomeScreen(
                    versionName = versionName,
                    onSelectDevice = { backStack.add(DeviceKey(it)) },
                    onOpenPrivacyPolicy = onOpenPrivacyPolicy,
                )
            }

            entry<DeviceKey> { key ->
                // Nav3 にはルート引数がないので、キーが持つ DeviceType をそのまま
                // コンストラクタへ渡す。氏名と選択中の配色は引き続き SavedStateHandle に
                // 載るので、回転でもプロセス death からの復帰でも残る。
                DeviceColorScreen(
                    onNavigateUp = { backStack.removeLastOrNull() },
                    viewModel = viewModel(factory = deviceColorViewModelFactory(key)),
                )
            }
        },
    )
}

private fun deviceColorViewModelFactory(key: DeviceKey): ViewModelProvider.Factory =
    viewModelFactory {
        initializer { DeviceColorViewModel(key.type, createSavedStateHandle()) }
    }
