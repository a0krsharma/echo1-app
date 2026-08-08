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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
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
import com.echo.app.ui.theme.DarkNeutral900
import com.echo.app.ui.theme.Neutral500
import com.echo.app.ui.theme.PitchBlack
import com.echo.app.ui.theme.PureWhite
import com.echo.app.ui.viewmodel.MainViewModel

@Composable
fun NotificationsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }
    val filterTabs = listOf("ALL", "VIBES", "DROP REVERBS", "ORBITERS", "STAGE")

    val filteredList = notifications.filter { notif ->
        selectedFilter == "ALL" || notif.category == selectedFilter || (selectedFilter == "VIBES" && notif.category == "PULSES") || (selectedFilter == "DROP REVERBS" && notif.category == "REVERBS")
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "// PINGS · REALTIME NOTIFICATIONS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Neutral500,
                    letterSpacing = 2.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        modifier = Modifier
                            .clickable { viewModel.markAllNotificationsRead() }
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Mark Read",
                            tint = Neutral500,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "READ ALL",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = Neutral500
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .border(1.dp, DarkNeutral900)
                            .background(PitchBlack)
                            .clickable { viewModel.clearAllNotifications() }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("clear_all_notifications_btn")
                    ) {
                        Text(
                            text = "[ CLEAR ALL ]",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Neutral500
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Tabs
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filterTabs) { tab ->
                    val isSelected = selectedFilter == tab
                    Text(
                        text = tab,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PureWhite else Neutral500,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .clickable { selectedFilter = tab }
                            .padding(vertical = 4.dp)
                            .testTag("notif_tab_$tab")
                    )
                }
            }
        }

        if (filteredList.isEmpty()) {
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
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Neutral500,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "NO $selectedFilter NOTIFICATIONS YET.",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Neutral500,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Activity on your echoes and reverbs will appear here in real time.",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 13.sp,
                            color = Neutral500
                        )
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { notif ->
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
                        Text(
                            text = notif.title,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "[ DELETE ]",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Neutral500,
                            modifier = Modifier
                                .clickable { viewModel.deleteNotification(notif.id) }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .testTag("delete_notif_${notif.id}")
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notif.body,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 13.sp,
                        color = PureWhite
                    )
                }
            }
        }

        item {
            Text(
                text = "NOTIFICATIONS ARE PURGED AFTER 30 DAYS",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Neutral500,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
