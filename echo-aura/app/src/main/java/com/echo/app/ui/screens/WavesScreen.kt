package com.echo.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echo.app.data.models.EchoPost
import com.echo.app.data.models.WaveItem
import com.echo.app.ui.components.EchoPostShareModal
import com.echo.app.ui.components.InlineReverbsThread
import com.echo.app.ui.theme.AccentFire
import com.echo.app.ui.theme.DarkNeutral800
import com.echo.app.ui.theme.DarkNeutral900
import com.echo.app.ui.theme.Neutral500
import com.echo.app.ui.theme.PitchBlack
import com.echo.app.ui.theme.PureWhite
import com.echo.app.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Monochrome White and Black Accent Themes for WAVES
val WaveAccentThemes = listOf(
    PureWhite,
    PureWhite,
    PureWhite,
    PureWhite,
    PureWhite
)

val WaveThemeNames = listOf("MONOCHROME", "MONOCHROME", "MONOCHROME", "MONOCHROME", "MONOCHROME")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WavesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val waves by viewModel.waves.collectAsState()
    val reverbs by viewModel.reverbs.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var activeReverbWave by remember { mutableStateOf<WaveItem?>(null) }
    var waveToShare by remember { mutableStateOf<WaveItem?>(null) }

    val pagerState = rememberPagerState(pageCount = { waves.size.coerceAtLeast(1) })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .testTag("waves_screen")
    ) {
        if (waves.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NO AUDIO WAVES FOUND ON FREQUENCY.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Neutral500
                )
            }
        } else {
            // Full-screen Vertical Snap-Scroll Pager (TikTok / Reels Style)
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val wave = waves[page]
                val accentColor = WaveAccentThemes[wave.accentThemeIndex % WaveAccentThemes.size]
                val waveReverbs = reverbs.filter { it.postId == wave.id }
                val isCurrentPage = pagerState.currentPage == page

                WaveReelCard(
                    wave = wave,
                    reverbCount = waveReverbs.size,
                    accentColor = accentColor,
                    isMuted = isMuted,
                    isCurrentPage = isCurrentPage,
                    onPulseToggle = { viewModel.togglePulseWave(wave.id) },
                    onOpenReverbs = { activeReverbWave = wave },
                    onShareClick = { waveToShare = wave },
                    onCycleTheme = { viewModel.cycleWaveAccentTheme(wave.id) },
                    onAutoAdvanceNext = {
                        coroutineScope.launch {
                            if (pagerState.currentPage < waves.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                pagerState.animateScrollToPage(0)
                            }
                        }
                    }
                )
            }

            // Top Overlay Header: ← FREQ on left, Waves in center, Sound toggle on right (Matches exact Echo design)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .align(Alignment.TopCenter)
            ) {
                // Left: ← FREQ
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .background(PitchBlack.copy(alpha = 0.65f), RoundedCornerShape(16.dp))
                        .border(1.dp, DarkNeutral800, RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "← FREQ",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                }

                // Center: Waves title
                Text(
                    text = "Waves",
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Right: Mute toggle
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PitchBlack.copy(alpha = 0.7f))
                            .border(1.dp, PureWhite, CircleShape)
                            .clickable { viewModel.toggleMute() }
                            .testTag("waves_mute_toggle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Mute Toggle",
                            tint = if (isMuted) AccentFire else PureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Top Right Overlay: Radar Top Voices & Stage
            WavesRadarAndStageOverlay(
                modifier = Modifier.align(Alignment.TopEnd)
            )

            // Discreet Navigation Arrows for Desktop / Quick Touch
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (pagerState.currentPage > 0) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(PitchBlack.copy(alpha = 0.6f))
                            .border(1.dp, DarkNeutral800, CircleShape)
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                            .testTag("waves_nav_up"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Previous Reel",
                            tint = PureWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (pagerState.currentPage < waves.size - 1) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(PitchBlack.copy(alpha = 0.6f))
                            .border(1.dp, DarkNeutral800, CircleShape)
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                            .testTag("waves_nav_down"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Next Reel",
                            tint = PureWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Inline Reverbs BottomSheet Modal for Wave
        if (activeReverbWave != null) {
            val wave = activeReverbWave!!
            val waveReverbs = reverbs.filter { it.postId == wave.id }

            ModalBottomSheet(
                onDismissRequest = { activeReverbWave = null },
                containerColor = PitchBlack,
                dragHandle = { BottomSheetDefaults.DragHandle(color = PureWhite) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    InlineReverbsThread(
                        postId = wave.id,
                        reverbs = waveReverbs,
                        postAuthorHandle = wave.authorHandle,
                        onAddReverb = { text, isVoice, replyTo ->
                            viewModel.addReverb(wave.id, text, isVoice, replyTo)
                        },
                        onLikeReverb = { revId -> viewModel.toggleLikeReverb(revId) },
                        onDeleteReverb = { revId -> viewModel.deleteReverb(revId) }
                    )
                }
            }
        }

        // Share Card Modal for Wave
        if (waveToShare != null) {
            val wavePost = EchoPost(
                id = waveToShare!!.id,
                authorHandle = waveToShare!!.authorHandle,
                authorUid = "wave-uid",
                caption = waveToShare!!.caption,
                duration = waveToShare!!.duration,
                durationSec = waveToShare!!.durationSec,
                pulseCount = waveToShare!!.pulseCount,
                isPulsed = waveToShare!!.isPulsed,
                reverbCount = waveToShare!!.reverbCount
            )
            EchoPostShareModal(
                echo = wavePost,
                onClose = { waveToShare = null },
                onShare = {
                    viewModel.shareEcho(context, wavePost)
                    waveToShare = null
                }
            )
        }
    }
}

@Composable
fun WaveReelCard(
    wave: WaveItem,
    reverbCount: Int,
    accentColor: Color,
    isMuted: Boolean,
    isCurrentPage: Boolean,
    onPulseToggle: () -> Unit,
    onOpenReverbs: () -> Unit,
    onShareClick: () -> Unit,
    onCycleTheme: () -> Unit,
    onAutoAdvanceNext: () -> Unit
) {
    var userPaused by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var elapsedSec by remember { mutableIntStateOf(0) }
    var showPlayPauseOverlay by remember { mutableStateOf(false) }

    // Auto Play / Timer Progress Logic
    val isPlaying = isCurrentPage && !userPaused && !isMuted

    // Reset when page changes
    LaunchedEffect(isCurrentPage) {
        if (isCurrentPage) {
            userPaused = false
            currentProgress = 0f
            elapsedSec = 0
        } else {
            currentProgress = 0f
            elapsedSec = 0
        }
    }

    // Audio Playback Timer & Auto Advance
    LaunchedEffect(isCurrentPage, isPlaying, isMuted) {
        if (isPlaying) {
            val totalSec = wave.durationSec.coerceAtLeast(10)
            while (currentProgress < 1f && isPlaying) {
                delay(100)
                currentProgress += (0.1f / totalSec)
                elapsedSec = (currentProgress * totalSec).toInt().coerceAtMost(totalSec)
                if (currentProgress >= 1f) {
                    delay(300)
                    onAutoAdvanceNext()
                    break
                }
            }
        }
    }

    // Continuous Vinyl Disc Rotation
    val infiniteTransition = rememberInfiniteTransition(label = "disc_rotation")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disc_angle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        DarkNeutral900,
                        PitchBlack,
                        PitchBlack
                    ),
                    radius = 1200f
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                userPaused = !userPaused
                showPlayPauseOverlay = true
            }
            .testTag("wave_reel_card_${wave.id}")
    ) {
        // --- HERO CENTER: SLEEK WHITE WAVEFORM + CIRCULAR PLAY BUTTON ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Equalizer Bars
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(90.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedColoredWaveform(
                    isPlaying = isPlaying,
                    accentColor = PureWhite
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Prominent Circular Play / Pause Button (Exact match to reference photo)
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(PitchBlack.copy(alpha = 0.5f))
                    .border(2.dp, PureWhite, CircleShape)
                    .clickable {
                        userPaused = !userPaused
                        showPlayPauseOverlay = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause Wave",
                    tint = PureWhite,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Audio Progress Line
            LinearProgressIndicator(
                progress = { currentProgress },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = PureWhite,
                trackColor = DarkNeutral800.copy(alpha = 0.6f)
            )
        }

        // --- BOTTOM LEFT AREA: Author Handle, Caption & 1 WAVE Badge ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.75f)
                .padding(start = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Handle
            Text(
                text = wave.authorHandle,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                letterSpacing = 1.sp
            )

            // Quote / Orbit Caption
            Text(
                text = "\"${wave.caption}\"",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = PureWhite,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Pill: 1 WAVE / WAVE #
            Box(
                modifier = Modifier
                    .background(DarkNeutral800, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "1 WAVE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    letterSpacing = 1.sp
                )
            }
        }

        // --- RIGHT SIDE VERTICAL ACTION BAR (Reels / TikTok Style) ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Creator Avatar Profile Circle
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(DarkNeutral900)
                    .border(1.5.dp, accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Avatar",
                    tint = PureWhite,
                    modifier = Modifier.size(36.dp)
                )
            }

            // 2. Pulse / Heart Action Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onPulseToggle() }
                    .testTag("wave_pulse_btn_${wave.id}")
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(PitchBlack.copy(alpha = 0.75f))
                        .border(1.dp, if (wave.isPulsed) AccentFire else DarkNeutral800, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Pulse Wave",
                        tint = if (wave.isPulsed) AccentFire else PureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${wave.pulseCount}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (wave.isPulsed) AccentFire else PureWhite
                )
            }

            // 3. Reverb / Comment Action Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onOpenReverbs() }
                    .testTag("wave_reverb_btn_${wave.id}")
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(PitchBlack.copy(alpha = 0.75f))
                        .border(1.dp, DarkNeutral800, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Open Reverbs",
                        tint = PureWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "$reverbCount",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }

            // 4. Share Action Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onShareClick() }
                    .testTag("wave_share_btn_${wave.id}")
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(PitchBlack.copy(alpha = 0.75f))
                        .border(1.dp, DarkNeutral800, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Wave",
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "PUT ON",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = Neutral500
                )
            }

            // 5. Spinning Sound Disc (TikTok Reels Disc Accent)
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .rotate(if (isPlaying) discRotation else 0f)
                    .clip(CircleShape)
                    .background(PitchBlack)
                    .border(1.5.dp, accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Sound Disc",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AnimatedColoredWaveform(
    isPlaying: Boolean,
    accentColor: Color
) {
    val barHeights = listOf(14, 30, 48, 22, 60, 38, 20, 52, 32, 44, 18, 56, 30, 48, 22, 38, 54, 24)
    val infiniteTransition = rememberInfiniteTransition(label = "colored_waveform")

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        barHeights.forEachIndexed { index, baseHeight ->
            val animatedHeight by infiniteTransition.animateFloat(
                initialValue = 6f,
                targetValue = baseHeight.toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 280 + (index % 6) * 60,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave_bar_$index"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(if (isPlaying) animatedHeight.dp else (baseHeight * 0.25f).dp)
                    .background(
                        if (isPlaying) accentColor else accentColor.copy(alpha = 0.3f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}

@Composable
fun WavesRadarAndStageOverlay(
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(top = 52.dp, end = 12.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Toggle Pill
        Box(
            modifier = Modifier
                .background(PitchBlack.copy(alpha = 0.85f), RoundedCornerShape(14.dp))
                .border(1.dp, DarkNeutral800, RoundedCornerShape(14.dp))
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(AccentFire, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isExpanded) "HIDE RADAR ▲" else "// RADAR & STAGE ▼",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier.width(190.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top Voices Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PitchBlack.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                        .border(1.dp, DarkNeutral800, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "// RADAR: TOP VOICES",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = Neutral500,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("01. @ANON_LASJ", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = PureWhite)
                        Text("140", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("02. @ANON_2HNA", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500)
                        Text("120", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("03. @ANON_UVMQ", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500)
                        Text("60", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500)
                    }
                }

                // Live on the Stage Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PitchBlack.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                        .border(1.dp, DarkNeutral800, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(AccentFire, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE ON THE STAGE",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "@YOU vs @ANON_TSWG",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = Neutral500
                    )
                    Text(
                        text = "\"AI VS HUMAN\"",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Text(
                        text = "1 VOTES LIVE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        color = AccentFire
                    )
                }
            }
        }
    }
}

