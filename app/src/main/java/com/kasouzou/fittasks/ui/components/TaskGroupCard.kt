package com.kasouzou.fittasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kasouzou.fittasks.model.TaskGroup
import java.time.format.DateTimeFormatter

@Composable
fun TaskGroupCard(group: TaskGroup) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // 外枠の青いボーダー
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 1.dp,
                color = Color(0xFF64B5F6), // 薄い青
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 左側の時間表示エリア
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(80.dp)
            ) {
                // 開始時間
                TimeBadge(time = group.startTime.format(timeFormatter))
                
                // タスクあたりの割り当て時間を表示
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${group.durationPerTaskMinutes} min",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 終了時間
                TimeBadge(time = group.endTime.format(timeFormatter))
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 右側のタスクチップ一覧
            Column(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                group.tasks.forEach { task ->
                    TaskPill(title = task.title, color = task.color)
                }
            }
        }
    }
}

@Composable
fun TimeBadge(time: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = time,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
fun TaskPill(title: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(color, shape = RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
