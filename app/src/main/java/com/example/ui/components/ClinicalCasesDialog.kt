package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ClinicalCase
import com.example.model.ClinicalQuestion
import com.example.model.StudyMaterial
import com.example.ui.theme.*

@Composable
fun ClinicalCasesDialog(
    isOpen: Boolean,
    cases: List<ClinicalCase>,
    availableMaterials: List<StudyMaterial>,
    onDismiss: () -> Unit,
    onAnswerQuestion: (caseId: String, questionId: String, selectedOptionIndex: Int) -> Unit,
    onGenerateCaseFromMaterial: (StudyMaterial) -> Unit
) {
    if (!isOpen) return

    var selectedCaseIndex by remember { mutableStateOf(0) }
    var isGenerateDialogShown by remember { mutableStateOf(false) }

    val currentCase = cases.getOrNull(selectedCaseIndex) ?: cases.firstOrNull()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("clinical_cases_dialog"),
            color = SurfaceContainerLowest,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SageSecondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AssignmentLate,
                                contentDescription = null,
                                tint = PinePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Casos Clínicos & Exercícios Práticos",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinePrimary
                                )
                            )
                            Text(
                                text = "Exercícios formativos baseados nos conteúdos anexados",
                                style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                // Cases Tab Selector
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(cases) { index, c ->
                        val isSelected = index == selectedCaseIndex
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCaseIndex = index },
                            label = { Text("Caso ${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PinePrimary,
                                selectedLabelColor = OnPinePrimary
                            )
                        )
                    }

                    // Button to generate from material
                    item {
                        FilledTonalButton(
                            onClick = { isGenerateDialogShown = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = PinePrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gerar de Material", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PinePrimary)
                        }
                    }
                }

                if (currentCase != null) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Case Banner
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = PinePrimaryContainer)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color.White.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = currentCase.sourceMaterialOrDiscipline,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = PinePrimaryFixed,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(
                                            text = currentCase.patientAge,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                    Text(
                                        text = currentCase.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        // Anamnese & Queixa Principal
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "Anamnese & Queixa Principal",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = PinePrimary
                                            )
                                        )
                                    }
                                    Text(
                                        text = currentCase.anamneseVignette,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextOnSurface,
                                            lineHeight = 18.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Exame do Estado Mental
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Psychology, contentDescription = null, tint = SageSecondary, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "Exame do Estado Mental (Semiologia)",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = PinePrimary
                                            )
                                        )
                                    }
                                    Text(
                                        text = currentCase.mentalStateExam,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextOnSurfaceVariant,
                                            lineHeight = 17.sp,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Section Title: Clinical Questions
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Exercícios Formativos do Caso:",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PinePrimary
                                    )
                                )
                            }
                        }

                        // Questions
                        itemsIndexed(currentCase.questions) { qIndex, question ->
                            ClinicalQuestionCard(
                                questionIndex = qIndex + 1,
                                question = question,
                                onSelectOption = { optIndex ->
                                    onAnswerQuestion(currentCase.id, question.id, optIndex)
                                }
                            )
                        }

                        // Synthesis Box
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SageSecondaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "🎯 Conduta Terapêutica Recomendada:",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PinePrimary
                                        )
                                    )
                                    Text(
                                        text = currentCase.therapeuticApproach,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = PinePrimaryDark,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Footer Pagination Controls (Anterior / Próximo)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            if (selectedCaseIndex > 0) selectedCaseIndex--
                        },
                        enabled = selectedCaseIndex > 0,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Caso Anterior", fontSize = 11.sp)
                    }

                    Text(
                        text = "${selectedCaseIndex + 1} de ${cases.size}",
                        style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary, fontWeight = FontWeight.Bold)
                    )

                    Button(
                        onClick = {
                            if (selectedCaseIndex < cases.size - 1) selectedCaseIndex++
                        },
                        enabled = selectedCaseIndex < cases.size - 1,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PinePrimary,
                            contentColor = OnPinePrimary
                        )
                    ) {
                        Text("Próximo Caso", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    // Modal to pick which material to generate a case from
    if (isGenerateDialogShown) {
        Dialog(onDismissRequest = { isGenerateDialogShown = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(20.dp)),
                color = SurfaceContainerLowest,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Gerar Caso Clínico a partir de Material Anexado",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PinePrimary
                        )
                    )
                    Text(
                        text = "A IA elaborará uma vinheta clínica realista e perguntas diagnósticas com base no texto do arquivo:",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextOnSurfaceVariant)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(availableMaterials) { mat ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onGenerateCaseFromMaterial(mat)
                                        isGenerateDialogShown = false
                                    },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = PinePrimary, modifier = Modifier.size(20.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(mat.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(mat.linkedDiscipline, style = MaterialTheme.typography.labelSmall.copy(color = SageSecondary, fontSize = 10.sp))
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { isGenerateDialogShown = false }) {
                            Text("Cancelar", color = TextOnSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClinicalQuestionCard(
    questionIndex: Int,
    question: ClinicalQuestion,
    onSelectOption: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Prompt
            Text(
                text = "$questionIndex. ${question.prompt}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface,
                    lineHeight = 20.sp
                )
            )

            // Options
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                question.options.forEachIndexed { optIndex, optionText ->
                    val isSelected = question.selectedOptionIndex == optIndex
                    val isAnswered = question.selectedOptionIndex != null
                    val isCorrect = optIndex == question.correctOptionIndex

                    val backgroundColor = when {
                        !isAnswered -> if (isSelected) PinePrimaryContainer else SurfaceContainerLow
                        isCorrect -> Color(0xFFE8F5E9) // soft green
                        isSelected -> Color(0xFFFFEBEE) // soft red
                        else -> SurfaceContainerLow
                    }

                    val borderColor = when {
                        !isAnswered -> if (isSelected) PinePrimary else OutlineVariant
                        isCorrect -> Color(0xFF2E7D32)
                        isSelected -> Color(0xFFC62828)
                        else -> OutlineVariant
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = backgroundColor,
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isAnswered) { onSelectOption(optIndex) }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            !isAnswered -> if (isSelected) PinePrimary else SurfaceContainerHigh
                                            isCorrect -> Color(0xFF2E7D32)
                                            isSelected -> Color(0xFFC62828)
                                            else -> SurfaceContainerHigh
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                val letter = ('A' + optIndex).toString()
                                Text(
                                    text = letter,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected || (isAnswered && isCorrect)) Color.White else TextOnSurface
                                    )
                                )
                            }

                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextOnSurface,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected || (isAnswered && isCorrect)) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Explanation Feedback when answered
            AnimatedVisibility(visible = question.selectedOptionIndex != null) {
                val isAnswerCorrect = question.selectedOptionIndex == question.correctOptionIndex
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isAnswerCorrect) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isAnswerCorrect) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isAnswerCorrect) Color(0xFF2E7D32) else Color(0xFFE65100),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isAnswerCorrect) "Correto! Justificativa Clínica:" else "Atenção Semiológica - Justificativa:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAnswerCorrect) Color(0xFF2E7D32) else Color(0xFFE65100)
                                )
                            )
                        }
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextOnSurface,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
