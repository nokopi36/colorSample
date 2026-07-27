package com.nokopi.colorsample.ui.device

import androidx.lifecycle.SavedStateHandle
import com.nokopi.colorsample.data.BuiltInCatalog
import com.nokopi.colorsample.data.CatalogMerger
import com.nokopi.colorsample.data.SchemeSave
import com.nokopi.colorsample.data.model.Catalog
import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.DeviceId
import com.nokopi.colorsample.data.model.SchemeId
import com.nokopi.colorsample.data.store.StoredCatalog
import com.nokopi.colorsample.data.store.StoredColor
import com.nokopi.colorsample.data.store.StoredDevice
import com.nokopi.colorsample.data.store.StoredPart
import com.nokopi.colorsample.data.store.StoredScheme
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

    /** 保存された配色を控えるだけの偽物。リポジトリは Context を要るのでここでは使わない。 */
    private class RecordingSaver : suspend (SchemeSave) -> SchemeId {
        val saves = mutableListOf<SchemeSave>()
        var nextId = SchemeId("user:new")

        override suspend fun invoke(save: SchemeSave): SchemeId {
            saves += save
            return save.id ?: nextId
        }
    }

    private fun viewModelFor(
        deviceId: DeviceId = nbId,
        catalog: MutableStateFlow<Catalog> = catalogOf(),
        saved: Map<String, Any> = emptyMap(),
        schemeId: SchemeId? = null,
        saver: RecordingSaver = RecordingSaver(),
    ) = DeviceColorViewModel(deviceId, catalog, SavedStateHandle(saved), schemeId, saver)

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
                StoredDevice(
                    id = "user:d1",
                    name = "自作",
                    parts = listOf(StoredPart("user:p1", "本体", "p1.png", leather.value)),
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
        // 線画 (色を変えないレイヤー) は選択欄には出ない。
        assertEquals(9, state.device.parts.size)
        assertEquals(8, state.selections.size)
        assertEquals("", state.personName)
        assertTrue(state.selections.all { it.selected == it.palette.options.first() })
    }

    @Test
    fun `色を選ぶとそのパーツだけが変わる`() = runTest {
        val viewModel = viewModelFor()
        subscribe(viewModel)

        val parts = ready(viewModel).selections
        val target = parts[2]
        val newColor = target.palette.options[5]
        viewModel.selectColor(target.part.id, newColor.id)

        val after = ready(viewModel).selections
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

        val target = ready(viewModel).selections.first { it.part.paletteId == leather }
        val chosen = target.palette.options[3]
        viewModel.selectColor(target.part.id, chosen.id)
        assertEquals(chosen, ready(viewModel).selections.first { it.part.paletteId == leather }.selected)

        // 革のパレットに色が1つ増える
        catalog.value = CatalogMerger.merge(
            StoredCatalog(
                colors = listOf(
                    StoredColor("user:c1", leather.value, "特注", 0xFF123456.toInt()),
                ),
            ),
        )

        val after = ready(viewModel).selections.first { it.part.paletteId == leather }
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

        val target = ready(viewModel).selections.first { it.part.paletteId == leather }
        viewModel.selectColor(target.part.id, ColorId("user:c1"))
        assertEquals(ColorId("user:c1"), ready(viewModel).selections.first { it.part.paletteId == leather }.selected.id)

        catalog.value = CatalogMerger.merge(StoredCatalog.EMPTY)

        val after = ready(viewModel).selections.first { it.part.paletteId == leather }
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

        val target = ready(viewModel).selections[1]
        viewModel.updatePersonName("山田")
        viewModel.selectColor(target.part.id, target.palette.options[4].id)
        viewModel.reset()

        val state = ready(viewModel)
        assertEquals("山田", state.personName)
        assertTrue(state.selections.all { it.selected == it.palette.options.first() })
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
        assertEquals(colorId, state.selections[2].selected.id)
    }

    @Test
    fun `壊れた保存状態は初期値として扱う`() = runTest {
        val viewModel = viewModelFor(saved = mapOf("selections" to "これはJSONではない"))
        subscribe(viewModel)

        assertTrue(ready(viewModel).selections.all { it.selected == it.palette.options.first() })
    }

    // ---- 保存した配色 --------------------------------------------------

    /** 組み込みナイトブレースの色を変えるパーツ全部を、各パレットの末尾の色にした配色。 */
    private fun schemeCatalog(id: String = "user:s1"): StoredCatalog {
        val merged = CatalogMerger.merge(StoredCatalog.EMPTY)
        val device = merged.device(nbId)!!
        val selections = device.parts
            .mapNotNull { part -> part.paletteId?.let { part to merged.palette(it) } }
            .associate { (part, palette) -> part.id.value to palette.options.last().id.value }

        return StoredCatalog(
            schemes = listOf(
                StoredScheme(
                    id = id,
                    deviceId = nbId.value,
                    name = "運動会",
                    personName = "配色 太郎",
                    selections = selections,
                ),
            ),
        )
    }

    @Test
    fun `保存した配色から開くと色と氏名がその内容になる`() = runTest {
        val viewModel = viewModelFor(
            catalog = catalogOf(schemeCatalog()),
            schemeId = SchemeId("user:s1"),
        )
        subscribe(viewModel)

        val state = ready(viewModel)
        assertEquals("配色 太郎", state.personName)
        assertEquals("運動会", state.schemeName)
        assertTrue(state.selections.all { it.selected == it.palette.options.last() })
    }

    /**
     * 配色を開いて1色だけ変えたときに、残りが配色の色を失わないこと。
     *
     * 未操作のあいだ選択は SavedStateHandle に無いので、handle の中身（空）を土台に
     * 差分を積むと他のパーツが全部パレット先頭に戻ってしまう。
     */
    @Test
    fun `配色を開いて1色変えても他のパーツは配色のまま`() = runTest {
        val viewModel = viewModelFor(
            catalog = catalogOf(schemeCatalog()),
            schemeId = SchemeId("user:s1"),
        )
        subscribe(viewModel)

        val target = ready(viewModel).selections.first()
        viewModel.selectColor(target.part.id, target.palette.options.first().id)

        val after = ready(viewModel).selections
        assertEquals(after.first().palette.options.first(), after.first().selected)
        assertTrue(after.drop(1).all { it.selected == it.palette.options.last() })
    }

    @Test
    fun `新規保存では ID を渡さず、表示中の内容がそのまま残る`() = runTest {
        val saver = RecordingSaver()
        val viewModel = viewModelFor(saver = saver)
        subscribe(viewModel)

        val target = ready(viewModel).selections[1]
        val chosen = target.palette.options.last()
        viewModel.updatePersonName("山田")
        viewModel.selectColor(target.part.id, chosen.id)
        viewModel.saveScheme("春の配色", overwrite = false)

        val save = saver.saves.single()
        assertEquals(null, save.id)
        assertEquals(nbId, save.deviceId)
        assertEquals("春の配色", save.name)
        assertEquals("山田", save.personName)
        // 触っていないパーツも含めて、見えているとおりの内容が保存される。
        assertEquals(ready(viewModel).selections.size, save.selections.size)
        assertEquals(chosen.id, save.selections[target.part.id])
    }

    @Test
    fun `上書き保存は開いている配色の ID を渡す`() = runTest {
        val saver = RecordingSaver()
        val viewModel = viewModelFor(
            catalog = catalogOf(schemeCatalog()),
            schemeId = SchemeId("user:s1"),
            saver = saver,
        )
        subscribe(viewModel)

        viewModel.saveScheme("運動会", overwrite = true)

        assertEquals(SchemeId("user:s1"), saver.saves.single().id)
    }

    /**
     * 報告された手順の往復。革に足した色を選んで保存し、色の管理側の判定にかける。
     * ViewModel が作る [SchemeSave] をリポジトリと同じ形に変換して繋ぐ。
     */
    @Test
    fun `足した色を選んで保存した配色は、使用中として引ける`() = runTest {
        val userColor = StoredColor("user:c1", leather.value, "特注", 0xFF123456.toInt())
        val stored = StoredCatalog(colors = listOf(userColor))
        val saver = RecordingSaver()
        val viewModel = viewModelFor(catalog = catalogOf(stored), saver = saver)
        subscribe(viewModel)

        val target = ready(viewModel).selections.first { it.part.paletteId == leather }
        viewModel.selectColor(target.part.id, ColorId("user:c1"))
        viewModel.saveScheme("運動会", overwrite = false)

        val save = saver.saves.single()
        val merged = CatalogMerger.merge(
            stored.copy(
                schemes = listOf(
                    StoredScheme(
                        id = "user:s1",
                        deviceId = save.deviceId.value,
                        name = save.name,
                        personName = save.personName,
                        selections = save.selections.entries
                            .associate { (part, color) -> part.value to color.value },
                    ),
                ),
            ),
        )

        assertEquals(1, merged.schemesUsing(leather, ColorId("user:c1")).size)
    }

    /** 新規保存したあとは、そのまま続けて上書きできる。 */
    @Test
    fun `新規保存のあとの上書きは採番された ID を使う`() = runTest {
        val saver = RecordingSaver().apply { nextId = SchemeId("user:generated") }
        val viewModel = viewModelFor(saver = saver)
        subscribe(viewModel)

        viewModel.saveScheme("一回目", overwrite = false)
        viewModel.saveScheme("二回目", overwrite = true)

        assertEquals(null, saver.saves[0].id)
        assertEquals(SchemeId("user:generated"), saver.saves[1].id)
    }
}
