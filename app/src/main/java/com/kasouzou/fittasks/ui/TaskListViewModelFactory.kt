package com.kasouzou.fittasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kasouzou.fittasks.domain.repository.TaskRepository
import com.kasouzou.fittasks.domain.usecase.DeleteTaskGroupUseCase
import com.kasouzou.fittasks.domain.usecase.GetTaskGroupsUseCase

@Suppress("UNCHECKED_CAST")
class TaskListViewModelFactory(
    private val repository: TaskRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            val getUseCase = GetTaskGroupsUseCase(repository)
            val deleteUseCase = DeleteTaskGroupUseCase(repository)
            return TaskListViewModel(getUseCase, deleteUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
