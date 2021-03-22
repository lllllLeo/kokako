package com.example.kokako

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

// ㅇㄱ
class SettingsActivity : AppCompatActivity(){
    private var adView : AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_framelayout, SettingsFragment())
                .commit()
        }
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        MobileAds.initialize(this, getString(R.string.admob_app_id))
        adView = findViewById(R.id.adView)
        val adRequest : AdRequest = AdRequest.Builder().build()
        adView!!.loadAd(adRequest)

        val d = findViewById<ImageView>(R.id.setting_back_btn)
        d.setOnClickListener{ finish() }
    }

}