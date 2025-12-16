package com.example.employeetracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.employeetracker.models.Activity
import com.example.employeetracker.models.AttendanceRecord
import com.example.employeetracker.models.Employee
import com.example.employeetracker.models.Task

@Database(
    entities = [Employee::class, AttendanceRecord::class, Task::class, Activity::class],
    version = 7, // ✅ INCREMENTED from 6 to 7
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun employeeDao(): EmployeeDao
    abstract fun attendanceRecordDao(): AttendanceRecordDao
    abstract fun taskDao(): TaskDao
    abstract fun activityDao(): ActivityDao // ✅ NEW
}