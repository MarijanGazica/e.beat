package studio.nodroid.ebeat.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.ebeat.ui.flow.FlowActivity
import studio.nodroid.ebeat.ui.flow.flowUpdateWelcome.FlowUpdateWelcomeActivity


class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.requirementsMet.observe(this, Observer { event ->
            when (event) {
                SplashViewModel.Event.SHOW_MAIN -> startMain()
                SplashViewModel.Event.SHOW_FLOW_UPDATE_WELCOME -> startFlowUpdateWelcome()
                null -> Unit // noop
            }
        })
    }

    private fun startMain() {
        startActivity(Intent(this, FlowActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun startFlowUpdateWelcome() {
        startActivity(Intent(this, FlowUpdateWelcomeActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}