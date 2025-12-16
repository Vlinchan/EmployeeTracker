package com.example.employeetracker.data

import androidx.room.TypeConverter
import com.example.employeetracker.models.ActivityType
import com.example.employeetracker.models.AttendanceStatus
import java.util.Calendar
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.let { getNormalizedDate(it).time }
    }

    @TypeConverter
    fun fromAttendanceStatus(value: String?): AttendanceStatus? {
        return value?.let { AttendanceStatus.valueOf(it) }
    }

    @TypeConverter
    fun attendanceStatusToString(status: AttendanceStatus?): String? {
        return status?.name
    }

    // ✅ NEW: ActivityType converters
    @TypeConverter
    fun fromActivityType(value: String?): ActivityType? {
        return value?.let { ActivityType.valueOf(it) }
    }

    @TypeConverter
    fun activityTypeToString(type: ActivityType?): String? {
        return type?.name
    }
}

fun getNormalizedDate(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}