package studio.nodroid.ebeat.ui.flow.flowUpdateWelcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.activity_flow_update_welcome.*
import org.koin.android.ext.android.inject
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.analytics.AnalyticsScreen
import studio.nodroid.ebeat.sharedPrefs.SharedPrefs
import studio.nodroid.ebeat.ui.flow.FlowActivity
import studio.nodroid.ebeat.utils.startDotAnimation

class FlowUpdateWelcomeActivity : AppCompatActivity() {

    private val analytics by inject<Analytics>()
    private val sharedPrefs by inject<SharedPrefs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setContentView(R.layout.activity_flow_update_welcome)

        analytics.logScreenEvent(AnalyticsScreen.FLOW_UPDATE_WELCOME)

        motionLayout.doOnApplyWindowInsets { target, insets, initialState ->
            target.updatePadding(
                bottom = initialState.paddings.bottom + insets.systemWindowInsetBottom
            )
        }

        motionLayout.transitionToState(R.id.visible)

        icon.startDotAnimation()
        icon.transitionName = "dot_animation"

        start.setOnClickListener {
            startMain()
        }
    }

    private fun startMain() {
        sharedPrefs.setFlowUpdateWelcomeSeen()
        motionLayout.transitionToState(R.id.hidden)
        startActivity(Intent(this, FlowActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

}