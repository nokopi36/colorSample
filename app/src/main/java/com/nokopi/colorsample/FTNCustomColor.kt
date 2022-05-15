package com.nokopi.colorsample

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class FTNCustomColor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ftn_custom_color)

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

        val d1: Drawable? = ContextCompat.getDrawable(this,R.drawable.ftn1)
        val d2: Drawable? = ContextCompat.getDrawable(this,R.drawable.ftn2)
        val d3: Drawable? = ContextCompat.getDrawable(this,R.drawable.ftn3)
        val d4: Drawable? = ContextCompat.getDrawable(this,R.drawable.ftn4)
        val d5: Drawable? = ContextCompat.getDrawable(this,R.drawable.ftn5)
        val d6: Drawable? = ContextCompat.getDrawable(this,R.drawable.ftn6)
        val d7: Drawable? = ContextCompat.getDrawable(this,R.drawable.ftn7)
        val d8: Drawable? = ContextCompat.getDrawable(this,R.drawable.ftn8)
        val d9: Drawable? = ContextCompat.getDrawable(this,R.drawable.ftn9)

        imageView.setImageDrawable(d1)
        imageView2.setImageDrawable(d2)
        imageView3.setImageDrawable(d3)
        imageView4.setImageDrawable(d4)
        imageView5.setImageDrawable(d5)
        imageView6.setImageDrawable(d6)
        imageView7.setImageDrawable(d7)
        imageView8.setImageDrawable(d8)
        imageView9.setImageDrawable(d9)

        imageView10.setImageResource(R.drawable.ftn10)

        val image1Spinner: Spinner = findViewById(R.id.image1Spinner)
        val customDropDownAdapter12345 = CustomSpinnerAdapter(ChangeColors.leathersColor)
        image1Spinner.adapter = customDropDownAdapter12345
        image1Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d1, position, imageView)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val image2Spinner: Spinner = findViewById(R.id.image2Spinner)
        image2Spinner.adapter = customDropDownAdapter12345
        image2Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d2, position, imageView2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val image3Spinner: Spinner = findViewById(R.id.image3Spinner)
        image3Spinner.adapter = customDropDownAdapter12345
        image3Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d3, position, imageView3)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val image4Spinner: Spinner = findViewById(R.id.image4Spinner)
        image4Spinner.adapter = customDropDownAdapter12345
        image4Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d4, position, imageView4)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val image5Spinner: Spinner = findViewById(R.id.image5Spinner)
        image5Spinner.adapter = customDropDownAdapter12345
        image5Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeLeatherColors(d5, position, imageView5)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter67 = CustomSpinnerAdapter(ChangeColors.whiteBlack)
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

        val customDropDownAdapter8 = CustomSpinnerAdapter(ChangeColors.buttonsColor)
        val image8Spinner: Spinner = findViewById(R.id.image8Spinner)
        image8Spinner.adapter = customDropDownAdapter8
        image8Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeButtonColors(d8, position, imageView8)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        val customDropDownAdapter9 = CustomSpinnerAdapter(ChangeColors.stringsColor)
        val image9Spinner: Spinner = findViewById(R.id.image9Spinner)
        image9Spinner.adapter = customDropDownAdapter9
        image9Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                changeColors.changeStringColors(d9, position, imageView9)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }
    }
}