package com.echo.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.echo.app.data.models.ReverbItem
import com.echo.app.ui.components.EchoPostShareModal
import com.echo.app.ui.components.InlineReverbsThread
import com.echo.app.ui.theme.AccentFire
import com.echo.app.ui.theme.DarkNeutral800
import com.echo.app.ui.theme.DarkNeutral900
import com.echo.app.ui.theme.Neutral500
import com.echo.app.ui.theme.PitchBlack
import com.echo.app.ui.theme.PureWhite
import com.echo.app.ui.viewmodel.MainViewModel

@Composable
fun FrequencyScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val echoes by viewModel.echoes.collectAsState()
    val reverbs by viewModel.reverbs.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isAutoScanActive by viewModel.isAutoScanActive.collectAsState()
    val playingPostId by viewModel.playingPostId.collectAsState()
    val playbackProgress by viewModel.playbackProgress.collectAsState()
    val context = LocalContext.current

    var selectedTopicFilter by remember { mutableStateOf("ALL BROADCASTS") }
    val topicTabs = listOf("ALL BROADCASTS", "TECH", "AUDIO", "MUSIC", "PHILOSOPHY", "DEBATES")

    var postToDelete by remember { mutableStateOf<EchoPost?>(null) }
    var postToShare by remember { mutableStateOf<EchoPost?>(null) }
    var challengePostTarget by remember { mutableStateOf<EchoPost?>(null) }
    var postToFork by remember { mutableStateOf<EchoPost?>(null) }
    var lineagePostTarget by remember { mutableStateOf<EchoPost?>(null) }

    val filteredEchoes = remember(echoes, selectedTopicFilter) {
        if (selectedTopicFilter == "ALL BROADCASTS") {
            echoes
        } else {
            echoes.filter { it.categoryTag.equals(selectedTopicFilter, ignoreCase = true) }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(PitchBlack)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// THE FREQUENCY — BROADCAST FEED (${filteredEchoes.size})",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Neutral500,
                        letterSpacing = 1.sp
                    )

                    // Small, compact Auto-Scan Radio toggle
                    Box(
                        modifier = Modifier
                            .border(1.dp, if (isAutoScanActive) AccentFire else DarkNeutral800)
                            .background(if (isAutoScanActive) DarkNeutral900 else PitchBlack)
                            .clickable { viewModel.toggleAutoScan() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("auto_scan_radio_banner")
                    ) {
                        Text(
                            text = if (isAutoScanActive) "📻 AUTO-SCAN [ ON ]" else "📻 AUTO-SCAN [ OFF ]",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAutoScanActive) AccentFire else Neutral500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Topic & Algorithm Filter Chips Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(topicTabs) { topic ->
                            val isSel = selectedTopicFilter == topic
                            Box(
                                modifier = Modifier
                                    .border(1.dp, if (isSel) PureWhite else DarkNeutral800)
                                    .background(if (isSel) DarkNeutral900 else PitchBlack)
                                    .clickable { selectedTopicFilter = topic }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("filter_topic_$topic"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = topic,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) PureWhite else Neutral500
                                )
                            }
                        }
                    }
                }
            }

            if (filteredEchoes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkNeutral900)
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Neutral500,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "NO ECHOES FOUND FOR TOPIC '$selectedTopicFilter'.",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Neutral500,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredEchoes, key = { it.id }) { echo ->
                    val isPlaying = playingPostId == echo.id
                    val postReverbs = reverbs.filter { it.postId == echo.id }

                    EchoCard(
                        echo = echo,
                        postReverbs = postReverbs,
                        isPlaying = isPlaying,
                        progress = if (isPlaying) playbackProgress else 0f,
                        onPlayToggle = { viewModel.togglePlayEcho(echo.id, echo.durationSec) },
                        onPulseToggle = { 
                            val currentSec = if (isPlaying) (playbackProgress * echo.durationSec).toInt().coerceAtLeast(1) else 1
                            viewModel.togglePulseWithTelemetry(echo.id, currentSec)
                        },
                        onForkClick = { postToFork = echo },
                        onLineageClick = { lineagePostTarget = echo },
                        onDeleteClick = { postToDelete = echo },
                        onShareClick = { postToShare = echo },
                        onChallengeClick = { challengePostTarget = echo },
                        onAddReverb = { text, isVoice, replyTo -> viewModel.addReverb(echo.id, text, isVoice, replyTo) },
                        onLikeReverb = { revId -> viewModel.toggleLikeReverb(revId) },
                        onDeleteReverb = { revId -> viewModel.deleteReverb(revId) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Fork Audio Drop Modal Dialog
        if (postToFork != null) {
            val source = postToFork!!
            var forkCaptionText by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { postToFork = null },
                title = {
                    Text(
                        text = "↳ FORK AUDIO DROP",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "REMIXING @${source.authorHandle} [DEPTH: ${source.forkDepth + 1}]:\n\"${source.caption}\"",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp,
                            color = Neutral500
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = forkCaptionText,
                            onValueChange = { forkCaptionText = it },
                            placeholder = {
                                Text("Add your remix drop voice caption...", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Neutral500)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PureWhite,
                                unfocusedBorderColor = DarkNeutral800,
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.forkEcho(source, forkCaptionText)
                        postToFork = null
                    }) {
                        Text("PUBLISH FORK ↳", fontFamily = FontFamily.Monospace, color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { postToFork = null }) {
                        Text("CANCEL", fontFamily = FontFamily.Monospace, color = Neutral500)
                    }
                },
                containerColor = PitchBlack,
                modifier = Modifier.border(1.dp, DarkNeutral800)
            )
        }

        // Lineage Detail Modal Dialog
        if (lineagePostTarget != null) {
            val target = lineagePostTarget!!
            AlertDialog(
                onDismissRequest = { lineagePostTarget = null },
                title = {
                    Text(
                        text = "🌳 DROP LINEAGE TREE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "ROOT SOURCE DROP:",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Neutral500
                        )
                        Text(
                            text = "@${target.sourceAuthorHandle ?: "UNKNOWN"}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "↳ FORK DEPTH: LEVEL ${target.forkDepth}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = AccentFire
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "CURRENT FORK CAPTION:\n\"${target.caption}\"",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp,
                            color = Neutral500
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { lineagePostTarget = null }) {
                        Text("CLOSE", fontFamily = FontFamily.Monospace, color = PureWhite)
                    }
                },
                containerColor = PitchBlack,
                modifier = Modifier.border(1.dp, DarkNeutral800)
            )
        }

        // Quick Topic Challenge Launch Modal
        if (challengePostTarget != null) {
            val target = challengePostTarget!!
            AlertDialog(
                onDismissRequest = { challengePostTarget = null },
                title = {
                    Text(
                        text = "INVITE TO STAGE CHALLENGE?",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                },
                text = {
                    Text(
                        text = "Challenge ${target.authorHandle} to a live audio debate on motion:\n\n\"${target.caption}\"",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 13.sp,
                        color = Neutral500
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.inviteToChallengeOnTopic(target.caption, target.authorHandle)
                        challengePostTarget = null
                    }) {
                        Text("LAUNCH CHALLENGE", fontFamily = FontFamily.Monospace, color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { challengePostTarget = null }) {
                        Text("CANCEL", fontFamily = FontFamily.Monospace, color = Neutral500)
                    }
                },
                containerColor = PitchBlack,
                modifier = Modifier.border(1.dp, DarkNeutral800)
            )
        }

        // Share Card Modal
        if (postToShare != null) {
            EchoPostShareModal(
                echo = postToShare!!,
                onClose = { postToShare = null },
                onShare = {
                    viewModel.shareEcho(context, postToShare!!)
                    postToShare = null
                }
            )
        }

        // Delete Confirmation Dialog
        if (postToDelete != null) {
            AlertDialog(
                onDismissRequest = { postToDelete = null },
                title = {
                    Text(
                        text = "DELETE FREQUENCY DROP?",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to permanently purge this post from the network frequency?",
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.sp,
                        color = Neutral500
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        postToDelete?.let { viewModel.deleteEcho(it.id) }
                        postToDelete = null
                    }) {
                        Text("PURGE", fontFamily = FontFamily.Monospace, color = AccentFire)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { postToDelete = null }) {
                        Text("CANCEL", fontFamily = FontFamily.Monospace, color = Neutral500)
                    }
                },
                containerColor = PitchBlack,
                modifier = Modifier.border(1.dp, DarkNeutral800)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EchoCard(
    echo: EchoPost,
    postReverbs: List<ReverbItem>,
    isPlaying: Boolean,
    progress: Float,
    onPlayToggle: () -> Unit,
    onPulseToggle: () -> Unit,
    onForkClick: () -> Unit = {},
    onLineageClick: () -> Unit = {},
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
    onChallengeClick: () -> Unit,
    onAddReverb: (text: String, isVoice: Boolean, replyTo: String?) -> Unit,
    onLikeReverb: (reverbId: String) -> Unit,
    onDeleteReverb: (reverbId: String) -> Unit
) {
    var showReverbs by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkNeutral900)
            .background(PitchBlack)
            .padding(16.dp)
            .testTag("echo_card_${echo.id}")
    ) {
        // Fork Lineage Badge Header if drop is a Fork
        if (echo.isFork && !echo.sourceAuthorHandle.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkNeutral800)
                    .background(DarkNeutral900)
                    .clickable { onLineageClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SOURCE: @${echo.sourceAuthorHandle} ↳ FORK [DEPTH: ${echo.forkDepth}]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(TAP FOR LINEAGE)",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        color = AccentFire
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Author Handle, Topic Tag Badge, and Duration/Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = echo.authorHandle,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Topic Tag Badge
                Box(
                    modifier = Modifier
                        .border(1.dp, DarkNeutral800)
                        .background(DarkNeutral900)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "#${echo.categoryTag.uppercase()}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = Neutral500
                    )
                }

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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = echo.duration,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Neutral500
                )
                Spacer(modifier = Modifier.width(4.dp))

                // Delete Post option
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(28.dp).testTag("delete_echo_${echo.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Post",
                        tint = Neutral500,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Caption text with formatted quotes
        Text(
            text = "\"${echo.caption}\"",
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 15.sp,
            color = PureWhite,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Waveform Audio Player Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkNeutral800)
                .background(DarkNeutral900)
                .padding(12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Play/Pause Action Button
                    Box(
                        modifier = Modifier
                            .border(1.dp, PureWhite)
                            .clickable { onPlayToggle() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("play_button_${echo.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = PureWhite,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPlaying) "PAUSE" else "PLAY",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Equalizer Waveform Bars
                    WaveformEqualizer(isPlaying = isPlaying)
                }

                if (isPlaying) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = PureWhite,
                        trackColor = DarkNeutral800
                    )
                }

                // Time-Stamped Telemetry Heatmap Bar
                val telemetryMap = remember(echo.telemetryNodes) {
                    val map = mutableMapOf<Int, Int>()
                    if (echo.telemetryNodes.isNotBlank()) {
                        echo.telemetryNodes.split(",").forEach { pair ->
                            val parts = pair.split(":")
                            if (parts.size == 2) {
                                val sec = parts[0].trim().toIntOrNull()
                                val count = parts[1].trim().toIntOrNull()
                                if (sec != null && count != null) map[sec] = count
                            }
                        }
                    }
                    map
                }
                val peakNode = telemetryMap.maxByOrNull { it.value }

                if (telemetryMap.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ REACTION HEATMAP",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            color = Neutral500
                        )
                        if (peakNode != null) {
                            Text(
                                text = "PEAK: ${peakNode.key}s (${peakNode.value} PULSES)",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                color = AccentFire,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val maxDurationSec = maxOf(15, echo.durationSec)
                        val maxVal = (telemetryMap.values.maxOrNull() ?: 1).toFloat()
                        for (sec in 1..maxDurationSec) {
                            val count = telemetryMap[sec] ?: 0
                            val heightPct = if (count > 0) (count / maxVal).coerceIn(0.25f, 1f) else 0.1f
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height((10 * heightPct).dp)
                                    .background(if (count > 0) AccentFire else DarkNeutral800)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Bar: Vibe, Fork, Reverb, Challenge, Share
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Vibe / Pulse Button
            Row(
                modifier = Modifier
                    .clickable { onPulseToggle() }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
                    .testTag("pulse_button_${echo.id}"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Vibe",
                    tint = if (echo.isPulsed) AccentFire else Neutral500,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${echo.pulseCount} VIBED",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = if (echo.isPulsed) AccentFire else Neutral500
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Fork Button
                Row(
                    modifier = Modifier
                        .border(1.dp, DarkNeutral800)
                        .clickable { onForkClick() }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                        .testTag("fork_button_${echo.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "↳ FORK",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))
                // Drop Reverb Expand Button
                Row(
                    modifier = Modifier
                        .clickable { showReverbs = !showReverbs }
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                        .testTag("reverb_toggle_${echo.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Drop Reverb",
                        tint = if (showReverbs) PureWhite else Neutral500,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${postReverbs.size} REVERB",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = if (showReverbs) PureWhite else Neutral500
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Topic Challenge Invite Button
                Row(
                    modifier = Modifier
                        .border(1.dp, DarkNeutral800)
                        .clickable { onChallengeClick() }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                        .testTag("challenge_topic_button_${echo.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚔️ CHALLENGE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Put On (Share) Button
                Row(
                    modifier = Modifier
                        .clickable { onShareClick() }
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                        .testTag("share_button_${echo.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Put On",
                        tint = Neutral500,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "SHARE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = Neutral500
                    )
                }
            }
        }

        // Expandable Inline Reverbs Thread
        AnimatedVisibility(visible = showReverbs) {
            Spacer(modifier = Modifier.height(12.dp))
            InlineReverbsThread(
                postId = echo.id,
                reverbs = postReverbs,
                postAuthorHandle = echo.authorHandle,
                onAddReverb = { text, isVoice, replyTo ->
                    onAddReverb(text, isVoice, replyTo)
                },
                onLikeReverb = onLikeReverb,
                onDeleteReverb = onDeleteReverb
            )
        }
    }
}

@Composable
fun WaveformEqualizer(isPlaying: Boolean) {
    val barHeights = listOf(6, 14, 22, 12, 28, 18, 10, 24, 16, 20, 8, 26, 14, 22, 10)
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(28.dp)
    ) {
        barHeights.forEachIndexed { index, baseHeight ->
            val animatedHeight by infiniteTransition.animateFloat(
                initialValue = 4f,
                targetValue = baseHeight.toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 350 + (index % 5) * 80,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            )

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(if (isPlaying) animatedHeight.dp else (baseHeight * 0.35f).dp)
                    .background(if (isPlaying) PureWhite else Neutral500)
            )
        }
    }
}
