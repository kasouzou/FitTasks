package com.kasouzou.fittasks.domain.repository

import com.kasouzou.fittasks.domain.model.TaskGroup
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTaskGroups(): Flow<List<TaskGroup>>
    suspend fun saveTaskGroup(taskGroup: TaskGroup)
    suspend fun deleteTaskGroup(taskGroup: TaskGroup)
}
