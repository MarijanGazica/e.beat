package studio.nodroid.ebeat.utils

import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputLayout
import com.squareup.phrase.Phrase
import studio.nodroid.ebeat.R


fun TextInputLayout.onTextChanged(onChange: (String) -> Unit) {
    this.editText?.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            onChange(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

fun EditText.onTextChanged(onChange: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            onChange(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

fun TextInputLayout.onIntInputChanged(onChange: (Int) -> Unit) {
    this.editText?.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s?.toString().isNullOrBlank()) {
                error = null
                onChange(-1)
                return
            }
            try {
                error = null
                onChange(s.toString().toInt())
            } catch (e: NumberFormatException) {
                error = context.resources.getString(R.string.numberInputInvalid)
            }

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

fun TextInputLayout.setSelectedListener(action: () -> Unit) {
    setOnClickListener { action() }
    setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            action()
        }
    }

    editText?.setOnClickListener { action() }
    editText?.setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            action()
        }
    }
}

fun View.setBackgroundColorCompat(@ColorRes color: Int) =
    setBackgroundColor(ResourcesCompat.getColor(resources, color, context.theme))

fun Context.colorFormat(placeholder: String, insert: String): CharSequence {
    val styled = SpannableString(insert)
//    styled.setSpan(
////        ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.textAccent, theme)),
//        StyleSpan(Typeface.BOLD),
//        0,
//        insert.length,
//        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//    )
    styled.setSpan(
//        ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.textAccent, theme)),
        UnderlineSpan(),
        0,
        insert.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return Phrase.from(placeholder)
        .put("first", styled)
        .format()
}