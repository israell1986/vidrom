package com.example.vidrom

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.vidrom.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var hasPermission: Boolean = false
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: AlertDialog

    var PERMISSIONS = arrayOf(
        Manifest.permission.READ_PHONE_STATE
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        if (isServiceRunning()) {
            stopService(Intent(this, FloatingWindow::class.java))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkForPermission()
        } else {
            setExitState()
        }


        binding.button.setOnClickListener { view ->
            if (hasPermission) {
                finish()
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkForPermission()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkForPermission() {
        val granted = PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        if (granted) {
            checkFloatingPermission()
        } else {
            requestPermissions(
                PERMISSIONS,
                1
            )
        }

    }

    private fun checkFloatingPermission() {
        if (checkPermission()) {
            setExitState()

//            startService(Intent(this, FloatingWindow::class.java))
//            finish()
        } else {
            requestFloatingWindowPermission()
        }
    }

    private fun setExitState() {
        hasPermission = true
        binding.button.text = "exit"
        binding.text.text = "You have permission, you can exit from app"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    checkFloatingPermission()
                }
                return
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun isServiceRunning(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (FloatingWindow::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun requestFloatingWindowPermission() {

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle("Permission")
        builder.setMessage("Enable display floating from settings")
        builder.setPositiveButton(
            "Open settings",
            DialogInterface.OnClickListener { dialogInterface, i ->
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, RESULT_OK)
            })
        dialog = builder.create()
        dialog.show()
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            return true
        }
    }

}