package com.nokopi.colorsample.data

import androidx.compose.ui.graphics.toArgb
import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.DeviceId
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

    // ---- 装具 ----------------------------------------------------------

    private fun part(id: String, file: String, palette: String? = leather.value) =
        StoredPart(id = id, name = "パーツ$id", fileName = file, paletteId = palette)

    private fun storedDevice(
        id: String = "user:dev1",
        parts: List<StoredPart> = listOf(
            part("user:p1", "p1.png"),
            part("user:p2", "p2.png"),
            // 色を変えないレイヤー（線画）
            part("user:p3", "line.png", palette = null),
        ),
    ) = StoredDevice(id = id, name = "自作装具", parts = parts)

    @Test
    fun `ユーザーの装具は組み込みの後ろに並びパスが解決される`() {
        val catalog = CatalogMerger.merge(StoredCatalog(devices = listOf(storedDevice())))

        assertEquals(BuiltInCatalog.devices.size + 1, catalog.devices.size)
        val device = catalog.devices.last()

        assertEquals(DisplayText.Literal("自作装具"), device.label)
        assertEquals(
            PartImage.Stored("devices/user_dev1/p1.png"),
            device.parts.first().image,
        )
    }

    @Test
    fun `並び順がそのまま保たれる`() {
        // 描画順そのものなので、ここが崩れると重なりが変わる。
        val catalog = CatalogMerger.merge(StoredCatalog(devices = listOf(storedDevice())))
        assertEquals(
            listOf("p1.png", "p2.png", "line.png"),
            catalog.devices.last().parts.map {
                (it.image as PartImage.Stored).relativePath.substringAfterLast('/')
            },
        )
    }

    @Test
    fun `パレットが null のレイヤーは色を変えない扱いになる`() {
        val catalog = CatalogMerger.merge(StoredCatalog(devices = listOf(storedDevice())))
        val device = catalog.devices.last()

        assertEquals(2, device.tintedParts.size)
        assertTrue("線画が色を変える扱いになっている", !device.parts.last().isTinted)
    }

    @Test
    fun `色を変えないレイヤーは何枚でも持てて途中にも置ける`() {
        // 以前は最前面に1枚だけという制約があった。
        val catalog = CatalogMerger.merge(
            StoredCatalog(
                devices = listOf(
                    storedDevice(
                        parts = listOf(
                            part("user:p1", "shadow.png", palette = null),
                            part("user:p2", "body.png"),
                            part("user:p3", "buckle.png", palette = null),
                            part("user:p4", "belt.png"),
                            part("user:p5", "line.png", palette = null),
                        ),
                    ),
                ),
            ),
        )
        val device = catalog.devices.last()

        assertEquals(5, device.parts.size)
        assertEquals(2, device.tintedParts.size)
        assertEquals(
            listOf(false, true, false, true, false),
            device.parts.map { it.isTinted },
        )
    }

    @Test
    fun `サムネイルは一番手前の色を変えないレイヤーを使う`() {
        val catalog = CatalogMerger.merge(StoredCatalog(devices = listOf(storedDevice())))
        assertEquals(
            PartImage.Stored("devices/user_dev1/line.png"),
            catalog.devices.last().thumbnail,
        )
    }

    @Test
    fun `色を変えないレイヤーが無ければ一番手前を使う`() {
        val catalog = CatalogMerger.merge(
            StoredCatalog(
                devices = listOf(
                    storedDevice(
                        parts = listOf(part("user:p1", "p1.png"), part("user:p2", "p2.png")),
                    ),
                ),
            ),
        )
        assertEquals(
            PartImage.Stored("devices/user_dev1/p2.png"),
            catalog.devices.last().thumbnail,
        )
    }

    @Test
    fun `パレットが解決できないレイヤーだけ落とす`() {
        val catalog = CatalogMerger.merge(
            StoredCatalog(
                devices = listOf(
                    storedDevice(
                        parts = listOf(
                            part("user:p1", "p1.png"),
                            part("user:p2", "p2.png", palette = "not_a_palette"),
                            part("user:p3", "line.png", palette = null),
                        ),
                    ),
                ),
            ),
        )
        val device = catalog.devices.last()
        // 色を変えない層 (null) は落とさず、解決できない指定だけ落ちる。
        assertEquals(2, device.parts.size)
        assertEquals(
            listOf("p1.png", "line.png"),
            device.parts.map {
                (it.image as PartImage.Stored).relativePath.substringAfterLast('/')
            },
        )
    }

    @Test
    fun `レイヤーが1つも残らない装具は出さない`() {
        val catalog = CatalogMerger.merge(
            StoredCatalog(
                devices = listOf(
                    storedDevice(
                        parts = listOf(part("user:p1", "p1.png", palette = "not_a_palette")),
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
        assertNull(catalog.device(DeviceId("user:missing")))
    }
}
