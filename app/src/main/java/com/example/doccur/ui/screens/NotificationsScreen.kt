package com.example.doccur.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings

import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doccur.entities.Notification
import com.example.doccur.viewmodels.NotificationViewModel
import androidx.compose.ui.text.TextStyle
import com.example.doccur.ui.theme.AppColors
import com.example.doccur.ui.theme.Inter

val customTextStyle = TextStyle(
    fontFamily = Inter,
)
@Composable
fun NotificationsScreen(
    viewModel: NotificationViewModel,
    userId: Int,
    userType: String
) {
    // Collect state from ViewModel
    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Effect to fetch notifications when the screen is displayed
    // and connect to WebSocket
    LaunchedEffect(key1 = userId, key2 = userType) {
        viewModel.fetchNotifications(userId, userType)
        // La connexion WebSocket est maintenant gérée dans fetchNotifications()
    }

    // Effet pour déconnecter le WebSocket lors de la destruction
    DisposableEffect(key1 = Unit) {
        onDispose {
            viewModel.disconnectWebSocket()
        }
    }

    CompositionLocalProvider(
        LocalTextStyle provides customTextStyle
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with title and "Mark all as read" button
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 4.dp,
                backgroundColor = Color.White,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal=24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    TextButton(onClick = {
                        // Mark all notifications as read
                        notifications.forEach { notification ->
                            if (!notification.isRead) {
                                viewModel.markAsRead(notification.id)
                            }
                        }
                    }) {
                        Text(
                            text = "Mark all as read",
                            color = AppColors.Blue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(20.dp))


            Box(modifier = Modifier.fillMaxSize()) {
                // Show loading spinner if loading
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Show error if there's an error
                error?.let { errorMsg ->
                    Text(
                        text = errorMsg,
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                // Show notifications list
                if (!isLoading && error == null) {
                    if (notifications.isEmpty()) {
                        // Show empty state
                        Text(
                            text = "No notifications",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        // Show notifications list
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(notifications) { notification ->
                                // Determine icon based on content (in a real app, this would be determined by notification type)
                                val message = notification.message.orEmpty()

                                val (icon, iconBackgroundColor) = when {
                                    // Doctor confirmed appointment
                                    message.contains("confirm", ignoreCase = true) ->
                                        Pair(Icons.Default.CheckCircle, AppColors.Green) // greenish background

                                    // Doctor rejected appointment
                                    message.contains("reject", ignoreCase = true) ->
                                        Pair(Icons.Default.Cancel, Color(0xFFB91C1C)) // light red background

                                    // Patient canceled appointment
                                    message.contains("cancel", ignoreCase = true) ->
                                        Pair(Icons.Default.Cancel, Color(0xFFB91C1C)) // same as rejection (can customize if needed)

                                    // Patient rescheduled appointment
                                    message.contains("reschedule", ignoreCase = true) ->
                                        Pair(Icons.Default.EventRepeat, Color(0xFFB91C1C)) // light blue background

                                    // Default fallback
                                    else ->
                                        Pair(Icons.Default.Notifications, Color(0xFFE3E3E3))
                                }

                                // Determine timeAgo text based on createdAt
                                //val timeAgo = formatTimeAgo(notification.createdAt)

                                NotificationItem(
                                    notification = notification,
                                    icon = icon,
                                    iconColor = iconBackgroundColor,
                                    //timeAgo = timeAgo,
                                    isUnread = !notification.isRead,
                                    onClick = { viewModel.markAsRead(notification.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Fonction pour formater le temps écoulé
//private fun formatTimeAgo(dateTimeStr: String): String {
//    return try {
//        val instant = Instant.parse(dateTimeStr)
//        val now = Instant.now()
//        val duration = Duration.between(instant, now)
//
//        when {
//            duration.toMinutes() < 1 -> "Just now"
//            duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
//            duration.toHours() < 24 -> "${duration.toHours()} hours ago"
//            duration.toDays() < 30 -> "${duration.toDays()} days ago"
//            else -> {
//                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
//                    .withZone(ZoneId.systemDefault())
//                formatter.format(instant)
//            }
//        }
//    } catch (e: Exception) {
//        "Unknown time"
//    }
//}

@Composable
fun NotificationItem(
    notification: Notification,
    icon: ImageVector,
    iconColor: Color,
    isUnread: Boolean,
    onClick: () -> Unit
) {

    CompositionLocalProvider(
        LocalTextStyle provides customTextStyle
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(onClick = onClick)  // Ajouter cette ligne pour gérer les clics
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = Color.White,
                border = BorderStroke(0.2.dp, AppColors.LightGray),

                ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Notification icon with background

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                    )


                    Spacer(modifier = Modifier.width(16.dp))

                    // Notification content
                    Column(modifier = Modifier.weight(1f)) {
                        // Ajout du titre de la notification
                        notification.title?.let { title ->
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Text(
                            text = notification.message ?: "No message available",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                    }

                    // Unread indicator
                    if (isUnread) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AppColors.Blue)
                        )
                    }
                }
            }
        }
    }
}