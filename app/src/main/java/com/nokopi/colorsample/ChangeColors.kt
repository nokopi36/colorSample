package com.nokopi.colorsample

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView

class ChangeColors {
    companion object{
        val leathersColor = listOf(R.drawable.button_white, R.drawable.button_black, R.drawable.button_sepia, R.drawable.button_momosiotya, R.drawable.button_mediumslateblue, R.drawable.button_red,
            R.drawable.button_blue, R.drawable.button_summegreen, R.drawable.button_mandarinorange, R.drawable.button_framingopink, R.drawable.button_buff)
        val bandsColor = listOf(R.drawable.button_black, R.drawable.button_blue, R.drawable.button_lightblue, R.drawable.butyon_hotpink)
        val spongesColor = listOf(R.drawable.button_lightsalmon, R.drawable.button_white, R.drawable.button_black, R.drawable.button_mint, R.drawable.button_lightgreen, R.drawable.button_deepskyblue
            , R.drawable.button_orange, R.drawable.button_pink)
        val plasticsColor = listOf(R.drawable.button_white, R.drawable.button_black, R.drawable.button_blue, R.drawable.button_green, R.drawable.button_red)
        val stringsColor = listOf(R.drawable.button_orange, R.drawable.button_black, R.drawable.button_sepia, R.drawable.button_lightblue, R.drawable.button_mediumslateblue, R.drawable.button_blue,
            R.drawable.button_red, R.drawable.button_lightgreen, R.drawable.button_mandarinorange, R.drawable.button_framingopink, R.drawable.button_beigerose, R.drawable.button_sapphireblue)
        val buttonsColor = listOf(R.drawable.button_white, R.drawable.button_black, R.drawable.button_blue, R.drawable.button_red, R.drawable.button_green, R.drawable.button_yamabukitya, R.drawable.button_sepia,
            R.drawable.button_framingopink, R.drawable.button_vanilla)
        val nbSLBPlasticsColor = listOf(R.drawable.button_lightsalmon, R.drawable.button_white, R.drawable.button_black, R.drawable.button_red, R.drawable.button_green, R.drawable.button_blue, R.drawable.button_deepskyblue, R.drawable.button_maroon,
            R.drawable.butyon_hotpink)
        val plSpongesColor = listOf(R.drawable.button_white, R.drawable.button_black, R.drawable.button_mint, R.drawable.button_lightgreen, R.drawable.button_deepskyblue
            , R.drawable.button_orange, R.drawable.button_pink)
        val whiteBlack = listOf(R.drawable.button_white, R.drawable.button_black)
    }

    fun changeNBSLBPlasticColors(d: Drawable?, position: Int, imageView: ImageView){
        when(position){
            0 -> {
                changeColor(d!!, CustomColor.lightsalmon, imageView)
            }
            1 -> {
                changeColor(d!!, CustomColor.white, imageView)
            }
            2 -> {
                changeColor(d!!, CustomColor.black, imageView)
            }
            3 -> {
                changeColor(d!!, CustomColor.red, imageView)
            }
            4 -> {
                changeColor(d!!, CustomColor.green, imageView)
            }
            5 -> {
                changeColor(d!!, CustomColor.blue, imageView)
            }
            6 -> {
                changeColor(d!!, CustomColor.deepskyblue, imageView)
            }
            7 -> {
                changeColor(d!!, CustomColor.maroon, imageView)
            }
            8 -> {
                changeColor(d!!, CustomColor.hotpink, imageView)
            }
        }
    }

    fun changePLSpongeColors(d: Drawable?, position: Int, imageView: ImageView){
        when(position){
            0 -> {
                changeColor(d!!, CustomColor.white, imageView)
            }
            1 -> {
                changeColor(d!!, CustomColor.black, imageView)
            }
            2 -> {
                changeColor(d!!, CustomColor.mint, imageView)
            }
            3 -> {
                changeColor(d!!, CustomColor.lightgreen, imageView)
            }
            4 -> {
                changeColor(d!!, CustomColor.deepskyblue, imageView)
            }
            5 -> {
                changeColor(d!!, CustomColor.orange, imageView)
            }
            6 -> {
                changeColor(d!!, CustomColor.pink, imageView)
            }
        }
    }

    fun changeBandColors(d: Drawable?, position: Int, imageView: ImageView){
        when(position){
            0 -> {
                changeColor(d!!, CustomColor.black, imageView)
            }
            1 -> {
                changeColor(d!!, CustomColor.blue, imageView)
            }
            2 -> {
                changeColor(d!!, CustomColor.lightblue, imageView)
            }
            3 -> {
                changeColor(d!!, CustomColor.hotpink, imageView)
            }
        }
    }

    fun changeLeatherColors(d: Drawable?, position: Int, imageView: ImageView){
        when(position){
            0 -> {
                changeColor(d!!, CustomColor.white, imageView)
            }
            1 -> {
                changeColor(d!!, CustomColor.black, imageView)
            }
            2 -> {
                changeColor(d!!, CustomColor.sepia, imageView)
            }
            3 -> {
                changeColor(d!!, CustomColor.momosiotya, imageView)
            }
            4 -> {
                changeColor(d!!, CustomColor.mediumslateblue, imageView)
            }
            5 -> {
                changeColor(d!!, CustomColor.red, imageView)
            }
            6 -> {
                changeColor(d!!, CustomColor.blue, imageView)
            }
            7 -> {
                changeColor(d!!, CustomColor.summergreen, imageView)
            }
            8 -> {
                changeColor(d!!, CustomColor.mandarinorange, imageView)
            }
            9 -> {
                changeColor(d!!, CustomColor.framingopink, imageView)
            }
            10 -> {
                changeColor(d!!, CustomColor.buff, imageView)
            }
        }
    }

    fun changePlasticColors(d: Drawable?, position: Int, imageView: ImageView){
        when(position){
            0 -> {
                changeColor(d!!, CustomColor.white, imageView)
            }
            1 -> {
                changeColor(d!!, CustomColor.black, imageView)
            }
            2 -> {
                changeColor(d!!, CustomColor.blue, imageView)
            }
            3 -> {
                changeColor(d!!, CustomColor.green, imageView)
            }
            4 -> {
                changeColor(d!!, CustomColor.red, imageView)
            }
        }
    }

    fun changeSpongeColors(d: Drawable?, position: Int, imageView: ImageView){
        when(position){
            0 -> {
                changeColor(d!!, CustomColor.lightsalmon, imageView)
            }
            1 -> {
                changeColor(d!!, CustomColor.white, imageView)
            }
            2 -> {
                changeColor(d!!, CustomColor.black, imageView)
            }
            3 -> {
                changeColor(d!!, CustomColor.mint, imageView)
            }
            4 -> {
                changeColor(d!!, CustomColor.lightgreen, imageView)
            }
            5 -> {
                changeColor(d!!, CustomColor.deepskyblue, imageView)
            }
            6 -> {
                changeColor(d!!, CustomColor.orange, imageView)
            }
            7 -> {
                changeColor(d!!, CustomColor.pink, imageView)
            }
        }
    }

    fun changeColorsWhiteOrBlack(d: Drawable?, position: Int, imageView: ImageView){
        when(position){
            0 -> {
                changeColor(d!!, CustomColor.white, imageView)
            }
            1 -> {
                changeColor(d!!, CustomColor.black, imageView)
            }
        }
    }

    fun changeStringColors(d: Drawable?, position: Int, imageView: ImageView){
        when(position){
            0 -> {
                changeColor(d!!, CustomColor.orange, imageView)
            }
            1 -> {
                changeColor(d!!, CustomColor.black, imageView)
            }
            2 -> {
                changeColor(d!!, CustomColor.sepia, imageView)
            }
            3 -> {
                changeColor(d!!, CustomColor.lightblue, imageView)
            }
            4 -> {
                changeColor(d!!, CustomColor.mediumslateblue, imageView)
            }
            5 -> {
                changeColor(d!!, CustomColor.blue, imageView)
            }
            6 -> {
                changeColor(d!!, CustomColor.red, imageView)
            }
            7 -> {
                changeColor(d!!, CustomColor.lightgreen, imageView)
            }
            8 -> {
                changeColor(d!!, CustomColor.mandarinorange, imageView)
            }
            9 -> {
                changeColor(d!!, CustomColor.framingopink, imageView)
            }
            10 -> {
                changeColor(d!!, CustomColor.beigerose, imageView)
            }
            11 -> {
                changeColor(d!!, CustomColor.sapphireblue, imageView)
            }
        }
    }

    fun changeButtonColors(d: Drawable?, position: Int, imageView: ImageView){
        when(position){
            0 -> {
                changeColor(d!!, CustomColor.white, imageView)
            }
            1 -> {
                changeColor(d!!, CustomColor.black, imageView)
            }
            2 -> {
                changeColor(d!!, CustomColor.blue, imageView)
            }
            3 -> {
                changeColor(d!!, CustomColor.red, imageView)
            }
            4 -> {
                changeColor(d!!, CustomColor.green, imageView)
            }
            5 -> {
                changeColor(d!!, CustomColor.yamabukitya, imageView)
            }
            6 -> {
                changeColor(d!!, CustomColor.sepia, imageView)
            }
            7 -> {
                changeColor(d!!, CustomColor.framingopink, imageView)
            }
            8 -> {
                changeColor(d!!, CustomColor.vanilla, imageView)
            }
        }
    }

    private fun changeColor(draw : Drawable, color : String, imageView: ImageView){
        draw.setTint(Color.parseColor(color))
        imageView.setImageDrawable(draw)
        Log.i("colorCode", color)
    }

}