package com.nokopi.colorsample.ui.palette

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.nokopi.colorsample.data.BuiltInCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HexColorTest {

    private fun argbOf(text: String): Int? = parseHexColor(text)?.toArgb()

    @Test
    fun `6桁を読める`() {
        assertEquals(0xFFF5B2B2.toInt(), argbOf("#F5B2B2"))
    }

    @Test
    fun `シャープは省略できる`() {
        assertEquals(0xFFF5B2B2.toInt(), argbOf("F5B2B2"))
    }

    @Test
    fun `小文字でも読める`() {
        assertEquals(0xFFF5B2B2.toInt(), argbOf("#f5b2b2"))
    }

    @Test
    fun `前後の空白は無視する`() {
        assertEquals(0xFFF5B2B2.toInt(), argbOf("  #F5B2B2  "))
    }

    @Test
    fun `3桁は略記として展開する`() {
        assertEquals(0xFFAABBCC.toInt(), argbOf("#abc"))
        assertEquals(0xFF000000.toInt(), argbOf("#000"))
        assertEquals(0xFFFFFFFF.toInt(), argbOf("#fff"))
    }

    @Test
    fun `常に不透明になる`() {
        assertEquals(0xFF, argbOf("#000000")!! ushr 24)
        assertEquals(0xFF, argbOf("#ffffff")!! ushr 24)
    }

    @Test
    fun `形が合わないものは弾く`() {
        val invalid = listOf(
            "",
            "#",
            "#12",
            "#1234",
            "#12345",
            "#1234567",
            // アルファ付きは受け付けない。tint は SrcIn なので半透明にすると下が透ける。
            "#FF112233",
            "#GGHHII",
            "赤",
            "rgb(1,2,3)",
            "0xF5B2B2",
            "#F5 B2 B2",
        )
        for (text in invalid) {
            assertNull("「$text」は弾かれるべき", parseHexColor(text))
        }
    }

    @Test
    fun `整形は6桁大文字のシャープ付きになる`() {
        assertEquals("#F5B2B2", Color(0xFFF5B2B2).toHexString())
        assertEquals("#000000", Color(0xFF000000).toHexString())
        assertEquals("#FFFFFF", Color(0xFFFFFFFF).toHexString())
    }

    @Test
    fun `整形してから読み直すと同じ色に戻る`() {
        // 入力欄とスライダーの往復で色がずれないことがこの機能の要。
        val colors = BuiltInCatalog.palettes.flatMap { it.options }.map { it.color }
        for (color in colors) {
            assertEquals(
                "${color.toHexString()} の往復",
                color.toArgb(),
                parseHexColor(color.toHexString())?.toArgb(),
            )
        }
    }
}
