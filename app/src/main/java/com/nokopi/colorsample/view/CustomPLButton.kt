package com.nokopi.colorsample.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import com.nokopi.colorsample.R

class CustomPLButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): LinearLayout(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.custom_pl_button, this)
    }

    private var listener: OnClickListener? = null

    override fun setOnClickListener( l: OnClickListener?) {
        listener = l
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            if (listener != null) {
                post { listener!!.onClick(this) }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}