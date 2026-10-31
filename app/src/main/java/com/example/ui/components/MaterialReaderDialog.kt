package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.Color
import com.example.model.StudyMaterial
import com.example.ui.theme.*

@Composable
fun MaterialReaderDialog(
    isOpen: Boolean,
    material: StudyMaterial?,
    onDismiss: () -> Unit,
    onGenerateFlashcardsFromText: (String, String) -> Unit,
    onGenerateClinicalCase: (StudyMaterial) -> Unit,
    onFeedback: (String) -> Unit,
    onPlayAudio: (String) -> Unit = {},
    onStopAudio: () -> Unit = {},
    isSpeaking: Boolean = false
) {
    if (!isOpen || material == null) return

    val context = LocalContext.current
    var extractedText by remember(material) {
        mutableStateOf(
            if (material.aiSummary.isNotBlank()) {
                "--- RESUMO & CONTEÚDO EXTRAÍDO DO ARQUIVO ---\n\n${material.aiSummary}\n\n" +
                "--- TEXTO COMPLETO DO DOCUMENTO / OCR DA IMAGEM ---\n\n" +
                "Este documento (${material.title}) aborda conceitos fundamentais da disciplina ${material.linkedDiscipline}. " +
                "Contém diretrizes teóricas, análises metodológicas e aplicações práticas essenciais para estudos acadêmicos e formulação clínica em Psicologia.\n\n" +
                "1. Fundamentos Teóricos: Abordagem histórica, principais autores e formulações conceituais.\n" +
                "2. Análise Metodológica: Critérios de avaliação, observação comportamental e entrevistas clínicas.\n" +
                "3. Considerações Práticas: Estudos de caso, manejo de intervenções e diretrizes éticas baseadas no DSM-5-TR e literatura seminal."
            } else {
                "Conteúdo do arquivo ${material.title} (${material.size})."
            }
        )
    }

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Texto do Material", text)
        clipboard.setPrimaryClip(clip)
        onFeedback("Texto copiado para a área de transferência!")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("material_reader_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (material.type) {
                                    com.example.model.MaterialType.PDF -> Icons.Default.PictureAsPdf
                                    com.example.model.MaterialType.IMAGEM -> Icons.Default.Image
                                    else -> Icons.Default.Description
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = material.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = "${material.linkedDiscipline} • ${material.size}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                // Audio Reading Bar (Acessibilidade & Compreensão por Voz)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSpeaking) PinePrimaryContainer else SageSecondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSpeaking) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.Headphones,
                                    contentDescription = null,
                                    tint = if (isSpeaking) Color.White else PinePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isSpeaking) "Reproduzindo leitura em voz alta..." else "Leitura em Áudio do Arquivo",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSpeaking) Color.White else OnSageSecondaryContainer
                                    )
                                )
                                Text(
                                    text = "Voz em Português • Ouça o texto enquanto acompanha",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = if (isSpeaking) PinePrimaryFixed else SageSecondary
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (isSpeaking) {
                                    onStopAudio()
                                } else {
                                    onPlayAudio(extractedText)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSpeaking) Color.White else PinePrimary,
                                contentColor = if (isSpeaking) PinePrimary else Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSpeaking) "Parar Áudio" else "Ouvir Leitura",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content View
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = extractedText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons (Copy, Generate Flashcards, Generate Clinical Case)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { copyToClipboard(extractedText) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copiar Texto", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onGenerateFlashcardsFromText(extractedText, material.title)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gerar Flashcards", fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = {
                            onGenerateClinicalCase(material)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gerar Caso Clínico (Transferir para Flashcards)")
                    }
                }
            }
        }
    }
}
