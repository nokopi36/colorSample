package com.nokopi.colorsample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class CustomSpinnerAdapter(private val imageResIdList: List<Int>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.custom_spinner_item, parent, false)
        view.findViewById<ImageView>(R.id.img).setImageResource(imageResIdList[position])
        view.layoutParams.height = 100
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