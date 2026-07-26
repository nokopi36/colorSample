package com.nokopi.colorsample.data

import androidx.compose.ui.graphics.toArgb
import com.nokopi.colorsample.data.model.Catalog
import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.DisplayText
import com.nokopi.colorsample.data.model.Palette
import com.nokopi.colorsample.data.model.PaletteId
import com.nokopi.colorsample.data.model.resolveForTest
import com.nokopi.colorsample.data.store.HiddenColor
import com.nokopi.colorsample.data.store.StoredCatalog
import com.nokopi.colorsample.data.store.StoredColor
import com.nokopi.colorsample.data.store.StoredDevice
import com.nokopi.colorsample.data.store.StoredPalette
import com.nokopi.colorsample.data.store.StoredPart
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 色グループの追加・改名と、組み込み色の非表示。
 */
class PaletteMergeTest {

    private val leather = BuiltInCatalog.leatherId
    private val button = BuiltInCatalog.buttonId

    private fun palette(catalog: Catalog, id: PaletteId): Palette =
        catalog.palettes.first { it.id == id }

    private fun names(palette: Palette) = palette.options.map { it.label.resolveForTest() }

    // ---- 組み込み色の非表示 --------------------------------------------

    /**
     * この機能の本題。組み込みの色は複数グループで共有されている（白は8グループ）ので、
     * 色だけを指定して消すと他のグループからも消えてしまう。
     */
    @Test
    fun `非表示はグループ単位で他のグループの同じ色は残る`() {
        val white = ColorId("builtin:white")
        val catalog = CatalogMerger.merge(
            StoredCatalog(hiddenColors = listOf(HiddenColor(leather.value, white.value))),
        )

        assertFalse("革から白が消えていない", palette(catalog, leather).options.any { it.id == white })
        assertTrue("ボタンの白まで消えている", palette(catalog, button).options.any { it.id == white })
    }

    @Test
    fun `非表示にした色は hiddenOptions に入る`() {
        val white = ColorId("builtin:white")
        val catalog = CatalogMerger.merge(
            StoredCatalog(hiddenColors = listOf(HiddenColor(leather.value, white.value))),
        )
        val revised = palette(catalog, leather)

        assertEquals(listOf(white), revised.hiddenOptions.map { it.id })
        // 非表示にしていないグループでは空。
        assertTrue(palette(catalog, button).hiddenOptions.isEmpty())
    }

    @Test
    fun `複数の色をまとめて非表示にできる`() {
        val builtInLeather = BuiltInCatalog.palettes.first { it.id == leather }.options
        val catalog = CatalogMerger.merge(
            StoredCatalog(
                hiddenColors = builtInLeather.take(3).map {
                    HiddenColor(leather.value, it.id.value)
                },
            ),
        )
        val revised = palette(catalog, leather)

        assertEquals(builtInLeather.size - 3, revised.options.size)
        assertEquals(3, revised.hiddenOptions.size)
    }

    /**
     * 空のグループは Palette が受け付けないし、あっても操作できない。
     * 壊れた保存データを読んだときは指定を無視して表示できる状態に戻す。
     */
    @Test
    fun `全部消えてしまう非表示指定は無視する`() {
        val whiteBlack = BuiltInCatalog.whiteBlackId
        val all = BuiltInCatalog.palettes.first { it.id == whiteBlack }.options
        val catalog = CatalogMerger.merge(
            StoredCatalog(
                hiddenColors = all.map { HiddenColor(whiteBlack.value, it.id.value) },
            ),
        )
        val revised = palette(catalog, whiteBlack)

        assertEquals("色が消えている", all.size, revised.options.size)
        // 効いていないので「戻す」も出さない。
        assertTrue(revised.hiddenOptions.isEmpty())
    }

    @Test
    fun `知らないグループ宛の非表示は無害`() {
        val catalog = CatalogMerger.merge(
            StoredCatalog(hiddenColors = listOf(HiddenColor("not_a_palette", "builtin:white"))),
        )
        assertEquals(BuiltInCatalog.palettes.size, catalog.palettes.size)
        assertTrue(catalog.palettes.all { it.hiddenOptions.isEmpty() })
    }

    @Test
    fun `ユーザーが足した色も非表示にできる`() {
        val catalog = CatalogMerger.merge(
            StoredCatalog(
                colors = listOf(StoredColor("user:c1", leather.value, "特注", 0xFF112233.toInt())),
                hiddenColors = listOf(HiddenColor(leather.value, "user:c1")),
            ),
        )
        val revised = palette(catalog, leather)

        assertFalse(revised.options.any { it.id == ColorId("user:c1") })
        assertEquals(listOf(ColorId("user:c1")), revised.hiddenOptions.map { it.id })
    }

    // ---- グループの改名 ------------------------------------------------

    @Test
    fun `組み込みグループの名前を付け替えられる`() {
        val catalog = CatalogMerger.merge(
            StoredCatalog(paletteNames = mapOf(leather.value to "レザー")),
        )
        assertEquals("レザー", palette(catalog, leather).label.resolveForTest())
        // 他のグループは変わらない。
        assertTrue(palette(catalog, button).label is DisplayText.Res)
    }

    @Test
    fun `改名しても中身の色は変わらない`() {
        val before = names(BuiltInCatalog.palettes.first { it.id == leather })
        val catalog = CatalogMerger.merge(
            StoredCatalog(paletteNames = mapOf(leather.value to "レザー")),
        )
        assertEquals(before, names(palette(catalog, leather)))
    }

    @Test
    fun `組み込みグループは削除できない印を持つ`() {
        val catalog = CatalogMerger.merge(StoredCatalog.EMPTY)
        assertTrue(catalog.palettes.none { it.isUserDefined })
    }

    // ---- ユーザー定義グループ ------------------------------------------

    private val magicTape = StoredPalette(id = "user:p1", name = "マジックテープ")

    private fun withMagicTape(
        colors: List<StoredColor> = listOf(
            StoredColor("user:c1", "user:p1", "生成り", 0xFFEFE4D0.toInt()),
        ),
        hidden: List<HiddenColor> = emptyList(),
    ) = CatalogMerger.merge(
        StoredCatalog(palettes = listOf(magicTape), colors = colors, hiddenColors = hidden),
    )

    @Test
    fun `ユーザーのグループは組み込みの後ろに並ぶ`() {
        val catalog = withMagicTape()

        assertEquals(BuiltInCatalog.palettes.size + 1, catalog.palettes.size)
        val added = catalog.palettes.last()
        assertEquals(PaletteId("user:p1"), added.id)
        assertEquals("マジックテープ", added.label.resolveForTest())
        assertTrue(added.isUserDefined)
        assertEquals(listOf("生成り"), names(added))
        assertEquals(0xFFEFE4D0.toInt(), added.options.single().color.toArgb())
    }

    @Test
    fun `色が1つも無いユーザーグループは出さない`() {
        // 空の Palette は作れないので、保存データが壊れていても出さない。
        val catalog = CatalogMerger.merge(StoredCatalog(palettes = listOf(magicTape)))
        assertEquals(BuiltInCatalog.palettes.size, catalog.palettes.size)
    }

    @Test
    fun `ユーザーグループでも全消しの非表示は無視する`() {
        val catalog = withMagicTape(hidden = listOf(HiddenColor("user:p1", "user:c1")))
        val added = catalog.palettes.last()

        assertEquals(1, added.options.size)
        assertTrue(added.hiddenOptions.isEmpty())
    }

    @Test
    fun `ユーザーグループの色は追加した順に並ぶ`() {
        val catalog = withMagicTape(
            colors = listOf(
                StoredColor("user:c1", "user:p1", "生成り", 0xFFEFE4D0.toInt()),
                StoredColor("user:c2", "user:p1", "濃紺", 0xFF203040.toInt()),
            ),
        )
        assertEquals(listOf("生成り", "濃紺"), names(catalog.palettes.last()))
    }

    // ---- 使用状況の照会 ------------------------------------------------

    @Test
    fun `組み込み装具からの使用を拾える`() {
        val catalog = CatalogMerger.merge(StoredCatalog.EMPTY)
        val usages = catalog.usages(leather)

        assertTrue(usages.isNotEmpty())
        assertTrue("革を使う装具しか出てこないはず", usages.all { it.part.paletteId == leather })
    }

    @Test
    fun `ユーザー装具からの使用も拾える`() {
        val catalog = CatalogMerger.merge(
            StoredCatalog(
                palettes = listOf(magicTape),
                colors = listOf(StoredColor("user:c1", "user:p1", "生成り", 0xFFEFE4D0.toInt())),
                devices = listOf(
                    StoredDevice(
                        id = "user:d1",
                        name = "自作装具",
                        parts = listOf(StoredPart("user:pt1", "留め具", "a.png", "user:p1")),
                    ),
                ),
            ),
        )
        val usages = catalog.usages(PaletteId("user:p1"))

        assertEquals(1, usages.size)
        assertEquals("自作装具", usages.single().device.label.resolveForTest())
        assertEquals("留め具", usages.single().part.label.resolveForTest())
    }

    @Test
    fun `誰も使っていないグループは空を返す`() {
        assertTrue(withMagicTape().usages(PaletteId("user:p1")).isEmpty())
    }

    @Test
    fun `色を変えないレイヤーは使用に数えない`() {
        val catalog = CatalogMerger.merge(
            StoredCatalog(
                palettes = listOf(magicTape),
                colors = listOf(StoredColor("user:c1", "user:p1", "生成り", 0xFFEFE4D0.toInt())),
                devices = listOf(
                    StoredDevice(
                        id = "user:d1",
                        name = "自作装具",
                        parts = listOf(
                            StoredPart("user:pt1", "本体", "a.png", "user:p1"),
                            StoredPart("user:pt2", "線画", "line.png", null),
                        ),
                    ),
                ),
            ),
        )
        assertEquals(1, catalog.usages(PaletteId("user:p1")).size)
    }

    // ---- 不変条件 ------------------------------------------------------

    @Test
    fun `どのグループも必ず1色以上ある`() {
        // Palette の init で守っているが、マージ経路でも成り立つことを押さえる。
        val catalog = withMagicTape(
            hidden = BuiltInCatalog.palettes.flatMap { p ->
                p.options.map { HiddenColor(p.id.value, it.id.value) }
            },
        )
        assertTrue(catalog.palettes.all { it.options.isNotEmpty() })
    }
}
