package studio.nodroid.bloodpressurehelper.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*
import studio.nodroid.bloodpressurehelper.R


class MainActivity : AppCompatActivity() {

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

}
