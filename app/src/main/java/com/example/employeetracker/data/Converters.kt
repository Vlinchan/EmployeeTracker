package com.example.employeetracker.data

import androidx.room.TypeConverter
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
        // Before saving to the database, normalize the date to remove the time component.
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
}

/**
 * Normalizes a date by setting its time components (hour, minute, second, millisecond) to zero.
 * This ensures that two dates on the same day are treated as equal, regardless of the time.
 */
fun getNormalizedDate(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}
