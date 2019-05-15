package studio.nodroid.ebeat.utils

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class KeyboardVisibilityProvider(val context: Context) {

    private val isKeyboardVisible = MutableLiveData<Boolean>().apply { value = false }

    fun getKeyboardVisibility(view: ViewGroup): LiveData<Boolean> {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = view.rootView.height - view.height
            val keyboardUp = heightDiff > dpToPx(context, 200f)
            if (isKeyboardVisible.value != keyboardUp) {
                isKeyboardVisible.value = keyboardUp
            }
        }
        return isKeyboardVisible
    }

    private fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }
}