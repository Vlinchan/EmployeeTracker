package com.example.employeetracker.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.employeetracker.models.Employee
import com.example.employeetracker.viewmodels.EmployeeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
    employee: Employee,
    onBack: () -> Unit,
    employeeDetailViewModel: EmployeeDetailViewModel = hiltViewModel()
) {
    val tasks by employeeDetailViewModel.getTasksForEmployee(employee.id).collectAsState()
    var performance by remember { mutableFloatStateOf(employee.performance) }
    var rating by remember { mutableIntStateOf(employee.rating) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(employee.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(employee.designation, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(24.dp))

            Text("Performance", style = MaterialTheme.typography.titleLarge)
            Slider(
                value = performance,
                onValueChange = { performance = it },
                onValueChangeFinished = { employeeDetailViewModel.updateEmployeePerformance(employee, performance) },
                valueRange = 0f..1f,
                steps = 9
            )
            Text("${(performance * 100).toInt()}%", modifier = Modifier.align(Alignment.End))

            Spacer(modifier = Modifier.height(24.dp))

            Text("Overall Rating", style = MaterialTheme.typography.titleLarge)
            Row(modifier = Modifier.fillMaxWidth()) {
                (1..5).forEach { index ->
                    Icon(
                        imageVector = if (index <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Star $index",
                        tint = if (index <= rating) Color.Yellow else Color.Gray,
                        modifier = Modifier.clickable {
                            rating = index
                            employeeDetailViewModel.updateEmployeeRating(employee, rating)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Tasks", style = MaterialTheme.typography.titleLarge)
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(tasks) { task ->
                    TaskCard(
                        task = task,
                        onDelete = { employeeDetailViewModel.deleteTask(task) },
                        onStatusChange = { isCompleted -> employeeDetailViewModel.updateTaskStatus(task, isCompleted) }
                    )
                }
            }
        }
    }
}
