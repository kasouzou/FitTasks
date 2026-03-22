package com.kasouzou.fittasks.data.repository

import androidx.compose.ui.graphics.Color
import com.kasouzou.fittasks.domain.model.TaskGroup
import com.kasouzou.fittasks.domain.model.TaskItem
import com.kasouzou.fittasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalTime

class FakeTaskRepository : TaskRepository {
    private val taskGroups = listOf(
        TaskGroup(
            startTime = LocalTime.of(13, 12),
            endTime = LocalTime.of(13, 47),
            tasks = listOf(
                TaskItem("昼ごはん", Color(0xFF4285F4)),
                TaskItem("歯磨き", Color(0xFFFBC02D)),
                TaskItem("トイレ", Color(0xFF8BC34A)),
                TaskItem("電気を消す", Color(0xFFE53935))
            )
        ),
        TaskGroup(
            startTime = LocalTime.of(14, 23),
            endTime = LocalTime.of(14, 43),
            tasks = listOf(
                TaskItem("", Color(0xFF4285F4)),
                TaskItem("", Color(0xFFFBC02D)),
                TaskItem("", Color(0xFF8BC34A)),
                TaskItem("", Color(0xFFE53935))
            )
        )
    )

    override fun getTaskGroups(): Flow<List<TaskGroup>> {
        return flowOf(taskGroups)
    }

    override suspend fun saveTaskGroup(taskGroup: TaskGroup) {
        // NotImplemented
    }

    override suspend fun deleteTaskGroup(taskGroup: TaskGroup) {
        // NotImplemented
    }
}
