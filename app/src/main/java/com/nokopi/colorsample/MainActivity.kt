package com.nokopi.colorsample

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import com.nokopi.colorsample.view.CustomFTNButton
import com.nokopi.colorsample.view.CustomNBButton
import com.nokopi.colorsample.view.CustomPLButton
import com.nokopi.colorsample.view.CustomSLBButton
import kotlin.Exception

class MainActivity : AppCompatActivity() {

    private val MY_REQUEST_CODE = 1

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

//        val nbButton: Button = findViewById(R.id.nbButton)
        val nbButton: CustomNBButton = findViewById(R.id.nbButton)
//        val ftnButton: Button = findViewById(R.id.ftnButton)
        val ftnButton: CustomFTNButton = findViewById(R.id.ftnButton)
//        val slbButton: Button = findViewById(R.id.slbButton)
        val slbButton: CustomSLBButton = findViewById(R.id.slbButton)
//        val plButton: Button = findViewById(R.id.plButton)
        val plButton: CustomPLButton = findViewById(R.id.plButton)


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

    override fun onResume() {
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

    fun gotoPrivacyPolicy(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/RyotaHiyama/colorSample/blob/master/Privacy%20Policy"))
        startActivity(intent)
    }

}