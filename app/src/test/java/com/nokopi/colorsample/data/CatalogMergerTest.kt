package com.nokopi.colorsample.data

import androidx.compose.ui.graphics.toArgb
import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.DisplayText
import com.nokopi.colorsample.data.model.PartImage
import com.nokopi.colorsample.data.store.StoredCatalog
import com.nokopi.colorsample.data.store.StoredColor
import com.nokopi.colorsample.data.store.StoredDevice
import com.nokopi.colorsample.data.store.StoredPart
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogMergerTest {

    private val leather = BuiltInCatalog.leatherId
    private val builtInLeatherSize =
        BuiltInCatalog.palettes.first { it.id == leather }.options.size

    private fun userColor(
        id: String = "user:1",
        paletteId: String = leather.value,
        name: String = "特注ベージュ",
        argb: Int = 0xFFEEDDCC.toInt(),
    ) = StoredColor(id = id, paletteId = paletteId, name = name, argb = argb)

    @Test
    fun `ユーザーの色は組み込みの後ろに足される`() {
        val catalog = CatalogMerger.merge(StoredCatalog(colors = listOf(userColor())))
        val options = catalog.palette(leather).options

        assertEquals(builtInLeatherSize + 1, options.size)
        // 組み込みの並びが動かないので、既存の配色の見え方が色の追加で変わらない。
        assertTrue(options.take(builtInLeatherSize).all { it.isBuiltIn })

        val added = options.last()
        assertEquals(ColorId("user:1"), added.id)
        assertEquals(DisplayText.Literal("特注ベージュ"), added.label)
        assertEquals(0xFFEEDDCC.toInt(), added.color.toArgb())
    }

    @Test
    fun `他のパレットには影響しない`() {
        val catalog = CatalogMerger.merge(StoredCatalog(colors = listOf(userColor())))
        val buttons = catalog.palette(BuiltInCatalog.buttonId).options
        assertTrue(buttons.all { it.isBuiltIn })
    }

    @Test
    fun `知らないパレット宛の色は無視する`() {
        val catalog = CatalogMerger.merge(
            StoredCatalog(colors = listOf(userColor(paletteId = "not_a_palette"))),
        )
        assertTrue(catalog.palettes.flatMap { it.options }.all { it.isBuiltIn })
    }

    @Test
    fun `保存が空なら組み込みだけになる`() {
        val catalog = CatalogMerger.merge(StoredCatalog.EMPTY)
        assertEquals(BuiltInCatalog.devices.size, catalog.devices.size)
        assertTrue(catalog.palettes.flatMap { it.options }.all { it.isBuiltIn })
    }

    // ---- 装具（Phase 2 で使う経路。マージの筋だけ先に固めておく）----------

    private fun storedDevice(
        id: String = "user:dev1",
        parts: List<StoredPart> = listOf(
            StoredPart("user:p1", "本体", "p1.png", leather.value),
            StoredPart("user:p2", "ベルト", "p2.png", leather.value),
        ),
        overlay: String? = "line.png",
    ) = StoredDevice(id = id, name = "自作装具", parts = parts, overlayFileName = overlay)

    @Test
    fun `ユーザーの装具は組み込みの後ろに並びパスが解決される`() {
        val catalog = CatalogMerger.merge(StoredCatalog(devices = listOf(storedDevice())))

        assertEquals(BuiltInCatalog.devices.size + 1, catalog.devices.size)
        val device = catalog.devices.last()

        assertEquals(DisplayText.Literal("自作装具"), device.label)
        assertEquals(
            PartImage.Stored("devices/user:dev1/p1.png"),
            device.parts.first().image,
        )
        assertEquals(PartImage.Stored("devices/user:dev1/line.png"), device.overlay)
        // 線画があればサムネイルはそれを使う。
        assertEquals(device.overlay, device.thumbnail)
    }

    @Test
    fun `線画が無ければサムネイルは最背面のパーツになる`() {
        val catalog = CatalogMerger.merge(
            StoredCatalog(devices = listOf(storedDevice(overlay = null))),
        )
        val device = catalog.devices.last()

        assertNull(device.overlay)
        assertEquals(PartImage.Stored("devices/user:dev1/p1.png"), device.thumbnail)
    }

    @Test
    fun `パレットが解決できないパーツは落とす`() {
        val catalog = CatalogMerger.merge(
            StoredCatalog(
                devices = listOf(
                    storedDevice(
                        parts = listOf(
                            StoredPart("user:p1", "本体", "p1.png", leather.value),
                            StoredPart("user:p2", "謎", "p2.png", "not_a_palette"),
                        ),
                    ),
                ),
            ),
        )
        val device = catalog.devices.last()
        assertEquals(1, device.parts.size)
        assertEquals("本体", (device.parts.single().label as DisplayText.Literal).value)
    }

    @Test
    fun `パーツが1つも残らない装具は出さない`() {
        val catalog = CatalogMerger.merge(
            StoredCatalog(
                devices = listOf(
                    storedDevice(
                        parts = listOf(StoredPart("user:p1", "謎", "p1.png", "not_a_palette")),
                    ),
                ),
            ),
        )
        assertEquals(BuiltInCatalog.devices.size, catalog.devices.size)
    }

    @Test
    fun `device で ID から引ける`() {
        val catalog = CatalogMerger.merge(StoredCatalog(devices = listOf(storedDevice())))
        assertNotNull(catalog.device(catalog.devices.last().id))
        assertNull(catalog.device(com.nokopi.colorsample.data.model.DeviceId("user:missing")))
    }
}
