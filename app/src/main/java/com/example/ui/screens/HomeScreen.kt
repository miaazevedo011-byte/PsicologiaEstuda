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
import com.example.model.AcademicEvent
import com.example.model.EventType
import com.example.model.StudyTrack
import com.example.ui.theme.*
import com.example.viewmodel.AppTab
import com.example.viewmodel.PsicoUiState

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    uiState: PsicoUiState,
    onNavigateTab: (AppTab) -> Unit,
    onOpenAddEvent: () -> Unit,
    onOpenAddNote: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onOpenSocraticChat: () -> Unit,
    onOpenClinicalCases: () -> Unit = {},
    onOpenShareApp: () -> Unit = {},
    onOpenAcademicCalendar: () -> Unit = {},
    onOpenEmailLogin: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onToggleSuspendTrack: (String) -> Unit = {},
    onPlayTrackAudio: () -> Unit
) {
    var isNoticeDismissed by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Saudação e Perfil Resumido
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenEditProfile() }
                    .testTag("student_profile_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(SageSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (uiState.userProfile.name.firstOrNull()?.uppercase() ?: "E"),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Olá, ${uiState.userProfile.name}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextOnSurface
                                    )
                                )
                                Text(text = "👋", fontSize = 16.sp)
                            }
                            Text(
                                text = "${uiState.userProfile.semester} • ${uiState.userProfile.institution}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = PinePrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (uiState.studentEmail.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(12.dp))
                                    Text(
                                        text = uiState.studentEmail,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = PinePrimary,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SageSecondaryContainer,
                                    modifier = Modifier.clickable { onOpenEmailLogin() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = OnSageSecondaryContainer, modifier = Modifier.size(11.dp))
                                        Text("Vincular E-mail (Salvar Tudo)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = OnSageSecondaryContainer))
                                    }
                                }
                            }
                        }
                    }

                    FilledTonalIconButton(
                        onClick = onOpenAddNote,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Nota Rápida",
                            tint = PinePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 2. Painel de Progresso Semanal (Ritmo Acadêmico Dinâmico)
        item {
            val progressRatio = (uiState.userProfile.weeklyDedicatedHours / uiState.userProfile.weeklyTargetHours.coerceAtLeast(1f)).coerceIn(0f, 1f)
            val percent = (progressRatio * 100).toInt()
            val remainingHours = (uiState.userProfile.weeklyTargetHours - uiState.userProfile.weeklyDedicatedHours).coerceAtLeast(0f)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("academic_rhythm_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                            Icon(
                                imageVector = Icons.Default.Insights,
                                contentDescription = null,
                                tint = PinePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Ritmo Acadêmico",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextOnSurface
                                )
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SageSecondaryContainer
                        ) {
                            Text(
                                text = "Semana Ativa",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OnSageSecondaryContainer,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Circular progress ring representation
                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainerLowest),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$percent%",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PinePrimary
                                        )
                                    )
                                    Text(
                                        text = "da meta",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            color = TextOnSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }

                        // Hours and explanation
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "Tempo dedicado",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextOnSurfaceVariant)
                                )
                                Text(
                                    text = "%.1fh / %dh".format(uiState.userProfile.weeklyDedicatedHours, uiState.userProfile.weeklyTargetHours.toInt()),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PinePrimary
                                    )
                                )
                            }
                            LinearProgressIndicator(
                                progress = { progressRatio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = PinePrimary,
                                trackColor = SurfaceContainerHigh
                            )
                            Text(
                                text = if (remainingHours > 0) {
                                    "Faltam %.1fh para completar a sua meta semanal.".format(remainingHours)
                                } else {
                                    "Parabéns! Meta semanal de estudos atingida."
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SageSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // 3 Metric Counters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBox("${uiState.userProfile.verbetesRead}", "Verbetes lidos", Modifier.weight(1f))
                        MetricBox("${uiState.userProfile.cardsMastered}", "Cards fixados", Modifier.weight(1f))
                        MetricBox("${uiState.userProfile.audioClassesListened}", "Aulas áudio", Modifier.weight(1f))
                    }
                }
            }
        }

        // 3. Alerta Acadêmico / Conteúdo Pendente
        if (!isNoticeDismissed) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PeachTertiaryFixed)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = OnPeachTertiaryFixed,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "AVISO ACADÊMICO • NEUROANATOMIA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnPeachTertiaryFixed
                                )
                            )
                        }
                        Text(
                            text = "O artigo complementar 'Circuitos Neurais do Medo e Afeto' está indisponível na biblioteca física.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnPeachTertiaryFixed,
                                lineHeight = 18.sp
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { isNoticeDismissed = true }) {
                                Text("Dispensar", color = OnPeachTertiaryVariant, style = MaterialTheme.typography.labelSmall)
                            }
                            Button(
                                onClick = { onNavigateTab(AppTab.MATERIAIS) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PeachTertiaryContainer,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sintetizar com IA", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        // Card de Casos Clínicos & Exercícios Práticos
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenClinicalCases() }
                    .testTag("clinical_cases_banner_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PinePrimaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AssignmentLate,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Casos Clínicos & Exercícios",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PeachTertiaryFixed
                            ) {
                                Text(
                                    text = "${uiState.clinicalCases.size} casos",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = OnPeachTertiaryFixed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Treine hipóteses DSM-5-TR, anamnese e condutas a partir dos materiais anexados.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = PinePrimaryFixed,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                    FilledTonalButton(
                        onClick = onOpenClinicalCases,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.White,
                            contentColor = PinePrimary
                        )
                    ) {
                        Text("Resolver", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // 4. Trilhas em Andamento (Design System 4 Cards)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val track1 = uiState.studyTracks.find { it.id == "track_1" } ?: uiState.studyTracks.getOrNull(0)
                val track2 = uiState.studyTracks.find { it.id == "track_2" } ?: uiState.studyTracks.getOrNull(1)
                val track3 = uiState.studyTracks.find { it.id == "track_3" } ?: uiState.studyTracks.getOrNull(2)
                val track4 = uiState.studyTracks.find { it.id == "track_4" } ?: uiState.studyTracks.getOrNull(3)

                val activeCount = uiState.studyTracks.count { !it.isSuspended }
                val suspendedCount = uiState.studyTracks.count { it.isSuspended }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Trilhas em Andamento",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PinePrimary
                        )
                    )
                    Text(
                        text = "$activeCount ativas • $suspendedCount suspensas",
                        style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                    )
                }

                // Card 1: Destaque Verde Escuro (Psicanálise Freudiana)
                val isTrack1Suspended = track1?.isSuspended == true
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { if (!isTrack1Suspended) onNavigateTab(AppTab.ENCICLOPEDIA) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isTrack1Suspended) SurfaceContainerHigh else PinePrimaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (isTrack1Suspended) "⏸️ TEMA SUSPENSO" else "ESTUDO INTELIGENTE IA",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isTrack1Suspended) OnPeachTertiaryVariant else PinePrimaryFixed
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = track1?.title ?: "Síntese de Psicanálise Freudiana",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTrack1Suspended) TextOnSurface else Color.White
                                    )
                                )
                                Text(
                                    text = if (isTrack1Suspended) "Tema suspenso temporariamente da sua trilha de carreira." else (track1?.subtitle ?: "Conceitos fundamentais da Primeira Tópica e Mecanismos de Defesa."),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isTrack1Suspended) TextOnSurfaceVariant else Color.White.copy(alpha = 0.85f),
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isTrack1Suspended) SurfaceContainerLow else Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = if (isTrack1Suspended) SageSecondary else PinePrimaryFixed
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { onToggleSuspendTrack("track_1") },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (isTrack1Suspended) PinePrimary else Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isTrack1Suspended) Icons.Default.PlayCircle else Icons.Default.PauseCircleOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isTrack1Suspended) "Reativar Tema" else "Suspender Tema", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            if (!isTrack1Suspended) {
                                Button(
                                    onClick = { onNavigateTab(AppTab.ENCICLOPEDIA) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = PinePrimaryContainer
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text("Continuar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                // Grid: 2 Cards (Simulado Clínico + Assistente Socrático)
                val isTrack2Suspended = track2?.isSuspended == true
                val isTrack3Suspended = track3?.isSuspended == true
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 2: Simulado Clínico
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { if (!isTrack2Suspended) onNavigateTab(AppTab.DISCIPLINAS) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isTrack2Suspended) SurfaceContainerHigh else SageSecondaryFixed
                        )
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
                                Text(
                                    text = if (isTrack2Suspended) "⏸️ SUSPENSO" else "SIMULADO CLÍNICO",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTrack2Suspended) OnPeachTertiaryVariant else SageSecondary
                                    )
                                )
                                IconButton(
                                    onClick = { onToggleSuspendTrack("track_2") },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isTrack2Suspended) Icons.Default.PlayCircle else Icons.Default.PauseCircleOutline,
                                        contentDescription = "Suspender/Reativar",
                                        tint = SageSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = track2?.title ?: "Psicopatologia I",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextOnSurface
                                )
                            )
                            Text(
                                text = if (isTrack2Suspended) "Tema suspenso da trilha." else (track2?.subtitle ?: "15 questões DSM-5-TR sobre Transtornos de Humor."),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextOnSurfaceVariant,
                                    fontSize = 11.sp
                                ),
                                maxLines = 2
                            )
                            Button(
                                onClick = {
                                    if (isTrack2Suspended) onToggleSuspendTrack("track_2")
                                    else onNavigateTab(AppTab.DISCIPLINAS)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SageSecondary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(if (isTrack2Suspended) "Reativar" else "Iniciar", fontSize = 11.sp)
                            }
                        }
                    }

                    // Card 3: Diálogo Clínico Socrático
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { if (!isTrack3Suspended) onOpenSocraticChat() },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isTrack3Suspended) SurfaceContainerHigh else PeachTertiaryFixed
                        )
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
                                Text(
                                    text = if (isTrack3Suspended) "⏸️ SUSPENSO" else "DIÁLOGO CLÍNICO",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnPeachTertiaryVariant
                                    )
                                )
                                IconButton(
                                    onClick = { onToggleSuspendTrack("track_3") },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isTrack3Suspended) Icons.Default.PlayCircle else Icons.Default.PauseCircleOutline,
                                        contentDescription = "Suspender/Reativar",
                                        tint = PeachTertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = track3?.title ?: "Assistente Socrático",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnPeachTertiaryFixed
                                )
                            )
                            Text(
                                text = if (isTrack3Suspended) "Tema suspenso da trilha." else (track3?.subtitle ?: "Pratique formulações de casos com supervisão reflexiva."),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnPeachTertiaryVariant,
                                    fontSize = 11.sp
                                ),
                                maxLines = 2
                            )
                            Button(
                                onClick = {
                                    if (isTrack3Suspended) onToggleSuspendTrack("track_3")
                                    else onOpenSocraticChat()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PeachTertiary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(if (isTrack3Suspended) "Reativar" else "Conversar", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Card 4: Flashcards do Dia (Behaviorismo Radical)
                val isTrack4Suspended = track4?.isSuspended == true
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { if (!isTrack4Suspended) onNavigateTab(AppTab.FLASHCARDS) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isTrack4Suspended) SurfaceContainerHigh else AmberFlashcardBg
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isTrack4Suspended) SurfaceContainerLow else AmberFlashcardText.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Style, contentDescription = null, tint = if (isTrack4Suspended) SageSecondary else AmberFlashcardText)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = track4?.title ?: "Flashcards: Behaviorismo",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isTrack4Suspended) TextOnSurface else AmberFlashcardText
                                        )
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isTrack4Suspended) SurfaceContainerLow else AmberFlashcardText
                                    ) {
                                        Text(
                                            text = if (isTrack4Suspended) "Suspenso" else "18 pendentes",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isTrack4Suspended) SageSecondary else AmberFlashcardBg,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isTrack4Suspended) "Tema suspenso temporariamente." else (track4?.subtitle ?: "Esquemas de Reforço, Extinção e Punição"),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isTrack4Suspended) TextOnSurfaceVariant else AmberFlashcardMuted,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        IconButton(
                            onClick = { onToggleSuspendTrack("track_4") },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isTrack4Suspended) SageSecondaryContainer else AmberFlashcardText)
                        ) {
                            Icon(
                                imageVector = if (isTrack4Suspended) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (isTrack4Suspended) "Reativar" else "Suspender",
                                tint = if (isTrack4Suspended) PinePrimary else AmberFlashcardBg
                            )
                        }
                    }
                }
            }
        }

        // 5. Agenda e Planejamento Acadêmico (Data Presente do Celular)
        item {
            val ptBrLocale = remember { Locale("pt", "BR") }
            val todayCalendar = remember { Calendar.getInstance() }
            val todayDayOfYear = todayCalendar.get(Calendar.DAY_OF_YEAR)
            val todayYear = todayCalendar.get(Calendar.YEAR)

            val fullTodayDateFormatted = remember {
                val sdf = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", ptBrLocale)
                sdf.format(todayCalendar.time).replaceFirstChar { it.uppercase() }
            }

            // Tira horizontal semanal de 7 dias ancorada no sistema do celular
            val weekDays = remember {
                val list = mutableListOf<Triple<String, String, Boolean>>()
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_MONTH, -3)
                val dayFormat = SimpleDateFormat("EEE", ptBrLocale)
                val numFormat = SimpleDateFormat("dd", ptBrLocale)
                for (i in 0..6) {
                    val isToday = cal.get(Calendar.DAY_OF_YEAR) == todayDayOfYear && cal.get(Calendar.YEAR) == todayYear
                    val dayName = dayFormat.format(cal.time).replace(".", "").take(3).uppercase()
                    val dayNum = numFormat.format(cal.time)
                    list.add(Triple(dayName, dayNum, isToday))
                    cal.add(Calendar.DAY_OF_MONTH, 1)
                }
                list
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("academic_agenda_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PinePrimary)
                                Text(
                                    text = "Agenda Acadêmica",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextOnSurface
                                    )
                                )
                            }
                            Text(
                                text = "Hoje: $fullTodayDateFormatted",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = PinePrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SageSecondaryContainer,
                                modifier = Modifier.clickable { onOpenAcademicCalendar() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = OnSageSecondaryContainer, modifier = Modifier.size(14.dp))
                                    Text("Planilha", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = OnSageSecondaryContainer))
                                }
                            }
                            IconButton(
                                onClick = onOpenAddEvent,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.AddCircle, contentDescription = "Adicionar Compromisso", tint = PinePrimary)
                            }
                        }
                    }

                    // Tira horizontal semanal de 7 dias baseada no dia atual do dispositivo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weekDays.forEach { (day, num, isToday) ->
                            Column(
                                modifier = Modifier
                                    .width(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isToday) PinePrimary else SurfaceContainerLow)
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isToday) PinePrimaryFixed else TextOnSurfaceVariant
                                    )
                                )
                                Text(
                                    text = num,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isToday) Color.White else TextOnSurface
                                    )
                                )
                            }
                        }
                    }

                    // Lista de Próximos Eventos
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (uiState.academicEvents.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceContainerLow,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenAcademicCalendar() }
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "📅 Nenhuma prova ou entrega agendada",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = PinePrimary)
                                    )
                                    Text(
                                        text = "Toque em 'Planilha' para cadastrar datas de provas, feriados e entregas de TCC.",
                                        style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                                    )
                                }
                            }
                        } else {
                            uiState.academicEvents.forEach { event ->
                                AcademicEventCard(event)
                            }
                        }
                    }
                }
            }
        }

        // 6. Resumo Rápido: Última Anotação
        if (uiState.notes.isNotEmpty()) {
            item {
                val latestNote = uiState.notes.first()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenAddNote() },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.StickyNote2, contentDescription = null, tint = PinePrimary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Última anotação",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextOnSurfaceVariant)
                                    )
                                    Text(
                                        text = "• ${latestNote.timeAgo}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                                    )
                                }
                                Text(
                                    text = latestNote.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextOnSurface
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = latestNote.content,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextOnSurfaceVariant,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        IconButton(onClick = onOpenAddNote) {
                            Icon(Icons.Default.OpenInNew, contentDescription = "Abrir Nota", tint = PinePrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBox(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = SurfaceContainerLow
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PinePrimary
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = TextOnSurfaceVariant
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AcademicEventCard(event: AcademicEvent) {
    val (icon, bgCol, textCol) = when (event.type) {
        EventType.PROVA -> Triple(Icons.Default.EditCalendar, ErrorContainerRed, ErrorRed)
        EventType.RELATORIO -> Triple(Icons.Default.Assignment, SageSecondaryContainer, OnSageSecondaryContainer)
        EventType.APRESENTACAO -> Triple(Icons.Default.Groups, PeachTertiaryFixed, OnPeachTertiaryFixed)
        EventType.TESTE -> Triple(Icons.Default.Quiz, SurfaceContainerHigh, PinePrimary)
        EventType.FERIADO -> Triple(Icons.Default.Event, SageSecondaryContainer, PinePrimary)
        EventType.OUTRO -> Triple(Icons.Default.CalendarToday, SurfaceContainerHigh, TextOnSurface)
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgCol),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = textCol, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = event.type.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = textCol
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = bgCol
                    ) {
                        Text(
                            text = "Em ${event.daysRemaining} dias",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textCol
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextOnSurface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${event.dateDisplay} • ${event.timeDisplay} • ${event.location}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = TextOnSurfaceVariant
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SageSecondary)
        }
    }
}
