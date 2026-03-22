package com.kasouzou.fittasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasouzou.fittasks.domain.model.TaskGroup
import com.kasouzou.fittasks.domain.model.TaskItem
import com.kasouzou.fittasks.ui.components.FooterBannerAd
import com.kasouzou.fittasks.ui.theme.*
import java.time.LocalTime
import java.time.Duration
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    taskGroup: TaskGroup? = null,
    onBack: () -> Unit,
    onSave: (TaskGroup) -> Unit
) {
    var startTime by remember { mutableStateOf(taskGroup?.startTime ?: LocalTime.of(9, 0)) }
    var endTime by remember { mutableStateOf(taskGroup?.endTime ?: LocalTime.of(10, 0)) }
    var taskName by remember { mutableStateOf("") }
    
    val pastelColors = listOf(
        PastelPink, PastelBlue, PastelGreen, PastelYellow, PastelPurple, PastelOrange,
        SoftPink, SoftBlue
    )
    var selectedColor by remember { mutableStateOf(pastelColors[0]) }

    val tasks = remember { 
        mutableStateListOf<TaskItem>().apply {
            taskGroup?.let { addAll(it.tasks) }
        }
    }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskGroup == null) "タスク追加 ✨" else "タスク編集 ✏️", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            FooterBannerAd(
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Time Input Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("時間設定", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Start Time
                        OutlinedCard(
                            onClick = { showStartTimePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("開始時間", style = MaterialTheme.typography.labelSmall)
                                Text(startTime.toString(), style = MaterialTheme.typography.titleLarge)
                            }
                        }
                        
                        // End Time
                        OutlinedCard(
                            onClick = { showEndTimePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("終了時間", style = MaterialTheme.typography.labelSmall)
                                Text(endTime.toString(), style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Duration Input
                        var durationInput by remember(startTime, endTime) { 
                            mutableStateOf(Duration.between(startTime, endTime).toMinutes().toString()) 
                        }
                        
                        OutlinedTextField(
                            value = durationInput,
                            onValueChange = { 
                                durationInput = it
                                it.toLongOrNull()?.let { minutes ->
                                    if (minutes >= 0) {
                                        endTime = startTime.plusMinutes(minutes)
                                    }
                                }
                            },
                            label = { Text("所要時間 (分)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            ),
                            singleLine = true
                        )
                        
                        val duration = Duration.between(startTime, endTime)
                        val durationMinutes = duration.toMinutes()
                        
                        Text(
                            text = "合計: ${durationMinutes}分",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Task list input
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("タスクを追加", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = taskName,
                            onValueChange = { taskName = it },
                            label = { Text("タスク名") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (taskName.isNotBlank()) {
                                    tasks.add(TaskItem(taskName, selectedColor))
                                    taskName = ""
                                }
                            },
                            enabled = taskName.isNotBlank(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("追加")
                        }
                    }
                    
                    // Color selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pastelColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { selectedColor = color }
                                    .padding(2.dp)
                            ) {
                                if (selectedColor == color) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.2f))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Text("タスク一覧 (${tasks.size})", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = task.color.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.medium,
                        border = androidx.compose.foundation.BorderStroke(1.dp, task.color)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(task.color)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = task.title,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            IconButton(onClick = { tasks.remove(task) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            if (tasks.isNotEmpty()) {
                val durationPerTask = if (tasks.isEmpty()) 0 else Duration.between(startTime, endTime).toMinutes() / tasks.size
                Text(
                    text = "1タスクあたり: 約${durationPerTask}分",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    onSave(TaskGroup(
                        id = taskGroup?.id ?: 0,
                        startTime = startTime,
                        endTime = endTime,
                        tasks = tasks.toList()
                    ))
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = tasks.isNotEmpty() && Duration.between(startTime, endTime).toMinutes() > 0,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("保存", style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    if (showStartTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            initialTime = startTime,
            onTimeSelected = { 
                startTime = it
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            initialTime = endTime,
            onTimeSelected = { 
                endTime = it
                showEndTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("キャンセル")
            }
        },
        title = { Text("時間を選択") },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(state = timePickerState)
            }
        }
    )
}
