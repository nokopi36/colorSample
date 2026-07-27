package com.nokopi.colorsample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.nokopi.colorsample.data.CatalogRepository
import com.nokopi.colorsample.data.ImageImporter
import com.nokopi.colorsample.data.model.DeviceId
import com.nokopi.colorsample.data.model.PaletteId
import com.nokopi.colorsample.data.model.SchemeId
import com.nokopi.colorsample.ui.device.DeviceColorScreen
import com.nokopi.colorsample.ui.device.DeviceColorViewModel
import com.nokopi.colorsample.ui.deviceeditor.DeviceEditorScreen
import com.nokopi.colorsample.ui.deviceeditor.DeviceEditorViewModel
import com.nokopi.colorsample.ui.home.HomeScreen
import com.nokopi.colorsample.ui.home.HomeViewModel
import com.nokopi.colorsample.ui.palette.ManageColorsScreen
import com.nokopi.colorsample.ui.palette.ManageColorsViewModel
import com.nokopi.colorsample.ui.scheme.SavedSchemesScreen
import com.nokopi.colorsample.ui.scheme.SavedSchemesViewModel

@Composable
fun ColorSampleNavDisplay(
    versionName: String,
    onOpenPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey> = rememberNavBackStack(HomeKey),
) {
    val context = LocalContext.current
    val repository = CatalogRepository.get(context)

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
                val homeViewModel: HomeViewModel =
                    viewModel(factory = factory { HomeViewModel(repository) })
                val state by homeViewModel.uiState.collectAsStateWithLifecycle()

                HomeScreen(
                    versionName = versionName,
                    devices = state.devices,
                    hiddenDevices = state.hiddenDevices,
                    onSelectDevice = { backStack.add(DeviceKey(it.value)) },
                    onAddDevice = { backStack.add(DeviceEditorKey()) },
                    onEditDevice = { backStack.add(DeviceEditorKey(it.value)) },
                    onDeleteDevice = homeViewModel::deleteDevice,
                    onHideDevice = homeViewModel::hideDevice,
                    onUnhideDevice = homeViewModel::unhideDevice,
                    onManageColors = { backStack.add(ManageColorsKey()) },
                    onOpenSavedSchemes = { backStack.add(SavedSchemesKey) },
                    onOpenPrivacyPolicy = onOpenPrivacyPolicy,
                )
            }

            entry<DeviceKey> { key ->
                // Nav3 にはルート引数がないので、キーが持つ値をそのままコンストラクタへ渡す。
                // 氏名と選択中の配色は SavedStateHandle に載るので回転・プロセス death でも残る。
                DeviceColorScreen(
                    onNavigateUp = { backStack.removeLastOrNull() },
                    onAddColor = { backStack.add(ManageColorsKey(it.value)) },
                    viewModel = viewModel(
                        factory = factory {
                            DeviceColorViewModel(
                                deviceId = DeviceId(key.deviceId),
                                catalog = repository.catalog,
                                handle = createSavedStateHandle(),
                                initialSchemeId = key.schemeId?.let(::SchemeId),
                                persistScheme = repository::saveScheme,
                            )
                        },
                    ),
                )
            }

            entry<SavedSchemesKey> {
                SavedSchemesScreen(
                    onNavigateUp = { backStack.removeLastOrNull() },
                    onOpenScheme = {
                        backStack.add(DeviceKey(it.device.id.value, it.id.value))
                    },
                    viewModel = viewModel(factory = factory { SavedSchemesViewModel(repository) }),
                )
            }

            entry<ManageColorsKey> { key ->
                ManageColorsScreen(
                    onNavigateUp = { backStack.removeLastOrNull() },
                    focusPaletteId = key.focusPaletteId?.let(::PaletteId),
                    viewModel = viewModel(factory = factory { ManageColorsViewModel(repository) }),
                )
            }

            entry<DeviceEditorKey> { key ->
                val importer = remember(context) { ImageImporter(context) }
                DeviceEditorScreen(
                    onNavigateUp = { backStack.removeLastOrNull() },
                    viewModel = viewModel(
                        factory = factory {
                            DeviceEditorViewModel(
                                deviceId = key.deviceId?.let(::DeviceId),
                                repository = repository,
                                importer = importer,
                            )
                        },
                    ),
                )
            }
        },
    )
}

/**
 * `viewModel(factory = ...)` に渡す1行ファクトリ。
 * ここでしか使わないので DI ライブラリは入れていない。
 */
private inline fun <reified T : androidx.lifecycle.ViewModel> factory(
    crossinline create: androidx.lifecycle.viewmodel.CreationExtras.() -> T,
): ViewModelProvider.Factory = viewModelFactory {
    initializer { create() }
}
