package com.echo.app.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echo.app.data.models.ReverbItem
import com.echo.app.ui.screens.WaveformEqualizer
import com.echo.app.ui.theme.AccentFire
import com.echo.app.ui.theme.DarkNeutral800
import com.echo.app.ui.theme.DarkNeutral900
import com.echo.app.ui.theme.Neutral500
import com.echo.app.ui.theme.PitchBlack
import com.echo.app.ui.theme.PureWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InlineReverbsThread(
    postId: String,
    reverbs: List<ReverbItem>,
    onAddReverb: (content: String, isVoice: Boolean, replyToHandle: String?) -> Unit,
    onLikeReverb: (reverbId: String) -> Unit,
    onDeleteReverb: (reverbId: String) -> Unit,
    modifier: Modifier = Modifier,
    postAuthorHandle: String = "@AUTHOR"
) {
    var replyToTarget by remember { mutableStateOf<String?>(null) }
    var reverbInputText by remember { mutableStateOf("") }
    var isRecordingVoiceReverb by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DarkNeutral800)
            .background(DarkNeutral900.copy(alpha = 0.6f))
            .padding(12.dp)
            .testTag("inline_reverbs_thread_$postId")
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "// INLINE REVERBS (${reverbs.size})",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                letterSpacing = 1.sp
            )
            Text(
                text = "INSTAGRAM-STYLE THREAD",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = Neutral500
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (reverbs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkNeutral800)
                    .background(PitchBlack)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NO REVERBS YET. BE THE FIRST TO REVERB & @TAG",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Neutral500,
                    letterSpacing = 1.sp
                )
            }
        } else {
            // Render each reverb item
            reverbs.forEach { rev ->
                InlineReverbCard(
                    reverb = rev,
                    onLike = { onLikeReverb(rev.id) },
                    onDelete = { onDeleteReverb(rev.id) },
                    onReply = { author ->
                        replyToTarget = author
                        if (!reverbInputText.contains(author)) {
                            reverbInputText = if (reverbInputText.isBlank()) "$author " else "$reverbInputText $author "
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Active Reply-To Chip indicator
        if (replyToTarget != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .background(DarkNeutral800)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = "Replying",
                        tint = AccentFire,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Replying to ${replyToTarget!!}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = AccentFire,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear Reply Target",
                    tint = Neutral500,
                    modifier = Modifier
                        .size(14.dp)
                        .clickable { replyToTarget = null }
                )
            }
        }

        // @Tag Quick Chips
        Text(
            text = "@TAG HANDLE TO MENTION:",
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
            val tagCandidates = listOf(postAuthorHandle, "@ZARA.IQ", "@OG_VIBE", "@ARYAN.V", "@LUNA.SOUND", "@YOU")
                .distinct()
            tagCandidates.forEach { tag ->
                Box(
                    modifier = Modifier
                        .border(1.dp, DarkNeutral800)
                        .background(PitchBlack)
                        .clickable {
                            if (!reverbInputText.contains(tag)) {
                                reverbInputText = if (reverbInputText.isBlank()) "$tag " else "$reverbInputText $tag "
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

        Spacer(modifier = Modifier.height(10.dp))

        // Input Field for Reverb
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = reverbInputText,
                onValueChange = { reverbInputText = it },
                placeholder = {
                    Text(
                        text = if (replyToTarget != null) "Reverb reply to $replyToTarget..." else "Drop inline reverb or @tag...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Neutral500
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("inline_reverb_input_$postId"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PureWhite,
                    unfocusedBorderColor = DarkNeutral800,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Voice Reverb Button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .border(1.dp, if (isRecordingVoiceReverb) AccentFire else PureWhite)
                    .background(if (isRecordingVoiceReverb) AccentFire.copy(alpha = 0.2f) else PitchBlack)
                    .clickable {
                        isRecordingVoiceReverb = !isRecordingVoiceReverb
                        if (!isRecordingVoiceReverb) {
                            val msg = if (reverbInputText.isNotBlank()) reverbInputText else "[VOICE REVERB DROP]"
                            onAddReverb(msg, true, replyToTarget)
                            reverbInputText = ""
                            replyToTarget = null
                        }
                    }
                    .testTag("voice_reverb_btn_$postId"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Reverb",
                    tint = if (isRecordingVoiceReverb) AccentFire else PureWhite,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Send Button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .border(1.dp, PureWhite)
                    .background(PureWhite)
                    .clickable {
                        if (reverbInputText.isNotBlank()) {
                            onAddReverb(reverbInputText, false, replyToTarget)
                            reverbInputText = ""
                            replyToTarget = null
                        }
                    }
                    .testTag("send_reverb_btn_$postId"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Reverb",
                    tint = PitchBlack,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun InlineReverbCard(
    reverb: ReverbItem,
    onLike: () -> Unit,
    onDelete: () -> Unit,
    onReply: (authorHandle: String) -> Unit
) {
    var isMiniAudioPlaying by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkNeutral800)
            .background(PitchBlack)
            .padding(10.dp)
            .testTag("inline_reverb_item_${reverb.id}")
    ) {
        // Author & Action bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = reverb.authorHandle,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )

                if (!reverb.replyToHandle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "↩ ${reverb.replyToHandle}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = AccentFire,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // ❤️ Pulse / Like button
                Row(
                    modifier = Modifier
                        .clickable { onLike() }
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (reverb.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Pulse Reverb",
                        tint = if (reverb.isLiked) AccentFire else Neutral500,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${reverb.likeCount}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = if (reverb.isLiked) AccentFire else Neutral500
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // ↩ Reverb-on-reverb (Reply) button
                IconButton(
                    onClick = { onReply(reverb.authorHandle) },
                    modifier = Modifier.size(24.dp).testTag("reply_reverb_${reverb.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = "Reverb-on-reverb reply",
                        tint = Neutral500,
                        modifier = Modifier.size(13.dp)
                    )
                }

                var isFlagged by remember { mutableStateOf(false) }

                // Flag / Cancel Report Button
                Box(
                    modifier = Modifier
                        .clickable { isFlagged = !isFlagged }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isFlagged) "[ FLAGGED ]" else "FLAG",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = if (isFlagged) AccentFire else Neutral500,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Delete option
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Reverb",
                        tint = Neutral500,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Mini Audio Player for Voice Reverbs or Text Comment Content
        if (reverb.isVoice) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkNeutral900)
                    .border(1.dp, DarkNeutral800)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { isMiniAudioPlaying = !isMiniAudioPlaying }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isMiniAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isMiniAudioPlaying) "Pause Mini Audio" else "Play Mini Audio",
                            tint = PureWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = reverb.content,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = PureWhite
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WaveformEqualizer(isPlaying = isMiniAudioPlaying)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = reverb.audioDuration,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = Neutral500
                        )
                    }
                }

                if (isMiniAudioPlaying) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = PureWhite,
                        trackColor = DarkNeutral800
                    )
                }
            }
        } else {
            Text(
                text = reverb.content,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 12.sp,
                color = PureWhite,
                lineHeight = 18.sp
            )
        }
    }
}
