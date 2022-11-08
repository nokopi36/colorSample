package com.nokopi.colorsample

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.nokopi.colorsample.utils.ChangeColors
import com.nokopi.colorsample.utils.KeyboardUtils
import com.nokopi.colorsample.view.CustomSpinnerAdapter

class POGOCustomColor : AppCompatActivity() {
    private lateinit var mainLayout: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pogo_custom_color)
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
        val imageView7: ImageView = findViewById(R.id.imageView7)
        val imageView8: ImageView = findViewById(R.id.imageView8)
        val imageView9: ImageView = findViewById(R.id.imageView9)

        val d1: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo1)
        val d2: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo2)
        val d3: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo3)
        val d4: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo4)
        val d5: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo5)
        val d6: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo6)
        val d7: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo7)
        val d8: Drawable? = ContextCompat.getDrawable(this,R.drawable.pogo8)

        imageView.setImageDrawable(d1)
        imageView2.setImageDrawable(d2)
        imageView3.setImageDrawable(d3)
        imageView4.setImageDrawable(d4)
        imageView5.setImageDrawable(d5)
        imageView6.setImageDrawable(d6)
        imageView7.setImageDrawable(d7)
        imageView8.setImageDrawable(d8)

        imageView9.setImageResource(R.drawable.pogo9)

        val image1Spinner: Spinner = findViewById(R.id.image1Spinner)
        val customDropDownAdapter1 = CustomSpinnerAdapter(ChangeColors.nbSLBAPogoPlasticsColorSpinner, ChangeColors.nbSLBAPogoPlasticsColorSpinnerString)
        image1Spinner.adapter = customDropDownAdapter1
        image1Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeNBSLBAPlasticColors(d1, position, imageView)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val image2Spinner: Spinner = findViewById(R.id.image2Spinner)
        val customDropDownAdapter23456 = CustomSpinnerAdapter(ChangeColors.leathersColorSpinner, ChangeColors.leathersColorSpinnerString)
        image2Spinner.adapter = customDropDownAdapter23456
        image2Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d2, position, imageView2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val image3Spinner: Spinner = findViewById(R.id.image3Spinner)
        image3Spinner.adapter = customDropDownAdapter23456
        image3Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d3, position, imageView3)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val image4Spinner: Spinner = findViewById(R.id.image4Spinner)
        image4Spinner.adapter = customDropDownAdapter23456
        image4Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d4, position, imageView4)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val image5Spinner: Spinner = findViewById(R.id.image5Spinner)
        image5Spinner.adapter = customDropDownAdapter23456
        image5Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d5, position, imageView5)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val image6Spinner: Spinner = findViewById(R.id.image6Spinner)
        image6Spinner.adapter = customDropDownAdapter23456
        image6Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d6, position, imageView6)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val image7Spinner: Spinner = findViewById(R.id.image7Spinner)
        val customDropDownAdapter7 = CustomSpinnerAdapter(ChangeColors.buttonsColorSpinner, ChangeColors.buttonsColorSpinnerString)
        image7Spinner.adapter = customDropDownAdapter7
        image7Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeButtonColors(d7, position, imageView7)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val image8Spinner: Spinner = findViewById(R.id.image8Spinner)
        val customDropDownAdapter8 = CustomSpinnerAdapter(ChangeColors.stringsColorSpinner, ChangeColors.stringsColorSpinnerString)
        image8Spinner.adapter = customDropDownAdapter8
        image8Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeStringColors(d8, position, imageView8)
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