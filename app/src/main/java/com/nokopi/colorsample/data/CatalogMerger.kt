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
import com.nokopi.colorsample.data.model.SavedScheme
import com.nokopi.colorsample.data.model.SchemeId
import com.nokopi.colorsample.data.model.SchemeSelection
import com.nokopi.colorsample.data.store.StoredCatalog
import com.nokopi.colorsample.data.store.StoredDevice
import com.nokopi.colorsample.data.store.StoredScheme

/**
 * 組み込み定義とユーザー定義を1つの [Catalog] にまとめる。
 *
 * Android にも DataStore にも依存しない純粋な関数にしてあるので、素の JUnit で検証できる。
 */
object CatalogMerger {

    /**
     * 組み立ての順序に意味がある。
     * 組み込み＋ユーザー定義のグループを並べ → ユーザーの色を足し → 非表示を外し → 改名を当てる。
     */
    fun merge(
        stored: StoredCatalog,
        builtInPalettes: List<Palette> = BuiltInCatalog.palettes,
        builtInDevices: List<Device> = BuiltInCatalog.devices,
    ): Catalog {
        val userColorsByPalette = stored.colors.groupBy({ PaletteId(it.paletteId) }) { color ->
            ColorOption(
                id = ColorId(color.id),
                label = DisplayText.Literal(color.name),
                color = Color(color.argb),
            )
        }
        val hidden = stored.hiddenColors
            .groupBy({ PaletteId(it.paletteId) }) { ColorId(it.colorId) }
            .mapValues { (_, ids) -> ids.toSet() }

        val builtIn = builtInPalettes.map { palette ->
            val split = palette.options
                .withUserColors(userColorsByPalette[palette.id])
                .splitHidden(hidden[palette.id])
            palette.copy(
                // 組み込みグループは名前だけ付け替えられる。
                label = stored.paletteNames[palette.id.value]
                    ?.let(DisplayText::Literal)
                    ?: palette.label,
                options = split.visible,
                hiddenOptions = split.hidden,
            )
        }

        // ユーザーが作ったグループ。色が1つも残らないものは Palette にできないので出さない。
        val userDefined = stored.palettes.mapNotNull { storedPalette ->
            val id = PaletteId(storedPalette.id)
            val split = emptyList<ColorOption>()
                .withUserColors(userColorsByPalette[id])
                .splitHidden(hidden[id])
            if (split.visible.isEmpty()) {
                null
            } else {
                Palette(
                    id = id,
                    label = DisplayText.Literal(storedPalette.name),
                    options = split.visible,
                    hiddenOptions = split.hidden,
                )
            }
        }

        val palettes = builtIn + userDefined
        val knownPaletteIds = palettes.mapTo(mutableSetOf()) { it.id }
        val userDevices = stored.devices.mapNotNull { it.toDevice(knownPaletteIds) }

        // ホームから外した装具は分けて持つ。定義は消さないのでいつでも戻せる。
        val hiddenDeviceIds = stored.hiddenDevices.mapTo(mutableSetOf()) { DeviceId(it) }
        val (hiddenDevices, visibleDevices) =
            (builtInDevices + userDevices).partition { it.id in hiddenDeviceIds }

        val paletteById = palettes.associateBy { it.id }
        val deviceById = visibleDevices.associateBy { it.id }

        return Catalog(
            palettes = palettes,
            devices = visibleDevices,
            hiddenDevices = hiddenDevices,
            schemes = stored.schemes.mapNotNull { it.toScheme(deviceById, paletteById) },
        )
    }

    /**
     * 保存した配色を解決する。
     *
     * 装具が見つからないものは落とす。削除された装具はもちろん、非表示にした装具の配色も
     * ここで消える（[Catalog.device] が引けず、開いても配色画面が戻ってしまうため）。
     * 保存側の定義は残しているので、装具を戻せば配色も戻る。
     *
     * 色の参照が切れている場合は [Palette.optionOrFirst] が先頭に落とす。装具に後から
     * レイヤーが増えて保存側に無いパーツも同じ扱いになる。配色画面の挙動と揃えてある。
     */
    private fun StoredScheme.toScheme(
        deviceById: Map<DeviceId, Device>,
        paletteById: Map<PaletteId, Palette>,
    ): SavedScheme? {
        val device = deviceById[DeviceId(deviceId)] ?: return null

        val resolved = device.parts.mapNotNull { part ->
            val palette = part.paletteId?.let { paletteById[it] } ?: return@mapNotNull null
            SchemeSelection(
                part = part,
                option = palette.optionOrFirst(selections[part.id.value]?.let(::ColorId)),
            )
        }

        return SavedScheme(
            id = SchemeId(id),
            device = device,
            name = name,
            personName = personName,
            selections = resolved,
        )
    }

    /**
     * ユーザーの色は組み込みの後ろに積む。組み込みの並びが動かないので、
     * 色を足しても既存の配色の見え方が変わらない。
     */
    private fun List<ColorOption>.withUserColors(added: List<ColorOption>?): List<ColorOption> =
        if (added.isNullOrEmpty()) this else this + added

    private class Split(val visible: List<ColorOption>, val hidden: List<ColorOption>)

    /**
     * 非表示指定で色を「見える」「外した」に振り分ける。
     *
     * **全部消えてしまう場合は指定そのものを無視する。** 空のグループは [Palette] が
     * 受け付けないし、あっても操作できないので、壊れた保存データを読んでも
     * 表示できる状態に戻すほうがよい。この場合 hidden は空になるので、
     * 画面の「戻す」も実態どおり出ない。
     */
    private fun List<ColorOption>.splitHidden(hidden: Set<ColorId>?): Split {
        if (hidden.isNullOrEmpty()) return Split(this, emptyList())
        val (removed, remaining) = partition { it.id in hidden }
        return if (remaining.isEmpty()) Split(this, emptyList()) else Split(remaining, removed)
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
