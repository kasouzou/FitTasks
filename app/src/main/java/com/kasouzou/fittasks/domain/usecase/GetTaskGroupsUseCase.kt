package com.kasouzou.fittasks.domain.usecase

import com.kasouzou.fittasks.domain.model.TaskGroup
import com.kasouzou.fittasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTaskGroupsUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<TaskGroup>> {
        return repository.getTaskGroups()
    }
}
