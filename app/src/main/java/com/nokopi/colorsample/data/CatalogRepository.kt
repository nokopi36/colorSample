package com.nokopi.colorsample.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.nokopi.colorsample.data.model.Catalog
import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.DeviceId
import com.nokopi.colorsample.data.model.PaletteId
import com.nokopi.colorsample.data.model.PartId
import com.nokopi.colorsample.data.model.SchemeId
import com.nokopi.colorsample.data.model.userId
import com.nokopi.colorsample.data.store.CatalogStore
import com.nokopi.colorsample.data.store.HiddenColor
import com.nokopi.colorsample.data.store.StoredColor
import com.nokopi.colorsample.data.store.StoredPalette
import com.nokopi.colorsample.data.store.StoredDevice
import com.nokopi.colorsample.data.store.StoredPart
import com.nokopi.colorsample.data.store.StoredScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * 保存する装具1件ぶん。ファイルは保存の時点で staging に置かれている前提。
 */
data class DeviceSave(
    val id: DeviceId,
    val name: String,
    /** 描画順（先頭が最背面）。色を変えないレイヤーもここに含める。 */
    val parts: List<PartSave>,
)

data class PartSave(
    val id: PartId,
    val name: String,
    val fileName: String,
    /** null なら色を変えないレイヤー。 */
    val paletteId: PaletteId?,
)

/**
 * 配色1件ぶんの保存内容。
 *
 * @property id 既存の配色を上書きするならその ID。null なら新規。
 * @property selections パーツ -> 色。色を変えないレイヤーは含めない。
 */
data class SchemeSave(
    val id: SchemeId?,
    val deviceId: DeviceId,
    val name: String,
    val personName: String,
    val selections: Map<PartId, ColorId>,
)

/**
 * 画面が見るカタログの入り口。組み込み定義とユーザー定義をマージして流す。
 *
 * 装具の画像はファイルなので、[Context] を持ってファイルの移動と削除も引き受ける。
 */
class CatalogRepository(
    private val store: CatalogStore,
    private val context: Context,
) {

    val catalog: Flow<Catalog> = store.catalog.map { CatalogMerger.merge(it) }

    suspend fun addColor(paletteId: PaletteId, name: String, color: Color) {
        val newColor = StoredColor(
            id = userId(UUID.randomUUID().toString()),
            paletteId = paletteId.value,
            name = name.trim(),
            argb = color.toArgb(),
        )
        store.update { it.copy(colors = it.colors + newColor) }
    }

    /** 組み込みの色は編集できない。呼ばれても何もしない。 */
    suspend fun updateColor(id: ColorId, name: String, color: Color) {
        if (id.isBuiltIn) return
        store.update { stored ->
            stored.copy(
                colors = stored.colors.map {
                    if (it.id == id.value) {
                        it.copy(name = name.trim(), argb = color.toArgb())
                    } else {
                        it
                    }
                },
            )
        }
    }

    /**
     * 組み込みの色は削除できない。そのグループで最後の1色になる場合も削除しない。
     *
     * @return 消した内容。[restoreColor] に渡せば元に戻せる。消さなかった場合は null。
     */
    suspend fun deleteColor(id: ColorId): StoredColor? {
        if (id.isBuiltIn) return null
        val removed = store.catalog.first().colors.firstOrNull { it.id == id.value }
            ?: return null
        // グループを空にしてしまう削除は認めない。空のグループは表示も操作もできない。
        if (isLastColorIn(PaletteId(removed.paletteId))) return null

        store.update { stored ->
            stored.copy(colors = stored.colors.filterNot { it.id == id.value })
        }
        return removed
    }

    // ---- 色の非表示 ----------------------------------------------------

    /**
     * 色を一覧から外す。組み込みの色を「消す」のはこれ。
     *
     * グループ単位なので、革の白を外してもボタンの白は残る。
     *
     * @return 外したら true。そのグループで最後の1色なら何もせず false。
     */
    suspend fun hideColor(paletteId: PaletteId, colorId: ColorId): Boolean {
        if (isLastColorIn(paletteId)) return false
        val entry = HiddenColor(paletteId = paletteId.value, colorId = colorId.value)
        store.update { stored ->
            if (stored.hiddenColors.contains(entry)) {
                stored
            } else {
                stored.copy(hiddenColors = stored.hiddenColors + entry)
            }
        }
        return true
    }

    suspend fun unhideColor(paletteId: PaletteId, colorId: ColorId) {
        val entry = HiddenColor(paletteId = paletteId.value, colorId = colorId.value)
        store.update { stored ->
            stored.copy(hiddenColors = stored.hiddenColors.filterNot { it == entry })
        }
    }

    /** そのグループで非表示にした色をまとめて戻す。 */
    suspend fun unhideAll(paletteId: PaletteId) {
        store.update { stored ->
            stored.copy(
                hiddenColors = stored.hiddenColors.filterNot { it.paletteId == paletteId.value },
            )
        }
    }

    // ---- 色グループ ----------------------------------------------------

    /**
     * 色グループを作る。
     *
     * 最初の色を必ず一緒に入れるのは、色が0件のグループを存在させないため。
     * 空のグループは [com.nokopi.colorsample.data.model.Palette] が受け付けない。
     */
    suspend fun addPalette(name: String, firstColorName: String, firstColor: Color): PaletteId {
        val id = PaletteId(userId(UUID.randomUUID().toString()))
        val first = StoredColor(
            id = userId(UUID.randomUUID().toString()),
            paletteId = id.value,
            name = firstColorName.trim(),
            argb = firstColor.toArgb(),
        )
        store.update { stored ->
            stored.copy(
                palettes = stored.palettes + StoredPalette(id = id.value, name = name.trim()),
                colors = stored.colors + first,
            )
        }
        return id
    }

    /** 組み込みのグループも名前だけは変えられる。改名は上書きとして別に持つ。 */
    suspend fun renamePalette(id: PaletteId, name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        store.update { stored ->
            if (id.isUserDefined) {
                stored.copy(
                    palettes = stored.palettes.map {
                        if (it.id == id.value) it.copy(name = trimmed) else it
                    },
                )
            } else {
                stored.copy(paletteNames = stored.paletteNames + (id.value to trimmed))
            }
        }
    }

    /**
     * 色グループを削除する。
     *
     * 組み込みのグループは削除できない（組み込み装具が参照している）。
     * 使用中のグループも削除しない。黙って消すと装具のレイヤーが消えてしまうため、
     * 呼ぶ側で [Catalog.usages] を見て理由を出すこと。ここの判定は二重の防波堤。
     *
     * @return 削除したら true。
     */
    suspend fun deletePalette(id: PaletteId): Boolean {
        if (!id.isUserDefined) return false
        if (catalog.first().usages(id).isNotEmpty()) return false

        store.update { stored ->
            stored.copy(
                palettes = stored.palettes.filterNot { it.id == id.value },
                // 参照先が無くなる色と非表示指定も一緒に片付ける。
                colors = stored.colors.filterNot { it.paletteId == id.value },
                hiddenColors = stored.hiddenColors.filterNot { it.paletteId == id.value },
            )
        }
        return true
    }

    /** そのグループに見えている色が1つだけか。これ以上減らせない状態。 */
    private suspend fun isLastColorIn(paletteId: PaletteId): Boolean =
        (catalog.first().palettes.firstOrNull { it.id == paletteId }?.options?.size ?: 0) <= 1

    /** [deleteColor] の取り消し。同じ ID で戻すので、配色からの参照も復活する。 */
    suspend fun restoreColor(color: StoredColor) {
        store.update { stored ->
            if (stored.colors.any { it.id == color.id }) {
                stored
            } else {
                stored.copy(colors = stored.colors + color)
            }
        }
    }

    // ---- 装具 ----------------------------------------------------------

    fun newDeviceId(): DeviceId = DeviceId(userId(UUID.randomUUID().toString()))

    fun newPartId(): PartId = PartId(userId(UUID.randomUUID().toString()))

    /** 編集中の画像を置く場所。取り込みはここへ書き、保存で装具のディレクトリへ移す。 */
    fun stagingDirectory(): File = DeviceFiles.stagingDirectory(context)

    /** 編集を始めるとき・やめるときに呼ぶ。前回の編集の残りもここで消える。 */
    suspend fun clearStaging() = withContext(Dispatchers.IO) {
        DeviceFiles.stagingDirectory(context).deleteRecursively()
        Unit
    }

    /**
     * 既存の装具を編集するために、その画像を staging へ写す。
     * 保存も取り消しも staging だけを見ればよくなるので、途中でやめても元の装具は無傷。
     */
    suspend fun copyImagesToStaging(deviceId: DeviceId): Unit = withContext(Dispatchers.IO) {
        val source = DeviceFiles.directory(context, deviceId.value)
        if (!source.isDirectory) return@withContext
        val staging = DeviceFiles.stagingDirectory(context).apply { mkdirs() }
        source.listFiles()?.forEach { file ->
            if (file.isFile) file.copyTo(File(staging, file.name), overwrite = true)
        }
    }

    /**
     * 装具を保存する。新規でも上書きでも同じ経路を通る。
     *
     * 参照されているファイルを staging から装具のディレクトリへ移し、参照されなくなった
     * ファイルは残さない。
     */
    suspend fun saveDevice(save: DeviceSave) {
        withContext(Dispatchers.IO) {
            val staging = DeviceFiles.stagingDirectory(context)
            val target = DeviceFiles.directory(context, save.id.value)

            // 上書き保存のとき、前の画像を残さないよう作り直す。
            target.deleteRecursively()
            check(target.mkdirs()) { "保存先を作成できませんでした: $target" }

            for (fileName in save.parts.map { it.fileName }.distinct()) {
                val from = File(staging, fileName)
                check(from.isFile) { "編集中の画像が見つかりません: $fileName" }
                check(from.renameTo(File(target, fileName))) {
                    "画像を保存先へ移動できませんでした: $fileName"
                }
            }
        }

        val stored = StoredDevice(
            id = save.id.value,
            name = save.name.trim(),
            parts = save.parts.map {
                StoredPart(
                    id = it.id.value,
                    name = it.name.trim(),
                    fileName = it.fileName,
                    paletteId = it.paletteId?.value,
                )
            },
        )
        store.update { catalog ->
            val others = catalog.devices.filterNot { it.id == stored.id }
            // 既存を編集した場合も並びが飛ばないよう、元の位置に戻す。
            val index = catalog.devices.indexOfFirst { it.id == stored.id }
            val devices = others.toMutableList()
            devices.add(if (index >= 0) index.coerceAtMost(devices.size) else devices.size, stored)
            catalog.copy(devices = devices)
        }
        clearStaging()
    }

    /** 組み込みの装具は削除できない。画像のディレクトリごと消す。 */
    suspend fun deleteDevice(id: DeviceId) {
        if (id.isBuiltIn) return
        store.update { stored ->
            stored.copy(
                devices = stored.devices.filterNot { it.id == id.value },
                // 消えた装具への非表示指定は残しても意味がない。
                hiddenDevices = stored.hiddenDevices.filterNot { it == id.value },
                // 配色も参照先が無くなる。非表示と違い装具は戻ってこないので一緒に消す。
                schemes = stored.schemes.filterNot { it.deviceId == id.value },
            )
        }
        withContext(Dispatchers.IO) {
            DeviceFiles.directory(context, id.value).deleteRecursively()
        }
    }

    /**
     * 装具をホームから外す。
     *
     * 組み込みの装具はコード上の定義を消せないので、「消す」のはこれで表す。
     * 定義は残るのでいつでも戻せるし、アプリを更新しても壊れない。
     */
    suspend fun hideDevice(id: DeviceId) {
        store.update { stored ->
            if (stored.hiddenDevices.contains(id.value)) {
                stored
            } else {
                stored.copy(hiddenDevices = stored.hiddenDevices + id.value)
            }
        }
    }

    suspend fun unhideDevice(id: DeviceId) {
        store.update { stored ->
            stored.copy(hiddenDevices = stored.hiddenDevices.filterNot { it == id.value })
        }
    }

    // ---- 保存した配色 --------------------------------------------------

    /**
     * 配色を保存する。[SchemeSave.id] があれば上書き、無ければ新規に追加する。
     *
     * 上書きのときは並び順を変えない。名前を直すたびに一覧の位置が飛ぶと探しにくいため。
     *
     * @return 保存した配色の ID。新規なら採番されたもの。
     */
    suspend fun saveScheme(save: SchemeSave): SchemeId {
        val id = save.id ?: SchemeId(userId(UUID.randomUUID().toString()))
        val stored = StoredScheme(
            id = id.value,
            deviceId = save.deviceId.value,
            name = save.name.trim(),
            personName = save.personName.trim(),
            selections = save.selections.entries.associate { (part, color) ->
                part.value to color.value
            },
        )
        store.update { catalog ->
            val index = catalog.schemes.indexOfFirst { it.id == id.value }
            if (index >= 0) {
                catalog.copy(
                    schemes = catalog.schemes.toMutableList().apply { this[index] = stored },
                )
            } else {
                catalog.copy(schemes = catalog.schemes + stored)
            }
        }
        return id
    }

    suspend fun renameScheme(id: SchemeId, name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        store.update { stored ->
            stored.copy(
                schemes = stored.schemes.map {
                    if (it.id == id.value) it.copy(name = trimmed) else it
                },
            )
        }
    }

    suspend fun deleteScheme(id: SchemeId) {
        store.update { stored ->
            stored.copy(schemes = stored.schemes.filterNot { it.id == id.value })
        }
    }

    companion object {
        @Volatile
        private var instance: CatalogRepository? = null

        /**
         * DI ライブラリを入れるほどの規模ではないので、Application コンテキストで
         * 1つだけ持つ。
         */
        fun get(context: Context): CatalogRepository =
            instance ?: synchronized(this) {
                instance ?: run {
                    val app = context.applicationContext
                    CatalogRepository(CatalogStore.create(app), app).also { instance = it }
                }
            }
    }
}
