//package com.nokopi.colorsample;
//
//import static com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED;
//
//import android.content.Intent;
//import android.content.IntentSender;
//import android.content.res.Resources;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.material.snackbar.Snackbar;
//import com.google.android.play.core.appupdate.AppUpdateInfo;
//import com.google.android.play.core.appupdate.AppUpdateManager;
//import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
//import com.google.android.play.core.install.InstallStateUpdatedListener;
//import com.google.android.play.core.install.model.AppUpdateType;
//import com.google.android.play.core.install.model.InstallStatus;
//import com.google.android.play.core.install.model.UpdateAvailability;
//import com.google.android.play.core.tasks.OnSuccessListener;
//
//public class test2 extends AppCompatActivity {
//
//    private AppUpdateManager mAppUpdateManager;
//    private int RC_APP_UPDATE = 999;
//    private int inAppUpdateType;
//    private com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask;
//    private InstallStateUpdatedListener installStateUpdatedListener;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Creates instance of the manager.
//        mAppUpdateManager = AppUpdateManagerFactory.create(this);
//        // Returns an intent object that you use to check for an update.
//        appUpdateInfoTask = mAppUpdateManager.getAppUpdateInfo();
//        //lambda operation used for below listener
//        //For flexible update
//        installStateUpdatedListener = installState -> {
//            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
//                popupSnackbarForCompleteUpdate();
//            }
//        };
//        mAppUpdateManager.registerListener(installStateUpdatedListener);
//
//        //For Immediate
//        inAppUpdateType = AppUpdateType.IMMEDIATE; //1
//        inAppUpdate();
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        mAppUpdateManager.unregisterListener(installStateUpdatedListener);
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onResume() {
//        try {
//            mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
//                if (appUpdateInfo.updateAvailability() ==
//                        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
//                    // If an in-app update is already running, resume the update.
//                    try {
//                        mAppUpdateManager.startUpdateFlowForResult(
//                                appUpdateInfo,
//                                inAppUpdateType,
//                                this,
//                                RC_APP_UPDATE);
//                    } catch (IntentSender.SendIntentException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//
//            mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
//                //For flexible update
//                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
//                    popupSnackbarForCompleteUpdate();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        super.onResume();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_APP_UPDATE) {
//            //when user clicks update button
//            if (resultCode == RESULT_OK) {
//                Toast.makeText(MainActivity.this, "App download starts...", Toast.LENGTH_LONG).show();
//            } else if (resultCode != RESULT_CANCELED) {
//                //if you want to request the update again just call checkUpdate()
//                Toast.makeText(MainActivity.this, "App download canceled.", Toast.LENGTH_LONG).show();
//            } else if (resultCode == RESULT_IN_APP_UPDATE_FAILED) {
//                Toast.makeText(MainActivity.this, "App download failed.", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    private void inAppUpdate() {
//
//        try {
//            // Checks that the platform will allow the specified type of update.
//            appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
//                @Override
//                public void onSuccess(AppUpdateInfo appUpdateInfo) {
//                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                            // For a flexible update, use AppUpdateType.FLEXIBLE
//                            && appUpdateInfo.isUpdateTypeAllowed(inAppUpdateType)) {
//                        // Request the update.
//
//                        try {
//                            mAppUpdateManager.startUpdateFlowForResult(
//                                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
//                                    appUpdateInfo,
//                                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
//                                    inAppUpdateType,
//                                    // The current activity making the update request.
//                                    MainActivity.this,
//                                    // Include a request code to later monitor this update request.
//                                    RC_APP_UPDATE);
//                        } catch (IntentSender.SendIntentException ignored) {
//
//                        }
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void popupSnackbarForCompleteUpdate() {
//        try {
//            Snackbar snackbar =
//                    Snackbar.make(
//                            findViewById(R.id.id_of_root_loyout),
//                            "An update has just been downloaded.\nRestart to update",
//                            Snackbar.LENGTH_INDEFINITE);
//
//            snackbar.setAction("INSTALL", view -> {
//                if (mAppUpdateManager != null){
//                    mAppUpdateManager.completeUpdate();
//                }
//            });
//            snackbar.setActionTextColor(getResources().getColor(R.color.install_color));
//            snackbar.show();
//
//        } catch (Resources.NotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
