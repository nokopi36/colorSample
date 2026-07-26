package com.nokopi.colorsample.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.nokopi.colorsample.R

/**
 * 色を変えられるパーツ1つ分。[image] を [palette] から選んだ色で tint して重ねる。
 */
@Immutable
data class PartSpec(
    @StringRes val labelRes: Int,
    @DrawableRes val image: Int,
    val palette: Palette,
)

/**
 * 装具の種類。
 *
 * 6種類の画面はどれも「[parts] を順に重ねて描き、最後に tint しない [overlay] を乗せる」
 * という同じ構造なので、違いはこの表だけに閉じている。
 * [parts] の並びは描画順（先頭が最背面）であり、色選択リストの表示順も兼ねる。
 */
enum class DeviceType(
    @StringRes val titleRes: Int,
    @DrawableRes val thumbnail: Int,
    val parts: List<PartSpec>,
    @DrawableRes val overlay: Int?,
) {
    NB(
        titleRes = R.string.nb,
        thumbnail = R.drawable.nb,
        parts = listOf(
            PartSpec(R.string.plastic, R.drawable.nb1, Palette.ORTHOSIS_PLASTIC),
            PartSpec(R.string.sponge, R.drawable.nb2, Palette.SPONGE),
            PartSpec(R.string.belt1, R.drawable.nb3, Palette.LEATHER),
            PartSpec(R.string.belt2, R.drawable.nb4, Palette.LEATHER),
            PartSpec(R.string.belt3, R.drawable.nb5, Palette.LEATHER),
            PartSpec(R.string.felt, R.drawable.nb6, Palette.WHITE_BLACK),
            PartSpec(R.string.string, R.drawable.nb7, Palette.STRING),
            PartSpec(R.string.button, R.drawable.nb8, Palette.BUTTON),
        ),
        overlay = R.drawable.nb9,
    ),

    FTN(
        titleRes = R.string.ftn,
        thumbnail = R.drawable.ftn,
        parts = listOf(
            PartSpec(R.string.body, R.drawable.ftn1, Palette.LEATHER),
            PartSpec(R.string.kahuband, R.drawable.ftn2, Palette.LEATHER),
            PartSpec(R.string.belt1, R.drawable.ftn3, Palette.LEATHER),
            PartSpec(R.string.belt2, R.drawable.ftn4, Palette.LEATHER),
            PartSpec(R.string.belt3, R.drawable.ftn5, Palette.LEATHER),
            PartSpec(R.string.felt, R.drawable.ftn6, Palette.WHITE_BLACK),
            PartSpec(R.string.shoe_sole, R.drawable.ftn7, Palette.WHITE_BLACK),
            PartSpec(R.string.button, R.drawable.ftn8, Palette.BUTTON),
            PartSpec(R.string.string, R.drawable.ftn9, Palette.STRING),
        ),
        overlay = R.drawable.ftn10,
    ),

    SLB(
        titleRes = R.string.slb,
        thumbnail = R.drawable.slb,
        parts = listOf(
            PartSpec(R.string.plastic, R.drawable.slb1, Palette.ORTHOSIS_PLASTIC),
            PartSpec(R.string.sponge, R.drawable.slb2, Palette.SPONGE),
            PartSpec(R.string.kahuband, R.drawable.slb3, Palette.LEATHER),
            PartSpec(R.string.belt1, R.drawable.slb4, Palette.LEATHER),
            PartSpec(R.string.belt2, R.drawable.slb5, Palette.LEATHER),
            PartSpec(R.string.felt, R.drawable.slb6, Palette.WHITE_BLACK),
            PartSpec(R.string.shoe_sole, R.drawable.slb7, Palette.WHITE_BLACK),
            PartSpec(R.string.string, R.drawable.slb8, Palette.STRING),
            PartSpec(R.string.button, R.drawable.slb9, Palette.BUTTON),
        ),
        overlay = R.drawable.slb10,
    ),

    PL(
        titleRes = R.string.pl,
        thumbnail = R.drawable.pl,
        // 表示順は移行前のレイアウト (activity_pl_custom_color.xml) 準拠で
        // カン → スポンジ → ベルト → ボタン → プラスチック。
        parts = listOf(
            PartSpec(R.string.kan, R.drawable.pl5, Palette.WHITE_BLACK),
            PartSpec(R.string.sponge, R.drawable.pl2, Palette.PL_SPONGE),
            PartSpec(R.string.belt, R.drawable.pl3, Palette.BAND),
            PartSpec(R.string.button, R.drawable.pl4, Palette.BUTTON),
            PartSpec(R.string.plastic, R.drawable.pl1, Palette.PLASTIC),
        ),
        overlay = R.drawable.pl6,
    ),

    POGO(
        titleRes = R.string.pogo,
        thumbnail = R.drawable.pogo,
        parts = listOf(
            PartSpec(R.string.plastic, R.drawable.pogo1, Palette.ORTHOSIS_PLASTIC),
            // 移行前のレイアウトでは pogo2 のラベルも「ベルト2」になっていたが、
            // 続く pogo3/pogo4 がベルト2・ベルト3 なので誤記と判断してベルト1にしている。
            PartSpec(R.string.belt1, R.drawable.pogo2, Palette.LEATHER),
            PartSpec(R.string.belt2, R.drawable.pogo3, Palette.LEATHER),
            PartSpec(R.string.belt3, R.drawable.pogo4, Palette.LEATHER),
            PartSpec(R.string.ankle_band, R.drawable.pogo5, Palette.LEATHER),
            PartSpec(R.string.belt4, R.drawable.pogo6, Palette.LEATHER),
            PartSpec(R.string.button, R.drawable.pogo7, Palette.BUTTON),
            PartSpec(R.string.string, R.drawable.pogo8, Palette.STRING),
        ),
        overlay = R.drawable.pogo9,
    ),

    A(
        titleRes = R.string.a,
        thumbnail = R.drawable.a,
        parts = listOf(
            PartSpec(R.string.plastic, R.drawable.a1, Palette.ORTHOSIS_PLASTIC),
            PartSpec(R.string.sponge, R.drawable.a2, Palette.WHITE_BLACK),
            PartSpec(R.string.belt1, R.drawable.a3, Palette.LEATHER),
            PartSpec(R.string.belt2, R.drawable.a4, Palette.LEATHER),
            PartSpec(R.string.belt3, R.drawable.a5, Palette.LEATHER),
            PartSpec(R.string.belt4, R.drawable.a6, Palette.LEATHER),
            PartSpec(R.string.belt5, R.drawable.a7, Palette.LEATHER),
            PartSpec(R.string.felt, R.drawable.a8, Palette.WHITE_BLACK),
            PartSpec(R.string.button, R.drawable.a9, Palette.BUTTON),
            PartSpec(R.string.string, R.drawable.a10, Palette.STRING),
        ),
        overlay = R.drawable.a11,
    ),
    ;
    // ホーム画面に並べる順は enum の宣言順。移行前の MainActivity の並びに合わせてある。
}
