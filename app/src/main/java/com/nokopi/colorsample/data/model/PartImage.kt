package com.nokopi.colorsample.data.model

import androidx.annotation.DrawableRes

/**
 * パーツ画像の在りか。
 *
 * [DisplayText] と同じく実行時のモデル。[Bundled] が持つのはリソースIDなので永続化しない。
 * 保存されるのはユーザーが取り込んだ画像のパス ([Stored]) だけ。
 */
sealed interface PartImage {

    /** APK に同梱された画像。 */
    @JvmInline
    value class Bundled(@DrawableRes val res: Int) : PartImage

    /**
     * ユーザーが取り込んだ画像。
     *
     * @property relativePath `filesDir` からの相対パス（例 `devices/<deviceId>/<partId>.png`）。
     *   ファイル名だけでなくディレクトリまで含めるのは、読み出し側が装具の ID を
     *   知らなくても解決できるようにするため。
     */
    @JvmInline
    value class Stored(val relativePath: String) : PartImage
}
