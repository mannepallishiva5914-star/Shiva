package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.VideoProjectEntity
import com.example.data.model.CaptionBgStyle
import com.example.data.model.CaptionFont
import com.example.data.model.CaptionSegment
import com.example.data.model.CaptionStyle
import com.example.data.repository.CaptionProjectRepository
import com.example.engine.SupportedLanguage
import com.example.engine.TransliterationEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CaptionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CaptionProjectRepository =
        CaptionProjectRepository(AppDatabase.getInstance(application).projectDao())

    val allProjects: StateFlow<List<VideoProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Project State
    private val _currentProjectId = MutableStateFlow<String>("sample_vid_action_2025")
    val currentProjectId: StateFlow<String> = _currentProjectId.asStateFlow()

    private val _projectTitle = MutableStateFlow("VID_20250512_ACTION.mp4")
    val projectTitle: StateFlow<String> = _projectTitle.asStateFlow()

    private val _videoUri = MutableStateFlow<String?>(null)
    val videoUri: StateFlow<String?> = _videoUri.asStateFlow()

    private val _durationMs = MutableStateFlow(48200L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(6200L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _audioChannels = MutableStateFlow("2-CH PCM")
    val audioChannels: StateFlow<String> = _audioChannels.asStateFlow()

    private val _sampleRate = MutableStateFlow("48.0 kHz")
    val sampleRate: StateFlow<String> = _sampleRate.asStateFlow()

    private val _vocalBandStatus = MutableStateFlow("CLEAN VOCAL BAND")
    val vocalBandStatus: StateFlow<String> = _vocalBandStatus.asStateFlow()

    // Language & Matrix state
    private val _selectedLanguage = MutableStateFlow(TransliterationEngine.getLanguageByCode("te"))
    val selectedLanguage: StateFlow<SupportedLanguage> = _selectedLanguage.asStateFlow()

    private val _confidenceScore = MutableStateFlow(0.984f)
    val confidenceScore: StateFlow<Float> = _confidenceScore.asStateFlow()

    // Captions & Styling
    private val _captions = MutableStateFlow<List<CaptionSegment>>(emptyList())
    val captions: StateFlow<List<CaptionSegment>> = _captions.asStateFlow()

    private val _captionStyle = MutableStateFlow(CaptionStyle())
    val captionStyle: StateFlow<CaptionStyle> = _captionStyle.asStateFlow()

    // Generation / Processing state
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationProgress = MutableStateFlow(0f)
    val generationProgress: StateFlow<Float> = _generationProgress.asStateFlow()

    private val _generationStatusMessage = MutableStateFlow("")
    val generationStatusMessage: StateFlow<String> = _generationStatusMessage.asStateFlow()

    private var playbackJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializeDefaultSampleIfEmpty()
            loadProject("sample_vid_action_2025")
        }
    }

    fun loadProject(id: String) {
        viewModelScope.launch {
            val proj = repository.getProject(id)
            if (proj != null) {
                _currentProjectId.value = proj.id
                _projectTitle.value = proj.title
                _videoUri.value = proj.videoUri
                _durationMs.value = proj.durationMs
                _confidenceScore.value = proj.confidenceScore
                _audioChannels.value = proj.audioChannels
                _sampleRate.value = proj.sampleRate
                _vocalBandStatus.value = proj.vocalBandStatus

                val lang = TransliterationEngine.getLanguageByCode(proj.languageCode)
                _selectedLanguage.value = lang

                val parsedCaptions = CaptionSegment.parseList(proj.captionsJson)
                _captions.value = parsedCaptions
                _captionStyle.value = CaptionStyle.fromJson(proj.styleJson)
                _currentPositionMs.value = parsedCaptions.getOrNull(1)?.startMs ?: 0L
            }
        }
    }

    fun switchLanguage(language: SupportedLanguage) {
        _selectedLanguage.value = language
        _confidenceScore.value = language.defaultConfidence
    }

    fun togglePlayback() {
        if (_isPlaying.value) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    fun startPlayback() {
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (_isPlaying.value) {
                delay(50)
                val next = _currentPositionMs.value + 50
                if (next >= _durationMs.value) {
                    _currentPositionMs.value = 0L
                } else {
                    _currentPositionMs.value = next
                }
            }
        }
    }

    fun pausePlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
    }

    fun seekTo(positionMs: Long) {
        _currentPositionMs.value = positionMs.coerceIn(0L, _durationMs.value)
    }

    fun jumpRelative(deltaMs: Long) {
        seekTo(_currentPositionMs.value + deltaMs)
    }

    fun generateOfflineCaptions(onComplete: () -> Unit = {}) {
        if (_isGenerating.value) return
        _isGenerating.value = true
        _generationProgress.value = 0.05f
        _generationStatusMessage.value = "Acoustic Vocal Separation (Fast-Int8)..."

        viewModelScope.launch {
            delay(400)
            _generationProgress.value = 0.30f
            _generationStatusMessage.value = "Phoneme Extraction & Sound Matrix Analysis..."

            delay(500)
            _generationProgress.value = 0.65f
            _generationStatusMessage.value = "Sound-Preserving Latin Transliteration..."

            delay(400)
            _generationProgress.value = 0.90f
            _generationStatusMessage.value = "Synchronizing millisecond caption offsets..."

            delay(300)
            val generated = TransliterationEngine.generateAcousticCaptions(
                languageCode = _selectedLanguage.value.code,
                durationMs = _durationMs.value
            )
            _captions.value = generated
            _generationProgress.value = 1.0f
            _generationStatusMessage.value = "Completed!"
            delay(200)
            _isGenerating.value = false

            saveCurrentProject()
            onComplete()
        }
    }

    fun updateCaptionText(id: String, newText: String) {
        _captions.value = _captions.value.map { seg ->
            if (seg.id == id) seg.copy(phoneticText = newText) else seg
        }
        saveCurrentProject()
    }

    fun updateCaptionTiming(id: String, startMs: Long, endMs: Long) {
        _captions.value = _captions.value.map { seg ->
            if (seg.id == id) seg.copy(startMs = startMs, endMs = endMs) else seg
        }.sortedBy { it.startMs }
        saveCurrentProject()
    }

    fun regenerateCaption(id: String) {
        _captions.value = _captions.value.map { seg ->
            if (seg.id == id) {
                TransliterationEngine.regenerateSegment(seg, _selectedLanguage.value.code)
            } else seg
        }
        saveCurrentProject()
    }

    fun deleteCaption(id: String) {
        _captions.value = _captions.value.filterNot { it.id == id }
        saveCurrentProject()
    }

    fun addCaptionAtCurrentPosition() {
        val current = _currentPositionMs.value
        val newSeg = CaptionSegment(
            startMs = current,
            endMs = (current + 3000L).coerceAtMost(_durationMs.value),
            phoneticText = "New caption segment...",
            originalScriptText = "",
            confidence = 0.98f
        )
        _captions.value = (_captions.value + newSeg).sortedBy { it.startMs }
        saveCurrentProject()
    }

    // Styling Methods
    fun updateFont(font: CaptionFont) {
        _captionStyle.value = _captionStyle.value.copy(fontChoice = font)
        saveCurrentProject()
    }

    fun updateFontSize(sizeSp: Int) {
        _captionStyle.value = _captionStyle.value.copy(fontSizeSp = sizeSp)
        saveCurrentProject()
    }

    fun updateTextColor(colorHex: Long) {
        _captionStyle.value = _captionStyle.value.copy(textColorHex = colorHex)
        saveCurrentProject()
    }

    fun updateBgStyle(style: CaptionBgStyle) {
        _captionStyle.value = _captionStyle.value.copy(bgStyle = style)
        saveCurrentProject()
    }

    fun updatePosition(ratioY: Float) {
        _captionStyle.value = _captionStyle.value.copy(positionRatioY = ratioY)
        saveCurrentProject()
    }

    fun saveCurrentProject() {
        viewModelScope.launch {
            val entity = VideoProjectEntity(
                id = _currentProjectId.value,
                title = _projectTitle.value,
                videoUri = _videoUri.value,
                durationMs = _durationMs.value,
                detectedLanguage = "${_selectedLanguage.value.name} (${_selectedLanguage.value.nativeScript})",
                languageCode = _selectedLanguage.value.code,
                confidenceScore = _confidenceScore.value,
                audioChannels = _audioChannels.value,
                sampleRate = _sampleRate.value,
                vocalBandStatus = _vocalBandStatus.value,
                captionsJson = CaptionSegment.serializeList(_captions.value),
                styleJson = _captionStyle.value.toJson(),
                updatedAt = System.currentTimeMillis()
            )
            repository.saveProject(entity)
        }
    }

    fun importNewVideo(title: String, uri: String?, durationMs: Long = 42000L) {
        viewModelScope.launch {
            val proj = repository.createNewProject(
                title = title,
                videoUri = uri,
                detectedLang = "Telugu (తెలుగు)",
                langCode = "te",
                durationMs = durationMs
            )
            loadProject(proj.id)
        }
    }

    fun getActiveCaptionAt(positionMs: Long): CaptionSegment? {
        return _captions.value.firstOrNull { positionMs in it.startMs..it.endMs }
    }

    fun copyToClipboard(context: Context, text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }
}
