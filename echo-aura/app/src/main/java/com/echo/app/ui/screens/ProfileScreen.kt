package com.echo.app.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.echo.app.ui.theme.DarkNeutral800
import com.echo.app.ui.theme.DarkNeutral900
import com.echo.app.ui.theme.Neutral500
import com.echo.app.ui.theme.PitchBlack
import com.echo.app.ui.theme.PureWhite
import com.echo.app.data.models.EchoPost
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import coil.compose.AsyncImage
import com.echo.app.ui.components.GoogleSignInModal
import com.echo.app.ui.theme.AccentFire
import com.echo.app.ui.components.EchoPostShareModal
import com.echo.app.ui.components.EchoProfileShareModal
import com.echo.app.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val echoes by viewModel.echoes.collectAsState()
    val reverbs by viewModel.reverbs.collectAsState()
    val whispers by viewModel.whispers.collectAsState()
    val playingPostId by viewModel.playingPostId.collectAsState()
    val playbackProgress by viewModel.playbackProgress.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    var activeTab by remember { mutableStateOf("ECHOES") }
    val profileTabs = listOf("ECHOES", "DROP REVERB", "WHISPERS", "VIBE", "DRAFTS")

    var isRecordingBio by remember { mutableStateOf(false) }
    var bioRecorded by remember { mutableStateOf(false) }

    var showProfileShareModal by remember { mutableStateOf(false) }
    var postToShare by remember { mutableStateOf<EchoPost?>(null) }

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
                        text = "PROFILE DASHBOARD",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Neutral500,
                        letterSpacing = 1.5.sp
                    )

                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Profile",
                        tint = PureWhite,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { showProfileShareModal = true }
                            .testTag("share_profile_button")
                    )
                }

            Spacer(modifier = Modifier.height(16.dp))

            // User Handle & Aura Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkNeutral900)
                    .background(PitchBlack)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val uptimeDays = userProfile?.txUptimeDays ?: 14
                            val handleText = if (uptimeDays >= 30) "*${userProfile?.handle ?: "@ANON_8492"}" else (userProfile?.handle ?: "@ANON_8492")
                            Text(
                                text = handleText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(PureWhite, CircleShape)
                            )
                        }
                        Text(
                            text = "ANONYMOUS FREQUENCY",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Neutral500,
                            letterSpacing = 1.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "AURA",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = Neutral500,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "[ AURA: ${userProfile?.auraScore ?: 1420} ]",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Solo Creator Streak: TX_UPTIME ASCII Signal Bar & dB Gain Meter
                val uptime = userProfile?.txUptimeDays ?: 14
                val signal = userProfile?.signalDb ?: 24
                
                // Construct ASCII Signal Bar
                val totalBars = 10
                val activeBars = (uptime % totalBars).coerceAtLeast(1)
                val asciiWave = "|" .repeat(activeBars) + "." .repeat(totalBars - activeBars)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkNeutral800)
                        .background(DarkNeutral900)
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📡 TX_UPTIME : $uptime DAYS",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                            Text(
                                text = "SIGNAL : $signal dB",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentFire
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "UPLINK: [ $asciiWave ]",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Neutral500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Orbiters, Orbiting & Lock In Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1.4K Orbiters",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "•",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Neutral500
                        )
                        Text(
                            text = "38 Orbiting",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Neutral500
                        )
                    }

                    var isLockedIn by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .border(1.dp, if (isLockedIn) PureWhite else AccentFire)
                            .background(if (isLockedIn) PureWhite else PitchBlack)
                            .clickable { isLockedIn = !isLockedIn }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("lock_in_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isLockedIn) "[ LOCKED IN ]" else "[ LOCK IN ]",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLockedIn) PitchBlack else AccentFire
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Badges: Minimalist Emojis ONLY (🐐, 💀, 👑) with name hidden until clicked
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "BADGES:",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Neutral500
                    )

                    var selectedBadgeName by remember { mutableStateOf<String?>(null) }
                    val badges = listOf("🐐" to "GOAT", "💀" to "ROASTER", "👑" to "OG STAGE KING")

                    badges.forEach { (emoji, badgeName) ->
                        Box(
                            modifier = Modifier
                                .border(1.dp, DarkNeutral800, CircleShape)
                                .background(DarkNeutral900, CircleShape)
                                .clickable {
                                    selectedBadgeName = if (selectedBadgeName == badgeName) null else badgeName
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 14.sp)
                        }
                    }

                    if (selectedBadgeName != null) {
                        Box(
                            modifier = Modifier
                                .border(1.dp, PureWhite)
                                .background(PitchBlack)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = selectedBadgeName!!,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }
                }
            }
        }

        // Algorithmic Topic Preferences Customizer
        item {
            val allTopics = listOf("tech", "music", "crypto", "philosophy", "gaming", "raw_thoughts")
            val userLikedTopics = remember(userProfile?.likedTopics) {
                userProfile?.likedTopics?.split(",")?.map { it.trim().lowercase() }?.filter { it.isNotBlank() } ?: emptyList()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkNeutral900)
                    .background(DarkNeutral900)
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ ALGORITHM TOPIC PREFERENCES",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "SMART RECOM",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = AccentFire
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Select topics you love. The Frequency algorithm prioritizes drops, debates, and waves matching your active preferences.",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 12.sp,
                        color = Neutral500
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        allTopics.forEach { topic ->
                            val isLiked = userLikedTopics.contains(topic)
                            Box(
                                modifier = Modifier
                                    .border(1.dp, if (isLiked) PureWhite else DarkNeutral800)
                                    .background(if (isLiked) PitchBlack else DarkNeutral900)
                                    .clickable {
                                        val newList = if (isLiked) {
                                            userLikedTopics.filter { it != topic }
                                        } else {
                                            userLikedTopics + topic
                                        }
                                        viewModel.updateUserLikedTopics(newList.joinToString(","))
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isLiked) "✓ #${topic.uppercase()}" else "#${topic.uppercase()}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = if (isLiked) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isLiked) PureWhite else Neutral500
                                )
                            }
                        }
                    }
                }
            }
        }

        // Voice Bio Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkNeutral900)
                    .background(DarkNeutral900)
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "// VOICE BIO (30S MAX)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Neutral500,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (bioRecorded) "STATUS: ACTIVE" else "NOT RECORDED",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = PureWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .border(1.dp, PureWhite)
                                .clickable {
                                    if (isRecordingBio) {
                                        isRecordingBio = false
                                        bioRecorded = true
                                    } else {
                                        isRecordingBio = true
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("record_voice_bio_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isRecordingBio) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = PureWhite,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isRecordingBio) "[ STOP RECORDING ]" else "[ RECORD VOICE BIO ]",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = PureWhite
                                )
                            }
                        }

                        if (bioRecorded) {
                            Box(
                                modifier = Modifier
                                    .border(1.dp, DarkNeutral800)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play Bio",
                                        tint = PureWhite,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "PLAY BIO",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = PureWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Profile Tabs (ECHOES, REVERBS, PULSED, DRAFTS)
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(profileTabs) { tab ->
                    val isSelected = activeTab == tab
                    Text(
                        text = tab,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PureWhite else Neutral500,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .clickable { activeTab = tab }
                            .padding(vertical = 4.dp)
                            .testTag("profile_tab_$tab")
                    )
                }
            }
        }

        // Feed items for active tab
        if (activeTab == "WHISPERS") {
            if (whispers.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkNeutral900)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NO PRIVATE WHISPERS YET.",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Neutral500,
                            letterSpacing = 1.sp
                        )
                    }
                }
            } else {
                items(whispers, key = { "prof_w_${it.id}" }) { whisper ->
                    WhisperCard(
                        whisper = whisper,
                        onDeleteClick = { viewModel.deleteWhisper(whisper.id) },
                        onShareClick = { viewModel.shareWhisper(context, whisper) }
                    )
                }
            }
        } else {
            val myHandle = userProfile?.handle ?: "@YOU"
            val myEchoes = echoes.filter { it.authorHandle == myHandle }
            val myVibed = echoes.filter { it.isPulsed }
            val myReverbPostIds = reverbs.filter { it.authorHandle == myHandle }.map { it.postId }.toSet()
            val myReverbedEchoes = echoes.filter { myReverbPostIds.contains(it.id) }

            val displayList = when (activeTab) {
                "ECHOES" -> myEchoes
                "VIBE" -> myVibed
                "DROP REVERB" -> myReverbedEchoes
                else -> emptyList()
            }

            if (displayList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkNeutral900)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NO $activeTab YET.",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Neutral500,
                            letterSpacing = 1.sp
                        )
                    }
                }
            } else {
                items(displayList, key = { "prof_${it.id}" }) { echo ->
                    val isPlaying = playingPostId == echo.id
                    val postReverbs = reverbs.filter { rev -> rev.postId == echo.id }
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
                        onForkClick = { viewModel.forkEcho(echo, "") },
                        onDeleteClick = { viewModel.deleteEcho(echo.id) },
                        onShareClick = { postToShare = echo },
                        onChallengeClick = { viewModel.inviteToChallengeOnTopic(echo.caption, echo.authorHandle) },
                        onAddReverb = { text, isVoice, replyTo -> viewModel.addReverb(echo.id, text, isVoice, replyTo) },
                        onLikeReverb = { revId -> viewModel.toggleLikeReverb(revId) },
                        onDeleteReverb = { revId -> viewModel.deleteReverb(revId) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

    if (showProfileShareModal) {
        EchoProfileShareModal(
            profile = userProfile,
            onClose = { showProfileShareModal = false },
            onShare = {
                viewModel.shareProfile(context, userProfile)
                showProfileShareModal = false
            }
        )
    }

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
}
