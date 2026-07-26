package com.nokopi.colorsample.data

import com.nokopi.colorsample.data.ImageImport.Size
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.util.zip.CRC32
import java.util.zip.Deflater

/**
 * PNG ヘッダからの寸法とアルファ判定。
 *
 * 以前は `BitmapFactory` の `inJustDecodeBounds` で寸法を取っていたが、
 * この設定では `decodeStream` が仕様上 null を返すため、エルビス演算子が常に発火して
 * すべての画像が「読み込めませんでした」になっていた。Android に依存しない形へ移し、
 * ここで固定する。
 */
class PngInfoTest {

    // ---- テスト用の PNG 組み立て --------------------------------------

    private fun chunk(type: String, data: ByteArray): ByteArray {
        val out = ByteArrayOutputStream()
        out.write(intBytes(data.size))
        val body = type.toByteArray(Charsets.US_ASCII) + data
        out.write(body)
        val crc = CRC32().apply { update(body) }.value.toInt()
        out.write(intBytes(crc))
        return out.toByteArray()
    }

    private fun intBytes(value: Int) = byteArrayOf(
        (value ushr 24).toByte(),
        (value ushr 16).toByte(),
        (value ushr 8).toByte(),
        value.toByte(),
    )

    private fun ihdr(width: Int, height: Int, colorType: Int): ByteArray =
        intBytes(width) + intBytes(height) +
            byteArrayOf(8, colorType.toByte(), 0, 0, 0)

    private fun deflated(size: Int = 16): ByteArray {
        val deflater = Deflater()
        deflater.setInput(ByteArray(size))
        deflater.finish()
        val buffer = ByteArray(256)
        val n = deflater.deflate(buffer)
        deflater.end()
        return buffer.copyOf(n)
    }

    private fun png(
        width: Int = 1280,
        height: Int = 1280,
        colorType: Int,
        withTrns: Boolean = false,
        signature: ByteArray = SIGNATURE,
    ): ByteArray {
        val out = ByteArrayOutputStream()
        out.write(signature)
        out.write(chunk("IHDR", ihdr(width, height, colorType)))
        if (withTrns) out.write(chunk("tRNS", byteArrayOf(0, 0, 0)))
        out.write(chunk("IDAT", deflated()))
        out.write(chunk("IEND", ByteArray(0)))
        return out.toByteArray()
    }

    private val SIGNATURE
        get() = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)

    // ---- 署名 ----------------------------------------------------------

    @Test
    fun `PNG の署名を見分ける`() {
        assertTrue(ImageImport.isPng(png(colorType = COLOR_RGBA)))
        // JPEG の先頭
        assertFalse(ImageImport.isPng(byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())))
        assertFalse(ImageImport.isPng(ByteArray(0)))
        assertFalse(ImageImport.isPng("これは画像ではない".toByteArray()))
    }

    @Test
    fun `PNG でなければ情報も返さない`() {
        assertNull(ImageImport.readPngInfo(byteArrayOf(0xFF.toByte(), 0xD8.toByte())))
    }

    // ---- 寸法 ----------------------------------------------------------

    @Test
    fun `寸法を読める`() {
        val info = ImageImport.readPngInfo(png(1280, 720, COLOR_RGBA))
        assertEquals(Size(1280, 720), info?.size)
    }

    @Test
    fun `大きな寸法も読める`() {
        assertEquals(
            Size(4000, 4000),
            ImageImport.readPngInfo(png(4000, 4000, COLOR_RGBA))?.size,
        )
    }

    @Test
    fun `寸法が0なら読めない扱い`() {
        assertNull(ImageImport.readPngInfo(png(0, 100, COLOR_RGBA)))
        assertNull(ImageImport.readPngInfo(png(100, 0, COLOR_RGBA)))
    }

    // ---- アルファの有無 ------------------------------------------------

    @Test
    fun `RGBA はアルファを持つ`() {
        assertTrue(ImageImport.readPngInfo(png(colorType = COLOR_RGBA))!!.hasAlphaChannel)
    }

    @Test
    fun `グレースケール＋アルファもアルファを持つ`() {
        assertTrue(ImageImport.readPngInfo(png(colorType = COLOR_GRAY_ALPHA))!!.hasAlphaChannel)
    }

    @Test
    fun `RGB はアルファを持たない`() {
        // 端末で試した reject_no_alpha_1280.png と同じ形。
        assertFalse(ImageImport.readPngInfo(png(colorType = COLOR_RGB))!!.hasAlphaChannel)
    }

    @Test
    fun `グレースケール単体はアルファを持たない`() {
        assertFalse(ImageImport.readPngInfo(png(colorType = COLOR_GRAY))!!.hasAlphaChannel)
    }

    @Test
    fun `インデックスカラーでも tRNS があれば透明を持つ`() {
        // 書き出しツールによってはこの形になるので、これを弾くと使えない素材が出てしまう。
        assertTrue(
            ImageImport.readPngInfo(png(colorType = COLOR_PALETTE, withTrns = true))!!
                .hasAlphaChannel,
        )
        assertFalse(
            ImageImport.readPngInfo(png(colorType = COLOR_PALETTE, withTrns = false))!!
                .hasAlphaChannel,
        )
    }

    @Test
    fun `RGB でも tRNS があれば透明を持つ`() {
        assertTrue(
            ImageImport.readPngInfo(png(colorType = COLOR_RGB, withTrns = true))!!.hasAlphaChannel,
        )
    }

    // ---- 壊れた入力 ----------------------------------------------------

    @Test
    fun `署名だけで途切れていたら読めない`() {
        assertNull(ImageImport.readPngInfo(SIGNATURE))
    }

    @Test
    fun `IHDR の途中で途切れていたら読めない`() {
        val truncated = png(colorType = COLOR_RGBA).copyOf(20)
        assertNull(ImageImport.readPngInfo(truncated))
    }

    @Test
    fun `先頭のチャンクが IHDR でなければ読めない`() {
        val out = ByteArrayOutputStream()
        out.write(SIGNATURE)
        out.write(chunk("IDAT", deflated()))
        assertNull(ImageImport.readPngInfo(out.toByteArray()))
    }

    @Test
    fun `IDAT より後ろの tRNS は見ない`() {
        // PNG の決まりでは tRNS は IDAT より前。後ろにあるものは無効なので拾わない。
        val out = ByteArrayOutputStream()
        out.write(SIGNATURE)
        out.write(chunk("IHDR", ihdr(100, 100, COLOR_RGB)))
        out.write(chunk("IDAT", deflated()))
        out.write(chunk("tRNS", byteArrayOf(0, 0, 0)))
        assertFalse(ImageImport.readPngInfo(out.toByteArray())!!.hasAlphaChannel)
    }

    @Test
    fun `ヘッダを読む長さは寸法とtRNSに届く`() {
        // 実際には先頭 HEADER_PROBE_BYTES しか読まないので、その範囲で判定できること。
        val full = png(colorType = COLOR_PALETTE, withTrns = true)
        val head = full.copyOf(minOf(full.size, ImageImport.HEADER_PROBE_BYTES))
        val info = ImageImport.readPngInfo(head)
        assertEquals(Size(1280, 1280), info?.size)
        assertTrue(info!!.hasAlphaChannel)
    }

    private companion object {
        const val COLOR_GRAY = 0
        const val COLOR_RGB = 2
        const val COLOR_PALETTE = 3
        const val COLOR_GRAY_ALPHA = 4
        const val COLOR_RGBA = 6
    }
}
