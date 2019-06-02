package studio.nodroid.ebeat.ads

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.analytics.AnalyticsEvent
import studio.nodroid.ebeat.sharedPrefs.SharedPrefs

class AdSettingsViewModel(private val sharedPrefs: SharedPrefs, private val analytics: Analytics) : ViewModel() {

    val adSetupDone = MutableLiveData<Boolean>()

    fun userNotEea() {
        analytics.logEvent(AnalyticsEvent.AD_NOT_EEA)
        sharedPrefs.setPersonalisedOk()
        adSetupDone.value = true
    }

    fun consentError() {
        analytics.logEvent(AnalyticsEvent.AD_CONSENT_ERROR)
        sharedPrefs.setAdsDisabled()
        adSetupDone.value = true
    }

    fun selectedNonPersonalisedAds() {
        analytics.logEvent(AnalyticsEvent.AD_NOT_PERSONALISED)
        sharedPrefs.setPersonalisedNotOk()
        adSetupDone.value = true
    }

    fun selectedPersonalisedAds() {
        analytics.logEvent(AnalyticsEvent.AD_PERSONALISED)
        sharedPrefs.setPersonalisedOk()
        adSetupDone.value = true
    }
}