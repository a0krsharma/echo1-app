package com.echo.app.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echo.app.ui.theme.DarkNeutral800
import com.echo.app.ui.theme.DarkNeutral900
import com.echo.app.ui.theme.Neutral500
import com.echo.app.ui.theme.PitchBlack
import com.echo.app.ui.theme.PureWhite
import com.echo.app.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioScreen(
    viewModel: MainViewModel,
    onPublished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingMs by viewModel.recordingMs.collectAsState()
    val audioFile by viewModel.recordedAudioFile.collectAsState()
    val caption by viewModel.recordingCaption.collectAsState()
    val isPublishing by viewModel.isPublishing.collectAsState()
    val scrollState = rememberScrollState()

    // Format timer 00:00.00
    val totalSecs = (recordingMs / 1000).toInt()
    val mins = String.format("%02d", totalSecs / 60)
    val secs = String.format("%02d", totalSecs % 60)
    val hundredths = String.format("%02d", (recordingMs % 1000) / 10)
    val timerString = "$mins:$secs.$hundredths"

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_ring")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "// STUDIO · AUDIO CAPTURE",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Neutral500,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Speak raw.\nNo filters.",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 28.sp,
                color = PureWhite
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Center Record Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Live Timer
            Text(
                text = timerString,
                fontFamily = FontFamily.Monospace,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isRecording) "// RECORDING IN PROGRESS" else if (audioFile != null) "// AUDIO RECORDED" else "// HOLD OR TAP TO RECORD",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Neutral500,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Record Button
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .border(
                        width = 2.dp,
                        color = PureWhite,
                        shape = CircleShape
                    )
                    .background(
                        color = if (isRecording) DarkNeutral900 else PitchBlack,
                        shape = CircleShape
                    )
                    .clickable {
                        if (isRecording) {
                            viewModel.stopStudioRecording()
                        } else {
                            viewModel.startStudioRecording()
                        }
                    }
                    .testTag("studio_record_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isRecording) "Stop" else "Record",
                    tint = PureWhite,
                    modifier = Modifier.size(36.dp)
                )
            }

            if (isRecording) {
                Spacer(modifier = Modifier.height(16.dp))
                WaveformEqualizer(isPlaying = true)
            } else if (audioFile != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .border(1.dp, PureWhite)
                        .background(DarkNeutral900)
                        .clickable { viewModel.previewRecordedAudio() }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("studio_preview_sound_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "▶ PLAY PREVIEW SOUND",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Bottom Form Section (Caption & Publish)
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "TAG PROFILES IN ECHO:",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = Neutral500
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("@ZARA.IQ", "@OG_VIBE", "@ARYAN.V").forEach { tag ->
                    Box(
                        modifier = Modifier
                            .border(1.dp, DarkNeutral800)
                            .background(DarkNeutral900)
                            .clickable {
                                if (!caption.contains(tag)) {
                                    val newCap = if (caption.isBlank()) "with $tag " else "$caption $tag "
                                    viewModel.setRecordingCaption(newCap)
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tag,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = PureWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = caption,
                onValueChange = { viewModel.setRecordingCaption(it) },
                label = {
                    Text(
                        text = "ADD CAPTION / MOTION",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Neutral500
                    )
                },
                placeholder = {
                    Text(
                        text = "State your thought...",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 13.sp,
                        color = Neutral500
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PureWhite,
                    unfocusedBorderColor = DarkNeutral800,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Prominent Submission Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PureWhite)
                    .background(PureWhite)
                    .clickable(enabled = !isPublishing) {
                        if (isRecording) {
                            viewModel.stopStudioRecording()
                        }
                        viewModel.publishStudioRecording(onPublished)
                    }
                    .padding(vertical = 16.dp)
                    .testTag("publish_echo_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isPublishing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = PitchBlack,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (isPublishing) "TRANSMITTING TO FREQUENCY..." else "[ DROP ECHO TO FREQUENCY ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PitchBlack,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

