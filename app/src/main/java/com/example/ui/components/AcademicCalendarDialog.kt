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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AcademicEvent
import com.example.model.EventType
import com.example.ui.theme.*

@Composable
fun AcademicCalendarDialog(
    isOpen: Boolean,
    events: List<AcademicEvent>,
    onDismiss: () -> Unit,
    onAddEvent: (title: String, type: EventType, date: String, time: String, location: String, discipline: String) -> Unit,
    onDeleteEvent: (String) -> Unit
) {
    if (!isOpen) return

    var selectedCategory by remember { mutableStateOf<EventType?>(null) }
    var isNewEventSheetOpen by remember { mutableStateOf(false) }

    var titleInput by remember { mutableStateOf("") }
    var disciplineInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf("") }
    var timeInput by remember { mutableStateOf("") }
    var locationInput by remember { mutableStateOf("") }
    var eventTypeInput by remember { mutableStateOf(EventType.PROVA) }

    val filteredEvents = remember(events, selectedCategory) {
        if (selectedCategory == null) events else events.filter { it.type == selectedCategory }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("academic_calendar_dialog"),
            color = SurfaceContainerLowest,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
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
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SageSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(24.dp))
                        }
                        Column {
                            Text(
                                text = "Cronograma & Planilha de Datas",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                            Text(
                                text = "Provas, feriados, entregas de projeto e estágios",
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

                // Filter chips row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Todas (${events.size})", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedCategory == EventType.PROVA,
                        onClick = { selectedCategory = if (selectedCategory == EventType.PROVA) null else EventType.PROVA },
                        label = { Text("📝 Provas", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedCategory == EventType.APRESENTACAO,
                        onClick = { selectedCategory = if (selectedCategory == EventType.APRESENTACAO) null else EventType.APRESENTACAO },
                        label = { Text("📦 Projetos/TCC", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedCategory == EventType.RELATORIO,
                        onClick = { selectedCategory = if (selectedCategory == EventType.RELATORIO) null else EventType.RELATORIO },
                        label = { Text("🏖️ Feriados/Outros", fontSize = 11.sp) }
                    )
                }

                // Table / List View
                if (filteredEvents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.EventAvailable, contentDescription = null, tint = SageSecondary, modifier = Modifier.size(48.dp))
                            Text(
                                text = "Nenhuma data cadastrada nesta categoria.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextOnSurfaceVariant)
                            )
                            Text(
                                text = "Toque no botão abaixo para adicionar sua primeira data de prova ou entrega.",
                                style = MaterialTheme.typography.bodySmall.copy(color = SageSecondary)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredEvents) { event ->
                            val (badgeBg, badgeFg, icon) = when (event.type) {
                                EventType.PROVA -> Triple(PeachTertiaryFixed, OnPeachTertiaryFixed, Icons.Default.AssignmentLate)
                                EventType.RELATORIO -> Triple(SageSecondaryContainer, OnSageSecondaryContainer, Icons.Default.Description)
                                EventType.APRESENTACAO -> Triple(PinePrimaryContainer, Color.White, Icons.Default.CoPresent)
                                EventType.TESTE -> Triple(PeachTertiaryFixed, OnPeachTertiaryFixed, Icons.Default.Quiz)
                                EventType.FERIADO -> Triple(SageSecondaryContainer, OnSageSecondaryContainer, Icons.Default.Event)
                                EventType.OUTRO -> Triple(SurfaceContainerHigh, TextOnSurface, Icons.Default.Event)
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
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
                                            .size(42.dp)
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
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                // Add Event Row / Form toggle
                if (!isNewEventSheetOpen) {
                    Button(
                        onClick = { isNewEventSheetOpen = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinePrimary)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Adicionar Nova Data de Prova / Feriado / Projeto", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Cadastrar Compromisso / Data",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PinePrimary)
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
                                    label = { Text("Data (ex: 15/11)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = timeInput,
                                    onValueChange = { timeInput = it },
                                    label = { Text("Horário / Prazo") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = locationInput,
                                    onValueChange = { locationInput = it },
                                    label = { Text("Local / Sala / Portal") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            // Event Type Selection
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
                                    onClick = { isNewEventSheetOpen = false },
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
                                            isNewEventSheetOpen = false
                                        }
                                    },
                                    modifier = Modifier.weight(1.5f),
                                    colors = ButtonDefaults.buttonColors(containerColor = PinePrimary)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Salvar na Planilha")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
