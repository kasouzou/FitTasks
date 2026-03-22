package com.kasouzou.fittasks.domain.model

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime

class TaskGroupTest {

    @Test
    fun totalDurationMinutes_isCorrect() {
        val group = TaskGroup(
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(10, 30),
            tasks = emptyList()
        )
        assertEquals(30L, group.totalDurationMinutes)
    }

    @Test
    fun durationPerTaskMinutes_isCorrect() {
        val tasks = listOf(
            TaskItem("Task 1", Color.Red),
            TaskItem("Task 2", Color.Blue),
            TaskItem("Task 3", Color.Green),
            TaskItem("Task 4", Color.Yellow)
        )
        val group = TaskGroup(
            startTime = LocalTime.of(13, 0),
            endTime = LocalTime.of(13, 40),
            tasks = tasks
        )
        // 40 minutes / 4 tasks = 10 minutes
        assertEquals(10L, group.durationPerTaskMinutes)
    }

    @Test
    fun durationPerTaskMinutes_handlesTruncation() {
        val tasks = listOf(
            TaskItem("Task 1", Color.Red),
            TaskItem("Task 2", Color.Blue),
            TaskItem("Task 3", Color.Green)
        )
        val group = TaskGroup(
            startTime = LocalTime.of(13, 0),
            endTime = LocalTime.of(13, 10),
            tasks = tasks
        )
        // 10 minutes / 3 tasks = 3.33... -> 3 minutes
        assertEquals(3L, group.durationPerTaskMinutes)
    }

    @Test
    fun durationPerTaskMinutes_emptyTasks() {
        val group = TaskGroup(
            startTime = LocalTime.of(13, 0),
            endTime = LocalTime.of(13, 10),
            tasks = emptyList()
        )
        assertEquals(0L, group.durationPerTaskMinutes)
    }
}
