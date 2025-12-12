package com.example.employeetracker.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.employeetracker.R
import com.example.employeetracker.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userViewModel: UserViewModel = viewModel(),
    onBack: () -> Unit,
    onTeamOverviewClick: () -> Unit,
    onDirectReportsClick: () -> Unit,
    onPendingReviewsClick: () -> Unit,
    onFeedbackRequestsClick: () -> Unit,
    onPersonalSettingsClick: () -> Unit, // Maps to SettingsScreen
    onLogout: () -> Unit
) {
    val userName by userViewModel.userName.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var tempUserName by remember { mutableStateOf(userName) }

    // --- State for "Workable" Dialogs ---
    var showTeamStatsDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var showGoalsDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(userName) {
        tempUserName = userName
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- 1. HEADER SECTION (Avatar & Name) ---
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar with Ring
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        // Use a default icon if you don't have a real image resource handy,
                        // or keep your R.drawable.ic_launcher_background
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Editable Name Logic
                    if (isEditing) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = tempUserName,
                                onValueChange = { tempUserName = it },
                                label = { Text("Full Name") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                userViewModel.updateUserName(tempUserName)
                                isEditing = false
                            }) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(onClick = { isEditing = true }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Text(
                        text = "Senior Engineering Manager",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. MY TEAM SECTION ---
            ProfileSectionHeader("MY TEAM")

            // "Team Overview" -> Triggers Dialog
            ProfileRow(
                icon = Icons.Default.Groups,
                title = "Team Overview",
                subtitle = "12 Direct Reports",
                onClick = { showTeamStatsDialog = true }
            )

            // "Direct Reports" -> Triggers Navigation Callback
            ProfileRow(
                icon = Icons.Default.Face,
                title = "Direct Reports",
                subtitle = "View individual profiles",
                onClick = onDirectReportsClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3. MANAGEMENT TASKS ---
            ProfileSectionHeader("MANAGEMENT TASKS")

            // "Pending Reviews" -> Triggers Dialog (simulated work)
            ProfileRow(
                icon = Icons.Default.PendingActions,
                title = "Pending Reviews",
                subtitle = "3 reviews due this week",
                trailingHighlight = "3",
                onClick = { showReviewDialog = true }
            )

            // "Goal Approvals" -> Triggers Dialog
            ProfileRow(
                icon = Icons.Default.TrackChanges,
                title = "Goal Approvals",
                subtitle = "Q1 Objectives",
                onClick = { showGoalsDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- 4. SETTINGS & LOGOUT ---
            ProfileSectionHeader("SYSTEM")

            ProfileRow(
                icon = Icons.Default.Settings,
                title = "App Settings",
                subtitle = "Notifications, Password, Theme",
                onClick = onPersonalSettingsClick // Navigate to the SettingsScreen we built before
            )

            ProfileRow(
                icon = Icons.Default.Logout,
                title = "Logout",
                onClick = { showLogoutConfirm = true },
                isDestructive = true
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        // --- DIALOGS (To make the screen "Workable") ---

        // 1. Team Stats Dialog
        if (showTeamStatsDialog) {
            ProfileDialog(title = "Team Overview", onDismiss = { showTeamStatsDialog = false }) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatBadge("12", "Members")
                    StatBadge("92%", "Performance")
                    StatBadge("4", "Promotions")
                }
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { 0.8f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                )
                Text("Team Goal Completion", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp))
            }
        }

        // 2. Pending Reviews Dialog
        if (showReviewDialog) {
            ProfileDialog(title = "Pending Reviews", onDismiss = { showReviewDialog = false }) {
                ReviewItem("Sarah Jenkins", "Mid-Year Review", "Due in 2 days")
                ReviewItem("Mike Ross", "Probation Review", "Overdue")
                ReviewItem("Jessica Pearson", "Annual Review", "Due in 5 days")
            }
        }

        // 3. Goal Approvals Dialog
        if (showGoalsDialog) {
            ProfileDialog(title = "Goal Approvals", onDismiss = { showGoalsDialog = false }) {
                Text("The following employees have submitted goals for approval:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showGoalsDialog = false }, modifier = Modifier.fillMaxWidth()) {
                    Text("Approve All (2 Pending)")
                }
            }
        }

        // 4. Logout Confirmation
        if (showLogoutConfirm) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirm = false },
                title = { Text("Logout") },
                text = { Text("Are you sure you want to log out of your account?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutConfirm = false
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

// --- HELPER COMPONENTS ---

@Composable
fun ProfileSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
fun ProfileRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailingHighlight: String? = null,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isDestructive) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isDestructive) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (trailingHighlight != null) {
                Surface(
                    color = MaterialTheme.colorScheme.error,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = trailingHighlight,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun ProfileDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                content()
            }
        }
    }
}

@Composable
fun StatBadge(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ReviewItem(name: String, type: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.tertiaryContainer, modifier = Modifier.size(32.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Text(name.first().toString(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold)
            Text(type, style = MaterialTheme.typography.bodySmall)
        }
        Text(status, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
    }
}