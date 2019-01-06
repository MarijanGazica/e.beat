package studio.nodroid.bloodpressurehelper.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.ui.view.UserPickerDialog
import studio.nodroid.bloodpressurehelper.utils.KeyboardVisibilityProvider
import studio.nodroid.bloodpressurehelper.utils.ViewHeightAnimator
import studio.nodroid.bloodpressurehelper.vm.UserPickerViewModel


class MainActivity : AppCompatActivity() {

    private val keyboardVisibilityProvider by inject<KeyboardVisibilityProvider>()
    private val userPickerViewModel by viewModel<UserPickerViewModel>()

    private val bottomNavHeightVisibility by lazy { ViewHeightAnimator(bottomNavView) }

    private val userPickerDialog by lazy {
        UserPickerDialog().apply {
            onAddUserSelected = addUserSelected
            onSelect = userPickerViewModel.onUserSelected
        }
    }

    private val addUserSelected: () -> Unit = {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        bottomNavView.setupWithNavController(findNavController(this, R.id.navigationHostFragment))

        NavHostFragment.findNavController(navigationHostFragment)
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.inputHistoryFragment, R.id.settingsFragment, R.id.pressureInputFragment -> {
                        bottomNavView.visibility = View.VISIBLE
                    }
                    else -> {
                        bottomNavView.visibility = View.GONE
                    }
                }
            }

        userPickerViewModel.allUsers.observe(this, Observer { userPickerViewModel.usersAvailable() })
        userPickerViewModel.activeUser.observe(this, Observer { it?.run { supportActionBar?.title = it.name } })

        toolbar.setOnClickListener { showUserPicker() }
    }

    override fun onSupportNavigateUp() = findNavController(this, R.id.navigationHostFragment).navigateUp()

    override fun onResume() {
        super.onResume()
        keyboardVisibilityProvider.getKeyboardVisibility(root).observe(this, Observer {
            bottomNavHeightVisibility.visible = !it
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.run {
            menuInflater.inflate(R.menu.main_menu, this)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return item?.run {
            if (this.itemId == R.id.selectUser) {
                showUserPicker()
                return@run true
            } else {
                super.onOptionsItemSelected(item)
            }
        } ?: super.onOptionsItemSelected(item)
    }

    private fun showUserPicker() {
        userPickerDialog.show(supportFragmentManager, "userPicker")
    }
}
