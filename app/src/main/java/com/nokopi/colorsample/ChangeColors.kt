package com.nokopi.colorsample

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView

class ChangeColors {
    companion object{
        val leathersColorSpinner = listOf(R.drawable.button_white, R.drawable.button_black, R.drawable.button_sepia, R.drawable.button_momosiotya, R.drawable.button_mediumslateblue, R.drawable.button_red,
            R.drawable.button_blue, R.drawable.button_summergreen, R.drawable.button_mandarinorange, R.drawable.button_framingopink, R.drawable.button_buff)
        val leathersColor = listOf(CustomColor.white, CustomColor.black, CustomColor.sepia, CustomColor.momosiotya, CustomColor.mediumslateblue, CustomColor.red,
            CustomColor.blue, CustomColor.summergreen, CustomColor.mandarinorange, CustomColor.framingopink, CustomColor.buff)

        val bandsColorSpinner = listOf(R.drawable.button_black, R.drawable.button_blue, R.drawable.button_lightblue, R.drawable.button_hotpink)
        val bandsColor = listOf(CustomColor.black, CustomColor.blue, CustomColor.lightblue, CustomColor.hotpink)

        val spongesColorSpinner = listOf(R.drawable.button_lightsalmon, R.drawable.button_white, R.drawable.button_black, R.drawable.button_mint, R.drawable.button_lightgreen, R.drawable.button_deepskyblue
            , R.drawable.button_orange, R.drawable.button_pink)
        val spongesColor = listOf(CustomColor.lightsalmon, CustomColor.white, CustomColor.black, CustomColor.mint, CustomColor.lightgreen, CustomColor.deepskyblue
            , CustomColor.orange, CustomColor.pink)

        val plasticsColorSpinner = listOf(R.drawable.button_white, R.drawable.button_black, R.drawable.button_blue, R.drawable.button_green, R.drawable.button_red)
        val plasticsColor = listOf(CustomColor.white, CustomColor.black, CustomColor.blue, CustomColor.green, CustomColor.red)

        val stringsColorSpinner = listOf(R.drawable.button_orange, R.drawable.button_black, R.drawable.button_sepia, R.drawable.button_lightblue, R.drawable.button_mediumslateblue, R.drawable.button_blue,
            R.drawable.button_red, R.drawable.button_lightgreen, R.drawable.button_mandarinorange, R.drawable.button_framingopink, R.drawable.button_beigerose, R.drawable.button_sapphireblue)
        val stringsColor = listOf(CustomColor.orange, CustomColor.black, CustomColor.sepia, CustomColor.lightblue, CustomColor.mediumslateblue, CustomColor.blue,
            CustomColor.red, CustomColor.lightgreen, CustomColor.mandarinorange, CustomColor.framingopink, CustomColor.beigerose, CustomColor.sapphireblue)

        val buttonsColorSpinner = listOf(R.drawable.button_white, R.drawable.button_black, R.drawable.button_blue, R.drawable.button_red, R.drawable.button_green, R.drawable.button_yamabukitya, R.drawable.button_sepia,
            R.drawable.button_framingopink, R.drawable.button_vanilla)
        val buttonsColor = listOf(CustomColor.white, CustomColor.black, CustomColor.blue, CustomColor.red, CustomColor.green, CustomColor.yamabukitya, CustomColor.sepia,
            CustomColor.framingopink, CustomColor.vanilla)

        val nbSLBPlasticsColorSpinner = listOf(R.drawable.button_lightsalmon, R.drawable.button_white, R.drawable.button_black, R.drawable.button_red, R.drawable.button_green, R.drawable.button_blue, R.drawable.button_deepskyblue, R.drawable.button_maroon,
            R.drawable.button_hotpink)
        val nbSLBPlasticsColor = listOf(CustomColor.lightsalmon, CustomColor.white, CustomColor.black, CustomColor.red,  CustomColor.green, CustomColor.blue, CustomColor.deepskyblue, CustomColor.maroon,
            CustomColor.hotpink)

        val plSpongesColorSpinner = listOf(R.drawable.button_white, R.drawable.button_black, R.drawable.button_mint, R.drawable.button_lightgreen, R.drawable.button_deepskyblue
            , R.drawable.button_orange, R.drawable.button_pink)
        val plSpongesColor = listOf(CustomColor.white, CustomColor.black, CustomColor.mint, CustomColor.lightgreen, CustomColor.deepskyblue
            , CustomColor.orange, CustomColor.pink)

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