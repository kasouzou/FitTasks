package com.kasouzou.fittasks.domain.usecase

import com.kasouzou.fittasks.domain.model.TaskGroup
import com.kasouzou.fittasks.domain.repository.TaskRepository

class SaveTaskGroupUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskGroup: TaskGroup) {
        repository.saveTaskGroup(taskGroup)
    }
}
