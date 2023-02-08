package com.nokopi.colorsample

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.nokopi.colorsample.databinding.ActivityMainBinding
import kotlin.Exception

class MainActivity : AppCompatActivity() {

    private val MY_REQUEST_CODE = 1
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

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

        val version: String
        try {
            val packageName = packageName
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            version = packageInfo.versionName
            "ver.$version".also { binding.versionName.text = it }
        } catch (e: Exception){
            e.printStackTrace()
        }

        binding.nbButton.setOnClickListener {
            val nbIntent = Intent(this, NBCustomColor::class.java)
            startActivity(nbIntent)
        }

        binding.ftnButton.setOnClickListener {
            val ftnIntent = Intent(this, FTNCustomColor::class.java)
            startActivity(ftnIntent)
        }

        binding.slbButton.setOnClickListener {
            val slbIntent = Intent(this, SLBCustomColor::class.java)
            startActivity(slbIntent)
        }

        binding.plButton.setOnClickListener {
            val plIntent = Intent(this, PLCustomColor::class.java)
            startActivity(plIntent)
        }

        binding.pogoButton.setOnClickListener {
            val pogoIntent = Intent(this, POGOCustomColor::class.java)
            startActivity(pogoIntent)
        }

        binding.aButton.setOnClickListener{
            val aIntent = Intent(this, ACustomColor::class.java)
            startActivity(aIntent)
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
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://qiita.com/nokopi/private/610b9ee0ca4986d59b8d"))
        startActivity(intent)
    }

}