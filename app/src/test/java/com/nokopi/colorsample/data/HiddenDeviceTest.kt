package com.nokopi.colorsample.data

import com.nokopi.colorsample.data.model.DeviceId
import com.nokopi.colorsample.data.model.resolveForTest
import com.nokopi.colorsample.data.store.StoredCatalog
import com.nokopi.colorsample.data.store.StoredColor
import com.nokopi.colorsample.data.store.StoredDevice
import com.nokopi.colorsample.data.store.StoredPart
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 装具をホームから外す（非表示）機能。
 *
 * 組み込みの装具はコード上の定義を消せないので、色と同じく「見せない」ことで削除を表す。
 */
class HiddenDeviceTest {

    private val nb = DeviceId("builtin:nb")
    private val a = DeviceId("builtin:a")

    private fun merge(vararg hidden: String, extra: StoredCatalog = StoredCatalog.EMPTY) =
        CatalogMerger.merge(extra.copy(hiddenDevices = hidden.toList()))

    @Test
    fun `非表示にした装具は一覧から消える`() {
        val catalog = merge(nb.value)

        assertEquals(BuiltInCatalog.devices.size - 1, catalog.devices.size)
        assertTrue(catalog.devices.none { it.id == nb })
    }

    @Test
    fun `非表示にした装具は内容ごと hiddenDevices に入る`() {
        val catalog = merge(nb.value)

        assertEquals(1, catalog.hiddenDevices.size)
        val hidden = catalog.hiddenDevices.single()
        assertEquals(nb, hidden.id)
        // 戻すときに名前とサムネイルを出したいので、中身が残っていること。
        assertTrue(hidden.parts.isNotEmpty())
    }

    @Test
    fun `他の装具には影響しない`() {
        val catalog = merge(nb.value)
        assertTrue(catalog.devices.any { it.id == a })
    }

    @Test
    fun `複数まとめて非表示にできる`() {
        val catalog = merge(nb.value, a.value)

        assertEquals(BuiltInCatalog.devices.size - 2, catalog.devices.size)
        assertEquals(setOf(nb, a), catalog.hiddenDevices.mapTo(mutableSetOf()) { it.id })
    }

    /**
     * 色のパレットとは違い、装具は全部隠しても落ちない（空のホームになるだけ）。
     * 画面側はメニューから戻せるようにしてある。
     */
    @Test
    fun `全部非表示にしても壊れない`() {
        val catalog = merge(*BuiltInCatalog.devices.map { it.id.value }.toTypedArray())

        assertTrue(catalog.devices.isEmpty())
        assertEquals(BuiltInCatalog.devices.size, catalog.hiddenDevices.size)
        // 色は無関係なので残っている。
        assertEquals(BuiltInCatalog.palettes.size, catalog.palettes.size)
    }

    @Test
    fun `非表示にすると ID から引けなくなる`() {
        // 表示中に非表示にされた画面が NotFound になって戻れるのは、この振る舞いによる。
        assertNull(merge(nb.value).device(nb))
    }

    @Test
    fun `知らないIDの非表示指定は無害`() {
        val catalog = merge("builtin:does_not_exist")

        assertEquals(BuiltInCatalog.devices.size, catalog.devices.size)
        assertTrue(catalog.hiddenDevices.isEmpty())
    }

    @Test
    fun `ユーザーが作った装具も非表示にできる`() {
        val stored = StoredCatalog(
            devices = listOf(
                StoredDevice(
                    id = "user:d1",
                    name = "自作装具",
                    parts = listOf(
                        StoredPart("user:p1", "本体", "p1.png", BuiltInCatalog.leatherId.value),
                    ),
                ),
            ),
        )
        val catalog = merge("user:d1", extra = stored)

        assertEquals(BuiltInCatalog.devices.size, catalog.devices.size)
        assertEquals("自作装具", catalog.hiddenDevices.single().label.resolveForTest())
    }

    @Test
    fun `非表示指定が無ければ hiddenDevices は空`() {
        val catalog = CatalogMerger.merge(StoredCatalog.EMPTY)

        assertTrue(catalog.hiddenDevices.isEmpty())
        assertEquals(BuiltInCatalog.devices.size, catalog.devices.size)
    }

    @Test
    fun `装具の非表示は色に影響しない`() {
        val catalog = merge(
            nb.value,
            extra = StoredCatalog(
                colors = listOf(
                    StoredColor("user:c1", BuiltInCatalog.leatherId.value, "特注", 0xFF112233.toInt()),
                ),
            ),
        )
        val leather = catalog.palette(BuiltInCatalog.leatherId)
        assertTrue(leather.options.any { it.id.value == "user:c1" })
    }

    @Test
    fun `表示の並び順は変わらない`() {
        // 途中の装具を隠しても、残りの並びは組み込みの宣言順のまま。
        val catalog = merge(BuiltInCatalog.devices[2].id.value)
        val expected = BuiltInCatalog.devices.filterIndexed { i, _ -> i != 2 }.map { it.id }

        assertEquals(expected, catalog.devices.map { it.id })
    }
}
