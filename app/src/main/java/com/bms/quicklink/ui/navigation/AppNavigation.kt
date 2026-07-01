package com.bms.quicklink.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bms.quicklink.ui.BmsViewModel
import com.bms.quicklink.ui.screens.*

@Composable
fun AppNavigation(
    viewModel: BmsViewModel,
    hasPermissions: Boolean,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

    val startDestination = if (isOnboardingCompleted) "main" else "onboarding"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        modifier = modifier.fillMaxSize()
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    viewModel.onOnboardingCompleted()
                    navController.navigate("main") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                hasPermissions = hasPermissions,
                onRequestPermissions = onRequestPermissions,
                navController = navController
            )
        }
        composable("appearance") {
            AppearanceScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("developer") {
            DeveloperScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("terms") {
            TermsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("privacy") {
            PrivacyScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
