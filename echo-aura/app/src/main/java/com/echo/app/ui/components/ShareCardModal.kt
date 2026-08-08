package com.echo.app.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echo.app.data.models.EchoPost
import com.echo.app.data.models.EchoUserProfile
import com.echo.app.ui.screens.WaveformEqualizer
import com.echo.app.ui.theme.AccentFire
import com.echo.app.ui.theme.DarkNeutral800
import com.echo.app.ui.theme.DarkNeutral900
import com.echo.app.ui.theme.Neutral500
import com.echo.app.ui.theme.PitchBlack
import com.echo.app.ui.theme.PureWhite

@Composable
fun EchoPostShareModal(
    echo: EchoPost,
    onClose: () -> Unit,
    onShare: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack.copy(alpha = 0.92f))
            .clickable { onClose() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PureWhite)
                .background(PitchBlack)
                .clickable(enabled = false) {}
                .padding(20.dp)
                .testTag("echo_share_modal"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "// ECHO. FREQUENCY CARD PREVIEW",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Neutral500,
                    letterSpacing = 1.sp
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = PureWhite,
                    modifier = Modifier
                        .clickable { onClose() }
                        .size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Brutalist Share Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PureWhite)
                    .background(DarkNeutral900)
                    .padding(16.dp)
            ) {
                // Brand Header Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Echo.",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "[RAW FREQUENCY]",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = AccentFire
                        )
                    }

                    Text(
                        text = echo.duration,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Neutral500
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Author & Tagged
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = echo.authorHandle,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )

                    if (echo.taggedHandles.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "with ${echo.taggedHandles}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = AccentFire
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Caption
                Text(
                    text = "\"${echo.caption}\"",
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    color = PureWhite,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Waveform Equalizer Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PitchBlack)
                        .border(1.dp, DarkNeutral800)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        WaveformEqualizer(isPlaying = true)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer Metrics & Frequency Code
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = AccentFire,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${echo.pulseCount}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = PureWhite
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${echo.reverbCount}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = PureWhite
                        )
                    }

                    Text(
                        text = "|||| ||| |||| |",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Neutral500,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Copy Card Text
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, PureWhite)
                            .background(DarkNeutral900)
                            .clickable {
                                val clipManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Echo Frequency Card", getFormattedEchoCardText(echo))
                                clipManager.setPrimaryClip(clip)
                                Toast.makeText(context, "Frequency Card Copied!", Toast.LENGTH_SHORT).show()
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = PureWhite,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "COPY CARD",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = PureWhite,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Share to Social Media
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, PureWhite)
                            .background(PureWhite)
                            .clickable { onShare() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = PitchBlack,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SHARE CARD",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PitchBlack,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // Story / Status Export Button (Instagram, WhatsApp Status, Snapchat, X)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AccentFire)
                        .background(DarkNeutral900)
                        .clickable {
                            val storyText = """
                                ┌──────────────────────────────────────────────┐
                                │  ECHO. RAW FREQUENCY CARD                    │
                                ├──────────────────────────────────────────────┤
                                │  VOICE DROP BY ${echo.authorHandle}            │
                                │                                              │
                                │  "${echo.caption}"                           │
                                │                                              │
                                │  ⏱ DURATION: ${echo.duration} | ⚡ PULSES: ${echo.pulseCount}│
                                │  🎧 LISTEN LIVE: https://echo.app/drop/${echo.id}│
                                └──────────────────────────────────────────────┘
                                Posted via Echo. Frequency Network
                            """.trimIndent()

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Echo Story Post")
                                putExtra(Intent.EXTRA_TEXT, storyText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Post to Story / Status via..."))
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📱 POST TO STORY / WHATSAPP STATUS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentFire,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EchoProfileShareModal(
    profile: EchoUserProfile?,
    onClose: () -> Unit,
    onShare: () -> Unit
) {
    val context = LocalContext.current
    val p = profile ?: EchoUserProfile("you-001", "@YOU", "Raw thoughts. Unfiltered frequencies.", 1840, "00:15", null)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack.copy(alpha = 0.92f))
            .clickable { onClose() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PureWhite)
                .background(PitchBlack)
                .clickable(enabled = false) {}
                .padding(20.dp)
                .testTag("profile_share_modal"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "// ECHO. PROFILE CARD PREVIEW",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Neutral500,
                    letterSpacing = 1.sp
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = PureWhite,
                    modifier = Modifier
                        .clickable { onClose() }
                        .size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Card Preview
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PureWhite)
                    .background(DarkNeutral900)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = p.handle,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "AURA SCORE: ${p.auraScore} PTS",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = AccentFire
                        )
                    }

                    Text(
                        text = "Echo.",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 18.sp,
                        color = PureWhite
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "\"${p.displayName}\"",
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp,
                    color = PureWhite,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PitchBlack)
                        .border(1.dp, DarkNeutral800)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${p.auraScore}", fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                        Text(text = "AURA POINTS", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = p.voiceBioDuration ?: "00:15", fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                        Text(text = "VOICE BIO", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, PureWhite)
                        .background(DarkNeutral900)
                        .clickable {
                            val clipManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Echo Profile Card", getFormattedProfileCardText(p))
                            clipManager.setPrimaryClip(clip)
                            Toast.makeText(context, "Profile Card Copied!", Toast.LENGTH_SHORT).show()
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Profile",
                            tint = PureWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "COPY CARD",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = PureWhite,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, PureWhite)
                        .background(PureWhite)
                        .clickable { onShare() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Profile",
                            tint = PitchBlack,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SHARE CARD",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PitchBlack,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

fun getFormattedEchoCardText(echo: EchoPost): String {
    val tagStr = if (echo.taggedHandles.isNotBlank()) " WITH ${echo.taggedHandles}" else ""
    return """
        ┌────────────────────────────────────────────────┐
        │  ECHO. UNFILTERED AUDIO FREQUENCY CARD          │
        ├────────────────────────────────────────────────┤
        │ AUTHOR: ${echo.authorHandle}$tagStr
        ├────────────────────────────────────────────────┤
        │ "${echo.caption}"
        ├────────────────────────────────────────────────┤
        │ WAVEFORM:  |█|▌|█|▌|█|▌|█|▌ (${echo.duration})   │
        │ METRICS:   🔥 ${echo.pulseCount} PULSES  |  🔁 ${echo.reverbCount} REVERBS │
        └────────────────────────────────────────────────┘
        🔊 Listen & Reverb: https://echo.app/post/${echo.id}
    """.trimIndent()
}

fun getFormattedProfileCardText(p: EchoUserProfile): String {
    return """
        ┌────────────────────────────────────────────────┐
        │  ECHO. PROFILE FREQUENCY CARD                  │
        ├────────────────────────────────────────────────┤
        │ HANDLE: ${p.handle}
        │ DISPLAY NAME: ${p.displayName}
        │ AURA SCORE: ${p.auraScore} PTS
        │ VOICE BIO DURATION: ${p.voiceBioDuration ?: "00:15"}
        └────────────────────────────────────────────────┘
        🎙️ Tune in: https://echo.app/profile/${p.handle.replace("@", "")}
    """.trimIndent()
}
