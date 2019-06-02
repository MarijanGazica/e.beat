package studio.nodroid.ebeat.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.ads.consent.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.ads.AdSettingsViewModel
import studio.nodroid.ebeat.ui.flow.FlowActivity
import java.net.URL


class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModel<SplashViewModel>()
    private val adSettingsViewModel by viewModel<AdSettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.requirementsMet.observe(this, Observer { userReady ->
            if (userReady) {
                startMain()
            }
        })

        adSettingsViewModel.adSetupDone.observe(this, Observer { done ->
            if (done) {
                viewModel.adSetupDone()
            }
        })

        checkConsent()
    }

    private fun checkConsent() {
        val consentInformation = ConsentInformation.getInstance(this)
        val pubId = resources.getString(R.string.admob_pub_id)
        val publisherIds = arrayOf(pubId)
        consentInformation.requestConsentInfoUpdate(publisherIds, object : ConsentInfoUpdateListener {
            override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                if (consentInformation.isRequestLocationInEeaOrUnknown) {
                    handleConsent(consentStatus)
                } else {
                    adSettingsViewModel.userNotEea()
                }
            }

            override fun onFailedToUpdateConsentInfo(errorDescription: String) {
                adSettingsViewModel.consentError()
            }
        })
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

    private fun startMain() {
        startActivity(Intent(this, FlowActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}