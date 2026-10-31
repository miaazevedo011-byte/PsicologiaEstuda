package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
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
    val webAppUrl = "https://ais-pre-hqwu75bcs3egwem7l7aqns-607091693762.us-east1.run.app"

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        onCopied("Link web copiado para a área de transferência!")
    }

    fun shareViaSystem() {
        val shareMessage = buildString {
            append("🎓 PsicoEstuda AI — Plataforma Web de Estudos para Estudantes de Psicologia\n\n")
            append("🌐 Acesse pelo navegador em qualquer celular, tablet ou computador:\n")
            append("$webAppUrl\n\n")
            append("📱 Dica para iOS (iPhone/iPad): Abra no Safari, toque no ícone de Compartilhar e selecione 'Adicionar à Tela de Início' para usar em tela cheia!\n")
            append("🤖 Dica para Android: Acesse pelo Chrome, toque nos três pontos e selecione 'Adicionar à tela inicial' ou 'Instalar aplicativo'.\n\n")
            append("Contém: Enciclopédia, Grade Curricular, Casos Clínicos, Flashcards, Biblioteca e Leitores de Áudio integrados!")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Compartilhar PsicoEstuda AI via Web")
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
                                text = "Compartilhe o link web com colegas de turma",
                                style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                // Description
                Text(
                    text = "O PsicoEstuda AI funciona totalmente na web. Qualquer estudante pode acessar instantaneamente pelo navegador ou instalar como aplicativo na tela inicial do celular (Android e iOS).",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextOnSurfaceVariant,
                        lineHeight = 18.sp
                    )
                )

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
                                text = "Link de Acesso Web:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SageSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            IconButton(
                                onClick = { copyToClipboard(webAppUrl, "Link PsicoEstuda AI") },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copiar Link", tint = PinePrimary, modifier = Modifier.size(16.dp))
                            }
                        }

                        Text(
                            text = webAppUrl,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PinePrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Instructional Guide for Web / PWA installation
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
                            text = "💡 Como estudar e instalar pelo celular:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PinePrimary
                            )
                        )
                        Text(
                            text = "• Android (Google Chrome): Abra o link, toque no menu (três pontos) e selecione 'Adicionar à tela inicial'.\n• iOS (Safari): Abra o link, toque no botão 'Compartilhar' e selecione 'Adicionar à Tela de Início'.\n• Computador: Acesse diretamente por qualquer navegador web com todas as funções ativas.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PinePrimaryDark,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }

                // Action Buttons: Copy Link & Share Sheet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { copyToClipboard(webAppUrl, "Link PsicoEstuda AI") },
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
