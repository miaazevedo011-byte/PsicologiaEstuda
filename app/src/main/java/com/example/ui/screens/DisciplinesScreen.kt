package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Discipline
import com.example.model.GlossaryTerm
import com.example.ui.theme.*
import com.example.viewmodel.AppTab
import com.example.viewmodel.PsicoUiState

@Composable
fun DisciplinesScreen(
    uiState: PsicoUiState,
    onSelectSemester: (Int) -> Unit,
    onToggleShowCompleted: (Boolean) -> Unit,
    onSelectGlossaryTerm: (GlossaryTerm) -> Unit,
    onOpenAddMaterial: () -> Unit,
    onOpenAddNote: (String, String) -> Unit,
    onNavigateTab: (AppTab) -> Unit,
    onPlayDisciplineAudio: (Discipline) -> Unit,
    onOpenAcademicCalendar: () -> Unit = {},
    onAdvanceModule: (String) -> Unit = {},
    onToggleObjective: (String, Int) -> Unit = { _, _ -> },
    onGenerateFlashcards: (Discipline) -> Unit = {}
) {
    var expandedDisciplineId by remember(uiState.selectedSemester) {
        mutableStateOf(uiState.disciplines.firstOrNull()?.id)
    }
    var glossarySearch by remember { mutableStateOf("") }

    val filteredGlossaryTerms = remember(glossarySearch, uiState.glossaryTerms) {
        if (glossarySearch.isBlank()) uiState.glossaryTerms
        else uiState.glossaryTerms.filter { it.term.contains(glossarySearch, ignoreCase = true) || it.definition.contains(glossarySearch, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("disciplines_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Grade Curricular Header
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
                    Column {
                        Text(
                            text = "Grade Curricular",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PinePrimary
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = SageSecondary, modifier = Modifier.size(14.dp))
                            Text(
                                text = "${uiState.selectedSemester}º Período • ${uiState.userProfile.institution}",
                                style = MaterialTheme.typography.bodySmall.copy(color = SageSecondary, fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SageSecondaryContainer,
                        modifier = Modifier.clickable { onOpenAcademicCalendar() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = OnSageSecondaryContainer, modifier = Modifier.size(16.dp))
                            Text("Planilha", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = OnSageSecondaryContainer))
                        }
                    }
                }

                // Semesters Horizontal Scroll (1º ao 10º)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items((1..10).toList()) { sem ->
                        val isSelected = uiState.selectedSemester == sem
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) PinePrimary else SurfaceContainerHigh,
                            modifier = Modifier.clickable { onSelectSemester(sem) }
                        ) {
                            Text(
                                text = "${sem}º Período",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else SageSecondary
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Status Toggle & Count
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        val isCompleted = uiState.showCompletedDisciplines
                        Button(
                            onClick = { onToggleShowCompleted(false) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isCompleted) SurfaceContainerLowest else Color.Transparent,
                                contentColor = if (!isCompleted) PinePrimary else SageSecondary
                            ),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text("Disciplinas do ${uiState.selectedSemester}º (${uiState.disciplines.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = { onOpenAcademicCalendar() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCompleted) SurfaceContainerLowest else Color.Transparent,
                                contentColor = if (isCompleted) PinePrimary else SageSecondary
                            ),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Provas & Feriados (${uiState.academicEvents.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // 2. Action: Adicionar Disciplina / Ementa
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAddMaterial() },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PinePrimaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = PinePrimaryFixed)
                        }
                        Column {
                            Text(
                                text = "Adicionar Disciplina / Ementa",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Extrair tópicos e cronograma via PDF com IA",
                                style = MaterialTheme.typography.labelSmall.copy(color = PinePrimaryFixed)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PinePrimaryFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // 3. Disciplinas List
        items(uiState.disciplines) { discipline ->
            val isExpanded = expandedDisciplineId == discipline.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("discipline_card_${discipline.id}"),
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
                    // Header row
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
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SageSecondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Healing, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(22.dp))
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = discipline.name,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PinePrimary
                                        )
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = SageSecondaryFixed
                                    ) {
                                        Text(
                                            text = "${discipline.progressPercent}%",
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
                                    text = "${discipline.professor} • ${discipline.code}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                expandedDisciplineId = if (isExpanded) null else discipline.id
                            }
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Expandir/Recolher",
                                tint = PinePrimary
                            )
                        }
                    }

                    // Progress bar
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        LinearProgressIndicator(
                            progress = { discipline.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = PinePrimary,
                            trackColor = SurfaceContainerHigh
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Módulo ${discipline.currentModule} de ${discipline.totalModules}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = SageSecondary)
                                )
                                if (discipline.currentModule < discipline.totalModules) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SageSecondaryContainer,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { onAdvanceModule(discipline.id) }
                                            .testTag("advance_module_${discipline.id}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = OnSageSecondaryContainer, modifier = Modifier.size(10.dp))
                                            Text(
                                                text = "Avançar Módulo",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = OnSageSecondaryContainer,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp
                                                )
                                            )
                                        }
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFE8F5E9)
                                    ) {
                                        Text(
                                            text = "✓ 100% Concluído",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF2E7D32),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${discipline.materialsCount} materiais • ${discipline.flashcardsCount} cards",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = SageSecondary)
                            )
                        }
                    }

                    // Expandable Details
                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Syllabus
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceContainerLow,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "EMENTA ATIVA",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SageSecondary
                                        )
                                    )
                                    Text(
                                        text = discipline.syllabusSummary,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextOnSurfaceVariant,
                                            lineHeight = 17.sp
                                        )
                                    )
                                }
                            }

                            // Learning Objectives (Clickable to mark as studied)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Objetivos de Aprendizagem (Clique para marcar)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PinePrimary
                                        )
                                    )
                                    Text(
                                        text = "${discipline.learningObjectives.count { it.second }}/${discipline.learningObjectives.size}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SageSecondary
                                        )
                                    )
                                }
                                discipline.learningObjectives.forEachIndexed { idx, (obj, isDone) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { onToggleObjective(discipline.id, idx) }
                                            .padding(vertical = 2.dp)
                                            .testTag("obj_${discipline.id}_$idx")
                                    ) {
                                        Icon(
                                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (isDone) PinePrimary else OutlineColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = obj,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 11.sp,
                                                color = if (isDone) TextOnSurface else TextOnSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }

                            // Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalButton(
                                    onClick = { onOpenAddNote("Anotação: ${discipline.name}", discipline.name) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = SurfaceContainerHigh)
                                ) {
                                    Icon(Icons.Default.EditNote, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Nota Rápida", fontSize = 11.sp, color = PinePrimary)
                                }

                                Button(
                                    onClick = { onGenerateFlashcards(discipline) },
                                    modifier = Modifier.weight(1f).testTag("generate_flashcards_${discipline.id}"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PeachTertiaryFixed, contentColor = OnPeachTertiaryFixed)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Flashcards IA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = { onPlayDisciplineAudio(discipline) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHighest, contentColor = PinePrimary)
                            ) {
                                Icon(Icons.Default.Headphones, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ouvir Resumo da Matéria em Áudio (${discipline.audioDurationMin} min)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // 4. Glossário Técnico Integrado da Psicologia
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PinePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "Glossário Técnico Integrado",
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
                            text = "DSM-5-TR & CID-11",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSageSecondaryContainer,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Glossary Search
                OutlinedTextField(
                    value = glossarySearch,
                    onValueChange = { glossarySearch = it },
                    placeholder = { Text("Buscar termos médicos e clínicos...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SageSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("glossary_search_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceContainerLowest,
                        unfocusedContainerColor = SurfaceContainerLowest,
                        focusedBorderColor = PinePrimary,
                        unfocusedBorderColor = OutlineVariant
                    ),
                    singleLine = true
                )

                // Quick Terms Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.glossaryTerms) { term ->
                        val isSelected = uiState.selectedGlossaryTerm.id == term.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectGlossaryTerm(term) },
                            label = { Text(term.term, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PinePrimary,
                                selectedLabelColor = OnPinePrimary
                            )
                        )
                    }
                }

                // Term Details Card
                val term = uiState.selectedGlossaryTerm
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("glossary_term_card"),
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
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = term.term,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PinePrimary
                                        )
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = SageSecondaryContainer
                                    ) {
                                        Text(
                                            text = term.category,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = OnSageSecondaryContainer,
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${term.phonetic} • ${term.wordClass}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SageSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            IconButton(onClick = { /* bookmark */ }) {
                                Icon(Icons.Default.Bookmark, contentDescription = "Salvar termo", tint = PinePrimary)
                            }
                        }

                        // Technical Definition
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceContainerLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "DEFINIÇÃO TÉCNICA",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PinePrimary,
                                        fontSize = 10.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = term.definition,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextOnSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }

                        // Clinical Differential Warning
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PeachTertiaryFixed.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = PeachTertiary, modifier = Modifier.size(18.dp))
                                Column {
                                    Text(
                                        text = "Diferencial Clínico Relevante",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PeachTertiary
                                        )
                                    )
                                    Text(
                                        text = term.clinicalDifferential,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = OnPeachTertiaryVariant,
                                            lineHeight = 16.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Linked Lesson Action
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SageSecondaryContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateTab(AppTab.DISCIPLINAS) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.PlayLesson, contentDescription = null, tint = PinePrimary)
                                    Column {
                                        Text(
                                            text = "AULA VINCULADA",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PinePrimary
                                            )
                                        )
                                        Text(
                                            text = term.linkedLesson,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextOnSurface
                                            )
                                        )
                                    }
                                }
                                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = PinePrimary)
                            }
                        }

                        // Polar Diagram DSM-5-TR
                        term.polarDiagram?.let { poles ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Correlação Diagnóstica DSM-5-TR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PinePrimary
                                    )
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    poles.forEach { (pole, status, desc) ->
                                        val isCenter = pole.contains("Central", ignoreCase = true)
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isCenter) SageSecondaryFixed else SurfaceContainerLow,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(8.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = pole,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 9.sp,
                                                        color = SageSecondary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                )
                                                Text(
                                                    text = status,
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = PinePrimary
                                                    )
                                                )
                                                Text(
                                                    text = desc,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 9.sp,
                                                        color = TextOnSurfaceVariant
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
