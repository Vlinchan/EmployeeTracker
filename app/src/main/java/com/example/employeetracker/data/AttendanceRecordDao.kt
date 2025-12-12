package com.example.employeetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.employeetracker.models.AttendanceRecord
import com.example.employeetracker.models.AttendanceStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface AttendanceRecordDao {

    // --- The new, safer approach ---

    /**
     * Inserts a new attendance record. If a record with the same primary key already exists, it will be ignored.
     * This is the safe way to add new, distinct records.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: AttendanceRecord): Long

    /**
     * Updates the status of an existing record for a specific employee on a specific date.
     */
    @Query("UPDATE attendance_records SET status = :newStatus WHERE employeeId = :employeeId AND date = :date")
    suspend fun updateStatus(employeeId: Long, newStatus: AttendanceStatus, date: Date)

    /**
     * Finds a single attendance record for a given employee on a specific date.
     * This is crucial for checking if a record already exists before trying to insert a new one.
     */
    @Query("SELECT * FROM attendance_records WHERE employeeId = :employeeId AND date = :date LIMIT 1")
    suspend fun getAttendanceForEmployeeOnDate(employeeId: Long, date: Date): AttendanceRecord?


    // --- Existing queries ---

    @Query("SELECT * FROM attendance_records WHERE date = :date")
    fun getAttendanceForDate(date: Date): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records")
    fun getAllRecords(): Flow<List<AttendanceRecord>>

    // The problematic insertOrUpdateAttendance function has been removed.
}
