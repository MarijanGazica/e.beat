package studio.nodroid.bloodpressurehelper.sharedPrefs

import android.content.SharedPreferences

class SharedPrefsImpl(private val sharedPreferences: SharedPreferences) : SharedPrefs {
    private val lastUserId = "last_user_id"

    override fun saveLastUserId(id: Int) = sharedPreferences.edit().putInt(lastUserId, id).apply()

    override fun getLastUserId(): Int = sharedPreferences.getInt(lastUserId, -1)
}

interface SharedPrefs {

    fun saveLastUserId(id: Int)

    fun getLastUserId(): Int
}