package com.example.employeetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.employeetracker.data.getNormalizedDate
import com.example.employeetracker.models.AttendanceRecord
import com.example.employeetracker.models.AttendanceStatus
import com.example.employeetracker.repository.AttendanceRepository
import com.example.employeetracker.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(getNormalizedDate(Date()))

    private val _attendanceRecords = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendanceRecords: StateFlow<List<AttendanceRecord>> = _attendanceRecords

    init {
        viewModelScope.launch {
            _selectedDate.collect { date ->
                val normalizedDate = getNormalizedDate(date)
                // Combine employee list with attendance records for the selected date
                employeeRepository.getAllEmployees().combine(attendanceRepository.getAttendanceForDate(normalizedDate)) { employees, attendance ->
                    employees.map { employee ->
                        attendance.find { it.employeeId == employee.id } ?: AttendanceRecord(
                            employeeId = employee.id,
                            employeeName = employee.name,
                            date = normalizedDate,
                            status = AttendanceStatus.ABSENT // Default status
                        )
                    }
                }.collect { combined ->
                    _attendanceRecords.value = combined
                }
            }
        }
    }

    fun updateStatus(employeeId: Long, newStatus: AttendanceStatus) {
        viewModelScope.launch {
            val normalizedDate = getNormalizedDate(_selectedDate.value)
            val record = _attendanceRecords.value.find { it.employeeId == employeeId }?.copy(
                status = newStatus,
                date = normalizedDate // Ensure the date is normalized before saving
            ) ?: AttendanceRecord(
                employeeId = employeeId, 
                employeeName = "Unknown", 
                date = normalizedDate, 
                status = newStatus
            )

            // Use the new, corrected repository method
            attendanceRepository.markAttendance(record)
        }
    }

    fun loadAttendanceForDate(date: Date) {
        _selectedDate.value = getNormalizedDate(date)
    }
}
