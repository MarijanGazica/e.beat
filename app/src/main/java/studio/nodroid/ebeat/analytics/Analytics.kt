package studio.nodroid.ebeat.analytics

import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class Analytics(app: Application) {

    private val firebase = FirebaseAnalytics.getInstance(app)

    fun logEvent(event: AnalyticsEvent) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CONTENT, event.name)
        firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }
}

enum class AnalyticsEvent(value: String) {
    USER_PICKER_OPEN("user_picker_open"),
    USER_ADD("user_add"),
    USER_CHANGE("user_change"),
    READING_ADD("reading_add"),
    READING_WITH_DESC("reading_with_desc"),
    LIST_7("list_7"),
    LIST_30("list_30"),
    LIST_ALL("list_all"),
    LIST_RANGE("list_range"),
    GRAPHS_7("graphs_7"),
    GRAPHS_30("graphs_30"),
    GRAPHS_ALL("graphs_all"),
    GRAPHS_RANGE("graphs_range"),
    PRIVACY_POLICY("privacy_policy"),
    AD_SETTINGS("ad_settings"),
    AD_PERSONALISED("ad_personalised"),
    AD_NOT_PERSONALISED("ad_not_personalised"),
    AD_NOT_EEA("ad_not_eea"),
    AD_CONSENT_ERROR("ad_consent_error")
}