package studio.nodroid.ebeat.ui

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.activity_webview.*
import org.koin.android.ext.android.inject
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.analytics.AnalyticsScreen

class WebViewActivity : AppCompatActivity() {

    private val analytics by inject<Analytics>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setContentView(R.layout.activity_webview)

        if (savedInstanceState == null) {
            analytics.logScreenEvent(AnalyticsScreen.APP_INFO)
        }

        webview.doOnApplyWindowInsets { target, insets, initialState ->
            target.updatePadding(
                bottom = initialState.paddings.bottom + insets.systemWindowInsetBottom,
                top = initialState.paddings.top + insets.systemWindowInsetTop
            )
        }

        close.doOnApplyWindowInsets { target, insets, initialState ->
            target.updatePadding(
                top = initialState.paddings.top + insets.systemWindowInsetTop
            )
        }

        webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.scrollTo(0, 0)
            }
        }
        webview.loadUrl(getString(R.string.privacy_policy_url))

        close.setOnClickListener { finish() }
    }
}