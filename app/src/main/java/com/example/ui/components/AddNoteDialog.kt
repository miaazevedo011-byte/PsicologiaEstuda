package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Note
import com.example.ui.theme.*

@Composable
fun AddNoteDialog(
    isOpen: Boolean,
    noteToEdit: Note? = null,
    initialTopic: String,
    initialDiscipline: String,
    onDismiss: () -> Unit,
    onSaveNote: (title: String, content: String, topic: String, discipline: String, tags: List<String>) -> Unit,
    onUpdateNote: (id: String, title: String, content: String, topic: String, discipline: String, tags: List<String>) -> Unit = { _, _, _, _, _, _ -> }
) {
    if (!isOpen) return

    val isEditing = noteToEdit != null
    var title by remember(noteToEdit) { mutableStateOf(noteToEdit?.title ?: "") }
    var topic by remember(noteToEdit, initialTopic) { mutableStateOf(noteToEdit?.linkedTopic ?: initialTopic) }
    var discipline by remember(noteToEdit, initialDiscipline) { mutableStateOf(noteToEdit?.discipline ?: initialDiscipline.ifEmpty { "Psicopatologia I" }) }
    var content by remember(noteToEdit) { mutableStateOf(noteToEdit?.content ?: "") }
    var tagInput by remember(noteToEdit) { mutableStateOf(noteToEdit?.tags?.joinToString(", ") ?: "Clínica, Revisão") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .testTag("add_note_dialog"),
            color = SurfaceContainerLowest,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = PinePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (isEditing) "Editar Anotação de Estudo" else "Nova Anotação de Estudo",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PinePrimary
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título da anotação") },
                    placeholder = { Text("Ex: Síntese da aula de Psicanálise...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_title_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Topic and Discipline
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Tema / Verbete") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = discipline,
                        onValueChange = { discipline = it },
                        label = { Text("Disciplina") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Content
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Conteúdo / Reflexão clínica") },
                    placeholder = { Text("Escreva suas anotações, correlações bibliográficas ou dúvidas para supervisão...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("note_content_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Tags
                OutlinedTextField(
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    label = { Text("Tags (separadas por vírgula)") },
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
                            val tagsList = tagInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            if (isEditing && noteToEdit != null) {
                                onUpdateNote(noteToEdit.id, title, content, topic, discipline, tagsList)
                            } else {
                                onSaveNote(title, content, topic, discipline, tagsList)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PinePrimary,
                            contentColor = OnPinePrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_note_confirm_button")
                    ) {
                        Text(if (isEditing) "Atualizar Anotação" else "Salvar no Caderno", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
