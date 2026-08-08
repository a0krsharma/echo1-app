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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.echo.app.ui.theme.AccentGreen
import com.echo.app.ui.theme.DarkNeutral800
import com.echo.app.ui.theme.DarkNeutral900
import com.echo.app.ui.theme.Neutral500
import com.echo.app.ui.theme.PitchBlack
import com.echo.app.ui.theme.PureWhite
import com.echo.app.ui.viewmodel.MainViewModel

@Composable
fun StageDetailScreen(
    clashId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val clashes by viewModel.clashes.collectAsState()
    val clash = clashes.find { it.id == clashId } ?: clashes.firstOrNull()

    val activeRoomId by viewModel.activeRoomId.collectAsState()
    val isRoomPlayingInBg by viewModel.isRoomPlayingInBg.collectAsState()
    val isRoomMuted by viewModel.isRoomMuted.collectAsState()
    val mySpeakRequestClashId by viewModel.mySpeakRequestClashId.collectAsState()
    val speakRequestsMap by viewModel.speakRequestsMap.collectAsState()

    val pendingRequests = speakRequestsMap[clashId] ?: emptyList()
    val isJoinedThisRoom = activeRoomId == clashId && isRoomPlayingInBg
    val isMyRequestPending = mySpeakRequestClashId == clashId

    var showInviteModal by remember { mutableStateOf(false) }
    var memberHandleInput by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize().background(PitchBlack)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))

                // Back button
                Row(
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BACK TO STAGE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Live status header & Room Privacy Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(com.echo.app.ui.theme.AccentFire, androidx.compose.foundation.shape.CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (clash?.isPrivate == true) "🔒 PRIVATE STAGE" else "🌐 PUBLIC STAGE ARENA",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            letterSpacing = 1.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .border(1.dp, PureWhite)
                            .clickable { showInviteModal = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("invite_member_stage_button")
                    ) {
                        Text(
                            text = "+ INVITE MEMBER",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // BACKGROUND ROOM AUDIO CONTROLS BANNER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (isJoinedThisRoom) AccentGreen else DarkNeutral800)
                        .background(if (isJoinedThisRoom) DarkNeutral900 else PitchBlack)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isJoinedThisRoom) "📻 ROOM AUDIO: PLAYING IN BACKGROUND" else "📻 BACKGROUND AUDIO ROOM",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isJoinedThisRoom) AccentGreen else PureWhite
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "AUDIO STAYS ACTIVE WHILE YOU NAVIGATE OTHER SCREENS",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                color = Neutral500
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (isJoinedThisRoom) {
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, PureWhite)
                                        .clickable { viewModel.toggleMuteRoom() }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (isRoomMuted) "UNMUTE" else "MUTE",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .border(1.dp, com.echo.app.ui.theme.AccentFire)
                                        .clickable { viewModel.leaveStageRoom() }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "LEAVE",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = com.echo.app.ui.theme.AccentFire
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, AccentGreen)
                                        .background(AccentGreen)
                                        .clickable {
                                            clash?.let { viewModel.joinStageRoom(it.id, it.title) }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "JOIN ROOM",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PitchBlack
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title & Topic
                Text(
                    text = clash?.title ?: "LIVE STAGE DEBATE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Neutral500,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"${clash?.topic ?: "AI vs Human Vocal Expression"}\"",
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 22.sp,
                    color = PureWhite,
                    lineHeight = 28.sp
                )

                if (clash?.invitedHandles?.isNotBlank() == true) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Invited: ${clash.invitedHandles}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Neutral500
                    )
                }
            }

        // Active Speaker Cards
        item {
            Text(
                text = "// ACTIVE SPEAKERS ON FREQUENCY",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Neutral500,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Speaker A
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, PureWhite)
                        .background(DarkNeutral900)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = clash?.handleA ?: "@SIDE_A",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Speaking",
                            tint = AccentGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    WaveformEqualizer(isPlaying = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = clash?.posA ?: "Side A stance",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 12.sp,
                        color = Neutral500
                    )
                }

                // Speaker B
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, DarkNeutral800)
                        .background(DarkNeutral900)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = clash?.handleB ?: "@SIDE_B",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Muted",
                            tint = Neutral500,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    WaveformEqualizer(isPlaying = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = clash?.posB ?: "Side B stance",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 12.sp,
                        color = Neutral500
                    )
                }
            }
        }

        // Live Audience Controls & Speak Requests
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Speak Request & Story Sharing Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Ask to Speak Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, if (isMyRequestPending) AccentGreen else PureWhite)
                            .background(if (isMyRequestPending) DarkNeutral900 else PitchBlack)
                            .clickable {
                                clash?.let {
                                    if (isMyRequestPending) {
                                        viewModel.cancelSpeakRequest(it.id)
                                    } else {
                                        viewModel.requestToSpeak(it.id)
                                    }
                                }
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isMyRequestPending) "✋ REQUEST SENT (CANCEL)" else "✋ ASK TO SPEAK",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMyRequestPending) AccentGreen else PureWhite
                        )
                    }

                    // Share to Story / Status
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, com.echo.app.ui.theme.AccentFire)
                            .background(DarkNeutral900)
                            .clickable {
                                clash?.let {
                                    viewModel.shareToStoryStatus(
                                        context = context,
                                        title = it.title,
                                        caption = it.topic,
                                        author = "${it.handleA} vs ${it.handleB}"
                                    )
                                }
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📱 SHARE TO STORY",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = com.echo.app.ui.theme.AccentFire
                        )
                    }
                }

                // Stage Host: Pending Speak Requests Queue
                if (pendingRequests.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkNeutral800)
                            .background(DarkNeutral900)
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✋ AUDIENCE REQUESTS TO SPEAK (${pendingRequests.size})",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                                Text(
                                    text = "HOST QUEUE",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    color = Neutral500
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            pendingRequests.forEach { handle ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = handle,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = PureWhite
                                    )

                                    Box(
                                        modifier = Modifier
                                            .border(1.dp, AccentGreen)
                                            .background(AccentGreen)
                                            .clickable {
                                                clash?.let { viewModel.approveSpeakRequest(it.id, handle) }
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "GRANT MIC 🎤",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PitchBlack
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Live Voting Controls
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkNeutral900)
                        .background(DarkNeutral900)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "// AUDIENCE VOTE",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Neutral500,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, PureWhite)
                                    .clickable { clash?.let { viewModel.voteOnClash(it.id, "A") } }
                                    .padding(vertical = 10.dp)
                                    .testTag("detail_vote_a"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "VOTE SIDE A (${clash?.votesA ?: 0})",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, PureWhite)
                                    .clickable { clash?.let { viewModel.voteOnClash(it.id, "B") } }
                                    .padding(vertical = 10.dp)
                                    .testTag("detail_vote_b"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "VOTE SIDE B (${clash?.votesB ?: 0})",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    } // end outer Box

    if (showInviteModal && clash != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PitchBlack.copy(alpha = 0.9f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkNeutral800)
                    .background(PitchBlack)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// INVITE MEMBER TO STAGE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = PureWhite,
                        modifier = Modifier
                            .clickable { showInviteModal = false }
                            .size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Send an explicit stage invite notification to join this ${if (clash.isPrivate) "private" else "public"} debate.",
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp,
                    color = Neutral500
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = memberHandleInput,
                    onValueChange = { memberHandleInput = it },
                    label = { Text("MEMBER HANDLE (e.g. @NOVA, @ZARA)", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = DarkNeutral800,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (memberHandleInput.isNotBlank()) PureWhite else DarkNeutral800)
                        .background(if (memberHandleInput.isNotBlank()) PureWhite else DarkNeutral900)
                        .clickable(enabled = memberHandleInput.isNotBlank()) {
                            viewModel.inviteUserToStage(clash.id, memberHandleInput)
                            showInviteModal = false
                            memberHandleInput = ""
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SEND STAGE INVITE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (memberHandleInput.isNotBlank()) PitchBlack else Neutral500
                    )
                }
            }
        }
    }
}
