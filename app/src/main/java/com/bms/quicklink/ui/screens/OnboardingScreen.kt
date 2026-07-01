package com.bms.quicklink.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableStateOf(0) }

    val slides = listOf(
        OnboardingSlide(
            title = "Elite BLE Quick Link",
            description = "Experience lightning-fast, ultra-reliable Bluetooth Low Energy connection specifically optimized for compatible LiFePO4 Battery Management Systems.",
            icon = Icons.Default.BluetoothConnected
        ),
        OnboardingSlide(
            title = "Safe Hardware Control",
            description = "Directly manage four core hardware functions including Charge and Discharge MOSFETs, Auto Balancing, and Heating pads with strict, verified confirmation dialogues.",
            icon = Icons.Default.Tune
        ),
        OnboardingSlide(
            title = "Absolute Offline Privacy",
            description = "BMS Quick Link & Control operates 100% offline with zero background tracking, zero analytics, and absolutely no internet permissions required.",
            icon = Icons.Default.Lock
        )
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(targetState = currentPage, label = "slide_animation") { page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Clean icon placement, no neon boxes
                    Icon(
                        imageVector = slides[page].icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = slides[page].title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = slides[page].description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                // Page Indicator Dots
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (i in slides.indices) {
                        val isSelected = i == currentPage
                        Box(
                            modifier = Modifier
                                .width(if (isSelected) 24.dp else 10.dp)
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (currentPage < slides.lastIndex) {
                            currentPage += 1
                        } else {
                            onFinish()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text(
                        text = if (currentPage == slides.lastIndex) "Get Started" else "Next",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private data class OnboardingSlide(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
