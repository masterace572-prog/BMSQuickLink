package com.bms.quicklink.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bms.quicklink.auth.AuthState
import com.bms.quicklink.ui.BmsViewModel

@Composable
fun AuthScreen(
    viewModel: BmsViewModel,
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authState.collectAsState()
    val authErrorMessage by viewModel.authErrorMessage.collectAsState()
    val authIsLoading by viewModel.authIsLoading.collectAsState()

    var enteredPin by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState == AuthState.Authenticated) {
            onAuthenticated()
        }
    }

    LaunchedEffect(authErrorMessage) {
        if (authErrorMessage != null) {
            enteredPin = "" // Reset on error
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (authState == AuthState.SetupRequired) "Set Secure PIN" else "Enter Security PIN",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (authState == AuthState.SetupRequired) {
                    "Create a 4-to-6 digit PIN to secure hardware controls."
                } else {
                    "Enter your secure PIN to access BMS Quick Link."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            // PIN Dots Display
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 6) {
                    val isFilled = i < enteredPin.length
                    val isBorderOnly = i >= enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (isFilled) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .border(2.dp, if (isBorderOnly) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error Message
            AnimatedVisibility(visible = authErrorMessage != null) {
                Text(
                    text = authErrorMessage ?: "",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (authIsLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Keypad
            val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "DEL", "0", "OK")
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.width(280.dp)
            ) {
                items(keys) { key ->
                    KeypadButton(
                        key = key,
                        onClick = {
                            viewModel.clearAuthErrorMessage()
                            when (key) {
                                "DEL" -> if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                "OK" -> {
                                    if (authState == AuthState.SetupRequired) {
                                        viewModel.setupPin(enteredPin)
                                    } else {
                                        viewModel.verifyPin(enteredPin)
                                    }
                                }
                                else -> if (enteredPin.length < 6) enteredPin += key
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    key: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when (key) {
            "DEL" -> Icon(imageVector = Icons.Default.Backspace, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            "OK" -> Icon(imageVector = Icons.Default.Check, contentDescription = "Submit", tint = MaterialTheme.colorScheme.primary)
            else -> Text(
                text = key,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
