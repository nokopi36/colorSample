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
import com.nokopi.colorsample.databinding.ActivityPogoCustomColorBinding
import com.nokopi.colorsample.utils.ChangeColors
import com.nokopi.colorsample.utils.KeyboardUtils
import com.nokopi.colorsample.view.CustomSpinnerAdapter

class POGOCustomColor : AppCompatActivity() {
    private lateinit var binding: ActivityPogoCustomColorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pogo_custom_color)

        setSupportActionBar(binding.title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val changeColors = ChangeColors()

        val d1: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo1)
        val d2: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo2)
        val d3: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo3)
        val d4: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo4)
        val d5: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo5)
        val d6: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo6)
        val d7: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo7)
        val d8: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo8)

        binding.imageView.setImageDrawable(d1)
        binding.imageView2.setImageDrawable(d2)
        binding.imageView3.setImageDrawable(d3)
        binding.imageView4.setImageDrawable(d4)
        binding.imageView5.setImageDrawable(d5)
        binding.imageView6.setImageDrawable(d6)
        binding.imageView7.setImageDrawable(d7)
        binding.imageView8.setImageDrawable(d8)

        binding.imageView9.setImageResource(R.drawable.pogo9)

        val customDropDownAdapter1 = CustomSpinnerAdapter(ChangeColors.nbSLBAPogoPlasticsColorSpinner, ChangeColors.nbSLBAPogoPlasticsColorSpinnerString)
        binding.image1Spinner.adapter = customDropDownAdapter1
        binding.image1Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeNBSLBAPlasticColors(d1, position, binding.imageView)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter23456 = CustomSpinnerAdapter(ChangeColors.leathersColorSpinner, ChangeColors.leathersColorSpinnerString)
        binding.image2Spinner.adapter = customDropDownAdapter23456
        binding.image2Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d2, position, binding.imageView2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        binding.image3Spinner.adapter = customDropDownAdapter23456
        binding.image3Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d3, position, binding.imageView3)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        binding.image4Spinner.adapter = customDropDownAdapter23456
        binding.image4Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d4, position, binding.imageView4)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        binding.image5Spinner.adapter = customDropDownAdapter23456
        binding.image5Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d5, position, binding.imageView5)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        binding.image6Spinner.adapter = customDropDownAdapter23456
        binding.image6Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d6, position, binding.imageView6)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter7 = CustomSpinnerAdapter(ChangeColors.buttonsColorSpinner, ChangeColors.buttonsColorSpinnerString)
        binding.image7Spinner.adapter = customDropDownAdapter7
        binding.image7Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeButtonColors(d7, position, binding.imageView7)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter8 = CustomSpinnerAdapter(ChangeColors.stringsColorSpinner, ChangeColors.stringsColorSpinnerString)
        binding.image8Spinner.adapter = customDropDownAdapter8
        binding.image8Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeStringColors(d8, position, binding.imageView8)
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