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
    val id: Long = 0,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val tasks: List<TaskItem>
) {
    /** グループの合計時間を分で取得 */
    val totalDurationMinutes: Long
        get() = Duration.between(startTime, endTime).toMinutes()

    /** グループの合計時間を秒で取得 */
    val totalDurationSeconds: Long
        get() = Duration.between(startTime, endTime).seconds

    /** 1タスクあたりの割り当て時間を秒で取得 */
    val durationPerTaskSeconds: Long
        get() = if (tasks.isEmpty()) 0 else totalDurationSeconds / tasks.size

    /** 1タスクあたりの割り当て時間を分で取得（表示用） */
    val durationPerTaskMinutes: Long
        get() = durationPerTaskSeconds / 60
}
