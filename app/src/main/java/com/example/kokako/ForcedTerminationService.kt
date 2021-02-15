package com.example.kokako

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class ForcedTerminationService : Service() {
    companion object {
        val TAG = "TAG ViewWordActivity"
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.e(TAG, "onTaskRemoved - 강제 종료 $rootIntent")
        stopSelf() // 서비스 종료
   }
}