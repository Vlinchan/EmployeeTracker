package com.example.employeetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.employeetracker.models.Employee
import com.example.employeetracker.models.Task
import com.example.employeetracker.repository.EmployeeRepository
import com.example.employeetracker.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeDetailViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    fun getTasksForEmployee(employeeId: Long): StateFlow<List<Task>> {
        return taskRepository.getTasksForEmployee(employeeId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun updateEmployeePerformance(employee: Employee, performance: Float) {
        viewModelScope.launch {
            employeeRepository.updateEmployeePerformance(employee.id, performance)
        }
    }

    fun updateEmployeeRating(employee: Employee, rating: Int) {
        viewModelScope.launch {
            employeeRepository.updateEmployeeRating(employee.id, rating)
        }
    }

    fun updateTaskStatus(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = isCompleted)
            taskRepository.updateTask(updatedTask)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}
