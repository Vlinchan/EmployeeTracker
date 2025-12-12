package com.example.employeetracker

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.employeetracker.data.ThemeDataStore
import com.example.employeetracker.models.AttendanceStatus
import com.example.employeetracker.models.Employee
import com.example.employeetracker.screens.*
import com.example.employeetracker.viewmodels.AttendanceViewModel
import com.example.employeetracker.viewmodels.EmployeeViewModel
import com.example.employeetracker.viewmodels.TaskViewModel
import com.example.employeetracker.viewmodels.UserViewModel
import kotlinx.coroutines.launch

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

sealed interface DashboardScreen {
    val label: String
    val icon: ImageVector

    data object Overview : DashboardScreen {
        override val label = "Overview"
        override val icon = Icons.Filled.Dashboard
    }

    data object Employees : DashboardScreen {
        override val label = "Employees"
        override val icon = Icons.Filled.People
    }

    data object Tasks : DashboardScreen {
        override val label = "Tasks"
        override val icon = Icons.Filled.Task
    }

    data object Attendance : DashboardScreen {
        override val label = "Attendance"
        override val icon = Icons.Filled.CoPresent
    }

    data object Performance : DashboardScreen {
        override val label = "Performance"
        override val icon = Icons.AutoMirrored.Filled.TrendingUp
    }

    data object Settings : DashboardScreen {
        override val label = "Settings"
        override val icon = Icons.Filled.Settings
    }

    data object RecentActivity : DashboardScreen {
        override val label = "Recent Activity"
        override val icon = Icons.Filled.History // Won't be shown in drawer
    }

    data object Profile : DashboardScreen {
        override val label = "Profile"
        override val icon = Icons.Filled.Person // Won't be shown in drawer
    }

    data object Feedback : DashboardScreen {
        override val label = "Feedback"
        override val icon = Icons.Filled.Feedback // Won't be shown in drawer
    }

    data object ChangePassword : DashboardScreen {
        override val label = "Change Password"
        override val icon = Icons.Filled.Lock // Won't be shown in drawer
    }
    data object Notification : DashboardScreen {
        override val label = "Notification"
        override val icon = Icons.Filled.Notifications // Won't be shown in drawer
    }


    data class EmployeeDetail(val employee: Employee) : DashboardScreen {
        override val label = employee.name
        override val icon = Icons.Filled.Person
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenMain(
    onLogout: () -> Unit,
    themeDataStore: ThemeDataStore,
    onNavigateToSettings: () -> Unit,
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel(),
    attendanceViewModel: AttendanceViewModel = hiltViewModel()
) {

    var selectedScreen by remember { mutableStateOf<DashboardScreen>(DashboardScreen.Overview) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val activity = context.findActivity()

    val employees by employeeViewModel.allEmployees.collectAsState()
    val tasks by taskViewModel.allTasks.collectAsState()
    val taskAssignmentError by taskViewModel.taskAssignmentError.collectAsState()
    val attendanceRecords by attendanceViewModel.attendanceRecords.collectAsState()

    BackHandler {
        if (selectedScreen != DashboardScreen.Overview) {
            selectedScreen = DashboardScreen.Overview
        } else {
            activity?.moveTaskToBack(true)
        }
    }

    val screensInDrawer = listOf(
        DashboardScreen.Overview,
        DashboardScreen.Employees,
        DashboardScreen.Tasks,
        DashboardScreen.Attendance,
        DashboardScreen.Performance,
        DashboardScreen.Settings
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                screensInDrawer.forEach { screen ->
                    DrawerItemRow(
                        icon = screen.icon,
                        label = screen.label,
                        selected = selectedScreen == screen
                    ) {
                        if (screen == DashboardScreen.Settings) {
                            onNavigateToSettings()
                        } else {
                            selectedScreen = screen
                        }
                        scope.launch { drawerState.close() }
                    }
                }
                HorizontalDivider()
                DrawerItemRow(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    label = "Logout",
                    selected = false
                ) {
                    scope.launch { drawerState.close() }
                    onLogout()
                }
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                if (selectedScreen !is DashboardScreen.EmployeeDetail && selectedScreen != DashboardScreen.Profile && selectedScreen != DashboardScreen.ChangePassword) {
                    if (selectedScreen == DashboardScreen.Overview) {
                        TopAppBarDashboard(scope, drawerState, onNotificationClick = { selectedScreen = DashboardScreen.Notification}, onProfileClick = { selectedScreen = DashboardScreen.Profile })
                    } else {
                        SimpleTopAppBar(selectedScreen.label, scope, drawerState)
                    }
                }
            },
        ) { paddingValues ->
            Crossfade(targetState = selectedScreen, label = "screen_transition") { screen ->
                when (screen) {
                    is DashboardScreen.EmployeeDetail -> {
                        EmployeeDetailScreen(
                            employee = screen.employee,
                            onBack = { selectedScreen = DashboardScreen.Employees }
                        )
                    }
                    DashboardScreen.Overview -> OverviewPage(
                        modifier = Modifier.padding(paddingValues),
                        employees = employees,
                        tasks = tasks,
                        onGoToEmployeesScreen = { selectedScreen = DashboardScreen.Employees },
                        onGoToRecentActivityScreen = { selectedScreen = DashboardScreen.RecentActivity },
                        onEmployeeClick = { employee -> selectedScreen = DashboardScreen.EmployeeDetail(employee) }
                    )
                    DashboardScreen.Employees -> EmployeesScreen(
                        modifier = Modifier.padding(paddingValues),
                        employees = employees,
                        onDeleteEmployee = { /* TODO */ },
                        onAddEmployee = { name, email, designation, department, phoneNumber ->
                            employeeViewModel.addEmployee(name, email, designation, department, phoneNumber)
                        },
                        onEmployeeClick = { employee -> selectedScreen = DashboardScreen.EmployeeDetail(employee) }
                    )

                    DashboardScreen.Tasks -> TasksScreen(
                        modifier = Modifier.padding(paddingValues),
                        tasks = tasks,
                        employees = employees,
                        onAddTask = { title, employeeId -> taskViewModel.addTask(title, employeeId) },
                        onDeleteTask = { task -> taskViewModel.deleteTask(task) },
                        onUpdateTaskStatus = { task, isCompleted -> taskViewModel.updateTaskStatus(task, isCompleted) },
                        errorMessage = taskAssignmentError,
                        onClearTaskAssignmentError = { taskViewModel.clearTaskAssignmentError() }
                    )
                    DashboardScreen.Attendance -> {
                        AttendanceScreen(
                            modifier = Modifier.padding(paddingValues),
                            records = attendanceRecords,
                            onUpdateStatus = { employeeId: Long, newStatus: AttendanceStatus -> attendanceViewModel.updateStatus(employeeId, newStatus) },
                            onDateSelected = { date -> attendanceViewModel.loadAttendanceForDate(date) },
                            onNavigateToSettings = { onNavigateToSettings() }
                        )
                    }
                    DashboardScreen.Performance -> {
                        PerformanceScreen(onBack = { selectedScreen = DashboardScreen.Overview })
                    }
                    DashboardScreen.Settings -> SettingsScreen(
                        modifier = Modifier.padding(paddingValues),
                        themeDataStore = themeDataStore,
                        onLogout = onLogout,
                        onNavigateToChangePassword = { selectedScreen = DashboardScreen.ChangePassword },
                        onBack = { selectedScreen = DashboardScreen.Overview }
                    )
                    DashboardScreen.RecentActivity -> RecentActivityScreen(onBack = { selectedScreen = DashboardScreen.Overview })
                    DashboardScreen.Profile -> ProfileScreen(
                        userViewModel = userViewModel,
                        onBack = { selectedScreen = DashboardScreen.Overview },
                        onTeamOverviewClick = { selectedScreen = DashboardScreen.Overview },
                        onDirectReportsClick = { selectedScreen = DashboardScreen.Employees },
                        onPendingReviewsClick = { selectedScreen = DashboardScreen.Tasks },
                        onFeedbackRequestsClick = { selectedScreen = DashboardScreen.Feedback },
                        onPersonalSettingsClick = { selectedScreen = DashboardScreen.Settings },
                        onLogout = onLogout
                    )
                    DashboardScreen.Feedback -> {
                        /* TODO: Create FeedbackScreen */
                    }
                    DashboardScreen.ChangePassword -> {
                        ChangePasswordScreen(
                            userViewModel = userViewModel,
                            onBack = { selectedScreen = DashboardScreen.Settings }
                        )
                    }
                     DashboardScreen.Notification -> NotificationScreen(onBack = { selectedScreen = DashboardScreen.Overview })
                }
            }
        }
    }
}
