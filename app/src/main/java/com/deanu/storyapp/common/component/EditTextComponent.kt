package com.deanu.storyapp.common.component

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.deanu.storyapp.R

class EditTextComponent : ConstraintLayout {
    private var view: View = inflate(context, R.layout.component_edit_text, this)
    private lateinit var tv: TextView
    private lateinit var edt: EditText
    private var inputType: Int = 0
    private var isValidPassword: Boolean = true
    private var isValidEmail: Boolean = true

    constructor(context: Context) : super(context) {
        initialize()
        initAllListener()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
        initTypedValue(attrs)
        initAllListener()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize()
        initTypedValue(attrs)
        initAllListener()
    }

    private fun initialize() {
        edt = view.findViewById(R.id.edt)
        tv = view.findViewById(R.id.tv)

        edt.maxLines = 1
    }

    private fun initAllListener() {
        edt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // empty
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // for password checking
                if (!isValidPassword && inputType == TYPE_PASSWORD) {
                    edt.background =
                        ContextCompat.getDrawable(context, R.drawable.selector_rounded_corner)
                }

                // for email checking
                if (!isValidEmail && inputType == TYPE_EMAIL) {
                    edt.background =
                        ContextCompat.getDrawable(context, R.drawable.selector_rounded_corner)
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                // for password checking
                if (editable != null && inputType == TYPE_PASSWORD) {
                    isValidPassword =
                        editable.toString().length > 6 && editable.toString().isNotEmpty()
                }

                // for email checking
                if (editable != null && inputType == TYPE_EMAIL) {
                    val emailToCheck = editable.toString()
                    isValidEmail =
                        emailToCheck.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToCheck)
                            .matches()
                }
            }
        })
    }

    private fun initTypedValue(attrs: AttributeSet) {
        val values = context.obtainStyledAttributes(attrs, R.styleable.EditTextComponent, 0, 0)
        when (values.getInt(R.styleable.EditTextComponent_input_type, 0)) {
            TYPE_TEXT -> {
                edt.inputType = InputType.TYPE_CLASS_TEXT
                inputType = TYPE_TEXT
            }
            TYPE_EMAIL -> {
                edt.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                inputType = TYPE_EMAIL
            }
            TYPE_PASSWORD -> {
                inputType = TYPE_PASSWORD
            }
            0 -> {
                edt.inputType = InputType.TYPE_CLASS_TEXT
                inputType = TYPE_PASSWORD
            }
        }

        edt.hint =
            if (!values.getString(R.styleable.EditTextComponent_text_hint).isNullOrEmpty()) {
                values.getString(R.styleable.EditTextComponent_text_hint)
            } else {
                ""
            }

        values.recycle()
    }


    fun isValid(): Boolean {
        return when (inputType) {
            TYPE_EMAIL -> {
                isValidEmail and !edt.text.isNullOrEmpty()
            }
            TYPE_PASSWORD -> {
                isValidPassword and !edt.text.isNullOrEmpty()
            }
            else -> {
                false
            }
        }
    }

    fun setError(isError: Boolean, errorText: String) {
        if (isError) {
            edt.background =
                ContextCompat.getDrawable(context, R.drawable.selector_error_rounder_corner)
            tv.text = errorText
            tv.visibility = VISIBLE
        } else {
            edt.background =
                ContextCompat.getDrawable(context, R.drawable.selector_rounded_corner)
            tv.visibility = INVISIBLE
        }
    }

    companion object {
        const val TYPE_TEXT = 1
        const val TYPE_EMAIL = 2
        const val TYPE_PASSWORD = 3
    }
}