package studio.nodroid.bloodpressurehelper.utils

import android.animation.ValueAnimator
import android.view.View


class ViewHeightAnimator(val view: View) {

    private val originalHeight: Int = view.layoutParams.height

    var visible: Boolean? = false
        set(value) {
            if (field != value) {
                when (value) {
                    true -> showView()
                    false -> hideView()
                }
            }
            field = value
        }

    private fun showView() {
        val heightAnim = ValueAnimator.ofInt(view.height, originalHeight)
        heightAnim.duration = 300
        heightAnim.addUpdateListener {
            val layoutParams = view.layoutParams
            layoutParams.height = it.animatedValue as Int
            view.layoutParams = layoutParams
            if (view.visibility != View.VISIBLE) {
                view.visibility = View.VISIBLE
            }
        }
        heightAnim.start()
    }

    private fun hideView() {
        val heightAnim = ValueAnimator.ofInt(view.height, 1)
        heightAnim.duration = 300
        heightAnim.addUpdateListener {
            val layoutParams = view.layoutParams
            layoutParams.height = it.animatedValue as Int
            view.layoutParams = layoutParams
            if (it.animatedFraction == 1f) {
                view.visibility = View.INVISIBLE
            }
        }
        heightAnim.start()
    }

}