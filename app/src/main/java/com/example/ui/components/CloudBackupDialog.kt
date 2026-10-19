package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

@Composable
fun CloudBackupDialog(
    isOpen: Boolean,
    studentEmail: String,
    onDismiss: () -> Unit,
    onTriggerBackup: (String) -> Unit
) {
    if (!isOpen) return

    var selectedProvider by remember { mutableStateOf("gdrive") } // "gdrive" or "onedrive"
    var isBackingUp by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .testTag("cloud_backup_dialog"),
            color = SurfaceContainerLowest,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SageSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(24.dp))
                        }
                        Column {
                            Text(
                                text = "Backup na Nuvem (Drive / OneDrive)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                            Text(
                                text = "Sincronize arquivos, resumos e flashcards",
                                style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextOnSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SageSecondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Conta vinculada: ${if (studentEmail.isNotBlank()) studentEmail else "estudante@psicoestuda.ai"}. Selecione o provedor para exportar ou restaurar todos os seus dados de estudos com segurança.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSageSecondaryContainer,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }

                // Provider Selection Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedProvider = "gdrive" },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedProvider == "gdrive") PinePrimaryContainer else SurfaceContainerLow
                        ),
                        border = if (selectedProvider == "gdrive") androidx.compose.foundation.BorderStroke(2.dp, PinePrimary) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Cloud,
                                contentDescription = null,
                                tint = if (selectedProvider == "gdrive") Color.White else PinePrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Google Drive",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedProvider == "gdrive") Color.White else PinePrimary
                                )
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedProvider = "onedrive" },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedProvider == "onedrive") PinePrimaryContainer else SurfaceContainerLow
                        ),
                        border = if (selectedProvider == "onedrive") androidx.compose.foundation.BorderStroke(2.dp, PinePrimary) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.FolderShared,
                                contentDescription = null,
                                tint = if (selectedProvider == "onedrive") Color.White else PinePrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Microsoft OneDrive",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedProvider == "onedrive") Color.White else PinePrimary
                                )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            isBackingUp = true
                            onTriggerBackup(if (selectedProvider == "gdrive") "Google Drive" else "Microsoft OneDrive")
                            isBackingUp = false
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinePrimary)
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBackingUp) "Salvando..." else "Fazer Backup", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
