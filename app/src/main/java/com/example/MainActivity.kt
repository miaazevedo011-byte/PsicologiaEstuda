package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceCanvas
import com.example.util.SpeechManager
import com.example.viewmodel.AppTab
import com.example.viewmodel.PsicoEstudaViewModel

private const val TAG_APP_CHECK = "AppCheckInit"
private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context.applicationContext)
        }

        if (BuildConfig.DEBUG) {
            val debugToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)
            if (!debugToken.isNullOrEmpty()) {
                val persistenceKey = FirebaseApp.getInstance().persistenceKey
                val prefs = context.getSharedPreferences(
                    "$DEBUG_STORE_PREFIX$persistenceKey",
                    Context.MODE_PRIVATE
                )
                prefs.edit()
                    .putString(DEBUG_SECRET_KEY, debugToken)
                    .commit()
                Log.d(TAG_APP_CHECK, "App Check debug secret configured successfully.")
            } else {
                Log.w(TAG_APP_CHECK, "No debug token found in Intent extra.")
            }

            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            Log.d(TAG_APP_CHECK, "DebugAppCheckProviderFactory installed.")
        } else {
            val resId = context.resources.getIdentifier(RECAPTCHA_SITE_KEY_RES, "string", context.packageName)
            if (resId != 0) {
                val siteKey = context.getString(resId)
                if (siteKey.isNotBlank()) {
                    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                        RecaptchaAppCheckProviderFactory.getInstance(siteKey)
                    )
                    Log.d(TAG_APP_CHECK, "RecaptchaAppCheckProviderFactory installed.")
                } else {
                    Log.e(TAG_APP_CHECK, "recaptcha_site_key resource string is blank.")
                }
            } else {
                Log.e(TAG_APP_CHECK, "recaptcha_site_key string resource not found in package.")
            }
        }
    } catch (e: Exception) {
        Log.e(TAG_APP_CHECK, "Failed to initialize Firebase App Check", e)
    }
}

class MainActivity : ComponentActivity() {

    private val viewModel: PsicoEstudaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            MyApplicationTheme(
                darkTheme = uiState.isDarkMode,
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

    val isSpeaking by speechManager.isSpeaking.collectAsStateWithLifecycle()
    val spokenText by speechManager.spokenText.collectAsStateWithLifecycle()
    val currentTitle by speechManager.currentTitle.collectAsStateWithLifecycle()
    val activeItemId by speechManager.activeItemId.collectAsStateWithLifecycle()
    val speechRate by speechManager.speechRate.collectAsStateWithLifecycle()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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
                    AppTab.CRONOGRAMA -> "Cronograma Acadêmico"
                    AppTab.DISCIPLINAS -> "Disciplinas"
                    AppTab.FLASHCARDS -> "Flashcards"
                    AppTab.MATERIAIS -> "Biblioteca & Materiais"
                },
                studentName = uiState.userProfile.name,
                semesterText = uiState.userProfile.semester,
                onSearchClick = { viewModel.openUnifiedSearch() },
                onSettingsClick = { viewModel.openSettingsDialog() },
                onNotificationsClick = { viewModel.selectTab(AppTab.CRONOGRAMA) },
                onShareClick = { viewModel.openShareAppDialog() },
                onProfileClick = { viewModel.openEditProfileDialog() }
            )
        },
        bottomBar = {
            PsicoBottomBar(
                currentTab = uiState.currentTab,
                onTabSelected = { tab -> viewModel.selectTab(tab) }
            )
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
                    onOpenAcademicCalendar = { viewModel.selectTab(AppTab.CRONOGRAMA) },
                    onOpenEmailLogin = { viewModel.openEmailLoginDialog() },
                    onOpenSettings = { viewModel.openSettingsDialog() },
                    onToggleSuspendTrack = { trackId -> viewModel.toggleSuspendStudyTrack(trackId) },
                    onPlayTrackAudio = {
                        speechManager.speak("PsicoEstuda AI: Ritmo acadêmico ativado. Foco nas metas semestrais e leitura dos materiais recomendados.")
                    }
                )

                AppTab.ENCICLOPEDIA -> EncyclopediaScreen(
                    uiState = uiState,
                    activeItemId = activeItemId,
                    isSpeaking = isSpeaking,
                    onSelectSubTab = { subTab -> viewModel.selectEncyclopediaSubTab(subTab) },
                    onPlayVerbeteAudio = { verbete ->
                        val microPillsText = verbete.microPills.joinToString(". ") { "${it.first}: ${it.second}" }
                        speechManager.togglePlayPause(
                            id = verbete.id,
                            title = verbete.title,
                            text = "Artigo da Enciclopédia: ${verbete.title}. Escola teórica: ${verbete.school}. Autores: ${verbete.authors}. Resumo conceitual: ${verbete.summary}. Pílulas de Conhecimento: $microPillsText"
                        )
                    },
                    onCreateNoteForVerbete = { verbete ->
                        viewModel.openAddNoteDialog(initialTopic = verbete.title, discipline = verbete.linkedDiscipline)
                    },
                    onToggleBookmark = { id -> viewModel.toggleBookmarkVerbete(id) },
                    onAuthorClick = { author ->
                        viewModel.openAuthorDetailDialog(author)
                    }
                )

                AppTab.CRONOGRAMA -> CronogramaScreen(
                    uiState = uiState,
                    onAddEvent = { title, type, date, time, location, disc ->
                        viewModel.addAcademicEvent(title, type, date, time, location, disc)
                    },
                    onDeleteEvent = { id ->
                        viewModel.deleteAcademicEvent(id)
                    },
                    onPlayAudioSummary = {
                        val count = uiState.academicEvents.size
                        val msg = if (count > 0) {
                            "Você possui $count eventos cadastrados no seu cronograma acadêmico. Mantenha os prazos em dia."
                        } else {
                            "Seu cronograma acadêmico está vazio. Toque no botão para cadastrar provas e entregas de estágio."
                        }
                        speechManager.speak("cronograma_summary", "Cronograma Semestral", msg)
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
                        speechManager.speak("disc_${disc.id}", disc.name, "Disciplina: ${disc.name}. Professor: ${disc.professor}. Ementa: ${disc.syllabusSummary}")
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
                            speechManager.speak("card_${card.id}", "Flashcard", reading)
                        } else {
                            speechManager.speak("Nenhum cartão selecionado no momento.")
                        }
                    },
                    onGenerateFromCustomText = { text, topic -> viewModel.generateFlashcardsFromCustomText(text, topic) }
                )

                AppTab.MATERIAIS -> MaterialsScreen(
                    uiState = uiState,
                    activeItemId = activeItemId,
                    isSpeaking = isSpeaking,
                    onSelectFilter = { filter -> viewModel.selectMaterialsFilter(filter) },
                    onOpenAddMaterial = { viewModel.openAddMaterialDialog() },
                    onOpenAddNote = { topic, disc -> viewModel.openAddNoteDialog(topic, disc) },
                    onSelectMaterial = { material -> viewModel.openMaterialReader(material) },
                    onEditNote = { note -> viewModel.openEditNoteDialog(note) },
                    onDeleteNote = { id -> viewModel.deleteNote(id) },
                    onPlayNoteAudio = { note ->
                        speechManager.togglePlayPause(
                            id = note.id,
                            title = note.title,
                            text = "Anotação de Estudo: ${note.title}. Disciplina: ${note.discipline}. Tópico: ${note.linkedTopic}. Tags: ${note.tags.joinToString(", ")}. Conteúdo completo: ${note.content}"
                        )
                    },
                    onPlayMaterialAudio = { mat ->
                        speechManager.togglePlayPause(
                            id = mat.id,
                            title = mat.title,
                            text = "Material de Estudo: ${mat.title}. Disciplina: ${mat.linkedDiscipline}. Tipo: ${mat.type}. Resumo Inteligente e Conteúdo: ${mat.aiSummary}"
                        )
                    }
                )
            }

            TtsPlayerBar(
                isSpeaking = isSpeaking,
                title = currentTitle,
                spokenText = spokenText,
                speechRate = speechRate,
                onStop = { speechManager.stop() },
                onSpeedChange = { rate -> speechManager.setSpeed(rate) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
            )
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
        },
        onPlayAudio = { title, content ->
            speechManager.togglePlayPause(
                id = "draft_note",
                title = title.ifBlank { "Rascunho de Anotação" },
                text = "${title.ifBlank { "Anotação" }}. $content"
            )
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

    MaterialReaderDialog(
        isOpen = uiState.isMaterialReaderOpen,
        material = uiState.selectedMaterial,
        onDismiss = {
            speechManager.stop()
            viewModel.closeMaterialReader()
        },
        onGenerateFlashcardsFromText = { text, topic ->
            speechManager.stop()
            viewModel.generateFlashcardsFromCustomText(text, topic)
            viewModel.closeMaterialReader()
        },
        onGenerateClinicalCase = { mat ->
            speechManager.stop()
            viewModel.generateClinicalCaseFromMaterial(mat)
            viewModel.closeMaterialReader()
        },
        onFeedback = { msg -> viewModel.showFeedbackToast(msg) },
        onPlayAudio = { text -> speechManager.speak(text) },
        onStopAudio = { speechManager.stop() },
        isSpeaking = isSpeaking
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
        currentFontScale = uiState.appFontScale,
        studyNotifsEnabled = uiState.studyNotificationsEnabled,
        spacedRepetitionNotifsEnabled = uiState.spacedRepetitionAlertsEnabled,
        examDeadlinesNotifsEnabled = uiState.examDeadlinesAlertsEnabled,
        studentEmail = uiState.studentEmail,
        onDismiss = { viewModel.closeSettingsDialog() },
        onToggleDarkMode = { enabled -> viewModel.setDarkMode(enabled) },
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
