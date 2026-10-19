package com.example.ui.components

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.MaterialType
import com.example.ui.theme.*

@Composable
fun AddMaterialDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onIngestMaterial: (title: String, type: MaterialType, discipline: String, notesContext: String, uriString: String?, realSize: String?) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MaterialType.PDF) }
    var discipline by remember { mutableStateOf("Psicopatologia I") }
    var notesContext by remember { mutableStateOf("") }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedFileSize by remember { mutableStateOf<String?>(null) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    // Helper to query file metadata from ContentResolver
    fun handleFileUri(uri: Uri?) {
        if (uri == null) return
        selectedUri = uri
        var name: String? = null
        var sizeBytes: Long? = null

        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) name = cursor.getString(nameIndex)
                    if (sizeIndex != -1) sizeBytes = cursor.getLong(sizeIndex)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val resolvedName = name ?: uri.lastPathSegment ?: "arquivo_selecionado"
        selectedFileName = resolvedName

        val formattedSize = if (sizeBytes != null && sizeBytes!! > 0) {
            val kb = sizeBytes!! / 1024.0
            if (kb > 1024) "%.1f MB".format(kb / 1024.0) else "%.0f KB".format(kb)
        } else {
            "Tamanho do dispositivo"
        }
        selectedFileSize = formattedSize

        // Infer material type from filename extension or mime
        val lower = resolvedName.lowercase()
        selectedType = when {
            lower.endsWith(".pdf") -> MaterialType.PDF
            lower.endsWith(".doc") || lower.endsWith(".docx") -> MaterialType.DOCX
            lower.endsWith(".mp3") || lower.endsWith(".wav") || lower.endsWith(".m4a") || lower.endsWith(".aac") -> MaterialType.AUDIO
            lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp") -> MaterialType.IMAGEM
            else -> selectedType
        }

        // Auto-fill title if empty
        if (title.isBlank()) {
            val baseName = resolvedName.substringBeforeLast(".")
            title = baseName.replace("_", " ").replace("-", " ")
        }
    }

    // 1. Real Device Storage File Picker (PDF, DOCX, Audio, etc.)
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        handleFileUri(uri)
    }

    // 2. Real Android Photo Picker / Gallery (zero-permission)
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        handleFileUri(uri)
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
                .testTag("add_material_dialog"),
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PinePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Encaminhar Material Real",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                            Text(
                                text = "Acesse a galeria ou arquivos do seu dispositivo",
                                style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                // Two Direct Native Device Access Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Origem do Arquivo no Dispositivo:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PinePrimary
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Button 1: Gallery / Images
                        Button(
                            onClick = {
                                galleryPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pick_from_gallery_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PeachTertiaryFixed,
                                contentColor = OnPeachTertiaryFixed
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Abrir Galeria", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Button 2: Storage Document Picker (PDF, DOCX, Audio)
                        Button(
                            onClick = {
                                documentPickerLauncher.launch(
                                    arrayOf(
                                        "application/pdf",
                                        "audio/*",
                                        "application/msword",
                                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                        "text/plain",
                                        "*/*"
                                    )
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pick_from_storage_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SageSecondaryContainer,
                                contentColor = OnSageSecondaryContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Arquivos / PDFs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Display Selected Device File Status
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (selectedFileName != null) SurfaceContainerHigh else SurfaceContainerLow,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (selectedFileName != null) PinePrimary else OutlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedFileName != null) PinePrimary else SageSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selectedFileName != null) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                                contentDescription = null,
                                tint = if (selectedFileName != null) Color.White else PinePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedFileName ?: "Nenhum arquivo do dispositivo selecionado ainda",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (selectedFileName != null) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedFileName != null) PinePrimary else TextOnSurfaceVariant
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (selectedFileName != null) {
                                    "Origem: Dispositivo local • ${selectedFileSize ?: ""}"
                                } else {
                                    "Toque em 'Abrir Galeria' ou 'Arquivos / PDFs' acima"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SageSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        if (selectedFileName != null) {
                            IconButton(
                                onClick = {
                                    selectedFileName = null
                                    selectedFileSize = null
                                    selectedUri = null
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remover", tint = TextOnSurfaceVariant)
                            }
                        }
                    }
                }

                // File Type Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        MaterialType.PDF to "PDF",
                        MaterialType.DOCX to "DOCX",
                        MaterialType.AUDIO to "Áudio Aula",
                        MaterialType.IMAGEM to "Imagem",
                        MaterialType.LINK to "Link Web"
                    ).forEach { (type, label) ->
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedType = type },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PinePrimary,
                                selectedLabelColor = OnPinePrimary
                            )
                        )
                    }
                }

                // Title input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título / Nome do Material") },
                    placeholder = { Text("Ex: Relatório de Estágio ou Cap. 4 Semiologia") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("material_title_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Discipline
                OutlinedTextField(
                    value = discipline,
                    onValueChange = { discipline = it },
                    label = { Text("Disciplina relacionada") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Additional notes or prompt
                OutlinedTextField(
                    value = notesContext,
                    onValueChange = { notesContext = it },
                    label = { Text("Instruções de síntese / Observações (Opcional)") },
                    placeholder = { Text("Ex: Destacar critérios diagnósticos e pontos de supervisão...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = TextOnSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val finalTitle = title.ifBlank { selectedFileName ?: "Novo Material de Estudo" }
                            onIngestMaterial(
                                finalTitle,
                                selectedType,
                                discipline,
                                notesContext,
                                selectedUri?.toString(),
                                selectedFileSize
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PinePrimary,
                            contentColor = OnPinePrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("ingest_material_confirm_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Processar com IA", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
