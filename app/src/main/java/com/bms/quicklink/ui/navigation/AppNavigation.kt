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
import com.bms.quicklink.auth.AuthState
import com.bms.quicklink.ui.BmsViewModel
import com.bms.quicklink.ui.screens.AuthScreen
import com.bms.quicklink.ui.screens.MainScreen

@Composable
fun AppNavigation(
    viewModel: BmsViewModel,
    hasPermissions: Boolean,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val authState by viewModel.authState.collectAsState()

    val startDestination = when (authState) {
        is AuthState.Authenticated -> "main"
        else -> "auth"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        modifier = modifier.fillMaxSize()
    ) {
        composable("auth") {
            AuthScreen(
                viewModel = viewModel,
                onAuthenticated = {
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                hasPermissions = hasPermissions,
                onRequestPermissions = onRequestPermissions,
                onLogout = {
                    viewModel.logout()
                    navController.navigate("auth") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}
