package com.echo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.echo.app.ui.components.GoogleSignInModal
import com.echo.app.ui.screens.FrequencyScreen
import com.echo.app.ui.screens.NotificationsScreen
import com.echo.app.ui.screens.ProfileScreen
import com.echo.app.ui.screens.RadarScreen
import com.echo.app.ui.screens.StageDetailScreen
import com.echo.app.ui.screens.StageScreen
import com.echo.app.ui.screens.StudioScreen
import com.echo.app.ui.screens.TerminalScreen
import com.echo.app.ui.screens.WavesScreen
import com.echo.app.ui.screens.WhispersScreen
import com.echo.app.ui.theme.AccentGreen
import com.echo.app.ui.theme.DarkNeutral900
import com.echo.app.ui.theme.EchoTheme
import com.echo.app.ui.theme.Neutral500
import com.echo.app.ui.theme.PitchBlack
import com.echo.app.ui.theme.PureWhite
import com.echo.app.ui.viewmodel.MainViewModel

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.text.style.TextOverflow
import com.echo.app.ui.theme.AccentFire

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 1001)
        }

        setContent {
            EchoTheme {
                EchoMainApp(viewModel = viewModel)
            }
        }
    }
}

enum class NavigationDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    FREQUENCY("frequency", "FREQUENCY", Icons.Default.GraphicEq),
    WAVES("waves", "WAVES", Icons.Default.VolumeUp),
    STAGE("stage", "STAGE", Icons.Default.SportsKabaddi),
    STUDIO("studio", "STUDIO", Icons.Default.Mic),
    RADAR("radar", "RADAR", Icons.Default.Radar),
    PROFILE("profile", "PROFILE", Icons.Default.Person),
    TERMINAL("terminal", "TERMINAL", Icons.Default.Terminal),
    WHISPERS("whispers", "WHISPERS", Icons.Default.VolumeUp),
    NOTIFICATIONS("notifications", "NOTIFICATIONS", Icons.Default.Notifications)
}

@Composable
fun EchoMainApp(viewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf(NavigationDestination.FREQUENCY) }
    var selectedStageClashId by remember { mutableStateOf<String?>(null) }
    var showGoogleAuthModal by remember { mutableStateOf(false) }

    val userProfile by viewModel.userProfile.collectAsState()

    if (showGoogleAuthModal) {
        GoogleSignInModal(
            viewModel = viewModel,
            onDismiss = { showGoogleAuthModal = false },
            onSignInSuccess = {
                currentScreen = NavigationDestination.TERMINAL
                showGoogleAuthModal = false
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        containerColor = PitchBlack,
        topBar = {
            Column {
                TopHeaderBar(
                    currentScreen = currentScreen,
                    userProfile = userProfile,
                    onOpenGoogleAuth = { showGoogleAuthModal = true },
                    onNavigateToNotifications = { currentScreen = NavigationDestination.NOTIFICATIONS },
                    onNavigateToTerminal = { currentScreen = NavigationDestination.TERMINAL }
                )
                TrendingTickerBar()
            }
        },
        bottomBar = {
            Column {
                BackgroundRoomAudioBar(viewModel = viewModel)
                UtilitarianBottomNav(
                    currentScreen = currentScreen,
                    onSelectScreen = { destination ->
                        selectedStageClashId = null
                        currentScreen = destination
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                selectedStageClashId != null -> {
                    StageDetailScreen(
                        clashId = selectedStageClashId!!,
                        viewModel = viewModel,
                        onBack = { selectedStageClashId = null }
                    )
                }

                else -> when (currentScreen) {
                    NavigationDestination.FREQUENCY -> FrequencyScreen(viewModel = viewModel)
                    NavigationDestination.WAVES -> WavesScreen(viewModel = viewModel)
                    NavigationDestination.STAGE -> StageScreen(
                        viewModel = viewModel,
                        onNavigateToStageDetail = { clashId -> selectedStageClashId = clashId }
                    )
                    NavigationDestination.STUDIO -> StudioScreen(
                        viewModel = viewModel,
                        onPublished = { currentScreen = NavigationDestination.FREQUENCY }
                    )
                    NavigationDestination.RADAR -> RadarScreen(viewModel = viewModel)
                    NavigationDestination.PROFILE -> ProfileScreen(viewModel = viewModel)
                    NavigationDestination.TERMINAL -> TerminalScreen(viewModel = viewModel)
                    NavigationDestination.WHISPERS -> WhispersScreen(viewModel = viewModel)
                    NavigationDestination.NOTIFICATIONS -> NotificationsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun TopHeaderBar(
    currentScreen: NavigationDestination,
    userProfile: com.echo.app.data.models.EchoUserProfile?,
    onOpenGoogleAuth: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToTerminal: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PitchBlack)
            .border(width = 1.dp, color = DarkNeutral900)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Echo.",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "// ${currentScreen.title}",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Neutral500,
                letterSpacing = 1.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // dB GAIN Signal Meter Pill
            val signal = userProfile?.signalDb ?: 24
            Box(
                modifier = Modifier
                    .border(1.dp, com.echo.app.ui.theme.DarkNeutral800)
                    .background(com.echo.app.ui.theme.DarkNeutral900)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .testTag("signal_db_meter_pill"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SIGNAL: $signal dB",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = com.echo.app.ui.theme.PureWhite
                )
            }

            // Google Auth Pill Button / Badge
            Box(
                modifier = Modifier
                    .border(
                        1.dp,
                        if (userProfile?.isGoogleSignedIn == true) PureWhite else com.echo.app.ui.theme.DarkNeutral800
                    )
                    .background(if (userProfile?.isGoogleSignedIn == true) com.echo.app.ui.theme.DarkNeutral800 else PitchBlack)
                    .clickable { onOpenGoogleAuth() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("top_google_sign_in_btn"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(PureWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = PitchBlack
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = if (userProfile?.isGoogleSignedIn == true) {
                            userProfile.googleAccountName?.take(10) ?: "GOOGLE"
                        } else {
                            "SIGN IN"
                        },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = PureWhite,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onNavigateToNotifications() }
                    .testTag("top_nav_notifications")
            )
            Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = "Terminal Settings",
                tint = PureWhite,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onNavigateToTerminal() }
                    .testTag("top_nav_terminal")
            )
        }
    }
}

@Composable
fun UtilitarianBottomNav(
    currentScreen: NavigationDestination,
    onSelectScreen: (NavigationDestination) -> Unit
) {
    val navItems = listOf(
        NavigationDestination.FREQUENCY,
        NavigationDestination.WAVES,
        NavigationDestination.STAGE,
        NavigationDestination.STUDIO,
        NavigationDestination.RADAR,
        NavigationDestination.PROFILE
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PitchBlack)
            .border(width = 1.dp, color = DarkNeutral900)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { destination ->
                val isSelected = currentScreen == destination
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onSelectScreen(destination) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("bottom_nav_${destination.route}")
                ) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title,
                        tint = if (isSelected) PureWhite else Neutral500,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = destination.title,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PureWhite else Neutral500,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TrendingTickerBar() {
    val newsItems = remember {
        listOf(
            "🔥 TRENDING: #AuraDebate • \"AI vs Human Creative Synthesis\"",
            "🎙️ LIVE STAGE: @YOU vs @ANON_TSWG • 1,420 Active Listeners",
            "⚡ REALTIME WAVE: @ANON_LASJ dropped \"ORBIT: hellow pfnd\"",
            "🏆 TOP RADAR: @ANON_2HNA reached 1,420 Aura Points",
            "💬 HOT REVERB: @ZARA.IQ replied with +180 voice wave drop"
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(3800)
            currentIndex = (currentIndex + 1) % newsItems.size
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkNeutral900)
            .border(width = 1.dp, color = PitchBlack)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(PureWhite, RoundedCornerShape(2.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "LIVE",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = PitchBlack,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        AnimatedContent(
            targetState = newsItems[currentIndex],
            transitionSpec = {
                (fadeIn(animationSpec = tween(400))) togetherWith fadeOut(animationSpec = tween(400))
            },
            label = "trending_ticker"
        ) { newsText ->
            Text(
                text = newsText,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = PureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BackgroundRoomAudioBar(viewModel: MainViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activeRoomId by viewModel.activeRoomId.collectAsState()
    val activeRoomTitle by viewModel.activeRoomTitle.collectAsState()
    val isRoomPlayingInBg by viewModel.isRoomPlayingInBg.collectAsState()
    val isRoomMuted by viewModel.isRoomMuted.collectAsState()
    val mySpeakRequestClashId by viewModel.mySpeakRequestClashId.collectAsState()

    if (!isRoomPlayingInBg || activeRoomId == null) return

    val isMyRequestPending = mySpeakRequestClashId == activeRoomId

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkNeutral900)
            .border(width = 1.dp, color = AccentGreen)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("background_room_audio_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(AccentGreen, androidx.compose.foundation.shape.CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "LIVE ROOM: ${activeRoomTitle ?: "BACKGROUND STAGE"}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isRoomMuted) "MUTED • TAP UNMUTE" else "BACKGROUND AUDIO PLAYING",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        color = Neutral500
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Mute / Unmute
                Box(
                    modifier = Modifier
                        .border(1.dp, PureWhite)
                        .clickable { viewModel.toggleMuteRoom() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isRoomMuted) "UNMUTE" else "MUTE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = PureWhite
                    )
                }

                // Ask to Speak
                Box(
                    modifier = Modifier
                        .border(1.dp, if (isMyRequestPending) AccentGreen else PureWhite)
                        .background(if (isMyRequestPending) AccentGreen else PitchBlack)
                        .clickable {
                            val roomId = activeRoomId
                            if (roomId != null) {
                                if (isMyRequestPending) {
                                    viewModel.cancelSpeakRequest(roomId)
                                } else {
                                    viewModel.requestToSpeak(roomId)
                                }
                            }
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isMyRequestPending) "✋ PENDING" else "✋ SPEAK",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMyRequestPending) PitchBlack else PureWhite
                    )
                }

                // Share Story
                Box(
                    modifier = Modifier
                        .border(1.dp, com.echo.app.ui.theme.AccentFire)
                        .clickable {
                            viewModel.shareToStoryStatus(
                                context = context,
                                title = activeRoomTitle ?: "Live Room",
                                caption = "Live Room Audio active on Stage",
                                author = "@YOU"
                            )
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "📱 SHARE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = com.echo.app.ui.theme.AccentFire
                    )
                }

                // Leave Room
                Box(
                    modifier = Modifier
                        .border(1.dp, com.echo.app.ui.theme.AccentFire)
                        .clickable { viewModel.leaveStageRoom() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✕",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = com.echo.app.ui.theme.AccentFire
                    )
                }
            }
        }
    }
}
