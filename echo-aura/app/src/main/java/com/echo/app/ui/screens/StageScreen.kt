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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.echo.app.data.models.ClashItem
import com.echo.app.ui.theme.AccentFire
import com.echo.app.ui.theme.DarkNeutral800
import com.echo.app.ui.theme.DarkNeutral900
import com.echo.app.ui.theme.Neutral500
import com.echo.app.ui.theme.PitchBlack
import com.echo.app.ui.theme.PureWhite
import com.echo.app.ui.viewmodel.MainViewModel

@Composable
fun StageScreen(
    viewModel: MainViewModel,
    onNavigateToStageDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clashes by viewModel.clashes.collectAsState()
    val showModal by viewModel.showChallengeModal.collectAsState()

    Box(modifier = modifier.fillMaxSize().background(PitchBlack)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "// THE STAGE — WHERE VOICES CLASH",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Neutral500,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Two voices.\nOne truth.",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 32.sp,
                            color = PureWhite,
                            lineHeight = 36.sp
                        )
                    }

                    // Challenge Action Button
                    Box(
                        modifier = Modifier
                            .border(1.dp, PureWhite)
                            .background(PureWhite)
                            .clickable { viewModel.setShowChallengeModal(true) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("challenge_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SportsKabaddi,
                                contentDescription = "Challenge",
                                tint = PitchBlack,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CHALLENGE",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PitchBlack,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "// LIVE DEBATES ON THE STAGE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = PureWhite,
                    letterSpacing = 1.sp
                )
            }

            if (clashes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkNeutral900)
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No active debates on the Stage right now.",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 15.sp,
                            color = Neutral500
                        )
                    }
                }
            } else {
                items(clashes, key = { it.id }) { clash ->
                    ClashCard(
                        clash = clash,
                        onVote = { side -> viewModel.voteOnClash(clash.id, side) },
                        onJoinStage = { onNavigateToStageDetail(clash.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Challenge Modal Overlay
        if (showModal) {
            ChallengeModalDialog(
                onClose = { viewModel.setShowChallengeModal(false) },
                onSend = { title, topic, handleA, posA, handleB, posB, isPrivate, invited ->
                    viewModel.launchClash(title, topic, handleA, posA, handleB, posB, isPrivate, invited)
                }
            )
        }
    }
}

@Composable
fun ClashCard(
    clash: ClashItem,
    onVote: (String) -> Unit,
    onJoinStage: () -> Unit
) {
    val totalVotes = (clash.votesA + clash.votesB).coerceAtLeast(1)
    val pctA = (clash.votesA.toFloat() / totalVotes.toFloat())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkNeutral900)
            .background(PitchBlack)
            .padding(16.dp)
            .testTag("clash_card_${clash.id}")
    ) {
        // Title, Room Privacy Badge & Audience Count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (clash.isPrivate) AccentFire else PureWhite,
                            androidx.compose.foundation.shape.CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = clash.title,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                    letterSpacing = 1.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Room Privacy Badge
                Box(
                    modifier = Modifier
                        .border(1.dp, DarkNeutral800)
                        .background(DarkNeutral900)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (clash.isPrivate) "🔒 PRIVATE" else "🌐 PUBLIC",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = if (clash.isPrivate) AccentFire else PureWhite
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${clash.listeners} AUDIENCE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Neutral500
                )
            }
        }

        if (clash.invitedHandles.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Invited Members: ${clash.invitedHandles}",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Neutral500
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Topic Question
        Text(
            text = "\"${clash.topic}\"",
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 16.sp,
            color = PureWhite,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stances side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Side A
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, DarkNeutral800)
                    .background(DarkNeutral900)
                    .padding(10.dp)
            ) {
                Text(
                    text = "SIDE A · ${clash.handleA}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Neutral500,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = clash.posA,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp,
                    color = PureWhite
                )
            }

            // Side B
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, DarkNeutral800)
                    .background(DarkNeutral900)
                    .padding(10.dp)
            ) {
                Text(
                    text = "SIDE B · ${clash.handleB}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Neutral500,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = clash.posB,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp,
                    color = PureWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Vote Progress Ratio
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(pctA * 100).toInt()}% SIDE A (${clash.votesA})",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = PureWhite
                )
                Text(
                    text = "${((1f - pctA) * 100).toInt()}% SIDE B (${clash.votesB})",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Neutral500
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { pctA },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = PureWhite,
                trackColor = DarkNeutral800
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons: Vote & Join Room Relay
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .border(1.dp, if (clash.userVotedSide == "A") PureWhite else DarkNeutral800)
                        .clickable { onVote("A") }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("vote_a_${clash.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (clash.userVotedSide == "A") "[ VOTED A ]" else "VOTE A",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = PureWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .border(1.dp, if (clash.userVotedSide == "B") PureWhite else DarkNeutral800)
                        .clickable { onVote("B") }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("vote_b_${clash.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (clash.userVotedSide == "B") "[ VOTED B ]" else "VOTE B",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = PureWhite
                    )
                }
            }

            Box(
                modifier = Modifier
                    .border(1.dp, PureWhite)
                    .clickable { onJoinStage() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("join_stage_${clash.id}"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = "Join Stage",
                        tint = PureWhite,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "JOIN STAGE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ChallengeModalDialog(
    onClose: () -> Unit,
    onSend: (title: String, topic: String, handleA: String, posA: String, handleB: String, posB: String, isPrivate: Boolean, invitedHandles: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var handleA by remember { mutableStateOf("@YOU") }
    var posA by remember { mutableStateOf("") }
    var handleB by remember { mutableStateOf("") }
    var posB by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    var invitedHandles by remember { mutableStateOf("") }

    val canLaunch = topic.length > 5 && posA.length > 5 && posB.length > 5

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack.copy(alpha = 0.9f))
            .padding(16.dp),
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
                Column {
                    Text(
                        text = "// CHALLENGE TO THE STAGE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Neutral500,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Set the debate motion.",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 18.sp,
                        color = PureWhite
                    )
                }

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = PureWhite,
                    modifier = Modifier
                        .clickable { onClose() }
                        .size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Public vs Private Stage Room Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, if (!isPrivate) PureWhite else DarkNeutral800)
                        .background(if (!isPrivate) DarkNeutral900 else PitchBlack)
                        .clickable { isPrivate = false }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🌐 PUBLIC STAGE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = if (!isPrivate) FontWeight.Bold else FontWeight.Normal,
                        color = PureWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, if (isPrivate) AccentFire else DarkNeutral800)
                        .background(if (isPrivate) DarkNeutral900 else PitchBlack)
                        .clickable { isPrivate = true }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔒 PRIVATE INVITE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = if (isPrivate) FontWeight.Bold else FontWeight.Normal,
                        color = if (isPrivate) AccentFire else Neutral500
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val tfColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PureWhite,
                unfocusedBorderColor = DarkNeutral800,
                focusedTextColor = PureWhite,
                unfocusedTextColor = PureWhite
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("DEBATE TITLE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500) },
                modifier = Modifier.fillMaxWidth(),
                colors = tfColors
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = handleB,
                onValueChange = { handleB = it },
                label = { Text("OPPONENT HANDLE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500) },
                modifier = Modifier.fillMaxWidth(),
                colors = tfColors
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isPrivate) {
                OutlinedTextField(
                    value = invitedHandles,
                    onValueChange = { invitedHandles = it },
                    label = { Text("INVITED MEMBERS (CSV, e.g. @ZARA, @NOVA)", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = tfColors
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("DEBATE TOPIC / MOTION", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = tfColors
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = posA,
                    onValueChange = { posA = it },
                    label = { Text("SIDE A STANCE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500) },
                    modifier = Modifier.weight(1f),
                    colors = tfColors
                )
                OutlinedTextField(
                    value = posB,
                    onValueChange = { posB = it },
                    label = { Text("SIDE B STANCE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500) },
                    modifier = Modifier.weight(1f),
                    colors = tfColors
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .border(1.dp, if (canLaunch) PureWhite else DarkNeutral800)
                        .background(if (canLaunch) PureWhite else DarkNeutral900)
                        .clickable(enabled = canLaunch) {
                            onSend(title, topic, handleA, posA, handleB, posB, isPrivate, invitedHandles)
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("launch_debate_submit"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[ LAUNCH DEBATE ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canLaunch) PitchBlack else Neutral500,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "CANCEL",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Neutral500,
                    modifier = Modifier.clickable { onClose() }
                )
            }
        }
    }
}
