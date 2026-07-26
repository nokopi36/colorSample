package com.nokopi.colorsample.ui.deviceeditor

import com.nokopi.colorsample.data.BuiltInCatalog
import com.nokopi.colorsample.data.model.PaletteId
import com.nokopi.colorsample.data.model.PartId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceEditorStateTest {

    private val leather = BuiltInCatalog.leatherId
    private val button = BuiltInCatalog.buttonId

    private fun layer(
        id: String,
        name: String = "パーツ$id",
        paletteId: PaletteId? = leather,
    ) = DraftLayer(
        partId = PartId(id),
        name = name,
        fileName = "$id.png",
        paletteId = paletteId,
    )

    /** 色を変えないレイヤー。 */
    private fun untinted(id: String) = layer(id, name = "", paletteId = null)

    private fun layers(vararg ids: String) = ids.map { layer(it) }

    private fun ids(layers: List<DraftLayer>) = layers.map { it.partId.value }

    // ---- 並べ替え ------------------------------------------------------

    @Test
    fun `奥へ動かす`() {
        val moved = DraftLayers.moved(layers("a", "b", "c"), PartId("b"), -1)
        assertEquals(listOf("b", "a", "c"), ids(moved))
    }

    @Test
    fun `手前へ動かす`() {
        val moved = DraftLayers.moved(layers("a", "b", "c"), PartId("b"), 1)
        assertEquals(listOf("a", "c", "b"), ids(moved))
    }

    @Test
    fun `端を越える指定は何もしない`() {
        val original = layers("a", "b", "c")
        assertSame(original, DraftLayers.moved(original, PartId("a"), -1))
        assertSame(original, DraftLayers.moved(original, PartId("c"), 1))
    }

    @Test
    fun `知らないパーツを動かそうとしても何もしない`() {
        val original = layers("a", "b")
        assertSame(original, DraftLayers.moved(original, PartId("zzz"), 1))
    }

    @Test
    fun `動かしても枚数と中身は変わらない`() {
        val original = layers("a", "b", "c", "d")
        val moved = DraftLayers.moved(original, PartId("d"), -1)
        assertEquals(original.size, moved.size)
        assertEquals(original.toSet(), moved.toSet())
    }

    // ---- 色を変える／変えないの切り替え --------------------------------

    private fun toggle(layers: List<DraftLayer>, id: String, default: PaletteId = leather) =
        DraftLayers.toggleTinting(layers, PartId(id), default)

    @Test
    fun `1回押すと色を変えないになる`() {
        val updated = toggle(layers("a", "b"), "a")
        assertNull(updated[0].paletteId)
        assertFalse(updated[0].isTinted)
        // 他のレイヤーは動かない。
        assertEquals(leather, updated[1].paletteId)
    }

    /**
     * ここが本題。以前は画面側が「変えたあとの値」を組み立てる形で、その反転を
     * 間違えていたためチップが無反応だった。押すたびに必ず反転すること。
     */
    @Test
    fun `押すたびに必ず切り替わる`() {
        var result = layers("a")
        assertTrue(result[0].isTinted)

        result = toggle(result, "a")
        assertFalse("1回目で切り替わっていない", result[0].isTinted)

        result = toggle(result, "a")
        assertTrue("2回目で戻っていない", result[0].isTinted)

        result = toggle(result, "a")
        assertFalse("3回目で切り替わっていない", result[0].isTinted)
    }

    @Test
    fun `色を変える側に戻すと既定のパレットが入る`() {
        val untintedFirst = toggle(layers("a"), "a")
        val restored = toggle(untintedFirst, "a", default = button)
        assertEquals(button, restored[0].paletteId)
        assertTrue(restored[0].isTinted)
    }

    @Test
    fun `色を変えないレイヤーは何枚でも持てる`() {
        // 以前は「線画は装具全体で1枚だけ」という排他制約があった。
        var result = layers("a", "b", "c")
        result = toggle(result, "a")
        result = toggle(result, "c")

        assertEquals(listOf("a", "c"), ids(result.filterNot { it.isTinted }))
        assertEquals(listOf("b"), ids(result.filter { it.isTinted }))
    }

    @Test
    fun `色を変えないレイヤーを途中に挟める`() {
        // 固定色の影や金具を tint する層のあいだに置ける。
        val result = toggle(layers("a", "b", "c"), "b")
        assertEquals(listOf(true, false, true), result.map { it.isTinted })
        // 並び順は動かさない。
        assertEquals(listOf("a", "b", "c"), ids(result))
    }

    @Test
    fun `知らないパーツの切り替えは何も変えない`() {
        val original = layers("a", "b")
        assertEquals(original, toggle(original, "zzz"))
    }

    // ---- 名前とパレット ------------------------------------------------

    @Test
    fun `指定したパーツだけ名前が変わる`() {
        val updated = DraftLayers.withName(layers("a", "b"), PartId("b"), "ベルト")
        assertEquals("パーツa", updated[0].name)
        assertEquals("ベルト", updated[1].name)
    }

    @Test
    fun `指定したパーツだけパレットが変わる`() {
        val updated = DraftLayers.withPalette(layers("a", "b"), PartId("a"), button)
        assertEquals(button, updated[0].paletteId)
        assertEquals(leather, updated[1].paletteId)
    }

    @Test
    fun `パーツを外せる`() {
        assertEquals(listOf("a", "c"), ids(DraftLayers.without(layers("a", "b", "c"), PartId("b"))))
    }

    // ---- 保存できるかの判定 --------------------------------------------

    private fun state(
        name: String = "自作装具",
        layers: List<DraftLayer> = layers("a"),
        isLoading: Boolean = false,
        isImporting: Boolean = false,
        isSaving: Boolean = false,
    ) = DeviceEditorUiState(
        isLoading = isLoading,
        name = name,
        layers = layers,
        palettes = BuiltInCatalog.palettes,
        isImporting = isImporting,
        isSaving = isSaving,
    )

    @Test
    fun `名前とパーツが揃えば保存できる`() {
        assertTrue(state().canSave)
    }

    @Test
    fun `装具の名前が空なら保存できない`() {
        assertFalse(state(name = "").canSave)
        assertFalse(state(name = "   ").canSave)
    }

    @Test
    fun `パーツが無ければ保存できない`() {
        assertFalse(state(layers = emptyList()).canSave)
    }

    @Test
    fun `色を変えない層だけでは保存できない`() {
        // 色を変えられる層が1枚も無いと、配色画面で何も操作できない。
        assertFalse(state(layers = listOf(untinted("a"))).canSave)
        assertFalse(state(layers = listOf(untinted("a"), untinted("b"))).canSave)
    }

    @Test
    fun `名前の無いパーツがあれば保存できない`() {
        assertFalse(state(layers = listOf(layer("a", name = ""))).canSave)
        assertFalse(state(layers = listOf(layer("a"), layer("b", name = " "))).canSave)
    }

    @Test
    fun `色を変えない層の名前は空でも保存できる`() {
        // 色を変えないレイヤーは選択欄に出ないので名前を持たない。
        assertTrue(state(layers = listOf(layer("a"), untinted("b"))).canSave)
        assertTrue(state(layers = listOf(untinted("a"), layer("b"), untinted("c"))).canSave)
    }

    @Test
    fun `読み込み中や処理中は保存できない`() {
        assertFalse(state(isLoading = true).canSave)
        assertFalse(state(isImporting = true).canSave)
        assertFalse(state(isSaving = true).canSave)
    }

    @Test
    fun `色を選べるレイヤーだけ取り出せる`() {
        val all = listOf(layer("a"), untinted("b"), layer("c"))
        assertEquals(listOf("a", "c"), ids(state(layers = all).tintedLayers))
    }

    @Test
    fun `編集中の画像は staging から読む`() {
        // 保存前のプレビューは staging のファイルを指す。装具のディレクトリではない。
        assertEquals(
            "devices/.staging/a.png",
            (layer("a").image as com.nokopi.colorsample.data.model.PartImage.Stored).relativePath,
        )
    }
}
