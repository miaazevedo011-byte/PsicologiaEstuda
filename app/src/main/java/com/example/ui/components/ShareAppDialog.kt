package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

@Composable
fun ShareAppDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onCopied: (String) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    var selectedTarget by remember { mutableStateOf("web_ios") } // "web_ios" or "android_apk"

    // App URLs
    val webIosUrl = "https://ais-pre-hqwu75bcs3egwem7l7aqns-607091693762.us-east1.run.app"
    val androidDownloadUrl = "https://ais-pre-hqwu75bcs3egwem7l7aqns-607091693762.us-east1.run.app"

    val currentUrl = if (selectedTarget == "web_ios") webIosUrl else androidDownloadUrl

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        onCopied("Link copiado para a área de transferência!")
    }

    fun shareViaSystem() {
        val shareMessage = buildString {
            append("🎓 PsicoEstuda AI — Aplicativo de Estudos para Estudantes de Psicologia\n\n")
            if (selectedTarget == "web_ios") {
                append("📱 Versão Web (iPhone / iPad / iOS & Qualquer Dispositivo):\n")
                append("$webIosUrl\n\n")
                append("👉 Dica para iOS: Abra no Safari, toque no ícone de Compartilhar e selecione 'Adicionar à Tela de Início' para usar como aplicativo nativo!\n")
            } else {
                append("🤖 Download para Android (Instale no seu aparelho):\n")
                append("$androidDownloadUrl\n")
            }
            append("\nContém: Enciclopédia, Grade Curricular, Casos Clínicos, Flashcards, Biblioteca e Podcasts integrados!")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Compartilhar PsicoEstuda AI com colegas")
        context.startActivity(shareIntent)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .testTag("share_app_dialog"),
            color = SurfaceContainerLowest,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PinePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = "Compartilhar PsicoEstuda AI",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                            Text(
                                text = "Gere o link de acesso para outros estudantes",
                                style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                // Platform Selector (iOS / Web vs Android APK)
                Text(
                    text = "Escolha o formato do link para compartilhar:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Option 1: Web / iOS
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTarget = "web_ios" }
                            .testTag("share_option_web_ios"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTarget == "web_ios") PinePrimaryContainer else SurfaceContainerLow
                        ),
                        border = if (selectedTarget == "web_ios") androidx.compose.foundation.BorderStroke(2.dp, PinePrimary) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneIphone,
                                    contentDescription = null,
                                    tint = if (selectedTarget == "web_ios") Color.White else PinePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Versão Web / iOS",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedTarget == "web_ios") Color.White else PinePrimary
                                    )
                                )
                            }
                            Text(
                                text = "Para iPhone, iPad e Safari (funciona sem baixar APK)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (selectedTarget == "web_ios") PinePrimaryFixed else TextOnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Option 2: Android Device Download
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTarget = "android_apk" }
                            .testTag("share_option_android_apk"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTarget == "android_apk") PinePrimaryContainer else SurfaceContainerLow
                        ),
                        border = if (selectedTarget == "android_apk") androidx.compose.foundation.BorderStroke(2.dp, PinePrimary) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Android,
                                    contentDescription = null,
                                    tint = if (selectedTarget == "android_apk") Color.White else PinePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Android APK",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedTarget == "android_apk") Color.White else PinePrimary
                                    )
                                )
                            }
                            Text(
                                text = "Para baixar e instalar no próprio aparelho Android",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (selectedTarget == "android_apk") PinePrimaryFixed else TextOnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // Link Display Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainerHigh,
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedTarget == "web_ios") "Link da Versão Web (Plataforma iOS):" else "Link de Download do App (Android):",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SageSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            IconButton(
                                onClick = { copyToClipboard(currentUrl, "Link PsicoEstuda AI") },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copiar Link", tint = PinePrimary, modifier = Modifier.size(16.dp))
                            }
                        }

                        Text(
                            text = currentUrl,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PinePrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Instructional Guide for iOS users
                if (selectedTarget == "web_ios") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SageSecondaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "💡 Instruções para usuários de iPhone / iPad (iOS):",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                            Text(
                                text = "1. Abra o link gerado acima no navegador Safari do iPhone.\n2. Toque no botão 'Compartilhar' (ícone quadrado com seta).\n3. Selecione 'Adicionar à Tela de Início'.\n4. O PsicoEstuda AI funcionará em tela cheia como um app nativo no iOS!",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = PinePrimaryDark,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }

                // Action Buttons: Copy Link & Share Sheet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { copyToClipboard(currentUrl, "Link PsicoEstuda AI") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_link_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copiar Link", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { shareViaSystem() },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("share_system_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PinePrimary,
                            contentColor = OnPinePrimary
                        )
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Compartilhar Link", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
