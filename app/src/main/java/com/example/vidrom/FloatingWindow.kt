package com.example.vidrom

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import de.hdodenhof.circleimageview.CircleImageView
import android.view.Gravity





class FloatingWindow : Service() {

    private lateinit var floatView: ViewGroup
    private lateinit var floatWindowLayoutParams: WindowManager.LayoutParams
    private var LAYOUT_TYPE: Int? = null
    private lateinit var windowManager: WindowManager
    private lateinit var button: CircleImageView
    private lateinit var dismissButton: ImageButton

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ip_camera)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)


        val metrics = applicationContext.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val inflater = baseContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        floatView = inflater.inflate(R.layout.floagint_window, null, false) as ViewGroup

        button = floatView.findViewById(R.id.button)
        dismissButton = floatView.findViewById(R.id.dismissButton)

        LAYOUT_TYPE = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_TOAST
        }

        floatWindowLayoutParams = WindowManager.LayoutParams(
//            (width * .4).toInt(),
//            (height * .4).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_TYPE!!,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        floatWindowLayoutParams.gravity = Gravity.TOP or Gravity.LEFT
        floatWindowLayoutParams.x = 0
        floatWindowLayoutParams.y = 0

        windowManager.addView(floatView, floatWindowLayoutParams)

        button.setOnClickListener {
            val mainIntent = Intent(Intent.ACTION_MAIN, null)
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

            val launchIntent = packageManager.getLaunchIntentForPackage("com.hichip.campro")
            launchIntent?.let {
                startActivity(it)
                stopSelf()
            }
            if (launchIntent == null) {
                Toast.makeText(
                    applicationContext, "don't have com.hichip.campro package", Toast.LENGTH_SHORT
                ).show()
            }
        }

        dismissButton.setOnClickListener {
            stopSelf()
            windowManager.removeView(floatView)
        }

        floatView.setOnTouchListener(object : View.OnTouchListener {
            val updateFloatWindowLayoutParams = floatWindowLayoutParams
            var x = 0.0
            var y = 0.0
            var px = 0.0
            var py = 0.0


            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                event?.let { motionEvent ->
                    Log.d("######", "motionEvent: ${motionEvent.action}")
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            x = updateFloatWindowLayoutParams.x.toDouble()
                            y = updateFloatWindowLayoutParams.y.toDouble()

                            px = event.rawX.toDouble()
                            py = event.rawY.toDouble()
                        }

                        MotionEvent.ACTION_MOVE -> {
                            updateFloatWindowLayoutParams.x = (x + event.rawX - px).toInt()
                            updateFloatWindowLayoutParams.y = (y + event.rawY - py).toInt()

                            windowManager.updateViewLayout(floatView, updateFloatWindowLayoutParams)
                        }

                    }
                }
                return false
            }

        })


    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        windowManager.removeView(floatView)
    }

}