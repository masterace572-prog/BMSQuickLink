package com.bms.quicklink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.bms.quicklink.ui.theme.LocalCardStyle
import com.bms.quicklink.ui.theme.LocalCornerStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val cardStyle = LocalCardStyle.current
    val cornerStyle = LocalCornerStyle.current
    val cardRadius = when (cornerStyle) {
        "SHARP" -> 8.dp
        "SOFT" -> 28.dp
        else -> 20.dp
    }

    val cardBg = when (cardStyle) {
        "GLASS" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        "OUTLINED" -> Color.Transparent
        else -> MaterialTheme.colorScheme.surface
    }
    val cardBorder = if (cardStyle == "FILLED") Color.Transparent else MaterialTheme.colorScheme.outline

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Developer Profile", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                // Main Profile Card
                Card(
                    shape = RoundedCornerShape(cardRadius),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(if (cardStyle == "FILLED") 0.dp else 1.dp, cardBorder, RoundedCornerShape(cardRadius))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Anoy", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(text = "Arjun (Age 18)", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Creator & Lead Developer of BMS Quick Link & Control", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                        }
                    }
                }
            }

            item {
                Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))
            }

            item {
                Text(text = "Connect & Handles", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            }

            item {
                SocialHandleCard(
                    title = "GitHub",
                    handle = "masterace572-prog",
                    icon = Icons.Default.Code,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    cardStyle = cardStyle,
                    cardRadius = cardRadius,
                    onClick = { uriHandler.openUri("https://github.com/masterace572-prog") }
                )
            }

            item {
                SocialHandleCard(
                    title = "Instagram",
                    handle = "yt.android_gamer",
                    icon = Icons.Default.PhotoCamera,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    cardStyle = cardStyle,
                    cardRadius = cardRadius,
                    onClick = { uriHandler.openUri("https://instagram.com/yt.android_gamer") }
                )
            }

            item {
                SocialHandleCard(
                    title = "Telegram",
                    handle = "@libAkAudioVisiual",
                    icon = Icons.Default.Send,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    cardStyle = cardStyle,
                    cardRadius = cardRadius,
                    onClick = { uriHandler.openUri("https://t.me/libAkAudioVisiual") }
                )
            }

            item {
                SocialHandleCard(
                    title = "WhatsApp Business",
                    handle = "+91 77078 87028",
                    icon = Icons.Default.Chat,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    cardStyle = cardStyle,
                    cardRadius = cardRadius,
                    onClick = { uriHandler.openUri("https://wa.me/917707887028") }
                )
            }

            item {
                SocialHandleCard(
                    title = "Email",
                    handle = "Saabarjun705@gmail.com",
                    icon = Icons.Default.Email,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    cardStyle = cardStyle,
                    cardRadius = cardRadius,
                    onClick = { uriHandler.openUri("mailto:Saabarjun705@gmail.com") }
                )
            }

            item {
                Spacer(modifier = Modifier.height(110.dp))
            }
        }
    }
}

@Composable
private fun SocialHandleCard(
    title: String,
    handle: String,
    icon: ImageVector,
    cardBg: Color,
    cardBorder: Color,
    cardStyle: String,
    cardRadius: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(cardRadius),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(if (cardStyle == "FILLED") 0.dp else 1.dp, cardBorder, RoundedCornerShape(cardRadius))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = handle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Open", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp))
        }
    }
}
