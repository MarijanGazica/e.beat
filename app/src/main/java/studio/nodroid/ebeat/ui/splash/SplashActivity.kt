package studio.nodroid.ebeat.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.ads.consent.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.ui.MainActivity
import java.net.URL


class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.requirementsMet.observe(this, Observer { userReady ->
            if (userReady) {
                startMain()
            }
        })

        checkConsent()
    }

    private fun checkConsent() {
        val pubId = resources.getString(R.string.admob_pub_id)
        val consentInformation = ConsentInformation.getInstance(this)
        if (consentInformation.isRequestLocationInEeaOrUnknown) {

            val publisherIds = arrayOf(pubId)
            consentInformation.requestConsentInfoUpdate(publisherIds, object : ConsentInfoUpdateListener {
                override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                    handleConsent(consentStatus)
                }

                override fun onFailedToUpdateConsentInfo(errorDescription: String) {
                    viewModel.consentError()
                }
            })
        } else {
            viewModel.userNotEea()
        }
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
                    viewModel.consentError()
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
                viewModel.selectedNonPersonalisedAds()
            }
            ConsentStatus.PERSONALIZED -> {
                viewModel.selectedPersonalisedAds()
            }
        }
    }

    private fun startMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}