package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CurriculumData
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun EditProfileDialog(
    isOpen: Boolean,
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onSaveProfile: (
        name: String,
        institution: String,
        course: String,
        semesterNumber: Int,
        shift: String,
        weeklyTargetHours: Float,
        resetStats: Boolean
    ) -> Unit
) {
    if (!isOpen) return

    var name by remember(currentProfile.name) { mutableStateOf(currentProfile.name) }
    var institution by remember(currentProfile.institution) { mutableStateOf(currentProfile.institution) }
    var course by remember(currentProfile.course) { mutableStateOf(currentProfile.course) }
    var selectedSemesterNumber by remember(currentProfile.semesterNumber) { mutableStateOf(currentProfile.semesterNumber) }
    var selectedShift by remember(currentProfile.shift) { mutableStateOf(currentProfile.shift) }
    var weeklyTargetHoursText by remember(currentProfile.weeklyTargetHours) {
        mutableStateOf(currentProfile.weeklyTargetHours.toInt().toString())
    }
    var shouldResetStats by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .testTag("edit_profile_dialog"),
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
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SageSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = PinePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Personalizar Perfil & Instituição",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                            Text(
                                text = "Sincronize sua universidade e período acadêmico",
                                style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Estudante") },
                    placeholder = { Text("Ex: Seu nome ou apelido") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SageSecondary) }
                )

                // Institution / University Input (Requested by user)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = institution,
                        onValueChange = { institution = it },
                        label = { Text("Instituição de Ensino / Universidade") },
                        placeholder = { Text("Ex: USP, PUC, UNIP, UFMG, Estácio...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("institution_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = PinePrimary) }
                    )

                    // Popular university chips for fast one-tap selection
                    Text(
                        text = "Instituições sugeridas para sincronização rápida:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SageSecondary,
                            fontSize = 11.sp
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CurriculumData.popularInstitutions.take(4).forEach { inst ->
                            val shortName = inst.substringBefore(" -")
                            SuggestionChip(
                                onClick = { institution = inst },
                                label = { Text(shortName, fontSize = 10.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (institution.contains(shortName)) PinePrimaryContainer else SurfaceContainerLow,
                                    labelColor = if (institution.contains(shortName)) androidx.compose.ui.graphics.Color.White else PinePrimary
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CurriculumData.popularInstitutions.drop(4).take(4).forEach { inst ->
                            val shortName = inst.substringBefore(" -")
                            SuggestionChip(
                                onClick = { institution = inst },
                                label = { Text(shortName, fontSize = 10.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (institution.contains(shortName)) PinePrimaryContainer else SurfaceContainerLow,
                                    labelColor = if (institution.contains(shortName)) androidx.compose.ui.graphics.Color.White else PinePrimary
                                )
                            )
                        }
                    }

                    // Notice explaining automatic syllabus synchronization
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SageSecondaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Ao salvar, a Grade Curricular será automaticamente sincronizada com as matérias e professores da $institution para o ${selectedSemesterNumber}º período.",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = PinePrimary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // Course / Focus Area
                OutlinedTextField(
                    value = course,
                    onValueChange = { course = it },
                    label = { Text("Curso / Ênfase") },
                    placeholder = { Text("Ex: Psicologia, Psicologia Clínica...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_course_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Period / Semester Selection (1º ao 10º período)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Período / Fase Atual do Curso:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PinePrimary
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        (1..5).forEach { sem ->
                            val isSelected = selectedSemesterNumber == sem
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedSemesterNumber = sem },
                                label = { Text("${sem}º", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PinePrimary,
                                    selectedLabelColor = OnPinePrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        (6..10).forEach { sem ->
                            val isSelected = selectedSemesterNumber == sem
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedSemesterNumber = sem },
                                label = { Text("${sem}º", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PinePrimary,
                                    selectedLabelColor = OnPinePrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Shift Selection
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Turno:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PinePrimary
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Matutino", "Vespertino", "Noturno", "Integral").forEach { shift ->
                            val isSelected = selectedShift == shift
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedShift = shift },
                                label = { Text(shift, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PinePrimary,
                                    selectedLabelColor = OnPinePrimary
                                )
                            )
                        }
                    }
                }

                // Weekly Target Hours
                OutlinedTextField(
                    value = weeklyTargetHoursText,
                    onValueChange = { weeklyTargetHoursText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Meta de Estudos Semanal (Horas)") },
                    placeholder = { Text("Ex: 15 ou 20") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Option to reset study metrics to 0 (so student tracks from scratch)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Checkbox(
                            checked = shouldResetStats,
                            onCheckedChange = { shouldResetStats = it },
                            colors = CheckboxDefaults.colors(checkedColor = PinePrimary)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Iniciar histórico de estudos do zero",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextOnSurface
                                )
                            )
                            Text(
                                text = "Zera os contadores (horas, flashcards e verbetes) para registrar o progresso deste novo semestre.",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextOnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // Actions
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
                            val finalName = name.ifBlank { "Estudante" }
                            val finalInstitution = institution.ifBlank { "Universidade de São Paulo (USP)" }
                            val targetHours = weeklyTargetHoursText.toFloatOrNull() ?: 20f
                            onSaveProfile(
                                finalName,
                                finalInstitution,
                                course.ifBlank { "Psicologia" },
                                selectedSemesterNumber,
                                selectedShift,
                                targetHours,
                                shouldResetStats
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PinePrimary,
                            contentColor = OnPinePrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_profile_button")
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Salvar & Sincronizar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
