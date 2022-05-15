//package com.nokopi.colorsample
//
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.play.core.appupdate.AppUpdateManager
//import com.google.android.play.core.appupdate.AppUpdateInfo
//import com.google.android.play.core.install.InstallStateUpdatedListener
//import android.os.Bundle
//import com.nokopi.colorsample.R
//import com.google.android.play.core.appupdate.AppUpdateManagerFactory
//import com.google.android.play.core.install.InstallState
//import com.google.android.play.core.install.model.InstallStatus
//import com.google.android.play.core.install.model.AppUpdateType
//import com.google.android.play.core.tasks.OnSuccessListener
//import com.google.android.play.core.install.model.UpdateAvailability
//import android.content.IntentSender.SendIntentException
//import android.content.Intent
//import android.app.Activity
//import android.widget.Toast
//import com.google.android.material.snackbar.Snackbar
//import android.content.res.Resources.NotFoundException
//import android.view.View
//import com.google.android.play.core.install.model.ActivityResult
//import com.google.android.play.core.tasks.Task
//import java.lang.Exception
//
//class test : AppCompatActivity() {
//    private var mAppUpdateManager: AppUpdateManager? = null
//    private val RC_APP_UPDATE = 999
//    private var inAppUpdateType = 0
//    private var appUpdateInfoTask: Task<AppUpdateInfo>? = null
//    private var installStateUpdatedListener: InstallStateUpdatedListener? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Creates instance of the manager.
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
//    }
//
//    override fun onDestroy() {
//        mAppUpdateManager!!.unregisterListener(installStateUpdatedListener!!)
//        super.onDestroy()
//    }
//
//    override fun onResume() {
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
//                    } catch (e: SendIntentException) {
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
//        super.onResume()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_APP_UPDATE) {
//            //when user clicks update button
//            if (resultCode == RESULT_OK) {
//                Toast.makeText(this@MainActivity, "App download starts...", Toast.LENGTH_LONG)
//                    .show()
//            } else if (resultCode != RESULT_CANCELED) {
//                //if you want to request the update again just call checkUpdate()
//                Toast.makeText(this@MainActivity, "App download canceled.", Toast.LENGTH_LONG)
//                    .show()
//            } else if (resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
//                Toast.makeText(this@MainActivity, "App download failed.", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//    private fun inAppUpdate() {
//        try {
//            // Checks that the platform will allow the specified type of update.
//            appUpdateInfoTask!!.addOnSuccessListener { appUpdateInfo ->
//                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE // For a flexible update, use AppUpdateType.FLEXIBLE
//                    && appUpdateInfo.isUpdateTypeAllowed(inAppUpdateType)
//                ) {
//                    // Request the update.
//                    try {
//                        mAppUpdateManager.startUpdateFlowForResult( // Pass the intent that is returned by 'getAppUpdateInfo()'.
//                            appUpdateInfo,  // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
//                            inAppUpdateType,  // The current activity making the update request.
//                            this@MainActivity,  // Include a request code to later monitor this update request.
//                            RC_APP_UPDATE
//                        )
//                    } catch (ignored: SendIntentException) {
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
//                findViewById(R.id.id_of_root_loyout),
//                "An update has just been downloaded.\nRestart to update",
//                Snackbar.LENGTH_INDEFINITE
//            )
//            snackbar.setAction("INSTALL") { view: View? ->
//                if (mAppUpdateManager != null) {
//                    mAppUpdateManager!!.completeUpdate()
//                }
//            }
//            snackbar.setActionTextColor(resources.getColor(R.color.install_color))
//            snackbar.show()
//        } catch (e: NotFoundException) {
//            e.printStackTrace()
//        }
//    }
//}