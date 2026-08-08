package com.echo.app.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.echo.app.auth.GoogleAccountInfo
import com.echo.app.auth.GoogleAuthManager
import com.echo.app.data.local.EchoDatabase
import com.echo.app.data.models.AppNotificationItem
import com.echo.app.data.models.ClashItem
import com.echo.app.data.models.EchoPost
import com.echo.app.data.models.EchoUserProfile
import com.echo.app.data.models.ReverbItem
import com.echo.app.data.models.TerminalSettings
import com.echo.app.data.models.WaveItem
import com.echo.app.data.models.WhisperItem
import com.echo.app.ui.components.getFormattedEchoCardText
import com.echo.app.ui.components.getFormattedProfileCardText
import com.echo.app.util.AudioEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        EchoDatabase::class.java,
        "echo_db"
    ).fallbackToDestructiveMigration().build()

    private val dao = db.echoDao()
    val audioEngine = AudioEngine(application)

    val waves: StateFlow<List<WaveItem>> = dao.getAllWaves()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val echoes: StateFlow<List<EchoPost>> = dao.getAllEchoes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reverbs: StateFlow<List<ReverbItem>> = dao.getAllReverbs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clashes: StateFlow<List<ClashItem>> = dao.getActiveClashes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<EchoUserProfile?> = dao.getUserProfile("user_me")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val whispers: StateFlow<List<WhisperItem>> = dao.getAllWhispers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<AppNotificationItem>> = dao.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<TerminalSettings?> = dao.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Audio Playback State
    private val _playingPostId = MutableStateFlow<String?>(null)
    val playingPostId: StateFlow<String?> = _playingPostId.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    // Background Playable Live Stage Room State
    private val _activeRoomId = MutableStateFlow<String?>(null)
    val activeRoomId: StateFlow<String?> = _activeRoomId.asStateFlow()

    private val _activeRoomTitle = MutableStateFlow<String?>("GLOBAL STAGE ARENA")
    val activeRoomTitle: StateFlow<String?> = _activeRoomTitle.asStateFlow()

    private val _isRoomPlayingInBg = MutableStateFlow(false)
    val isRoomPlayingInBg: StateFlow<Boolean> = _isRoomPlayingInBg.asStateFlow()

    private val _isRoomMuted = MutableStateFlow(false)
    val isRoomMuted: StateFlow<Boolean> = _isRoomMuted.asStateFlow()

    // Audience Request to Speak State
    private val _speakRequestsMap = MutableStateFlow<Map<String, List<String>>>(
        mapOf("clash-1" to listOf("@NOVA.VOICE", "@ALEX_AUDIO"))
    )
    val speakRequestsMap: StateFlow<Map<String, List<String>>> = _speakRequestsMap.asStateFlow()

    private val _mySpeakRequestClashId = MutableStateFlow<String?>(null)
    val mySpeakRequestClashId: StateFlow<String?> = _mySpeakRequestClashId.asStateFlow()

    fun joinStageRoom(clashId: String, title: String) {
        _activeRoomId.value = clashId
        _activeRoomTitle.value = title
        _isRoomPlayingInBg.value = true
        _isRoomMuted.value = false
        audioEngine.playRealtimeSynthTone()
        recordActivityGain(5)

        viewModelScope.launch(Dispatchers.IO) {
            val list = dao.getActiveClashes().first()
            val clash = list.find { it.id == clashId }
            if (clash != null) {
                dao.updateClash(clash.copy(listeners = clash.listeners + 1))
            }
        }
    }

    fun leaveStageRoom() {
        _activeRoomId.value = null
        _isRoomPlayingInBg.value = false
        audioEngine.stopPlayback()
    }

    fun toggleMuteRoom() {
        _isRoomMuted.value = !_isRoomMuted.value
        if (_isRoomMuted.value) {
            audioEngine.stopPlayback()
        } else {
            audioEngine.playRealtimeSynthTone()
        }
    }

    fun requestToSpeak(clashId: String) {
        val handle = userProfile.value?.handle ?: "@YOU"
        _mySpeakRequestClashId.value = clashId

        val currentMap = _speakRequestsMap.value.toMutableMap()
        val currentList = (currentMap[clashId] ?: emptyList()).toMutableList()
        if (!currentList.contains(handle)) {
            currentList.add(handle)
        }
        currentMap[clashId] = currentList
        _speakRequestsMap.value = currentMap

        recordActivityGain(5)

        viewModelScope.launch(Dispatchers.IO) {
            dao.insertNotification(
                AppNotificationItem(
                    id = "notif-speak-${UUID.randomUUID()}",
                    title = "SPEAK REQUEST SENT ✋",
                    body = "You requested to speak on stage in room '$clashId'. Host notified.",
                    category = "STAGE"
                )
            )
        }
    }

    fun approveSpeakRequest(clashId: String, handle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentMap = _speakRequestsMap.value.toMutableMap()
            val currentList = (currentMap[clashId] ?: emptyList()).toMutableList()
            currentList.remove(handle)
            currentMap[clashId] = currentList
            _speakRequestsMap.value = currentMap

            // Update clash speaker
            val list = dao.getActiveClashes().first()
            val clash = list.find { it.id == clashId }
            if (clash != null) {
                val updatedClash = clash.copy(
                    handleB = handle,
                    posB = "$handle granted stage mic!"
                )
                dao.updateClash(updatedClash)
            }

            dao.insertNotification(
                AppNotificationItem(
                    id = "notif-speak-approved-${UUID.randomUUID()}",
                    title = "MIC GRANTED 🎤",
                    body = "$handle was approved to speak on Stage by Host!",
                    category = "STAGE"
                )
            )
        }
    }

    fun cancelSpeakRequest(clashId: String) {
        val handle = userProfile.value?.handle ?: "@YOU"
        if (_mySpeakRequestClashId.value == clashId) {
            _mySpeakRequestClashId.value = null
        }
        val currentMap = _speakRequestsMap.value.toMutableMap()
        val currentList = (currentMap[clashId] ?: emptyList()).toMutableList()
        currentList.remove(handle)
        currentMap[clashId] = currentList
        _speakRequestsMap.value = currentMap
    }

    // Social Media Sharing & Story/Status Intent Helper
    fun shareToSocialMedia(context: Context, textContent: String, title: String = "Echo Frequency Drop") {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, textContent)
            }
            val chooser = Intent.createChooser(shareIntent, "Share Frequency Drop via...")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            recordActivityGain(3)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareToStoryStatus(context: Context, title: String, caption: String, author: String, duration: String = "00:30") {
        val storyFormattedText = """
            ┌──────────────────────────────────────────────┐
            │  ECHO. RAW AUDIO FREQUENCY                 │
            ├──────────────────────────────────────────────┤
            │  VOICE DROP BY $author                       │
            │                                              │
            │  "$caption"                                  │
            │                                              │
            │  ⏱ DURATION: $duration | ⚡ STAGE ROOM      │
            │  🎧 LISTEN LIVE: https://echo.app/drop/live  │
            └──────────────────────────────────────────────┘
            Posted via Echo. Frequency Network
        """.trimIndent()

        shareToSocialMedia(context, storyFormattedText, "Post to Story / Status: $title")
    }

    // Auto-Scan Radio State
    private val _isAutoScanActive = MutableStateFlow(false)
    val isAutoScanActive: StateFlow<Boolean> = _isAutoScanActive.asStateFlow()

    fun toggleAutoScan() {
        _isAutoScanActive.value = !_isAutoScanActive.value
        recordActivityGain(1)
    }

    // Studio Recording State
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingMs = MutableStateFlow(0L)
    val recordingMs: StateFlow<Long> = _recordingMs.asStateFlow()

    private val _recordedAudioFile = MutableStateFlow<File?>(null)
    val recordedAudioFile: StateFlow<File?> = _recordedAudioFile.asStateFlow()

    private val _recordingCaption = MutableStateFlow("")
    val recordingCaption: StateFlow<String> = _recordingCaption.asStateFlow()

    private val _isPublishing = MutableStateFlow(false)
    val isPublishing: StateFlow<Boolean> = _isPublishing.asStateFlow()

    // Challenge Modal state for Stage
    private val _showChallengeModal = MutableStateFlow(false)
    val showChallengeModal: StateFlow<Boolean> = _showChallengeModal.asStateFlow()

    init {
        seedInitialDataIfNeeded()
    }

    private fun seedInitialDataIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            val existingWaves = dao.getAllWaves().first()
            if (existingWaves.isEmpty()) {
                val initialWaves = listOf(
                    WaveItem(
                        id = "wave-1",
                        authorHandle = "@ZARA.IQ",
                        caption = "Electro-acoustic vocal textures & raw pitch shifts with @OG_VIBE. Unfiltered audio reels 🌊",
                        duration = "00:28",
                        durationSec = 28,
                        pulseCount = 1840,
                        isPulsed = false,
                        reverbCount = 4,
                        accentThemeIndex = 0
                    ),
                    WaveItem(
                        id = "wave-2",
                        authorHandle = "@OG_VIBE",
                        caption = "Midnight ambient synth wave & bass resonance with @YOU. Pure nocturnal frequency drop.",
                        duration = "00:42",
                        durationSec = 42,
                        pulseCount = 2150,
                        isPulsed = true,
                        reverbCount = 6,
                        accentThemeIndex = 1
                    ),
                    WaveItem(
                        id = "wave-3",
                        authorHandle = "@LUNA.SOUND",
                        caption = "Binaural drone frequencies & spatial audio experiments. Tap reverb to leave a voice drop @YOU",
                        duration = "00:35",
                        durationSec = 35,
                        pulseCount = 980,
                        isPulsed = false,
                        reverbCount = 3,
                        accentThemeIndex = 2
                    ),
                    WaveItem(
                        id = "wave-4",
                        authorHandle = "@ARYAN.V",
                        caption = "Stage clash highlight: Unfiltered vocal debate audio vs AI voice models with @ZARA.IQ",
                        duration = "00:20",
                        durationSec = 20,
                        pulseCount = 3120,
                        isPulsed = true,
                        reverbCount = 8,
                        accentThemeIndex = 3
                    ),
                    WaveItem(
                        id = "wave-5",
                        authorHandle = "@YOU",
                        caption = "Studio session raw drop. High energy frequency pulse & brutalist soundscape.",
                        duration = "00:30",
                        durationSec = 30,
                        pulseCount = 1450,
                        isPulsed = false,
                        reverbCount = 2,
                        accentThemeIndex = 4
                    )
                )
                initialWaves.forEach { dao.insertWave(it) }

                // Seed Initial Wave Reverbs
                val waveReverbs = listOf(
                    ReverbItem(
                        id = "rev-w1",
                        postId = "wave-1",
                        authorHandle = "@OG_VIBE",
                        content = "@ZARA.IQ That vocal pitch modulation at 00:15 is insane!",
                        isVoice = false,
                        likeCount = 34,
                        isLiked = true,
                        replyToHandle = "@ZARA.IQ"
                    ),
                    ReverbItem(
                        id = "rev-w2",
                        postId = "wave-1",
                        authorHandle = "@YOU",
                        content = "[VOICE REVERB] Harmonic resonance overlay...",
                        isVoice = true,
                        audioDuration = "00:09",
                        likeCount = 18,
                        isLiked = false,
                        replyToHandle = "@OG_VIBE"
                    )
                )
                waveReverbs.forEach { dao.insertReverb(it) }
            }

            val existing = dao.getAllEchoes().first()
            if (existing.isEmpty()) {
                // Seed Echo Posts
                val initialPosts = listOf(
                    EchoPost(
                        id = "echo-1",
                        authorHandle = "@ZARA.IQ",
                        authorUid = "zara-001",
                        caption = "Raw thoughts on creative momentum with @OG_VIBE. Why overthinking kills raw voice audio.",
                        duration = "00:24",
                        durationSec = 24,
                        pulseCount = 1420,
                        isPulsed = false,
                        reverbCount = 2,
                        taggedHandles = "@OG_VIBE"
                    ),
                    EchoPost(
                        id = "echo-2",
                        authorHandle = "@OG_VIBE",
                        authorUid = "vibe-002",
                        caption = "Late night frequency drop. Synth loops with @YOU, nocturnal soundscapes, and brutalist beatcraft.",
                        duration = "00:45",
                        durationSec = 45,
                        pulseCount = 890,
                        isPulsed = true,
                        reverbCount = 1,
                        taggedHandles = "@YOU"
                    ),
                    EchoPost(
                        id = "echo-3",
                        authorHandle = "@ARYAN.V",
                        authorUid = "aryan-003",
                        caption = "The stage debate motion with @ZARA.IQ: Will AI replace human vocal expression or amplify unfiltered truth?",
                        duration = "00:18",
                        durationSec = 18,
                        pulseCount = 2100,
                        isPulsed = false,
                        reverbCount = 1,
                        taggedHandles = "@ZARA.IQ"
                    )
                )
                initialPosts.forEach { dao.insertEcho(it) }

                // Seed Reverbs
                val initialReverbs = listOf(
                    ReverbItem(
                        id = "rev-1",
                        postId = "echo-1",
                        authorHandle = "@OG_VIBE",
                        content = "Resonating heavily with this frequency! Raw texture is unmatched.",
                        isVoice = false,
                        likeCount = 14,
                        isLiked = true
                    ),
                    ReverbItem(
                        id = "rev-2",
                        postId = "echo-1",
                        authorHandle = "@YOU",
                        content = "[VOICE REVERB] Adding vocal perspective on tone dynamics...",
                        isVoice = true,
                        audioDuration = "00:12",
                        likeCount = 8,
                        isLiked = false
                    ),
                    ReverbItem(
                        id = "rev-3",
                        postId = "echo-2",
                        authorHandle = "@ARYAN.V",
                        content = "That low end synth pulse @OG_VIBE goes wild 🔥",
                        isVoice = false,
                        likeCount = 22,
                        isLiked = false
                    )
                )
                initialReverbs.forEach { dao.insertReverb(it) }

                // Seed Clashes
                val initialClashes = listOf(
                    ClashItem(
                        id = "clash-101",
                        title = "LIVE STAGE DEBATE",
                        topic = "AI vs Human Vocal Expression in Music & Art",
                        handleA = "@ZARA.IQ",
                        posA = "Human emotion and tone instability form authentic beauty.",
                        votesA = 184,
                        handleB = "@ARYAN.V",
                        posB = "AI voice models expand creative possibilities without boundaries.",
                        votesB = 126,
                        listeners = 340
                    ),
                    ClashItem(
                        id = "clash-102",
                        title = "THE FREQUENCY MOTION",
                        topic = "Unfiltered Voice Notes vs Polished Studio Podcasts",
                        handleA = "@OG_VIBE",
                        posA = "Raw instant voice notes capture genuine soul and context.",
                        votesA = 312,
                        handleB = "@YOU",
                        posB = "High-fidelity audio production enhances listener engagement.",
                        votesB = 280,
                        listeners = 520
                    )
                )
                initialClashes.forEach { dao.insertClash(it) }

                // Seed User Profile
                dao.insertOrUpdateProfile(
                    EchoUserProfile(
                        uid = "user_me",
                        handle = "@YOU",
                        displayName = "Authentic Voice",
                        auraScore = 1420,
                        voiceBioDuration = "00:30"
                    )
                )

                // Seed Settings
                dao.updateSettings(TerminalSettings())

                // Seed Notifications
                val initialNotifications = listOf(
                    AppNotificationItem(
                        id = "notif-1",
                        title = "PULSE RECEIVED",
                        body = "@ZARA.IQ pulsed your echo 'Raw thoughts on creative momentum'",
                        category = "PULSES"
                    ),
                    AppNotificationItem(
                        id = "notif-2",
                        title = "STAGE CHALLENGE",
                        body = "@ARYAN.V launched a debate challenge against @YOU on The Stage",
                        category = "STAGE"
                    )
                )
                initialNotifications.forEach { dao.insertNotification(it) }
            }
        }
    }

    // Audio Playback with Auto-Scan Radio sequence
    fun togglePlayEcho(postId: String, durationSec: Int) {
        if (_playingPostId.value == postId) {
            audioEngine.stopPlayback()
            _playingPostId.value = null
            _playbackProgress.value = 0f
        } else {
            audioEngine.stopPlayback()
            _playingPostId.value = postId

            viewModelScope.launch(Dispatchers.IO) {
                val echoList = dao.getAllEchoes().first()
                val targetEcho = echoList.find { it.id == postId }
                val waveList = dao.getAllWaves().first()
                val targetWave = waveList.find { it.id == postId }
                val audioUrl = targetEcho?.audioUrl?.ifBlank { null } ?: targetWave?.audioUrl?.ifBlank { null } ?: postId

                audioEngine.playAudio(
                    fileOrUrl = audioUrl,
                    onCompletion = {
                        _playingPostId.value = null
                        _playbackProgress.value = 0f
                        if (_isAutoScanActive.value) {
                            playNextInAutoScan(postId)
                        }
                    },
                    onError = {
                        _playingPostId.value = null
                        _playbackProgress.value = 0f
                        if (_isAutoScanActive.value) {
                            playNextInAutoScan(postId)
                        }
                    }
                )
            }

            viewModelScope.launch {
                val totalSteps = Math.max(10, durationSec * 10)
                for (step in 1..totalSteps) {
                    if (_playingPostId.value != postId) break
                    delay(100)
                    _playbackProgress.value = step.toFloat() / totalSteps.toFloat()
                }
                if (_playingPostId.value == postId) {
                    _playingPostId.value = null
                    _playbackProgress.value = 0f
                    if (_isAutoScanActive.value) {
                        playNextInAutoScan(postId)
                    }
                }
            }
        }
    }

    private fun playNextInAutoScan(currentPostId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val echoList = dao.getAllEchoes().first()
            if (echoList.isEmpty()) return@launch
            val currentIndex = echoList.indexOfFirst { it.id == currentPostId }
            val nextPost = if (currentIndex != -1 && currentIndex + 1 < echoList.size) {
                echoList[currentIndex + 1]
            } else {
                echoList.first() // Loop back to start
            }

            // Play radio tuning static transition sound (*kshhh*)
            audioEngine.playRadioStaticSound {
                togglePlayEcho(nextPost.id, nextPost.durationSec)
            }
        }
    }

    // Fork an Audio Drop (Duet / Remix with lineage tracking)
    fun forkEcho(sourcePost: EchoPost, forkCaption: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = userProfile.value
            val handle = profile?.handle ?: "@YOU"
            val newEcho = EchoPost(
                id = "echo-fork-${UUID.randomUUID()}",
                authorHandle = handle,
                authorUid = profile?.uid ?: "user_me",
                caption = forkCaption.ifBlank { "↳ FORK of @${sourcePost.authorHandle}: \"${sourcePost.caption.take(30)}\"" },
                duration = "00:15",
                durationSec = 15,
                pulseCount = 1,
                audioUrl = sourcePost.audioUrl,
                categoryTag = sourcePost.categoryTag,
                isFork = true,
                sourcePostId = sourcePost.id,
                sourceAuthorHandle = sourcePost.authorHandle,
                forkDepth = sourcePost.forkDepth + 1
            )
            dao.insertEcho(newEcho)
            recordTransmissionUptime()
            recordActivityGain(10)

            // Notify original author
            dao.insertNotification(
                AppNotificationItem(
                    id = "notif-fork-${UUID.randomUUID()}",
                    title = "FORK CREATED ↳",
                    body = "$handle forked your drop '${sourcePost.caption.take(20)}...'",
                    category = "REVERBS"
                )
            )
        }
    }

    // Time-Stamped Telemetry Pulse Reaction
    fun togglePulseWithTelemetry(postId: String, currentPlaybackSec: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = dao.getAllEchoes().first()
            val item = list.find { it.id == postId } ?: return@launch

            // Parse current telemetry nodes CSV: "5:12,12:28,22:45"
            val nodeMap = mutableMapOf<Int, Int>()
            if (item.telemetryNodes.isNotBlank()) {
                item.telemetryNodes.split(",").forEach { pair ->
                    val parts = pair.split(":")
                    if (parts.size == 2) {
                        val sec = parts[0].trim().toIntOrNull()
                        val count = parts[1].trim().toIntOrNull()
                        if (sec != null && count != null) {
                            nodeMap[sec] = count
                        }
                    }
                }
            }

            // Increment pulse count for exact playing second
            val targetSec = currentPlaybackSec.coerceAtLeast(1)
            nodeMap[targetSec] = (nodeMap[targetSec] ?: 0) + 1

            // Re-serialize telemetry nodes
            val newTelemetryStr = nodeMap.entries.joinToString(",") { "${it.key}:${it.value}" }

            val updated = item.copy(
                isPulsed = !item.isPulsed,
                pulseCount = if (item.isPulsed) item.pulseCount - 1 else item.pulseCount + 1,
                telemetryNodes = newTelemetryStr
            )
            dao.updateEcho(updated)
            if (!item.isPulsed) {
                recordActivityGain(3)
            }
        }
    }

    // Play preview of recorded Studio audio
    fun previewRecordedAudio() {
        val file = _recordedAudioFile.value
        if (file != null && file.exists()) {
            audioEngine.playAudio(file.absolutePath)
        } else {
            audioEngine.playRealtimeSynthTone()
        }
    }

    // Pulse / Upvote Echo
    fun togglePulse(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = dao.getAllEchoes().first()
            val item = list.find { it.id == postId } ?: return@launch
            val updated = item.copy(
                isPulsed = !item.isPulsed,
                pulseCount = if (item.isPulsed) item.pulseCount - 1 else item.pulseCount + 1
            )
            dao.updateEcho(updated)
            if (!item.isPulsed) {
                recordActivityGain(2)
            }
        }
    }

    // Stage Debate Voting with dB Gain Multiplier Power
    fun voteOnClash(clashId: String, side: String) { // "A" or "B"
        viewModelScope.launch(Dispatchers.IO) {
            val list = dao.getActiveClashes().first()
            val clash = list.find { it.id == clashId } ?: return@launch
            if (clash.userVotedSide != null) return@launch // already voted

            val profile = userProfile.value
            val signal = profile?.signalDb ?: 24
            val voteWeight = maxOf(1, 1 + (signal / 10))

            val updated = if (side == "A") {
                clash.copy(votesA = clash.votesA + voteWeight, userVotedSide = "A")
            } else {
                clash.copy(votesB = clash.votesB + voteWeight, userVotedSide = "B")
            }
            dao.updateClash(updated)
            recordActivityGain(5)
        }
    }

    // Challenge / Launch Debate
    fun launchClash(
        title: String,
        topic: String,
        handleA: String,
        posA: String,
        handleB: String,
        posB: String,
        isPrivate: Boolean = false,
        invitedHandles: String = "",
        topicCategory: String = "DEBATES"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newClash = ClashItem(
                id = "clash-${UUID.randomUUID()}",
                title = title.ifBlank { "LIVE STAGE DEBATE" },
                topic = topic,
                handleA = handleA.ifBlank { "@YOU" },
                posA = posA,
                votesA = 1,
                handleB = handleB.ifBlank { "@CHALLENGER" },
                posB = posB,
                votesB = 0,
                listeners = 1,
                isPrivate = isPrivate,
                invitedHandles = invitedHandles,
                topicCategory = topicCategory
            )
            dao.insertClash(newClash)
            _showChallengeModal.value = false

            // Notify invited handles if any
            if (invitedHandles.isNotBlank()) {
                val notifyHandles = invitedHandles.split(",").map { it.trim() }.filter { it.isNotBlank() }
                notifyHandles.forEach { handle ->
                    dao.insertNotification(
                        AppNotificationItem(
                            id = "notif-clash-${UUID.randomUUID()}",
                            title = "STAGE CHALLENGE INVITE",
                            body = "$handleA invited you ($handle) to a ${if (isPrivate) "PRIVATE" else "PUBLIC"} debate challenge on '$topic'",
                            category = "STAGE"
                        )
                    )
                }
            }
        }
    }

    fun inviteUserToStage(clashId: String, invitedHandle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = dao.getActiveClashes().first()
            val clash = list.find { it.id == clashId } ?: return@launch
            val currentInvited = clash.invitedHandles.split(",").map { it.trim() }.filter { it.isNotBlank() }.toMutableSet()
            currentInvited.add(invitedHandle)
            val updated = clash.copy(invitedHandles = currentInvited.joinToString(","))
            dao.updateClash(updated)

            val profile = userProfile.value
            val sender = profile?.handle ?: "@YOU"

            dao.insertNotification(
                AppNotificationItem(
                    id = "notif-stage-inv-${UUID.randomUUID()}",
                    title = "STAGE ROOM INVITE",
                    body = "$sender invited you to join live Stage Room '${clash.title}'",
                    category = "STAGE"
                )
            )
        }
    }

    fun updateUserLikedTopics(topicsCsv: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = userProfile.value ?: return@launch
            val updated = profile.copy(likedTopics = topicsCsv)
            dao.insertOrUpdateProfile(updated)
        }
    }

    fun inviteToChallengeOnTopic(topic: String, targetHandle: String) {
        val profile = userProfile.value
        val myHandle = profile?.handle ?: "@YOU"
        launchClash(
            title = "TOPIC CHALLENGE: ${topic.take(20).uppercase()}",
            topic = topic,
            handleA = myHandle,
            posA = "Proponent stance on $topic",
            handleB = targetHandle,
            posB = "Counter stance on $topic",
            isPrivate = false,
            invitedHandles = targetHandle,
            topicCategory = "CHALLENGE"
        )
    }

    fun setShowChallengeModal(show: Boolean) {
        _showChallengeModal.value = show
    }

    // Studio Recording
    fun startStudioRecording() {
        val file = audioEngine.startRecording()
        _recordedAudioFile.value = file
        _isRecording.value = true
        _recordingMs.value = 0L

        viewModelScope.launch {
            val start = System.currentTimeMillis()
            while (_isRecording.value) {
                _recordingMs.value = System.currentTimeMillis() - start
                delay(30)
            }
        }
    }

    fun stopStudioRecording() {
        _isRecording.value = false
        val file = audioEngine.stopRecording()
        _recordedAudioFile.value = file
    }

    fun setRecordingCaption(caption: String) {
        _recordingCaption.value = caption
    }

    fun publishStudioRecording(onPublished: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isPublishing.value = true
            delay(600) // simulate processing/uploading

            val totalSec = Math.max(1, (_recordingMs.value / 1000).toInt())
            val formattedDuration = String.format("%02d:%02d", totalSec / 60, totalSec % 60)

            val profile = userProfile.value
            val authorHandle = profile?.handle ?: "@YOU"

            val newEcho = EchoPost(
                id = "echo-${UUID.randomUUID()}",
                authorHandle = authorHandle,
                authorUid = profile?.uid ?: "user_me",
                caption = _recordingCaption.value.ifBlank { "Unfiltered voice drop" },
                duration = formattedDuration,
                durationSec = totalSec,
                pulseCount = 1,
                isPulsed = true,
                audioUrl = _recordedAudioFile.value?.absolutePath ?: ""
            )

            dao.insertEcho(newEcho)

            // Record transmission uptime streak & signal gain
            recordTransmissionUptime()
            recordActivityGain(5)

            // Increment Aura score
            if (profile != null) {
                dao.insertOrUpdateProfile(profile.copy(auraScore = profile.auraScore + 50))
            }

            // Reset studio state
            _recordingCaption.value = ""
            _recordingMs.value = 0L
            _recordedAudioFile.value = null
            _isPublishing.value = false

            viewModelScope.launch(Dispatchers.Main) {
                onPublished()
            }
        }
    }

    // Engagement Loop & Streak Algorithms (PHASE-LOCK, TX_UPTIME, dB GAIN)
    fun recordActivityGain(gainDb: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = userProfile.value ?: return@launch
            val now = System.currentTimeMillis()
            val timeSinceActive = now - profile.lastActiveTimestamp
            
            // Attenuation / Decay math: If inactive > 24 hours, attenuate signal by -3 dB per 24 hours
            var currentSignal = profile.signalDb
            if (timeSinceActive > 24 * 3600 * 1000L) {
                val daysInactive = (timeSinceActive / (24 * 3600 * 1000L)).toInt()
                currentSignal = maxOf(0, currentSignal - (daysInactive * 3))
            }
            
            val newSignal = minOf(100, currentSignal + gainDb)
            val updated = profile.copy(
                signalDb = newSignal,
                lastActiveTimestamp = now
            )
            dao.insertOrUpdateProfile(updated)
        }
    }

    fun recordTransmissionUptime() {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = userProfile.value ?: return@launch
            val now = System.currentTimeMillis()
            val timeSinceLastTx = now - profile.lastTxTimestamp
            val hours24 = 24 * 3600 * 1000L
            val hours48 = 48 * 3600 * 1000L

            val newUptime = when {
                timeSinceLastTx <= hours48 && timeSinceLastTx >= hours24 -> profile.txUptimeDays + 1
                timeSinceLastTx < hours24 -> profile.txUptimeDays // same day broadcast
                else -> 1 // broke streak -> reset
            }

            val updated = profile.copy(
                txUptimeDays = newUptime,
                lastTxTimestamp = now,
                auraScore = profile.auraScore + 25
            )
            dao.insertOrUpdateProfile(updated)

            // Trigger notification if milestone
            if (newUptime % 7 == 0) {
                dao.insertNotification(
                    AppNotificationItem(
                        id = "notif-uptime-${UUID.randomUUID()}",
                        title = "📡 TX_UPTIME MILESTONE",
                        body = "Your transmission uptime reached $newUptime DAYS unbroken streak!",
                        category = "PULSES"
                    )
                )
            }
        }
    }

    // Whispers / Private Messages with PHASE-LOCK Algorithm
    fun sendWhisper(recipientHandle: String, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val recipient = recipientHandle.ifBlank { "@ANON" }
            val existing = dao.getAllWhispers().first()
                .filter { it.recipientHandle.equals(recipient, ignoreCase = true) || it.senderHandle.equals(recipient, ignoreCase = true) }
                .maxByOrNull { it.timestamp }

            val now = System.currentTimeMillis()
            val timeDiff = if (existing != null) now - existing.lastInteractionTimestamp else 0L

            val streakDays = when {
                existing == null -> 1
                timeDiff <= 48 * 3600 * 1000L -> existing.phaseLockStreakDays + 1
                else -> 1 // DECOHERENT reset
            }

            val phaseState = when {
                timeDiff > 20 * 3600 * 1000L && timeDiff <= 24 * 3600 * 1000L -> "DRIFTING"
                timeDiff > 24 * 3600 * 1000L && timeDiff <= 48 * 3600 * 1000L -> "DRIFTING"
                timeDiff > 48 * 3600 * 1000L -> "DECOHERENT"
                else -> "PHASE-LOCKED"
            }

            val hz = streakDays * 10

            val whisper = WhisperItem(
                id = "whisper-${UUID.randomUUID()}",
                senderHandle = "@YOU",
                recipientHandle = recipient,
                message = message,
                timestamp = now,
                phaseLockHz = hz,
                phaseLockStreakDays = streakDays,
                lastInteractionTimestamp = now,
                phaseState = phaseState
            )
            dao.insertWhisper(whisper)
            recordActivityGain(2)
        }
    }

    // Notifications
    fun markAllNotificationsRead() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.markAllNotificationsRead()
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteNotification(notificationId)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearAllNotifications()
        }
    }

    // Settings
    fun updateTerminalSettings(newSettings: TerminalSettings) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateSettings(newSettings)
        }
    }

    // Delete Echo Post / Frequency
    fun deleteEcho(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteEcho(postId)
        }
    }

    // Delete Wave
    fun deleteWave(waveId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteWave(waveId)
        }
    }

    // Delete Whisper
    fun deleteWhisper(whisperId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteWhisper(whisperId)
        }
    }

    // Mute State for WAVES / Audio Reels
    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    // Pulse / Like Wave Item
    fun togglePulseWave(waveId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = dao.getAllWaves().first()
            val item = list.find { it.id == waveId } ?: return@launch
            val updated = item.copy(
                isPulsed = !item.isPulsed,
                pulseCount = if (item.isPulsed) item.pulseCount - 1 else item.pulseCount + 1
            )
            dao.updateWave(updated)
        }
    }

    // Cycle Wave Accent Theme (0..4)
    fun cycleWaveAccentTheme(waveId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = dao.getAllWaves().first()
            val item = list.find { it.id == waveId } ?: return@launch
            val nextTheme = (item.accentThemeIndex + 1) % 5
            dao.updateWave(item.copy(accentThemeIndex = nextTheme))
        }
    }

    // Reverbs (Comments & Voice Quote Responses)
    fun addReverb(postId: String, content: String, isVoice: Boolean = false, replyToHandle: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = userProfile.value
            val handle = profile?.handle ?: "@YOU"

            val newReverb = ReverbItem(
                id = "rev-${UUID.randomUUID()}",
                postId = postId,
                authorHandle = handle,
                content = content.ifBlank { if (isVoice) "[VOICE REVERB DROP]" else "Reverbed frequency" },
                isVoice = isVoice,
                audioDuration = if (isVoice) "00:10" else "00:00",
                likeCount = 1,
                isLiked = true,
                replyToHandle = replyToHandle
            )
            dao.insertReverb(newReverb)

            // Update Echo Post reverb count if post is an Echo
            val posts = dao.getAllEchoes().first()
            val targetPost = posts.find { it.id == postId }
            if (targetPost != null) {
                dao.updateEcho(targetPost.copy(reverbCount = targetPost.reverbCount + 1))
            }

            // Update Wave Item reverb count if post is a Wave
            val wavesList = dao.getAllWaves().first()
            val targetWave = wavesList.find { it.id == postId }
            if (targetWave != null) {
                dao.updateWave(targetWave.copy(reverbCount = targetWave.reverbCount + 1))
            }

            // Trigger Notification for engagement
            dao.insertNotification(
                AppNotificationItem(
                    id = "notif-${UUID.randomUUID()}",
                    title = "NEW REVERB",
                    body = "$handle reverbed on drop: '${(targetPost?.caption ?: targetWave?.caption ?: "").take(30)}'",
                    category = "REVERBS"
                )
            )
        }
    }

    fun toggleLikeReverb(reverbId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = dao.getAllReverbs().first()
            val target = list.find { it.id == reverbId } ?: return@launch
            val updated = target.copy(
                isLiked = !target.isLiked,
                likeCount = if (target.isLiked) target.likeCount - 1 else target.likeCount + 1
            )
            dao.updateReverb(updated)
        }
    }

    fun deleteReverb(reverbId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = dao.getAllReverbs().first()
            val target = list.find { it.id == reverbId }
            dao.deleteReverb(reverbId)
            if (target != null) {
                val posts = dao.getAllEchoes().first()
                val targetPost = posts.find { it.id == target.postId }
                if (targetPost != null && targetPost.reverbCount > 0) {
                    dao.updateEcho(targetPost.copy(reverbCount = targetPost.reverbCount - 1))
                }
            }
        }
    }

    // Google Authentication States
    private val _deviceGoogleAccounts = MutableStateFlow<List<GoogleAccountInfo>>(emptyList())
    val deviceGoogleAccounts: StateFlow<List<GoogleAccountInfo>> = _deviceGoogleAccounts.asStateFlow()

    private val _googleAuthError = MutableStateFlow<String?>(null)
    val googleAuthError: StateFlow<String?> = _googleAuthError.asStateFlow()

    private val _isAuthenticatingGoogle = MutableStateFlow(false)
    val isAuthenticatingGoogle: StateFlow<Boolean> = _isAuthenticatingGoogle.asStateFlow()

    fun loadDeviceGoogleAccounts(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val accounts = GoogleAuthManager.fetchDeviceGoogleAccounts(context)
            _deviceGoogleAccounts.value = accounts
        }
    }

    fun signInWithGoogleAccount(account: GoogleAccountInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            _isAuthenticatingGoogle.value = true
            _googleAuthError.value = null

            delay(200)

            val current = userProfile.value
            val handleClean = "@" + account.email.substringBefore("@").uppercase().replace(".", "_")

            val updatedProfile = EchoUserProfile(
                uid = "user_me",
                handle = handleClean,
                displayName = account.displayName,
                auraScore = (current?.auraScore ?: 1420) + 100,
                voiceBioDuration = current?.voiceBioDuration ?: "00:30",
                voiceBioUrl = current?.voiceBioUrl ?: "",
                email = account.email,
                photoUrl = account.photoUrl ?: "https://lh3.googleusercontent.com/a/default-user",
                isGoogleSignedIn = true,
                googleAccountName = account.displayName
            )

            dao.insertOrUpdateProfile(updatedProfile)

            dao.insertNotification(
                AppNotificationItem(
                    id = "notif-google-${System.currentTimeMillis()}",
                    title = "GOOGLE SIGN IN SUCCESS",
                    body = "Authenticated as ${account.displayName} (${account.email})",
                    category = "GOOGLE_AUTH"
                )
            )

            _isAuthenticatingGoogle.value = false
        }
    }

    fun signOutGoogle() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = userProfile.value
            val signedOutProfile = EchoUserProfile(
                uid = "user_me",
                handle = "@GUEST",
                displayName = "Guest Echo User",
                auraScore = current?.auraScore ?: 1000,
                voiceBioDuration = current?.voiceBioDuration ?: "00:30",
                voiceBioUrl = current?.voiceBioUrl ?: "",
                email = null,
                photoUrl = null,
                isGoogleSignedIn = false,
                googleAccountName = null
            )
            dao.insertOrUpdateProfile(signedOutProfile)

            dao.insertNotification(
                AppNotificationItem(
                    id = "notif-signout-${System.currentTimeMillis()}",
                    title = "GOOGLE SIGNED OUT",
                    body = "Switched to local Guest session",
                    category = "GOOGLE_AUTH"
                )
            )
        }
    }

    fun setGoogleAuthError(error: String?) {
        _googleAuthError.value = error
    }

    // Sharing via System Intent
    fun shareEcho(context: Context, echo: EchoPost) {
        val shareText = getFormattedEchoCardText(echo)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Echo Frequency Card")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun shareProfile(context: Context, profile: EchoUserProfile?) {
        val p = profile ?: EchoUserProfile("you-001", "@YOU", "Raw thoughts. Unfiltered frequencies.", 1840, "00:15", null)
        val shareText = getFormattedProfileCardText(p)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Echo Profile Card")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun shareWhisper(context: Context, whisper: WhisperItem) {
        val shareText = "💬 Private Whisper to ${whisper.recipientHandle}:\n\"${whisper.message}\"\n\nEcho Audio Terminal: https://echo.app/whisper/${whisper.id}"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Whisper")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}
