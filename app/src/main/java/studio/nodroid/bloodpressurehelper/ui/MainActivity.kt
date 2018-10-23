package studio.nodroid.bloodpressurehelper.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*
import studio.nodroid.bloodpressurehelper.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NavigationUI.setupWithNavController(bottomNavView, findNavController(this, R.id.navigationHostFragment))
    }

    override fun onSupportNavigateUp() = findNavController(this, R.id.navigationHostFragment).navigateUp()

}
