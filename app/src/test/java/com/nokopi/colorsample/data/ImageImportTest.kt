package com.nokopi.colorsample.data

import com.nokopi.colorsample.data.ImageImport.Rejection
import com.nokopi.colorsample.data.ImageImport.Size
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 取り込む画像の条件判定。
 *
 * レイヤーを重ねて描く仕組みなので、寸法が揃っていないと位置が合わない。
 * 巨大な画像をそのまま持つと端末によって OOM するので縮める。
 */
class ImageImportTest {

    // ---- 寸法の突き合わせ ----------------------------------------------

    @Test
    fun `1枚目は基準がないので通る`() {
        assertNull(ImageImport.checkSize(reference = null, actual = Size(1280, 1280)))
    }

    @Test
    fun `基準と同じなら通る`() {
        assertNull(ImageImport.checkSize(Size(1280, 1280), Size(1280, 1280)))
    }

    @Test
    fun `寸法が違えば期待値と実際の値を添えて弾く`() {
        val rejection = ImageImport.checkSize(Size(1280, 1280), Size(1024, 1024))
        assertEquals(Rejection.SizeMismatch(Size(1280, 1280), Size(1024, 1024)), rejection)
    }

    @Test
    fun `縦横が入れ替わっただけでも弾く`() {
        // 重ねる前提なので、面積が同じでも向きが違えば合わない。
        assertTrue(
            ImageImport.checkSize(Size(1280, 720), Size(720, 1280)) is Rejection.SizeMismatch,
        )
    }

    @Test
    fun `寸法が取れなければ読めない扱いにする`() {
        assertEquals(Rejection.Unreadable, ImageImport.checkSize(null, Size(0, 0)))
        assertEquals(Rejection.Unreadable, ImageImport.checkSize(null, Size(-1, 100)))
    }

    @Test
    fun `寸法違いのメッセージに使う文字列が読める形になっている`() {
        assertEquals("1280×720", Size(1280, 720).toString())
    }

    // ---- 間引き率 ------------------------------------------------------

    @Test
    fun `上限内なら間引かない`() {
        assertEquals(1, ImageImport.sampleSizeFor(Size(1280, 1280)))
        assertEquals(1, ImageImport.sampleSizeFor(Size(1440, 1000)))
    }

    @Test
    fun `間引いても上限を下回らない範囲で最大にする`() {
        // 2880/2 = 1440 なので 2 まで間引ける。
        assertEquals(2, ImageImport.sampleSizeFor(Size(2880, 2880)))
        // 4000/2 = 2000 は上限以上なのでさらに /2 して 1000。1000 < 1440 なので 2 で止まる。
        assertEquals(2, ImageImport.sampleSizeFor(Size(4000, 4000)))
        assertEquals(4, ImageImport.sampleSizeFor(Size(5760, 5760)))
    }

    @Test
    fun `長辺で判断する`() {
        assertEquals(2, ImageImport.sampleSizeFor(Size(2880, 100)))
        assertEquals(2, ImageImport.sampleSizeFor(Size(100, 2880)))
    }

    @Test
    fun `間引き率は必ず1以上`() {
        assertEquals(1, ImageImport.sampleSizeFor(Size(1, 1)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `上限が0以下なら弾く`() {
        ImageImport.sampleSizeFor(Size(100, 100), maxDimension = 0)
    }

    // ---- 縮小後の寸法 --------------------------------------------------

    @Test
    fun `上限内ならそのまま`() {
        assertEquals(Size(1280, 960), ImageImport.scaledSize(Size(1280, 960)))
        assertEquals(Size(1440, 1440), ImageImport.scaledSize(Size(1440, 1440)))
    }

    @Test
    fun `長辺を上限に合わせ縦横比を保つ`() {
        assertEquals(Size(1440, 1440), ImageImport.scaledSize(Size(2880, 2880)))
        assertEquals(Size(1440, 720), ImageImport.scaledSize(Size(2880, 1440)))
        assertEquals(Size(720, 1440), ImageImport.scaledSize(Size(1440, 2880)))
    }

    @Test
    fun `極端に細長くても1px未満にはしない`() {
        val scaled = ImageImport.scaledSize(Size(10_000, 1))
        assertEquals(ImageImport.MAX_DIMENSION, scaled.width)
        assertTrue("高さが0になっている", scaled.height >= 1)
    }

    @Test
    fun `空かどうかを調べる縮小でも1px未満にはしない`() {
        val probe = ImageImport.scaledSize(
            Size(10_000, 1),
            ImageImport.ALPHA_PROBE_DIMENSION,
        )
        assertEquals(ImageImport.ALPHA_PROBE_DIMENSION, probe.width)
        assertTrue(probe.height >= 1)
    }
}
