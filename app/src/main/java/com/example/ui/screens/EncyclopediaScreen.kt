package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.model.Author
import com.example.model.TimelineMilestone
import com.example.model.Verbete
import com.example.ui.theme.*
import com.example.viewmodel.EncyclopediaSubTab
import com.example.viewmodel.PsicoUiState

@Composable
fun EncyclopediaScreen(
    uiState: PsicoUiState,
    onSelectSubTab: (EncyclopediaSubTab) -> Unit,
    onPlayVerbeteAudio: (Verbete) -> Unit,
    onCreateNoteForVerbete: (Verbete) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onAuthorClick: (Author) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("Todos") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredVerbetes = remember(searchQuery, selectedFilter, uiState.verbetes) {
        uiState.verbetes.filter { verbete ->
            val matchesFilter = if (selectedFilter == "Todos") true else verbete.school.contains(selectedFilter, ignoreCase = true)
            val matchesSearch = if (searchQuery.isBlank()) true else {
                verbete.title.contains(searchQuery, ignoreCase = true) ||
                verbete.summary.contains(searchQuery, ignoreCase = true) ||
                verbete.authors.contains(searchQuery, ignoreCase = true)
            }
            matchesFilter && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("encyclopedia_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. View Switcher Tabs (Verbetes & Conceitos vs Linha do Tempo)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = SurfaceContainerHigh
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isConcepts = uiState.encyclopediaSubTab == EncyclopediaSubTab.CONCEITOS
                    Button(
                        onClick = { onSelectSubTab(EncyclopediaSubTab.CONCEITOS) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_concepts_button"),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isConcepts) PinePrimary else Color.Transparent,
                            contentColor = if (isConcepts) OnPinePrimary else TextOnSurfaceVariant
                        ),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verbetes & Conceitos", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { onSelectSubTab(EncyclopediaSubTab.LINHA_DO_TEMPO) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_timeline_button"),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isConcepts) PinePrimary else Color.Transparent,
                            contentColor = if (!isConcepts) OnPinePrimary else TextOnSurfaceVariant
                        ),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Linha do Tempo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // VIEW 1: CONCEITOS & AUTORES
        if (uiState.encyclopediaSubTab == EncyclopediaSubTab.CONCEITOS) {
            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar autor, conceito ou teoria...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = SageSecondary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("encyclopedia_search_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceContainerLowest,
                        unfocusedContainerColor = SurfaceContainerLowest,
                        focusedBorderColor = PinePrimary,
                        unfocusedBorderColor = OutlineVariant
                    ),
                    singleLine = true
                )
            }

            // Quick Category Filters
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf("Todos", "Psicanálise", "Sócio-Histórica", "Psicologia Social", "Behaviorismo", "Humanismo")
                    items(filters) { filter ->
                        val isSelected = selectedFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PinePrimary,
                                selectedLabelColor = OnPinePrimary
                            )
                        )
                    }
                }
            }

            // Autores em Destaque Carousel
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
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
                            Icon(Icons.Default.Star, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Autores em Destaque",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                        }
                        Text("Deslize para ver", style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary))
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.authors) { author ->
                            AuthorCard(author = author, onClick = { onAuthorClick(author) })
                        }
                    }
                }
            }

            // Verbetes Fundamentais Header
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
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Verbetes Fundamentais",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PinePrimary
                            )
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SageSecondaryContainer
                    ) {
                        Text(
                            text = "Curadoria Acadêmica",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OnSageSecondaryContainer,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Verbetes Cards
            items(filteredVerbetes) { verbete ->
                VerbeteCard(
                    verbete = verbete,
                    onPlayAudio = { onPlayVerbeteAudio(verbete) },
                    onCreateNote = { onCreateNoteForVerbete(verbete) },
                    onToggleBookmark = { onToggleBookmark(verbete.id) }
                )
            }
        } else {
            // VIEW 2: LINHA DO TEMPO DA PSICOLOGIA
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Linha do Tempo da Psicologia",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PinePrimary
                        )
                    )
                    Text(
                        text = "Evolução epistemológica, matrizes teóricas e intersecção com a grade do curso.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextOnSurfaceVariant)
                    )
                }
            }

            items(uiState.timeline) { milestone ->
                TimelineMilestoneCard(milestone)
            }
        }
    }
}

@Composable
private fun AuthorCard(
    author: Author,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() }
            .testTag("author_card_${author.id}"),
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(SageSecondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = author.initial,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PinePrimary
                        )
                    )
                }
                Column {
                    Text(
                        text = author.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PinePrimary
                        )
                    )
                    Text(
                        text = author.dates,
                        style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                    )
                    Text(
                        text = author.school,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = TextOnSurfaceVariant
                        )
                    )
                }
            }
            Text(
                text = author.bio,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = TextOnSurfaceVariant,
                    lineHeight = 16.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SurfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📖 ${author.verbetesCount} verbetes",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PinePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "🎧 ${author.audiosCount} áudios",
                        style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                    )
                }
            }
        }
    }
}

@Composable
private fun VerbeteCard(
    verbete: Verbete,
    onPlayAudio: () -> Unit,
    onCreateNote: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("verbete_card_${verbete.id}"),
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
            // Badges row
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
                        shape = RoundedCornerShape(12.dp),
                        color = SageSecondaryContainer
                    ) {
                        Text(
                            text = verbete.school,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSageSecondaryContainer
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerHigh
                    ) {
                        Text(
                            text = verbete.authors,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PinePrimary,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "🎴 ${verbete.flashcardsCount} Cards",
                    style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                )
            }

            // Title & summary
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = verbete.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PinePrimary
                    )
                )
                Text(
                    text = verbete.subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SageSecondary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = verbete.summary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextOnSurfaceVariant,
                        lineHeight = 18.sp
                    )
                )
            }

            // Micro-pills
            if (verbete.microPills.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    verbete.microPills.take(3).forEach { (title, subtitle) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceContainerLow,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PinePrimary,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 9.sp,
                                        color = TextOnSurfaceVariant,
                                        lineHeight = 12.sp
                                    ),
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onPlayAudio,
                    colors = ButtonDefaults.buttonColors(containerColor = PinePrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ouvir Áudio (${verbete.audioDuration})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                FilledTonalButton(
                    onClick = onCreateNote,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = SurfaceContainerHigh),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Anotar", fontSize = 11.sp, color = PinePrimary)
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerHigh)
                ) {
                    Icon(
                        imageVector = if (verbete.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Favoritar",
                        tint = if (verbete.isBookmarked) PinePrimary else TextOnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineMilestoneCard(milestone: TimelineMilestone) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Milestone Pin
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PinePrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = milestone.year.takeLast(2),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = milestone.year,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PinePrimary
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SageSecondaryContainer
                    ) {
                        Text(
                            text = milestone.badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSageSecondaryContainer,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                )
                Text(
                    text = milestone.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextOnSurfaceVariant,
                        lineHeight = 18.sp
                    )
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "📖 Obra-chave: ${milestone.keyWork}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextOnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = "🔗 Disciplina: ${milestone.linkedDiscipline}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PinePrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
