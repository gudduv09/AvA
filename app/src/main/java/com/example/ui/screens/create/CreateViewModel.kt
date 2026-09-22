package com.example.ui.screens.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.repository.UserRepository
import com.example.data.repository.VideoRepository
import com.example.domain.model.VideoVisibility
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class RecordDuration(val seconds: Int, val label: String) {
    SEC_15(15, "15s"),
    SEC_30(30, "30s"),
    SEC_60(60, "60s"),
    MIN_3(180, "3m")
}

enum class VideoFilter(val displayName: String) {
    NORMAL("Normal"),
    CINEMATIC("Cinematic"),
    PINK_GLOW("Pink Glow"),
    CYBERPUNK("Cyberpunk"),
    NOIR("Noir"),
    RETRO("Retro")
}

enum class VoiceEffect(val displayName: String) {
    NORMAL("Normal"),
    ROBOT("Robot"),
    DEEP("Deep Bass"),
    ECHO("Echo Chamber"),
    RADIO("Vintage Radio")
}

class CreateViewModel(
    private val videoRepository: VideoRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // Camera State
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordProgress = MutableStateFlow(0f)
    val recordProgress: StateFlow<Float> = _recordProgress.asStateFlow()

    private val _selectedDuration = MutableStateFlow(RecordDuration.SEC_15)
    val selectedDuration: StateFlow<RecordDuration> = _selectedDuration.asStateFlow()

    private val _isFlashOn = MutableStateFlow(false)
    val isFlashOn: StateFlow<Boolean> = _isFlashOn.asStateFlow()

    private val _isFrontCamera = MutableStateFlow(false)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    private val _speed = MutableStateFlow("1x")
    val speed: StateFlow<String> = _speed.asStateFlow()

    private val _selectedSound = MutableStateFlow<String?>("Midnight Pulse - Synthwave Mix")
    val selectedSound: StateFlow<String?> = _selectedSound.asStateFlow()

    private val _recordedThumbnailRes = MutableStateFlow(R.drawable.ava_thumb_1)
    val recordedThumbnailRes: StateFlow<Int> = _recordedThumbnailRes.asStateFlow()

    // Editor State
    private val _activeFilter = MutableStateFlow(VideoFilter.NORMAL)
    val activeFilter: StateFlow<VideoFilter> = _activeFilter.asStateFlow()

    private val _activeVoiceEffect = MutableStateFlow(VoiceEffect.NORMAL)
    val activeVoiceEffect: StateFlow<VoiceEffect> = _activeVoiceEffect.asStateFlow()

    private val _textOverlay = MutableStateFlow("")
    val textOverlay: StateFlow<String> = _textOverlay.asStateFlow()

    private val _trimRange = MutableStateFlow(0f..1f)
    val trimRange: StateFlow<ClosedFloatingPointRange<Float>> = _trimRange.asStateFlow()

    // Publish State
    private val _caption = MutableStateFlow("")
    val caption: StateFlow<String> = _caption.asStateFlow()

    private val _visibility = MutableStateFlow(VideoVisibility.PUBLIC)
    val visibility: StateFlow<VideoVisibility> = _visibility.asStateFlow()

    private val _allowComments = MutableStateFlow(true)
    val allowComments: StateFlow<Boolean> = _allowComments.asStateFlow()

    private val _allowDuet = MutableStateFlow(true)
    val allowDuet: StateFlow<Boolean> = _allowDuet.asStateFlow()

    private val _allowDownload = MutableStateFlow(true)
    val allowDownload: StateFlow<Boolean> = _allowDownload.asStateFlow()

    private var recordJob: Job? = null

    fun selectDuration(duration: RecordDuration) {
        _selectedDuration.value = duration
    }

    fun toggleFlash() {
        _isFlashOn.value = !_isFlashOn.value
    }

    fun toggleCamera() {
        _isFrontCamera.value = !_isFrontCamera.value
    }

    fun setSpeed(s: String) {
        _speed.value = s
    }

    fun selectSound(sound: String?) {
        _selectedSound.value = sound
    }

    fun startRecording() {
        if (_isRecording.value) return
        _isRecording.value = true
        _recordProgress.value = 0f

        val totalSec = _selectedDuration.value.seconds
        val intervalMs = 100L
        val step = intervalMs.toFloat() / (totalSec * 1000f)

        recordJob = viewModelScope.launch {
            while (_recordProgress.value < 1f && _isRecording.value) {
                delay(intervalMs)
                _recordProgress.value = (_recordProgress.value + step).coerceAtMost(1f)
            }
            stopRecording()
        }
    }

    fun stopRecording() {
        recordJob?.cancel()
        _isRecording.value = false
        // Cycle thumbnail among realistic assets for demo recording
        val thumbs = listOf(R.drawable.ava_thumb_1, R.drawable.ava_thumb_2, R.drawable.ava_thumb_3, R.drawable.ava_thumb_4)
        _recordedThumbnailRes.value = thumbs.random()
    }

    fun setFilter(filter: VideoFilter) {
        _activeFilter.value = filter
    }

    fun setVoiceEffect(effect: VoiceEffect) {
        _activeVoiceEffect.value = effect
    }

    fun setTextOverlay(text: String) {
        _textOverlay.value = text
    }

    fun setTrimRange(range: ClosedFloatingPointRange<Float>) {
        _trimRange.value = range
    }

    fun setCaption(text: String) {
        _caption.value = text
    }

    fun addHashtag(tag: String) {
        val current = _caption.value
        val formattedTag = if (tag.startsWith("#")) tag else "#$tag"
        if (!current.contains(formattedTag)) {
            _caption.value = if (current.isBlank()) formattedTag else "$current $formattedTag"
        }
    }

    fun setVisibility(vis: VideoVisibility) {
        _visibility.value = vis
    }

    fun toggleComments() {
        _allowComments.value = !_allowComments.value
    }

    fun toggleDuet() {
        _allowDuet.value = !_allowDuet.value
    }

    fun toggleDownload() {
        _allowDownload.value = !_allowDownload.value
    }

    fun saveDraft(onSaved: () -> Unit) {
        viewModelScope.launch {
            userRepository.saveDraft(
                title = if (_caption.value.isNotBlank()) _caption.value.take(20) else "New Draft",
                caption = _caption.value,
                thumbnailResId = _recordedThumbnailRes.value,
                filter = _activeFilter.value.displayName
            )
            onSaved()
        }
    }

    // Upload & Transcoding Pipeline State
    private val _uploadStatus = MutableStateFlow<String?>("Idle")
    val uploadStatus: StateFlow<String?> = _uploadStatus.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress.asStateFlow()

    fun publishVideo(onPublished: () -> Unit) {
        viewModelScope.launch {
            _uploadStatus.value = "Validating video format & Keystore signature..."
            _uploadProgress.value = 0.15f
            delay(350)

            _uploadStatus.value = "Compressing & Transcoding (H.264 / AAC 1080p 60fps)..."
            _uploadProgress.value = 0.45f
            delay(400)

            _uploadStatus.value = "Generating thumbnail covers & preview sprites..."
            _uploadProgress.value = 0.70f
            delay(350)

            _uploadStatus.value = "Uploading video chunks to MBS Global CDN edge..."
            _uploadProgress.value = 0.90f
            delay(400)

            _uploadStatus.value = "Live on AVA!"
            _uploadProgress.value = 1.0f
            delay(200)

            val tags = Regex("#\\w+").findAll(_caption.value).map { it.value }.toList()
            val finalCaption = if (_caption.value.isNotBlank()) _caption.value else "New vibe on AVA ✨"
            videoRepository.publishVideo(
                caption = finalCaption,
                hashtags = if (tags.isNotEmpty()) tags else listOf("#AVA", "#vibe"),
                visibility = _visibility.value,
                allowComments = _allowComments.value,
                allowDuet = _allowDuet.value,
                allowDownload = _allowDownload.value,
                thumbnailResId = _recordedThumbnailRes.value
            )
            // Reset fields
            _caption.value = ""
            _recordProgress.value = 0f
            _uploadStatus.value = "Idle"
            _uploadProgress.value = 0f
            onPublished()
        }
    }
}
