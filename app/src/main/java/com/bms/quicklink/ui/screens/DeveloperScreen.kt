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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.bms.quicklink.ui.theme.LocalCardStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val cardStyle = LocalCardStyle.current

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
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(if (cardStyle == "FILLED") 0.dp else 1.dp, cardBorder, RoundedCornerShape(32.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(28.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Anoy", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Arjun (Age 18)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

            // GitHub Handle
            item {
                SocialHandleCard(
                    title = "GitHub",
                    handle = "masterace572-prog",
                    icon = GitHubIcon,
                    iconTint = Color(0xFF24292E), // Real solid flat color
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    cardStyle = cardStyle,
                    onClick = { uriHandler.openUri("https://github.com/masterace572-prog") }
                )
            }

            // Instagram Handle
            item {
                SocialHandleCard(
                    title = "Instagram",
                    handle = "yt.android_gamer",
                    icon = InstagramIcon,
                    iconTint = Color(0xFFE1306C), // Real solid flat color
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    cardStyle = cardStyle,
                    onClick = { uriHandler.openUri("https://instagram.com/yt.android_gamer") }
                )
            }

            // Telegram Handle
            item {
                SocialHandleCard(
                    title = "Telegram",
                    handle = "@libAkAudioVisiual",
                    icon = TelegramIcon,
                    iconTint = Color(0xFF0088CC), // Real solid flat color
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    cardStyle = cardStyle,
                    onClick = { uriHandler.openUri("https://t.me/libAkAudioVisiual") }
                )
            }

            // WhatsApp Business Handle
            item {
                SocialHandleCard(
                    title = "WhatsApp Business",
                    handle = "+91 77078 87028",
                    icon = WhatsAppIcon,
                    iconTint = Color(0xFF25D366), // Real solid flat color
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    cardStyle = cardStyle,
                    onClick = { uriHandler.openUri("https://wa.me/917707887028") }
                )
            }

            // Email Handle
            item {
                SocialHandleCard(
                    title = "Email",
                    handle = "Saabarjun705@gmail.com",
                    icon = Icons.Default.Email,
                    iconTint = Color(0xFFD44638), // Real solid flat color
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    cardStyle = cardStyle,
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
    iconTint: Color,
    cardBg: Color,
    cardBorder: Color,
    cardStyle: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(if (cardStyle == "FILLED") 0.dp else 1.dp, cardBorder, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = handle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Open", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
    }
}

// Real Solid Vector App Icons (No Gradients)
private val GitHubIcon: ImageVector = ImageVector.Builder(
    name = "GitHub", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f
).apply {
    addPath(
        pathData = PathData {
            parsePathString("M12 2C6.477 2 2 6.477 2 12c0 4.42 2.865 8.17 6.839 9.49.5.09.682-.217.682-.482 0-.237-.008-.866-.013-1.7-2.782.603-3.369-1.34-3.369-1.34-.454-1.156-1.11-1.464-1.11-1.464-.908-.62.069-.608.069-.608 1.003.07 1.53 1.03 1.53 1.03.892 1.529 2.341 1.087 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.11-4.555-4.943 0-1.091.39-1.984 1.029-2.683-.103-.253-.446-1.27.098-2.647 0 0 .84-.269 2.75 1.025A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.294 2.747-1.025 2.747-1.025.546 1.377.203 2.394.1 2.647.64.699 1.028 1.592 1.028 2.683 0 3.842-2.339 4.687-4.566 4.935.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12c0-5.523-4.477-10-10-10z")
        },
        fill = SolidColor(Color.Black)
    )
}.build()

private val InstagramIcon: ImageVector = ImageVector.Builder(
    name = "Instagram", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f
).apply {
    addPath(
        pathData = PathData {
            parsePathString("M12 2c2.717 0 3.056.01 4.122.06 1.065.05 1.79.217 2.428.465.66.254 1.216.598 1.772 1.153a4.908 4.908 0 011.153 1.772c.247.637.415 1.363.465 2.428.047 1.066.06 1.405.06 4.122 0 2.717-.01 3.056-.06 4.122-.05 1.065-.218 1.79-.465 2.428a4.883 4.883 0 01-1.153 1.772 4.915 4.915 0 01-1.772 1.153c-.637.247-1.363.415-2.428.465-1.066.047-1.405.06-4.122.06-2.717 0-3.056-.01-4.122-.06-1.065-.05-1.79-.218-2.428-.465a4.89 4.89 0 01-1.772-1.153 4.904 4.904 0 01-1.153-1.772c-.248-.637-.415-1.363-.465-2.428C2.013 15.056 2 14.717 2 12c0-2.717.01-3.056.06-4.122.05-1.065.217-1.79.465-2.428a4.88 4.88 0 011.153-1.772A4.897 4.897 0 015.45 2.525c.638-.248 1.363-.415 2.428-.465C8.944 2.013 9.283 2 12 2zm0 1.802c-2.67 0-2.987.01-4.042.059-.975.045-1.504.207-1.857.344-.467.182-.8.398-1.15.748-.35.35-.566.683-.748 1.15-.137.353-.3.882-.344 1.857-.048 1.055-.058 1.372-.058 4.042 0 2.67.01 2.987.058 4.042.045.975.207 1.504.344 1.857.182.466.399.8.748 1.15.35.35.683.566 1.15.748.353.137.882.3 1.857.344 1.055.048 1.372.058 4.042.058 2.67 0 2.987-.01 4.042-.058.975-.045 1.504-.207 1.857-.344.466-.182.8-.398 1.15-.748.35-.35.566-.683.748-1.15.137-.353.3-.882.344-1.857.048-1.055.058-1.372.058-4.042 0-2.67-.01-2.987-.058-4.042-.045-.975-.207-1.504-.344-1.857-.182-.467-.398-.8-.748-1.15-.35-.35-.683-.566-1.15-.748-.353-.137-.882-.3-1.857-.344-1.055-.048-1.372-.058-4.042-.058zm0 3.065a5.133 5.133 0 100 10.266 5.133 5.133 0 000-10.266zm0 8.464a3.331 3.331 0 110-6.662 3.331 3.331 0 010 6.662zm3.32-9.664a1.199 1.199 0 100 2.398 1.199 1.199 0 000-2.398z")
        },
        fill = SolidColor(Color.Black)
    )
}.build()

private val TelegramIcon: ImageVector = ImageVector.Builder(
    name = "Telegram", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f
).apply {
    addPath(
        pathData = PathData {
            parsePathString("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm4.64 6.8c-.15 1.58-.8 5.42-1.13 7.19-.14.75-.42 1-.68 1.03-.58.05-1.02-.38-1.58-.75-.88-.58-1.38-.94-2.23-1.5-.99-.65-.35-1.01.22-1.59.15-.15 2.71-2.48 2.76-2.69.01-.03.01-.14-.07-.18-.08-.04-.19-.02-.27 0-.12.02-2.01 1.27-5.69 3.76-.54.37-1.03.55-1.47.54-.48-.01-1.41-.27-2.1-.5-.85-.28-1.52-.43-1.47-.91.03-.25.37-.51 1.03-.78 4.04-1.76 6.74-2.92 8.09-3.48 3.85-1.6 4.65-1.88 5.17-1.89.11 0 .37.03.52.15.13.1.17.25.17.38 0 .14-.02.3-.04.42z")
        },
        fill = SolidColor(Color.Black)
    )
}.build()

private val WhatsAppIcon: ImageVector = ImageVector.Builder(
    name = "WhatsApp", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f
).apply {
    addPath(
        pathData = PathData {
            parsePathString("M12 2a10 10 0 00-8.54 15.22L2 22l4.89-1.43A10 10 0 1012 2zm5.36 14.17c-.22.62-1.28 1.15-1.77 1.23-.46.07-1.06.18-3.3-.75-2.73-1.13-4.52-3.92-4.66-4.1-.14-.18-1.11-1.48-1.11-2.82 0-1.35.7-2.05.96-2.31.25-.26.55-.33.73-.33.18 0 .36 0 .52.01.17.01.38-.06.59.43.27.63.73 1.79.8 1.94.07.15.12.33.02.53-.1.2-.15.32-.29.5-.15.17-.31.37-.44.5-.15.15-.3.32-.13.62.17.3 1.76 2.89 2.05 3.2.37.39.73.34.88.22.15-.12.3-.29.47-.48.15-.17.33-.14.49-.08.17.06 1.07.51 1.25.6.18.09.3.14.35.22.05.08.05.47-.17 1.09z")
        },
        fill = SolidColor(Color.Black)
    )
}.build()
