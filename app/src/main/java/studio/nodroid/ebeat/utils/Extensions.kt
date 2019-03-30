package studio.nodroid.ebeat.utils

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.textfield.TextInputLayout
import studio.nodroid.ebeat.R


fun TextInputLayout.onTextChanged(onChange: (String) -> Unit) {
    this.editText?.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            onChange(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

fun EditText.onTextChanged(onChange: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            onChange(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

fun TextInputLayout.onIntInputChanged(onChange: (Int) -> Unit) {
    this.editText?.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s?.toString().isNullOrBlank()) {
                error = null
                onChange(-1)
                return
            }
            try {
                error = null
                onChange(s.toString().toInt())
            } catch (e: NumberFormatException) {
                error = context.resources.getString(R.string.numberInputInvalid)
            }

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

fun TextInputLayout.setSelectedListener(action: () -> Unit) {
    setOnClickListener { action() }
    setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            action()
        }
    }

    editText?.setOnClickListener { action() }
    editText?.setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            action()
        }
    }
}

fun FrameLayout.inflateAd(context: Context) {
    val adView = AdView(context)

    val displayMetrics = context.resources.displayMetrics
    val dpWidth = displayMetrics.widthPixels / displayMetrics.density

    adView.setBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.transparent, context.theme))

    adView.adSize = AdSize(dpWidth.toInt(), 60)
    adView.adUnitId = resources.getString(R.string.admob_banner_non_native)

    addView(adView)


}

fun AdView.inflateAd(personalised: Boolean) {
    val listener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            this@inflateAd.visibility = View.VISIBLE
        }

        override fun onAdFailedToLoad(p0: Int) {
            super.onAdFailedToLoad(p0)
            this@inflateAd.visibility = View.GONE
        }

    }

    adListener = listener


    val adRequestBuilder = AdRequest.Builder()
        .addTestDevice("190B5E72876DC84A5968A67A1DCA8691") // s9+
        .addTestDevice("8168E714BB90F44F74FC714181827C3A") // htc
        .addTestDevice("4FB13D1519FF1AB35B624F3F800C34C5") // moto g4

    if (!personalised) {
        val extras = Bundle()
        extras.putString("npa", "1")
        adRequestBuilder.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
    }

    loadAd(adRequestBuilder.build())
}