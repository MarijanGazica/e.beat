package studio.nodroid.ebeat.utils

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.TypedValue
import android.widget.ImageView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.squareup.phrase.Phrase
import studio.nodroid.ebeat.R

fun boldFormat(placeholder: String, firstInset: String, secondInset: String? = null): CharSequence {

    val styledFirst = SpannableString(firstInset)

    styledFirst.setSpan(
        StyleSpan(Typeface.ITALIC),
        0,
        firstInset.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    val styledSecond = secondInset?.run {
        val styled = SpannableString(this)
        styled.setSpan(
            StyleSpan(Typeface.ITALIC),
            0,
            this.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        styled
    }


    val phrase = Phrase.from(placeholder)
        .put("first", styledFirst)

    styledSecond?.run {
        phrase.put("second", this)
    }

    return phrase.format()
}

fun Context.dpPx(dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
}


fun ImageView.startDotAnimation() {
    val animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_e_beat_logo_dot)
    animatedVectorDrawableCompat?.registerAnimationCallback(
        object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                post { animatedVectorDrawableCompat.start() }
            }
        })
    setImageDrawable(animatedVectorDrawableCompat)
    animatedVectorDrawableCompat?.start()
}