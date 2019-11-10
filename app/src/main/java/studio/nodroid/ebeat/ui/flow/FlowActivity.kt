package studio.nodroid.ebeat.ui.flow

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import kotlinx.android.synthetic.main.activity_flow.*
import studio.nodroid.ebeat.R

class FlowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setContentView(R.layout.activity_flow)

        val animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.ic_e_beat_logo_dot)
        animatedVectorDrawableCompat?.registerAnimationCallback(
            object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    icon.post { animatedVectorDrawableCompat.start() }
                }
            })
        icon.setImageDrawable(animatedVectorDrawableCompat)
        animatedVectorDrawableCompat?.start()

    }
}