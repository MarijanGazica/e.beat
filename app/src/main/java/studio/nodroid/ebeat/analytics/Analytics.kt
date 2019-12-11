package studio.nodroid.ebeat.analytics

import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class Analytics(app: Application) {

    private val firebase = FirebaseAnalytics.getInstance(app)

    fun logEvent(event: AnalyticsEvent, bundle: Bundle = Bundle()) {
        bundle.putString("name", event.eventName)
        firebase.logEvent("user_action", bundle)
    }

    fun logScreenEvent(event: AnalyticsScreen) {
        val bundle = Bundle()
        bundle.putString("name", event.screenName)
        firebase.logEvent("screen_open", bundle)
    }
}

enum class AnalyticsScreen(val screenName: String) {
    ACTIONS("Actions"),
    ADD_READING("Add reading"),
    HISTORY_LIST("History list"),
    HISTORY_GRAPH("History graph"),
    MANAGE_USERS("Manage users"),
    APP_INFO("App info"),
}

enum class AnalyticsEvent(val eventName: String) {
    READING_ADDED("Reading added"),
    READING_DISCARDED("Reading discarded"),
    VIEWED_LIST("Viewed list"),
    VIEWED_GRAPH("Viewed graph"),
    USER_ADDED("User added"),
    USER_DELETED("User deleted")
}