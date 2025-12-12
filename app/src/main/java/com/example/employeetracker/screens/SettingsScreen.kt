package com.example.employeetracker.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.employeetracker.data.ThemeDataStore
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    themeDataStore: ThemeDataStore,
    onLogout: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onBack: () -> Unit // Added for navigation
) {
    val isDarkTheme by themeDataStore.isDarkTheme.collectAsState(initial = true)
    val scope = rememberCoroutineScope()

    // --- State for Dialogs ---
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showCycleDialog by remember { mutableStateOf(false) }
    var showMilestoneDialog by remember { mutableStateOf(false) }
    var showTemplateDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var showRolesDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Account Section
            SettingsSection(title = "ACCOUNT") {
                SettingsItem(
                    icon = Icons.Default.Security,
                    title = "Password & Security",
                    onClick = onNavigateToChangePassword
                )
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    onClick = { showNotificationDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "Appearance",
                    subtitle = "Current: ${if(isDarkTheme) "Dark Mode" else "Light Mode"}",
                    onClick = { scope.launch { themeDataStore.setTheme(!isDarkTheme) } }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Performance Cycles Section
            SettingsSection(title = "PERFORMANCE CYCLES") {
                Text(
                    text = "Control when and how long your performance process runs.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(16.dp)
                )
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Create/Edit Cycle",
                    subtitle = "Define timing for new review periods.",
                    onClick = { showCycleDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(
                    icon = Icons.Default.DateRange,
                    title = "Milestones & Deadlines",
                    subtitle = "Set due dates for each phase.",
                    onClick = { showMilestoneDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(
                    icon = Icons.Default.Article,
                    title = "Default Template Assignment",
                    subtitle = "Assign standard review forms.",
                    onClick = { showTemplateDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(
                    icon = Icons.Default.Verified,
                    title = "Cycle Status Management",
                    subtitle = "Open, Close or Archive cycles.",
                    onClick = { showStatusDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Core Configuration Section
            SettingsSection(title = "CORE CONFIGURATION") {
                SettingsItem(
                    icon = Icons.Default.Article,
                    title = "Review Templates",
                    onClick = { showTemplateDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.Grade,
                    title = "Rating Scales",
                    onClick = { showRatingDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.AdminPanelSettings,
                    title = "User Roles & Access",
                    onClick = { showRolesDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. App Preferences Section (Theme Switch)
            SettingsSection(title = "APP PREFERENCES") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Display Mode",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Display Mode",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = !isDarkTheme,
                        onCheckedChange = { scope.launch { themeDataStore.setTheme(!it) } }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Logout", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- DIALOG IMPLEMENTATIONS ---

        if (showNotificationDialog) {
            SimpleSettingsDialog(
                title = "Notifications",
                onDismiss = { showNotificationDialog = false }
            ) {
                DialogSwitchItem("Email Notifications", true)
                DialogSwitchItem("Push Notifications", true)
                DialogSwitchItem("Weekly Summary", false)
            }
        }

        if (showCycleDialog) {
            SimpleSettingsDialog(
                title = "Create New Cycle",
                onDismiss = { showCycleDialog = false },
                confirmButtonText = "Create"
            ) {
                OutlinedTextField(
                    value = "FY 2025 Annual Review",
                    onValueChange = {},
                    label = { Text("Cycle Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = "Jan 1, 2025 - Dec 31, 2025",
                    onValueChange = {},
                    label = { Text("Duration") },
                    trailingIcon = { Icon(Icons.Default.DateRange, null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (showMilestoneDialog) {
            SimpleSettingsDialog(
                title = "Milestones",
                onDismiss = { showMilestoneDialog = false }
            ) {
                Text("Set deadlines for the current active cycle.", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))
                DialogDateItem("Self Review Due", "March 15, 2025")
                DialogDateItem("Manager Review Due", "March 30, 2025")
                DialogDateItem("HR Calibration", "April 5, 2025")
            }
        }

        if (showTemplateDialog) {
            SimpleSettingsDialog(
                title = "Review Templates",
                onDismiss = { showTemplateDialog = false },
                confirmButtonText = "Save Selection"
            ) {
                var selected by remember { mutableIntStateOf(0) }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selected == 0, onClick = { selected = 0 })
                        Text("Standard Performance Review")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selected == 1, onClick = { selected = 1 })
                        Text("Engineering Competency Framework")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selected == 2, onClick = { selected = 2 })
                        Text("Sales Quarterly Review")
                    }
                }
            }
        }

        if (showStatusDialog) {
            SimpleSettingsDialog(
                title = "Cycle Status",
                onDismiss = { showStatusDialog = false }
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Current: FY2025 Q1", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("Status: Active", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                Button(
                    onClick = { showStatusDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close Cycle & Archive")
                }
            }
        }

        if (showRatingDialog) {
            SimpleSettingsDialog(
                title = "Rating Configuration",
                onDismiss = { showRatingDialog = false }
            ) {
                Text("Select the rating scale used for reviews.")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("1 - 5 Scale (Standard)")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("1 - 10 Scale (Detailed)")
                }
            }
        }

        if (showRolesDialog) {
            SimpleSettingsDialog(title = "User Access", onDismiss = { showRolesDialog = false }) {
                Text("Manage admin privileges.", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                DialogSwitchItem("Allow Employee Self-Registration", false)
                DialogSwitchItem("Managers can edit finalized reviews", true)
            }
        }
    }
}

// --- HELPER COMPOSABLES ---

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
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

        // This handles the Arrow icon safely
        val arrowIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight
        Icon(
            imageVector = arrowIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun SimpleSettingsDialog(
    title: String,
    onDismiss: () -> Unit,
    confirmButtonText: String = "Save",
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                content()
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(onClick = onDismiss) {
                        Text(confirmButtonText)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogSwitchItem(text: String, initialChecked: Boolean) {
    var checked by remember { mutableStateOf(initialChecked) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = { checked = it })
    }
}

@Composable
fun DialogDateItem(label: String, date: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            Text(date, style = MaterialTheme.typography.bodyMedium)
        }
        Icon(Icons.Default.DateRange, contentDescription = null)
    }
}
