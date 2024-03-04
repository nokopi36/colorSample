package com.nokopi.colorsample.utils

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.nokopi.colorsample.R
import java.lang.StringBuilder

class ChangeColors {
    companion object {
        val leathersColor = listOf(
            CustomColor.white,
            CustomColor.kikutinashi,
            CustomColor.red,
            CustomColor.hatimitu,
            CustomColor.mumei,
            CustomColor.deeproyalpurple,
            CustomColor.midnightblue,
            CustomColor.blue,
            CustomColor.seagreen,
            CustomColor.tetuguro,
            CustomColor.black
        )
        val leathersColorMap = mapOf(
            R.drawable.white to "白",
            R.drawable.kikutinashi to "黄",
            R.drawable.red to "赤",
            R.drawable.hatimitu to "ベージュ",
            R.drawable.mumei to "ピンク",
            R.drawable.deeproyalpurple to "紫",
            R.drawable.midnightblue to "紺",
            R.drawable.blue to "青",
            R.drawable.seagreen to "緑",
            R.drawable.tetuguro to "茶",
            R.drawable.black to "黒"
        )

        val bandsColor =
            listOf(CustomColor.pink, CustomColor.blue, CustomColor.lightskyblue, CustomColor.black)
        val bandsColorMap = mapOf(
            R.drawable.pink to "ピンク",
            R.drawable.blue to "青",
            R.drawable.lightskyblue to "水色",
            R.drawable.black to "黒"
        )

        val spongesColor = listOf(
            CustomColor.white,
            CustomColor.sandybrown,
            CustomColor.black,
            CustomColor.orange,
            CustomColor.pink,
            CustomColor.zenithblue,
            CustomColor.aquamarine,
            CustomColor.lightgreen
        )
        val spongesColorMap = mapOf(
            R.drawable.white to "白",
            R.drawable.sandybrown to "ベージュ",
            R.drawable.black to "黒",
            R.drawable.orange to "オレンジ",
            R.drawable.pink to "ピンク",
            R.drawable.zenithblue to "青",
            R.drawable.aquamarine to "ミント",
            R.drawable.lightgreen to "黄緑"
        )

        val plasticsColor = listOf(
            CustomColor.white,
            CustomColor.red,
            CustomColor.blue,
            CustomColor.green,
            CustomColor.black
        )
        val plasticsColorMap = mapOf(
            R.drawable.white to "白",
            R.drawable.red to "赤",
            R.drawable.blue to "青",
            R.drawable.green to "緑",
            R.drawable.black to "黒"
        )

        val stringsColor = listOf(
            CustomColor.white,
            CustomColor.yellow,
            CustomColor.orange,
            CustomColor.red,
            CustomColor.beigerose,
            CustomColor.mumei,
            CustomColor.mediumslateblue,
            CustomColor.midnightblue,
            CustomColor.blue,
            CustomColor.lightskyblue,
            CustomColor.green,
            CustomColor.lightgreen,
            CustomColor.tetuguro,
            CustomColor.black
        )
        val stringsColorMap = mapOf(
            R.drawable.white to "白",
            R.drawable.yellow to "黄",
            R.drawable.orange to "オレンジ",
            R.drawable.red to "赤",
            R.drawable.beigerose to "ベージュ",
            R.drawable.mumei to "ピンク",
            R.drawable.mediumslateblue to "紫",
            R.drawable.midnightblue to "紺",
            R.drawable.blue to "青",
            R.drawable.lightskyblue to "水色",
            R.drawable.green to "緑",
            R.drawable.lightgreen to "黄緑",
            R.drawable.tetuguro to "茶",
            R.drawable.black to "黒"
        )

        val buttonsColor = listOf(
            CustomColor.white,
            CustomColor.kikutinashi,
            CustomColor.red,
            CustomColor.vanilla,
            CustomColor.mumei,
            CustomColor.blue,
            CustomColor.green,
            CustomColor.tetuguro,
            CustomColor.black
        )
        val buttonsColorMap = mapOf(
            R.drawable.white to "白",
            R.drawable.kikutinashi to "黄",
            R.drawable.red to "赤",
            R.drawable.vanilla to "ベージュ",
            R.drawable.mumei to "ピンク",
            R.drawable.blue to "青",
            R.drawable.green to "緑",
            R.drawable.tetuguro to "茶",
            R.drawable.black to "黒"
        )

        val nbSLBAPogoPlasticsColor = listOf(
            CustomColor.white,
            CustomColor.red,
            CustomColor.yuou,
            CustomColor.rosepink,
            CustomColor.blue,
            CustomColor.lightskyblue,
            CustomColor.green,
            CustomColor.kogetya,
            CustomColor.black
        )
        val nbSLBAPogoPlasticsColorMap = mapOf(
            R.drawable.white to "白",
            R.drawable.red to "赤",
            R.drawable.yuou to "ベージュ",
            R.drawable.rosepink to "ピンク",
            R.drawable.blue to "青",
            R.drawable.lightskyblue to "水色",
            R.drawable.green to "緑",
            R.drawable.kogetya to "茶",
            R.drawable.black to "黒"
        )

        val plSpongesColor = listOf(
            CustomColor.white,
            CustomColor.orange,
            CustomColor.pink,
            CustomColor.zenithblue,
            CustomColor.aquamarine,
            CustomColor.lightgreen,
            CustomColor.black
        )
        val plSpongesColorMap = mapOf(
            R.drawable.white to "白",
            R.drawable.orange to "オレンジ",
            R.drawable.pink to "ピンク",
            R.drawable.zenithblue to "青",
            R.drawable.aquamarine to "ミント",
            R.drawable.lightgreen to "黄緑",
            R.drawable.black to "黒"
        )

        val whiteBlack = listOf(CustomColor.white, CustomColor.black)
        val whiteBlackMap = mapOf(
            R.drawable.white to "白",
            R.drawable.black to "黒"
        )
    }

    fun changeNBSLBAPlasticColors(d: Drawable?, position: Int, imageView: ImageView) {
        changeColor(d!!, nbSLBAPogoPlasticsColor[position], imageView)
    }

    fun changePLSpongeColors(d: Drawable?, position: Int, imageView: ImageView) {
        changeColor(d!!, plSpongesColor[position], imageView)
    }

    fun changeBandColors(d: Drawable?, position: Int, imageView: ImageView) {
        changeColor(d!!, bandsColor[position], imageView)
    }

    fun changeLeatherColors(d: Drawable?, position: Int, imageView: ImageView) {
        changeColor(d!!, leathersColor[position], imageView)
    }

    fun changePlasticColors(d: Drawable?, position: Int, imageView: ImageView) {
        changeColor(d!!, plasticsColor[position], imageView)
    }

    fun changeSpongeColors(d: Drawable?, position: Int, imageView: ImageView) {
        changeColor(d!!, spongesColor[position], imageView)
    }

    fun changeColorsWhiteOrBlack(d: Drawable?, position: Int, imageView: ImageView) {
        changeColor(d!!, whiteBlack[position], imageView)
    }

    fun changeStringColors(d: Drawable?, position: Int, imageView: ImageView) {
        changeColor(d!!, stringsColor[position], imageView)
    }

    fun changeButtonColors(d: Drawable?, position: Int, imageView: ImageView) {
        changeColor(d!!, buttonsColor[position], imageView)
    }

    private fun changeColor(draw: Drawable, color: String, imageView: ImageView) {
        draw.setTint(Color.parseColor(color))
        imageView.setImageDrawable(draw)
        Log.i("colorCode", color)
    }

    fun changeNBSLBAPlasticColors2(d: Drawable?, position: Int): Drawable {
        return changeColor2(d!!, nbSLBAPogoPlasticsColor[position])
    }

    fun changePLSpongeColors2(d: Drawable?, position: Int): Drawable {
        return changeColor2(d!!, plSpongesColor[position])
    }

    fun changeBandColors2(d: Drawable?, position: Int): Drawable {
        return changeColor2(d!!, bandsColor[position])
    }

    fun changeLeatherColors2(d: Drawable?, position: Int): Drawable {
        return changeColor2(d!!, leathersColor[position])
    }

    fun changePlasticColors2(d: Drawable?, position: Int): Drawable {
        return changeColor2(d!!, plasticsColor[position])
    }

    fun changeSpongeColors2(d: Drawable?, position: Int): Drawable {
        return changeColor2(d!!, spongesColor[position])
    }

    fun changeColorsWhiteOrBlack2(d: Drawable?, position: Int): Drawable {
        return changeColor2(d!!, whiteBlack[position])
    }

    fun changeStringColors2(d: Drawable?, position: Int): Drawable {
        return changeColor2(d!!, stringsColor[position])
    }

    fun changeButtonColors2(d: Drawable?, position: Int): Drawable {
        return changeColor2(d!!, buttonsColor[position])
    }

    private fun changeColor2(draw: Drawable, color: String): Drawable {
        draw.setTint(Color.parseColor(color))
        return draw
    }

}