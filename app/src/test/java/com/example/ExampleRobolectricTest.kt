package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.CardMasteryStatus
import com.example.model.MaterialType
import com.example.viewmodel.AppTab
import com.example.viewmodel.PsicoEstudaViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PsicoEstuda AI", appName)
  }

  @Test
  fun `verify initial viewmodel state and tab navigation`() {
    val viewModel = PsicoEstudaViewModel()
    val initialState = viewModel.uiState.value
    assertEquals(AppTab.INICIO, initialState.currentTab)
    assertTrue(initialState.academicEvents.isNotEmpty())

    viewModel.selectTab(AppTab.ENCICLOPEDIA)
    assertEquals(AppTab.ENCICLOPEDIA, viewModel.uiState.value.currentTab)

    viewModel.selectTab(AppTab.DISCIPLINAS)
    assertEquals(AppTab.DISCIPLINAS, viewModel.uiState.value.currentTab)

    viewModel.selectTab(AppTab.FLASHCARDS)
    assertEquals(AppTab.FLASHCARDS, viewModel.uiState.value.currentTab)
  }

  @Test
  fun `verify student profile editing and dynamic semester progression`() {
    val viewModel = PsicoEstudaViewModel()
    
    // Change profile to a 2nd period student starting their own journey
    viewModel.updateStudentProfile(
      name = "Lucas Silva",
      course = "Psicologia",
      semesterNumber = 2,
      shift = "Noturno",
      weeklyTargetHours = 15f,
      resetStats = true
    )

    val updatedState = viewModel.uiState.value
    assertEquals("Lucas Silva", updatedState.userProfile.name)
    assertEquals("2º Período", updatedState.userProfile.semester)
    assertEquals(2, updatedState.userProfile.semesterNumber)
    assertEquals(2, updatedState.selectedSemester)
    assertEquals(0f, updatedState.userProfile.weeklyDedicatedHours, 0.01f)
    assertEquals(0, updatedState.userProfile.cardsMastered)
    assertEquals(0, updatedState.userProfile.verbetesRead)
  }

  @Test
  fun `verify flashcard interaction and SM2 rating advancing stats`() {
    val viewModel = PsicoEstudaViewModel()
    val initialMastered = viewModel.uiState.value.userProfile.cardsMastered
    
    viewModel.flipCard()
    assertTrue(viewModel.uiState.value.isCardFlipped)

    viewModel.rateCard(CardMasteryStatus.DOMINADO)
    assertEquals("Avaliação registrada: DOMINADO", viewModel.uiState.value.toastMessage)
    assertEquals(initialMastered + 1, viewModel.uiState.value.userProfile.cardsMastered)
  }

  @Test
  fun `verify note saving and hours progression`() {
    val viewModel = PsicoEstudaViewModel()
    val initialNotesCount = viewModel.uiState.value.notes.size
    val initialHours = viewModel.uiState.value.userProfile.weeklyDedicatedHours

    viewModel.saveNote(
      title = "Minha reflexão sobre ZDP",
      content = "Mediação semiótica em Vygotsky",
      topic = "ZDP",
      discipline = "Processos de Aprendizagem",
      tags = listOf("Vygotsky", "Estudo")
    )

    assertEquals(initialNotesCount + 1, viewModel.uiState.value.notes.size)
    assertEquals("Minha reflexão sobre ZDP", viewModel.uiState.value.notes.first().title)
    assertTrue(viewModel.uiState.value.userProfile.weeklyDedicatedHours > initialHours)
  }

  @Test
  fun `verify material ingestion with real device file metadata`() {
    val viewModel = PsicoEstudaViewModel()
    val initialMaterialsCount = viewModel.uiState.value.materials.size

    viewModel.ingestMaterial(
      title = "Artigo_Ansiedade_Clinica_SciELO.pdf",
      type = MaterialType.PDF,
      discipline = "Psicopatologia I",
      notesContext = "Foco em critérios do DSM-5-TR",
      uriString = "content://media/external/documents/1234",
      realSize = "4.2 MB"
    )

    val updatedState = viewModel.uiState.value
    assertEquals(initialMaterialsCount + 1, updatedState.materials.size)
    val added = updatedState.materials.first()
    assertEquals("Artigo_Ansiedade_Clinica_SciELO.pdf", added.title)
    assertEquals("4.2 MB", added.size)
    assertEquals("content://media/external/documents/1234", added.uriString)
  }
}
