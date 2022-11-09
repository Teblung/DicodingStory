package com.teblung.dicodingstory.ui.custom

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.teblung.dicodingstory.R
import com.teblung.dicodingstory.databinding.ViewInputBinding
import java.util.regex.Matcher
import java.util.regex.Pattern

class InputView : ConstraintLayout {

    private lateinit var mContext: Context

    private val binding: ViewInputBinding by lazy {
        ViewInputBinding.bind(
            LayoutInflater.from(mContext).inflate(R.layout.view_input, this, true)
        )
    }

    private var inputLength: Int = 0
    private var listener: InputViewListener? = null
    private var inputType = TYPE.TEXT

    enum class TYPE { TEXT, PASSWORD, EMAIL }

    interface InputViewListener {
        fun afterTextChanged(input: String)
    }

    companion object {
        private val TAG: String = InputView::class.java.simpleName
    }

    constructor(context: Context) : super(context, null) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attributeSet: AttributeSet?) {
        mContext = context
        binding.apply {
            tiedInputView.addTextChangedListener(textWatcher)
        }
        Log.d(TAG, attributeSet.toString())
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.tiedInputView.error = null
        }

        override fun afterTextChanged(p0: Editable?) {
            listener?.afterTextChanged(p0.toString())
            if (p0.toString().length < inputLength && inputType == TYPE.PASSWORD) {
                setHelperText(
                    resources.getString(
                        R.string.req_password,
                        inputLength.toString()
                    )
                )
            } else {
                setHelperText("")
            }
        }

    }

    fun setHelperText(text: String, color: Int? = null) {
        with(binding) {
            tilInputView.helperText = text
            tilInputView.setHelperTextColor(
                ColorStateList.valueOf(
                    color ?: ContextCompat.getColor(context, R.color.purple_200)
                )
            )
        }
    }

    fun setRequireField(text: String) {
        with(binding) {
            tiedInputView.error = text
        }
    }

    fun setMinLength(length: Int) {
        inputLength = length
    }

    fun setHint(hint: String) {
        binding.tilInputView.hint = hint
    }

    fun setInputType(type: TYPE) {
        inputType = type
        binding.tiedInputView.inputType = when (type) {
            TYPE.PASSWORD -> InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            TYPE.EMAIL -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
            else -> InputType.TYPE_CLASS_TEXT
        }
    }

    fun setCleartext(states: Boolean) {
        binding.tilInputView.endIconMode =
            if (states) TextInputLayout.END_ICON_CLEAR_TEXT else TextInputLayout.END_ICON_NONE
    }

    fun setVisiblePassword() {
        binding.tilInputView.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
    }

    fun isValid(): Boolean {
        return if (inputType == TYPE.EMAIL) isEmailValid(getText()) else getText().isNotEmpty()
    }

    fun getText(): String {
        return binding.tiedInputView.text.toString().trim()
    }

    fun getLength() : Int {
        return binding.tiedInputView.length()
    }

    fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun setListener(inputListener: InputViewListener) {
        listener = inputListener
    }
}