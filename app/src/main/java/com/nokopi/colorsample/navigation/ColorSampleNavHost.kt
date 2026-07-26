package com.nokopi.colorsample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nokopi.colorsample.ui.device.DeviceColorScreen
import com.nokopi.colorsample.ui.home.HomeScreen

@Composable
fun ColorSampleNavHost(
    versionName: String,
    onOpenPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.HOME,
        modifier = modifier,
    ) {
        composable(Destinations.HOME) {
            HomeScreen(
                versionName = versionName,
                onSelectDevice = { navController.navigate(Destinations.device(it)) },
                onOpenPrivacyPolicy = onOpenPrivacyPolicy,
            )
        }

        composable(
            route = Destinations.DEVICE,
            arguments = listOf(navArgument(DEVICE_TYPE_ARG) { type = NavType.StringType }),
        ) {
            // 装具の種類はルート引数から DeviceColorViewModel が SavedStateHandle 経由で受け取る。
            DeviceColorScreen(onNavigateUp = { navController.popBackStack() })
        }
    }
}
