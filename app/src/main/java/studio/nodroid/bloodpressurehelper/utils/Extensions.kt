package studio.nodroid.bloodpressurehelper.utils

import android.graphics.PorterDuff
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.textfield.TextInputLayout
import studio.nodroid.bloodpressurehelper.R

fun TextInputLayout.onTextChanged(onChange: (String) -> Unit) {
    this.editText?.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            onChange(s.toString())
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
            if (s.toString().isBlank()) {
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

fun View.animateVisibility(visible: Boolean) {
    if (translationY == 0f && visible) {
        return
    }

//    translationY = if (visible) height.toFloat() else 0f

    animate().apply {
        duration = 300
        translationY(if (visible) 0f else height.toFloat())
        withStartAction {
            if (visible) {
                visibility = View.VISIBLE
            }
        }
        withEndAction {
            if (!visible) {
                visibility = View.GONE
            }
        }
    }
}


fun TextInputLayout.activateDrawableTinter() {

    fun setDrawableAndColor(hasFocus: Boolean, view: EditText) {
        val color = if (hasFocus) {
            context.resources.getColor(R.color.colorPrimary)
        } else {
            context.resources.getColor(R.color.drawableInactive)
        }
        val drawableSrc = context.resources.getDrawable(R.drawable.ic_heart)
        val drawable = DrawableCompat.wrap(drawableSrc).mutate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(color)
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
        view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }

    editText?.run {
        setOnFocusChangeListener { _, hasFocus ->
            setDrawableAndColor(hasFocus, this)
        }
        setDrawableAndColor(hasFocus(), this)
    }
}

