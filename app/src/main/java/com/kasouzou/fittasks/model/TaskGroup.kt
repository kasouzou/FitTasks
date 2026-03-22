package com.kasouzou.fittasks.model

import androidx.compose.ui.graphics.Color

/** 個別のタスクデータ */
data class TaskItem(val title: String = "", val color: Color)

/** 時間枠で区切られたタスクのグループ */
data class TaskGroup(val startTime: String, val tasks: List<TaskItem>)
