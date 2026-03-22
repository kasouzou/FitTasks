package com.kasouzou.fittasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kasouzou.fittasks.data.repository.FakeTaskRepository
import com.kasouzou.fittasks.domain.usecase.GetTaskGroupsUseCase

@Suppress("UNCHECKED_CAST")
class TaskListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            val repository = FakeTaskRepository()
            val useCase = GetTaskGroupsUseCase(repository)
            return TaskListViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
