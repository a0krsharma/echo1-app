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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.draw.clip
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

data class RadarTrendingTopic(
    val rank: Int,
    val category: String,
    val topicName: String,
    val postCount: String,
    val velocityText: String,
    val summaryText: String
)

data class RadarAudioNews(
    val id: String,
    val headline: String,
    val narratorHandle: String,
    val duration: String,
    val durationSec: Int,
    val categoryTag: String,
    val timestampText: String,
    val listenersCount: String
)

@Composable
fun RadarScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val categories = listOf("ALL", "TRENDING TOPICS", "AUDIO NEWS", "RISING FAST", "LIVE STAGE ROOMS")
    val echoes by viewModel.echoes.collectAsState()
    val clashes by viewModel.clashes.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val playingPostId by viewModel.playingPostId.collectAsState()
    val playbackProgress by viewModel.playbackProgress.collectAsState()

    // Sample Trending Topics (Twitter/X style)
    val trendingTopics = remember {
        listOf(
            RadarTrendingTopic(
                rank = 1,
                category = "TECH & AI",
                topicName = "#NeuralAudioModels",
                postCount = "4.2K ECHOS",
                velocityText = "🔥 +620% VELOCITY",
                summaryText = "Creators debating zero-shot voice synthesis vs raw analog microphone feeds."
            ),
            RadarTrendingTopic(
                rank = 2,
                category = "AUDIO ENGINE",
                topicName = "#SpatialBinaural3D",
                postCount = "2.8K ECHOS",
                velocityText = "⚡ RISING FAST",
                summaryText = "High-fidelity 3D spatial acoustics drops taking over nocturnal frequency channels."
            ),
            RadarTrendingTopic(
                rank = 3,
                category = "PHILOSOPHY",
                topicName = "#UnfilteredVoiceOnly",
                postCount = "1.9K ECHOS",
                velocityText = "📈 +310% TODAY",
                summaryText = "Global network debate on why text algorithms kill emotional resonance."
            ),
            RadarTrendingTopic(
                rank = 4,
                category = "DEBATES",
                topicName = "#StageClash2026",
                postCount = "3.5K LISTENERS",
                velocityText = "🔴 12 LIVE ROOMS",
                summaryText = "Live audio debate battles on Stage reaching peak audience participation."
            )
        )
    }

    // Sample Breaking Audio News Items
    val audioNewsList = remember {
        listOf(
            RadarAudioNews(
                id = "news-1",
                headline = "BREAKING: Global Audio Mesh Protocol V3 Deployed on Echo",
                narratorHandle = "@ECHO_NEWS_HUB",
                duration = "00:45",
                durationSec = 45,
                categoryTag = "TECH",
                timestampText = "12 MIN AGO",
                listenersCount = "3.4K HEARD"
            ),
            RadarAudioNews(
                id = "news-2",
                headline = "TRENDING DISCOVERY: Ultra-low Latency Voice Whispers Reach 100Hz Resonance",
                narratorHandle = "@RESONANCE_LABS",
                duration = "00:30",
                durationSec = 30,
                categoryTag = "AUDIO",
                timestampText = "45 MIN AGO",
                listenersCount = "1.8K HEARD"
            ),
            RadarAudioNews(
                id = "news-3",
                headline = "RISING TOPIC: Autonomous Voice Synthesizers Enter Live Stage Battles",
                narratorHandle = "@STAGE_DISPATCH",
                duration = "00:50",
                durationSec = 50,
                categoryTag = "DEBATES",
                timestampText = "2 HOURS AGO",
                listenersCount = "5.1K HEARD"
            )
        )
    }

    val filteredEchoes = echoes.filter { echo ->
        searchQuery.isBlank() || echo.caption.contains(searchQuery, ignoreCase = true) || echo.authorHandle.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "// RADAR & DISCOVERY",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Neutral500,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Search input field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "SEARCH TOPICS, NEWS, VOICES...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Neutral500,
                        letterSpacing = 1.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Neutral500,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("radar_search_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PureWhite,
                    unfocusedBorderColor = DarkNeutral800,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Categories horizontal tab selector
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .border(1.dp, if (isSelected) PureWhite else DarkNeutral800)
                            .background(if (isSelected) PureWhite else PitchBlack)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) PitchBlack else Neutral500,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // Section: RISING LIVE STAGE ROOMS (Twitter Spaces style)
        if (selectedCategory == "ALL" || selectedCategory == "LIVE STAGE ROOMS") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔴 RISING LIVE STAGE ROOMS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${clashes.size} ACTIVE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = AccentFire,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    clashes.forEach { clash ->
                        LiveStageRoomCard(
                            clash = clash,
                            onVote = { side -> viewModel.voteOnClash(clash.id, side) }
                        )
                    }
                }
            }
        }

        // Section: TRENDING TOPICS
        if (selectedCategory == "ALL" || selectedCategory == "TRENDING TOPICS") {
            item {
                Text(
                    text = "🔥 TRENDING TOPICS & HASHTAGS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    trendingTopics.forEach { topic ->
                        TrendingTopicCard(
                            topic = topic,
                            onClick = { searchQuery = topic.topicName.removePrefix("#") }
                        )
                    }
                }
            }
        }

        // Section: BREAKING AUDIO NEWS & FAST RISING DISPATCHES
        if (selectedCategory == "ALL" || selectedCategory == "AUDIO NEWS" || selectedCategory == "RISING FAST") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ BREAKING AUDIO NEWS & DISPATCHES",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "REALTIME",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = Neutral500
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    audioNewsList.forEach { news ->
                        val isPlaying = playingPostId == news.id
                        AudioNewsCard(
                            news = news,
                            isPlaying = isPlaying,
                            progress = if (isPlaying) playbackProgress else 0f,
                            onPlayToggle = { viewModel.togglePlayEcho(news.id, news.durationSec) }
                        )
                    }
                }
            }
        }

        // Section: MATCHING VOICES & USER AURA
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "// DISCOVERY MESH & AUTHENTIC VOICES",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Neutral500,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkNeutral900)
                    .background(PitchBlack)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = userProfile?.handle ?: "@YOU",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "AURA: ${userProfile?.auraScore ?: 1420} • TX UPTIME: ${userProfile?.txUptimeDays ?: 14} DAYS",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Neutral500
                        )
                    }

                    Box(
                        modifier = Modifier
                            .border(1.dp, DarkNeutral800)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "AUTHENTICATED",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Neutral500
                        )
                    }
                }
            }
        }

        // Section: DISCOVERED FREQUENCIES FEED
        item {
            Text(
                text = "// DISCOVERED FREQUENCIES (${filteredEchoes.size})",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Neutral500,
                letterSpacing = 1.sp
            )
        }

        if (filteredEchoes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkNeutral900)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO MATCHING ECHOES FOUND.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Neutral500,
                        letterSpacing = 1.sp
                    )
                }
            }
        } else {
            items(filteredEchoes, key = { "radar_${it.id}" }) { echo ->
                val isPlaying = playingPostId == echo.id
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkNeutral900)
                        .background(DarkNeutral900)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = echo.authorHandle,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Box(
                            modifier = Modifier
                                .border(1.dp, DarkNeutral800)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = echo.categoryTag,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = Neutral500
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"${echo.caption}\"",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 13.sp,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .border(1.dp, DarkNeutral800)
                                .clickable { viewModel.togglePlayEcho(echo.id, echo.durationSec) }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = PureWhite,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPlaying) "PLAYING" else "LISTEN (${echo.duration})",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = PureWhite
                            )
                        }

                        Row(
                            modifier = Modifier
                                .border(1.dp, DarkNeutral800)
                                .clickable { viewModel.togglePulseWithTelemetry(echo.id, 1) }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡ ${echo.pulseCount} PULSES",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = if (echo.isPulsed) AccentFire else Neutral500,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isPlaying) {
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { playbackProgress },
                            modifier = Modifier.fillMaxWidth().height(2.dp),
                            color = PureWhite,
                            trackColor = DarkNeutral800
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun LiveStageRoomCard(
    clash: ClashItem,
    onVote: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkNeutral800)
            .background(DarkNeutral900)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AccentFire)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "LIVE AUDIO ROOM",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentFire,
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = "📡 ${clash.listeners} LISTENERS",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = clash.title,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PureWhite
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "\"${clash.topic}\"",
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 13.sp,
            color = Neutral500
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Speakers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SPEAKERS: ${clash.handleA} vs ${clash.handleB}",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Neutral500
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .border(1.dp, DarkNeutral800)
                        .clickable { onVote("A") }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "VOTE ${clash.handleA.take(6)} [${clash.votesA}]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = PureWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .border(1.dp, DarkNeutral800)
                        .clickable { onVote("B") }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "VOTE ${clash.handleB.take(6)} [${clash.votesB}]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = PureWhite
                    )
                }
            }
        }
    }
}

@Composable
fun TrendingTopicCard(
    topic: RadarTrendingTopic,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkNeutral900)
            .background(PitchBlack)
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#${topic.rank} IN ${topic.category}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = Neutral500
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = topic.velocityText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentFire
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = topic.topicName,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = topic.summaryText,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 11.sp,
                color = Neutral500
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .border(1.dp, DarkNeutral800)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = topic.postCount,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
        }
    }
}

@Composable
fun AudioNewsCard(
    news: RadarAudioNews,
    isPlaying: Boolean,
    progress: Float,
    onPlayToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkNeutral800)
            .background(DarkNeutral900)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .border(1.dp, DarkNeutral800)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = news.categoryTag,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }

            Text(
                text = "${news.timestampText} • ${news.listenersCount}",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = Neutral500
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = news.headline,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PureWhite
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NARRATED BY: ${news.narratorHandle}",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = Neutral500
            )

            Row(
                modifier = Modifier
                    .border(1.dp, DarkNeutral800)
                    .clickable { onPlayToggle() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.VolumeUp,
                    contentDescription = "Listen News",
                    tint = PureWhite,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isPlaying) "PLAYING BULLETIN..." else "LISTEN BULLETIN (${news.duration})",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }
        }

        if (isPlaying) {
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color = AccentFire,
                trackColor = DarkNeutral800
            )
        }
    }
}

