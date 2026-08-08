package com.echo.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "echoes")
@Serializable
data class EchoPost(
    @PrimaryKey val id: String,
    val authorHandle: String,
    val authorUid: String,
    val caption: String,
    val duration: String,
    val durationSec: Int,
    val pulseCount: Int,
    val isPulsed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val audioUrl: String = "",
    val reverbCount: Int = 0,
    val taggedHandles: String = "",
    val categoryTag: String = "TECH",
    val isFork: Boolean = false,
    val sourcePostId: String? = null,
    val sourceAuthorHandle: String? = null,
    val forkDepth: Int = 0,
    val telemetryNodes: String = "5:12,12:28,22:45"
)

@Entity(tableName = "reverbs")
@Serializable
data class ReverbItem(
    @PrimaryKey val id: String,
    val postId: String,
    val authorHandle: String,
    val content: String,
    val isVoice: Boolean = false,
    val audioDuration: String = "00:08",
    val likeCount: Int = 0,
    val isLiked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val replyToHandle: String? = null
)

@Entity(tableName = "waves")
@Serializable
data class WaveItem(
    @PrimaryKey val id: String,
    val authorHandle: String,
    val caption: String,
    val duration: String = "00:30",
    val durationSec: Int = 30,
    val pulseCount: Int = 342,
    val isPulsed: Boolean = false,
    val reverbCount: Int = 12,
    val audioUrl: String = "",
    val accentThemeIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val categoryTag: String = "AUDIO"
)

@Entity(tableName = "clashes")
@Serializable
data class ClashItem(
    @PrimaryKey val id: String,
    val title: String,
    val topic: String,
    val handleA: String,
    val posA: String,
    val votesA: Int,
    val handleB: String,
    val posB: String,
    val votesB: Int,
    val listeners: Int = 120,
    val isActive: Boolean = true,
    val userVotedSide: String? = null, // "A" or "B" or null
    val isPrivate: Boolean = false,
    val invitedHandles: String = "",
    val topicCategory: String = "DEBATES"
)

@Entity(tableName = "user_profile")
@Serializable
data class EchoUserProfile(
    @PrimaryKey val uid: String,
    val handle: String,
    val displayName: String,
    val auraScore: Int,
    val voiceBioDuration: String? = null,
    val voiceBioUrl: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val isGoogleSignedIn: Boolean = false,
    val googleAccountName: String? = null,
    val likedTopics: String = "TECH,AUDIO,MUSIC,DEBATES,PHILOSOPHY",
    val txUptimeDays: Int = 14,
    val lastTxTimestamp: Long = System.currentTimeMillis(),
    val signalDb: Int = 24,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "whispers")
@Serializable
data class WhisperItem(
    @PrimaryKey val id: String,
    val senderHandle: String,
    val recipientHandle: String,
    val message: String,
    val duration: String = "00:15",
    val timestamp: Long = System.currentTimeMillis(),
    val phaseLockHz: Int = 140,
    val phaseLockStreakDays: Int = 14,
    val lastInteractionTimestamp: Long = System.currentTimeMillis(),
    val phaseState: String = "PHASE-LOCKED" // PHASE-LOCKED, DRIFTING, DECOHERENT
)

@Entity(tableName = "notifications")
@Serializable
data class AppNotificationItem(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val category: String, // PULSES, REVERBS, ORBITERS, STAGE
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "terminal_settings")
@Serializable
data class TerminalSettings(
    @PrimaryKey val id: Int = 1,
    val pingPulses: Boolean = true,
    val pingReverbs: Boolean = true,
    val pingOnFire: Boolean = true,
    val pingLockIns: Boolean = false,
    val pingStage: Boolean = true,
    val privateAcc: Boolean = false,
    val auraVisible: Boolean = true,
    val anonMode: Boolean = false,
    val lockApproval: Boolean = false,
    val yapControl: String = "EVERYONE",
    val echoControl: String = "EVERYONE",
    val whoCanWhisper: String = "ORBITERS",
    val audioQuality: String = "HIGH",
    val autoTranscribe: Boolean = false,
    val autoPlay: Boolean = true
)
