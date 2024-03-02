package com.nokopi.colorsample

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.nokopi.colorsample.databinding.ActivityPlCustomColorBinding
import com.nokopi.colorsample.utils.ChangeColors
import com.nokopi.colorsample.view.CustomSpinnerAdapter
import com.nokopi.colorsample.utils.KeyboardUtils

class PLCustomColor: AppCompatActivity() {
    private lateinit var binding: ActivityPlCustomColorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_pl_custom_color)

        setSupportActionBar(binding.title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val changeColors = ChangeColors()

        val d1: Drawable? = ContextCompat.getDrawable(this,R.drawable.pl1)
        val d2: Drawable? = ContextCompat.getDrawable(this,R.drawable.pl2)
        val d3: Drawable? = ContextCompat.getDrawable(this,R.drawable.pl3)
        val d4: Drawable? = ContextCompat.getDrawable(this,R.drawable.pl4)
        val d5: Drawable? = ContextCompat.getDrawable(this,R.drawable.pl5)

        binding.imageView.setImageDrawable(d1)
        binding.imageView2.setImageDrawable(d2)
        binding.imageView3.setImageDrawable(d3)
        binding.imageView4.setImageDrawable(d4)
        binding.imageView5.setImageDrawable(d5)

        binding.imageView6.setImageResource(R.drawable.pl6)

        val customDropDownAdapter1 = CustomSpinnerAdapter(ChangeColors.plasticsColorSpinner, ChangeColors.plasticsColorSpinnerString)
        binding.image1Spinner.adapter = customDropDownAdapter1
        binding.image1Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changePlasticColors(d1, position, binding.imageView)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter2 = CustomSpinnerAdapter(ChangeColors.plSpongesColorSpinner, ChangeColors.plSpongeColorSpinnerString)
        binding.image2Spinner.adapter = customDropDownAdapter2
        binding.image2Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changePLSpongeColors(d2, position, binding.imageView2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter3 = CustomSpinnerAdapter(ChangeColors.bandsColorSpinner, ChangeColors.bandsColorSpinnerString)
        binding.image3Spinner.adapter = customDropDownAdapter3
        binding.image3Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeBandColors(d3, position, binding.imageView3)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter4 = CustomSpinnerAdapter(ChangeColors.buttonsColorSpinner, ChangeColors.buttonsColorSpinnerString)
        binding.image4Spinner.adapter = customDropDownAdapter4
        binding.image4Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeButtonColors(d4, position, binding.imageView4)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter5 = CustomSpinnerAdapter(ChangeColors.whiteBlackSpinner, ChangeColors.whiteBlackSpinnerString)
        binding.image5Spinner.adapter = customDropDownAdapter5
        binding.image5Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeColorsWhiteOrBlack(d5, position, binding.imageView5)
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