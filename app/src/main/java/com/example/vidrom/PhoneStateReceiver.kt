package com.example.vidrom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log

class PhoneStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        state?.let { state ->
            if (prevState == TelephonyManager.EXTRA_STATE_RINGING && state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
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
            }
            prevState = state
        }
    }

    companion object {
        var prevState = ""
    }


}