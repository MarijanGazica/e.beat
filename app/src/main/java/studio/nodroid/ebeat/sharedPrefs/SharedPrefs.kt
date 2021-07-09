package studio.nodroid.ebeat.sharedPrefs

import android.content.SharedPreferences

class SharedPrefsImpl(private val sharedPreferences: SharedPreferences) : SharedPrefs {

    private val lastUserId = "last_user_id"
    private val shouldShowFlowUpdateWelcome = "shouldShowFlowUpdateWelcome"

    override fun saveLastUserId(id: Int) {
        sharedPreferences.edit().putInt(lastUserId, id).apply()
    }

    override fun getLastUserId(): Int {
        return sharedPreferences.getInt(lastUserId, -1)
    }

    override fun shouldShowFlowUpdateWelcome(): Boolean {
        return sharedPreferences.getBoolean(shouldShowFlowUpdateWelcome, true)
    }

    override fun setFlowUpdateWelcomeSeen() {
        sharedPreferences.edit().putBoolean(shouldShowFlowUpdateWelcome, false).apply()
    }
}

//    override fun setPersonalisedOk() {
//        sharedPreferences.edit().putInt(adSettings, 1).apply()
//    }
//
//    override fun setAdsDisabled() {
//        sharedPreferences.edit().putInt(adSettings, 0).apply()
//    }
//
//    override fun setPersonalisedNotOk() {
//        sharedPreferences.edit().putInt(adSettings, 2).apply()
//    }
//
//    override fun getAdStatus(): AdStatus {
//        return when (sharedPreferences.getInt(adSettings, 0)) {
//            1 -> AdStatus.PERSONALISED
//            2 -> AdStatus.NON_PERSONALISED
//            else -> AdStatus.DISABLED
//        }
//    }


interface SharedPrefs {
    fun saveLastUserId(id: Int)
    fun getLastUserId(): Int
    fun shouldShowFlowUpdateWelcome(): Boolean
    fun setFlowUpdateWelcomeSeen()
//    fun setPersonalisedOk()
//    fun setAdsDisabled()
//    fun setPersonalisedNotOk()
//    fun getAdStatus(): AdStatus
}

//enum class AdStatus {
//    DISABLED, NON_PERSONALISED, PERSONALISED
//}