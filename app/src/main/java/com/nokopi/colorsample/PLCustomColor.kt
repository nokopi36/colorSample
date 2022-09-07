package com.nokopi.colorsample

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.nokopi.colorsample.utils.ChangeColors
import com.nokopi.colorsample.view.CustomSpinnerAdapter
import com.nokopi.colorsample.utils.KeyboardUtils

class PLCustomColor: AppCompatActivity() {
    private lateinit var mainLayout: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pl_custom_color)
        mainLayout =findViewById(R.id.main_layout)

        val toolbar = findViewById<Toolbar>(R.id.title)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val changeColors = ChangeColors()

        val imageView: ImageView = findViewById(R.id.imageView)
        val imageView2: ImageView = findViewById(R.id.imageView2)
        val imageView3: ImageView = findViewById(R.id.imageView3)
        val imageView4: ImageView = findViewById(R.id.imageView4)
        val imageView5: ImageView = findViewById(R.id.imageView5)
        val imageView6: ImageView = findViewById(R.id.imageView6)

        val d1: Drawable? = ContextCompat.getDrawable(this,R.drawable.pl1)
        val d2: Drawable? = ContextCompat.getDrawable(this,R.drawable.pl2)
        val d3: Drawable? = ContextCompat.getDrawable(this,R.drawable.pl3)
        val d4: Drawable? = ContextCompat.getDrawable(this,R.drawable.pl4)
        val d5: Drawable? = ContextCompat.getDrawable(this,R.drawable.pl5)

        imageView.setImageDrawable(d1)
        imageView2.setImageDrawable(d2)
        imageView3.setImageDrawable(d3)
        imageView4.setImageDrawable(d4)
        imageView5.setImageDrawable(d5)

        imageView6.setImageResource(R.drawable.pl6)

        val image1Spinner: Spinner = findViewById(R.id.image1Spinner)
        val customDropDownAdapter1 = CustomSpinnerAdapter(ChangeColors.plasticsColorSpinner, ChangeColors.plasticsColorSpinnerString)
        image1Spinner.adapter = customDropDownAdapter1
        image1Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changePlasticColors(d1, position, imageView)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter2 = CustomSpinnerAdapter(ChangeColors.plSpongesColorSpinner, ChangeColors.plSpongeColorSpinnerString)
        val image2Spinner: Spinner = findViewById(R.id.image2Spinner)
        image2Spinner.adapter = customDropDownAdapter2
        image2Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changePLSpongeColors(d2, position, imageView2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter3 = CustomSpinnerAdapter(ChangeColors.bandsColorSpinner, ChangeColors.bandsColorSpinnerString)
        val image3Spinner: Spinner = findViewById(R.id.image3Spinner)
        image3Spinner.adapter = customDropDownAdapter3
        image3Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeBandColors(d3, position, imageView3)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter4 = CustomSpinnerAdapter(ChangeColors.buttonsColorSpinner, ChangeColors.buttonsColorSpinnerString)
        val image4Spinner: Spinner = findViewById(R.id.image4Spinner)
        image4Spinner.adapter = customDropDownAdapter4
        image4Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeButtonColors(d4, position, imageView4)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter5 = CustomSpinnerAdapter(ChangeColors.whiteBlackSpinner, ChangeColors.whiteBlackSpinnerString)
        val image5Spinner: Spinner = findViewById(R.id.image5Spinner)
        image5Spinner.adapter = customDropDownAdapter5
        image5Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeColorsWhiteOrBlack(d5, position, imageView5)
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
        mainLayout.requestFocus()
        return false
    }

}