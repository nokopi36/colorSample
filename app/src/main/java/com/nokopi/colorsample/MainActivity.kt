package com.nokopi.colorsample

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.nokopi.colorsample.navigation.ColorSampleNavDisplay
import com.nokopi.colorsample.ui.theme.ColorSampleTheme

// 本文は docs/index.html。GitHub Pages が master の docs/ を公開している。
// ストア掲載欄と同じ URL を指すこと。
private const val PRIVACY_POLICY_URL = "https://nokopi36.github.io/colorSample/"

/**
 * アプリ唯一の Activity。画面遷移は Navigation 3 (NavDisplay) が持つ。
 */
class MainActivity : ComponentActivity() {

    private lateinit var appUpdateManager: AppUpdateManager

    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        when (result.resultCode) {
            // IMMEDIATE は更新が済むと Play がアプリを再起動するため、ここへはめったに来ない。
            RESULT_OK ->
                Toast.makeText(this, R.string.update_completed, Toast.LENGTH_SHORT).show()

            // 見送られただけ。ダウンロード済みなら onResume 側で再開を試みる。
            RESULT_CANCELED ->
                Log.i(TAG, "アプリ内アップデートがキャンセルされました")

            // ダウンロードやインストールの失敗。onResume の再開対象にはならない。
            ActivityResult.RESULT_IN_APP_UPDATE_FAILED ->
                Log.w(TAG, "アプリ内アップデートに失敗しました")

            else ->
                Log.w(TAG, "アプリ内アップデートが完了しませんでした: resultCode=${result.resultCode}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                info.isUpdateTypeAllowed(IMMEDIATE)
            ) {
                startImmediateUpdate(info)
            }
        }

        setContent {
            ColorSampleTheme {
                ColorSampleNavDisplay(
                    versionName = BuildConfig.VERSION_NAME,
                    onOpenPrivacyPolicy = ::openPrivacyPolicy,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() ==
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                // 中断されたアップデートを再開する。
                startImmediateUpdate(info)
            }
        }
    }

    private fun startImmediateUpdate(info: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            info,
            updateLauncher,
            AppUpdateOptions.newBuilder(IMMEDIATE).build(),
        )
    }

    private fun openPrivacyPolicy() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, PRIVACY_POLICY_URL.toUri()))
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "プライバシーポリシーを開けるアプリがありません", e)
        }
    }

    private companion object {
        const val TAG = "MainActivity"
    }
}
