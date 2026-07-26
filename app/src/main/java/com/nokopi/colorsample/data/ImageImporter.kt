package com.nokopi.colorsample.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.scale
import com.nokopi.colorsample.data.ImageImport.Rejection
import com.nokopi.colorsample.data.ImageImport.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/** 取り込みに成功した1枚。 */
data class ImportedImage(val fileName: String, val size: Size)

/**
 * 選ばれた画像を検証して、編集中の置き場 (staging) に PNG として保存する。
 *
 * 取り込んだ時点でファイルに落としてしまうのは、`OpenMultipleDocuments` が返す URI の
 * 読み取り権限が編集を終えるまで生きている保証がないため。ここでコピーしておけば、
 * 以降はアプリ内のファイルだけを見ればよくなる。
 */
class ImageImporter(private val context: Context) {

    /**
     * @param reference すでに取り込んだ画像の寸法。1枚目なら null。
     * @return 成功なら [ImportedImage]、条件を満たさなければ [Rejection]。
     */
    suspend fun import(
        uri: Uri,
        reference: Size?,
        targetDirectory: File,
        fileName: String,
    ): Result<ImportedImage> = withContext(Dispatchers.IO) {
        runCatching {
            val head = readHead(uri) ?: throw ImportRejected(Rejection.Unreadable)
            // PNG でないものは、そもそも透明を持てないので先に落とす。
            if (!ImageImport.isPng(head)) throw ImportRejected(Rejection.NotTransparentPng)

            val info = ImageImport.readPngInfo(head) ?: throw ImportRejected(Rejection.Unreadable)
            if (!info.hasAlphaChannel) throw ImportRejected(Rejection.NotTransparentPng)

            ImageImport.checkSize(reference, info.size)?.let { throw ImportRejected(it) }

            val bitmap = decodeScaled(uri, info.size)
                ?: throw ImportRejected(Rejection.Unreadable)
            try {
                if (bitmap.isFullyTransparent()) throw ImportRejected(Rejection.Empty)

                targetDirectory.mkdirs()
                FileOutputStream(File(targetDirectory, fileName)).use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                // 2枚目以降の突き合わせは縮小前の寸法で行うので、ヘッダの値を返す。
                ImportedImage(fileName = fileName, size = info.size)
            } finally {
                bitmap.recycle()
            }
        }
    }

    /** ヘッダを読むぶんだけ先頭を取る。 */
    private fun readHead(uri: Uri): ByteArray? {
        val stream = context.contentResolver.openInputStream(uri) ?: return null
        return stream.use { input ->
            val buffer = ByteArray(ImageImport.HEADER_PROBE_BYTES)
            var read = 0
            while (read < buffer.size) {
                val n = input.read(buffer, read, buffer.size - read)
                if (n < 0) break
                read += n
            }
            if (read <= 0) null else buffer.copyOf(read)
        }
    }

    private fun decodeScaled(uri: Uri, size: Size): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inSampleSize = ImageImport.sampleSizeFor(size)
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val stream = context.contentResolver.openInputStream(uri) ?: return null
        val decoded = stream.use { BitmapFactory.decodeStream(it, null, options) } ?: return null

        val target = ImageImport.scaledSize(Size(decoded.width, decoded.height))
        if (target.width == decoded.width && target.height == decoded.height) return decoded

        // inSampleSize は2の累乗しか効かないので、端数をここで詰める。
        return decoded.scale(target.width, target.height).also {
            if (it !== decoded) decoded.recycle()
        }
    }
}

/** [ImageImporter.import] が条件を満たさなかったことを [Result] に載せるための例外。 */
class ImportRejected(val rejection: Rejection) : Exception(rejection.toString())

/**
 * 全面が透明かどうか。全画素を見ると重いので縮めた写しで調べる。
 * パーツのシルエットは面で塗られているため、この粗さでも取りこぼさない。
 */
private fun Bitmap.isFullyTransparent(): Boolean {
    val probeSize = ImageImport.scaledSize(
        Size(width, height),
        ImageImport.ALPHA_PROBE_DIMENSION,
    )
    val probe = scale(probeSize.width, probeSize.height)
    try {
        val pixels = IntArray(probe.width * probe.height)
        probe.getPixels(pixels, 0, probe.width, 0, 0, probe.width, probe.height)
        return pixels.none { (it ushr 24) != 0 }
    } finally {
        if (probe !== this) probe.recycle()
    }
}
