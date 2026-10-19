package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.PodcastAudio
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceCanvas
import com.example.util.SpeechManager
import com.example.viewmodel.AppTab
import com.example.viewmodel.PsicoEstudaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PsicoEstudaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            MyApplicationTheme(
                darkTheme = uiState.isDarkMode,
                fontStyle = uiState.appFontStyle,
                fontScale = uiState.appFontScale
            ) {
                PsicoEstudaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PsicoEstudaApp(viewModel: PsicoEstudaViewModel) {
    val context = LocalContext.current
    val speechManager = remember { SpeechManager(context) }

    DisposableEffect(Unit) {
        onDispose {
            speechManager.shutdown()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // TTS Voice Speech synchronization with Audio Player
    LaunchedEffect(uiState.isAudioPlaying, uiState.activeAudio) {
        if (uiState.isAudioPlaying && uiState.activeAudio != null) {
            speechManager.setSpeed(uiState.playbackSpeed)
            val text = "${uiState.activeAudio?.episodeTitle}. ${uiState.activeAudio?.speakerAndDiscipline}. Conteúdo educacional de Psicologia: Este módulo sintetiza os conceitos centrais e aplicações práticas necessárias para seu aprofundamento acadêmico."
            speechManager.speak(text)
        } else if (!uiState.isAudioPlaying) {
            speechManager.stop()
        }
    }

    // Handle Toast messages
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.dismissToast()
        }
    }

    // BackHandler for custom state-based navigation
    BackHandler(
        enabled = uiState.currentTab != AppTab.INICIO ||
                uiState.isSettingsDialogOpen ||
                uiState.isUnifiedSearchOpen ||
                uiState.isAddNoteDialogOpen ||
                uiState.isAddEventDialogOpen ||
                uiState.isAddMaterialDialogOpen ||
                uiState.isSocraticAssistantOpen ||
                uiState.isEditProfileDialogOpen ||
                uiState.isClinicalCasesDialogOpen ||
                uiState.isShareAppDialogOpen ||
                uiState.isSelectAudioDialogOpen ||
                uiState.isAuthorDetailDialogOpen ||
                uiState.isAcademicCalendarDialogOpen ||
                uiState.isEmailLoginDialogOpen
    ) {
        when {
            uiState.isSettingsDialogOpen -> viewModel.closeSettingsDialog()
            uiState.isAuthorDetailDialogOpen -> viewModel.closeAuthorDetailDialog()
            uiState.isAcademicCalendarDialogOpen -> viewModel.closeAcademicCalendarDialog()
            uiState.isEmailLoginDialogOpen -> viewModel.closeEmailLoginDialog()
            uiState.isClinicalCasesDialogOpen -> viewModel.closeClinicalCasesDialog()
            uiState.isShareAppDialogOpen -> viewModel.closeShareAppDialog()
            uiState.isSelectAudioDialogOpen -> viewModel.closeSelectAudioDialog()
            uiState.isEditProfileDialogOpen -> viewModel.closeEditProfileDialog()
            uiState.isUnifiedSearchOpen -> viewModel.closeUnifiedSearch()
            uiState.isAddNoteDialogOpen -> viewModel.closeAddNoteDialog()
            uiState.isAddEventDialogOpen -> viewModel.closeAddEventDialog()
            uiState.isAddMaterialDialogOpen -> viewModel.closeAddMaterialDialog()
            uiState.isSocraticAssistantOpen -> viewModel.closeSocraticChat()
            uiState.currentTab != AppTab.INICIO -> viewModel.selectTab(AppTab.INICIO)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            PsicoTopBar(
                subtitle = when (uiState.currentTab) {
                    AppTab.INICIO -> "Início"
                    AppTab.ENCICLOPEDIA -> "Enciclopédia"
                    AppTab.DISCIPLINAS -> "Disciplinas"
                    AppTab.FLASHCARDS -> "Flashcards"
                    AppTab.MATERIAIS -> "Biblioteca & Materiais"
                },
                studentName = uiState.userProfile.name,
                semesterText = uiState.userProfile.semester,
                onSearchClick = { viewModel.openUnifiedSearch() },
                onSettingsClick = { viewModel.openSettingsDialog() },
                onNotificationsClick = { viewModel.openAcademicCalendarDialog() },
                onShareClick = { viewModel.openShareAppDialog() },
                onProfileClick = { viewModel.openEditProfileDialog() }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                AudioPlayerBar(
                    audio = uiState.activeAudio,
                    isPlaying = uiState.isAudioPlaying,
                    currentPositionSeconds = uiState.playbackPositionSeconds,
                    playbackSpeed = uiState.playbackSpeed,
                    onTogglePlayPause = { viewModel.togglePlayPauseAudio() },
                    onSeekBy = { delta -> viewModel.seekAudioBy(delta) },
                    onCycleSpeed = {
                        viewModel.cycleSpeed()
                        speechManager.setSpeed(uiState.playbackSpeed)
                    },
                    onStop = {
                        viewModel.stopAudio()
                        speechManager.stop()
                    },
                    onNextTrack = { viewModel.nextAudio() },
                    onPreviousTrack = { viewModel.previousAudio() },
                    onChangeTrackClick = { viewModel.openSelectAudioDialog() },
                    onClose = {
                        viewModel.closeAudio()
                        speechManager.stop()
                    },
                    onCreateNoteFromAudio = { topic, disc ->
                        viewModel.openAddNoteDialog(initialTopic = topic, discipline = disc)
                    }
                )
                PsicoBottomBar(
                    currentTab = uiState.currentTab,
                    onTabSelected = { tab -> viewModel.selectTab(tab) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                AppTab.INICIO -> HomeScreen(
                    uiState = uiState,
                    onNavigateTab = { tab -> viewModel.selectTab(tab) },
                    onOpenAddEvent = { viewModel.openAddEventDialog() },
                    onOpenAddNote = { viewModel.openAddNoteDialog() },
                    onOpenEditProfile = { viewModel.openEditProfileDialog() },
                    onOpenSocraticChat = { viewModel.openSocraticChat() },
                    onOpenClinicalCases = { viewModel.openClinicalCasesDialog() },
                    onOpenShareApp = { viewModel.openShareAppDialog() },
                    onOpenAcademicCalendar = { viewModel.openAcademicCalendarDialog() },
                    onOpenEmailLogin = { viewModel.openEmailLoginDialog() },
                    onOpenSettings = { viewModel.openSettingsDialog() },
                    onToggleSuspendTrack = { trackId -> viewModel.toggleSuspendStudyTrack(trackId) },
                    onPlayTrackAudio = {
                        uiState.audios.firstOrNull()?.let { viewModel.playAudio(it) }
                    }
                )

                AppTab.ENCICLOPEDIA -> EncyclopediaScreen(
                    uiState = uiState,
                    onSelectSubTab = { subTab -> viewModel.selectEncyclopediaSubTab(subTab) },
                    onPlayVerbeteAudio = { verbete ->
                        val track = PodcastAudio(
                            id = "audio_${verbete.id}",
                            episodeTitle = "Resumo: ${verbete.title}",
                            speakerAndDiscipline = "${verbete.authors} • ${verbete.school}",
                            totalDurationSeconds = 480,
                            currentPositionSeconds = 0,
                            isPlaying = true,
                            speed = 1.0f,
                            tag = verbete.school
                        )
                        viewModel.playAudio(track)
                    },
                    onCreateNoteForVerbete = { verbete ->
                        viewModel.openAddNoteDialog(initialTopic = verbete.title, discipline = verbete.linkedDiscipline)
                    },
                    onToggleBookmark = { id -> viewModel.toggleBookmarkVerbete(id) },
                    onAuthorClick = { author ->
                        viewModel.openAuthorDetailDialog(author)
                    }
                )

                AppTab.DISCIPLINAS -> DisciplinesScreen(
                    uiState = uiState,
                    onSelectSemester = { sem -> viewModel.selectSemester(sem) },
                    onToggleShowCompleted = { completed -> viewModel.toggleCompletedDisciplines(completed) },
                    onSelectGlossaryTerm = { term -> viewModel.selectGlossaryTerm(term) },
                    onOpenAddMaterial = { viewModel.openAddMaterialDialog() },
                    onOpenAddNote = { topic, disc -> viewModel.openAddNoteDialog(topic, disc) },
                    onNavigateTab = { tab -> viewModel.selectTab(tab) },
                    onPlayDisciplineAudio = { disc ->
                        val track = PodcastAudio(
                            id = "audio_${disc.id}",
                            episodeTitle = "Resumo da Matéria: ${disc.name}",
                            speakerAndDiscipline = "${disc.professor} • Módulo ${disc.currentModule}",
                            totalDurationSeconds = disc.audioDurationMin * 60,
                            currentPositionSeconds = 0,
                            isPlaying = true,
                            speed = 1.0f,
                            tag = disc.code
                        )
                        viewModel.playAudio(track)
                    },
                    onOpenAcademicCalendar = { viewModel.openAcademicCalendarDialog() },
                    onAdvanceModule = { id -> viewModel.advanceDisciplineModule(id) },
                    onToggleObjective = { id, idx -> viewModel.toggleDisciplineObjective(id, idx) },
                    onGenerateFlashcards = { disc -> viewModel.generateFlashcardsForDiscipline(disc) }
                )

                AppTab.FLASHCARDS -> FlashcardsScreen(
                    uiState = uiState,
                    onSelectDeck = { deck -> viewModel.selectDeck(deck) },
                    onFlipCard = { viewModel.flipCard() },
                    onToggleHint = { viewModel.toggleHint() },
                    onRateCard = { status -> viewModel.rateCard(status) },
                    onNextCard = { viewModel.nextCard() },
                    onPreviousCard = { viewModel.previousCard() },
                    onOpenAddMaterial = { viewModel.openAddMaterialDialog() },
                    onOpenAllDecks = { viewModel.openAllDecksDialog() },
                    onPlayAudioReading = {
                        val card = uiState.flashcards.getOrNull(uiState.currentCardIndex)
                        if (card != null) {
                            val reading = if (uiState.isCardFlipped) {
                                "Pergunta: ${card.question}. Resposta: ${card.answer}."
                            } else {
                                "Pergunta do Cartão: ${card.question}. Dica: ${card.hint}."
                            }
                            speechManager.speak(reading)
                        } else {
                            speechManager.speak("Nenhum cartão selecionado no momento.")
                        }
                    },
                    onGenerateFromCustomText = { text, topic -> viewModel.generateFlashcardsFromCustomText(text, topic) }
                )

                AppTab.MATERIAIS -> MaterialsScreen(
                    uiState = uiState,
                    onSelectFilter = { filter -> viewModel.selectMaterialsFilter(filter) },
                    onOpenAddMaterial = { viewModel.openAddMaterialDialog() },
                    onOpenAddNote = { topic, disc -> viewModel.openAddNoteDialog(topic, disc) },
                    onPlayAudio = { audio -> viewModel.playAudio(audio) },
                    onEditNote = { note -> viewModel.openEditNoteDialog(note) },
                    onDeleteNote = { id -> viewModel.deleteNote(id) }
                )
            }
        }
    }

    // Modal Dialogs
    AuthorDetailDialog(
        author = uiState.selectedAuthor,
        isOpen = uiState.isAuthorDetailDialogOpen,
        onDismiss = { viewModel.closeAuthorDetailDialog() },
        onFilterVerbetesByAuthor = { authorName ->
            viewModel.openUnifiedSearch(authorName)
        }
    )

    AcademicCalendarDialog(
        isOpen = uiState.isAcademicCalendarDialogOpen,
        events = uiState.academicEvents,
        onDismiss = { viewModel.closeAcademicCalendarDialog() },
        onAddEvent = { title, type, date, time, location, disc ->
            viewModel.addAcademicEvent(title, type, date, time, location, disc)
        },
        onDeleteEvent = { id ->
            viewModel.deleteAcademicEvent(id)
        }
    )

    EmailLoginDialog(
        isOpen = uiState.isEmailLoginDialogOpen,
        currentEmail = uiState.studentEmail,
        onDismiss = { viewModel.closeEmailLoginDialog() },
        onSaveEmail = { email ->
            viewModel.saveStudentEmail(email)
        }
    )

    UnifiedSearchDialog(
        isOpen = uiState.isUnifiedSearchOpen,
        query = uiState.searchQuery,
        onQueryChange = { q -> viewModel.updateSearchQuery(q) },
        onDismiss = { viewModel.closeUnifiedSearch() },
        verbetes = uiState.verbetes,
        disciplines = uiState.disciplines,
        flashcards = uiState.flashcards,
        notes = uiState.notes,
        audios = uiState.audios,
        materials = uiState.materials,
        authors = uiState.authors,
        onSelectVerbete = { verbete ->
            viewModel.selectTab(AppTab.ENCICLOPEDIA)
            viewModel.selectEncyclopediaSubTab(com.example.viewmodel.EncyclopediaSubTab.CONCEITOS)
        },
        onSelectDiscipline = { disc ->
            viewModel.selectTab(AppTab.DISCIPLINAS)
        },
        onSelectFlashcard = { card ->
            viewModel.selectTab(AppTab.FLASHCARDS)
        },
        onSelectAudio = { audio ->
            viewModel.playAudio(audio)
        },
        onSelectAuthor = { author ->
            viewModel.openAuthorDetailDialog(author)
        }
    )

    AddNoteDialog(
        isOpen = uiState.isAddNoteDialogOpen,
        noteToEdit = uiState.noteToEdit,
        initialTopic = uiState.noteDraftTopic,
        initialDiscipline = uiState.noteDraftDiscipline,
        onDismiss = { viewModel.closeAddNoteDialog() },
        onSaveNote = { title, content, topic, disc, tags ->
            viewModel.saveNote(title, content, topic, disc, tags)
        },
        onUpdateNote = { id, title, content, topic, disc, tags ->
            viewModel.updateNote(id, title, content, topic, disc, tags)
        }
    )

    AddEventDialog(
        isOpen = uiState.isAddEventDialogOpen,
        onDismiss = { viewModel.closeAddEventDialog() },
        onAddEvent = { title, type, date, time, location, disc ->
            viewModel.addAcademicEvent(title, type, date, time, location, disc)
        }
    )

    AddMaterialDialog(
        isOpen = uiState.isAddMaterialDialogOpen,
        onDismiss = { viewModel.closeAddMaterialDialog() },
        onIngestMaterial = { title, type, disc, notes, uri, realSize ->
            viewModel.ingestMaterial(title, type, disc, notes, uri, realSize ?: "2.4 MB")
        }
    )

    EditProfileDialog(
        isOpen = uiState.isEditProfileDialogOpen,
        currentProfile = uiState.userProfile,
        onDismiss = { viewModel.closeEditProfileDialog() },
        onSaveProfile = { name, institution, course, sem, shift, target, reset ->
            viewModel.updateStudentProfile(name, institution, course, sem, shift, target, reset)
        }
    )

    SelectAudioDialog(
        isOpen = uiState.isSelectAudioDialogOpen,
        audios = uiState.audios,
        currentAudioId = uiState.activeAudio?.id,
        onDismiss = { viewModel.closeSelectAudioDialog() },
        onSelectAudio = { audio -> viewModel.playAudio(audio) }
    )

    ClinicalCasesDialog(
        isOpen = uiState.isClinicalCasesDialogOpen,
        cases = uiState.clinicalCases,
        availableMaterials = uiState.materials,
        onDismiss = { viewModel.closeClinicalCasesDialog() },
        onAnswerQuestion = { caseId, questionId, optIndex ->
            viewModel.answerClinicalQuestion(caseId, questionId, optIndex)
        },
        onGenerateCaseFromMaterial = { material ->
            viewModel.generateClinicalCaseFromMaterial(material)
        }
    )

    ShareAppDialog(
        isOpen = uiState.isShareAppDialogOpen,
        onDismiss = { viewModel.closeShareAppDialog() },
        onCopied = { msg ->
            // Feedback provided within dialog and toast
        }
    )

    AllDecksDialog(
        isOpen = uiState.isAllDecksDialogOpen,
        decks = uiState.decks,
        activeDeckId = uiState.activeDeck.id,
        onDismiss = { viewModel.closeAllDecksDialog() },
        onSelectDeck = { deck -> viewModel.selectDeck(deck) }
    )

    SocraticAssistantDialog(
        isOpen = uiState.isSocraticAssistantOpen,
        messages = uiState.socraticChatMessages,
        onSendMessage = { msg -> viewModel.sendSocraticMessage(msg) },
        onDismiss = { viewModel.closeSocraticChat() }
    )

    AppSettingsDialog(
        isOpen = uiState.isSettingsDialogOpen,
        isDarkMode = uiState.isDarkMode,
        currentFontStyle = uiState.appFontStyle,
        currentFontScale = uiState.appFontScale,
        studyNotifsEnabled = uiState.studyNotificationsEnabled,
        spacedRepetitionNotifsEnabled = uiState.spacedRepetitionAlertsEnabled,
        examDeadlinesNotifsEnabled = uiState.examDeadlinesAlertsEnabled,
        studentEmail = uiState.studentEmail,
        onDismiss = { viewModel.closeSettingsDialog() },
        onToggleDarkMode = { enabled -> viewModel.setDarkMode(enabled) },
        onSelectFontStyle = { style -> viewModel.setFontStyle(style) },
        onSelectFontScale = { scale -> viewModel.setFontScale(scale) },
        onToggleStudyNotifs = { enabled -> viewModel.toggleStudyNotifications(enabled) },
        onToggleSpacedRepetitionNotifs = { enabled -> viewModel.toggleSpacedRepetitionAlerts(enabled) },
        onToggleExamDeadlinesNotifs = { enabled -> viewModel.toggleExamDeadlinesAlerts(enabled) },
        onLinkEmail = { email -> viewModel.linkStudentEmail(email) },
        onUnlinkEmail = { viewModel.unlinkStudentEmail() },
        onOpenAcademicCalendar = { viewModel.openAcademicCalendarDialog() },
        onOpenCloudBackup = { viewModel.openEmailLoginDialog() },
        onExportBackupJson = { viewModel.getExportBackupJson() },
        onFeedbackMessage = { msg -> viewModel.showFeedbackToast(msg) }
    )
}
