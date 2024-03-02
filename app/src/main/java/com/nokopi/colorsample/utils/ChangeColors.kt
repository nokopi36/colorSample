package com.nokopi.colorsample.utils

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.nokopi.colorsample.R

class ChangeColors {
    companion object{
        val leathersColorSpinner = listOf(
            R.drawable.white,
            R.drawable.kikutinashi,
            R.drawable.red,
            R.drawable.hatimitu,
            R.drawable.mumei,
            R.drawable.deeproyalpurple,
            R.drawable.midnightblue,
            R.drawable.blue,
            R.drawable.seagreen,
            R.drawable.tetuguro,
            R.drawable.black
        )
        val leathersColorSpinnerString = listOf("白", "黄", "赤", "ベージュ", "ピンク", "紫", "紺", "青", "緑", "茶", "黒")
        val leathersColor = listOf(
            CustomColor.white, CustomColor.kikutinashi, CustomColor.red, CustomColor.hatimitu, CustomColor.mumei, CustomColor.deeproyalpurple,
            CustomColor.midnightblue, CustomColor.blue, CustomColor.seagreen, CustomColor.tetuguro, CustomColor.black)

        val bandsColorSpinner = listOf(
            R.drawable.pink,
            R.drawable.blue,
            R.drawable.lightskyblue,
            R.drawable.black
        )
        val bandsColorSpinnerString = listOf("ピンク", "青", "水色", "黒")
        val bandsColor = listOf(CustomColor.pink, CustomColor.blue, CustomColor.lightskyblue, CustomColor.black)

        val spongesColorSpinner = listOf(
            R.drawable.white,
            R.drawable.sandybrown,
            R.drawable.black,
            R.drawable.orange,
            R.drawable.pink,
            R.drawable.zenithblue,
            R.drawable.aquamarine,
            R.drawable.lightgreen
        )
        val spongesColorSpinnerString = listOf("白", "ベージュ", "黒", "オレンジ", "ピンク", "青", "ミント", "黄緑")
        val spongesColor = listOf(
            CustomColor.white, CustomColor.sandybrown, CustomColor.black, CustomColor.orange, CustomColor.pink, CustomColor.zenithblue
            , CustomColor.aquamarine, CustomColor.lightgreen)

        val plasticsColorSpinner = listOf(
            R.drawable.white,
            R.drawable.red,
            R.drawable.blue,
            R.drawable.green,
            R.drawable.black
        )
        val plasticsColorSpinnerString = listOf("白", "赤", "青", "緑", "黒")
        val plasticsColor = listOf(CustomColor.white, CustomColor.red, CustomColor.blue, CustomColor.green, CustomColor.black)

        val stringsColorSpinner = listOf(
            R.drawable.white,
            R.drawable.yellow,
            R.drawable.orange,
            R.drawable.red,
            R.drawable.beigerose,
            R.drawable.mumei,
            R.drawable.mediumslateblue,
            R.drawable.midnightblue,
            R.drawable.blue,
            R.drawable.lightskyblue,
            R.drawable.green,
            R.drawable.lightgreen,
            R.drawable.tetuguro,
            R.drawable.black
        )
        val stringsColorSpinnerString = listOf("白", "黄", "オレンジ", "赤", "ベージュ", "ピンク", "紫", "紺", "青", "水色", "緑", "黄緑", "茶", "黒")
        val stringsColor = listOf(
            CustomColor.white, CustomColor.yellow, CustomColor.orange, CustomColor.red, CustomColor.beigerose, CustomColor.mumei,
            CustomColor.mediumslateblue, CustomColor.midnightblue, CustomColor.blue, CustomColor.lightskyblue, CustomColor.green, CustomColor.lightgreen, CustomColor.tetuguro,
            CustomColor.black)

        val buttonsColorSpinner = listOf(
            R.drawable.white,
            R.drawable.kikutinashi,
            R.drawable.red,
            R.drawable.vanilla,
            R.drawable.mumei,
            R.drawable.blue,
            R.drawable.green,
            R.drawable.tetuguro,
            R.drawable.black
        )
        val buttonsColorSpinnerString = listOf("白", "黄", "赤", "ベージュ", "ピンク", "青", "緑", "茶", "黒")
        val buttonsColor = listOf(
            CustomColor.white, CustomColor.kikutinashi, CustomColor.red, CustomColor.vanilla, CustomColor.mumei, CustomColor.blue, CustomColor.green,
            CustomColor.tetuguro, CustomColor.black)

        val nbSLBAPogoPlasticsColorSpinner = listOf(
            R.drawable.white,
            R.drawable.red,
            R.drawable.yuou,
            R.drawable.rosepink,
            R.drawable.blue,
            R.drawable.lightskyblue,
            R.drawable.green,
            R.drawable.kogetya,
            R.drawable.black
        )
        val nbSLBAPogoPlasticsColorSpinnerString = listOf("白", "赤", "ベージュ", "ピンク", "青", "水色", "緑", "茶", "黒")
        val nbSLBAPogoPlasticsColor = listOf(
            CustomColor.white, CustomColor.red, CustomColor.yuou, CustomColor.rosepink, CustomColor.blue, CustomColor.lightskyblue, CustomColor.green,
            CustomColor.kogetya, CustomColor.black)

        val plSpongesColorSpinner = listOf(
            R.drawable.white,
            R.drawable.orange,
            R.drawable.pink,
            R.drawable.zenithblue,
            R.drawable.aquamarine,
            R.drawable.lightgreen,
            R.drawable.black
        )
        val plSpongeColorSpinnerString = listOf("白", "オレンジ", "ピンク", "青", "ミント", "黄緑", "黒")
        val plSpongesColor = listOf(
            CustomColor.white, CustomColor.orange, CustomColor.pink, CustomColor.zenithblue, CustomColor.aquamarine
            , CustomColor.lightgreen, CustomColor.black)

        val whiteBlackSpinner = listOf(R.drawable.white, R.drawable.black)
        val whiteBlackSpinnerString = listOf("白", "黒")
        val whiteBlack = listOf(CustomColor.white, CustomColor.black)
    }

    fun changeNBSLBAPlasticColors(d: Drawable?, position: Int, imageView: ImageView){
                changeColor(d!!, nbSLBAPogoPlasticsColor[position], imageView)
    }

    fun changePLSpongeColors(d: Drawable?, position: Int, imageView: ImageView){
                changeColor(d!!, plSpongesColor[position], imageView)
    }

    fun changeBandColors(d: Drawable?, position: Int, imageView: ImageView){
                changeColor(d!!, bandsColor[position], imageView)
    }

    fun changeLeatherColors(d: Drawable?, position: Int, imageView: ImageView){
                changeColor(d!!, leathersColor[position], imageView)
    }

    fun changePlasticColors(d: Drawable?, position: Int, imageView: ImageView){
                changeColor(d!!, plasticsColor[position], imageView)
    }

    fun changeSpongeColors(d: Drawable?, position: Int, imageView: ImageView){
                changeColor(d!!, spongesColor[position], imageView)
    }

    fun changeColorsWhiteOrBlack(d: Drawable?, position: Int, imageView: ImageView){
                changeColor(d!!, whiteBlack[position], imageView)
    }

    fun changeStringColors(d: Drawable?, position: Int, imageView: ImageView){
                changeColor(d!!, stringsColor[position], imageView)
    }

    fun changeButtonColors(d: Drawable?, position: Int, imageView: ImageView){
                changeColor(d!!, buttonsColor[position], imageView)
    }

    private fun changeColor(draw : Drawable, color : String, imageView: ImageView){
        draw.setTint(Color.parseColor(color))
        imageView.setImageDrawable(draw)
        Log.i("colorCode", color)
    }

    fun changeNBSLBAPlasticColors2(d: Drawable?, position: Int): Drawable{
        return changeColor2(d!!, nbSLBAPogoPlasticsColor[position])
    }

    fun changePLSpongeColors2(d: Drawable?, position: Int): Drawable{
        return changeColor2(d!!, plSpongesColor[position])
    }

    fun changeBandColors2(d: Drawable?, position: Int): Drawable{
        return changeColor2(d!!, bandsColor[position])
    }

    fun changeLeatherColors2(d: Drawable?, position: Int): Drawable{
        return changeColor2(d!!, leathersColor[position])
    }

    fun changePlasticColors2(d: Drawable?, position: Int): Drawable{
        return changeColor2(d!!, plasticsColor[position])
    }

    fun changeSpongeColors2(d: Drawable?, position: Int): Drawable{
        return changeColor2(d!!, spongesColor[position])
    }

    fun changeColorsWhiteOrBlack2(d: Drawable?, position: Int): Drawable{
        return changeColor2(d!!, whiteBlack[position])
    }

    fun changeStringColors2(d: Drawable?, position: Int): Drawable{
        return changeColor2(d!!, stringsColor[position])
    }

    fun changeButtonColors2(d: Drawable?, position: Int): Drawable{
        return changeColor2(d!!, buttonsColor[position])
    }

    private fun changeColor2(draw : Drawable, color : String): Drawable{
        draw.setTint(Color.parseColor(color))
        return draw
    }

}