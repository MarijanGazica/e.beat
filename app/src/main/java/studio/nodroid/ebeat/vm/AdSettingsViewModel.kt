package studio.nodroid.ebeat.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.nodroid.ebeat.sharedPrefs.SharedPrefs

class AdSettingsViewModel(private val sharedPrefs: SharedPrefs) : ViewModel() {

    val adSetupDone = MutableLiveData<Boolean>()

    fun userNotEea() {
        sharedPrefs.setPersonalisedOk()
        adSetupDone.value = true
    }

    fun consentError() {
        sharedPrefs.setAdsDisabled()
        adSetupDone.value = true
    }

    fun selectedNonPersonalisedAds() {
        sharedPrefs.setPersonalisedNotOk()
        adSetupDone.value = true
    }

    fun selectedPersonalisedAds() {
        sharedPrefs.setPersonalisedOk()
        adSetupDone.value = true
    }
}