package com.example.employeetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.employeetracker.models.AttendanceStatus
import com.example.employeetracker.repository.AttendanceRepository
import com.example.employeetracker.repository.TaskRepository
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PerformanceUiState(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val taskCompletionRate: Float = 0f,
    val attendanceRate: Float = 0f,
    val statusChartModel: com.patrykandpatrick.vico.core.entry.ChartEntryModel? = null,
    val weeklyActivityModel: com.patrykandpatrick.vico.core.entry.ChartEntryModel? = null
)

@HiltViewModel
class PerformanceViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    val uiState: StateFlow<PerformanceUiState> = combine(
        taskRepository.getAllTasks(),
        attendanceRepository.getAllRecords()
    ) { tasks, attendanceList ->
        val total = tasks.size
        val completed = tasks.count { it.isCompleted }
        val pending = total - completed
        val completionRate = if (total > 0) completed.toFloat() / total else 0f

        val presentDays = attendanceList.count { it.status == AttendanceStatus.PRESENT }
        val totalDays = attendanceList.size
        val attRate = if (totalDays > 0) (presentDays.toFloat() / totalDays) * 100 else 0f

        val statusProducer = ChartEntryModelProducer(listOf(
            entryOf(0, pending.toFloat()),
            entryOf(1, completed.toFloat())
        ))

        val recentActivityEntries = tasks.takeLast(7).mapIndexed { index, task ->
            entryOf(index.toFloat(), if (task.isCompleted) 1f else 0f)
        }
        val activityProducer = ChartEntryModelProducer(recentActivityEntries)

        PerformanceUiState(
            totalTasks = total,
            completedTasks = completed,
            pendingTasks = pending,
            taskCompletionRate = completionRate,
            attendanceRate = attRate,
            statusChartModel = statusProducer.getModel(),
            weeklyActivityModel = activityProducer.getModel()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PerformanceUiState()
    )
}