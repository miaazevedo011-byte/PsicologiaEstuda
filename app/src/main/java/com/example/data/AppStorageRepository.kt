package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

class AppStorageRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("psicoestuda_user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_PROFILE = "key_user_profile"
        private const val KEY_ACADEMIC_EVENTS = "key_academic_events"
        private const val KEY_MATERIALS = "key_materials"
        private const val KEY_NOTES = "key_notes"
        private const val KEY_FLASHCARDS = "key_flashcards"
        private const val KEY_CLINICAL_CASES = "key_clinical_cases"
        private const val KEY_STUDENT_EMAIL = "key_student_email"
        private const val KEY_STUDY_TRACKS = "key_study_tracks"
        private const val KEY_HAS_INITIALIZED = "key_has_initialized"
        private const val KEY_IS_DARK_MODE = "key_is_dark_mode"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_FONT_STYLE = "key_font_style"
        private const val KEY_FONT_SCALE = "key_font_scale"
        private const val KEY_NOTIF_STUDY = "key_notif_study"
        private const val KEY_NOTIF_SPACED = "key_notif_spaced"
        private const val KEY_NOTIF_EXAMS = "key_notif_exams"
    }

    fun hasSavedData(): Boolean {
        return prefs.getBoolean(KEY_HAS_INITIALIZED, false)
    }

    fun saveStudentEmail(email: String) {
        prefs.edit().putString(KEY_STUDENT_EMAIL, email).apply()
    }

    fun getStudentEmail(): String {
        return prefs.getString(KEY_STUDENT_EMAIL, "") ?: ""
    }

    // --- Settings Preferences ---
    fun saveDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DARK_MODE, enabled).apply()
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean(KEY_IS_DARK_MODE, false)
    }

    fun saveThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun getThemeMode(): AppThemeMode {
        val name = prefs.getString(KEY_THEME_MODE, AppThemeMode.LIGHT.name) ?: AppThemeMode.LIGHT.name
        return try { AppThemeMode.valueOf(name) } catch (e: Exception) { AppThemeMode.LIGHT }
    }

    fun saveFontStyle(style: AppFontStyle) {
        prefs.edit().putString(KEY_FONT_STYLE, style.name).apply()
    }

    fun getFontStyle(): AppFontStyle {
        val name = prefs.getString(KEY_FONT_STYLE, AppFontStyle.DEFAULT.name) ?: AppFontStyle.DEFAULT.name
        return try { AppFontStyle.valueOf(name) } catch (e: Exception) { AppFontStyle.DEFAULT }
    }

    fun saveFontScale(scale: Float) {
        prefs.edit().putFloat(KEY_FONT_SCALE, scale).apply()
    }

    fun getFontScale(): Float {
        return prefs.getFloat(KEY_FONT_SCALE, 1.0f)
    }

    fun saveStudyNotifications(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIF_STUDY, enabled).apply()
    }

    fun getStudyNotifications(): Boolean {
        return prefs.getBoolean(KEY_NOTIF_STUDY, true)
    }

    fun saveSpacedRepetitionAlerts(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIF_SPACED, enabled).apply()
    }

    fun getSpacedRepetitionAlerts(): Boolean {
        return prefs.getBoolean(KEY_NOTIF_SPACED, true)
    }

    fun saveExamDeadlinesAlerts(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIF_EXAMS, enabled).apply()
    }

    fun getExamDeadlinesAlerts(): Boolean {
        return prefs.getBoolean(KEY_NOTIF_EXAMS, true)
    }

    fun exportAllDataBackup(): String {
        val backup = JSONObject().apply {
            put("app", "PsicoEstuda AI")
            put("exportedAt", System.currentTimeMillis())
            put("studentEmail", getStudentEmail())
            put("userProfile", prefs.getString(KEY_USER_PROFILE, "{}"))
            put("materials", prefs.getString(KEY_MATERIALS, "[]"))
            put("notes", prefs.getString(KEY_NOTES, "[]"))
            put("flashcards", prefs.getString(KEY_FLASHCARDS, "[]"))
            put("clinicalCases", prefs.getString(KEY_CLINICAL_CASES, "[]"))
            put("events", prefs.getString(KEY_ACADEMIC_EVENTS, "[]"))
        }
        return backup.toString(2)
    }

    // --- User Profile ---
    fun saveUserProfile(profile: UserProfile) {
        val json = JSONObject().apply {
            put("name", profile.name)
            put("course", profile.course)
            put("institution", profile.institution)
            put("semester", profile.semester)
            put("semesterNumber", profile.semesterNumber)
            put("shift", profile.shift)
            put("currentWeek", profile.currentWeek)
            put("totalWeeks", profile.totalWeeks)
            put("weeklyDedicatedHours", profile.weeklyDedicatedHours.toDouble())
            put("weeklyTargetHours", profile.weeklyTargetHours.toDouble())
            put("verbetesRead", profile.verbetesRead)
            put("cardsMastered", profile.cardsMastered)
            put("audioClassesListened", profile.audioClassesListened)
        }
        prefs.edit()
            .putString(KEY_USER_PROFILE, json.toString())
            .putBoolean(KEY_HAS_INITIALIZED, true)
            .apply()
    }

    fun loadUserProfile(): UserProfile? {
        val jsonStr = prefs.getString(KEY_USER_PROFILE, null) ?: return null
        return try {
            val json = JSONObject(jsonStr)
            UserProfile(
                name = json.optString("name", "Estudante"),
                course = json.optString("course", "Psicologia"),
                institution = json.optString("institution", "USP - Univ. de São Paulo"),
                semester = json.optString("semester", "1º Período"),
                semesterNumber = json.optInt("semesterNumber", 1),
                shift = json.optString("shift", "Matutino"),
                currentWeek = json.optInt("currentWeek", 1),
                totalWeeks = json.optInt("totalWeeks", 18),
                weeklyDedicatedHours = json.optDouble("weeklyDedicatedHours", 0.0).toFloat(),
                weeklyTargetHours = json.optDouble("weeklyTargetHours", 10.0).toFloat(),
                verbetesRead = json.optInt("verbetesRead", 0),
                cardsMastered = json.optInt("cardsMastered", 0),
                audioClassesListened = json.optInt("audioClassesListened", 0)
            )
        } catch (e: Exception) {
            null
        }
    }

    // --- Academic Events ---
    fun saveAcademicEvents(events: List<AcademicEvent>) {
        val array = JSONArray()
        for (evt in events) {
            val obj = JSONObject().apply {
                put("id", evt.id)
                put("title", evt.title)
                put("type", evt.type.name)
                put("dateDisplay", evt.dateDisplay)
                put("timeDisplay", evt.timeDisplay)
                put("location", evt.location)
                put("daysRemaining", evt.daysRemaining)
                put("discipline", evt.discipline)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_ACADEMIC_EVENTS, array.toString()).apply()
    }

    fun loadAcademicEvents(): List<AcademicEvent>? {
        val jsonStr = prefs.getString(KEY_ACADEMIC_EVENTS, null) ?: return null
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<AcademicEvent>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val typeStr = obj.optString("type", "PROVA")
                val type = try { EventType.valueOf(typeStr) } catch (e: Exception) { EventType.PROVA }
                list.add(
                    AcademicEvent(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        type = type,
                        dateDisplay = obj.getString("dateDisplay"),
                        timeDisplay = obj.getString("timeDisplay"),
                        location = obj.getString("location"),
                        daysRemaining = obj.optInt("daysRemaining", 0),
                        discipline = obj.getString("discipline")
                    )
                )
            }
            list
        } catch (e: Exception) {
            null
        }
    }

    // --- Study Materials ---
    fun saveStudyMaterials(materials: List<StudyMaterial>) {
        val array = JSONArray()
        for (mat in materials) {
            val obj = JSONObject().apply {
                put("id", mat.id)
                put("title", mat.title)
                put("type", mat.type.name)
                put("size", mat.size)
                put("updatedAt", mat.updatedAt)
                put("linkedDiscipline", mat.linkedDiscipline)
                put("aiSummary", mat.aiSummary)
                put("flashcardsCount", mat.flashcardsCount)
                put("verbetesCount", mat.verbetesCount)
                put("uriString", mat.uriString ?: "")
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_MATERIALS, array.toString()).apply()
    }

    fun loadStudyMaterials(): List<StudyMaterial>? {
        val jsonStr = prefs.getString(KEY_MATERIALS, null) ?: return null
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<StudyMaterial>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val typeStr = obj.optString("type", "PDF")
                val type = try { MaterialType.valueOf(typeStr) } catch (e: Exception) { MaterialType.PDF }
                list.add(
                    StudyMaterial(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        type = type,
                        size = obj.getString("size"),
                        updatedAt = obj.getString("updatedAt"),
                        linkedDiscipline = obj.getString("linkedDiscipline"),
                        aiSummary = obj.getString("aiSummary"),
                        flashcardsCount = obj.optInt("flashcardsCount", 4),
                        verbetesCount = obj.optInt("verbetesCount", 2),
                        uriString = obj.optString("uriString", "").ifBlank { null }
                    )
                )
            }
            list
        } catch (e: Exception) {
            null
        }
    }

    // --- Flashcards ---
    fun saveFlashcards(flashcards: List<Flashcard>) {
        val array = JSONArray()
        for (f in flashcards) {
            val obj = JSONObject().apply {
                put("id", f.id)
                put("deckId", f.deckId)
                put("category", f.category)
                put("question", f.question)
                put("hint", f.hint)
                put("answer", f.answer)
                put("citation", f.citation)
                put("status", f.status.name)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_FLASHCARDS, array.toString()).apply()
    }

    fun loadFlashcards(): List<Flashcard>? {
        val jsonStr = prefs.getString(KEY_FLASHCARDS, null) ?: return null
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<Flashcard>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val statusStr = obj.optString("status", "NOVO")
                val status = try { CardMasteryStatus.valueOf(statusStr) } catch (e: Exception) { CardMasteryStatus.NOVO }
                list.add(
                    Flashcard(
                        id = obj.getString("id"),
                        deckId = obj.getString("deckId"),
                        category = obj.getString("category"),
                        question = obj.getString("question"),
                        hint = obj.getString("hint"),
                        answer = obj.getString("answer"),
                        citation = obj.getString("citation"),
                        status = status
                    )
                )
            }
            list
        } catch (e: Exception) {
            null
        }
    }

    // --- Notes ---
    fun saveNotes(notes: List<Note>) {
        val array = JSONArray()
        for (note in notes) {
            val obj = JSONObject().apply {
                put("id", note.id)
                put("title", note.title)
                put("timeAgo", note.timeAgo)
                put("content", note.content)
                put("linkedTopic", note.linkedTopic)
                put("discipline", note.discipline)
                put("tags", JSONArray(note.tags))
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_NOTES, array.toString()).apply()
    }

    fun loadNotes(): List<Note>? {
        val jsonStr = prefs.getString(KEY_NOTES, null) ?: return null
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<Note>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val tagsArray = obj.optJSONArray("tags")
                val tagsList = mutableListOf<String>()
                if (tagsArray != null) {
                    for (t in 0 until tagsArray.length()) {
                        tagsList.add(tagsArray.getString(t))
                    }
                }
                list.add(
                    Note(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        timeAgo = obj.getString("timeAgo"),
                        content = obj.getString("content"),
                        linkedTopic = obj.getString("linkedTopic"),
                        discipline = obj.getString("discipline"),
                        tags = tagsList
                    )
                )
            }
            list
        } catch (e: Exception) {
            null
        }
    }

    // --- Clinical Cases ---
    fun saveClinicalCases(cases: List<ClinicalCase>) {
        val array = JSONArray()
        for (c in cases) {
            val obj = JSONObject().apply {
                put("id", c.id)
                put("title", c.title)
                put("patientAge", c.patientAge)
                put("anamneseVignette", c.anamneseVignette)
                put("sourceMaterialOrDiscipline", c.sourceMaterialOrDiscipline)
                put("mentalStateExam", c.mentalStateExam)
                put("dsm5DiagnosticRationale", c.dsm5DiagnosticRationale)
                put("therapeuticApproach", c.therapeuticApproach)

                val questionsArray = JSONArray()
                for (q in c.questions) {
                    val qObj = JSONObject().apply {
                        put("id", q.id)
                        put("prompt", q.prompt)
                        put("options", JSONArray(q.options))
                        put("correctOptionIndex", q.correctOptionIndex)
                        put("selectedOptionIndex", q.selectedOptionIndex ?: -1)
                        put("explanation", q.explanation)
                        put("isAnswered", q.isAnswered)
                    }
                    questionsArray.put(qObj)
                }
                put("questions", questionsArray)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_CLINICAL_CASES, array.toString()).apply()
    }

    fun loadClinicalCases(): List<ClinicalCase>? {
        val jsonStr = prefs.getString(KEY_CLINICAL_CASES, null) ?: return null
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<ClinicalCase>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val questionsArray = obj.optJSONArray("questions")
                val questionsList = mutableListOf<ClinicalQuestion>()
                if (questionsArray != null) {
                    for (q in 0 until questionsArray.length()) {
                        val qObj = questionsArray.getJSONObject(q)
                        val optArray = qObj.getJSONArray("options")
                        val optList = mutableListOf<String>()
                        for (o in 0 until optArray.length()) {
                            optList.add(optArray.getString(o))
                        }
                        val selIdx = qObj.optInt("selectedOptionIndex", -1)
                        questionsList.add(
                            ClinicalQuestion(
                                id = qObj.getString("id"),
                                prompt = qObj.getString("prompt"),
                                options = optList,
                                correctOptionIndex = qObj.getInt("correctOptionIndex"),
                                selectedOptionIndex = if (selIdx >= 0) selIdx else null,
                                explanation = qObj.getString("explanation"),
                                isAnswered = qObj.optBoolean("isAnswered", false)
                            )
                        )
                    }
                }
                list.add(
                    ClinicalCase(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        patientAge = obj.getString("patientAge"),
                        anamneseVignette = obj.getString("anamneseVignette"),
                        sourceMaterialOrDiscipline = obj.getString("sourceMaterialOrDiscipline"),
                        mentalStateExam = obj.getString("mentalStateExam"),
                        questions = questionsList,
                        dsm5DiagnosticRationale = obj.getString("dsm5DiagnosticRationale"),
                        therapeuticApproach = obj.getString("therapeuticApproach")
                    )
                )
            }
            list
        } catch (e: Exception) {
            null
        }
    }

    // --- Study Tracks (including suspended themes) ---
    fun saveStudyTracks(tracks: List<StudyTrack>) {
        val array = JSONArray()
        for (t in tracks) {
            val obj = JSONObject().apply {
                put("id", t.id)
                put("title", t.title)
                put("subtitle", t.subtitle)
                put("discipline", t.discipline)
                put("durationMin", t.durationMin)
                put("tag", t.tag)
                put("colorType", t.colorType)
                put("isSuspended", t.isSuspended)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_STUDY_TRACKS, array.toString()).apply()
    }

    fun loadStudyTracks(): List<StudyTrack>? {
        val jsonStr = prefs.getString(KEY_STUDY_TRACKS, null) ?: return null
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<StudyTrack>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    StudyTrack(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        subtitle = obj.getString("subtitle"),
                        discipline = obj.getString("discipline"),
                        durationMin = obj.optInt("durationMin", 15),
                        tag = obj.getString("tag"),
                        colorType = obj.optString("colorType", "primary"),
                        isSuspended = obj.optBoolean("isSuspended", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            null
        }
    }
}
