package com.example.employeetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.employeetracker.models.Employee
import com.example.employeetracker.models.Task
import com.example.employeetracker.models.Activity
import com.example.employeetracker.models.ActivityType
import com.example.employeetracker.repository.EmployeeRepository
import com.example.employeetracker.repository.TaskRepository
import com.example.employeetracker.data.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val employeeRepository: EmployeeRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    val allTasks: StateFlow<List<Task>> = taskRepository.getAllTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allEmployees: StateFlow<List<Employee>> = employeeRepository.getAllEmployees().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _taskAssignmentError = MutableStateFlow<String?>(null)
    val taskAssignmentError: StateFlow<String?> = _taskAssignmentError.asStateFlow()

    fun addTask(title: String, description: String, employeeId: Long) {
        viewModelScope.launch {
            val task = Task(title = title, description = description, employeeId = employeeId)
            taskRepository.insertTask(task)
            
            val employee = employeeRepository.getEmployeeById(employeeId)
            val employeeName = employee?.name ?: "Unknown Employee"
            
            activityRepository.addActivity(
                Activity(
                    title = "New Task Assigned",
                    description = "Task '$title' assigned to $employeeName",
                    type = ActivityType.TASK_CREATED,
                    relatedEmployeeId = employeeId
                )
            )
        }
    }

    fun updateTaskStatus(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = isCompleted)
            taskRepository.updateTask(updatedTask)
            
            if (isCompleted) {
                val employee = employeeRepository.getEmployeeById(task.employeeId)
                val employeeName = employee?.name ?: "Unknown Employee"
                
                activityRepository.addActivity(
                    Activity(
                        title = "Task Completed",
                        description = "Task '${task.title}' completed by $employeeName",
                        type = ActivityType.TASK_COMPLETED,
                        relatedEmployeeId = task.employeeId,
                        relatedTaskId = task.id
                    )
                )
            }
        }
    }

    fun clearTaskAssignmentError() {
        _taskAssignmentError.value = null
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}
