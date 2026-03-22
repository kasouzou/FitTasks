package com.kasouzou.fittasks.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kasouzou.fittasks.domain.model.TaskGroup
import com.kasouzou.fittasks.domain.model.TaskItem
import com.kasouzou.fittasks.ui.theme.PastelBlue
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    onBack: () -> Unit,
    onSave: (TaskGroup) -> Unit
) {
    var startTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var endTime by remember { mutableStateOf(LocalTime.of(10, 0)) }
    var taskName by remember { mutableStateOf("") }
    val tasks = remember { mutableStateListOf<TaskItem>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("タスク追加 ✨") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
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
            // Time Input (Simplified for now)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = startTime.toString(),
                    onValueChange = { }, // TODO: Time picker
                    label = { Text("開始時間") },
                    modifier = Modifier.weight(1f),
                    readOnly = true
                )
                OutlinedTextField(
                    value = endTime.toString(),
                    onValueChange = { }, // TODO: Time picker
                    label = { Text("終了時間") },
                    modifier = Modifier.weight(1f),
                    readOnly = true
                )
            }

            // Task list input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("タスク名") },
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = {
                    if (taskName.isNotBlank()) {
                        tasks.add(TaskItem(taskName, PastelBlue))
                        taskName = ""
                    }
                }) {
                    Text("追加")
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = task.color,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = task.title,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    onSave(TaskGroup(startTime, endTime, tasks.toList()))
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = tasks.isNotEmpty()
            ) {
                Text("保存")
            }
        }
    }
}
