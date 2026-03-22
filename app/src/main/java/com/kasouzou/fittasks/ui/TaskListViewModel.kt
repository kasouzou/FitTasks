package com.kasouzou.fittasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasouzou.fittasks.domain.model.TaskGroup
import com.kasouzou.fittasks.domain.usecase.GetTaskGroupsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val getTaskGroupsUseCase: GetTaskGroupsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TaskListUiState>(TaskListUiState.Loading)
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        loadTaskGroups()
    }

    private fun loadTaskGroups() {
        viewModelScope.launch {
            getTaskGroupsUseCase().collect { groups ->
                _uiState.value = TaskListUiState.Success(groups)
            }
        }
    }
}

sealed interface TaskListUiState {
    object Loading : TaskListUiState
    data class Success(val groups: List<TaskGroup>) : TaskListUiState
    data class Error(val message: String) : TaskListUiState
}
