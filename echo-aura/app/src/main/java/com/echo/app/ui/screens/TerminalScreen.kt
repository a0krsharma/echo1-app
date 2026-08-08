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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import com.echo.app.data.models.EchoPost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echo.app.data.models.TerminalSettings
import com.echo.app.ui.components.GoogleSignInModal
import com.echo.app.ui.theme.AccentFire
import com.echo.app.ui.theme.DarkNeutral800
import com.echo.app.ui.theme.DarkNeutral900
import com.echo.app.ui.theme.Neutral500
import com.echo.app.ui.theme.PitchBlack
import com.echo.app.ui.theme.PureWhite
import com.echo.app.ui.viewmodel.MainViewModel

@Composable
fun TerminalScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val settingsState by viewModel.settings.collectAsState()
    val settings = settingsState ?: TerminalSettings()
    val userProfile by viewModel.userProfile.collectAsState()
    val echoes by viewModel.echoes.collectAsState()

    var showGoogleAuthModal by remember { mutableStateOf(false) }
    var showDangerZone by remember { mutableStateOf(false) }

    // Modals state
    var showStashModal by remember { mutableStateOf(false) }
    var showTimeCapsuleModal by remember { mutableStateOf(false) }
    var showPingSettingsModal by remember { mutableStateOf(false) }
    var showDayOnesModal by remember { mutableStateOf(false) }
    var showExiledModal by remember { mutableStateOf(false) }
    var showHiddenWordsModal by remember { mutableStateOf(false) }
    var showEchoGuideModal by remember { mutableStateOf(false) }
    var showReportModal by remember { mutableStateOf(false) }
    var showSafetyModal by remember { mutableStateOf(false) }
    var showContactModal by remember { mutableStateOf(false) }

    // Dynamic Lists State
    var dayOnesList by remember { mutableStateOf(listOf("@synth_kid", "@luna_wave", "@void_walker")) }
    var exiledList by remember { mutableStateOf(listOf("@spam_bot99", "@noise_maker")) }
    var hiddenWordsList by remember { mutableStateOf(listOf("crypto", "scam", "spoiler")) }
    var terminalBannerMsg by remember { mutableStateOf<String?>(null) }

    if (showGoogleAuthModal) {
        GoogleSignInModal(
            viewModel = viewModel,
            onDismiss = { showGoogleAuthModal = false }
        )
    }

    if (showStashModal) {
        TerminalStashModal(
            echoes = echoes.filter { it.isPulsed },
            onDismiss = { showStashModal = false },
            onUnstash = { id -> viewModel.togglePulse(id) }
        )
    }

    if (showTimeCapsuleModal) {
        TerminalTimeCapsuleModal(
            echoes = echoes.take(2),
            onDismiss = { showTimeCapsuleModal = false }
        )
    }

    if (showPingSettingsModal) {
        TerminalPingSettingsModal(
            settings = settings,
            onUpdate = { viewModel.updateTerminalSettings(it) },
            onDismiss = { showPingSettingsModal = false }
        )
    }

    if (showDayOnesModal) {
        TerminalDayOnesModal(
            dayOnes = dayOnesList,
            onAdd = { handle -> if (handle.isNotBlank()) dayOnesList = dayOnesList + if (handle.startsWith("@")) handle else "@$handle" },
            onRemove = { handle -> dayOnesList = dayOnesList - handle },
            onDismiss = { showDayOnesModal = false }
        )
    }

    if (showExiledModal) {
        TerminalExiledModal(
            exiled = exiledList,
            onAdd = { handle -> if (handle.isNotBlank()) exiledList = exiledList + if (handle.startsWith("@")) handle else "@$handle" },
            onRemove = { handle -> exiledList = exiledList - handle },
            onDismiss = { showExiledModal = false }
        )
    }

    if (showHiddenWordsModal) {
        TerminalHiddenWordsModal(
            words = hiddenWordsList,
            onAdd = { word -> if (word.isNotBlank()) hiddenWordsList = hiddenWordsList + word.lowercase().trim() },
            onRemove = { word -> hiddenWordsList = hiddenWordsList - word },
            onDismiss = { showHiddenWordsModal = false }
        )
    }

    if (showEchoGuideModal) {
        TerminalEchoGuideModal(onDismiss = { showEchoGuideModal = false })
    }

    if (showReportModal) {
        TerminalReportModal(
            onSubmit = { msg -> terminalBannerMsg = "REPORT SUBMITTED: #TICKET-${(1000..9999).random()}" },
            onDismiss = { showReportModal = false }
        )
    }

    if (showSafetyModal) {
        TerminalSafetyModal(onDismiss = { showSafetyModal = false })
    }

    if (showContactModal) {
        TerminalContactModal(
            onSend = { msg -> terminalBannerMsg = "MESSAGE TRANSMITTED TO ECHO TEAM" },
            onDismiss = { showContactModal = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // System Banner Header
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "// SYSTEM: ECHO TERMINAL v1.0",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Neutral500,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Terminal",
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
            Spacer(modifier = Modifier.height(6.dp))

            terminalBannerMsg?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PureWhite)
                        .background(DarkNeutral900)
                        .padding(10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "[✓] $msg",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "[X]",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Neutral500,
                            modifier = Modifier.clickable { terminalBannerMsg = null }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Section: YOUR IDENTITY & GOOGLE AUTH
        item {
            SectionHeader(label = "YOUR IDENTITY & GOOGLE AUTH")
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (userProfile?.isGoogleSignedIn == true) PureWhite else DarkNeutral900)
                    .background(DarkNeutral900)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userProfile?.handle ?: "@ANON_8492",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (userProfile?.isGoogleSignedIn == true) {
                                "${userProfile?.googleAccountName} (${userProfile?.email})"
                            } else {
                                "AURA: ${userProfile?.auraScore ?: 1420} • ANONYMOUS HANDLE"
                            },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Neutral500
                        )
                    }

                    Box(
                        modifier = Modifier
                            .border(1.dp, PureWhite)
                            .background(PitchBlack)
                            .clickable { showGoogleAuthModal = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("claim_handle_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (userProfile?.isGoogleSignedIn == true) "[ VERIFIED ]" else "SIGN IN WITH GOOGLE",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }

                if (userProfile?.isGoogleSignedIn == true) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, PureWhite)
                                .background(PitchBlack)
                                .clickable { showGoogleAuthModal = true }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SWITCH GOOGLE ACC",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }

                        Box(
                            modifier = Modifier
                                .border(1.dp, DarkNeutral800)
                                .background(PitchBlack)
                                .clickable {
                                    viewModel.signOutGoogle()
                                    terminalBannerMsg = "SIGNED OUT OF GOOGLE ACCOUNT"
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SIGN OUT",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = Neutral500
                            )
                        }
                    }
                }
            }
        }

        // Section: VAULT & STASH
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(label = "THE STASH & TIME CAPSULE")
        }

        item {
            NavigationRow(
                label = "THE STASH",
                sub = "your saved posts & audio drops (${echoes.count { it.isPulsed }})",
                tag = "row_the_stash",
                onClick = { showStashModal = true }
            )
        }

        item {
            NavigationRow(
                label = "TIME CAPSULE",
                sub = "ARCHIVED ECHOES • only you can see these",
                tag = "row_time_capsule",
                onClick = { showTimeCapsuleModal = true }
            )
        }

        // Section: PINGS — NOTIFICATIONS
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(label = "PINGS — NOTIFICATIONS")
        }

        item {
            NavigationRow(
                label = "PING SETTINGS",
                sub = "control what reaches you (Pulses, Reverbs, Stage, Lock-ins)",
                tag = "toggle_pings",
                onClick = { showPingSettingsModal = true }
            )
        }

        // Section: AUDIO SETTINGS
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(label = "AUDIO SETTINGS")
        }

        item {
            MultiChoiceRow(
                label = "STREAM QUALITY",
                sub = "data usage vs fidelity",
                options = listOf("HIGH", "STANDARD", "LOW"),
                selected = settings.audioQuality,
                onSelect = { viewModel.updateTerminalSettings(settings.copy(audioQuality = it)) },
                tag = "choice_stream_quality"
            )
        }

        item {
            ToggleRow(
                label = "AUTO-TRANSCRIBE REVERBS",
                sub = "convert yaps to text",
                value = settings.autoTranscribe,
                onToggle = { viewModel.updateTerminalSettings(settings.copy(autoTranscribe = !settings.autoTranscribe)) },
                tag = "toggle_auto_transcribe"
            )
        }

        item {
            ToggleRow(
                label = "AUTO-PLAY ON FREQUENCY",
                sub = "start playing on scroll",
                value = settings.autoPlay,
                onToggle = { viewModel.updateTerminalSettings(settings.copy(autoPlay = !settings.autoPlay)) },
                tag = "toggle_auto_play"
            )
        }

        // Section: YAP CONTROL — REVERB PERMISSIONS
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(label = "YAP CONTROL — REVERB PERMISSIONS")
        }

        item {
            MultiChoiceRow(
                label = "WHO CAN DROP REVERB",
                sub = "who can voice-reply to your echoes",
                options = listOf("EVERYONE", "ORBITERS", "DAY ONES", "NOBODY"),
                selected = settings.yapControl,
                onSelect = { viewModel.updateTerminalSettings(settings.copy(yapControl = it)) },
                tag = "choice_yap_control"
            )
        }

        item {
            MultiChoiceRow(
                label = "WHO CAN PUT ON / ECHO",
                sub = "who can repost your voice",
                options = listOf("EVERYONE", "ORBITERS", "NOBODY"),
                selected = settings.echoControl,
                onSelect = { viewModel.updateTerminalSettings(settings.copy(echoControl = it)) },
                tag = "choice_echo_control"
            )
        }

        item {
            MultiChoiceRow(
                label = "WHO CAN WHISPER YOU",
                sub = "private audio messages",
                options = listOf("EVERYONE", "ORBITERS", "DAY ONES"),
                selected = settings.whoCanWhisper,
                onSelect = { viewModel.updateTerminalSettings(settings.copy(whoCanWhisper = it)) },
                tag = "choice_whisper_control"
            )
        }

        // Section: PRIVACY & BOUNDARIES
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(label = "PRIVACY & BOUNDARIES")
        }

        item {
            ToggleRow(
                label = "PRIVATE FREQUENCY",
                sub = "approve orbiters manually",
                value = settings.privateAcc,
                onToggle = { viewModel.updateTerminalSettings(settings.copy(privateAcc = !settings.privateAcc)) },
                tag = "toggle_private_freq"
            )
        }

        item {
            ToggleRow(
                label = "SHOW AURA SCORE",
                sub = "visible on your profile",
                value = settings.auraVisible,
                onToggle = { viewModel.updateTerminalSettings(settings.copy(auraVisible = !settings.auraVisible)) },
                tag = "toggle_show_aura"
            )
        }

        item {
            ToggleRow(
                label = "ANONYMOUS MODE",
                sub = "mask handle in public feed",
                value = settings.anonMode,
                onToggle = { viewModel.updateTerminalSettings(settings.copy(anonMode = !settings.anonMode)) },
                tag = "toggle_anon_mode"
            )
        }

        item {
            ToggleRow(
                label = "APPROVE LOCK-INS",
                sub = "manually approve orbiters",
                value = settings.lockApproval,
                onToggle = { viewModel.updateTerminalSettings(settings.copy(lockApproval = !settings.lockApproval)) },
                tag = "toggle_approve_lockins"
            )
        }

        item {
            NavigationRow(
                label = "DAY ONES",
                sub = "your inner circle priority list (${dayOnesList.size})",
                tag = "row_day_ones",
                onClick = { showDayOnesModal = true }
            )
        }

        item {
            NavigationRow(
                label = "EXILED",
                sub = "voices & handles you have silenced (${exiledList.size})",
                tag = "row_exiled",
                onClick = { showExiledModal = true }
            )
        }

        item {
            NavigationRow(
                label = "HIDDEN WORDS",
                sub = "words filtered from reverbs & yaps (${hiddenWordsList.size})",
                tag = "row_hidden_words",
                onClick = { showHiddenWordsModal = true }
            )
        }

        // Section: HELP / SOS
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(label = "HELP / SOS")
        }

        item {
            NavigationRow(
                label = "ECHO GUIDE",
                sub = "how everything works in Echo",
                tag = "row_echo_guide",
                onClick = { showEchoGuideModal = true }
            )
        }

        item {
            NavigationRow(
                label = "REPORT A PROBLEM",
                sub = "bugs, crashes, weirdness",
                tag = "row_report_problem",
                onClick = { showReportModal = true }
            )
        }

        item {
            NavigationRow(
                label = "SAFETY CENTRE",
                sub = "mental health & support",
                tag = "row_safety_centre",
                onClick = { showSafetyModal = true }
            )
        }

        item {
            NavigationRow(
                label = "CONTACT US",
                sub = "reach the humans behind echo",
                tag = "row_contact_us",
                onClick = { showContactModal = true }
            )
        }

        // Section: DANGER ZONE
        item {
            Spacer(modifier = Modifier.height(6.dp))
            SectionHeader(label = "DANGER ZONE")
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (showDangerZone) PureWhite else DarkNeutral900)
                    .background(PitchBlack)
                    .padding(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PureWhite)
                        .clickable { showDangerZone = !showDangerZone }
                        .padding(12.dp)
                        .testTag("danger_zone_toggle"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (showDangerZone) "[ HIDE DANGER ZONE ]" else "[ SHOW DANGER ZONE ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                }

                if (showDangerZone) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "⚠️ CAUTION: Dangerous operations ahead",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    DangerButton(
                        label = "PURGE LOCAL CACHE",
                        sub = "Clear downloaded wave clips & audio buffer",
                        onClick = { terminalBannerMsg = "CACHE PURGED: 38.4 MB FREED" }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    DangerButton(
                        label = "RESET TERMINAL SESSION",
                        sub = "Restore all terminal configurations to default",
                        onClick = {
                            viewModel.updateTerminalSettings(TerminalSettings())
                            terminalBannerMsg = "TERMINAL PREFERENCES RESTORED TO DEFAULT"
                        }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    DangerButton(
                        label = "DEACTIVATE FREQUENCY",
                        sub = "Temporarily archive your profile & hide feed",
                        onClick = { terminalBannerMsg = "FREQUENCY DEACTIVATED & ARCHIVED" }
                    )
                }
            }
        }

        // System Footer
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ECHO v0.1.0 — AUG 2026 — UTILITARIAN CANVAS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = Neutral500,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionHeader(label: String) {
    Text(
        text = "// $label",
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        color = Neutral500,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun NavigationRow(
    label: String,
    sub: String,
    tag: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkNeutral900)
            .background(PitchBlack)
            .clickable { onClick() }
            .padding(14.dp)
            .testTag(tag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = sub,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Neutral500
            )
        }

        Box(
            modifier = Modifier
                .border(1.dp, DarkNeutral800)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "→",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = PureWhite
            )
        }
    }
}

@Composable
fun ToggleRow(
    label: String,
    sub: String,
    value: Boolean,
    onToggle: () -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkNeutral900)
            .background(PitchBlack)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = sub,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Neutral500
            )
        }

        Box(
            modifier = Modifier
                .border(1.dp, if (value) PureWhite else DarkNeutral800)
                .clickable { onToggle() }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag(tag),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (value) "[ ON ]" else "[ OFF ]",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (value) PureWhite else Neutral500
            )
        }
    }
}

@Composable
fun MultiChoiceRow(
    label: String,
    sub: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    tag: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkNeutral900)
            .background(PitchBlack)
            .padding(14.dp)
            .testTag(tag)
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PureWhite,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = sub,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = Neutral500
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEach { opt ->
                val isSelected = opt.equals(selected, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .border(1.dp, if (isSelected) PureWhite else DarkNeutral800)
                        .background(if (isSelected) DarkNeutral900 else PitchBlack)
                        .clickable { onSelect(opt) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSelected) "[ $opt ]" else opt,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PureWhite else Neutral500
                    )
                }
            }
        }
    }
}

@Composable
fun DangerButton(
    label: String,
    sub: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkNeutral800)
            .background(DarkNeutral900)
            .clickable { onClick() }
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
            Text(
                text = sub,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = Neutral500
            )
        }
        Text(
            text = "⚡",
            fontSize = 12.sp
        )
    }
}

// ==================== TERMINAL MODALS ====================

@Composable
fun TerminalStashModal(
    echoes: List<EchoPost>,
    onDismiss: () -> Unit,
    onUnstash: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PureWhite)
                .background(PitchBlack)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// THE STASH",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "[ CLOSE ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Neutral500,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Saved audio drops and bookmarked echoes.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Neutral500
                )
                Spacer(modifier = Modifier.height(14.dp))

                if (echoes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkNeutral900)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NO STASHED ECHOES YET.\nBookmark posts on Frequency to access them here.",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Neutral500
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(echoes, key = { "stash_${it.id}" }) { post ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, DarkNeutral800)
                                    .background(DarkNeutral900)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = post.authorHandle,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite
                                    )
                                    Text(
                                        text = post.caption.ifBlank { "Voice Drop (${post.durationSec}s)" },
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Neutral500,
                                        maxLines = 1
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, DarkNeutral800)
                                        .clickable { onUnstash(post.id) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "[ UNSAVE ]",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        color = Neutral500
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TerminalTimeCapsuleModal(
    echoes: List<EchoPost>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PureWhite)
                .background(PitchBlack)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// TIME CAPSULE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "[ CLOSE ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Neutral500,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ARCHIVED ECHOES • Private audio vault visible only to you.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Neutral500
                )
                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkNeutral900)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TIME CAPSULE ARCHIVE ACTIVE.\n0 private archived echoes stored.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Neutral500
                    )
                }
            }
        }
    }
}

@Composable
fun TerminalPingSettingsModal(
    settings: TerminalSettings,
    onUpdate: (TerminalSettings) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PureWhite)
                .background(PitchBlack)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// PING SETTINGS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "[ DONE ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                ToggleRow(
                    label = "VIBE NOTIFICATIONS",
                    sub = "pings when someone vibes on your post",
                    value = settings.pingPulses,
                    onToggle = { onUpdate(settings.copy(pingPulses = !settings.pingPulses)) },
                    tag = "ping_pulses"
                )

                ToggleRow(
                    label = "DROP REVERB / VOICE REPLIES",
                    sub = "pings when someone drops a reverb",
                    value = settings.pingReverbs,
                    onToggle = { onUpdate(settings.copy(pingReverbs = !settings.pingReverbs)) },
                    tag = "ping_reverbs"
                )

                ToggleRow(
                    label = "ON FIRE NOTIFICATIONS",
                    sub = "pings when post reaches trending viral status",
                    value = settings.pingOnFire,
                    onToggle = { onUpdate(settings.copy(pingOnFire = !settings.pingOnFire)) },
                    tag = "ping_on_fire"
                )

                ToggleRow(
                    label = "LOCK-IN REQUESTS",
                    sub = "pings when users request orbiter access",
                    value = settings.pingLockIns,
                    onToggle = { onUpdate(settings.copy(pingLockIns = !settings.pingLockIns)) },
                    tag = "ping_lockins"
                )

                ToggleRow(
                    label = "STAGE ANNOUNCEMENTS",
                    sub = "pings when live audio stages begin",
                    value = settings.pingStage,
                    onToggle = { onUpdate(settings.copy(pingStage = !settings.pingStage)) },
                    tag = "ping_stage"
                )
            }
        }
    }
}

@Composable
fun TerminalDayOnesModal(
    dayOnes: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputHandle by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PureWhite)
                .background(PitchBlack)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// DAY ONES",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "[ CLOSE ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Neutral500,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Inner circle priority handles with full access.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Neutral500
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputHandle,
                        onValueChange = { inputHandle = it },
                        placeholder = { Text("@handle", color = Neutral500, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = DarkNeutral800,
                            focusedBorderColor = PureWhite,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                        )
                    )

                    Box(
                        modifier = Modifier
                            .border(1.dp, PureWhite)
                            .background(DarkNeutral900)
                            .clickable {
                                onAdd(inputHandle)
                                inputHandle = ""
                            }
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+ ADD",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(dayOnes) { handle ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DarkNeutral800)
                                .background(DarkNeutral900)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = handle,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                            Text(
                                text = "[ REMOVE ]",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = Neutral500,
                                modifier = Modifier.clickable { onRemove(handle) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TerminalExiledModal(
    exiled: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputHandle by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PureWhite)
                .background(PitchBlack)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// EXILED",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "[ CLOSE ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Neutral500,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Silenced voices and blocked handles.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Neutral500
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputHandle,
                        onValueChange = { inputHandle = it },
                        placeholder = { Text("@handle_to_exile", color = Neutral500, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = DarkNeutral800,
                            focusedBorderColor = PureWhite,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                        )
                    )

                    Box(
                        modifier = Modifier
                            .border(1.dp, PureWhite)
                            .background(DarkNeutral900)
                            .clickable {
                                onAdd(inputHandle)
                                inputHandle = ""
                            }
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EXILE",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(exiled) { handle ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DarkNeutral800)
                                .background(DarkNeutral900)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = handle,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                            Text(
                                text = "[ UN-EXILE ]",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = Neutral500,
                                modifier = Modifier.clickable { onRemove(handle) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TerminalHiddenWordsModal(
    words: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputWord by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PureWhite)
                .background(PitchBlack)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// HIDDEN WORDS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "[ CLOSE ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Neutral500,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Words automatically muted in reverbs & transcripts.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Neutral500
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputWord,
                        onValueChange = { inputWord = it },
                        placeholder = { Text("filter keyword", color = Neutral500, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = DarkNeutral800,
                            focusedBorderColor = PureWhite,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                        )
                    )

                    Box(
                        modifier = Modifier
                            .border(1.dp, PureWhite)
                            .background(DarkNeutral900)
                            .clickable {
                                onAdd(inputWord)
                                inputWord = ""
                            }
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+ ADD",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(words) { word ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DarkNeutral800)
                                .background(DarkNeutral900)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = word,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                            Text(
                                text = "[ REMOVE ]",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = Neutral500,
                                modifier = Modifier.clickable { onRemove(word) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TerminalEchoGuideModal(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PureWhite)
                .background(PitchBlack)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// ECHO GUIDE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "[ CLOSE ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Neutral500,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.height(320.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        GuideSection("1. FREQUENCY", "Public raw voice drop feed. Share authentic audio thoughts, pulse posts, and drop voice replies.")
                    }
                    item {
                        GuideSection("2. WAVES", "Short vertical audio reels with waveform visuals, sound clips, and looping voice tracks.")
                    }
                    item {
                        GuideSection("3. WHISPERS", "Private 1-on-1 encrypted audio notes sent directly to handles.")
                    }
                    item {
                        GuideSection("4. STAGE & CLASHES", "Live audio channels and real-time voice battles with spectator pulses.")
                    }
                    item {
                        GuideSection("5. AURA SCORE", "Engagement and voice activity score earned by broadcasting and participating.")
                    }
                }
            }
        }
    }
}

@Composable
fun GuideSection(title: String, desc: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkNeutral800)
            .background(DarkNeutral900)
            .padding(10.dp)
    ) {
        Text(
            text = title,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PureWhite
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = desc,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = Neutral500
        )
    }
}

@Composable
fun TerminalReportModal(
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var detailsText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("BUG") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PureWhite)
                .background(PitchBlack)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// REPORT A PROBLEM",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "[ CLOSE ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Neutral500,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("BUG", "AUDIO GLITCH", "CRASH", "OTHER").forEach { cat ->
                        val sel = cat == category
                        Box(
                            modifier = Modifier
                                .border(1.dp, if (sel) PureWhite else DarkNeutral800)
                                .background(if (sel) DarkNeutral900 else PitchBlack)
                                .clickable { category = cat }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = cat,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = if (sel) PureWhite else Neutral500
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = detailsText,
                    onValueChange = { detailsText = it },
                    placeholder = { Text("Describe the issue or crash logs...", color = Neutral500, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = DarkNeutral800,
                        focusedBorderColor = PureWhite,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PureWhite)
                        .background(PureWhite)
                        .clickable {
                            onSubmit(detailsText)
                            onDismiss()
                        }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SUBMIT TICKET",
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
}

@Composable
fun TerminalSafetyModal(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PureWhite)
                .background(PitchBlack)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// SAFETY CENTRE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "[ CLOSE ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Neutral500,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Text(
                    text = "Mental health resources, safety controls, and crisis support.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Neutral500
                )

                GuideSection("CRISIS HELPLINE", "Dial or Text 988 (Suicide & Crisis Lifeline) available 24/7 free & confidential.")
                GuideSection("MUTE & EXILE BOUNDARIES", "Exile handles from Terminal to instantly block all voice messages and audio reverbs.")
                GuideSection("ENCRYPTED WHISPERS", "Your private voice notes are end-to-end encrypted and stored locally.")
            }
        }
    }
}

@Composable
fun TerminalContactModal(
    onSend: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var msgText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PureWhite)
                .background(PitchBlack)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "// CONTACT ECHO TEAM",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "[ CLOSE ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Neutral500,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                GuideSection("EMAIL TEAM", "support@echo.audio")
                GuideSection("TERMINAL DISCORD", "discord.gg/echo-audio")

                OutlinedTextField(
                    value = msgText,
                    onValueChange = { msgText = it },
                    placeholder = { Text("Direct message to the Echo human team...", color = Neutral500, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = DarkNeutral800,
                        focusedBorderColor = PureWhite,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PureWhite)
                        .background(PureWhite)
                        .clickable {
                            onSend(msgText)
                            onDismiss()
                        }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TRANSMIT MESSAGE",
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
}

