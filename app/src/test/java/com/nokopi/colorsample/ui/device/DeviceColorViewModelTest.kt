package com.nokopi.colorsample.ui.device

import androidx.lifecycle.SavedStateHandle
import com.nokopi.colorsample.data.DeviceType
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeviceColorViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModelFor(
        device: DeviceType,
        saved: Map<String, Any> = emptyMap(),
    ) = DeviceColorViewModel(device, SavedStateHandle(saved))

    @Test
    fun `装具はキーから受け取った値をそのまま持つ`() {
        assertEquals(DeviceType.POGO, viewModelFor(DeviceType.POGO).device)
    }

    @Test
    fun `初期状態は全パーツが先頭の色`() = runTest {
        val viewModel = viewModelFor(DeviceType.A)
        val state = viewModel.uiState.value

        assertEquals(DeviceType.A.parts.size, state.selectedIndices.size)
        assertEquals(List(DeviceType.A.parts.size) { 0 }, state.selectedIndices)
        assertEquals("", state.personName)
    }

    @Test
    fun `色を選ぶとそのパーツだけが変わる`() = runTest {
        val viewModel = viewModelFor(DeviceType.NB)
        subscribe(viewModel)

        viewModel.selectColor(partIndex = 2, optionIndex = 5)
        viewModel.selectColor(partIndex = 0, optionIndex = 3)

        val selections = viewModel.uiState.value.selectedIndices
        assertEquals(3, selections[0])
        assertEquals(0, selections[1])
        assertEquals(5, selections[2])
    }

    @Test
    fun `氏名を更新できる`() = runTest {
        val viewModel = viewModelFor(DeviceType.SLB)
        subscribe(viewModel)

        viewModel.updatePersonName("山田 太郎")

        assertEquals("山田 太郎", viewModel.uiState.value.personName)
    }

    @Test
    fun `リセットで色は戻るが氏名は残る`() = runTest {
        val viewModel = viewModelFor(DeviceType.FTN)
        subscribe(viewModel)

        viewModel.updatePersonName("山田")
        viewModel.selectColor(partIndex = 1, optionIndex = 4)
        viewModel.reset()

        val state = viewModel.uiState.value
        assertEquals(List(DeviceType.FTN.parts.size) { 0 }, state.selectedIndices)
        assertEquals("山田", state.personName)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `範囲外のパーツを指定すると弾く`() {
        viewModelFor(DeviceType.PL).selectColor(partIndex = 99, optionIndex = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `パレットにない色を指定すると弾く`() {
        // PL のカン (先頭パーツ) は白黒の2色しかない。
        viewModelFor(DeviceType.PL).selectColor(partIndex = 0, optionIndex = 2)
    }

    @Test
    fun `保存済みの状態から復元する`() = runTest {
        val viewModel = viewModelFor(
            DeviceType.NB,
            saved = mapOf(
                "personName" to "復元 花子",
                "selections" to intArrayOf(1, 2, 3, 0, 0, 1, 0, 0),
            ),
        )
        val state = viewModel.uiState.value

        assertEquals("復元 花子", state.personName)
        assertEquals(listOf(1, 2, 3, 0, 0, 1, 0, 0), state.selectedIndices)
    }

    @Test
    fun `パーツ数が合わない保存状態は初期値として扱う`() = runTest {
        // 装具の定義が変わった後にアプリが復帰した場合を想定。
        val viewModel = viewModelFor(
            DeviceType.NB,
            saved = mapOf("selections" to intArrayOf(1, 2)),
        )

        assertEquals(
            List(DeviceType.NB.parts.size) { 0 },
            viewModel.uiState.value.selectedIndices,
        )
    }

    /**
     * WhileSubscribed の StateFlow を動かすために購読だけしておく。
     * 購読の開始と以降の更新をその場で流したいので unconfined で回す。
     */
    private fun TestScope.subscribe(viewModel: DeviceColorViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
    }
}
