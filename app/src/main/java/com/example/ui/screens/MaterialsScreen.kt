package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MaterialType
import com.example.model.Note
import com.example.model.StudyMaterial
import com.example.ui.theme.*
import com.example.viewmodel.MaterialsFilter
import com.example.viewmodel.PsicoUiState

@Composable
fun MaterialsScreen(
    uiState: PsicoUiState,
    onSelectFilter: (MaterialsFilter) -> Unit,
    onOpenAddMaterial: () -> Unit,
    onOpenAddNote: (String, String) -> Unit,
    onSelectMaterial: (StudyMaterial) -> Unit,
    onEditNote: (Note) -> Unit = {},
    onDeleteNote: (String) -> Unit = {},
    onPlayNoteAudio: (Note) -> Unit = {},
    onPlayMaterialAudio: (StudyMaterial) -> Unit = {},
    activeItemId: String? = null,
    isSpeaking: Boolean = false
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredMaterials = remember(searchQuery, uiState.materialsFilter, uiState.materials) {
        uiState.materials.filter { mat ->
            val matchesFilter = when (uiState.materialsFilter) {
                MaterialsFilter.TODOS -> true
                MaterialsFilter.PDFS -> mat.type == MaterialType.PDF || mat.type == MaterialType.DOCX || mat.type == MaterialType.IMAGEM
                MaterialsFilter.NOTAS -> false
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                mat.title.contains(searchQuery, ignoreCase = true) ||
                mat.linkedDiscipline.contains(searchQuery, ignoreCase = true) ||
                mat.aiSummary.contains(searchQuery, ignoreCase = true)
            }
            matchesFilter && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("materials_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header: Repositório Acadêmico Vivo
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "● REPOSITÓRIO ACADÊMICO VIVO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageSecondary,
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp
                            )
                        )
                        Text(
                            text = "Biblioteca & Materiais",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PinePrimary
                            )
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SageSecondaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, tint = OnSageSecondaryContainer, modifier = Modifier.size(14.dp))
                            Text(
                                text = "14.2 GB livres",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSageSecondaryContainer,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Unified Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Pesquisa unificada em Verbetes, PDFs...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SageSecondary) },
                    trailingIcon = { Icon(Icons.Default.Tune, contentDescription = null, tint = SageSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("materials_search_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceContainerLowest,
                        unfocusedContainerColor = SurfaceContainerLowest,
                        focusedBorderColor = PinePrimary,
                        unfocusedBorderColor = OutlineVariant
                    ),
                    singleLine = true
                )

                // Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        MaterialsFilter.TODOS to "Todos",
                        MaterialsFilter.PDFS to "PDFs & Artigos",
                        MaterialsFilter.NOTAS to "Notas"
                    ).forEach { (filter, label) ->
                        val isSelected = uiState.materialsFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectFilter(filter) },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PinePrimary,
                                selectedLabelColor = OnPinePrimary
                            )
                        )
                    }
                }
            }
        }

        // 2. Ingestão Cognitiva com IA (Upload / Anexo Interativo)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cognitive_ingestion_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PinePrimaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PinePrimaryFixed, modifier = Modifier.size(18.dp))
                            }
                            Text(
                                text = "Ingestão Cognitiva com IA",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Auto-indexação",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = PinePrimaryFixed,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Attach Drag-and-Drop Area (Triggers Device Storage & Gallery Picker)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, PinePrimaryFixed.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .clickable { onOpenAddMaterial() }
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                            }
                            Text(
                                text = "Acesse a Galeria ou Arquivos do seu Dispositivo",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "Toque aqui para encaminhar fotos, PDFs ou áudios reais",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = PinePrimaryFixed,
                                    fontSize = 11.sp
                                )
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "🖼️ Galeria de Fotos",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "📁 Arquivos / PDFs",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Processamento Recente Concluído
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PinePrimaryFixed, modifier = Modifier.size(16.dp))
                                Column {
                                    Text(
                                        text = if (uiState.materials.isNotEmpty()) "ÚLTIMO MATERIAL PROCESSADO" else "BIBLIOTECA VAZIA",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PinePrimaryFixed
                                        )
                                    )
                                    Text(
                                        text = uiState.materials.firstOrNull()?.title ?: "Nenhum arquivo anexado ainda",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            if (uiState.materials.isNotEmpty()) {
                                val latest = uiState.materials.first()
                                // 4 Pills of Extracted Content
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    IngestionStatPill("1 Resumo & Áudio", Modifier.weight(1f))
                                    IngestionStatPill("${latest.flashcardsCount} Flashcards Gerados", Modifier.weight(1f))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    IngestionStatPill(latest.linkedDiscipline, Modifier.weight(1f))
                                    IngestionStatPill(latest.size, Modifier.weight(1f))
                                }
                            } else {
                                Text(
                                    text = "Toque acima para anexar seus primeiros PDFs ou fotos. O app desenvolverá disciplinas, flashcards e leitura por voz a partir dos seus arquivos!",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Meus Materiais de Estudo (Listagem com Conexões Ativas)
        if (uiState.materialsFilter != MaterialsFilter.NOTAS) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.FolderShared, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Meus Materiais de Estudo",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PinePrimary
                            )
                        )
                    }
                    IconButton(onClick = onOpenAddMaterial) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = PinePrimary)
                    }
                }
            }

            if (filteredMaterials.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenAddMaterial() }
                            .testTag("empty_materials_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(SageSecondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(28.dp))
                            }
                            Text(
                                text = "Nenhum material anexado ainda",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                            Text(
                                text = "Anexe apostilas, PDFs de ementas ou fotos de anotações. Você poderá abrir qualquer arquivo para leitura por voz, e a IA sincronizará flashcards e casos práticos!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextOnSurfaceVariant,
                                    lineHeight = 18.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            )
                            Button(
                                onClick = onOpenAddMaterial,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PinePrimary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Anexar Primeiro Material")
                            }
                        }
                    }
                }
            } else {
                items(filteredMaterials) { material ->
                    val isPlaying = isSpeaking && activeItemId == material.id
                    StudyMaterialCard(
                        material = material,
                        isPlaying = isPlaying,
                        onPlayAudio = { onPlayMaterialAudio(material) },
                        onClick = { onSelectMaterial(material) }
                    )
                }
            }
        }

        // 5. Bloco de Notas Integrado
        if (uiState.materialsFilter == MaterialsFilter.TODOS || uiState.materialsFilter == MaterialsFilter.NOTAS) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.StickyNote2, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Blocos de Notas & Caderno Integrado",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PinePrimary
                            )
                        )
                    }
                    Button(
                        onClick = { onOpenAddNote("", "") },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SageSecondaryContainer, contentColor = OnSageSecondaryContainer),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nova Nota", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            items(uiState.notes) { note ->
                val isPlaying = isSpeaking && activeItemId == note.id
                NoteCard(
                    note = note,
                    isPlaying = isPlaying,
                    onPlayAudio = { onPlayNoteAudio(note) },
                    onEdit = { onEditNote(note) },
                    onDelete = { onDeleteNote(note.id) }
                )
            }
        }
    }
}

@Composable
private fun IngestionStatPill(text: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StudyMaterialCard(
    material: StudyMaterial,
    isPlaying: Boolean = false,
    onPlayAudio: () -> Unit = {},
    onClick: () -> Unit
) {
    val (icon, iconCol) = when (material.type) {
        MaterialType.PDF -> Icons.Default.PictureAsPdf to PinePrimary
        MaterialType.DOCX -> Icons.Default.Description to SageSecondary
        MaterialType.AUDIO -> Icons.Default.AudioFile to PeachTertiary
        MaterialType.LINK -> Icons.Default.Link to PinePrimary
        MaterialType.IMAGEM -> Icons.Default.Image to SageSecondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("material_card_${material.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = iconCol, modifier = Modifier.size(24.dp))
                    }
                    Column {
                        Text(
                            text = material.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PinePrimary
                            )
                        )
                        Text(
                            text = "${material.type.name} • ${material.size} • ${material.updatedAt}",
                            style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                        )
                    }
                }

                IconButton(
                    onClick = onPlayAudio,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) PeachTertiary else SageSecondaryContainer)
                        .testTag("play_material_${material.id}")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                        contentDescription = if (isPlaying) "Parar áudio" else "Ouvir resumo do material",
                        tint = if (isPlaying) OnPeachTertiary else PinePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Highlights and notes indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✎ ${material.highlightsCount} grifos ativos",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextOnSurfaceVariant)
                )
                Text(text = "•", color = SageSecondary)
                Text(
                    text = "📝 ${material.linkedNotesCount} notas vinculadas",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextOnSurfaceVariant)
                )
            }

            // AI summary quote
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = material.aiSummary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = TextOnSurfaceVariant,
                        lineHeight = 16.sp
                    ),
                    modifier = Modifier.padding(10.dp)
                )
            }

            // Audio listen action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onPlayAudio,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) PeachTertiary else SageSecondaryContainer,
                        contentColor = if (isPlaying) OnPeachTertiary else OnSageSecondaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.GraphicEq,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlaying) "Parar Áudio do Material" else "Ouvir Resumo por Voz (TTS)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "Toque para abrir leitor ➔",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SageSecondary,
                        fontSize = 10.sp
                    )
                )
            }

            // Conexão Ativa Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SageSecondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Hub, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(16.dp))
                        Column {
                            Text(
                                text = "CONEXÃO ATIVA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                            Text(
                                text = material.linkedDiscipline,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSageSecondaryContainer
                                )
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PinePrimary
                        ) {
                            Text(
                                text = "${material.flashcardsCount} Cards",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SurfaceContainerLowest
                        ) {
                            Text(
                                text = "${material.verbetesCount} Verbetes",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = PinePrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteCard(
    note: Note,
    isPlaying: Boolean = false,
    onPlayAudio: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("note_card_${note.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SageSecondaryContainer
                    ) {
                        Text(
                            text = note.discipline,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSageSecondaryContainer
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "• ${note.timeAgo}",
                        style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPlayAudio,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("play_note_${note.id}")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                            contentDescription = if (isPlaying) "Parar leitura" else "Ouvir anotação",
                            tint = if (isPlaying) PeachTertiary else PinePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp).testTag("edit_note_${note.id}")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar anotação", tint = PinePrimary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp).testTag("delete_note_${note.id}")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir anotação", tint = Color(0xFFC62828), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PinePrimary
                )
            )

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextOnSurfaceVariant,
                    lineHeight = 18.sp
                )
            )

            // TTS Play Action Button
            Button(
                onClick = onPlayAudio,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlaying) PeachTertiary else SageSecondaryContainer,
                    contentColor = if (isPlaying) OnPeachTertiary else OnSageSecondaryContainer
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("play_note_btn_${note.id}")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isPlaying) "Parar Leitura da Anotação" else "Ouvir Anotação por Voz (TTS)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Tags
            if (note.tags.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    note.tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SurfaceContainerLow
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = SageSecondary
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
