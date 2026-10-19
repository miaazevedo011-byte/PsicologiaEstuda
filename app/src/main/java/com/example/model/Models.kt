package com.example.model

enum class EventType {
    PROVA, RELATORIO, APRESENTACAO, TESTE, FERIADO, OUTRO
}

enum class CardMasteryStatus {
    DIFICIL, BOM, DOMINADO, NOVO
}

enum class MaterialType {
    PDF, DOCX, AUDIO, LINK, IMAGEM
}

enum class AppFontStyle(val label: String) {
    DEFAULT("Padrão (Inter / Sans-Serif)"),
    SERIF("Serifada Acadêmica (Didática)"),
    MONOSPACE("Estruturada (Monoespaçada)")
}

enum class AppThemeMode(val label: String) {
    LIGHT("Claro"),
    DARK("Escuro"),
    SYSTEM("Automático (Sistema)")
}

data class UserProfile(
    val name: String = "Estudante",
    val course: String = "Psicologia",
    val institution: String = "Universidade de São Paulo (USP)",
    val semester: String = "1º Período",
    val semesterNumber: Int = 1,
    val shift: String = "Matutino",
    val currentWeek: Int = 1,
    val totalWeeks: Int = 18,
    val weeklyDedicatedHours: Float = 4.5f,
    val weeklyTargetHours: Float = 20.0f,
    val verbetesRead: Int = 6,
    val cardsMastered: Int = 12,
    val audioClassesListened: Int = 1
)

data class AcademicEvent(
    val id: String,
    val title: String,
    val type: EventType,
    val dateDisplay: String,
    val timeDisplay: String,
    val location: String,
    val daysRemaining: Int,
    val discipline: String
)

data class StudyTrack(
    val id: String,
    val title: String,
    val subtitle: String,
    val discipline: String,
    val durationMin: Int,
    val tag: String,
    val colorType: String = "primary", // "primary", "secondary", "tertiary", "amber"
    val isSuspended: Boolean = false
)

data class Author(
    val id: String,
    val name: String,
    val dates: String,
    val school: String,
    val bio: String,
    val verbetesCount: Int,
    val audiosCount: Int,
    val initial: String
)

data class Verbete(
    val id: String,
    val title: String,
    val subtitle: String,
    val school: String,
    val authors: String,
    val summary: String,
    val microPills: List<Pair<String, String>>,
    val flashcardsCount: Int,
    val audioDuration: String,
    val audioTopicKey: String,
    val linkedDiscipline: String,
    val isBookmarked: Boolean = false
)

data class TimelineMilestone(
    val year: String,
    val badge: String,
    val title: String,
    val description: String,
    val keyWork: String,
    val linkedDiscipline: String
)

data class Discipline(
    val id: String,
    val name: String,
    val code: String,
    val professor: String,
    val progressPercent: Int,
    val currentModule: Int,
    val totalModules: Int,
    val syllabusSummary: String,
    val learningObjectives: List<Pair<String, Boolean>>,
    val materialsCount: Int,
    val flashcardsCount: Int,
    val audioDurationMin: Int,
    val semester: Int = 5,
    val isCompleted: Boolean = false
)

data class GlossaryTerm(
    val id: String,
    val term: String,
    val category: String,
    val phonetic: String,
    val wordClass: String,
    val definition: String,
    val clinicalDifferential: String,
    val linkedLesson: String,
    val polarDiagram: List<Triple<String, String, String>>? = null
)

data class Flashcard(
    val id: String,
    val deckId: String,
    val category: String,
    val question: String,
    val hint: String,
    val answer: String,
    val citation: String,
    val status: CardMasteryStatus = CardMasteryStatus.NOVO
)

data class Deck(
    val id: String,
    val title: String,
    val category: String,
    val totalCards: Int,
    val dailyGoal: Int,
    val masteredCount: Int,
    val isCurrent: Boolean = false
)

data class Note(
    val id: String,
    val title: String,
    val timeAgo: String,
    val content: String,
    val linkedTopic: String,
    val discipline: String,
    val tags: List<String> = emptyList()
)

data class PodcastAudio(
    val id: String,
    val episodeTitle: String,
    val speakerAndDiscipline: String,
    val totalDurationSeconds: Int,
    val currentPositionSeconds: Int,
    val isPlaying: Boolean = false,
    val speed: Float = 1.0f,
    val tag: String = "PsicoCast Acadêmico"
)

data class StudyMaterial(
    val id: String,
    val title: String,
    val type: MaterialType,
    val size: String,
    val updatedAt: String,
    val highlightsCount: Int = 0,
    val linkedNotesCount: Int = 0,
    val linkedDiscipline: String,
    val flashcardsCount: Int = 4,
    val verbetesCount: Int = 2,
    val aiSummary: String,
    val isAudioTranscribed: Boolean = false,
    val uriString: String? = null
)

data class ClinicalQuestion(
    val id: String,
    val prompt: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val selectedOptionIndex: Int? = null,
    val isAnswered: Boolean = false
)

data class ClinicalCase(
    val id: String,
    val title: String,
    val patientAge: String,
    val anamneseVignette: String,
    val sourceMaterialOrDiscipline: String,
    val mentalStateExam: String,
    val questions: List<ClinicalQuestion>,
    val dsm5DiagnosticRationale: String,
    val therapeuticApproach: String
)
