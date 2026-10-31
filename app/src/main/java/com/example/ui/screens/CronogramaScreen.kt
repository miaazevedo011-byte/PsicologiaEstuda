package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AcademicEvent
import com.example.model.EventType
import com.example.ui.theme.*
import com.example.viewmodel.PsicoUiState

@Composable
fun CronogramaScreen(
    uiState: PsicoUiState,
    onAddEvent: (title: String, type: EventType, date: String, time: String, location: String, discipline: String) -> Unit,
    onDeleteEvent: (String) -> Unit,
    onPlayAudioSummary: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<EventType?>(null) }
    var isAddCardOpen by remember { mutableStateOf(false) }

    var titleInput by remember { mutableStateOf("") }
    var disciplineInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf("") }
    var timeInput by remember { mutableStateOf("") }
    var locationInput by remember { mutableStateOf("") }
    var eventTypeInput by remember { mutableStateOf(EventType.PROVA) }

    val filteredEvents = remember(uiState.academicEvents, selectedCategory) {
        if (selectedCategory == null) uiState.academicEvents else uiState.academicEvents.filter { it.type == selectedCategory }
    }

    val examsCount = remember(uiState.academicEvents) {
        uiState.academicEvents.count { it.type == EventType.PROVA || it.type == EventType.TESTE }
    }
    val projectsCount = remember(uiState.academicEvents) {
        uiState.academicEvents.count { it.type == EventType.APRESENTACAO || it.type == EventType.RELATORIO }
    }
    val holidaysCount = remember(uiState.academicEvents) {
        uiState.academicEvents.count { it.type == EventType.FERIADO || it.type == EventType.OUTRO }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("cronograma_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Hero Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cronograma_hero_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = PinePrimaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SageSecondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = PinePrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Cronograma Acadêmico",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnPinePrimaryContainer
                                    )
                                )
                                Text(
                                    text = "${uiState.userProfile.semester} • ${uiState.userProfile.shift}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SageSecondary
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = onPlayAudioSummary,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SageSecondaryContainer.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Ouvir resumo do cronograma",
                                tint = PinePrimary
                            )
                        }
                    }

                    // Stat Pills Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = PeachTertiaryFixed
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$examsCount",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnPeachTertiaryFixed
                                    )
                                )
                                Text(
                                    text = "Provas",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = OnPeachTertiaryFixed,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = SageSecondaryContainer
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$projectsCount",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSageSecondaryContainer
                                    )
                                )
                                Text(
                                    text = "Entregas/TCC",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = OnSageSecondaryContainer,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceContainerHigh
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$holidaysCount",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextOnSurface
                                    )
                                )
                                Text(
                                    text = "Recessos",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextOnSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("Todas (${uiState.academicEvents.size})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedCategory == EventType.PROVA,
                    onClick = { selectedCategory = if (selectedCategory == EventType.PROVA) null else EventType.PROVA },
                    label = { Text("📝 Provas", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedCategory == EventType.APRESENTACAO,
                    onClick = { selectedCategory = if (selectedCategory == EventType.APRESENTACAO) null else EventType.APRESENTACAO },
                    label = { Text("📦 Projetos", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedCategory == EventType.RELATORIO,
                    onClick = { selectedCategory = if (selectedCategory == EventType.RELATORIO) null else EventType.RELATORIO },
                    label = { Text("🏖️ Feriados", fontSize = 11.sp) }
                )
            }
        }

        // Add Event Action / Form Card
        item {
            if (!isAddCardOpen) {
                Button(
                    onClick = { isAddCardOpen = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_open_add_event"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PinePrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Adicionar Nova Data / Prova / Feriado", fontWeight = FontWeight.Bold)
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_add_event_form"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PinePrimary.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Cadastrar Compromisso no Cronograma",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PinePrimary
                            )
                        )

                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Título (ex: P1 de Psicopatologia, Feriado Tiradentes)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = disciplineInput,
                                onValueChange = { disciplineInput = it },
                                label = { Text("Disciplina / Categoria") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = dateInput,
                                onValueChange = { dateInput = it },
                                label = { Text("Data (ex: 20/11)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = timeInput,
                                onValueChange = { timeInput = it },
                                label = { Text("Horário (ex: 08:30)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = locationInput,
                                onValueChange = { locationInput = it },
                                label = { Text("Local / Sala") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        // Type selector chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = eventTypeInput == EventType.PROVA,
                                onClick = { eventTypeInput = EventType.PROVA },
                                label = { Text("Prova", fontSize = 10.sp) }
                            )
                            FilterChip(
                                selected = eventTypeInput == EventType.APRESENTACAO,
                                onClick = { eventTypeInput = EventType.APRESENTACAO },
                                label = { Text("Projeto/TCC", fontSize = 10.sp) }
                            )
                            FilterChip(
                                selected = eventTypeInput == EventType.RELATORIO,
                                onClick = { eventTypeInput = EventType.RELATORIO },
                                label = { Text("Relatório", fontSize = 10.sp) }
                            )
                            FilterChip(
                                selected = eventTypeInput == EventType.OUTRO,
                                onClick = { eventTypeInput = EventType.OUTRO },
                                label = { Text("Feriado", fontSize = 10.sp) }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { isAddCardOpen = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }
                            Button(
                                onClick = {
                                    if (titleInput.isNotBlank()) {
                                        onAddEvent(
                                            titleInput.trim(),
                                            eventTypeInput,
                                            dateInput.ifBlank { "A definir" },
                                            timeInput.ifBlank { "08:00" },
                                            locationInput.ifBlank { "Campus / Portal" },
                                            disciplineInput.ifBlank { "Geral" }
                                        )
                                        titleInput = ""
                                        disciplineInput = ""
                                        dateInput = ""
                                        timeInput = ""
                                        locationInput = ""
                                        isAddCardOpen = false
                                    }
                                },
                                modifier = Modifier.weight(1.4f),
                                colors = ButtonDefaults.buttonColors(containerColor = PinePrimary)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Salvar no Cronograma")
                            }
                        }
                    }
                }
            }
        }

        // Events List
        if (filteredEvents.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SageSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventAvailable,
                                contentDescription = null,
                                tint = PinePrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = "Nenhum compromisso cadastrado",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextOnSurface
                            )
                        )
                        Text(
                            text = "Adicione as datas de provas, entregas de relatórios de estágio e feriados para manter seu ritmo semestral em dia.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextOnSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                    }
                }
            }
        } else {
            items(filteredEvents, key = { it.id }) { event ->
                val (badgeBg, badgeFg, icon) = when (event.type) {
                    EventType.PROVA -> Triple(PeachTertiaryFixed, OnPeachTertiaryFixed, Icons.Default.AssignmentLate)
                    EventType.RELATORIO -> Triple(SageSecondaryContainer, OnSageSecondaryContainer, Icons.Default.Description)
                    EventType.APRESENTACAO -> Triple(PinePrimaryContainer, Color.White, Icons.Default.CoPresent)
                    EventType.TESTE -> Triple(PeachTertiaryFixed, OnPeachTertiaryFixed, Icons.Default.Quiz)
                    EventType.FERIADO -> Triple(SageSecondaryContainer, OnSageSecondaryContainer, Icons.Default.Event)
                    EventType.OUTRO -> Triple(SurfaceContainerHigh, TextOnSurface, Icons.Default.Event)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("event_item_${event.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(badgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = badgeFg, modifier = Modifier.size(22.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextOnSurface
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = badgeBg
                                ) {
                                    Text(
                                        text = event.type.name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = badgeFg,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "📅 ${event.dateDisplay} • ${event.timeDisplay}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = PinePrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            Text(
                                text = "📍 ${event.location} • ${event.discipline}",
                                style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                            )
                        }

                        IconButton(
                            onClick = { onDeleteEvent(event.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Excluir evento",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
