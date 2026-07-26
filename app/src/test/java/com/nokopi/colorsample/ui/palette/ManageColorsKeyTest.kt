package com.nokopi.colorsample.ui.palette

import com.nokopi.colorsample.data.BuiltInCatalog
import com.nokopi.colorsample.data.CatalogMerger
import com.nokopi.colorsample.data.store.HiddenColor
import com.nokopi.colorsample.data.store.StoredCatalog
import com.nokopi.colorsample.data.store.StoredPalette
import com.nokopi.colorsample.data.store.StoredColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 色の管理画面は全パレットを1つの LazyColumn に並べる。
 *
 * 白や黒のような組み込みの色は複数のパレットに同じ ID で登場するので、
 * 色の ID だけをキーにすると LazyColumn が重複キーで実行時に落ちる。実際に落とした。
 */
class ManageColorsKeyTest {

    private fun keysFor(stored: StoredCatalog): List<String> =
        CatalogMerger.merge(stored).palettes.flatMap { palette ->
            palette.options.map { colorItemKey(palette, it) }
        }

    @Test
    fun `組み込みだけでも行のキーが全体で一意になる`() {
        val keys = keysFor(StoredCatalog.EMPTY)
        assertEquals(keys.size, keys.toSet().size)
    }

    @Test
    fun `同じ色が複数のパレットに出ることを前提にしたテストである`() {
        // 前提が崩れたらこのテスト自体の意味がなくなるので、それも見張る。
        val colorIds = BuiltInCatalog.palettes.flatMap { p -> p.options.map { it.id } }
        assertTrue(
            "同じ色が複数パレットに出る前提が崩れている",
            colorIds.size > colorIds.toSet().size,
        )
    }

    @Test
    fun `ユーザー定義グループを足してもキーが一意のまま`() {
        // グループを増やせるようになったので、そこでも衝突しないことを押さえる。
        val keys = keysFor(
            StoredCatalog(
                palettes = listOf(
                    StoredPalette("user:p1", "マジックテープ"),
                    StoredPalette("user:p2", "刺繍糸"),
                ),
                colors = listOf(
                    // 組み込みと同名・同色でもキーはグループ込みなので衝突しない。
                    StoredColor("user:c1", "user:p1", "白", 0xFFFFFFFF.toInt()),
                    StoredColor("user:c2", "user:p2", "白", 0xFFFFFFFF.toInt()),
                ),
            ),
        )
        assertEquals(keys.size, keys.toSet().size)
    }

    @Test
    fun `非表示の色を含んでもキーが一意のまま`() {
        val keys = keysFor(
            StoredCatalog(
                hiddenColors = listOf(
                    HiddenColor(BuiltInCatalog.leatherId.value, "builtin:white"),
                ),
            ),
        )
        assertEquals(keys.size, keys.toSet().size)
    }

    @Test
    fun `ユーザーの色を足してもキーが一意のまま`() {
        val keys = keysFor(
            StoredCatalog(
                colors = listOf(
                    StoredColor("user:1", BuiltInCatalog.leatherId.value, "特注A", 0xFF112233.toInt()),
                    StoredColor("user:2", BuiltInCatalog.buttonId.value, "特注B", 0xFF445566.toInt()),
                ),
            ),
        )
        assertEquals(keys.size, keys.toSet().size)
    }
}
