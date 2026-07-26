package com.nokopi.colorsample.data

/**
 * 取り込む画像に求める条件と、その判定。
 *
 * このアプリの装具はパーツごとに1枚のレイヤーを重ねて描くので、素材の作りに条件がある。
 * 判定を Android から切り離した純粋な関数に置いてあるのは、素の JUnit で押さえたいため。
 * 実際のデコードと保存は [ImageImporter] 側。
 */
object ImageImport {

    /** 保存する画像の長辺の上限。これを超えるものは縮めて持つ。 */
    const val MAX_DIMENSION = 1440

    /** 空かどうかを見るときに、この程度まで縮めてから調べる。 */
    const val ALPHA_PROBE_DIMENSION = 64

    data class Size(val width: Int, val height: Int) {
        override fun toString(): String = "${width}×$height"
    }

    /** 取り込めなかった理由。画面ではこれを文言に変える。 */
    sealed interface Rejection {
        /** PNG ではない、またはアルファチャンネルを持たない。 */
        data object NotTransparentPng : Rejection

        /** 1枚目と寸法が違う。重ねる前提なので揃っていないと位置が合わない。 */
        data class SizeMismatch(val expected: Size, val actual: Size) : Rejection

        /** 全面が透明。重ねても何も出ないので弾く。 */
        data object Empty : Rejection

        /** デコードそのものに失敗した。 */
        data object Unreadable : Rejection
    }

    /**
     * 2枚目以降の寸法が1枚目と揃っているか。
     *
     * @param reference 先に取り込んだ画像の寸法。1枚目なら null。
     */
    fun checkSize(reference: Size?, actual: Size): Rejection? = when {
        actual.width <= 0 || actual.height <= 0 -> Rejection.Unreadable
        reference == null || reference == actual -> null
        else -> Rejection.SizeMismatch(reference, actual)
    }

    /**
     * デコード時に使う間引き率。
     *
     * `BitmapFactory` の `inSampleSize` は2の累乗しか効かないので、長辺が [maxDimension]
     * を下回らない範囲で最大まで間引く率を返す。残りの端数は呼び出し側で縮小する。
     */
    fun sampleSizeFor(size: Size, maxDimension: Int = MAX_DIMENSION): Int {
        require(maxDimension > 0) { "上限は正の値でなければならない: $maxDimension" }
        val longest = maxOf(size.width, size.height)
        var sample = 1
        while (longest / (sample * 2) >= maxDimension) {
            sample *= 2
        }
        return sample
    }

    // ---- PNG ヘッダの読み取り ------------------------------------------

    /**
     * ヘッダを読むのに十分な先頭バイト数。
     * 寸法は IHDR に、透明度の情報は IDAT より前に置かれる決まりなので、これで足りる。
     */
    const val HEADER_PROBE_BYTES = 64 * 1024

    data class PngInfo(val size: Size, val hasAlphaChannel: Boolean)

    private val PNG_SIGNATURE = byteArrayOf(
        0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
    )

    fun isPng(bytes: ByteArray): Boolean =
        bytes.size >= PNG_SIGNATURE.size &&
            PNG_SIGNATURE.indices.all { bytes[it] == PNG_SIGNATURE[it] }

    /**
     * PNG のヘッダから寸法と透明度の有無を読む。
     *
     * `BitmapFactory` の `outMimeType` と、デコード後の `Bitmap.hasAlpha()` に頼らず
     * 自分で読むのは、ここを純粋な関数にしてテストで固定したいため。
     * 形式から直接判断できるので、判定の理由もはっきりする。
     *
     * アルファを持つとみなすのは、カラータイプが 4 (グレー+α) か 6 (RGBA) の場合、
     * または tRNS チャンクで透明色が指定されている場合（インデックスカラーの書き出しなど）。
     *
     * @return 読めなければ null。PNG でない場合も null になるので、先に [isPng] で分けること。
     */
    fun readPngInfo(bytes: ByteArray): PngInfo? {
        if (!isPng(bytes)) return null

        // 署名の直後は必ず IHDR。長さ13・タイプ・データ・CRC で 8+13+4 バイト。
        val ihdrStart = PNG_SIGNATURE.size
        if (bytes.size < ihdrStart + 8 + 13 + 4) return null
        if (readInt(bytes, ihdrStart) != 13) return null
        if (chunkType(bytes, ihdrStart) != "IHDR") return null

        val width = readInt(bytes, ihdrStart + 8)
        val height = readInt(bytes, ihdrStart + 12)
        val colorType = bytes[ihdrStart + 8 + 9].toInt() and 0xFF
        if (width <= 0 || height <= 0) return null

        val hasAlphaChannel = colorType == COLOR_TYPE_GRAY_ALPHA ||
            colorType == COLOR_TYPE_RGBA ||
            hasTransparencyChunk(bytes, ihdrStart + 8 + 13 + 4)

        return PngInfo(Size(width, height), hasAlphaChannel)
    }

    /** IDAT に当たるまでチャンクを辿って tRNS があるか見る。 */
    private fun hasTransparencyChunk(bytes: ByteArray, from: Int): Boolean {
        var offset = from
        while (offset + 8 <= bytes.size) {
            val length = readInt(bytes, offset)
            if (length < 0) return false
            when (chunkType(bytes, offset)) {
                "tRNS" -> return true
                // 画像データが始まったら、それ以降に tRNS は来ない。
                "IDAT", "IEND" -> return false
            }
            offset += 8 + length + 4
        }
        return false
    }

    private fun readInt(bytes: ByteArray, offset: Int): Int =
        ((bytes[offset].toInt() and 0xFF) shl 24) or
            ((bytes[offset + 1].toInt() and 0xFF) shl 16) or
            ((bytes[offset + 2].toInt() and 0xFF) shl 8) or
            (bytes[offset + 3].toInt() and 0xFF)

    private fun chunkType(bytes: ByteArray, lengthOffset: Int): String? {
        val start = lengthOffset + 4
        if (start + 4 > bytes.size) return null
        return String(bytes, start, 4, Charsets.US_ASCII)
    }

    private const val COLOR_TYPE_GRAY_ALPHA = 4
    private const val COLOR_TYPE_RGBA = 6

    /**
     * 長辺を [maxDimension] に収めた寸法。縦横比は保つ。上限内ならそのまま返す。
     */
    fun scaledSize(size: Size, maxDimension: Int = MAX_DIMENSION): Size {
        val longest = maxOf(size.width, size.height)
        if (longest <= maxDimension) return size
        val ratio = maxDimension.toDouble() / longest
        return Size(
            width = (size.width * ratio).toInt().coerceAtLeast(1),
            height = (size.height * ratio).toInt().coerceAtLeast(1),
        )
    }
}
