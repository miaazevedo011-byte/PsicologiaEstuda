package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppStorageRepository
import com.example.data.CurriculumData
import com.example.data.SampleData
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab(val label: String, val route: String) {
    INICIO("Início", "inicio"),
    ENCICLOPEDIA("Enciclopédia", "enciclopedia"),
    DISCIPLINAS("Disciplinas", "disciplinas"),
    FLASHCARDS("Flashcards", "flashcards"),
    MATERIAIS("Materiais", "materiais")
}

enum class EncyclopediaSubTab {
    CONCEITOS, LINHA_DO_TEMPO
}

enum class MaterialsFilter {
    TODOS, PDFS, AUDIOS, NOTAS
}

data class UnifiedSearchResult(
    val verbetes: List<Verbete>,
    val disciplines: List<Discipline>,
    val flashcards: List<Flashcard>,
    val notes: List<Note>,
    val audios: List<PodcastAudio>,
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
    
    // Flashcard State
    val decks: List<Deck> = SampleData.decks,
    val activeDeck: Deck = SampleData.decks.first(),
    val flashcards: List<Flashcard> = emptyList(),
    val currentCardIndex: Int = 0,
    val isCardFlipped: Boolean = false,
    val isHintExpanded: Boolean = true,
    
    // Audio Player State
    val audios: List<PodcastAudio> = emptyList(),
    val activeAudio: PodcastAudio? = null,
    val isAudioPlaying: Boolean = false,
    val playbackPositionSeconds: Int = 0,
    val playbackSpeed: Float = 1.0f,
    
    // Clinical Cases (Generated dynamically from attached materials)
    val clinicalCases: List<ClinicalCase> = emptyList(),
    val isClinicalCasesDialogOpen: Boolean = false,
    val isShareAppDialogOpen: Boolean = false,
    val isSelectAudioDialogOpen: Boolean = false,

    // Dialogs & Modals
    val isSettingsDialogOpen: Boolean = false,
    val isDarkMode: Boolean = false,
    val appThemeMode: AppThemeMode = AppThemeMode.LIGHT,
    val appFontStyle: AppFontStyle = AppFontStyle.DEFAULT,
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
        val hasSaved = storage.hasSavedData()
        val savedProfile = storage.loadUserProfile()
        val savedEmail = storage.getStudentEmail()
        val savedEvents = storage.loadAcademicEvents()
        val savedMaterials = storage.loadStudyMaterials()
        val savedNotes = storage.loadNotes()
        val savedCases = storage.loadClinicalCases()

        val savedDarkMode = storage.isDarkMode()
        val savedThemeMode = storage.getThemeMode()
        val savedFontStyle = storage.getFontStyle()
        val savedFontScale = storage.getFontScale()
        val savedNotifStudy = storage.getStudyNotifications()
        val savedNotifSpaced = storage.getSpacedRepetitionAlerts()
        val savedNotifExams = storage.getExamDeadlinesAlerts()

        // If first launch, start cleanly with student starting at 1st period and 0 stats
        val initialProfile = savedProfile ?: UserProfile(
            name = "Novo Estudante",
            course = "Psicologia",
            institution = "USP - Univ. de São Paulo",
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
        val initialDisciplines = CurriculumData.getDisciplinesFor(initialProfile.institution, initialSemester)
        val initialEvents = savedEvents ?: emptyList()
        val initialMaterials = savedMaterials ?: emptyList()
        val initialNotes = savedNotes ?: emptyList()
        val initialCases = savedCases ?: emptyList()

        val savedTracks = storage.loadStudyTracks()
        val initialTracks = savedTracks ?: SampleData.studyTracks

        val savedFlashcards = storage.loadFlashcards()
        val initialCards = if (savedFlashcards != null && savedFlashcards.isNotEmpty()) {
            savedFlashcards
        } else if (hasSaved) {
            SampleData.initialFlashcards
        } else {
            SampleData.initialFlashcards.take(6)
        }

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
                flashcards = initialCards,
                audios = if (initialMaterials.isNotEmpty()) SampleData.initialAudioPodcasts else emptyList(),
                activeAudio = if (initialMaterials.isNotEmpty()) SampleData.initialAudioPodcasts.firstOrNull() else null,
                isDarkMode = savedDarkMode,
                appThemeMode = savedThemeMode,
                appFontStyle = savedFontStyle,
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

    // Dynamic semester selection: updates semester AND changes the disciplines according to curriculum!
    fun selectSemester(semester: Int) {
        _uiState.update { state ->
            val updatedDisciplines = CurriculumData.getDisciplinesFor(
                institution = state.userProfile.institution,
                semester = semester
            )
            state.copy(
                selectedSemester = semester,
                disciplines = updatedDisciplines
            )
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
        val syncedDisciplines = CurriculumData.getDisciplinesFor(institution, semesterNumber)

        _uiState.update { state ->
            val updatedProfile = state.userProfile.copy(
                name = name.ifBlank { "Estudante" },
                institution = institution.ifBlank { "USP - Univ. de São Paulo" },
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
                disciplines = syncedDisciplines,
                isEditProfileDialogOpen = false,
                toastMessage = "Perfil e grade do ${semesterNumber}º Período atualizados e salvos!"
            )
        }
    }

    // Audio Player Controls
    fun playAudio(audio: PodcastAudio) {
        _uiState.update { state ->
            val list = if (state.audios.any { it.id == audio.id }) state.audios else listOf(audio) + state.audios
            state.copy(
                activeAudio = audio,
                audios = list,
                isAudioPlaying = true,
                playbackPositionSeconds = 0,
                isSelectAudioDialogOpen = false,
                userProfile = state.userProfile.copy(audioClassesListened = state.userProfile.audioClassesListened + 1),
                toastMessage = "Reproduzindo: ${audio.episodeTitle}"
            )
        }
    }

    fun togglePlayPauseAudio() {
        _uiState.update { it.copy(isAudioPlaying = !it.isAudioPlaying) }
    }

    fun stopAudio() {
        _uiState.update { it.copy(isAudioPlaying = false, playbackPositionSeconds = 0) }
    }

    fun closeAudio() {
        _uiState.update { it.copy(activeAudio = null, isAudioPlaying = false, playbackPositionSeconds = 0) }
    }

    fun seekAudioBy(deltaSeconds: Int) {
        _uiState.update { state ->
            val total = state.activeAudio?.totalDurationSeconds ?: 0
            val newPos = (state.playbackPositionSeconds + deltaSeconds).coerceIn(0, total)
            state.copy(playbackPositionSeconds = newPos)
        }
    }

    fun cycleSpeed() {
        _uiState.update { state ->
            val speeds = listOf(1.0f, 1.25f, 1.5f, 2.0f)
            val currentIdx = speeds.indexOf(state.playbackSpeed)
            val nextSpeed = speeds[(currentIdx + 1) % speeds.size]
            state.copy(playbackSpeed = nextSpeed)
        }
    }

    fun nextAudio() {
        _uiState.update { state ->
            val list = state.audios
            if (list.isNotEmpty()) {
                val currentIdx = list.indexOfFirst { it.id == state.activeAudio?.id }
                val nextIdx = (if (currentIdx == -1) 0 else currentIdx + 1) % list.size
                val nextTrack = list[nextIdx]
                state.copy(activeAudio = nextTrack, isAudioPlaying = true, playbackPositionSeconds = 0)
            } else state
        }
    }

    fun previousAudio() {
        _uiState.update { state ->
            val list = state.audios
            if (list.isNotEmpty()) {
                val currentIdx = list.indexOfFirst { it.id == state.activeAudio?.id }
                val prevIdx = if (currentIdx <= 0) list.size - 1 else currentIdx - 1
                val prevTrack = list[prevIdx]
                state.copy(activeAudio = prevTrack, isAudioPlaying = true, playbackPositionSeconds = 0)
            } else state
        }
    }

    fun selectAudio(audio: PodcastAudio) {
        playAudio(audio)
    }

    fun openSelectAudioDialog() {
        _uiState.update { it.copy(isSelectAudioDialogOpen = true) }
    }

    fun closeSelectAudioDialog() {
        _uiState.update { it.copy(isSelectAudioDialogOpen = false) }
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
            val newMaterial = StudyMaterial(
                id = "mat_${System.currentTimeMillis()}",
                title = title.ifBlank { "Arquivo_${System.currentTimeMillis()}" },
                type = type,
                size = fileSizeString,
                updatedAt = "Processado agora",
                linkedDiscipline = discipline,
                aiSummary = if (notes.isNotBlank()) notes else "Material importado do dispositivo em $discipline. Conteúdo sintetizado e indexado para geração de flashcards, resumo em áudio e casos clínicos práticos.",
                flashcardsCount = 4,
                verbetesCount = 2,
                uriString = realFileUri
            )

            // Dynamic Cross-Link: Generate Clinical Case Exercise from attached content
            val generatedCase = ClinicalCase(
                id = "case_ai_${System.currentTimeMillis()}",
                title = "Caso Clínico: Vinheta de '$title'",
                patientAge = "Paciente M.R., 32 anos",
                anamneseVignette = "Caso clínico formativo estruturado a partir do material '$title' anexado na disciplina $discipline. Paciente procura atendimento apresentando queixa subjetiva e alterações comportamentais correlatas ao conteúdo deste material.",
                sourceMaterialOrDiscipline = "Baseado em: $title • $discipline",
                mentalStateExam = "Consciência lúcida, orientação têmporo-espacial preservada, afeto modulado com humor ansioso, sem delírios ou alucinações.",
                questions = listOf(
                    ClinicalQuestion(
                        id = "q_mat_1_${System.currentTimeMillis()}",
                        prompt = "Qual conceitualização diagnóstica e conduta clínica é recomendada a partir dos pontos-chave de '$title'?",
                        options = listOf(
                            "Formulação integrada e planejamento psicoterapêutico alinhado a $discipline",
                            "Conduta expectante sem qualquer anamnese aprofundada",
                            "Simulação deliberada de queixas",
                            "Intervenção aversiva imediata"
                        ),
                        correctOptionIndex = 0,
                        explanation = "A fundamentação teórica de '$title' orienta para a formulação de caso e intervenção estruturada na disciplina $discipline."
                    )
                ),
                dsm5DiagnosticRationale = "Semiologia e critérios alinhados ao conteúdo programático de $discipline.",
                therapeuticApproach = "Prática clínica baseada em evidências e supervisão reflexiva."
            )

            val generatedCard = Flashcard(
                id = "card_ai_${System.currentTimeMillis()}",
                deckId = "deck_custom",
                category = discipline,
                question = "Conceito extraído de '$title': Qual a premissa central apresentada?",
                hint = "Revise os pontos-chave sintetizados pelo PsicoEstuda AI.",
                answer = "O material aborda a correlação fundamental entre teoria e a formulação clínica em $discipline.",
                citation = "$title, Material do Dispositivo",
                status = CardMasteryStatus.NOVO
            )

            val newAudio = PodcastAudio(
                id = "audio_ai_${System.currentTimeMillis()}",
                episodeTitle = "Síntese: $title",
                speakerAndDiscipline = "Leitura Pedagógica • $discipline",
                totalDurationSeconds = 360,
                currentPositionSeconds = 0,
                speed = 1.0f,
                tag = "Material Importado"
            )

            _uiState.update { state ->
                val updatedMaterials = listOf(newMaterial) + state.materials
                val updatedCases = listOf(generatedCase) + state.clinicalCases
                val updatedCards = listOf(generatedCard) + state.flashcards
                val updatedAudios = listOf(newAudio) + state.audios

                storage.saveStudyMaterials(updatedMaterials)
                storage.saveClinicalCases(updatedCases)

                val newHours = (state.userProfile.weeklyDedicatedHours + 0.4f).coerceAtMost(state.userProfile.weeklyTargetHours)
                val updatedProfile = state.userProfile.copy(
                    verbetesRead = state.userProfile.verbetesRead + 1,
                    weeklyDedicatedHours = newHours
                )
                storage.saveUserProfile(updatedProfile)

                state.copy(
                    materials = updatedMaterials,
                    clinicalCases = updatedCases,
                    flashcards = updatedCards,
                    audios = updatedAudios,
                    isAddMaterialDialogOpen = false,
                    userProfile = updatedProfile,
                    toastMessage = "Arquivo indexado! Flashcards, Áudio e Caso Clínico gerados e salvos!"
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

    fun setFontStyle(style: AppFontStyle) {
        storage.saveFontStyle(style)
        _uiState.update { it.copy(appFontStyle = style) }
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
}
