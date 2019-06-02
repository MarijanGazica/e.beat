package studio.nodroid.ebeat.ads

import android.os.Bundle
import android.view.View
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView


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