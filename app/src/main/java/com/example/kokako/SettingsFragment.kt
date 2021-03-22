package com.example.kokako

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

/*
배너 광고	ca-app-pub-3940256099942544/6300978111
전면 광고	ca-app-pub-3940256099942544/1033173712
전면 동영상 광고	ca-app-pub-3940256099942544/8691691433
보상형 동영상 광고	ca-app-pub-3940256099942544/5224354917
네이티브 광고 고급형	ca-app-pub-3940256099942544/2247696110
네이티브 동영상 광고 고급형	ca-app-pub-3940256099942544/1044960115
* */
class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    private var interstitialAd : InterstitialAd? = null
    private var versionCode = 0
    private var versionName = ""
    private var appInfo : Preference? = null
    private var screen : PreferenceScreen? = null
    
    companion object {
        const val TAG = "TAG SettingsFragment"
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)
        val pInfo = context?.packageManager!!.getPackageInfo(context?.packageName, 0)
        this.versionName = pInfo.versionName
        this.versionCode = pInfo.versionCode

        screen = preferenceScreen
        appInfo = screen!!.findPreference<Preference>("app_info")

        MobileAds.initialize(context, getString(R.string.admob_app_id))
        interstitialAd = InterstitialAd(context)
        interstitialAd!!.adUnitId = getString(R.string.reward_ad_unit_id_for_test)
        Log.d(TAG, "onCreatePreferences: ${getString(R.string.reward_ad_unit_id_for_test)}")
        interstitialAd!!.loadAd(AdRequest.Builder().build())
        if (interstitialAd!!.isLoaded) {
            Log.d(TAG, "onCreatePreferences: 로드됏니")
        }

//        변화 이벤트가 일어났을 시 동작
        appInfo!!.onPreferenceChangeListener = this
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        var key : String = preference!!.key

        when(key) {
            "feedback" -> {
                Log.d(TAG, "onPreferenceTreeClick: feedback IN")
                val kakaoOpenLink = "https://open.kakao.com/o/syo6CC3c"
                val feedbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(kakaoOpenLink))
                startActivity(feedbackIntent)
            }
            "app_review" -> {
                Log.d(TAG, "onPreferenceTreeClick: app review IN")
                val reviewIntent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + requireActivity().application.packageName))
                startActivity(reviewIntent)
            }
            "open_license" -> {
                startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                OssLicensesMenuActivity.setActivityTitle("Ossl Title")
            }
            "watch_ad_for_dev" -> {
                Log.d(TAG, "onPreferenceTreeClick: 개발자를 위해 광고보기")
                if (interstitialAd!!.isLoaded) {
                    interstitialAd!!.show()
                    Log.d(TAG, "onPreferenceTreeClick:  쇼쇼")
                }
                interstitialAd!!.adListener = object : AdListener() {
                    override fun onAdClosed() {
                        interstitialAd!!.loadAd(AdRequest.Builder().build())
                    }
                }
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
    
//    액티비티 실행할 때 저장되어 있는 summary값을 set
//    안하면 안뜸
    private fun updateSummary() {
        appInfo!!.summary = versionName
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val value : String = newValue as String

        if (preference == appInfo){
//            appInfo!!.setSummary()
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        updateSummary()
        Log.d(TAG, "onResume: In")
        
    }
}
