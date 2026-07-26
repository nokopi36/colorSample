package com.nokopi.colorsample.data

import androidx.compose.ui.graphics.toArgb
import com.nokopi.colorsample.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 移行前の Activity / レイアウト XML / ChangeColors に散らばっていた定義を
 * [DeviceType] と [Palette] へ写したときに取りこぼしがないかを固定するテスト。
 *
 * 期待値は移行前のソースから独立に書き起こしてある。
 */
class DeviceCatalogTest {

    // ---- パレット --------------------------------------------------------

    /** 移行前の `CustomColor` の hex を、`ChangeColors` の各リストの順で並べたもの。 */
    private val expectedPalettes: Map<Palette, List<Long>> = mapOf(
        Palette.LEATHER to listOf(
            0xFFFFFFFF, // white
            0xFFFFDB4F, // kikutinashi
            0xFFFF0000, // red
            0xFFFDDEA5, // hatimitu
            0xFFFFCCCB, // mumei
            0xFF47266E, // deeproyalpurple
            0xFF001E43, // midnightblue
            0xFF0000FF, // blue
            0xFF2E8B57, // seagreen
            0xFF281A14, // tetuguro
            0xFF000000, // black
        ),
        Palette.BAND to listOf(
            0xFFF5B2B2, // pink
            0xFF0000FF, // blue
            0xFF87CEFA, // lightskyblue
            0xFF000000, // black
        ),
        Palette.SPONGE to listOf(
            0xFFFFFFFF, // white
            0xFFF4A460, // sandybrown
            0xFF000000, // black
            0xFFFF8C00, // orange
            0xFFF5B2B2, // pink
            0xFF4496D3, // zenithblue
            0xFF7FFFD4, // aquamarine
            0xFF7CFC00, // lightgreen
        ),
        Palette.PLASTIC to listOf(
            0xFFFFFFFF, // white
            0xFFFF0000, // red
            0xFF0000FF, // blue
            0xFF008000, // green
            0xFF000000, // black
        ),
        Palette.STRING to listOf(
            0xFFFFFFFF, // white
            0xFFFFFF00, // yellow
            0xFFFF8C00, // orange
            0xFFFF0000, // red
            0xFFE8D3CA, // beigerose
            0xFFFFCCCB, // mumei
            0xFF7B68EE, // mediumslateblue
            0xFF001E43, // midnightblue
            0xFF0000FF, // blue
            0xFF87CEFA, // lightskyblue
            0xFF008000, // green
            0xFF7CFC00, // lightgreen
            0xFF281A14, // tetuguro
            0xFF000000, // black
        ),
        Palette.BUTTON to listOf(
            0xFFFFFFFF, // white
            0xFFFFDB4F, // kikutinashi
            0xFFFF0000, // red
            0xFFE8C59C, // vanilla
            0xFFFFCCCB, // mumei
            0xFF0000FF, // blue
            0xFF008000, // green
            0xFF281A14, // tetuguro
            0xFF000000, // black
        ),
        Palette.ORTHOSIS_PLASTIC to listOf(
            0xFFFFFFFF, // white
            0xFFFF0000, // red
            0xFFF9C89B, // yuou
            0xFFF19CA7, // rosepink
            0xFF0000FF, // blue
            0xFF87CEFA, // lightskyblue
            0xFF008000, // green
            0xFF6F4B3E, // kogetya
            0xFF000000, // black
        ),
        Palette.PL_SPONGE to listOf(
            0xFFFFFFFF, // white
            0xFFFF8C00, // orange
            0xFFF5B2B2, // pink
            0xFF4496D3, // zenithblue
            0xFF7FFFD4, // aquamarine
            0xFF7CFC00, // lightgreen
            0xFF000000, // black
        ),
        Palette.WHITE_BLACK to listOf(
            0xFFFFFFFF, // white
            0xFF000000, // black
        ),
    )

    @Test
    fun `パレットの色と並びが移行前と一致する`() {
        for (palette in Palette.entries) {
            val expected = requireNotNull(expectedPalettes[palette]) {
                "期待値が定義されていないパレット: $palette"
            }
            val actual = palette.options.map { it.color.toArgb().toLong() and 0xFFFFFFFFL }
            assertEquals("$palette の色", expected, actual)
        }
    }

    @Test
    fun `全てのパレットに期待値が用意されている`() {
        assertEquals(Palette.entries.toSet(), expectedPalettes.keys)
    }

    // ---- 装具 ------------------------------------------------------------

    /** 移行前の各 Activity / レイアウトから読み取ったパーツ定義。 */
    private val expectedParts: Map<DeviceType, List<Triple<Int, Int, Palette>>> = mapOf(
        DeviceType.NB to listOf(
            Triple(R.string.plastic, R.drawable.nb1, Palette.ORTHOSIS_PLASTIC),
            Triple(R.string.sponge, R.drawable.nb2, Palette.SPONGE),
            Triple(R.string.belt1, R.drawable.nb3, Palette.LEATHER),
            Triple(R.string.belt2, R.drawable.nb4, Palette.LEATHER),
            Triple(R.string.belt3, R.drawable.nb5, Palette.LEATHER),
            Triple(R.string.felt, R.drawable.nb6, Palette.WHITE_BLACK),
            Triple(R.string.string, R.drawable.nb7, Palette.STRING),
            Triple(R.string.button, R.drawable.nb8, Palette.BUTTON),
        ),
        DeviceType.FTN to listOf(
            Triple(R.string.body, R.drawable.ftn1, Palette.LEATHER),
            Triple(R.string.kahuband, R.drawable.ftn2, Palette.LEATHER),
            Triple(R.string.belt1, R.drawable.ftn3, Palette.LEATHER),
            Triple(R.string.belt2, R.drawable.ftn4, Palette.LEATHER),
            Triple(R.string.belt3, R.drawable.ftn5, Palette.LEATHER),
            Triple(R.string.felt, R.drawable.ftn6, Palette.WHITE_BLACK),
            Triple(R.string.shoe_sole, R.drawable.ftn7, Palette.WHITE_BLACK),
            Triple(R.string.button, R.drawable.ftn8, Palette.BUTTON),
            Triple(R.string.string, R.drawable.ftn9, Palette.STRING),
        ),
        DeviceType.SLB to listOf(
            Triple(R.string.plastic, R.drawable.slb1, Palette.ORTHOSIS_PLASTIC),
            Triple(R.string.sponge, R.drawable.slb2, Palette.SPONGE),
            Triple(R.string.kahuband, R.drawable.slb3, Palette.LEATHER),
            Triple(R.string.belt1, R.drawable.slb4, Palette.LEATHER),
            Triple(R.string.belt2, R.drawable.slb5, Palette.LEATHER),
            Triple(R.string.felt, R.drawable.slb6, Palette.WHITE_BLACK),
            Triple(R.string.shoe_sole, R.drawable.slb7, Palette.WHITE_BLACK),
            Triple(R.string.string, R.drawable.slb8, Palette.STRING),
            Triple(R.string.button, R.drawable.slb9, Palette.BUTTON),
        ),
        DeviceType.PL to listOf(
            Triple(R.string.kan, R.drawable.pl5, Palette.WHITE_BLACK),
            Triple(R.string.sponge, R.drawable.pl2, Palette.PL_SPONGE),
            Triple(R.string.belt, R.drawable.pl3, Palette.BAND),
            Triple(R.string.button, R.drawable.pl4, Palette.BUTTON),
            Triple(R.string.plastic, R.drawable.pl1, Palette.PLASTIC),
        ),
        DeviceType.POGO to listOf(
            Triple(R.string.plastic, R.drawable.pogo1, Palette.ORTHOSIS_PLASTIC),
            // 移行前は pogo2 も「ベルト2」表記だったが、pogo3/pogo4 と重複するため
            // 誤記と判断して「ベルト1」に直してある。
            Triple(R.string.belt1, R.drawable.pogo2, Palette.LEATHER),
            Triple(R.string.belt2, R.drawable.pogo3, Palette.LEATHER),
            Triple(R.string.belt3, R.drawable.pogo4, Palette.LEATHER),
            Triple(R.string.ankle_band, R.drawable.pogo5, Palette.LEATHER),
            Triple(R.string.belt4, R.drawable.pogo6, Palette.LEATHER),
            Triple(R.string.button, R.drawable.pogo7, Palette.BUTTON),
            Triple(R.string.string, R.drawable.pogo8, Palette.STRING),
        ),
        DeviceType.A to listOf(
            Triple(R.string.plastic, R.drawable.a1, Palette.ORTHOSIS_PLASTIC),
            Triple(R.string.sponge, R.drawable.a2, Palette.WHITE_BLACK),
            Triple(R.string.belt1, R.drawable.a3, Palette.LEATHER),
            Triple(R.string.belt2, R.drawable.a4, Palette.LEATHER),
            Triple(R.string.belt3, R.drawable.a5, Palette.LEATHER),
            Triple(R.string.belt4, R.drawable.a6, Palette.LEATHER),
            Triple(R.string.belt5, R.drawable.a7, Palette.LEATHER),
            Triple(R.string.felt, R.drawable.a8, Palette.WHITE_BLACK),
            Triple(R.string.button, R.drawable.a9, Palette.BUTTON),
            Triple(R.string.string, R.drawable.a10, Palette.STRING),
        ),
    )

    private val expectedOverlays: Map<DeviceType, Int> = mapOf(
        DeviceType.NB to R.drawable.nb9,
        DeviceType.FTN to R.drawable.ftn10,
        DeviceType.SLB to R.drawable.slb10,
        DeviceType.PL to R.drawable.pl6,
        DeviceType.POGO to R.drawable.pogo9,
        DeviceType.A to R.drawable.a11,
    )

    @Test
    fun `装具ごとのパーツ定義が移行前と一致する`() {
        for (device in DeviceType.entries) {
            val expected = requireNotNull(expectedParts[device]) {
                "期待値が定義されていない装具: $device"
            }
            val actual = device.parts.map { Triple(it.labelRes, it.image, it.palette) }
            assertEquals("$device のパーツ", expected, actual)
        }
    }

    @Test
    fun `装具ごとの最前面レイヤーが移行前と一致する`() {
        for (device in DeviceType.entries) {
            assertEquals("$device の overlay", expectedOverlays[device], device.overlay)
        }
    }

    @Test
    fun `6種類すべてが定義されている`() {
        assertEquals(6, DeviceType.entries.size)
        assertEquals(DeviceType.entries.toSet(), expectedParts.keys)
    }

    @Test
    fun `パーツ画像が装具内で重複せず overlay とも重ならない`() {
        for (device in DeviceType.entries) {
            val images = device.parts.map { it.image }
            assertEquals("$device のパーツ画像に重複がある", images.size, images.toSet().size)
            assertTrue(
                "$device の overlay がパーツ画像と重複している",
                device.overlay !in images,
            )
        }
    }

    @Test
    fun `全パーツに選べる色がありサムネイルとタイトルを持つ`() {
        for (device in DeviceType.entries) {
            assertTrue("$device の titleRes", device.titleRes != 0)
            assertTrue("$device の thumbnail", device.thumbnail != 0)
            for (part in device.parts) {
                assertTrue(
                    "$device の ${part.labelRes} に色がない",
                    part.palette.options.isNotEmpty(),
                )
            }
        }
    }
}
