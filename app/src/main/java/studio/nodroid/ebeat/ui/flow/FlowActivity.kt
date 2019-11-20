package studio.nodroid.ebeat.ui.flow

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import studio.nodroid.ebeat.R

class FlowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setContentView(R.layout.activity_flow)
    }
}