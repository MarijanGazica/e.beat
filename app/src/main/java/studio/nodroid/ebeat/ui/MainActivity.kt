package studio.nodroid.ebeat.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.ads.consent.*
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.ads.AdSettingsViewModel
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.analytics.AnalyticsEvent
import studio.nodroid.ebeat.ui.graphs.GraphsFragment
import studio.nodroid.ebeat.ui.inputHistory.InputHistoryFragment
import studio.nodroid.ebeat.ui.pressureInput.PressureInputFragment
import studio.nodroid.ebeat.ui.userPicker.UserPickerDialog
import studio.nodroid.ebeat.ui.userPicker.UserPickerViewModel
import studio.nodroid.ebeat.utils.KeyboardVisibilityProvider
import studio.nodroid.ebeat.utils.ViewHeightAnimator
import java.net.URL


class MainActivity : AppCompatActivity() {

    private val keyboardVisibilityProvider by inject<KeyboardVisibilityProvider>()
    private val userPickerViewModel by viewModel<UserPickerViewModel>()
    private val adSettingsViewModel by viewModel<AdSettingsViewModel>()

    private val analytics by inject<Analytics>()

    private val bottomNavHeightVisibility by lazy { ViewHeightAnimator(bottomNavView) }

    private val userPickerDialog by lazy {
        UserPickerDialog().apply {
            onSelect = userPickerViewModel.onUserSelected
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        bottomNavView.setOnNavigationItemSelectedListener { menuItem ->
            val destination = when (menuItem.itemId) {
                R.id.inputHistoryFragment -> InputHistoryFragment()
                R.id.graphFragment -> GraphsFragment()
//                R.id.settingsFragment -> SettingsFragment()
                else -> PressureInputFragment()
            }
            showDestination(destination)
            true
        }

        bottomNavView.setOnNavigationItemReselectedListener { }

        showDestination(PressureInputFragment())

        userPickerViewModel.allUsers.observe(this, Observer { userPickerViewModel.usersAvailable() })
        userPickerViewModel.activeUser.observe(this, Observer { it?.run { supportActionBar?.title = it.name } })

        toolbar.setOnClickListener { showUserPicker() }
    }

    private fun showDestination(destination: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragmentContainer, destination)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        keyboardVisibilityProvider.getKeyboardVisibility(root).observe(this, Observer {
            bottomNavHeightVisibility.visible = !it
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.run {

            val consentInformation = ConsentInformation.getInstance(this@MainActivity)

            val pubId = resources.getString(R.string.admob_pub_id)
            val publisherIds = arrayOf(pubId)
            consentInformation.requestConsentInfoUpdate(publisherIds, object : ConsentInfoUpdateListener {
                override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                    if (consentInformation.isRequestLocationInEeaOrUnknown) {
                        menuInflater.inflate(R.menu.main_menu, this@run)
                    } else {
                        menuInflater.inflate(R.menu.main_menu_no_ad, this@run)
                    }
                }

                override fun onFailedToUpdateConsentInfo(errorDescription: String) {
                    menuInflater.inflate(R.menu.main_menu_no_ad, this@run)
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return item?.run {
            when {
                this.itemId == R.id.selectUser -> {
                    showUserPicker()
                    return@run true
                }
                this.itemId == R.id.privacyPolicy -> {
                    analytics.logEvent(AnalyticsEvent.PRIVACY_POLICY)
                    startActivity(Intent(this@MainActivity, WebViewActivity::class.java))
                    return@run true
                }
                this.itemId == R.id.adSettings -> {
                    analytics.logEvent(AnalyticsEvent.AD_SETTINGS)
                    requestConsent()
                    return@run true
                }
                else -> super.onOptionsItemSelected(item)
            }
        } ?: super.onOptionsItemSelected(item)
    }

    private fun showUserPicker() {
        analytics.logEvent(AnalyticsEvent.USER_PICKER_OPEN)
        userPickerDialog.show(supportFragmentManager, "userPicker")
    }

    private fun requestConsent() {
        val privacyString = resources.getString(R.string.privacy_policy_url)
        val privacyUrl = URL(privacyString)

        var form: ConsentForm? = null
        form = ConsentForm.Builder(this, privacyUrl)
            .withListener(object : ConsentFormListener() {
                override fun onConsentFormLoaded() {
                    form?.show()
                    // Consent form loaded successfully.
                }

                override fun onConsentFormOpened() {
                    // Consent form was displayed.
                }

                override fun onConsentFormClosed(consentStatus: ConsentStatus, userPrefersAdFree: Boolean?) {
                    handleConsent(consentStatus)
                }

                override fun onConsentFormError(errorDescription: String?) {
                    adSettingsViewModel.consentError()
                }
            })
            .withPersonalizedAdsOption()
            .withNonPersonalizedAdsOption()
//            .withAdFreeOption()
            .build()

        form?.load()
    }

    private fun handleConsent(consentStatus: ConsentStatus) {
        when (consentStatus) {
            ConsentStatus.UNKNOWN -> requestConsent()
            ConsentStatus.NON_PERSONALIZED -> {
                adSettingsViewModel.selectedNonPersonalisedAds()
            }
            ConsentStatus.PERSONALIZED -> {
                adSettingsViewModel.selectedPersonalisedAds()
            }
        }
    }
}