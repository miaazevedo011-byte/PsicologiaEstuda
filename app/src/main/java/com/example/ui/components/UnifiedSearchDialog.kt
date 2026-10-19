package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun UnifiedSearchDialog(
    isOpen: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    verbetes: List<Verbete>,
    disciplines: List<Discipline>,
    flashcards: List<Flashcard>,
    notes: List<Note>,
    audios: List<PodcastAudio>,
    materials: List<StudyMaterial>,
    authors: List<Author> = emptyList(),
    onSelectVerbete: (Verbete) -> Unit,
    onSelectDiscipline: (Discipline) -> Unit,
    onSelectFlashcard: (Flashcard) -> Unit,
    onSelectAudio: (PodcastAudio) -> Unit,
    onSelectAuthor: (Author) -> Unit = {}
) {
    if (!isOpen) return

    val cleanQuery = query.trim().lowercase()

    val filteredVerbetes = remember(cleanQuery, verbetes) {
        if (cleanQuery.isEmpty()) verbetes.take(2)
        else verbetes.filter { it.title.lowercase().contains(cleanQuery) || it.summary.lowercase().contains(cleanQuery) || it.authors.lowercase().contains(cleanQuery) }
    }

    val filteredDisciplines = remember(cleanQuery, disciplines) {
        if (cleanQuery.isEmpty()) disciplines.take(2)
        else disciplines.filter { it.name.lowercase().contains(cleanQuery) || it.syllabusSummary.lowercase().contains(cleanQuery) }
    }

    val filteredFlashcards = remember(cleanQuery, flashcards) {
        if (cleanQuery.isEmpty()) flashcards.take(2)
        else flashcards.filter { it.question.lowercase().contains(cleanQuery) || it.answer.lowercase().contains(cleanQuery) }
    }

    val filteredNotes = remember(cleanQuery, notes) {
        if (cleanQuery.isEmpty()) notes.take(1)
        else notes.filter { it.title.lowercase().contains(cleanQuery) || it.content.lowercase().contains(cleanQuery) }
    }

    val filteredAudios = remember(cleanQuery, audios) {
        if (cleanQuery.isEmpty()) audios.take(2)
        else audios.filter { it.episodeTitle.lowercase().contains(cleanQuery) || it.speakerAndDiscipline.lowercase().contains(cleanQuery) }
    }

    val filteredMaterials = remember(cleanQuery, materials) {
        if (cleanQuery.isEmpty()) materials.take(2)
        else materials.filter { it.title.lowercase().contains(cleanQuery) || it.aiSummary.lowercase().contains(cleanQuery) }
    }

    val filteredAuthors = remember(cleanQuery, authors) {
        if (cleanQuery.isEmpty()) authors.take(2)
        else authors.filter { it.name.lowercase().contains(cleanQuery) || it.school.lowercase().contains(cleanQuery) || it.bio.lowercase().contains(cleanQuery) }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("unified_search_dialog"),
            color = SurfaceCanvas,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Search Bar in Dialog
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        placeholder = { Text("Buscar em verbetes, PDFs, notas, áudios...", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = PinePrimary)
                        },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { onQueryChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Limpar busca", tint = SageSecondary)
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("unified_search_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceContainerLowest,
                            unfocusedContainerColor = SurfaceContainerLowest,
                            focusedBorderColor = PinePrimary,
                            unfocusedBorderColor = OutlineVariant
                        ),
                        singleLine = true
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextOnSurface)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (cleanQuery.isEmpty()) "Sugestões Unificadas" else "Resultados Simultâneos",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PinePrimary
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SageSecondaryContainer
                    ) {
                        Text(
                            text = "6 Fontes Conectadas",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSageSecondaryContainer,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Verbetes Results
                    if (filteredVerbetes.isNotEmpty()) {
                        item {
                            CategorySectionHeader("ENCICLOPÉDIA & VERBETES", Icons.Default.MenuBook)
                        }
                        items(filteredVerbetes) { verbete ->
                            SearchResultItem(
                                title = verbete.title,
                                subtitle = "${verbete.authors} • ${verbete.school}",
                                tag = "${verbete.flashcardsCount} Cards",
                                containerColor = SurfaceContainerLowest,
                                onClick = {
                                    onSelectVerbete(verbete)
                                    onDismiss()
                                }
                            )
                        }
                    }

                    // Autores Results
                    if (filteredAuthors.isNotEmpty()) {
                        item {
                            CategorySectionHeader("AUTORES & TEÓRICOS", Icons.Default.Person)
                        }
                        items(filteredAuthors) { author ->
                            SearchResultItem(
                                title = author.name,
                                subtitle = "${author.dates} • ${author.school}",
                                tag = "${author.verbetesCount} Verbetes",
                                containerColor = SurfaceContainerLowest,
                                onClick = {
                                    onSelectAuthor(author)
                                    onDismiss()
                                }
                            )
                        }
                    }

                    // Disciplinas Results
                    if (filteredDisciplines.isNotEmpty()) {
                        item {
                            CategorySectionHeader("DISCIPLINAS DA GRADE", Icons.Default.Psychology)
                        }
                        items(filteredDisciplines) { discipline ->
                            SearchResultItem(
                                title = discipline.name,
                                subtitle = "${discipline.professor} • Módulo ${discipline.currentModule}/${discipline.totalModules}",
                                tag = "${discipline.progressPercent}% Concluído",
                                containerColor = SurfaceContainerLowest,
                                onClick = {
                                    onSelectDiscipline(discipline)
                                    onDismiss()
                                }
                            )
                        }
                    }

                    // Flashcards Results
                    if (filteredFlashcards.isNotEmpty()) {
                        item {
                            CategorySectionHeader("FLASHCARDS DE REPETIÇÃO ESPAÇADA", Icons.Default.Style)
                        }
                        items(filteredFlashcards) { card ->
                            SearchResultItem(
                                title = card.question,
                                subtitle = card.answer,
                                tag = card.category,
                                containerColor = AmberFlashcardBg.copy(alpha = 0.5f),
                                onClick = {
                                    onSelectFlashcard(card)
                                    onDismiss()
                                }
                            )
                        }
                    }

                    // Audios / Podcasts Results
                    if (filteredAudios.isNotEmpty()) {
                        item {
                            CategorySectionHeader("PODCASTS & SÍNTESES EM ÁUDIO", Icons.Default.Headphones)
                        }
                        items(filteredAudios) { audio ->
                            SearchResultItem(
                                title = audio.episodeTitle,
                                subtitle = audio.speakerAndDiscipline,
                                tag = "${audio.totalDurationSeconds / 60} min",
                                containerColor = SurfaceContainerLowest,
                                onClick = {
                                    onSelectAudio(audio)
                                    onDismiss()
                                }
                            )
                        }
                    }

                    // Materiais Results
                    if (filteredMaterials.isNotEmpty()) {
                        item {
                            CategorySectionHeader("REPOSITÓRIO & ARQUIVOS", Icons.Default.Description)
                        }
                        items(filteredMaterials) { mat ->
                            SearchResultItem(
                                title = mat.title,
                                subtitle = mat.aiSummary,
                                tag = mat.size,
                                containerColor = SurfaceContainerLowest,
                                onClick = { onDismiss() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategorySectionHeader(label: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = SageSecondary, modifier = Modifier.size(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = SageSecondary,
                letterSpacing = 0.8.sp
            )
        )
    }
}

@Composable
private fun SearchResultItem(
    title: String,
    subtitle: String,
    tag: String,
    containerColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PinePrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextOnSurfaceVariant,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SurfaceContainerHigh
            ) {
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = PinePrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}
