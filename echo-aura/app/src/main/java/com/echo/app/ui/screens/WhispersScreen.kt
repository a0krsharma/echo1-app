package com.echo.app.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echo.app.data.models.WhisperItem
import com.echo.app.util.ResonanceStreakManager
import com.echo.app.ui.theme.DarkNeutral800
import com.echo.app.ui.theme.DarkNeutral900
import com.echo.app.ui.theme.Neutral500
import com.echo.app.ui.theme.PitchBlack
import com.echo.app.ui.theme.PureWhite
import com.echo.app.ui.viewmodel.MainViewModel

@Composable
fun WhispersScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val whispers by viewModel.whispers.collectAsState()
    var showNewModal by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                    Column {
                        Text(
                            text = "Echo.",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "WHISPERS · PRIVATE FREQUENCY",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Neutral500,
                            letterSpacing = 1.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .border(1.dp, PureWhite)
                            .clickable { showNewModal = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("new_whisper_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New Whisper",
                                tint = PureWhite,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "NEW WHISPER",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = PureWhite
                            )
                        }
                    }
                }
            }

            if (whispers.isEmpty()) {
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
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Neutral500,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "NO PRIVATE WHISPERS YET.",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Neutral500,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Start an encrypted 1-on-1 audio frequency with any voice.",
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 13.sp,
                                color = Neutral500
                            )
                        }
                    }
                }
            } else {
                items(whispers, key = { it.id }) { whisper ->
                    WhisperCard(
                        whisper = whisper,
                        onDeleteClick = { viewModel.deleteWhisper(whisper.id) },
                        onShareClick = { viewModel.shareWhisper(context, whisper) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (showNewModal) {
            NewWhisperModal(
                onClose = { showNewModal = false },
                onSend = { recipient, msg ->
                    viewModel.sendWhisper(recipient, msg)
                    showNewModal = false
                }
            )
        }
    }
}

@Composable
fun WhisperCard(
    whisper: WhisperItem,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val streakInfo = remember(whisper.lastInteractionTimestamp, whisper.recipientHandle) {
        ResonanceStreakManager.getStreakForWhisper(whisper)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkNeutral900)
            .background(PitchBlack)
            .padding(14.dp)
            .testTag("whisper_card_${whisper.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = streakInfo.handleStreakText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = streakInfo.formattedStatusText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (streakInfo.phaseState) {
                        "DRIFTING" -> com.echo.app.ui.theme.AccentFire
                        "DECOHERENT" -> Neutral500
                        else -> PureWhite
                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = whisper.duration,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Neutral500
                )
                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Whisper",
                        tint = Neutral500,
                        modifier = Modifier.size(13.dp)
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Whisper",
                        tint = Neutral500,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "\"${whisper.message}\"",
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 13.sp,
            color = PureWhite
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewWhisperModal(
    onClose: () -> Unit,
    onSend: (handle: String, msg: String) -> Unit
) {
    var handle by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }

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
                Text(
                    text = "// START PRIVATE WHISPER",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Neutral500,
                    letterSpacing = 1.sp
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = PureWhite,
                    modifier = Modifier.clickable { onClose() }.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val tfColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PureWhite,
                unfocusedBorderColor = DarkNeutral800,
                focusedTextColor = PureWhite,
                unfocusedTextColor = PureWhite
            )

            // Quick Handles
            Text(
                text = "SELECT RECIPIENT HANDLE:",
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
                listOf("@ZARA.IQ", "@OG_VIBE", "@ARYAN.V").forEach { recipientTag ->
                    Box(
                        modifier = Modifier
                            .border(1.dp, DarkNeutral800)
                            .background(DarkNeutral900)
                            .clickable { handle = recipientTag }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = recipientTag,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = PureWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = handle,
                onValueChange = { handle = it },
                label = { Text("RECIPIENT @HANDLE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500) },
                modifier = Modifier.fillMaxWidth(),
                colors = tfColors
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = msg,
                onValueChange = { msg = it },
                label = { Text("VOICE WHISPER CAPTION", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Neutral500) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = tfColors
            )

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PureWhite)
                    .background(PureWhite)
                    .clickable { onSend(handle, msg) }
                    .padding(vertical = 12.dp)
                    .testTag("send_whisper_submit"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "[ INITIATE PRIVATE FREQUENCY ]",
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
