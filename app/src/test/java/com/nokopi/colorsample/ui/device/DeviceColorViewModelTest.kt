package com.nokopi.colorsample.ui.device

import androidx.lifecycle.SavedStateHandle
import com.nokopi.colorsample.data.BuiltInCatalog
import com.nokopi.colorsample.data.CatalogMerger
import com.nokopi.colorsample.data.model.Catalog
import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.DeviceId
import com.nokopi.colorsample.data.store.StoredCatalog
import com.nokopi.colorsample.data.store.StoredColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeviceColorViewModelTest {

    private val nbId = DeviceId("builtin:nb")
    private val leather = BuiltInCatalog.leatherId

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun catalogOf(stored: StoredCatalog = StoredCatalog.EMPTY) =
        MutableStateFlow(CatalogMerger.merge(stored))

    private fun viewModelFor(
        deviceId: DeviceId = nbId,
        catalog: MutableStateFlow<Catalog> = catalogOf(),
        saved: Map<String, Any> = emptyMap(),
    ) = DeviceColorViewModel(deviceId, catalog, SavedStateHandle(saved))

    /** WhileSubscribed の StateFlow を動かすために購読だけしておく。 */
    private fun TestScope.subscribe(viewModel: DeviceColorViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
    }

    private fun ready(viewModel: DeviceColorViewModel) =
        viewModel.uiState.value as DeviceColorUiState.Ready

    @Test
    fun `最初はカタログ待ちの Loading`() {
        assertEquals(DeviceColorUiState.Loading, viewModelFor().uiState.value)
    }

    @Test
    fun `知らない装具は NotFound になる`() = runTest {
        val viewModel = viewModelFor(deviceId = DeviceId("user:missing"))
        subscribe(viewModel)

        assertEquals(DeviceColorUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `表示中に装具が消えたら NotFound に変わる`() = runTest {
        val userDevice = StoredCatalog(
            devices = listOf(
                com.nokopi.colorsample.data.store.StoredDevice(
                    id = "user:d1",
                    name = "自作",
                    parts = listOf(
                        com.nokopi.colorsample.data.store.StoredPart(
                            "user:p1", "本体", "p1.png", leather.value,
                        ),
                    ),
                ),
            ),
        )
        val catalog = catalogOf(userDevice)
        val viewModel = viewModelFor(deviceId = DeviceId("user:d1"), catalog = catalog)
        subscribe(viewModel)
        assertTrue(viewModel.uiState.value is DeviceColorUiState.Ready)

        catalog.value = CatalogMerger.merge(StoredCatalog.EMPTY)

        assertEquals(DeviceColorUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `初期状態は全パーツがパレット先頭の色`() = runTest {
        val viewModel = viewModelFor()
        subscribe(viewModel)

        val state = ready(viewModel)
        assertEquals(8, state.parts.size)
        assertEquals("", state.personName)
        assertTrue(state.parts.all { it.selected == it.palette.options.first() })
    }

    @Test
    fun `色を選ぶとそのパーツだけが変わる`() = runTest {
        val viewModel = viewModelFor()
        subscribe(viewModel)

        val parts = ready(viewModel).parts
        val target = parts[2]
        val newColor = target.palette.options[5]
        viewModel.selectColor(target.part.id, newColor.id)

        val after = ready(viewModel).parts
        assertEquals(newColor, after[2].selected)
        assertEquals(parts[0].selected, after[0].selected)
        assertEquals(parts[1].selected, after[1].selected)
    }

    /**
     * index ではなく ID で持っている理由そのもの。
     * 色を1つ増やしても、選んでいた色が別のものに化けてはいけない。
     */
    @Test
    fun `色が追加されても選択中の色は変わらない`() = runTest {
        val catalog = catalogOf()
        val viewModel = viewModelFor(catalog = catalog)
        subscribe(viewModel)

        val target = ready(viewModel).parts.first { it.part.paletteId == leather }
        val chosen = target.palette.options[3]
        viewModel.selectColor(target.part.id, chosen.id)
        assertEquals(chosen, ready(viewModel).parts.first { it.part.paletteId == leather }.selected)

        // 革のパレットに色が1つ増える
        catalog.value = CatalogMerger.merge(
            StoredCatalog(
                colors = listOf(
                    StoredColor("user:c1", leather.value, "特注", 0xFF123456.toInt()),
                ),
            ),
        )

        val after = ready(viewModel).parts.first { it.part.paletteId == leather }
        assertEquals(chosen, after.selected)
        assertEquals(target.palette.options.size + 1, after.palette.options.size)
    }

    @Test
    fun `選んでいた色が削除されたらパレット先頭に落ちる`() = runTest {
        val withUserColor = StoredCatalog(
            colors = listOf(StoredColor("user:c1", leather.value, "特注", 0xFF123456.toInt())),
        )
        val catalog = catalogOf(withUserColor)
        val viewModel = viewModelFor(catalog = catalog)
        subscribe(viewModel)

        val target = ready(viewModel).parts.first { it.part.paletteId == leather }
        viewModel.selectColor(target.part.id, ColorId("user:c1"))
        assertEquals(ColorId("user:c1"), ready(viewModel).parts.first { it.part.paletteId == leather }.selected.id)

        catalog.value = CatalogMerger.merge(StoredCatalog.EMPTY)

        val after = ready(viewModel).parts.first { it.part.paletteId == leather }
        assertEquals(after.palette.options.first(), after.selected)
    }

    @Test
    fun `氏名を更新できる`() = runTest {
        val viewModel = viewModelFor()
        subscribe(viewModel)

        viewModel.updatePersonName("山田 太郎")

        assertEquals("山田 太郎", ready(viewModel).personName)
    }

    @Test
    fun `リセットで色は戻るが氏名は残る`() = runTest {
        val viewModel = viewModelFor()
        subscribe(viewModel)

        val target = ready(viewModel).parts[1]
        viewModel.updatePersonName("山田")
        viewModel.selectColor(target.part.id, target.palette.options[4].id)
        viewModel.reset()

        val state = ready(viewModel)
        assertEquals("山田", state.personName)
        assertTrue(state.parts.all { it.selected == it.palette.options.first() })
    }

    @Test
    fun `保存済みの状態から復元する`() = runTest {
        val partId = BuiltInCatalog.devices.first { it.id == nbId }.parts[2].id
        val colorId = BuiltInCatalog.palettes.first { it.id == leather }.options[6].id
        val viewModel = viewModelFor(
            saved = mapOf(
                "personName" to "復元 花子",
                "selections" to """{"${partId.value}":"${colorId.value}"}""",
            ),
        )
        subscribe(viewModel)

        val state = ready(viewModel)
        assertEquals("復元 花子", state.personName)
        assertEquals(colorId, state.parts[2].selected.id)
    }

    @Test
    fun `壊れた保存状態は初期値として扱う`() = runTest {
        val viewModel = viewModelFor(saved = mapOf("selections" to "これはJSONではない"))
        subscribe(viewModel)

        assertTrue(ready(viewModel).parts.all { it.selected == it.palette.options.first() })
    }
}
