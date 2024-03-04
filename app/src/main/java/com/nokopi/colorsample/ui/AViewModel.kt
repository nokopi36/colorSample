package com.nokopi.colorsample.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nokopi.colorsample.R
import com.nokopi.colorsample.utils.ChangeColors

class AViewModel(
    context: Context
) : ViewModel() {
    private val changeColors = ChangeColors()
    val plasticsColor = ChangeColors.nbSLBAPogoPlasticsColorMap.keys
    val plasticsColorString = ChangeColors.nbSLBAPogoPlasticsColorMap.values
    val whiteBlack = ChangeColors.whiteBlackMap.keys
    val whiteBlackString = ChangeColors.whiteBlackMap.values
    val leatherColor = ChangeColors.leathersColorMap.keys
    val leatherColorString = ChangeColors.leathersColorMap.values
    val buttonColor = ChangeColors.buttonsColorMap.keys
    val buttonColorString = ChangeColors.buttonsColorMap.values
    val stringColor = ChangeColors.stringsColorMap.keys
    val stringColorString = ChangeColors.stringsColorMap.values

    private val a1 = ContextCompat.getDrawable(context, R.drawable.a1)
    private val _d1: MutableLiveData<Drawable> =
        MutableLiveData<Drawable>().also { mutableLiveData ->
            mutableLiveData.value = a1
        }
    val d1: LiveData<Drawable>
        get() = _d1
//    fun onItemSelected1(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//        _d1.value = changeColors.changeNBSLBAPlasticColors2(a1, position)
//    }

    fun updateD1(position: Int) {
        _d1.value = changeColors.changeNBSLBAPlasticColors2(a1, position)
    }

    private val a2 = ContextCompat.getDrawable(context, R.drawable.a2)
    private val _d2: MutableLiveData<Drawable> =
        MutableLiveData<Drawable>().also { mutableLiveData ->
            mutableLiveData.value = a2
        }
    val d2: LiveData<Drawable>
        get() = _d2
//    fun onItemSelected2(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//        Log.i("onItemSelected2", "$position")
//        _d2.value = changeColors.changeColorsWhiteOrBlack2(a2, position)
//    }
    fun updateD2(position: Int) {
        _d2.value = changeColors.changeColorsWhiteOrBlack2(a2, position)
    }

    private val a3 = ContextCompat.getDrawable(context, R.drawable.a3)
    private val _d3: MutableLiveData<Drawable> =
        MutableLiveData<Drawable>().also { mutableLiveData ->
            mutableLiveData.value = a3
        }
    val d3: LiveData<Drawable>
        get() = _d3
//    fun onItemSelected3(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//        _d3.value = changeColors.changeLeatherColors2(a3, position)
//    }
    fun updateD3(position: Int) {
        _d3.value = changeColors.changeLeatherColors2(a3, position)
    }

    private val a4 = ContextCompat.getDrawable(context, R.drawable.a4)
    private val _d4: MutableLiveData<Drawable> =
        MutableLiveData<Drawable>().also { mutableLiveData ->
            mutableLiveData.value = a4
        }
    val d4: LiveData<Drawable>
        get() = _d4
//    fun onItemSelected4(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//        _d4.value = changeColors.changeLeatherColors2(a4, position)
//    }
    fun updateD4(position: Int) {
        _d4.value = changeColors.changeLeatherColors2(a4, position)
    }

    private val a5 = ContextCompat.getDrawable(context, R.drawable.a5)
    private val _d5: MutableLiveData<Drawable> =
        MutableLiveData<Drawable>().also { mutableLiveData ->
            mutableLiveData.value = a5
        }
    val d5: LiveData<Drawable>
        get() = _d5
//    fun onItemSelected5(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//        _d5.value = changeColors.changeLeatherColors2(a5, position)
//    }
    fun updateD5(position: Int) {
        _d5.value = changeColors.changeLeatherColors2(a5, position)
    }

    private val a6 = ContextCompat.getDrawable(context, R.drawable.a6)
    private val _d6: MutableLiveData<Drawable> =
        MutableLiveData<Drawable>().also { mutableLiveData ->
            mutableLiveData.value = a6
        }
    val d6: LiveData<Drawable>
        get() = _d6
//    fun onItemSelected6(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//        _d6.value = changeColors.changeLeatherColors2(a6, position)
//    }
    fun updateD6(position: Int) {
        _d6.value = changeColors.changeLeatherColors2(a6, position)
    }

    private val a7 = ContextCompat.getDrawable(context, R.drawable.a7)
    private val _d7: MutableLiveData<Drawable> =
        MutableLiveData<Drawable>().also { mutableLiveData ->
            mutableLiveData.value = a7
        }
    val d7: LiveData<Drawable>
        get() = _d7
//    fun onItemSelected7(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//        _d7.value = changeColors.changeLeatherColors2(a7, position)
//    }
    fun updateD7(position: Int) {
        _d7.value = changeColors.changeLeatherColors2(a7, position)
    }

    private val a8 = ContextCompat.getDrawable(context, R.drawable.a8)
    private val _d8: MutableLiveData<Drawable> =
        MutableLiveData<Drawable>().also { mutableLiveData ->
            mutableLiveData.value = a8
        }
    val d8: LiveData<Drawable>
        get() = _d8
//    fun onItemSelected8(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//        _d8.value = changeColors.changeColorsWhiteOrBlack2(a8, position)
//    }
    fun updateD8(position: Int) {
        _d8.value = changeColors.changeColorsWhiteOrBlack2(a8, position)
    }

    private val a9 = ContextCompat.getDrawable(context, R.drawable.a9)
    private val _d9: MutableLiveData<Drawable> =
        MutableLiveData<Drawable>().also { mutableLiveData ->
            mutableLiveData.value = a9
        }
    val d9: LiveData<Drawable>
        get() = _d9
//    fun onItemSelected9(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//        _d9.value = changeColors.changeButtonColors2(a9, position)
//    }
    fun updateD9(position: Int) {
        _d9.value = changeColors.changeButtonColors2(a9, position)
    }

    private val a10 = ContextCompat.getDrawable(context, R.drawable.a10)
    private val _d10: MutableLiveData<Drawable> =
        MutableLiveData<Drawable>().also { mutableLiveData ->
            mutableLiveData.value = a10
        }
    val d10: LiveData<Drawable>
        get() = _d10
//    fun onItemSelected10(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//        _d10.value = changeColors.changeStringColors2(a10, position)
//    }
    fun updateD10(position: Int) {
        _d10.value = changeColors.changeStringColors2(a10, position)
    }

//    val d1: Drawable? = ContextCompat.getDrawable(context, R.drawable.a1)
//    val d2: Drawable? = ContextCompat.getDrawable(context, R.drawable.a2)
//    val d3: Drawable? = ContextCompat.getDrawable(context, R.drawable.a3)
//    val d4: Drawable? = ContextCompat.getDrawable(context, R.drawable.a4)
//    val d5: Drawable? = ContextCompat.getDrawable(context, R.drawable.a5)
//    val d6: Drawable? = ContextCompat.getDrawable(context, R.drawable.a6)
//    val d7: Drawable? = ContextCompat.getDrawable(context, R.drawable.a7)
//    val d8: Drawable? = ContextCompat.getDrawable(context, R.drawable.a8)
//    val d9: Drawable? = ContextCompat.getDrawable(context, R.drawable.a9)
//    val d10: Drawable? = ContextCompat.getDrawable(context, R.drawable.a10)

}

class AViewModelFactory(
    private val context: Context
): ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass){
            AViewModel::class.java -> {
                AViewModel(context) as T
            }
            else -> {
                throw RuntimeException("Cannot create an instance of $modelClass")
            }
        }
    }
}