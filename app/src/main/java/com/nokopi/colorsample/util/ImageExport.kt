package com.nokopi.colorsample.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 配色プレビューを PNG として端末に保存したり、他アプリへ渡したりする。
 *
 * 保存先は Android 10 (API 29) 以降は MediaStore（権限不要）、
 * それより前は外部ストレージの Pictures 配下（要 WRITE_EXTERNAL_STORAGE）。
 */
object ImageExport {

    /** ギャラリーに作るフォルダ名。共有用キャッシュのサブディレクトリ名も兼ねる。 */
    private const val ALBUM_NAME = "OColorDesign"
    private const val MIME_TYPE = "image/png"

    /** API 28 以下で保存するときだけ必要になる権限。 */
    val legacyWritePermission: String? =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        } else {
            null
        }

    fun buildFileName(deviceName: String, personName: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val person = personName.trim().replace(UNSAFE_FILE_NAME_CHARS, "_").take(32)
        return if (person.isEmpty()) {
            "${deviceName}_$timestamp.png"
        } else {
            "${person}_${deviceName}_$timestamp.png"
        }
    }

    /** ギャラリーに保存する。 */
    suspend fun saveToGallery(
        context: Context,
        bitmap: Bitmap,
        fileName: String,
    ): Result<Uri> = withContext(Dispatchers.IO) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveViaMediaStore(context, bitmap, fileName)
            } else {
                saveToPublicPictures(context, bitmap, fileName)
            }
        }
    }

    /** 他アプリへ渡すための共有 Intent を組み立てる。 */
    suspend fun createShareIntent(
        context: Context,
        bitmap: Bitmap,
        fileName: String,
    ): Result<Intent> = withContext(Dispatchers.IO) {
        runCatching {
            val dir = File(context.cacheDir, ALBUM_NAME).apply { mkdirs() }
            // 共有用キャッシュは溜め込まず、毎回作り直す。
            dir.listFiles()?.forEach { it.delete() }

            val file = File(dir, fileName)
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file,
            )

            Intent(Intent.ACTION_SEND).apply {
                type = MIME_TYPE
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
    private fun saveViaMediaStore(context: Context, bitmap: Bitmap, fileName: String): Uri {
        val resolver = context.contentResolver
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, MIME_TYPE)
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/$ALBUM_NAME",
            )
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = requireNotNull(resolver.insert(collection, values)) {
            "MediaStore にレコードを作成できませんでした"
        }

        try {
            requireNotNull(resolver.openOutputStream(uri)) {
                "MediaStore の出力ストリームを開けませんでした"
            }.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        } catch (e: Throwable) {
            resolver.delete(uri, null, null)
            throw e
        }

        resolver.update(
            uri,
            ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) },
            null,
            null,
        )
        return uri
    }

    @Suppress("DEPRECATION")
    private fun saveToPublicPictures(context: Context, bitmap: Bitmap, fileName: String): Uri {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            ALBUM_NAME,
        )
        check(dir.exists() || dir.mkdirs()) { "保存先フォルダを作成できませんでした: $dir" }

        val file = File(dir, fileName)
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }

        // ギャラリーアプリにすぐ出てくるようにインデックスさせる。
        MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf(MIME_TYPE), null)
        return Uri.fromFile(file)
    }

    private val UNSAFE_FILE_NAME_CHARS = Regex("""[\\/:*?"<>|\s]""")
}
