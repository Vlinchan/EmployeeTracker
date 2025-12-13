package com.example.employeetracker.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.employeetracker.viewmodels.EmployeeViewModel
import com.example.employeetracker.viewmodels.TaskViewModel
import com.example.employeetracker.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onTeamOverviewClick: () -> Unit,
    onDirectReportsClick: () -> Unit,
    onPendingReviewsClick: () -> Unit,
    onFeedbackRequestsClick: () -> Unit,
    onPersonalSettingsClick: () -> Unit,
    onLogout: () -> Unit
) {
    val userName by userViewModel.userName.collectAsState()
    val employees by employeeViewModel.allEmployees.collectAsState()
    val tasks by taskViewModel.allTasks.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var tempUserName by remember { mutableStateOf(userName) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    // Calculate real statistics from database
    val totalEmployees = employees.size
    val avgPerformance = if (employees.isNotEmpty()) {
        (employees.sumOf { it.performance.toDouble() } / employees.size * 100).toInt()
    } else 0

    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.isCompleted }
    val pendingTasks = totalTasks - completedTasks

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

            // --- 1. HEADER SECTION ---
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
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = userName.split(" ")
                            .mapNotNull { it.firstOrNull() }
                            .take(2)
                            .joinToString("")
                        Text(
                            initials,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Editable Name
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
                        text = "Admin Manager",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. STATISTICS CARD (From Real Database) ---
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Team Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatBadge(totalEmployees.toString(), "Employees")
                        StatBadge("$avgPerformance%", "Avg Rating")
                        StatBadge(completedTasks.toString(), "Tasks Done")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3. MY TEAM SECTION ---
            ProfileSectionHeader("MY TEAM")

            ProfileRow(
                icon = Icons.Default.Groups,
                title = "Team Overview",
                subtitle = "$totalEmployees Active Members",
                onClick = onTeamOverviewClick
            )

            ProfileRow(
                icon = Icons.Default.Face,
                title = "Direct Reports",
                subtitle = "View all employees",
                onClick = onDirectReportsClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- 4. TASKS ---
            ProfileSectionHeader("MANAGEMENT")

            ProfileRow(
                icon = Icons.Default.PendingActions,
                title = "Pending Tasks",
                subtitle = "$pendingTasks tasks remaining",
                trailingHighlight = if (pendingTasks > 0) pendingTasks.toString() else null,
                onClick = onPendingReviewsClick
            )

            ProfileRow(
                icon = Icons.Default.CheckCircle,
                title = "Completed Tasks",
                subtitle = "$completedTasks tasks finished",
                onClick = onPendingReviewsClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- 5. SETTINGS & LOGOUT ---
            ProfileSectionHeader("SYSTEM")

            ProfileRow(
                icon = Icons.Default.Settings,
                title = "App Settings",
                subtitle = "Notifications, Password, Theme",
                onClick = onPersonalSettingsClick
            )

            ProfileRow(
                icon = Icons.Default.Logout,
                title = "Logout",
                onClick = { showLogoutConfirm = true },
                isDestructive = true
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Logout Confirmation
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
fun StatBadge(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}