package studio.nodroid.bloodpressurehelper.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.utils.KeyboardVisibilityProvider
import studio.nodroid.bloodpressurehelper.utils.ViewHeightAnimator


class MainActivity : AppCompatActivity() {

    private val keyboardVisibilityProvider by inject<KeyboardVisibilityProvider>()
    private val bottomNavHeightVisibility by lazy { ViewHeightAnimator(bottomNavView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        NavigationUI.setupWithNavController(bottomNavView, findNavController(this, R.id.navigationHostFragment))

        NavHostFragment.findNavController(navigationHostFragment)
            .addOnNavigatedListener { _, destination ->
                when (destination.id) {
                    R.id.inputHistoryFragment, R.id.settingsFragment, R.id.pressureInputFragment -> {
                        bottomNavView.visibility = View.VISIBLE
                    }
                    else -> {
                        bottomNavView.visibility = View.GONE
                    }
                }
            }
    }

    override fun onSupportNavigateUp() = findNavController(this, R.id.navigationHostFragment).navigateUp()

    override fun onResume() {
        super.onResume()
        keyboardVisibilityProvider.getKeyboardVisibility(root).observe(this, Observer {
            bottomNavHeightVisibility.visible = !it
        })
    }

}
