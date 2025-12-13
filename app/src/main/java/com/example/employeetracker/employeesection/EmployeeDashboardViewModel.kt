package com.example.employeetracker.employeesection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.employeetracker.models.AttendanceRecord
import com.example.employeetracker.models.Task
import com.example.employeetracker.repository.AttendanceRepository
import com.example.employeetracker.repository.EmployeeRepository
import com.example.employeetracker.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployeeDashboardUiState(
    val employeeId: Long = 1L, // Default to first employee for demo
    val employeeName: String = "Loading...",
    val designation: String = "",
    val department: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val performance: Float = 0f,
    val rating: Int = 0,
    val tasks: List<Task> = emptyList(),
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val attendanceRecords: List<AttendanceRecord> = emptyList(),
    val attendanceRate: Float = 0f,
    val isLoading: Boolean = true
)

@HiltViewModel
class EmployeeDashboardViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val taskRepository: TaskRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    // In a real app, you'd get this from login session
    // For now, using the first employee as example
    private val currentEmployeeId = MutableStateFlow(1L)

    val uiState: StateFlow<EmployeeDashboardUiState> = combine(
        currentEmployeeId,
        employeeRepository.getAllEmployees(),
        taskRepository.getAllTasks(),
        attendanceRepository.getAllRecords()
    ) { employeeId, allEmployees, allTasks, allAttendance ->

        // Find current employee
        val employee = allEmployees.find { it.id == employeeId } ?: allEmployees.firstOrNull()

        if (employee == null) {
            return@combine EmployeeDashboardUiState(isLoading = false)
        }

        // Get tasks for this employee
        val employeeTasks = allTasks.filter { it.employeeId == employee.id }
        val completedCount = employeeTasks.count { it.isCompleted }

        // Get attendance for this employee
        val employeeAttendance = allAttendance.filter { it.employeeId == employee.id }
        val presentCount = employeeAttendance.count {
            it.status == com.example.employeetracker.models.AttendanceStatus.PRESENT
        }
        val attendanceRate = if (employeeAttendance.isNotEmpty()) {
            (presentCount.toFloat() / employeeAttendance.size) * 100
        } else {
            0f
        }

        EmployeeDashboardUiState(
            employeeId = employee.id,
            employeeName = employee.name,
            designation = employee.designation,
            department = employee.department,
            email = employee.email,
            phoneNumber = employee.phoneNumber,
            performance = employee.performance,
            rating = employee.rating,
            tasks = employeeTasks,
            totalTasks = employeeTasks.size,
            completedTasks = completedCount,
            attendanceRecords = employeeAttendance,
            attendanceRate = attendanceRate,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EmployeeDashboardUiState()
    )

    fun setEmployeeId(employeeId: Long) {
        currentEmployeeId.value = employeeId
    }

    fun toggleTaskStatus(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            taskRepository.updateTask(updatedTask)
        }
    }
}