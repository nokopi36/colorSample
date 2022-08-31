package com.nokopi.colorsample

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.nokopi.colorsample.utils.ChangeColors
import com.nokopi.colorsample.view.CustomSpinnerAdapter
import com.nokopi.colorsample.utils.KeyboardUtils

class SLBCustomColor : AppCompatActivity() {
    private lateinit var mainLayout: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slb_custom_color)
        mainLayout =findViewById(R.id.main_layout)

        val changeColors = ChangeColors()

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val imageView: ImageView = findViewById(R.id.imageView)
        val imageView2: ImageView = findViewById(R.id.imageView2)
        val imageView3: ImageView = findViewById(R.id.imageView3)
        val imageView4: ImageView = findViewById(R.id.imageView4)
        val imageView5: ImageView = findViewById(R.id.imageView5)
        val imageView6: ImageView = findViewById(R.id.imageView6)
        val imageView7: ImageView = findViewById(R.id.imageView7)
        val imageView8: ImageView = findViewById(R.id.imageView8)
        val imageView9: ImageView = findViewById(R.id.imageView9)
        val imageView10: ImageView = findViewById(R.id.imageView10)

        val d1: Drawable? = ContextCompat.getDrawable(this,R.drawable.slb1)
        val d2: Drawable? = ContextCompat.getDrawable(this,R.drawable.slb2)
        val d3: Drawable? = ContextCompat.getDrawable(this,R.drawable.slb3)
        val d4: Drawable? = ContextCompat.getDrawable(this,R.drawable.slb4)
        val d5: Drawable? = ContextCompat.getDrawable(this,R.drawable.slb5)
        val d6: Drawable? = ContextCompat.getDrawable(this,R.drawable.slb6)
        val d7: Drawable? = ContextCompat.getDrawable(this,R.drawable.slb7)
        val d8: Drawable? = ContextCompat.getDrawable(this,R.drawable.slb8)
        val d9: Drawable? = ContextCompat.getDrawable(this,R.drawable.slb9)

        imageView.setImageDrawable(d1)
        imageView2.setImageDrawable(d2)
        imageView3.setImageDrawable(d3)
        imageView4.setImageDrawable(d4)
        imageView5.setImageDrawable(d5)
        imageView6.setImageDrawable(d6)
        imageView7.setImageDrawable(d7)
        imageView8.setImageDrawable(d8)
        imageView9.setImageDrawable(d9)

        imageView10.setImageResource(R.drawable.slb10)

        val image1Spinner: Spinner = findViewById(R.id.image1Spinner)
        val customDropDownAdapter1 = CustomSpinnerAdapter(ChangeColors.nbSLBPlasticsColorSpinner)
        image1Spinner.adapter = customDropDownAdapter1
        image1Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeNBSLBPlasticColors(d1, position, imageView)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter2 = CustomSpinnerAdapter(ChangeColors.spongesColorSpinner)
        val image2Spinner: Spinner = findViewById(R.id.image2Spinner)
        image2Spinner.adapter = customDropDownAdapter2
        image2Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeSpongeColors(d2, position, imageView2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter345 = CustomSpinnerAdapter(ChangeColors.leathersColorSpinner)
        val image3Spinner: Spinner = findViewById(R.id.image3Spinner)
        image3Spinner.adapter = customDropDownAdapter345
        image3Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d3, position, imageView3)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val image4Spinner: Spinner = findViewById(R.id.image4Spinner)
        image4Spinner.adapter = customDropDownAdapter345
        image4Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d4, position, imageView4)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val image5Spinner: Spinner = findViewById(R.id.image5Spinner)
        image5Spinner.adapter = customDropDownAdapter345
        image5Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d5, position, imageView5)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter67 = CustomSpinnerAdapter(ChangeColors.whiteBlackSpinner)
        val image6Spinner: Spinner = findViewById(R.id.image6Spinner)
        image6Spinner.adapter = customDropDownAdapter67
        image6Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeColorsWhiteOrBlack(d6, position, imageView6)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val image7Spinner: Spinner = findViewById(R.id.image7Spinner)
        image7Spinner.adapter = customDropDownAdapter67
        image7Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeColorsWhiteOrBlack(d7, position, imageView7)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter8 = CustomSpinnerAdapter(ChangeColors.stringsColorSpinner)
        val image8Spinner: Spinner = findViewById(R.id.image8Spinner)
        image8Spinner.adapter = customDropDownAdapter8
        image8Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeStringColors(d8, position, imageView8)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter9 = CustomSpinnerAdapter(ChangeColors.buttonsColorSpinner)
        val image9Spinner: Spinner = findViewById(R.id.image9Spinner)
        image9Spinner.adapter = customDropDownAdapter9
        image9Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeButtonColors(d9, position, imageView9)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val focusView = currentFocus ?: return true
        KeyboardUtils.hideKeyboard(focusView)
        mainLayout.requestFocus()
        return false
    }

}