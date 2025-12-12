package com.example.employeetracker.repository

import com.example.employeetracker.data.AttendanceRecordDao
import com.example.employeetracker.models.AttendanceRecord
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class AttendanceRepository @Inject constructor(private val attendanceDao: AttendanceRecordDao) {

    fun getAttendanceForDate(date: Date): Flow<List<AttendanceRecord>> {
        return attendanceDao.getAttendanceForDate(date)
    }

    fun getAllRecords(): Flow<List<AttendanceRecord>> = attendanceDao.getAllRecords()

    /**
     * Inserts a new attendance record or updates an existing one for a given employee and date.
     * This prevents creating duplicate records for the same day.
     */
    suspend fun markAttendance(record: AttendanceRecord) {
        // Check if a record already exists for this employee on this specific date.
        val existingRecord = attendanceDao.getAttendanceForEmployeeOnDate(record.employeeId, record.date)

        if (existingRecord == null) {
            // If no record exists, insert a new one.
            attendanceDao.insert(record)
        } else {
            // If a record already exists, just update its status.
            attendanceDao.updateStatus(record.employeeId, record.status, record.date)
        }
    }
}
