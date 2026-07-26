package com.nokopi.colorsample.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.nokopi.colorsample.data.model.Catalog
import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.PaletteId
import com.nokopi.colorsample.data.model.userId
import com.nokopi.colorsample.data.store.CatalogStore
import com.nokopi.colorsample.data.store.StoredColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * 画面が見るカタログの入り口。組み込み定義とユーザー定義をマージして流す。
 */
class CatalogRepository(private val store: CatalogStore) {

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
     * 組み込みの色は削除できない。
     *
     * @return 消した内容。[restoreColor] に渡せば元に戻せる。消さなかった場合は null。
     */
    suspend fun deleteColor(id: ColorId): StoredColor? {
        if (id.isBuiltIn) return null
        val removed = store.catalog.first().colors.firstOrNull { it.id == id.value }
            ?: return null
        store.update { stored ->
            stored.copy(colors = stored.colors.filterNot { it.id == id.value })
        }
        return removed
    }

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

    companion object {
        @Volatile
        private var instance: CatalogRepository? = null

        /**
         * DI ライブラリを入れるほどの規模ではないので、Application コンテキストで
         * 1つだけ持つ。
         */
        fun get(context: Context): CatalogRepository =
            instance ?: synchronized(this) {
                instance ?: CatalogRepository(
                    CatalogStore.create(context.applicationContext),
                ).also { instance = it }
            }
    }
}
