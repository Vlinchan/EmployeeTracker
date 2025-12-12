package com.example.employeetracker.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.employeetracker.models.AttendanceRecord
import com.example.employeetracker.models.AttendanceStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Define constant colors for consistency
val PresentColor = Color(0xFF34D399)
val AbsentColor = Color(0xFFFB7185)
val LeaveColor = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    modifier: Modifier = Modifier,
    records: List<AttendanceRecord>,
    onUpdateStatus: (employeeId: Long, newStatus: AttendanceStatus) -> Unit,
    onDateSelected: (date: Date) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var displayedDate by remember { mutableStateOf(Date()) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AttendanceHeader(
                date = displayedDate,
                onCalendarClick = { showDatePicker = true },
                onSettingsClick = onNavigateToSettings
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AttendanceSummary(records = records)
                }

                item {
                    Text(
                        "Daily Records",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(records, key = { it.employeeId }) { record ->
                    AttendanceItem(
                        record = record,
                        onStatusChange = { newStatus ->
                            onUpdateStatus(record.employeeId, newStatus)
                        }
                    )
                }
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selected = Date(millis + TimeZone.getDefault().getOffset(millis))
                            displayedDate = selected
                            onDateSelected(selected)
                        }
                    }) { Text("Confirm") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun AttendanceHeader(
    date: Date,
    onCalendarClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp, top = 16.dp, bottom = 16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Attendance",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                dateFormat.format(date),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row {
            IconButton(onClick = onCalendarClick) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select Date",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AttendanceSummary(records: List<AttendanceRecord>) {
    val presentCount = records.count { it.status == AttendanceStatus.PRESENT }
    val absentCount = records.count { it.status == AttendanceStatus.ABSENT }
    val onLeaveCount = records.count { it.status == AttendanceStatus.LEAVE }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SummaryCard("Present", presentCount, PresentColor, Modifier.weight(1f))
        SummaryCard("Absent", absentCount, AbsentColor, Modifier.weight(1f))
        SummaryCard("Leave", onLeaveCount, LeaveColor, Modifier.weight(1f))
    }
}

@Composable
fun SummaryCard(title: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = count.toString(),
                color = color,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AttendanceItem(record: AttendanceRecord, onStatusChange: (AttendanceStatus) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    val initials =
                        record.employeeName.split(" ").mapNotNull { it.firstOrNull() }.take(2)
                            .joinToString("")
                    Text(
                        initials,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    record.employeeName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            StatusToggleGroup(currentStatus = record.status, onStatusChange = onStatusChange)
        }
    }
}

@Composable
fun StatusToggleGroup(currentStatus: AttendanceStatus, onStatusChange: (AttendanceStatus) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatusButton(
            text = "Present",
            status = AttendanceStatus.PRESENT,
            currentStatus = currentStatus,
            color = PresentColor,
            onClick = onStatusChange,
            modifier = Modifier.weight(1f)
        )
        StatusButton(
            text = "Absent",
            status = AttendanceStatus.ABSENT,
            currentStatus = currentStatus,
            color = AbsentColor,
            onClick = onStatusChange,
            modifier = Modifier.weight(1f)
        )
        StatusButton(
            text = "Leave",
            status = AttendanceStatus.LEAVE,
            currentStatus = currentStatus,
            color = LeaveColor,
            onClick = onStatusChange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatusButton(
    text: String,
    status: AttendanceStatus,
    currentStatus: AttendanceStatus,
    color: Color,
    onClick: (AttendanceStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = status == currentStatus
    val buttonColors = if (isSelected) {
        ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White
        )
    } else {
        ButtonDefaults.outlinedButtonColors(
            contentColor = color
        )
    }

    OutlinedButton(
        onClick = { onClick(status) },
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(8.dp),
        colors = buttonColors,
        border = if (isSelected) null else BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}