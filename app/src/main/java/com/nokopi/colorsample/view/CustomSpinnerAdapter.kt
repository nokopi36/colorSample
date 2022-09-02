package com.nokopi.colorsample.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.nokopi.colorsample.R

class CustomSpinnerAdapter(private val imageResIdList: List<Int>, private val imageTextList: List<String>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.custom_spinner_item, parent, false)
        view.findViewById<ImageView>(R.id.img).setImageResource(imageResIdList[position])
        view.layoutParams.height = 100
        view.findViewById<TextView>(R.id.imgText).text = imageTextList[position]
        return view
    }

    override fun getItem(position: Int): Any {
        return imageResIdList[position]
    }

    override fun getCount(): Int {
        return imageResIdList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /*
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.dropdown_item, parent, false)
        view.findViewById<ImageView>(R.id.image_view).setImageResource(imageResIdList[position])
        return view
    }
     */
}