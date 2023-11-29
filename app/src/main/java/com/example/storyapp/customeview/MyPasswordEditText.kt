package com.example.storyapp.customeview

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class MyPasswordEditText : AppCompatEditText {

    private var isPasswordVisible: Boolean = false
    private val drawableStart = ContextCompat.getDrawable(context, R.drawable.ic_baseline_lock_24)
    private val visibleIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_visibility_24)
    private val invisibleIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_visibility_off_24)

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        updateIcon()
        setDrawableVisibilityOnClickListener()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                updateIcon()
                if (!text.isNullOrBlank()) {
                    error = if (text!!.length < 8) {
                        resources.getString(R.string.password_minimum)
                    } else {
                        null
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (text.isNullOrEmpty()) {
                    error = resources.getString(R.string.password_cannot_empty)
                }
            }
        })

        val textColor = textColors
        visibleIcon?.setTintList(textColor)
        invisibleIcon?.setTintList(textColor)
        drawableStart?.setTintList(hintTextColors)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setDrawableVisibilityOnClickListener() {
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEndRect = getCompoundDrawablesRelative()[2]?.bounds
                if (drawableEndRect != null && event.rawX >= right - drawableEndRect.width()) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        updateIcon()
        transformationMethod = if (isPasswordVisible) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
    }

    private fun updateIcon() {
        val drawableEnd = if (isPasswordVisible) visibleIcon else invisibleIcon
        setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, drawableEnd, null)
    }
}