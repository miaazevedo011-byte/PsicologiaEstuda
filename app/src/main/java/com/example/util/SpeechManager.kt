package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _spokenText = MutableStateFlow("")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _currentTitle = MutableStateFlow("")
    val currentTitle: StateFlow<String> = _currentTitle.asStateFlow()

    private val _activeItemId = MutableStateFlow<String?>(null)
    val activeItemId: StateFlow<String?> = _activeItemId.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private var pendingText: String? = null
    private var pendingTitle: String? = null
    private var pendingId: String? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("pt", "BR"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.getDefault())
            }
            tts?.setSpeechRate(_speechRate.value)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _activeItemId.value = null
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _activeItemId.value = null
                }
            })
            isInitialized = true

            val pt = pendingText
            if (!pt.isNullOrBlank()) {
                speak(pendingId ?: "pending", pendingTitle ?: "Áudio de Estudo", pt)
                pendingText = null
                pendingTitle = null
                pendingId = null
            }
        } else {
            Log.e("SpeechManager", "TTS initialization failed with status $status")
        }
    }

    fun speak(id: String, title: String, text: String) {
        if (text.isBlank()) return
        _spokenText.value = text
        _currentTitle.value = title
        _activeItemId.value = id

        if (!isInitialized) {
            pendingText = text
            pendingTitle = title
            pendingId = id
            return
        }

        tts?.stop()
        tts?.setSpeechRate(_speechRate.value)
        val utteranceId = "utterance_${id}_${System.currentTimeMillis()}"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        _isSpeaking.value = true
    }

    fun speak(text: String) {
        speak("generic_${System.currentTimeMillis()}", "Leitura em Áudio", text)
    }

    fun togglePlayPause(id: String, title: String, text: String) {
        if (_isSpeaking.value && _activeItemId.value == id) {
            stop()
        } else {
            speak(id, title, text)
        }
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        _activeItemId.value = null
    }

    fun setSpeed(speed: Float) {
        _speechRate.value = speed
        tts?.setSpeechRate(speed)
        // If currently speaking, re-speak current text with new speed
        if (_isSpeaking.value && _spokenText.value.isNotBlank()) {
            val curId = _activeItemId.value ?: "current"
            val curTitle = _currentTitle.value
            val curText = _spokenText.value
            val utteranceId = "utterance_${curId}_${System.currentTimeMillis()}"
            tts?.speak(curText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        _isSpeaking.value = false
        _activeItemId.value = null
    }
}

