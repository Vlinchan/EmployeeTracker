package com.example.employeetracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class ActivityType {
    EMPLOYEE_ADDED,
    EMPLOYEE_UPDATED,
    TASK_CREATED,
    TASK_COMPLETED,
    ATTENDANCE_MARKED,
    PERFORMANCE_UPDATED
}

@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val type: ActivityType,
    val timestamp: Date = Date(),
    val relatedEmployeeId: Long? = null,
    val relatedTaskId: Long? = null
)