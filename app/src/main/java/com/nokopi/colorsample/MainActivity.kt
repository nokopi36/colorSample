package com.nokopi.colorsample

import android.content.Intent
import android.content.IntentSender
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import kotlin.Exception

class MainActivity : AppCompatActivity() {

//    private var mAppUpdateManager: AppUpdateManager? = null
//    private val RC_APP_UPDATE = 0
//    private var inAppUpdateType = 0
//    private var appUpdateInfoTask: Task<AppUpdateInfo>? = null
//    private var installStateUpdatedListener: InstallStateUpdatedListener? = null
    private val MY_REQUEST_CODE = 0
//    private val appUpdateManager = AppUpdateManagerFactory.create(this)
//    // Returns an intent object that you use to check for an update.
//    private val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val appUpdateManager = AppUpdateManagerFactory.create(this)

// Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)
            ) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    MY_REQUEST_CODE)
            }
        }



        val versionNameText: TextView = findViewById(R.id.versionName)

        val version: String
        try {
            val packageName = packageName
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            version = packageInfo.versionName
            "ver.$version".also { versionNameText.text = it }
        } catch (e: Exception){
            e.printStackTrace()
        }

        val nbButton: Button = findViewById(R.id.nbButton)
        val ftnButton: Button = findViewById(R.id.ftnButton)
        val slbButton: Button = findViewById(R.id.slbButton)
        val plButton: Button = findViewById(R.id.plButton)


        nbButton.setOnClickListener {
            val nbIntent = Intent(this, NBCustomColor::class.java)
            nbIntent.putExtra("type", "nb")
            startActivity(nbIntent)
        }

        ftnButton.setOnClickListener {
            val ftnIntent = Intent(this, FTNCustomColor::class.java)
            ftnIntent.putExtra("type", "ftn")
            startActivity(ftnIntent)
        }

        slbButton.setOnClickListener {
            val slbIntent = Intent(this, SLBCustomColor::class.java)
            slbIntent.putExtra("type", "slb")
            startActivity(slbIntent)
        }

        plButton.setOnClickListener {
            val plIntent = Intent(this, PLCustomColor::class.java)
            plIntent.putExtra("type", "slb")
            startActivity(plIntent)
        }

        // Creates instance of the manager.
//        mAppUpdateManager = AppUpdateManagerFactory.create(this)
//        // Returns an intent object that you use to check for an update.
//        appUpdateInfoTask = mAppUpdateManager!!.appUpdateInfo
//        //lambda operation used for below listener
//        //For flexible update
//        installStateUpdatedListener = InstallStateUpdatedListener { installState: InstallState ->
//            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
//                popupSnackbarForCompleteUpdate()
//            }
//        }
//        mAppUpdateManager!!.registerListener(installStateUpdatedListener!!)
//
//        //For Immediate
//        inAppUpdateType = AppUpdateType.IMMEDIATE //1
//        inAppUpdate()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "Update Complete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
//        //For Immediate
//        inAppUpdateType = AppUpdateType.IMMEDIATE //1
//        inAppUpdate()
        super.onStart()
    }

    override fun onDestroy() {
//        mAppUpdateManager!!.unregisterListener(installStateUpdatedListener!!)
        super.onDestroy()
    }

    override fun onResume() {
//        try {
//            mAppUpdateManager!!.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
//                if (appUpdateInfo.updateAvailability() ==
//                    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
//                ) {
//                    // If an in-app update is already running, resume the update.
//                    try {
//                        mAppUpdateManager!!.startUpdateFlowForResult(
//                            appUpdateInfo,
//                            inAppUpdateType,
//                            this,
//                            RC_APP_UPDATE
//                        )
//                    } catch (e: IntentSender.SendIntentException) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//            mAppUpdateManager!!.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
//                //For flexible update
//                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
//                    popupSnackbarForCompleteUpdate()
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        super.onResume()

        val appUpdateManager = AppUpdateManagerFactory.create(this)

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        IMMEDIATE,
                        this,
                        MY_REQUEST_CODE
                    )
                }
            }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_APP_UPDATE) {
//            //when user clicks update button
//            when {
//                resultCode == RESULT_OK -> {
//                    Toast.makeText(this@MainActivity, "App download starts...", Toast.LENGTH_LONG)
//                        .show()
//                }
//                resultCode != RESULT_CANCELED -> {
//                    //if you want to request the update again just call checkUpdate()
//                    Toast.makeText(this@MainActivity, "App download canceled.", Toast.LENGTH_LONG)
//                        .show()
//                }
//                resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
//                    Toast.makeText(this@MainActivity, "App download failed.", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }

//    private fun inAppUpdate() {
//        try {
//            // Checks that the platform will allow the specified type of update.
//            appUpdateInfoTask!!.addOnSuccessListener { appUpdateInfo ->
//                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE // For a flexible update, use AppUpdateType.FLEXIBLE
//                    && appUpdateInfo.isUpdateTypeAllowed(inAppUpdateType)
//                ) {
//                    // Request the update.
//                    try {
//                        mAppUpdateManager?.startUpdateFlowForResult( // Pass the intent that is returned by 'getAppUpdateInfo()'.
//                            appUpdateInfo,  // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
//                            inAppUpdateType,  // The current activity making the update request.
//                            this@MainActivity,  // Include a request code to later monitor this update request.
//                            RC_APP_UPDATE
//                        )
//                    } catch (ignored: IntentSender.SendIntentException) {
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun popupSnackbarForCompleteUpdate() {
//        try {
//            val snackbar = Snackbar.make(
//                findViewById(android.R.id.content),
//                "An update has just been downloaded.\nRestart to update",
//                Snackbar.LENGTH_INDEFINITE
//            )
//            snackbar.setAction("INSTALL") { view: View? ->
//                if (mAppUpdateManager != null) {
//                    mAppUpdateManager!!.completeUpdate()
//                }
//            }
//            snackbar.setActionTextColor(resources.getColor(R.color.white))
//            snackbar.show()
//        } catch (e: Resources.NotFoundException) {
//            e.printStackTrace()
//        }
//    }

    fun gotoPrivacyPolicy(v: View){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/RyotaHiyama/colorSample/blob/master/Privacy%20Policy"))
        startActivity(intent)
    }

}