package com.nokopi.colorsample.data

import androidx.compose.ui.graphics.Color
import com.nokopi.colorsample.R
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
import com.nokopi.colorsample.data.model.builtInId

/**
 * APK に同梱された色と装具の定義。ユーザーは編集・削除できない。
 *
 * ここに載っている ID は保存データから参照されるので、**一度出したものは変えないこと**。
 * 名前や色を直すのは構わないが、ID を変えると既存の配色が参照を失う。
 */
object BuiltInCatalog {

    // ---- 色 ------------------------------------------------------------
    // 名前は元の CustomColor のものを引き継いでいる。

    private fun color(name: String, labelRes: Int, argb: Long) = ColorOption(
        id = ColorId(builtInId(name)),
        label = DisplayText.Res(labelRes),
        color = Color(argb),
    )

    private val white = color("white", R.string.color_white, 0xFFFFFFFF)
    private val black = color("black", R.string.color_black, 0xFF000000)
    private val red = color("red", R.string.color_red, 0xFFFF0000)
    private val blue = color("blue", R.string.color_blue, 0xFF0000FF)
    private val green = color("green", R.string.color_green, 0xFF008000)
    private val yellow = color("yellow", R.string.color_yellow, 0xFFFFFF00)
    private val orange = color("orange", R.string.color_orange, 0xFFFF8C00)
    private val pink = color("pink", R.string.color_pink, 0xFFF5B2B2)
    private val lightSkyBlue = color("lightskyblue", R.string.color_light_blue, 0xFF87CEFA)
    private val lightGreen = color("lightgreen", R.string.color_yellow_green, 0xFF7CFC00)
    private val mediumSlateBlue = color("mediumslateblue", R.string.color_purple, 0xFF7B68EE)
    private val deepRoyalPurple = color("deeproyalpurple", R.string.color_purple, 0xFF47266E)
    private val midnightBlue = color("midnightblue", R.string.color_navy, 0xFF001E43)
    private val seaGreen = color("seagreen", R.string.color_green, 0xFF2E8B57)
    private val zenithBlue = color("zenithblue", R.string.color_blue, 0xFF4496D3)
    private val aquamarine = color("aquamarine", R.string.color_mint, 0xFF7FFFD4)
    private val beigeRose = color("beigerose", R.string.color_beige, 0xFFE8D3CA)
    private val vanilla = color("vanilla", R.string.color_beige, 0xFFE8C59C)
    private val sandyBrown = color("sandybrown", R.string.color_beige, 0xFFF4A460)
    private val hatimitu = color("hatimitu", R.string.color_beige, 0xFFFDDEA5)
    private val yuou = color("yuou", R.string.color_beige, 0xFFF9C89B)
    private val kikutinashi = color("kikutinashi", R.string.color_yellow, 0xFFFFDB4F)
    private val mumei = color("mumei", R.string.color_pink, 0xFFFFCCCB)
    private val rosePink = color("rosepink", R.string.color_pink, 0xFFF19CA7)
    private val tetuguro = color("tetuguro", R.string.color_brown, 0xFF281A14)
    private val kogetya = color("kogetya", R.string.color_brown, 0xFF6F4B3E)

    // ---- パレット ------------------------------------------------------
    // 並び順は Compose 移行前の ChangeColors の定義順そのまま。

    val leatherId = PaletteId("leather")
    val plasticId = PaletteId("plastic")
    val orthosisPlasticId = PaletteId("orthosis_plastic")
    val spongeId = PaletteId("sponge")
    val plSpongeId = PaletteId("pl_sponge")
    val bandId = PaletteId("band")
    val stringId = PaletteId("string")
    val buttonId = PaletteId("button")
    val whiteBlackId = PaletteId("white_black")

    val palettes: List<Palette> = listOf(
        Palette(
            leatherId, DisplayText.Res(R.string.palette_leather),
            listOf(
                white, kikutinashi, red, hatimitu, mumei, deepRoyalPurple,
                midnightBlue, blue, seaGreen, tetuguro, black,
            ),
        ),
        Palette(
            plasticId, DisplayText.Res(R.string.palette_plastic),
            listOf(white, red, blue, green, black),
        ),
        Palette(
            orthosisPlasticId, DisplayText.Res(R.string.palette_orthosis_plastic),
            listOf(white, red, yuou, rosePink, blue, lightSkyBlue, green, kogetya, black),
        ),
        Palette(
            spongeId, DisplayText.Res(R.string.palette_sponge),
            listOf(white, sandyBrown, black, orange, pink, zenithBlue, aquamarine, lightGreen),
        ),
        Palette(
            plSpongeId, DisplayText.Res(R.string.palette_pl_sponge),
            listOf(white, orange, pink, zenithBlue, aquamarine, lightGreen, black),
        ),
        Palette(
            bandId, DisplayText.Res(R.string.palette_band),
            listOf(pink, blue, lightSkyBlue, black),
        ),
        Palette(
            stringId, DisplayText.Res(R.string.palette_string),
            listOf(
                white, yellow, orange, red, beigeRose, mumei, mediumSlateBlue,
                midnightBlue, blue, lightSkyBlue, green, lightGreen, tetuguro, black,
            ),
        ),
        Palette(
            buttonId, DisplayText.Res(R.string.palette_button),
            listOf(white, kikutinashi, red, vanilla, mumei, blue, green, tetuguro, black),
        ),
        Palette(
            whiteBlackId, DisplayText.Res(R.string.palette_white_black),
            listOf(white, black),
        ),
    )

    // ---- 装具 ----------------------------------------------------------

    private fun part(device: String, index: Int, labelRes: Int, image: Int, palette: PaletteId) =
        PartSpec(
            id = PartId(builtInId("$device:$index")),
            label = DisplayText.Res(labelRes),
            image = PartImage.Bundled(image),
            paletteId = palette,
        )

    /**
     * 色を変えないレイヤー。装具の輪郭を描いた線画がこれにあたる。
     * 一番手前に置くので [parts] の末尾に並べる。
     */
    private fun lineArt(device: String, index: Int, image: Int) =
        PartSpec(
            id = PartId(builtInId("$device:$index")),
            label = DisplayText.Res(R.string.line_art),
            image = PartImage.Bundled(image),
            paletteId = null,
        )

    val devices: List<Device> = listOf(
        Device(
            id = DeviceId(builtInId("nb")),
            label = DisplayText.Res(R.string.nb),
            thumbnail = PartImage.Bundled(R.drawable.nb),
            parts = listOf(
                part("nb", 1, R.string.plastic, R.drawable.nb1, orthosisPlasticId),
                part("nb", 2, R.string.sponge, R.drawable.nb2, spongeId),
                part("nb", 3, R.string.belt1, R.drawable.nb3, leatherId),
                part("nb", 4, R.string.belt2, R.drawable.nb4, leatherId),
                part("nb", 5, R.string.belt3, R.drawable.nb5, leatherId),
                part("nb", 6, R.string.felt, R.drawable.nb6, whiteBlackId),
                part("nb", 7, R.string.string, R.drawable.nb7, stringId),
                part("nb", 8, R.string.button, R.drawable.nb8, buttonId),
                lineArt("nb", 9, R.drawable.nb9),
            ),
        ),
        Device(
            id = DeviceId(builtInId("ftn")),
            label = DisplayText.Res(R.string.ftn),
            thumbnail = PartImage.Bundled(R.drawable.ftn),
            parts = listOf(
                part("ftn", 1, R.string.body, R.drawable.ftn1, leatherId),
                part("ftn", 2, R.string.kahuband, R.drawable.ftn2, leatherId),
                part("ftn", 3, R.string.belt1, R.drawable.ftn3, leatherId),
                part("ftn", 4, R.string.belt2, R.drawable.ftn4, leatherId),
                part("ftn", 5, R.string.belt3, R.drawable.ftn5, leatherId),
                part("ftn", 6, R.string.felt, R.drawable.ftn6, whiteBlackId),
                part("ftn", 7, R.string.shoe_sole, R.drawable.ftn7, whiteBlackId),
                part("ftn", 8, R.string.button, R.drawable.ftn8, buttonId),
                part("ftn", 9, R.string.string, R.drawable.ftn9, stringId),
                lineArt("ftn", 10, R.drawable.ftn10),
            ),
        ),
        Device(
            id = DeviceId(builtInId("slb")),
            label = DisplayText.Res(R.string.slb),
            thumbnail = PartImage.Bundled(R.drawable.slb),
            parts = listOf(
                part("slb", 1, R.string.plastic, R.drawable.slb1, orthosisPlasticId),
                part("slb", 2, R.string.sponge, R.drawable.slb2, spongeId),
                part("slb", 3, R.string.kahuband, R.drawable.slb3, leatherId),
                part("slb", 4, R.string.belt1, R.drawable.slb4, leatherId),
                part("slb", 5, R.string.belt2, R.drawable.slb5, leatherId),
                part("slb", 6, R.string.felt, R.drawable.slb6, whiteBlackId),
                part("slb", 7, R.string.shoe_sole, R.drawable.slb7, whiteBlackId),
                part("slb", 8, R.string.string, R.drawable.slb8, stringId),
                part("slb", 9, R.string.button, R.drawable.slb9, buttonId),
                lineArt("slb", 10, R.drawable.slb10),
            ),
        ),
        Device(
            id = DeviceId(builtInId("pl")),
            label = DisplayText.Res(R.string.pl),
            thumbnail = PartImage.Bundled(R.drawable.pl),
            // 表示順は Compose 移行前のレイアウト準拠で
            // カン → スポンジ → ベルト → ボタン → プラスチック。
            parts = listOf(
                part("pl", 5, R.string.kan, R.drawable.pl5, whiteBlackId),
                part("pl", 2, R.string.sponge, R.drawable.pl2, plSpongeId),
                part("pl", 3, R.string.belt, R.drawable.pl3, bandId),
                part("pl", 4, R.string.button, R.drawable.pl4, buttonId),
                part("pl", 1, R.string.plastic, R.drawable.pl1, plasticId),
                lineArt("pl", 6, R.drawable.pl6),
            ),
        ),
        Device(
            id = DeviceId(builtInId("pogo")),
            label = DisplayText.Res(R.string.pogo),
            thumbnail = PartImage.Bundled(R.drawable.pogo),
            parts = listOf(
                part("pogo", 1, R.string.plastic, R.drawable.pogo1, orthosisPlasticId),
                // Compose 移行前は pogo2 も「ベルト2」表記だったが、pogo3/pogo4 と重複するため
                // 誤記と判断して「ベルト1」に直してある。
                part("pogo", 2, R.string.belt1, R.drawable.pogo2, leatherId),
                part("pogo", 3, R.string.belt2, R.drawable.pogo3, leatherId),
                part("pogo", 4, R.string.belt3, R.drawable.pogo4, leatherId),
                part("pogo", 5, R.string.ankle_band, R.drawable.pogo5, leatherId),
                part("pogo", 6, R.string.belt4, R.drawable.pogo6, leatherId),
                part("pogo", 7, R.string.button, R.drawable.pogo7, buttonId),
                part("pogo", 8, R.string.string, R.drawable.pogo8, stringId),
                lineArt("pogo", 9, R.drawable.pogo9),
            ),
        ),
        Device(
            id = DeviceId(builtInId("a")),
            label = DisplayText.Res(R.string.a),
            thumbnail = PartImage.Bundled(R.drawable.a),
            parts = listOf(
                part("a", 1, R.string.plastic, R.drawable.a1, orthosisPlasticId),
                part("a", 2, R.string.sponge, R.drawable.a2, whiteBlackId),
                part("a", 3, R.string.belt1, R.drawable.a3, leatherId),
                part("a", 4, R.string.belt2, R.drawable.a4, leatherId),
                part("a", 5, R.string.belt3, R.drawable.a5, leatherId),
                part("a", 6, R.string.belt4, R.drawable.a6, leatherId),
                part("a", 7, R.string.belt5, R.drawable.a7, leatherId),
                part("a", 8, R.string.felt, R.drawable.a8, whiteBlackId),
                part("a", 9, R.string.button, R.drawable.a9, buttonId),
                part("a", 10, R.string.string, R.drawable.a10, stringId),
                lineArt("a", 11, R.drawable.a11),
            ),
        ),
    )
}
