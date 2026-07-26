package com.nokopi.colorsample.data

import android.content.Context
import java.io.File

/**
 * ユーザーが取り込んだ装具の画像の置き場所。
 *
 * `filesDir/devices/<装具のディレクトリ名>/<ファイル名>` に置き、装具を削除したら
 * ディレクトリごと消す。編集中は `filesDir/devices/.staging/` に溜める。
 * パスの組み立てをここ1か所に閉じ込めて、保存側と読み出し側がずれないようにする。
 */
object DeviceFiles {

    private const val ROOT = "devices"

    /** 編集中の画像を溜める場所。装具のIDにはなりえない名前にしてある。 */
    private const val STAGING_DIR_NAME = ".staging"

    /**
     * ID をファイル名に使える形へ落とす。
     *
     * 装具やパーツの ID は `user:<uuid>` の形でコロンを含む。Android の内部ストレージでは
     * 通るが、ファイル名に使えない環境もあるため、英数字とハイフン・下線以外は潰しておく。
     */
    fun safeName(id: String): String =
        id.map { if (it.isLetterOrDigit() || it == '-' || it == '_') it else '_' }.joinToString("")

    /** `filesDir` からの相対パス。[com.nokopi.colorsample.data.model.PartImage.Stored] が持つ形。 */
    fun relativePath(deviceId: String, fileName: String): String =
        "$ROOT/${safeName(deviceId)}/$fileName"

    /** 編集中の画像の相対パス。保存前のプレビューがこれを読む。 */
    fun stagingRelativePath(fileName: String): String = "$ROOT/$STAGING_DIR_NAME/$fileName"

    fun directory(context: Context, deviceId: String): File =
        File(root(context), safeName(deviceId))

    /** 編集中の画像を置く場所。保存で装具のディレクトリへ移し、取り消しなら丸ごと消す。 */
    fun stagingDirectory(context: Context): File = File(root(context), STAGING_DIR_NAME)

    fun resolve(context: Context, relativePath: String): File =
        File(context.filesDir, relativePath)

    private fun root(context: Context): File = File(context.filesDir, ROOT)
}
