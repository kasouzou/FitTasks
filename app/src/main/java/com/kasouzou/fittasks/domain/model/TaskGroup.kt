package com.kasouzou.fittasks.domain.model

import androidx.compose.ui.graphics.Color
import java.time.LocalTime
import java.time.Duration

/** 個別のタスクデータ */
data class TaskItem(
    val title: String = "",
    val color: Color
)

/** 時間枠で区切られたタスクのグループ */
data class TaskGroup(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val tasks: List<TaskItem>
) {
    /** グループの合計時間を分で取得 */
    val totalDurationMinutes: Long
        get() = Duration.between(startTime, endTime).toMinutes()

    /** 1タスクあたりの割り当て時間を分で取得（端数は切り捨て） */
    val durationPerTaskMinutes: Long
        get() = if (tasks.isEmpty()) 0 else totalDurationMinutes / tasks.size
}
