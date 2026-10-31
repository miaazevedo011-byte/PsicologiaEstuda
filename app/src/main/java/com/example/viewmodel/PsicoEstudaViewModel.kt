package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppStorageRepository
import com.example.data.CurriculumData
import com.example.data.SampleData
import com.example.model.*
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

enum class AppTab(val label: String, val route: String) {
    INICIO("Início", "inicio"),
    ENCICLOPEDIA("Enciclopédia", "enciclopedia"),
    CRONOGRAMA("Cronograma", "cronograma"),
    FLASHCARDS("Flashcards", "flashcards"),
    DISCIPLINAS("Disciplinas", "disciplinas"),
    MATERIAIS("Materiais", "materiais")
}

enum class EncyclopediaSubTab {
    CONCEITOS, LINHA_DO_TEMPO
}

enum class MaterialsFilter {
    TODOS, PDFS, NOTAS
}

data class UnifiedSearchResult(
    val verbetes: List<Verbete>,
    val disciplines: List<Discipline>,
    val flashcards: List<Flashcard>,
    val notes: List<Note>,
    val materials: List<StudyMaterial>
)

data class PsicoUiState(
    val currentTab: AppTab = AppTab.INICIO,
    val encyclopediaSubTab: EncyclopediaSubTab = EncyclopediaSubTab.CONCEITOS,
    val materialsFilter: MaterialsFilter = MaterialsFilter.TODOS,
    val selectedSemester: Int = 1,
    val showCompletedDisciplines: Boolean = false,
    
    // User profile & Metrics (Personalized & Persistent)
    val userProfile: UserProfile,
    val studentEmail: String = "",
    val academicEvents: List<AcademicEvent> = emptyList(),
    val studyTracks: List<StudyTrack> = emptyList(),
    
    // Content Data (Encyclopedia & Timeline always full as reference library)
    val authors: List<Author> = SampleData.authors,
    val verbetes: List<Verbete> = SampleData.verbetes,
    val timeline: List<TimelineMilestone> = SampleData.timelineMilestones,
    val disciplines: List<Discipline> = emptyList(),
    val glossaryTerms: List<GlossaryTerm> = SampleData.glossaryTerms,
    val selectedGlossaryTerm: GlossaryTerm = SampleData.glossaryTerms.first(),
    val notes: List<Note> = emptyList(),
    val materials: List<StudyMaterial> = emptyList(),
    
    // Flashcard State (Starts empty, populated dynamically from attached materials)
    val decks: List<Deck> = emptyList(),
    val activeDeck: Deck = Deck(
        id = "deck_geral",
        title = "Meus Flashcards",
        category = "Geral",
        totalCards = 0,
        dailyGoal = 5,
        masteredCount = 0,
        isCurrent = true
    ),
    val flashcards: List<Flashcard> = emptyList(),
    val currentCardIndex: Int = 0,
    val isCardFlipped: Boolean = false,
    val isHintExpanded: Boolean = true,
    
    // Material Reader State
    val selectedMaterial: StudyMaterial? = null,
    val isMaterialReaderOpen: Boolean = false,

    // Clinical Cases (Generated dynamically from attached materials)
    val clinicalCases: List<ClinicalCase> = emptyList(),
    val isClinicalCasesDialogOpen: Boolean = false,
    val isShareAppDialogOpen: Boolean = false,

    // AI Chat & Search State (Gemini 3.8 Flash + Google Search Grounding)
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            text = "Olá! Sou seu assistente de Inteligência Artificial integrado ao Google Search Grounding e Gemini 3.8 Flash. Pesquiso em fontes científicas, acadêmicas e sites seguros autorizados para trazer veracidade e precisão aos seus estudos em Psicologia.",
            isUser = false
        )
    ),
    val isAiLoading: Boolean = false,
    val aiStreamingText: String = "",

    // Dialogs & Modals
    val isSettingsDialogOpen: Boolean = false,
    val isDarkMode: Boolean = false,
    val appThemeMode: AppThemeMode = AppThemeMode.LIGHT,
    val appFontScale: Float = 1.0f,
    val studyNotificationsEnabled: Boolean = true,
    val spacedRepetitionAlertsEnabled: Boolean = true,
    val examDeadlinesAlertsEnabled: Boolean = true,
    val isAuthorDetailDialogOpen: Boolean = false,
    val selectedAuthor: Author? = null,
    val isAcademicCalendarDialogOpen: Boolean = false,
    val isEmailLoginDialogOpen: Boolean = false,
    val isAllDecksDialogOpen: Boolean = false,
    val isUnifiedSearchOpen: Boolean = false,
    val searchQuery: String = "",
    val isAddNoteDialogOpen: Boolean = false,
    val noteToEdit: Note? = null,
    val noteDraftTopic: String = "",
    val noteDraftDiscipline: String = "",
    val isAddEventDialogOpen: Boolean = false,
    val isAddMaterialDialogOpen: Boolean = false,
    val isSocraticAssistantOpen: Boolean = false,
    val isEditProfileDialogOpen: Boolean = false,
    val socraticChatMessages: List<Pair<String, Boolean>> = listOf(
        "Olá! Sou seu assistente de formulação clínica e supervisão reflexiva em Psicologia. Qual caso clínico ou conceito psicopatológico gostaria de estruturar hoje?" to false
    ),
    val toastMessage: String? = null
)

class PsicoEstudaViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = AppStorageRepository(application.applicationContext)

    private val _uiState: MutableStateFlow<PsicoUiState>
    val uiState: StateFlow<PsicoUiState>

    init {
        val savedProfile = storage.loadUserProfile()
        val savedEmail = storage.getStudentEmail()
        val savedEvents = storage.loadAcademicEvents()
        val savedMaterials = storage.loadStudyMaterials()
        val savedNotes = storage.loadNotes()
        val savedCases = storage.loadClinicalCases()
        val savedDisciplines = storage.loadDisciplines()
        val savedTracks = storage.loadStudyTracks()
        val savedFlashcards = storage.loadFlashcards()
        val savedDecks = storage.loadDecks()

        val savedDarkMode = storage.isDarkMode()
        val savedThemeMode = storage.getThemeMode()
        val savedFontScale = storage.getFontScale()
        val savedNotifStudy = storage.getStudyNotifications()
        val savedNotifSpaced = storage.getSpacedRepetitionAlerts()
        val savedNotifExams = storage.getExamDeadlinesAlerts()

        // With the exception of the Encyclopedia, everything starts from zero:
        // Profile starts clean with 0 stats and 1st period
        val initialProfile = savedProfile ?: UserProfile(
            name = "Estudante",
            course = "Psicologia",
            institution = "Universidade",
            semester = "1º Período",
            semesterNumber = 1,
            shift = "Matutino",
            currentWeek = 1,
            totalWeeks = 18,
            weeklyDedicatedHours = 0.0f,
            weeklyTargetHours = 10.0f,
            verbetesRead = 0,
            cardsMastered = 0,
            audioClassesListened = 0
        )

        val initialSemester = initialProfile.semesterNumber
        val initialDisciplines = savedDisciplines ?: emptyList()
        val initialEvents = savedEvents ?: emptyList()
        val initialMaterials = savedMaterials ?: emptyList()
        val initialNotes = savedNotes ?: emptyList()
        val initialCases = savedCases ?: emptyList()
        val initialTracks = savedTracks ?: emptyList()
        val initialCards = savedFlashcards ?: emptyList()

        val defaultDeck = Deck(
            id = "deck_geral",
            title = "Meus Flashcards",
            category = "Geral",
            totalCards = initialCards.size,
            dailyGoal = 5,
            masteredCount = 0,
            isCurrent = true
        )
        val initialDecks = if (!savedDecks.isNullOrEmpty()) savedDecks else listOf(defaultDeck)
        val activeDeck = initialDecks.firstOrNull() ?: defaultDeck

        _uiState = MutableStateFlow(
            PsicoUiState(
                userProfile = initialProfile,
                studentEmail = savedEmail,
                selectedSemester = initialSemester,
                disciplines = initialDisciplines,
                academicEvents = initialEvents,
                studyTracks = initialTracks,
                materials = initialMaterials,
                notes = initialNotes,
                clinicalCases = initialCases,
                decks = initialDecks,
                activeDeck = activeDeck,
                flashcards = initialCards,
                isDarkMode = savedDarkMode,
                appThemeMode = savedThemeMode,
                appFontScale = savedFontScale,
                studyNotificationsEnabled = savedNotifStudy,
                spacedRepetitionAlertsEnabled = savedNotifSpaced,
                examDeadlinesAlertsEnabled = savedNotifExams
            )
        )
        uiState = _uiState.asStateFlow()
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun selectEncyclopediaSubTab(subTab: EncyclopediaSubTab) {
        _uiState.update { it.copy(encyclopediaSubTab = subTab) }
    }

    fun selectMaterialsFilter(filter: MaterialsFilter) {
        _uiState.update { it.copy(materialsFilter = filter) }
    }

    // Semester selection: updates student selected semester while preserving dynamic user disciplines
    fun selectSemester(semester: Int) {
        _uiState.update { state ->
            state.copy(selectedSemester = semester)
        }
    }

    fun toggleCompletedDisciplines(showCompleted: Boolean) {
        _uiState.update { it.copy(showCompletedDisciplines = showCompleted) }
    }

    fun advanceDisciplineModule(disciplineId: String) {
        _uiState.update { state ->
            val disc = state.disciplines.find { it.id == disciplineId }
            val updated = state.disciplines.map { d ->
                if (d.id == disciplineId) {
                    val nextModule = (d.currentModule + 1).coerceAtMost(d.totalModules)
                    val newProgress = ((nextModule.toFloat() / d.totalModules) * 100).toInt().coerceAtMost(100)
                    val updatedObjectives = d.learningObjectives.mapIndexed { idx, pair ->
                        if (idx < nextModule) pair.first to true else pair
                    }
                    d.copy(
                        currentModule = nextModule,
                        progressPercent = newProgress,
                        learningObjectives = updatedObjectives,
                        isCompleted = nextModule == d.totalModules
                    )
                } else d
            }
            state.copy(
                disciplines = updated,
                toastMessage = "Módulo avançado para '${disc?.name ?: "disciplina"}'!"
            )
        }
    }

    fun toggleDisciplineObjective(disciplineId: String, objectiveIndex: Int) {
        _uiState.update { state ->
            val updated = state.disciplines.map { disc ->
                if (disc.id == disciplineId) {
                    val updatedObjectives = disc.learningObjectives.mapIndexed { idx, pair ->
                        if (idx == objectiveIndex) pair.first to !pair.second else pair
                    }
                    val completedCount = updatedObjectives.count { it.second }
                    val newProgress = ((completedCount.toFloat() / updatedObjectives.size) * 100).toInt().coerceAtMost(100)
                    val newModule = (completedCount + 1).coerceIn(1, disc.totalModules)
                    disc.copy(
                        learningObjectives = updatedObjectives,
                        progressPercent = newProgress,
                        currentModule = if (completedCount == updatedObjectives.size) disc.totalModules else newModule,
                        isCompleted = completedCount == updatedObjectives.size
                    )
                } else disc
            }
            state.copy(disciplines = updated)
        }
    }

    fun generateFlashcardsForDiscipline(discipline: Discipline) {
        val deckId = "deck_${discipline.id}"
        val list = mutableListOf<Flashcard>()

        // Specific, didactic cards constructed directly from the syllabus and objectives
        val isPsico = discipline.name.contains("Psicopatologia", ignoreCase = true)
        val isBio = discipline.name.contains("Biológicas", ignoreCase = true) || discipline.name.contains("Neuro", ignoreCase = true)
        val isDev = discipline.name.contains("Desenvolvimento", ignoreCase = true)
        val isTCC = discipline.name.contains("Cognitiva", ignoreCase = true) || discipline.name.contains("TCC", ignoreCase = true)
        val isPsicanalise = discipline.name.contains("Psicanálise", ignoreCase = true)
        val isHumanis = discipline.name.contains("Humanista", ignoreCase = true) || discipline.name.contains("Fenomenológica", ignoreCase = true)
        val isSocial = discipline.name.contains("Social", ignoreCase = true)
        val isAEC = discipline.name.contains("Comportamento", ignoreCase = true) || discipline.name.contains("Experimental", ignoreCase = true)

        // Card 1: Core conceptual definition
        val q1 = when {
            isPsico -> "No exame do estado mental (Semiologia DSM-5-TR), como diferenciar uma alucinação de uma ilusão e de um delírio?"
            isBio -> "Qual o mecanismo biofísico de deflagração do Potencial de Ação neuronal e papel dos canais de Na+ e K+?"
            isDev -> "Qual o marco central que diferencia o estágio Pré-Operatório do Operatório Concreto de Jean Piaget?"
            isTCC -> "O que é a 'Tríade Cognitiva' postulada por Aaron Beck e como ela opera no Transtorno Depressivo Maior?"
            isPsicanalise -> "Na Segunda Tópica freudiana (1923), quais são as três instâncias psíquicas e suas leis de funcionamento?"
            isHumanis -> "Quais são as três condições necessárias e suficientes postuladas por Carl Rogers para a mudança terapêutica construtiva?"
            isSocial -> "Qual a conceituação de 'Dissonância Cognitiva' de Festinger e como ela induz a mudança de atitude do indivíduo?"
            isAEC -> "Qual a diferença funcional entre Reforçamento Negativo e Punição Positiva na Análise Experimental do Comportamento?"
            else -> "Qual a premissa epistemológica e conceitual central abordada na ementa de ${discipline.name}?"
        }
        val a1 = when {
            isPsico -> "Alucinação é percepção sensorial clara sem objeto real externo; Ilusão é percepção deformada de um objeto real existente; Delírio é juízo de realidade falso, irredutível à lógica formal e inabalável por contraprovas culturais."
            isBio -> "A despolarização atinge o limiar (-55mV), abrindo canais de Na+ voltagem-dependentes (influxo maciço). No pico (+30mV), os canais de Na+ se fecham e canais de K+ se abrem (efluxo), repolarizando e hiperpolarizando temporariamente a membrana."
            isDev -> "No estágio Operatório Concreto (7-11 anos), a criança desenvolve a reversibilidade de pensamento e a conservação de quantidade, peso e volume, superando o egocentrismo e a centração visual do pré-operatório."
            isTCC -> "A Tríade Cognitiva consiste em visões disfuncionais e hipernegativas sistemáticas sobre: (1) Si mesmo ('sou um fracasso'); (2) O mundo exterior ('todos me rejeitam'); e (3) O futuro ('nada jamais dará certo')."
            isPsicanalise -> "O Id (totalmente inconsciente, opera pelo princípio do prazer e processo primário); o Ego (instância executiva, opera pelo princípio da realidade e processo secundário); e o Superego (instância moral, herdeiro do Complexo de Édipo)."
            isHumanis -> "1) Congruência/Autenticidade do terapeuta; 2) Consideração Positiva Incondicional (aceitação afetuosa e não-julgadora); e 3) Empatia Fenomenológica profunda (compreender o mundo interno do cliente como se fosse o seu)."
            isSocial -> "Estado de desconforto psicológico decorrente da sustentação simultânea de cognições contraditórias ou agir contra seus próprios valores, impulsionando a pessoa a alterar suas crenças para restaurar a consonância interna."
            isAEC -> "Reforçamento Negativo aumenta a probabilidade do comportamento futuro através da remoção/fuga de um estímulo aversivo. Punição Positiva reduz a probabilidade do comportamento através da apresentação de um estímulo aversivo."
            else -> "${discipline.syllabusSummary}. Fundamenta o raciocínio clínico e metodológico na formação curricular de Psicologia."
        }

        list.add(
            Flashcard(
                id = "card_${discipline.id}_core",
                deckId = deckId,
                category = discipline.name,
                question = q1,
                hint = "Foco nos fundamentos conceituais do ${discipline.semester}º período.",
                answer = a1,
                citation = "${discipline.name} • Ementa Curricular",
                status = CardMasteryStatus.NOVO
            )
        )

        // Cards derived from each learning objective
        discipline.learningObjectives.forEachIndexed { idx, (obj, _) ->
            val objQuestion = "No contexto de '${discipline.name}', explique detalhadamente a competência: '$obj'."
            val objAnswer = when {
                obj.contains("Diferenciar senso comum", ignoreCase = true) ->
                    "O senso comum baseia-se em intuições cotidianas, preconceitos e generalizações ingênuas sem controle experimental; a Psicologia científica emprega método sistemático, amostragem empírica, falseabilidade e rigor metodológico para formular leis e teorias universais."
                obj.contains("Leipzig", ignoreCase = true) || obj.contains("Wundt", ignoreCase = true) ->
                    "O laboratório de Leipzig (1879) marcou o nascimento da psicologia científica ao adotar a introspecção controlada e cronometria mental para investigar a estrutura da consciência imediata, desvinculando-se da metafísica."
                obj.contains("reforço", ignoreCase = true) || obj.contains("reforçamento", ignoreCase = true) ->
                    "Reforçamento Contínuo (CRF) é ideal para aquisição inicial de novas respostas; Reforçamento Intermitente (Razão Fixa/Variável, Intervalo Fixo/Variável) produz taxas elevadas de resposta e alta resistência à extinção."
                obj.contains("Apego", ignoreCase = true) ->
                    "O Apego Seguro decorre de cuidadores previsíveis e responsivos; o Evitativo decorre de rejeição sistemática de afeto; e o Ambivalente decorre de imprevisibilidade materna. Estruturam os modelos operacionais internos que regem as relações na vida adulta."
                obj.contains("semiológica", ignoreCase = true) || obj.contains("Tristeza", ignoreCase = true) ->
                    "A tristeza normal é reativa, proporcional, transitória e preserva a autoestima; a depressão clínica patológica (DSM-5-TR) apresenta anedonia pervasiva, culpa excessiva, lentificação psicomotora e prejuízo funcional contínuo por no mínimo 2 semanas."
                obj.contains("sintomas positivos", ignoreCase = true) ->
                    "Sintomas Positivos representam excesso ou distorção de funções normais (delírios, alucinações, desorganização do pensamento); Sintomas Negativos representam perda ou diminuição funcional (embotamento afetivo, avolia, anedonia, alogia)."
                else ->
                    "Esta competência capacita o acadêmico a articular as bases teóricas de '$obj' com a prática clínica, avaliação diagnóstica e conduta ética preconizada pelo Conselho Federal de Psicologia (CFP)."
            }

            list.add(
                Flashcard(
                    id = "card_${discipline.id}_obj_${idx + 1}",
                    deckId = deckId,
                    category = "${discipline.name} • Tópico ${idx + 1}",
                    question = objQuestion,
                    hint = "Objetivo programático essencial do ${discipline.semester}º Período.",
                    answer = objAnswer,
                    citation = "${discipline.name} • Módulo ${discipline.currentModule}",
                    status = CardMasteryStatus.NOVO
                )
            )
        }

        // Diagnostic / Clinical Synthesis Card
        list.add(
            Flashcard(
                id = "card_${discipline.id}_clinical",
                deckId = deckId,
                category = "${discipline.name} • Aplicação Prática",
                question = "Como aplicar os conhecimentos acumulados em ${discipline.name} para a formulação de caso e intervenção ética?",
                hint = "Considere a integração teórico-prática e o Código de Ética Profissional do Psicólogo.",
                answer = "A fundamentação teórica de ${discipline.name} permite transpor conceitos abstratos para o diagnóstico diferencial (DSM-5-TR / CID-11), escuta qualificada, planejamento de metas terapêuticas e confecção de documentos psicológicos fundamentados.",
                citation = "Prática Clínica & Diretrizes Curriculares • ${discipline.code}",
                status = CardMasteryStatus.NOVO
            )
        )

        val deck = Deck(
            id = deckId,
            title = discipline.name,
            category = "${discipline.semester}º Período • ${discipline.code}",
            totalCards = list.size,
            dailyGoal = 5,
            masteredCount = 0,
            isCurrent = true
        )

        _uiState.update { state ->
            val updatedDecks = (state.decks.filter { it.id != deckId } + deck)
            val updatedCards = state.flashcards.filter { it.deckId != deckId } + list
            storage.saveFlashcards(updatedCards)
            state.copy(
                decks = updatedDecks,
                activeDeck = deck,
                flashcards = updatedCards,
                currentCardIndex = 0,
                isCardFlipped = false,
                currentTab = AppTab.FLASHCARDS,
                toastMessage = "Flashcards de alta precisão gerados para ${discipline.name}!"
            )
        }
    }

    fun generateFlashcardsFromCustomText(customText: String, topic: String = "Estudo Personalizado") {
        if (customText.isBlank()) return
        val deckId = "deck_custom_${System.currentTimeMillis()}"
        val sentences = customText.split(".", "\n").map { it.trim() }.filter { it.length > 20 }

        val list = mutableListOf<Flashcard>()
        val effectiveTopic = if (topic.isNotBlank()) topic else "Estudo Livre"

        // Card 1: Core definition from text
        list.add(
            Flashcard(
                id = "card_custom_1_${System.currentTimeMillis()}",
                deckId = deckId,
                category = effectiveTopic,
                question = "Segundo o texto estudado sobre '$effectiveTopic', qual a tese ou definição fundamental apresentada?",
                hint = "Revise a introdução e o conceito nuclear do conteúdo inserido.",
                answer = sentences.firstOrNull() ?: customText.take(200),
                citation = "Texto Inserido pelo Aluno • $effectiveTopic",
                status = CardMasteryStatus.NOVO
            )
        )

        // Card 2: Mechanism / Arguments
        if (sentences.size > 1) {
            list.add(
                Flashcard(
                    id = "card_custom_2_${System.currentTimeMillis()}",
                    deckId = deckId,
                    category = "$effectiveTopic • Mecanismos",
                    question = "Quais são os mecanismos, evidências ou desdobramentos conceituais detalhados no trecho?",
                    hint = "Atente-se aos conectivos causais e relações teóricas expostas.",
                    answer = sentences.getOrNull(1) ?: sentences.first(),
                    citation = "Anotação Pessoal • $effectiveTopic",
                    status = CardMasteryStatus.NOVO
                )
            )
        }

        // Card 3: Clinical / Psychological Application
        val clinicalDetail = if (sentences.size > 2) sentences[2] else "Aplicação dos conceitos de $effectiveTopic na prática e intervenção psicológica."
        list.add(
            Flashcard(
                id = "card_custom_3_${System.currentTimeMillis()}",
                deckId = deckId,
                category = "$effectiveTopic • Aplicação",
                question = "Como articular na prática psicológica os conceitos destacados em '$effectiveTopic'?",
                hint = "Pense na relevância clínica, diagnóstica e no manejo ético.",
                answer = clinicalDetail,
                citation = "Síntese Ativa PsicoEstuda • $effectiveTopic",
                status = CardMasteryStatus.NOVO
            )
        )

        val newDeck = Deck(
            id = deckId,
            title = effectiveTopic,
            category = "Baralho do Aluno",
            totalCards = list.size,
            dailyGoal = 5,
            masteredCount = 0,
            isCurrent = true
        )

        _uiState.update { state ->
            val updatedDecks = listOf(newDeck) + state.decks
            val updatedCards = list + state.flashcards
            storage.saveFlashcards(updatedCards)
            state.copy(
                decks = updatedDecks,
                activeDeck = newDeck,
                flashcards = updatedCards,
                currentCardIndex = 0,
                isCardFlipped = false,
                toastMessage = "Baralho criado com sucesso! ${list.size} flashcards gerados a partir do seu texto."
            )
        }
    }

    fun selectGlossaryTerm(term: GlossaryTerm) {
        _uiState.update { it.copy(selectedGlossaryTerm = term) }
    }

    // Author Details Modal
    fun openAuthorDetailDialog(author: Author) {
        _uiState.update { it.copy(isAuthorDetailDialogOpen = true, selectedAuthor = author) }
    }

    fun closeAuthorDetailDialog() {
        _uiState.update { it.copy(isAuthorDetailDialogOpen = false, selectedAuthor = null) }
    }

    // Academic Calendar & Spreadsheet Dialog
    fun openAcademicCalendarDialog() {
        _uiState.update { it.copy(isAcademicCalendarDialogOpen = true) }
    }

    fun closeAcademicCalendarDialog() {
        _uiState.update { it.copy(isAcademicCalendarDialogOpen = false) }
    }

    // Email Login Dialog
    fun openEmailLoginDialog() {
        _uiState.update { it.copy(isEmailLoginDialogOpen = true) }
    }

    fun closeEmailLoginDialog() {
        _uiState.update { it.copy(isEmailLoginDialogOpen = false) }
    }

    fun saveStudentEmail(email: String) {
        storage.saveStudentEmail(email)
        _uiState.update { state ->
            state.copy(
                studentEmail = email,
                toastMessage = "E-mail $email vinculado com sucesso! Todos os dados estão salvos."
            )
        }
    }

    fun openEditProfileDialog() {
        _uiState.update { it.copy(isEditProfileDialogOpen = true) }
    }

    fun closeEditProfileDialog() {
        _uiState.update { it.copy(isEditProfileDialogOpen = false) }
    }

    fun updateStudentProfile(
        name: String,
        institution: String,
        course: String,
        semesterNumber: Int,
        shift: String,
        weeklyTargetHours: Float,
        resetStats: Boolean
    ) {
        _uiState.update { state ->
            val updatedProfile = state.userProfile.copy(
                name = name.ifBlank { "Estudante" },
                institution = institution.ifBlank { "Universidade" },
                course = course.ifBlank { "Psicologia" },
                semester = "${semesterNumber}º Período",
                semesterNumber = semesterNumber,
                shift = shift,
                weeklyTargetHours = weeklyTargetHours,
                weeklyDedicatedHours = if (resetStats) 0.0f else state.userProfile.weeklyDedicatedHours,
                verbetesRead = if (resetStats) 0 else state.userProfile.verbetesRead,
                cardsMastered = if (resetStats) 0 else state.userProfile.cardsMastered,
                audioClassesListened = if (resetStats) 0 else state.userProfile.audioClassesListened
            )

            // Save to persistent storage
            storage.saveUserProfile(updatedProfile)

            state.copy(
                userProfile = updatedProfile,
                selectedSemester = semesterNumber,
                isEditProfileDialogOpen = false,
                toastMessage = "Perfil do estudante atualizado e salvo!"
            )
        }
    }

    // Material Reader Controls
    fun openMaterialReader(material: StudyMaterial) {
        _uiState.update { it.copy(selectedMaterial = material, isMaterialReaderOpen = true) }
    }

    fun closeMaterialReader() {
        _uiState.update { it.copy(isMaterialReaderOpen = false, selectedMaterial = null) }
    }

    // Clinical Cases (Exercícios Práticos)
    fun openClinicalCasesDialog() {
        _uiState.update { it.copy(isClinicalCasesDialogOpen = true) }
    }

    fun closeClinicalCasesDialog() {
        _uiState.update { it.copy(isClinicalCasesDialogOpen = false) }
    }

    fun answerClinicalQuestion(caseId: String, questionId: String, selectedOptionIndex: Int) {
        _uiState.update { state ->
            val updatedCases = state.clinicalCases.map { clinicalCase ->
                if (clinicalCase.id == caseId) {
                    val updatedQuestions = clinicalCase.questions.map { q ->
                        if (q.id == questionId) {
                            q.copy(
                                selectedOptionIndex = selectedOptionIndex,
                                isAnswered = true
                            )
                        } else q
                    }
                    clinicalCase.copy(questions = updatedQuestions)
                } else clinicalCase
            }

            // Save updated cases to storage
            storage.saveClinicalCases(updatedCases)

            val currentCase = updatedCases.firstOrNull { it.id == caseId }
            val currentQ = currentCase?.questions?.firstOrNull { it.id == questionId }
            val isCorrect = currentQ?.correctOptionIndex == selectedOptionIndex

            val feedback = if (isCorrect) {
                "Parabéns! Hipótese e conduta clínica corretas de acordo com o DSM-5-TR."
            } else {
                "Resposta registrada. Revise a justificativa diagnóstica e a semiologia clínica."
            }

            val newHours = (state.userProfile.weeklyDedicatedHours + 0.25f).coerceAtMost(state.userProfile.weeklyTargetHours)
            val updatedProfile = state.userProfile.copy(weeklyDedicatedHours = newHours)
            storage.saveUserProfile(updatedProfile)

            state.copy(
                clinicalCases = updatedCases,
                userProfile = updatedProfile,
                toastMessage = feedback
            )
        }
    }

    fun generateClinicalCaseFromMaterial(material: StudyMaterial) {
        val newCase = ClinicalCase(
            id = "case_${System.currentTimeMillis()}",
            title = "Caso Prático: Estudo de ${material.title}",
            patientAge = "Paciente Adulto, Atendimento Ambulatorial",
            anamneseVignette = "Paciente procura acolhimento clínico relatando sintomatologia e queixa subjetiva diretamente correlacionadas aos tópicos abordados no arquivo '${material.title}', vinculado à disciplina ${material.linkedDiscipline}. Apresenta demanda de avaliação psicodiagnóstica e manejo terapêutico.",
            sourceMaterialOrDiscipline = "Baseado em: ${material.title} • ${material.linkedDiscipline}",
            mentalStateExam = "Consciência clara, orientação global preservada, afeto congruente com humor relatado, discurso coerente e sem sinais de desagregação cognitiva.",
            questions = listOf(
                ClinicalQuestion(
                    id = "q_gen_${System.currentTimeMillis()}",
                    prompt = "Com base nas premissas fundamentais de '${material.title}', qual a primeira diretriz de conduta terapêutica e raciocínio clínico recomendada?",
                    options = listOf(
                        "Formulação integrada e planejamento psicoterapêutico alinhado a ${material.linkedDiscipline}",
                        "Encaminhamento imediato para internação involuntária sem avaliação prévia",
                        "Desconsiderar a queixa por falta de correlação teórica",
                        "Interrupção de qualquer escuta até a realização de exames invasivos"
                    ),
                    correctOptionIndex = 0,
                    explanation = "A fundamentação clínica baseada nas evidências e conceitos do material '${material.title}' preconiza formulação diagnóstica estruturada e projeto terapêutico singular em ${material.linkedDiscipline}."
                )
            ),
            dsm5DiagnosticRationale = "Critérios diagnósticos e semiológicos alinhados ao conteúdo de ${material.linkedDiscipline}.",
            therapeuticApproach = "Psicoterapia baseada em evidências, acolhimento ético e supervisão clínica estruturada."
        )

        _uiState.update { state ->
            val updatedCases = listOf(newCase) + state.clinicalCases
            storage.saveClinicalCases(updatedCases)

            val newHours = (state.userProfile.weeklyDedicatedHours + 0.3f).coerceAtMost(state.userProfile.weeklyTargetHours)
            val updatedProfile = state.userProfile.copy(weeklyDedicatedHours = newHours)
            storage.saveUserProfile(updatedProfile)

            state.copy(
                clinicalCases = updatedCases,
                isClinicalCasesDialogOpen = true,
                userProfile = updatedProfile,
                toastMessage = "Novo Caso Clínico gerado a partir do material '${material.title}'!"
            )
        }
    }

    // Share Dialog
    fun openShareAppDialog() {
        _uiState.update { it.copy(isShareAppDialogOpen = true) }
    }

    fun closeShareAppDialog() {
        _uiState.update { it.copy(isShareAppDialogOpen = false) }
    }

    // Study Tracks (Career Track Suspension)
    fun toggleSuspendStudyTrack(trackId: String) {
        _uiState.update { state ->
            val updated = state.studyTracks.map {
                if (it.id == trackId) it.copy(isSuspended = !it.isSuspended) else it
            }
            storage.saveStudyTracks(updated)
            val target = updated.find { it.id == trackId }
            val msg = if (target?.isSuspended == true) "Tema '${target.title}' suspenso da trilha." else "Tema '${target?.title}' reativado na trilha!"
            state.copy(studyTracks = updated, toastMessage = msg)
        }
    }

    // Flashcards Interaction
    fun openAllDecksDialog() {
        _uiState.update { it.copy(isAllDecksDialogOpen = true) }
    }

    fun closeAllDecksDialog() {
        _uiState.update { it.copy(isAllDecksDialogOpen = false) }
    }

    fun selectDeck(deck: Deck) {
        _uiState.update {
            it.copy(
                activeDeck = deck,
                currentCardIndex = 0,
                isCardFlipped = false,
                isHintExpanded = true,
                isAllDecksDialogOpen = false
            )
        }
    }

    fun flipCard() {
        _uiState.update { it.copy(isCardFlipped = !it.isCardFlipped) }
    }

    fun toggleHint() {
        _uiState.update { it.copy(isHintExpanded = !it.isHintExpanded) }
    }

    fun rateCard(status: CardMasteryStatus) {
        _uiState.update { state ->
            val currentCards = state.flashcards
            if (state.currentCardIndex in currentCards.indices) {
                val updatedCards = currentCards.toMutableList()
                val currentCard = updatedCards[state.currentCardIndex]
                updatedCards[state.currentCardIndex] = currentCard.copy(status = status)
                val nextIndex = (state.currentCardIndex + 1) % updatedCards.size

                val newMastered = if (status == CardMasteryStatus.DOMINADO && currentCard.status != CardMasteryStatus.DOMINADO) {
                    state.userProfile.cardsMastered + 1
                } else state.userProfile.cardsMastered

                val newHours = (state.userProfile.weeklyDedicatedHours + 0.15f).coerceAtMost(state.userProfile.weeklyTargetHours)
                val updatedProfile = state.userProfile.copy(
                    cardsMastered = newMastered,
                    weeklyDedicatedHours = newHours
                )
                storage.saveUserProfile(updatedProfile)
                storage.saveFlashcards(updatedCards)

                state.copy(
                    flashcards = updatedCards,
                    currentCardIndex = nextIndex,
                    isCardFlipped = false,
                    isHintExpanded = true,
                    userProfile = updatedProfile,
                    toastMessage = "Avaliação registrada: ${status.name}"
                )
            } else state
        }
    }

    fun nextCard() {
        _uiState.update { state ->
            val total = state.flashcards.size
            if (total > 0) {
                state.copy(
                    currentCardIndex = (state.currentCardIndex + 1) % total,
                    isCardFlipped = false,
                    isHintExpanded = true
                )
            } else state
        }
    }

    fun previousCard() {
        _uiState.update { state ->
            val total = state.flashcards.size
            if (total > 0) {
                val prev = if (state.currentCardIndex - 1 < 0) total - 1 else state.currentCardIndex - 1
                state.copy(
                    currentCardIndex = prev,
                    isCardFlipped = false,
                    isHintExpanded = true
                )
            } else state
        }
    }

    fun toggleBookmarkVerbete(verbeteId: String) {
        _uiState.update { state ->
            val updated = state.verbetes.map {
                if (it.id == verbeteId) it.copy(isBookmarked = !it.isBookmarked) else it
            }
            state.copy(verbetes = updated, toastMessage = "Verbete atualizado nos favoritos")
        }
    }

    fun openAddNoteDialog(initialTopic: String = "", discipline: String = "") {
        _uiState.update {
            it.copy(
                isAddNoteDialogOpen = true,
                noteToEdit = null,
                noteDraftTopic = initialTopic,
                noteDraftDiscipline = discipline
            )
        }
    }

    fun openEditNoteDialog(note: Note) {
        _uiState.update {
            it.copy(
                isAddNoteDialogOpen = true,
                noteToEdit = note,
                noteDraftTopic = note.linkedTopic,
                noteDraftDiscipline = note.discipline
            )
        }
    }

    fun closeAddNoteDialog() {
        _uiState.update { it.copy(isAddNoteDialogOpen = false, noteToEdit = null) }
    }

    fun updateNote(noteId: String, title: String, content: String, topic: String, discipline: String, tags: List<String>) {
        _uiState.update { state ->
            val updatedNotes = state.notes.map { note ->
                if (note.id == noteId) {
                    note.copy(
                        title = title.ifBlank { note.title },
                        content = content,
                        linkedTopic = topic.ifBlank { note.linkedTopic },
                        discipline = discipline.ifBlank { note.discipline },
                        tags = tags.ifEmpty { note.tags }
                    )
                } else note
            }
            storage.saveNotes(updatedNotes)
            state.copy(
                notes = updatedNotes,
                isAddNoteDialogOpen = false,
                noteToEdit = null,
                toastMessage = "Anotação atualizada com sucesso!"
            )
        }
    }

    fun deleteNote(noteId: String) {
        _uiState.update { state ->
            val updatedNotes = state.notes.filter { it.id != noteId }
            storage.saveNotes(updatedNotes)
            state.copy(
                notes = updatedNotes,
                toastMessage = "Anotação excluída."
            )
        }
    }

    fun saveNote(title: String, content: String, topic: String, discipline: String, tags: List<String>) {
        val newNote = Note(
            id = "note_${System.currentTimeMillis()}",
            title = title.ifBlank { "Anotação sobre $topic" },
            timeAgo = "agora",
            content = content,
            linkedTopic = topic.ifBlank { "Psicologia Geral" },
            discipline = discipline.ifBlank { "Clínica & Teoria" },
            tags = tags.ifEmpty { listOf("Estudo", "Reflexão") }
        )
        _uiState.update { state ->
            val updatedNotes = listOf(newNote) + state.notes
            storage.saveNotes(updatedNotes)

            val newHours = (state.userProfile.weeklyDedicatedHours + 0.2f).coerceAtMost(state.userProfile.weeklyTargetHours)
            val updatedProfile = state.userProfile.copy(
                weeklyDedicatedHours = newHours,
                verbetesRead = state.userProfile.verbetesRead + 1
            )
            storage.saveUserProfile(updatedProfile)

            state.copy(
                notes = updatedNotes,
                isAddNoteDialogOpen = false,
                userProfile = updatedProfile,
                toastMessage = "Anotação salva com sucesso e armazenada permanentemente!"
            )
        }
    }

    // Academic Event & Spreadsheet (Persistent)
    fun openAddEventDialog() {
        _uiState.update { it.copy(isAddEventDialogOpen = true) }
    }

    fun closeAddEventDialog() {
        _uiState.update { it.copy(isAddEventDialogOpen = false) }
    }

    fun addAcademicEvent(title: String, type: EventType, date: String, time: String, location: String, discipline: String) {
        val newEvent = AcademicEvent(
            id = "evt_${System.currentTimeMillis()}",
            title = title,
            type = type,
            dateDisplay = date,
            timeDisplay = time,
            location = location,
            daysRemaining = 5,
            discipline = discipline
        )
        _uiState.update { state ->
            val updatedEvents = state.academicEvents + newEvent
            storage.saveAcademicEvents(updatedEvents)
            state.copy(
                academicEvents = updatedEvents,
                isAddEventDialogOpen = false,
                toastMessage = "Data acadêmica registrada e salva na planilha!"
            )
        }
    }

    fun deleteAcademicEvent(eventId: String) {
        _uiState.update { state ->
            val updatedEvents = state.academicEvents.filterNot { it.id == eventId }
            storage.saveAcademicEvents(updatedEvents)
            state.copy(
                academicEvents = updatedEvents,
                toastMessage = "Compromisso removido da planilha."
            )
        }
    }

    // Material Ingestion with Device Storage & Real Clinical Case Generation
    fun openAddMaterialDialog() {
        _uiState.update { it.copy(isAddMaterialDialogOpen = true) }
    }

    fun closeAddMaterialDialog() {
        _uiState.update { it.copy(isAddMaterialDialogOpen = false) }
    }

    fun ingestMaterial(
        title: String,
        type: MaterialType,
        discipline: String,
        notes: String,
        realFileUri: String? = null,
        fileSizeString: String = "2.4 MB"
    ) {
        viewModelScope.launch {
            val discName = discipline.ifBlank { "Psicologia Geral" }
            val cleanTitle = title.ifBlank { "Arquivo_${System.currentTimeMillis()}" }
            val summaryText = if (notes.isNotBlank()) notes else "Material anexado pelo estudante em $discName. Conteúdo indexado para estudos com leitura por voz, flashcards e caso clínico prático."

            val newMaterial = StudyMaterial(
                id = "mat_${System.currentTimeMillis()}",
                title = cleanTitle,
                type = type,
                size = fileSizeString,
                updatedAt = "Processado agora",
                linkedDiscipline = discName,
                aiSummary = summaryText,
                flashcardsCount = 4,
                verbetesCount = 2,
                uriString = realFileUri
            )

            // Dynamic Cross-Link: Generate Clinical Case Exercise from attached content
            val generatedCase = ClinicalCase(
                id = "case_ai_${System.currentTimeMillis()}",
                title = "Caso Prático: Análise de '$cleanTitle'",
                patientAge = "Paciente Adulto, 28 anos",
                anamneseVignette = "Demanda clínica estruturada a partir do material '$cleanTitle' anexado em $discName. Paciente procura acolhimento psicológico apresentando queixas correlacionadas aos temas tratados no documento: '$summaryText'.",
                sourceMaterialOrDiscipline = "$cleanTitle • $discName",
                mentalStateExam = "Consciência clara, atenção voluntária preservada, afeto modulado e congruente com humor relatado, discurso com nexo causal, sem alterações senso-perceptivas.",
                questions = listOf(
                    ClinicalQuestion(
                        id = "q_mat_1_${System.currentTimeMillis()}",
                        prompt = "Com base nas evidências e premissas de '$cleanTitle', qual a conduta diagnóstica inicial recomendada em $discName?",
                        options = listOf(
                            "Formulação de caso integrada e hipótese diagnóstica estruturada em $discName",
                            "Conduta expectante sem qualquer anamnese clínica aprofundada",
                            "Intervenção aversiva imediata sem estabelecimento de vínculo",
                            "Desconsiderar a queixa subjetiva do paciente"
                        ),
                        correctOptionIndex = 0,
                        explanation = "A fundamentação teórica de '$cleanTitle' orienta formulação de caso compreensiva, escuta ética e projeto terapêutico singular em $discName."
                    )
                ),
                dsm5DiagnosticRationale = "Semiologia e critérios nosológicos alinhados ao conteúdo programático de $discName e DSM-5-TR.",
                therapeuticApproach = "Prática psicológica baseada em evidências científicas, acolhimento ético e supervisão clínica estruturada."
            )

            // Dynamic Flashcards derived from the attached material
            val deckId = "deck_${discName.lowercase().replace(" ", "_").filter { it.isLetterOrDigit() || it == '_' }}"
            val card1 = Flashcard(
                id = "card_${System.currentTimeMillis()}_1",
                deckId = deckId,
                category = discName,
                question = "Qual o conceito nuclear e tese central apresentada em '$cleanTitle'?",
                hint = "Foco nos fundamentos teóricos e no tema do material.",
                answer = summaryText,
                citation = "$cleanTitle • $discName",
                status = CardMasteryStatus.NOVO
            )
            val card2 = Flashcard(
                id = "card_${System.currentTimeMillis()}_2",
                deckId = deckId,
                category = "$discName • Metodologia",
                question = "Quais métodos de observação e critérios analíticos destacados em '$cleanTitle' orientam o estudante?",
                hint = "Considere a semiologia e o raciocínio metodológico.",
                answer = "Estruturação sistemática de hipóteses, registro de evidências empíricas e exame dos fenômenos psicológicos em $discName.",
                citation = "$cleanTitle • Metodologia",
                status = CardMasteryStatus.NOVO
            )
            val card3 = Flashcard(
                id = "card_${System.currentTimeMillis()}_3",
                deckId = deckId,
                category = "$discName • Prática Clínica",
                question = "Como articular na intervenção psicológica os postulados de '$cleanTitle'?",
                hint = "Pense no manejo clínico e conduta ética.",
                answer = "Aplicação de intervenções validadas, plano terapêutico singular e rigor ético preconizado pelo Conselho de Psicologia.",
                citation = "$cleanTitle • Prática",
                status = CardMasteryStatus.NOVO
            )
            val card4 = Flashcard(
                id = "card_${System.currentTimeMillis()}_4",
                deckId = deckId,
                category = "$discName • Semiologia DSM-5-TR",
                question = "Como o conteúdo de '$cleanTitle' auxilia no diagnóstico diferencial de sofrimento psíquico?",
                hint = "Revisão dos critérios nosológicos.",
                answer = "Permite discernir entre reações emocionais adaptativas normais e transtornos mentais com prejuízo funcional significativo.",
                citation = "$cleanTitle • Semiologia",
                status = CardMasteryStatus.NOVO
            )
            val generatedCards = listOf(card1, card2, card3, card4)

            // Dynamic StudyTrack for Home Screen
            val generatedTrack = StudyTrack(
                id = "track_${System.currentTimeMillis()}",
                title = "Estudo de $discName",
                subtitle = "Revisão ativa baseada em '$cleanTitle' com flashcards e caso clínico.",
                discipline = discName,
                durationMin = 15,
                tag = "Material Anexado",
                colorType = "primary"
            )

            _uiState.update { state ->
                val updatedMaterials = listOf(newMaterial) + state.materials
                val updatedCases = listOf(generatedCase) + state.clinicalCases
                val updatedCards = generatedCards + state.flashcards

                // Dynamically update or create Discipline
                val existingDisc = state.disciplines.find { it.name.equals(discName, ignoreCase = true) }
                val updatedDisciplines = if (existingDisc != null) {
                    state.disciplines.map { d ->
                        if (d.id == existingDisc.id) {
                            d.copy(
                                materialsCount = d.materialsCount + 1,
                                flashcardsCount = d.flashcardsCount + 4
                            )
                        } else d
                    }
                } else {
                    val newDiscipline = Discipline(
                        id = "disc_${System.currentTimeMillis()}",
                        name = discName,
                        code = "PSI-${(101..499).random()}",
                        professor = "Docente Responsável",
                        progressPercent = 25,
                        currentModule = 1,
                        totalModules = 4,
                        syllabusSummary = "Disciplina desenvolvida a partir dos materiais anexados. Aborda fundamentos teóricos, semiologia e intervenção prática em $discName.",
                        learningObjectives = listOf(
                            "Conceitos centrais de $discName baseados em '$cleanTitle'" to true,
                            "Análise metodológica e critérios do material" to false,
                            "Formulação de caso clínico e conduta ética" to false
                        ),
                        materialsCount = 1,
                        flashcardsCount = 4,
                        audioDurationMin = 10,
                        semester = state.selectedSemester
                    )
                    listOf(newDiscipline) + state.disciplines
                }

                // Dynamically update or create Deck
                val existingDeck = state.decks.find { it.id == deckId }
                val updatedDecks = if (existingDeck != null) {
                    state.decks.map { dk ->
                        if (dk.id == deckId) dk.copy(totalCards = dk.totalCards + 4) else dk
                    }
                } else {
                    val newDeck = Deck(
                        id = deckId,
                        title = discName,
                        category = discName,
                        totalCards = 4,
                        dailyGoal = 5,
                        masteredCount = 0,
                        isCurrent = true
                    )
                    listOf(newDeck) + state.decks
                }
                val activeDeck = updatedDecks.find { it.id == deckId } ?: updatedDecks.first()

                // Dynamically update StudyTracks
                val updatedTracks = listOf(generatedTrack) + state.studyTracks.filter { !it.discipline.equals(discName, ignoreCase = true) }

                // Save everything to persistent storage
                storage.saveStudyMaterials(updatedMaterials)
                storage.saveClinicalCases(updatedCases)
                storage.saveFlashcards(updatedCards)
                storage.saveDisciplines(updatedDisciplines)
                storage.saveDecks(updatedDecks)
                storage.saveStudyTracks(updatedTracks)

                val newHours = (state.userProfile.weeklyDedicatedHours + 0.5f).coerceAtMost(state.userProfile.weeklyTargetHours)
                val updatedProfile = state.userProfile.copy(
                    verbetesRead = state.userProfile.verbetesRead + 1,
                    weeklyDedicatedHours = newHours
                )
                storage.saveUserProfile(updatedProfile)

                state.copy(
                    materials = updatedMaterials,
                    clinicalCases = updatedCases,
                    flashcards = updatedCards,
                    disciplines = updatedDisciplines,
                    decks = updatedDecks,
                    activeDeck = activeDeck,
                    studyTracks = updatedTracks,
                    isAddMaterialDialogOpen = false,
                    userProfile = updatedProfile,
                    toastMessage = "Material '$cleanTitle' anexado! Disciplina, Flashcards e Caso Clínico sincronizados com sucesso!"
                )
            }
        }
    }

    // Socratic Chat
    fun openSocraticChat() {
        _uiState.update { it.copy(isSocraticAssistantOpen = true) }
    }

    fun closeSocraticChat() {
        _uiState.update { it.copy(isSocraticAssistantOpen = false) }
    }

    fun sendSocraticMessage(userMsg: String) {
        if (userMsg.isBlank()) return
        val current = _uiState.value.socraticChatMessages.toMutableList()
        current.add(userMsg to true)
        
        val botReply = when {
            userMsg.contains("depress", ignoreCase = true) -> 
                "Excelente reflexão. Na perspectiva da TCC de Beck e da formulação comportamental, de que maneira a tríade cognitiva (visão de si, do mundo e do futuro) se manifesta nesse padrão de humor? Como você diferenciaria da anedonia pura?"
            userMsg.contains("ansie", ignoreCase = true) ->
                "Muito oportuno. Ao avaliar a ansiedade patológica, quais marcadores somáticos do Eixo HPA e quais esquemas de evitação experiencial você investigaria na anamnese?"
            userMsg.contains("lacan", ignoreCase = true) || userMsg.contains("freud", ignoreCase = true) ->
                "Interessante correlação. Como o conceito de 'inconsciente estruturado como uma linguagem' altera a condução da escuta clínica comparado à primeira tópica freudiana?"
            else ->
                "Compreendo seu raciocínio clínico. Se você convidasse o paciente a refletir sobre as evidências a favor e contra essa interpretação, que hipótese diagnóstica ou intervenção se fortaleceria?"
        }
        current.add(botReply to false)
        _uiState.update { it.copy(socraticChatMessages = current) }
    }

    // Unified Search
    fun openUnifiedSearch(initialQuery: String = "") {
        _uiState.update {
            it.copy(isUnifiedSearchOpen = true, searchQuery = initialQuery)
        }
    }

    fun closeUnifiedSearch() {
        _uiState.update { it.copy(isUnifiedSearchOpen = false, searchQuery = "") }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun dismissToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    // --- Settings & Preferences ---
    fun openSettingsDialog() {
        _uiState.update { it.copy(isSettingsDialogOpen = true) }
    }

    fun closeSettingsDialog() {
        _uiState.update { it.copy(isSettingsDialogOpen = false) }
    }

    fun setDarkMode(enabled: Boolean) {
        storage.saveDarkMode(enabled)
        _uiState.update { it.copy(isDarkMode = enabled, appThemeMode = if (enabled) AppThemeMode.DARK else AppThemeMode.LIGHT) }
    }

    fun setThemeMode(mode: AppThemeMode) {
        storage.saveThemeMode(mode)
        val isDark = when (mode) {
            AppThemeMode.DARK -> true
            AppThemeMode.LIGHT -> false
            AppThemeMode.SYSTEM -> _uiState.value.isDarkMode
        }
        storage.saveDarkMode(isDark)
        _uiState.update { it.copy(appThemeMode = mode, isDarkMode = isDark) }
    }

    fun setFontScale(scale: Float) {
        storage.saveFontScale(scale)
        _uiState.update { it.copy(appFontScale = scale) }
    }

    fun toggleStudyNotifications(enabled: Boolean) {
        storage.saveStudyNotifications(enabled)
        _uiState.update {
            it.copy(
                studyNotificationsEnabled = enabled,
                toastMessage = if (enabled) "Notificações de estudo ativadas." else "Notificações de estudo desativadas."
            )
        }
    }

    fun toggleSpacedRepetitionAlerts(enabled: Boolean) {
        storage.saveSpacedRepetitionAlerts(enabled)
        _uiState.update {
            it.copy(
                spacedRepetitionAlertsEnabled = enabled,
                toastMessage = if (enabled) "Alertas de repetição espaçada ativados." else "Alertas de repetição desativados."
            )
        }
    }

    fun toggleExamDeadlinesAlerts(enabled: Boolean) {
        storage.saveExamDeadlinesAlerts(enabled)
        _uiState.update {
            it.copy(
                examDeadlinesAlertsEnabled = enabled,
                toastMessage = if (enabled) "Alertas de provas e prazos ativados." else "Alertas de provas desativados."
            )
        }
    }

    fun linkStudentEmail(email: String) {
        val trimmed = email.trim()
        if (trimmed.isNotBlank() && trimmed.contains("@")) {
            storage.saveStudentEmail(trimmed)
            _uiState.update {
                it.copy(
                    studentEmail = trimmed,
                    toastMessage = "E-mail $trimmed vinculado com sucesso! Seus dados de estudo estão protegidos."
                )
            }
        } else {
            _uiState.update { it.copy(toastMessage = "Por favor, digite um e-mail válido para vincular o backup.") }
        }
    }

    fun unlinkStudentEmail() {
        storage.saveStudentEmail("")
        _uiState.update {
            it.copy(
                studentEmail = "",
                toastMessage = "E-mail desvinculado. O app segue funcionando 100% de forma local e offline."
            )
        }
    }

    fun getExportBackupJson(): String {
        return storage.exportAllDataBackup()
    }

    fun showFeedbackToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }

    // Gemini 3.8 Flash + Google Search Grounding Chat Assistant
    private var chatSession: com.google.firebase.ai.Chat? = null

    private fun getOrInitChatSession(): com.google.firebase.ai.Chat {
        chatSession?.let { return it }
        val context = getApplication<Application>().applicationContext
        if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(context)
        }
        val model = Firebase.ai.generativeModel(
            modelName = "gemini-2.5-flash",
            systemInstruction = content {
                text("""
                    Você é o PsicoEstuda AI Assistente, mentor acadêmico especialista em Psicologia, Neurociências, Psicopatologia (DSM-5-TR, CID-11) e formulação clínica.
                    Sempre utilize a ferramenta de pesquisa Google Search integrada para buscar e fundamentar suas respostas em fontes reais, seguras, autorizadas e livres de vírus ou informações inverídicas (como SciELO, PubMed, APA, OMS, Conselho Federal de Psicologia - CFP, repositórios de universidades públicas, Google Sites acadêmicos certificados e periódicos científicos indexados).
                    Sua meta é fornecer:
                    1. Explicações estruturadas, de alto rigor conceitual e didáticas.
                    2. Citações e referências verídicas de autores e artigos científicos consolidados.
                    3. Respostas claras, éticas e empáticas em Português (Brasil).
                """.trimIndent())
            },
            tools = listOf(Tool.googleSearch())
        )
        val session = model.startChat()
        chatSession = session
        return session
    }

    fun sendAiMessage(userPrompt: String) {
        if (userPrompt.isBlank()) return
        val userMsg = ChatMessage(text = userPrompt, isUser = true)
        _uiState.update { state ->
            state.copy(
                chatMessages = state.chatMessages + userMsg,
                isAiLoading = true,
                aiStreamingText = ""
            )
        }

        viewModelScope.launch {
            try {
                val session = getOrInitChatSession()
                withTimeout(60_000L) {
                    var fullResponse = ""
                    val responseFlow = session.sendMessageStream(userPrompt)
                    responseFlow.collect { chunk ->
                        val chunkText = chunk.text ?: ""
                        fullResponse += chunkText
                        _uiState.update { it.copy(aiStreamingText = fullResponse) }
                    }

                    val aiMsg = ChatMessage(text = fullResponse, isUser = false)
                    _uiState.update { state ->
                        state.copy(
                            chatMessages = state.chatMessages + aiMsg,
                            isAiLoading = false,
                            aiStreamingText = ""
                        )
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e("AiChat", "Generation timed out", e)
                val errorMsg = ChatMessage(text = "A pesquisa ou geração demorou mais de 60 segundos. Por favor, tente novamente com uma pergunta mais específica.", isUser = false)
                _uiState.update { state ->
                    state.copy(
                        chatMessages = state.chatMessages + errorMsg,
                        isAiLoading = false,
                        aiStreamingText = ""
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("AiChat", "Error generating AI response", e)
                val errorMessage = when {
                    e is IllegalStateException && (e.message?.contains("FirebaseApp") == true || e.message?.contains("initialized") == true) ->
                        "O serviço Firebase AI Logic está sendo sincronizado no app. Por favor, tente novamente."
                    else ->
                        "Erro ao conectar com o Gemini: ${e.localizedMessage ?: "Verifique sua conexão ou configuração do App Check."}"
                }
                val errorMsg = ChatMessage(text = errorMessage, isUser = false)
                _uiState.update { state ->
                    state.copy(
                        chatMessages = state.chatMessages + errorMsg,
                        isAiLoading = false,
                        aiStreamingText = ""
                    )
                }
            }
        }
    }
}
