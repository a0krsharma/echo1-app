package com.echo.app.util

import com.echo.app.data.models.WhisperItem
import java.util.concurrent.TimeUnit

/**
 * Manages the calculation and formatting of [ PHASE-LOCK ] Resonance streaks (in Hz)
 * between users exchanging audio messages based on time differences.
 */
object ResonanceStreakManager {

    data class StreakInfo(
        val streakDays: Int,
        val hz: Int,
        val phaseState: String, // "PHASE-LOCKED", "DRIFTING", "DECOHERENT"
        val formattedStatusText: String, // e.g., "[ PHASE-LOCKED : 430 Hz ]" or "[ DRIFTING : 03H LEFT ]"
        val handleStreakText: String // e.g., "@NOVA_11 [ 430 Hz ]"
    )

    /**
     * Calculates Hz streak based on elapsed time between last audio message timestamp and current time.
     * Base frequency: 10 Hz per consecutive active day.
     */
    fun calculateStreak(
        lastInteractionTimestamp: Long,
        currentTimeMillis: Long = System.currentTimeMillis(),
        recipientHandle: String = "@USER"
    ): StreakInfo {
        val diffMillis = (currentTimeMillis - lastInteractionTimestamp).coerceAtLeast(0L)
        val hoursElapsed = TimeUnit.MILLISECONDS.toHours(diffMillis)
        
        // Active within last 24 hours: active streak days count
        val daysElapsed = (hoursElapsed / 24).toInt()
        val streakDays = (daysElapsed + 1).coerceAtLeast(1)
        val hz = streakDays * 10

        val phaseState: String
        val formattedStatusText: String

        if (hoursElapsed < 20) {
            phaseState = "PHASE-LOCKED"
            formattedStatusText = if (streakDays >= 30) {
                "█ PHASE-LOCKED : $hz Hz █"
            } else {
                "[ PHASE-LOCKED : $hz Hz ]"
            }
        } else if (hoursElapsed in 20..24) {
            phaseState = "DRIFTING"
            val hoursLeft = (24 - hoursElapsed).coerceAtLeast(1)
            val formattedHours = String.format("%02dH", hoursLeft)
            formattedStatusText = "[ DRIFTING : ${formattedHours} LEFT ]"
        } else {
            phaseState = "DECOHERENT"
            formattedStatusText = "[ STATIC ]"
        }

        val formattedHandle = if (recipientHandle.startsWith("@")) recipientHandle else "@$recipientHandle"
        val handleStreakText = when (phaseState) {
            "DECOHERENT" -> "$formattedHandle [ STATIC ]"
            "DRIFTING" -> "$formattedHandle [ LOSING SIGNAL : $hz Hz ]"
            else -> "$formattedHandle [ $hz Hz ]"
        }

        return StreakInfo(
            streakDays = streakDays,
            hz = hz,
            phaseState = phaseState,
            formattedStatusText = formattedStatusText,
            handleStreakText = handleStreakText
        )
    }

    /**
     * Convenience function to get calculated StreakInfo for a WhisperItem
     */
    fun getStreakForWhisper(whisper: WhisperItem, currentTimeMillis: Long = System.currentTimeMillis()): StreakInfo {
        return calculateStreak(
            lastInteractionTimestamp = whisper.lastInteractionTimestamp,
            currentTimeMillis = currentTimeMillis,
            recipientHandle = whisper.recipientHandle
        )
    }
}
