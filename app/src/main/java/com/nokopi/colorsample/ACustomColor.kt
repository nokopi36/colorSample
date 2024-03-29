package com.nokopi.colorsample

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.nokopi.colorsample.databinding.ActivityACustomColorBinding
import com.nokopi.colorsample.ui.AViewModel
import com.nokopi.colorsample.ui.AViewModelFactory
import com.nokopi.colorsample.utils.ChangeColors
import com.nokopi.colorsample.utils.KeyboardUtils
import com.nokopi.colorsample.view.CustomSpinnerAdapter

class ACustomColor : AppCompatActivity() {
    private lateinit var binding: ActivityACustomColorBinding
    private lateinit var viewModel: AViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a_custom_color)
        viewModel = ViewModelProvider(this, AViewModelFactory(this))[AViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_a_custom_color)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        setSupportActionBar(binding.title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val changeColors = ChangeColors()

//        val d1: Drawable? = ContextCompat.getDrawable(this,R.drawable.a1)
//        val d2: Drawable? = ContextCompat.getDrawable(this,R.drawable.a2)
//        val d3: Drawable? = ContextCompat.getDrawable(this,R.drawable.a3)
//        val d4: Drawable? = ContextCompat.getDrawable(this,R.drawable.a4)
//        val d5: Drawable? = ContextCompat.getDrawable(this,R.drawable.a5)
//        val d6: Drawable? = ContextCompat.getDrawable(this,R.drawable.a6)
//        val d7: Drawable? = ContextCompat.getDrawable(this,R.drawable.a7)
//        val d8: Drawable? = ContextCompat.getDrawable(this,R.drawable.a8)
//        val d9: Drawable? = ContextCompat.getDrawable(this,R.drawable.a9)
//        val d10: Drawable? = ContextCompat.getDrawable(this,R.drawable.a10)
//
//        binding.imageView.setImageDrawable(d1)
//        binding.imageView2.setImageDrawable(d2)
//        binding.imageView3.setImageDrawable(d3)
//        binding.imageView4.setImageDrawable(d4)
//        binding.imageView5.setImageDrawable(d5)
//        binding.imageView6.setImageDrawable(d6)
//        binding.imageView7.setImageDrawable(d7)
//        binding.imageView8.setImageDrawable(d8)
//        binding.imageView9.setImageDrawable(d9)
//        binding.imageView10.setImageDrawable(d10)
//
//        binding.imageView11.setImageResource(R.drawable.a11)

        val customDropDownAdapter1 = CustomSpinnerAdapter(ChangeColors.nbSLBAPogoPlasticsColorSpinner, ChangeColors.nbSLBAPogoPlasticsColorSpinnerString)
        binding.image1Spinner.adapter = customDropDownAdapter1
        binding.image1Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                Log.i("onItemSelected", "$position")
//                changeColors.changeNBSLBAPlasticColors(d1, position, binding.imageView)
                viewModel.updateD1(position)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter2 = CustomSpinnerAdapter(ChangeColors.whiteBlackSpinner, ChangeColors.whiteBlackSpinnerString)
        binding.image2Spinner.adapter = customDropDownAdapter2
        binding.image2Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//                changeColors.changeColorsWhiteOrBlack(d2, position, binding.imageView2)
                viewModel.updateD2(position)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter34567 = CustomSpinnerAdapter(ChangeColors.leathersColorSpinner, ChangeColors.leathersColorSpinnerString)
        binding.image3Spinner.adapter = customDropDownAdapter34567
        binding.image3Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//                changeColors.changeLeatherColors(d3, position, binding.imageView3)
                viewModel.updateD3(position)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        binding.image4Spinner.adapter = customDropDownAdapter34567
        binding.image4Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//                changeColors.changeLeatherColors(d4, position, binding.imageView4)
                viewModel.updateD4(position)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        binding.image5Spinner.adapter = customDropDownAdapter34567
        binding.image5Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//                changeColors.changeLeatherColors(d5, position, binding.imageView5)
                viewModel.updateD5(position)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        binding.image6Spinner.adapter = customDropDownAdapter34567
        binding.image6Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//                changeColors.changeLeatherColors(d6, position, binding.imageView6)
                viewModel.updateD6(position)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        binding.image7Spinner.adapter = customDropDownAdapter34567
        binding.image7Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//                changeColors.changeLeatherColors(d7, position, binding.imageView7)
                viewModel.updateD7(position)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter8 = CustomSpinnerAdapter(ChangeColors.whiteBlackSpinner, ChangeColors.whiteBlackSpinnerString)
        binding.image8Spinner.adapter = customDropDownAdapter8
        binding.image8Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//                changeColors.changeColorsWhiteOrBlack(d8, position, binding.imageView8)
                viewModel.updateD8(position)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter9 = CustomSpinnerAdapter(ChangeColors.buttonsColorSpinner, ChangeColors.buttonsColorSpinnerString)
        binding.image9Spinner.adapter = customDropDownAdapter9
        binding.image9Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//                changeColors.changeButtonColors(d9, position, binding.imageView9)
                viewModel.updateD9(position)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter10 = CustomSpinnerAdapter(ChangeColors.stringsColorSpinner, ChangeColors.stringsColorSpinnerString)
        binding.image10Spinner.adapter = customDropDownAdapter10
        binding.image10Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//                changeColors.changeStringColors(d10, position, binding.imageView10)
                viewModel.updateD10(position)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val focusView = currentFocus ?: return true
        KeyboardUtils.hideKeyboard(focusView)
        binding.mainLayout.requestFocus()
        return false
    }

}