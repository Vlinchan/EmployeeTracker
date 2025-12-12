package com.example.employeetracker.screens

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.employeetracker.ui.theme.VividGreen
import com.example.employeetracker.ui.theme.VividOrange
import com.example.employeetracker.viewmodels.PerformanceUiState
import com.example.employeetracker.viewmodels.PerformanceViewModel

// --- VICO IMPORTS (Fixed for v2.0+) ---
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.overlayingComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.DashedShape
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.marker.Marker

// Define colors locally if not available in theme
val ChartPrimary = Color(0xFF6366F1)
val BackgroundGray = Color(0xFFF8FAFC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceScreen(
    onBack: () -> Unit = {},
    viewModel: PerformanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Analytics",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundGray,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        PerformanceContent(
            uiState = uiState,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun PerformanceContent(
    uiState: PerformanceUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- 1. Top KPI Row ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiCard(
                title = "Attendance",
                value = "${uiState.attendanceRate.toInt()}%",
                icon = Icons.Default.DateRange,
                color = VividGreen,
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                title = "Pending",
                value = "${uiState.pendingTasks}",
                icon = Icons.Default.PendingActions,
                color = VividOrange,
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                title = "Done",
                value = "${uiState.completedTasks}",
                icon = Icons.Default.CheckCircle,
                color = ChartPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        // --- 2. Main Stats Row (Donut + Priority) ---
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left: Overall Progress
            Card(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Overall", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    AnimatedCircularGauge(
                        percentage = uiState.taskCompletionRate,
                        color = VividGreen
                    )
                }
            }

            // Right: Priority Breakdown
            Card(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("Task Priority", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    PriorityBar("High", 0.7f, Color(0xFFEF4444))
                    PriorityBar("Med", 0.4f, Color(0xFFF59E0B))
                    PriorityBar("Low", 0.2f, Color(0xFF3B82F6))
                }
            }
        }

        // --- 3. Bar Chart: Status Distribution ---
        if (uiState.statusChartModel != null) {
            GraphCard(title = "Task Status Breakdown", subtitle = "Comparison of Pending vs Done") {
                val horizontalAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                    when (value.toInt()) {
                        0 -> "Pending"
                        1 -> "Done"
                        else -> ""
                    }
                }

                Chart(
                    chart = columnChart(
                        columns = listOf(
                            lineComponent(
                                color = VividOrange,
                                thickness = 16.dp,
                                shape = Shapes.roundedCornerShape(topLeftPercent = 50, topRightPercent = 50)
                            ),
                            lineComponent(
                                color = VividGreen,
                                thickness = 16.dp,
                                shape = Shapes.roundedCornerShape(topLeftPercent = 50, topRightPercent = 50)
                            )
                        )
                    ),
                    model = uiState.statusChartModel!!, // Force unwrapped as we checked null
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(valueFormatter = horizontalAxisValueFormatter),
                    marker = rememberMarker(),
                    modifier = Modifier.height(200.dp).fillMaxWidth()
                )
            }
        }

        // --- 4. Line Chart: Trend ---
        if (uiState.weeklyActivityModel != null) {
            GraphCard(title = "Productivity Trend", subtitle = "Tasks completed over the last 7 days") {
                Chart(
                    chart = lineChart(
                        lines = listOf(
                            lineSpec(
                                lineColor = ChartPrimary,
                                // FIXED: Using DynamicShaders
                                lineBackgroundShader = verticalGradient(
                                    arrayOf(ChartPrimary.copy(alpha = 0.4f), ChartPrimary.copy(alpha = 0.0f))
                                )
                            )
                        )
                    ),
                    model = uiState.weeklyActivityModel!!,
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                    marker = rememberMarker(),
                    modifier = Modifier.height(200.dp).fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

// --- Helper Components ---

@Composable
fun GraphCard(title: String, subtitle: String? = null, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(24.dp))
            content()
        }
    }
}

@Composable
fun KpiCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
fun AnimatedCircularGauge(percentage: Float, color: Color) {
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(100.dp)) {
            // Background Track
            drawArc(
                color = Color.LightGray.copy(alpha = 0.3f),
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                style = Stroke(width = 25f, cap = StrokeCap.Round)
            )
            // Foreground Progress
            drawArc(
                color = color,
                startAngle = 140f,
                sweepAngle = 260f * percentage,
                useCenter = false,
                style = Stroke(width = 25f, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(percentage * 100).toInt()}%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PriorityBar(label: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
        )
    }
}

// --- FIXED MARKER IMPLEMENTATION ---
@Composable
fun rememberMarker(): Marker {
    val labelBackgroundColor = MaterialTheme.colorScheme.surface

    // FIXED: ShapeComponent constructor usage
    val labelBackground = remember(labelBackgroundColor) {
        ShapeComponent(Shapes.pillShape, labelBackgroundColor.toArgb()).setShadow(
            radius = 4f, dy = 2f, applyElevationOverlay = true
        )
    }

    val label = textComponent(
        background = labelBackground,
        lineCount = 1,
        padding = dimensionsOf(8.dp, 4.dp),
        typeface = Typeface.MONOSPACE
    )

    val indicatorInner = shapeComponent(Shapes.pillShape, MaterialTheme.colorScheme.surface)
    val indicatorCenter = shapeComponent(Shapes.pillShape, Color.White)
    val indicatorOuter = shapeComponent(Shapes.pillShape, Color.White)

    val indicator = overlayingComponent(
        outer = indicatorOuter,
        inner = overlayingComponent(
            outer = indicatorCenter,
            inner = indicatorInner,
            innerPaddingAll = 5.dp
        ),
        innerPaddingAll = 10.dp
    )

    val guideline = lineComponent(
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
        2.dp,
        DashedShape(
            Shapes.pillShape,
            10.dp.value,
            5.dp.value
        )
    )

    return remember(label, indicator, guideline) {
        object : MarkerComponent(label, indicator, guideline) {
            init {
                indicatorSizeDp = 12f
            }
        }
    }
}

// Helper: Convert Compose Color to Int (Required for Vico)
fun Color.toArgb(): Int {
    return android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}
