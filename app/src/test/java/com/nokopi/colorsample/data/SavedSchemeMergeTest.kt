package com.nokopi.colorsample.data

import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.DeviceId
import com.nokopi.colorsample.data.model.SchemeId
import com.nokopi.colorsample.data.store.StoredCatalog
import com.nokopi.colorsample.data.store.StoredColor
import com.nokopi.colorsample.data.store.StoredScheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 保存した配色の解決。
 *
 * 参照が切れていても落ちないこと、切れた参照が黙って別の色に化けないことを押さえる。
 */
class SavedSchemeMergeTest {

    private val nbId = DeviceId("builtin:nb")
    private val leather = BuiltInCatalog.leatherId

    private fun schemeOf(
        id: String = "user:s1",
        deviceId: String = nbId.value,
        selections: Map<String, String> = emptyMap(),
        personName: String = "",
    ) = StoredScheme(
        id = id,
        deviceId = deviceId,
        name = "運動会",
        personName = personName,
        selections = selections,
    )

    private fun merge(vararg schemes: StoredScheme, stored: StoredCatalog = StoredCatalog.EMPTY) =
        CatalogMerger.merge(stored.copy(schemes = schemes.toList()))

    @Test
    fun `配色は装具とパーツの色に解決される`() {
        val base = CatalogMerger.merge(StoredCatalog.EMPTY)
        val part = base.device(nbId)!!.parts.first { it.paletteId == leather }
        val color = base.palette(leather).options[4]

        val scheme = merge(
            schemeOf(selections = mapOf(part.id.value to color.id.value), personName = "太郎"),
        ).schemes.single()

        assertEquals(SchemeId("user:s1"), scheme.id)
        assertEquals(nbId, scheme.device.id)
        assertEquals("太郎", scheme.personName)
        assertEquals(color, scheme.selections.first { it.part.id == part.id }.option)
    }

    /** 選択欄に出ないレイヤーは配色にも並ばない。 */
    @Test
    fun `色を変えないレイヤーは配色に含まれない`() {
        val scheme = merge(schemeOf()).schemes.single()
        val device = scheme.device

        assertEquals(device.tintedParts.size, scheme.selections.size)
        assertTrue(scheme.selections.all { it.part.isTinted })
        // 並びは装具の描画順のまま。
        assertEquals(device.tintedParts.map { it.id }, scheme.selections.map { it.part.id })
    }

    @Test
    fun `装具が見つからない配色は落ちる`() {
        assertTrue(merge(schemeOf(deviceId = "user:missing")).schemes.isEmpty())
    }

    /**
     * 非表示にした装具の配色も一覧から消える。開いても [com.nokopi.colorsample.data.model.Catalog.device]
     * が引けず、配色画面がすぐ戻ってしまうため。装具を戻せば配色も戻る。
     */
    @Test
    fun `非表示にした装具の配色は出ないが、戻せば復活する`() {
        val hidden = StoredCatalog(hiddenDevices = listOf(nbId.value))
        assertTrue(merge(schemeOf(), stored = hidden).schemes.isEmpty())

        assertEquals(1, merge(schemeOf(), stored = StoredCatalog.EMPTY).schemes.size)
    }

    /** 消えた色を指していたら、配色画面と同じくパレット先頭に落ちる。 */
    @Test
    fun `解決できない色はパレット先頭になる`() {
        val base = CatalogMerger.merge(StoredCatalog.EMPTY)
        val part = base.device(nbId)!!.parts.first { it.paletteId == leather }

        val scheme = merge(
            schemeOf(selections = mapOf(part.id.value to "user:deleted")),
        ).schemes.single()

        val selection = scheme.selections.first { it.part.id == part.id }
        assertEquals(base.palette(leather).options.first(), selection.option)
    }

    /** 装具にあとからレイヤーが増えても、指定の無いパーツは先頭に落ちるだけで壊れない。 */
    @Test
    fun `指定の無いパーツはパレット先頭になる`() {
        val scheme = merge(schemeOf(selections = emptyMap())).schemes.single()

        assertTrue(scheme.selections.isNotEmpty())
        assertTrue(scheme.selections.all { it.option == scheme.selectionPalette(it.part.id) })
    }

    // ---- 色を消す前の確認に使う判定 ------------------------------------

    /**
     * 組み込みの色は複数グループで共有されている（黒は9グループ）。グループを見ずに
     * 色だけで判定すると、革の黒を消すときにボタンの黒を使う配色まで巻き込む。
     *
     * 全パーツを明示的に埋めてから1か所だけ差し替える。指定を省くとそのパーツは
     * パレット先頭に落ち、狙っていない側でも同じ色を「使っている」ことになってしまう。
     */
    @Test
    fun `色を使っている配色はグループ単位で引く`() {
        val base = CatalogMerger.merge(StoredCatalog.EMPTY)
        val device = base.device(nbId)!!
        val leatherPart = device.parts.first { it.paletteId == leather }
        val otherPart = device.parts.first { it.paletteId != null && it.paletteId != leather }
        val otherPaletteId = otherPart.paletteId!!
        // 両グループに入っている色。共有されている色でないとこのテストの意味がない。
        val shared = base.palette(leather).options
            .first { option -> base.palette(otherPaletteId).options.any { it.id == option.id } }

        // どのパーツも「その色ではない」状態を土台にする。
        val baseline = device.parts
            .mapNotNull { part -> part.paletteId?.let { part to base.palette(it) } }
            .associate { (part, palette) ->
                part.id.value to palette.options.first { it.id != shared.id }.id.value
            }

        val catalog = merge(
            schemeOf(id = "user:leather", selections = baseline + (leatherPart.id.value to shared.id.value)),
            schemeOf(id = "user:other", selections = baseline + (otherPart.id.value to shared.id.value)),
        )

        assertEquals(
            listOf(SchemeId("user:leather")),
            catalog.schemesUsing(leather, shared.id).map { it.id },
        )
        assertEquals(
            listOf(SchemeId("user:other")),
            catalog.schemesUsing(otherPaletteId, shared.id).map { it.id },
        )
    }

    /**
     * 指定を省いたパーツも「その色を使っている」に入る。先頭に落ちて実際にその色で
     * 表示されているので、消せば見え方が変わる。
     */
    @Test
    fun `指定の無いパーツが落ちた先の色も使用扱いになる`() {
        val base = CatalogMerger.merge(StoredCatalog.EMPTY)
        val first = base.palette(leather).options.first()

        val catalog = merge(schemeOf(selections = emptyMap()))

        assertEquals(1, catalog.schemesUsing(leather, first.id).size)
    }

    /** 報告された手順そのまま。革に色を足す → その色で配色を保存 → 消す前に引けるか。 */
    @Test
    fun `革に足した色を使う配色を引ける`() {
        val userColor = StoredColor("user:c1", leather.value, "特注", 0xFF123456.toInt())
        val withColor = StoredCatalog(colors = listOf(userColor))
        val part = CatalogMerger.merge(withColor).device(nbId)!!.parts.first {
            it.paletteId == leather
        }

        val catalog = CatalogMerger.merge(
            withColor.copy(
                schemes = listOf(schemeOf(selections = mapOf(part.id.value to "user:c1"))),
            ),
        )

        assertEquals(1, catalog.schemesUsing(leather, ColorId("user:c1")).size)
    }

    @Test
    fun `使われていない色は空で返る`() {
        val base = CatalogMerger.merge(StoredCatalog.EMPTY)
        val part = base.device(nbId)!!.parts.first { it.paletteId == leather }
        val used = base.palette(leather).options.first()
        val unused = base.palette(leather).options.last()

        val catalog = merge(schemeOf(selections = mapOf(part.id.value to used.id.value)))

        assertTrue(catalog.schemesUsing(leather, unused.id).isEmpty())
        assertEquals(1, catalog.schemesUsing(leather, used.id).size)
    }

    @Test
    fun `ID で引ける`() {
        val catalog = merge(schemeOf(id = "user:a"), schemeOf(id = "user:b"))

        assertEquals("user:a", catalog.scheme(SchemeId("user:a"))?.id?.value)
        assertNull(catalog.scheme(SchemeId("user:none")))
        assertEquals(2, catalog.schemesOf(nbId).size)
    }

    /** そのパーツのパレット先頭の色。 */
    private fun com.nokopi.colorsample.data.model.SavedScheme.selectionPalette(
        partId: com.nokopi.colorsample.data.model.PartId,
    ) = CatalogMerger.merge(StoredCatalog.EMPTY)
        .palette(device.parts.first { it.id == partId }.paletteId!!)
        .options
        .first()
}
