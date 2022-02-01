package com.example.vidrom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log

class PhoneStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        Log.d("PhoneStateReceiver: ", "state: $state")
        state?.let { state ->
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(Intent(context, FloatingWindow::class.java))
                } else {
                    context.startService(Intent(context, FloatingWindow::class.java))
                }

            } else if (state == TelephonyManager.EXTRA_STATE_IDLE){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.stopService(Intent(context, FloatingWindow::class.java))
                } else {
                    context.stopService(Intent(context, FloatingWindow::class.java))
                }
            } else {
            }
        }
    }

}