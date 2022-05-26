package com.nokopi.colorsample

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView

class ChangeColors {
    companion object{
        val leathersColorSpinner = listOf(R.drawable.button_white, R.drawable.button_mandarinorange, R.drawable.button_red, R.drawable.button_buff, R.drawable.button_framingopink, R.drawable.button_mediumslateblue,
            R.drawable.button_momosiotya, R.drawable.button_blue, R.drawable.button_summergreen, R.drawable.button_sepia, R.drawable.button_black)
        val leathersColor = listOf(CustomColor.white, CustomColor.mandarinorange, CustomColor.red, CustomColor.buff, CustomColor.framingopink, CustomColor.mediumslateblue,
            CustomColor.momosiotya, CustomColor.blue, CustomColor.summergreen, CustomColor.sepia, CustomColor.black)

        val bandsColorSpinner = listOf(R.drawable.button_hotpink, R.drawable.button_blue, R.drawable.button_lightblue, R.drawable.button_black)
        val bandsColor = listOf(CustomColor.hotpink, CustomColor.blue, CustomColor.lightblue, CustomColor.black)

        val spongesColorSpinner = listOf(R.drawable.button_white, R.drawable.button_lightsalmon, R.drawable.button_black, R.drawable.button_orange, R.drawable.button_pink, R.drawable.button_deepskyblue
            , R.drawable.button_mint, R.drawable.button_lightgreen)
        val spongesColor = listOf(CustomColor.white, CustomColor.lightsalmon, CustomColor.black, CustomColor.orange, CustomColor.pink, CustomColor.deepskyblue
            , CustomColor.mint, CustomColor.lightgreen)

        val plasticsColorSpinner = listOf(R.drawable.button_white, R.drawable.button_red, R.drawable.button_blue, R.drawable.button_green, R.drawable.button_black)
        val plasticsColor = listOf(CustomColor.white, CustomColor.red, CustomColor.blue, CustomColor.green, CustomColor.black)

        val stringsColorSpinner = listOf(R.drawable.button_white, R.drawable.button_mandarinorange, R.drawable.button_orange, R.drawable.button_red, R.drawable.button_beigerose, R.drawable.button_framingopink,
            R.drawable.button_mediumslateblue, R.drawable.button_sapphireblue, R.drawable.button_blue, R.drawable.button_lightblue, R.drawable.button_green, R.drawable.button_lightgreen, R.drawable.button_sepia,
            R.drawable.button_black)
        val stringsColor = listOf(CustomColor.white, CustomColor.mandarinorange, CustomColor.orange, CustomColor.red, CustomColor.beigerose, CustomColor.framingopink,
            CustomColor.mediumslateblue, CustomColor.sapphireblue, CustomColor.blue, CustomColor.lightblue, CustomColor.green, CustomColor.lightgreen, CustomColor.sepia,
            CustomColor.black)

        val buttonsColorSpinner = listOf(R.drawable.button_white, R.drawable.button_yamabukitya, R.drawable.button_red, R.drawable.button_vanilla, R.drawable.button_framingopink, R.drawable.button_blue, R.drawable.button_green,
            R.drawable.button_sepia, R.drawable.button_black)
        val buttonsColor = listOf(CustomColor.white, CustomColor.yamabukitya, CustomColor.red, CustomColor.vanilla, CustomColor.framingopink, CustomColor.blue, CustomColor.green,
            CustomColor.sepia, CustomColor.black)

        val nbSLBPlasticsColorSpinner = listOf(R.drawable.button_white, R.drawable.button_red, R.drawable.button_lightsalmon, R.drawable.button_hotpink, R.drawable.button_blue, R.drawable.button_deepskyblue, R.drawable.button_green,
            R.drawable.button_maroon, R.drawable.button_black)
        val nbSLBPlasticsColor = listOf(CustomColor.white, CustomColor.red, CustomColor.lightsalmon, CustomColor.hotpink, CustomColor.blue, CustomColor.deepskyblue, CustomColor.green,
            CustomColor.maroon, CustomColor.black)

        val plSpongesColorSpinner = listOf(R.drawable.button_white, R.drawable.button_orange, R.drawable.button_pink, R.drawable.button_deepskyblue, R.drawable.button_mint
            , R.drawable.button_lightgreen, R.drawable.button_black)
        val plSpongesColor = listOf(CustomColor.white, CustomColor.orange, CustomColor.pink, CustomColor.deepskyblue, CustomColor.mint
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