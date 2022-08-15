package com.nokopi.colorsample.utils

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.nokopi.colorsample.R

class ChangeColors {
    companion object{
        val leathersColorSpinner = listOf(
            R.drawable.button_white,
            R.drawable.button_kikutinashi,
            R.drawable.button_red,
            R.drawable.button_hatimitu,
            R.drawable.button_mumei,
            R.drawable.button_deeproyalpurple,
            R.drawable.button_midnightblue,
            R.drawable.button_blue,
            R.drawable.button_seagreen,
            R.drawable.button_tetuguro,
            R.drawable.button_black
        )
        val leathersColor = listOf(
            CustomColor.white, CustomColor.kikutinashi, CustomColor.red, CustomColor.hatimitu, CustomColor.mumei, CustomColor.deeproyalpurple,
            CustomColor.midnightblue, CustomColor.blue, CustomColor.seagreen, CustomColor.tetuguro, CustomColor.black)

        val bandsColorSpinner = listOf(
            R.drawable.button_pink,
            R.drawable.button_blue,
            R.drawable.button_lightskyblue,
            R.drawable.button_black
        )
        val bandsColor = listOf(CustomColor.pink, CustomColor.blue, CustomColor.lightskyblue, CustomColor.black)

        val spongesColorSpinner = listOf(
            R.drawable.button_white,
            R.drawable.button_sandybrown,
            R.drawable.button_black,
            R.drawable.button_orange,
            R.drawable.button_pink,
            R.drawable.button_zenithblue
            ,
            R.drawable.button_aquamarine,
            R.drawable.button_lightgreen
        )
        val spongesColor = listOf(
            CustomColor.white, CustomColor.sandybrown, CustomColor.black, CustomColor.orange, CustomColor.pink, CustomColor.zenithblue
            , CustomColor.aquamarine, CustomColor.lightgreen)

        val plasticsColorSpinner = listOf(
            R.drawable.button_white,
            R.drawable.button_red,
            R.drawable.button_blue,
            R.drawable.button_green,
            R.drawable.button_black
        )
        val plasticsColor = listOf(CustomColor.white, CustomColor.red, CustomColor.blue, CustomColor.green, CustomColor.black)

        val stringsColorSpinner = listOf(
            R.drawable.button_white,
            R.drawable.button_yellow,
            R.drawable.button_orange,
            R.drawable.button_red,
            R.drawable.button_beigerose,
            R.drawable.button_mumei,
            R.drawable.button_mediumslateblue,
            R.drawable.button_midnightblue,
            R.drawable.button_blue,
            R.drawable.button_lightskyblue,
            R.drawable.button_green,
            R.drawable.button_lightgreen,
            R.drawable.button_tetuguro,
            R.drawable.button_black
        )
        val stringsColor = listOf(
            CustomColor.white, CustomColor.yellow, CustomColor.orange, CustomColor.red, CustomColor.beigerose, CustomColor.mumei,
            CustomColor.mediumslateblue, CustomColor.midnightblue, CustomColor.blue, CustomColor.lightskyblue, CustomColor.green, CustomColor.lightgreen, CustomColor.tetuguro,
            CustomColor.black)

        val buttonsColorSpinner = listOf(
            R.drawable.button_white,
            R.drawable.button_kikutinashi,
            R.drawable.button_red,
            R.drawable.button_vanilla,
            R.drawable.button_mumei,
            R.drawable.button_blue,
            R.drawable.button_green,
            R.drawable.button_tetuguro,
            R.drawable.button_black
        )
        val buttonsColor = listOf(
            CustomColor.white, CustomColor.kikutinashi, CustomColor.red, CustomColor.vanilla, CustomColor.mumei, CustomColor.blue, CustomColor.green,
            CustomColor.tetuguro, CustomColor.black)

        val nbSLBPlasticsColorSpinner = listOf(
            R.drawable.button_white,
            R.drawable.button_red,
            R.drawable.button_yuou,
            R.drawable.button_rosepink,
            R.drawable.button_blue,
            R.drawable.button_lightskyblue,
            R.drawable.button_green,
            R.drawable.button_kogetya,
            R.drawable.button_black
        )
        val nbSLBPlasticsColor = listOf(
            CustomColor.white, CustomColor.red, CustomColor.yuou, CustomColor.rosepink, CustomColor.blue, CustomColor.lightskyblue, CustomColor.green,
            CustomColor.kogetya, CustomColor.black)

        val plSpongesColorSpinner = listOf(
            R.drawable.button_white,
            R.drawable.button_orange,
            R.drawable.button_pink,
            R.drawable.button_zenithblue,
            R.drawable.button_aquamarine
            ,
            R.drawable.button_lightgreen,
            R.drawable.button_black
        )
        val plSpongesColor = listOf(
            CustomColor.white, CustomColor.orange, CustomColor.pink, CustomColor.zenithblue, CustomColor.aquamarine
            , CustomColor.lightgreen, CustomColor.black)

        val whiteBlackSpinner = listOf(R.drawable.button_white, R.drawable.button_black)
        val whiteBlack = listOf(CustomColor.white, CustomColor.black)
    }

    fun changeNBSLBPlasticColors(d: Drawable?, position: Int, imageView: ImageView){
                changeColor(d!!, nbSLBPlasticsColor[position], imageView)
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

}