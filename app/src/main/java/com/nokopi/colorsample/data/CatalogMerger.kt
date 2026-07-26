package com.nokopi.colorsample.data

import androidx.compose.ui.graphics.Color
import com.nokopi.colorsample.data.model.Catalog
import com.nokopi.colorsample.data.model.ColorId
import com.nokopi.colorsample.data.model.ColorOption
import com.nokopi.colorsample.data.model.Device
import com.nokopi.colorsample.data.model.DeviceId
import com.nokopi.colorsample.data.model.DisplayText
import com.nokopi.colorsample.data.model.Palette
import com.nokopi.colorsample.data.model.PaletteId
import com.nokopi.colorsample.data.model.PartId
import com.nokopi.colorsample.data.model.PartImage
import com.nokopi.colorsample.data.model.PartSpec
import com.nokopi.colorsample.data.store.StoredCatalog
import com.nokopi.colorsample.data.store.StoredDevice

/**
 * 組み込み定義とユーザー定義を1つの [Catalog] にまとめる。
 *
 * Android にも DataStore にも依存しない純粋な関数にしてあるので、素の JUnit で検証できる。
 */
object CatalogMerger {

    fun merge(
        stored: StoredCatalog,
        builtInPalettes: List<Palette> = BuiltInCatalog.palettes,
        builtInDevices: List<Device> = BuiltInCatalog.devices,
    ): Catalog {
        val knownPaletteIds = builtInPalettes.mapTo(mutableSetOf()) { it.id }

        val userColorsByPalette = stored.colors
            // 知らないパレット宛の色は捨てる（将来の形式を読んでしまった場合の保険）
            .filter { PaletteId(it.paletteId) in knownPaletteIds }
            .groupBy({ PaletteId(it.paletteId) }) { color ->
                ColorOption(
                    id = ColorId(color.id),
                    label = DisplayText.Literal(color.name),
                    color = Color(color.argb),
                )
            }

        val palettes = builtInPalettes.map { palette ->
            val added = userColorsByPalette[palette.id].orEmpty()
            // ユーザーの色は組み込みの後ろに積む。組み込みの並びが動かないので、
            // 既存の配色の見え方が色の追加で変わらない。
            if (added.isEmpty()) palette else palette.copy(options = palette.options + added)
        }

        val userDevices = stored.devices.mapNotNull { it.toDevice(knownPaletteIds) }

        return Catalog(palettes = palettes, devices = builtInDevices + userDevices)
    }

    private fun StoredDevice.toDevice(knownPaletteIds: Set<PaletteId>): Device? {
        // 色を変えないレイヤー (paletteId == null) はそのまま通す。
        // 色を変える指定なのにパレットが解決できないものだけ落とす。
        val usableParts = parts.filter {
            it.paletteId == null || PaletteId(it.paletteId) in knownPaletteIds
        }
        if (usableParts.isEmpty()) return null

        fun image(fileName: String) = PartImage.Stored(DeviceFiles.relativePath(id, fileName))

        val specs = usableParts.map { part ->
            PartSpec(
                id = PartId(part.id),
                label = DisplayText.Literal(part.name),
                image = image(part.fileName),
                paletteId = part.paletteId?.let(::PaletteId),
            )
        }

        return Device(
            id = DeviceId(id),
            label = DisplayText.Literal(name),
            // サムネイルは一番手前の「色を変えない」層（＝線画）を優先し、
            // 無ければ一番手前のレイヤーを使う。
            thumbnail = (specs.lastOrNull { !it.isTinted } ?: specs.last()).image,
            parts = specs,
        )
    }
}
