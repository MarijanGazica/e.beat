package studio.nodroid.bloodpressurehelper.utils

import android.graphics.PorterDuff
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.textfield.TextInputLayout
import studio.nodroid.bloodpressurehelper.R
import java.math.BigDecimal
import java.math.RoundingMode

fun Double.roundTo(places: Int): Double {
    if (places < 0) throw IllegalArgumentException()

    var bd = BigDecimal(this)
    bd = bd.setScale(places, RoundingMode.HALF_UP)
    return bd.toDouble()
}

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