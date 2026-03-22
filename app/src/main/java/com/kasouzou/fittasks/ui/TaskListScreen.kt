package com.kasouzou.fittasks.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kasouzou.fittasks.model.TaskGroup
import com.kasouzou.fittasks.model.TaskItem
import com.kasouzou.fittasks.ui.components.TaskGroupCard
import java.time.LocalTime

@Composable
fun TaskListScreen() {
    // サンプルデータ
    val sampleData = listOf(
        TaskGroup(
            startTime = LocalTime.of(13, 12),
            endTime = LocalTime.of(13, 47), // 35分間 / 4タスク = 8.75分 (切り捨てで8分)
            tasks = listOf(
                TaskItem("昼ごはん", Color(0xFF4285F4)),
                TaskItem("歯磨き", Color(0xFFFBC02D)),
                TaskItem("トイレ", Color(0xFF8BC34A)),
                TaskItem("電気を消す", Color(0xFFE53935))
            )
        ),
        // スクショの下の方にある空のラベルパターン
        TaskGroup(
            startTime = LocalTime.of(14, 23),
            endTime = LocalTime.of(14, 43), // 20分間 / 4タスク = 5分
            tasks = listOf(
                TaskItem("", Color(0xFF4285F4)),
                TaskItem("", Color(0xFFFBC02D)),
                TaskItem("", Color(0xFF8BC34A)),
                TaskItem("", Color(0xFFE53935))
            )
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            items(sampleData) { group ->
                TaskGroupCard(group = group)
            }
        }
    }
}
