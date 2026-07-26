package com.nokopi.colorsample.data

import androidx.compose.ui.graphics.toArgb
import com.nokopi.colorsample.R
import com.nokopi.colorsample.data.model.DisplayText
import com.nokopi.colorsample.data.model.PaletteId
import com.nokopi.colorsample.data.model.PartImage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 組み込みカタログを固定するテスト。
 *
 * 期待値は Compose 移行前の Activity / レイアウト XML / ChangeColors から独立に書き起こしてある。
 * カタログをリポジトリ方式に作り替えたあとも、この内容が変わっていないことを保証する。
 */
class BuiltInCatalogTest {

    private fun paletteArgb(id: PaletteId): List<Long> =
        BuiltInCatalog.palettes.first { it.id == id }
            .options.map { it.color.toArgb().toLong() and 0xFFFFFFFFL }

    @Test
    fun `革のパレットが移行前と一致する`() {
        assertEquals(
            listOf(
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
            paletteArgb(BuiltInCatalog.leatherId),
        )
    }

    @Test
    fun `糸のパレットが移行前と一致する`() {
        assertEquals(
            listOf(
                0xFFFFFFFF, 0xFFFFFF00, 0xFFFF8C00, 0xFFFF0000, 0xFFE8D3CA, 0xFFFFCCCB,
                0xFF7B68EE, 0xFF001E43, 0xFF0000FF, 0xFF87CEFA, 0xFF008000, 0xFF7CFC00,
                0xFF281A14, 0xFF000000,
            ),
            paletteArgb(BuiltInCatalog.stringId),
        )
    }

    @Test
    fun `残りのパレットの色数と両端が移行前と一致する`() {
        val expected = mapOf(
            BuiltInCatalog.plasticId to Triple(5, 0xFFFFFFFFL, 0xFF000000L),
            BuiltInCatalog.orthosisPlasticId to Triple(9, 0xFFFFFFFFL, 0xFF000000L),
            BuiltInCatalog.spongeId to Triple(8, 0xFFFFFFFFL, 0xFF7CFC00L),
            BuiltInCatalog.plSpongeId to Triple(7, 0xFFFFFFFFL, 0xFF000000L),
            BuiltInCatalog.bandId to Triple(4, 0xFFF5B2B2L, 0xFF000000L),
            BuiltInCatalog.buttonId to Triple(9, 0xFFFFFFFFL, 0xFF000000L),
            BuiltInCatalog.whiteBlackId to Triple(2, 0xFFFFFFFFL, 0xFF000000L),
        )
        for ((id, spec) in expected) {
            val (size, first, last) = spec
            val actual = paletteArgb(id)
            assertEquals("${id.value} の色数", size, actual.size)
            assertEquals("${id.value} の先頭", first, actual.first())
            assertEquals("${id.value} の末尾", last, actual.last())
        }
    }

    @Test
    fun `装具ごとのパーツ定義が移行前と一致する`() {
        // (ラベル, 画像, パレット) を装具ごとに並べたもの。
        val expected = mapOf(
            "builtin:nb" to listOf(
                Triple(R.string.plastic, R.drawable.nb1, BuiltInCatalog.orthosisPlasticId),
                Triple(R.string.sponge, R.drawable.nb2, BuiltInCatalog.spongeId),
                Triple(R.string.belt1, R.drawable.nb3, BuiltInCatalog.leatherId),
                Triple(R.string.belt2, R.drawable.nb4, BuiltInCatalog.leatherId),
                Triple(R.string.belt3, R.drawable.nb5, BuiltInCatalog.leatherId),
                Triple(R.string.felt, R.drawable.nb6, BuiltInCatalog.whiteBlackId),
                Triple(R.string.string, R.drawable.nb7, BuiltInCatalog.stringId),
                Triple(R.string.button, R.drawable.nb8, BuiltInCatalog.buttonId),
                Triple(R.string.line_art, R.drawable.nb9, null),
            ),
            "builtin:ftn" to listOf(
                Triple(R.string.body, R.drawable.ftn1, BuiltInCatalog.leatherId),
                Triple(R.string.kahuband, R.drawable.ftn2, BuiltInCatalog.leatherId),
                Triple(R.string.belt1, R.drawable.ftn3, BuiltInCatalog.leatherId),
                Triple(R.string.belt2, R.drawable.ftn4, BuiltInCatalog.leatherId),
                Triple(R.string.belt3, R.drawable.ftn5, BuiltInCatalog.leatherId),
                Triple(R.string.felt, R.drawable.ftn6, BuiltInCatalog.whiteBlackId),
                Triple(R.string.shoe_sole, R.drawable.ftn7, BuiltInCatalog.whiteBlackId),
                Triple(R.string.button, R.drawable.ftn8, BuiltInCatalog.buttonId),
                Triple(R.string.string, R.drawable.ftn9, BuiltInCatalog.stringId),
                Triple(R.string.line_art, R.drawable.ftn10, null),
            ),
            "builtin:slb" to listOf(
                Triple(R.string.plastic, R.drawable.slb1, BuiltInCatalog.orthosisPlasticId),
                Triple(R.string.sponge, R.drawable.slb2, BuiltInCatalog.spongeId),
                Triple(R.string.kahuband, R.drawable.slb3, BuiltInCatalog.leatherId),
                Triple(R.string.belt1, R.drawable.slb4, BuiltInCatalog.leatherId),
                Triple(R.string.belt2, R.drawable.slb5, BuiltInCatalog.leatherId),
                Triple(R.string.felt, R.drawable.slb6, BuiltInCatalog.whiteBlackId),
                Triple(R.string.shoe_sole, R.drawable.slb7, BuiltInCatalog.whiteBlackId),
                Triple(R.string.string, R.drawable.slb8, BuiltInCatalog.stringId),
                Triple(R.string.button, R.drawable.slb9, BuiltInCatalog.buttonId),
                Triple(R.string.line_art, R.drawable.slb10, null),
            ),
            "builtin:pl" to listOf(
                Triple(R.string.kan, R.drawable.pl5, BuiltInCatalog.whiteBlackId),
                Triple(R.string.sponge, R.drawable.pl2, BuiltInCatalog.plSpongeId),
                Triple(R.string.belt, R.drawable.pl3, BuiltInCatalog.bandId),
                Triple(R.string.button, R.drawable.pl4, BuiltInCatalog.buttonId),
                Triple(R.string.plastic, R.drawable.pl1, BuiltInCatalog.plasticId),
                Triple(R.string.line_art, R.drawable.pl6, null),
            ),
            "builtin:pogo" to listOf(
                Triple(R.string.plastic, R.drawable.pogo1, BuiltInCatalog.orthosisPlasticId),
                // 移行前は pogo2 も「ベルト2」表記だったが、pogo3/pogo4 と重複するため
                // 誤記と判断して「ベルト1」に直してある。
                Triple(R.string.belt1, R.drawable.pogo2, BuiltInCatalog.leatherId),
                Triple(R.string.belt2, R.drawable.pogo3, BuiltInCatalog.leatherId),
                Triple(R.string.belt3, R.drawable.pogo4, BuiltInCatalog.leatherId),
                Triple(R.string.ankle_band, R.drawable.pogo5, BuiltInCatalog.leatherId),
                Triple(R.string.belt4, R.drawable.pogo6, BuiltInCatalog.leatherId),
                Triple(R.string.button, R.drawable.pogo7, BuiltInCatalog.buttonId),
                Triple(R.string.string, R.drawable.pogo8, BuiltInCatalog.stringId),
                Triple(R.string.line_art, R.drawable.pogo9, null),
            ),
            "builtin:a" to listOf(
                Triple(R.string.plastic, R.drawable.a1, BuiltInCatalog.orthosisPlasticId),
                Triple(R.string.sponge, R.drawable.a2, BuiltInCatalog.whiteBlackId),
                Triple(R.string.belt1, R.drawable.a3, BuiltInCatalog.leatherId),
                Triple(R.string.belt2, R.drawable.a4, BuiltInCatalog.leatherId),
                Triple(R.string.belt3, R.drawable.a5, BuiltInCatalog.leatherId),
                Triple(R.string.belt4, R.drawable.a6, BuiltInCatalog.leatherId),
                Triple(R.string.belt5, R.drawable.a7, BuiltInCatalog.leatherId),
                Triple(R.string.felt, R.drawable.a8, BuiltInCatalog.whiteBlackId),
                Triple(R.string.button, R.drawable.a9, BuiltInCatalog.buttonId),
                Triple(R.string.string, R.drawable.a10, BuiltInCatalog.stringId),
                Triple(R.string.line_art, R.drawable.a11, null),
            ),
        )

        assertEquals(expected.keys, BuiltInCatalog.devices.mapTo(mutableSetOf()) { it.id.value })

        for (device in BuiltInCatalog.devices) {
            val actual = device.parts.map { part ->
                Triple(
                    (part.label as DisplayText.Res).id,
                    (part.image as PartImage.Bundled).res,
                    part.paletteId,
                )
            }
            assertEquals("${device.id.value} のパーツ", expected[device.id.value], actual)
        }
    }

    @Test
    fun `線画は一番手前の1枚だけで色を変えない`() {
        // もと overlay 特別枠だったもの。今は「色を変えないレイヤー」として parts の末尾にいる。
        for (device in BuiltInCatalog.devices) {
            val untinted = device.parts.filterNot { it.isTinted }
            assertEquals("${device.id.value} の色を変えない層の数", 1, untinted.size)
            assertEquals(
                "${device.id.value} の線画が最前面にない",
                device.parts.last(),
                untinted.single(),
            )
        }
    }

    @Test
    fun `色を選べるパーツだけを取り出せる`() {
        for (device in BuiltInCatalog.devices) {
            // 線画1枚を除いた数になる。
            assertEquals(
                "${device.id.value} の色を選べるパーツ数",
                device.parts.size - 1,
                device.tintedParts.size,
            )
            assertTrue(device.tintedParts.all { it.paletteId != null })
        }
    }

    @Test
    fun `パーツ画像が装具内で重複しない`() {
        for (device in BuiltInCatalog.devices) {
            val images = device.parts.map { (it.image as PartImage.Bundled).res }
            assertEquals("${device.id.value} のパーツ画像に重複がある", images.size, images.toSet().size)
        }
    }

    @Test
    fun `ID が一意で全て builtin 印を持つ`() {
        val deviceIds = BuiltInCatalog.devices.map { it.id }
        assertEquals(deviceIds.size, deviceIds.toSet().size)
        assertTrue(deviceIds.all { it.isBuiltIn })

        val partIds = BuiltInCatalog.devices.flatMap { it.parts }.map { it.id }
        assertEquals("パーツIDが装具をまたいで衝突している", partIds.size, partIds.toSet().size)

        val colorIds = BuiltInCatalog.palettes.flatMap { it.options }.map { it.id }.toSet()
        assertTrue(colorIds.all { it.isBuiltIn })

        val paletteIds = BuiltInCatalog.palettes.map { it.id }
        assertEquals(paletteIds.size, paletteIds.toSet().size)
    }

    @Test
    fun `全パーツのパレットが定義済みでサムネイルとラベルを持つ`() {
        val known = BuiltInCatalog.palettes.mapTo(mutableSetOf()) { it.id }
        for (device in BuiltInCatalog.devices) {
            assertTrue("${device.id.value} のサムネイル", device.thumbnail is PartImage.Bundled)
            assertTrue("${device.id.value} のラベル", device.label is DisplayText.Res)
            // 色を変えないレイヤーは paletteId が null なので対象外。
            for (part in device.tintedParts) {
                assertTrue(
                    "${device.id.value} が未定義のパレット ${part.paletteId?.value} を指している",
                    part.paletteId in known,
                )
            }
        }
    }
}
