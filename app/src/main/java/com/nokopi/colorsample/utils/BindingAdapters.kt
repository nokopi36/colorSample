package com.nokopi.colorsample.utils

import android.widget.Spinner
import androidx.databinding.BindingAdapter
import com.nokopi.colorsample.view.CustomSpinnerAdapter

object BindingAdapters {
    @BindingAdapter("setCustomSpinnerAdapterId","setCustomSpinnerAdapterString")
    @JvmStatic
    fun setCustomSpinnerAdapter(view: Spinner, imageResIdList: List<Int>, imageTextList: List<String>){
        view.adapter = CustomSpinnerAdapter(imageResIdList, imageTextList)
    }
}