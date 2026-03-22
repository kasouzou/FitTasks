package com.kasouzou.fittasks.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kasouzou.fittasks.domain.model.TaskGroup
import com.kasouzou.fittasks.domain.model.TaskItem
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    taskGroup: TaskGroup,
    onBack: () -> Unit
) {
    val viewModel = remember { TimerViewModel(taskGroup) }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Task Group Title / Time range
            Text(
                text = "${taskGroup.startTime} - ${taskGroup.endTime}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Main Timer Display
            val currentTask = uiState.taskGroup.tasks.getOrNull(uiState.currentTaskIndex)
            
            if (uiState.isFinished) {
                FinishedDisplay(onBack)
            } else if (currentTask != null) {
                TimerDisplay(
                    task = currentTask,
                    remainingSeconds = uiState.remainingSeconds,
                    isRunning = uiState.isRunning,
                    onToggleTimer = {
                        if (uiState.isRunning) viewModel.pauseTimer() else viewModel.startTimer()
                    },
                    onSkip = { viewModel.skipTask() }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Task List showing progress
            Text(
                text = "Next Tasks",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.Start),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(uiState.taskGroup.tasks) { index, task ->
                    val isCurrent = index == uiState.currentTaskIndex
                    val isDone = index < uiState.currentTaskIndex
                    
                    MiniTaskItem(
                        task = task,
                        isCurrent = isCurrent,
                        isDone = isDone
                    )
                }
            }
        }
    }
}

@Composable
fun TimerDisplay(
    task: TaskItem,
    remainingSeconds: Long,
    isRunning: Boolean,
    onToggleTimer: () -> Unit,
    onSkip: () -> Unit
) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeStr = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Current Task Name
        Surface(
            color = task.color,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            val contentColor = if (task.color.luminance() > 0.5f) Color.Black else Color.White
            Text(
                text = task.title,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }

        // Timer Circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(task.color.copy(alpha = 0.1f))
        ) {
            Text(
                text = timeStr,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 64.sp
                ),
                color = task.color
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Toggle Button
            FloatingActionButton(
                onClick = onToggleTimer,
                containerColor = task.color,
                contentColor = if (task.color.luminance() > 0.5f) Color.Black else Color.White,
                shape = CircleShape,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Play",
                    modifier = Modifier.size(36.dp)
                )
            }

            // Skip Button
            IconButton(
                onClick = onSkip,
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(Icons.Default.SkipNext, contentDescription = "Skip")
            }
        }
    }
}

@Composable
fun FinishedDisplay(onBack: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 40.dp)
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = "Finished",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(120.dp)
        )
        Text(
            "Great Job!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "All tasks completed.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = onBack) {
            Text("Back to List")
        }
    }
}

@Composable
fun MiniTaskItem(
    task: TaskItem,
    isCurrent: Boolean,
    isDone: Boolean
) {
    val backgroundColor = if (isCurrent) task.color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
    val borderColor = if (isCurrent) task.color else Color.Transparent
    
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        border = if (isCurrent) androidx.compose.foundation.BorderStroke(2.dp, borderColor) else null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isDone) Color.Gray else task.color)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isDone) Color.Gray else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            )
            
            if (isDone) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
