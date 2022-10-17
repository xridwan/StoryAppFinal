package com.xridwan.mystoryfinal.ui.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.xridwan.mystoryfinal.R

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var txtColor: Int = 0

    init {
        init()
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background =
            ContextCompat.getDrawable(context, R.drawable.bg_edittext) as Drawable
        hint = "Password"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        textSize = 16F
        setHintTextColor(txtColor)
    }

    private fun init() {
        setOnTouchListener(this)
        txtColor = ContextCompat.getColor(context, R.color.hint_color)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                error = if (text.toString().length < 6 && text.toString().isNotEmpty()) {
                    "Password must be greater than 6"
                } else {
                    null
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
}