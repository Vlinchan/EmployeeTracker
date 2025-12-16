package com.example.employeetracker.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.employeetracker.models.Activity
import com.example.employeetracker.models.ActivityType
import com.example.employeetracker.models.Employee
import com.example.employeetracker.models.Task
import com.example.employeetracker.data.repository.ActivityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarDashboard(
    scope: CoroutineScope,
    drawerState: DrawerState,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text("Dashboard", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = onNotificationClick) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            }
            IconButton(onClick = onProfileClick) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "ET",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBar(title: String, scope: CoroutineScope, drawerState: DrawerState) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun OverviewPage(
    modifier: Modifier = Modifier,
    employees: List<Employee>,
    tasks: List<Task>,
    activities: List<Activity> = emptyList(), // ✅ NEW: Real activities from database
    onGoToEmployeesScreen: () -> Unit,
    onGoToRecentActivityScreen: () -> Unit,
    onEmployeeClick: (Employee) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredEmployees = if (searchQuery.isNotBlank()) {
        employees.filter { it.name.contains(searchQuery, ignoreCase = true) }
    } else {
        employees
    }

    val filteredTasks = if (searchQuery.isNotBlank()) {
        tasks.filter { it.title.contains(searchQuery, ignoreCase = true) }
    } else {
        tasks
    }

    // Calculate real KPIs from database
    val avgPerformance = if (employees.isNotEmpty()) {
        (employees.sumOf { it.performance.toDouble() } / employees.size * 100).toInt()
    } else 0

    val completedTasks = tasks.count { it.isCompleted }
    val avgRating = if (employees.isNotEmpty()) {
        (employees.sumOf { it.rating.toDouble() } / employees.size)
    } else 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {

        // 1. Search Bar
        item {
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
        }

        // 2. Dashboard Widgets (Only when not searching)
        if (searchQuery.isBlank()) {
            item {
                SectionTitle("Overview", null)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RealKpiCard(
                            title = "Performance",
                            value = "$avgPerformance%",
                            icon = Icons.Default.TrendingUp,
                            modifier = Modifier.weight(1f)
                        )
                        RealKpiCard(
                            title = "Employees",
                            value = "${employees.size}",
                            icon = Icons.Default.People,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RealKpiCard(
                            title = "Tasks Done",
                            value = "$completedTasks",
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f)
                        )
                        RealKpiCard(
                            title = "Avg Rating",
                            value = String.format("%.1f", avgRating),
                            icon = Icons.Default.Star,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item { SectionTitle("Top Performers", onViewAllClick = onGoToEmployeesScreen) }
            items(filteredEmployees.sortedByDescending { it.performance }.take(5)) { employee ->
                EmployeeProgressItem(emp = employee, onClick = { onEmployeeClick(employee) })
            }

            // ✅ Real Recent Activity from Database
            item { SectionTitle("Recent Activity", onViewAllClick = onGoToRecentActivityScreen) }
            items(activities.take(5)) { activity ->
                RealActivityItem(activity = activity)
            }

            if (activities.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(32.dp).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No recent activity",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            // 3. Search Results
            if (filteredEmployees.isNotEmpty()) {
                item { SectionTitle("Employees") }
                items(filteredEmployees) { employee ->
                    EmployeeProgressItem(emp = employee, onClick = { onEmployeeClick(employee) })
                }
            }

            if (filteredTasks.isNotEmpty()) {
                item { SectionTitle("Tasks") }
                items(filteredTasks) { task -> TaskItemRow(task = task) }
            }

            if (filteredEmployees.isEmpty() && filteredTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "No results found for \"$searchQuery\"",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// ✅ NEW: Real Activity Item with database data
@Composable
fun RealActivityItem(activity: Activity) {
    val icon = when (activity.type) {
        ActivityType.EMPLOYEE_ADDED -> Icons.Default.PersonAdd
        ActivityType.EMPLOYEE_UPDATED -> Icons.Default.Edit
        ActivityType.TASK_CREATED -> Icons.Default.AddTask
        ActivityType.TASK_COMPLETED -> Icons.Default.CheckCircle
        ActivityType.ATTENDANCE_MARKED -> Icons.Default.EventAvailable
        ActivityType.PERFORMANCE_UPDATED -> Icons.Default.TrendingUp
    }

    val iconColor = when (activity.type) {
        ActivityType.EMPLOYEE_ADDED -> Color(0xFF00C853)
        ActivityType.TASK_COMPLETED -> Color(0xFF00C853)
        ActivityType.PERFORMANCE_UPDATED -> Color(0xFF6200EA)
        ActivityType.TASK_CREATED -> Color(0xFF2196F3)
        ActivityType.ATTENDANCE_MARKED -> Color(0xFFFFAB00)
        ActivityType.EMPLOYEE_UPDATED -> Color(0xFF757575)
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = iconColor.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = getTimeAgo(activity.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Helper function to get "time ago" format
fun getTimeAgo(date: Date): String {
    val now = Date()
    val diff = now.time - date.time

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
        diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}d ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
    }
}

// ✅ Real KPI Card with actual data
@Composable
fun RealKpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search employees, tasks...") },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun EmployeeProgressItem(emp: Employee, onDelete: (() -> Unit)? = null, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val initials = if (emp.name.isNotBlank()) {
                        emp.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2)
                            .joinToString("")
                    } else {
                        "??"
                    }
                    Text(
                        initials,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = emp.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { emp.performance.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    color = when {
                        emp.performance > 0.85f -> Color(0xFF00C853)
                        emp.performance > 0.6f -> Color(0xFFFFAB00)
                        else -> Color(0xFFD50000)
                    }
                )
            }

            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun TaskItemRow(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (task.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "ID: ${task.employeeId}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, onViewAllClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.weight(1f))
        onViewAllClick?.let {
            Text(
                text = "View all",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = it)
            )
        }
    }
}

@Composable
fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(24.dp)
    ) {
        Text(
            text = "Employee Tracker",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = "Admin Panel",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun DrawerItemRow(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label, fontWeight = FontWeight.Medium) },
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}