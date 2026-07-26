package com.nokopi.colorsample.data

import android.content.Context
import java.io.File

/**
 * ユーザーが取り込んだ装具の画像の置き場所。
 *
 * `filesDir/devices/<deviceId>/<fileName>` に置き、装具を削除したらディレクトリごと消す。
 * パスの組み立てをここ1か所に閉じ込めて、保存側と読み出し側がずれないようにする。
 */
object DeviceFiles {

    private const val ROOT = "devices"

    /** `filesDir` からの相対パス。[com.nokopi.colorsample.data.model.PartImage.Stored] が持つ形。 */
    fun relativePath(deviceId: String, fileName: String): String = "$ROOT/$deviceId/$fileName"

    fun directory(context: Context, deviceId: String): File =
        File(File(context.filesDir, ROOT), deviceId)

    fun resolve(context: Context, relativePath: String): File =
        File(context.filesDir, relativePath)
}
