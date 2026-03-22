package com.kasouzou.fittasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasouzou.fittasks.domain.model.TaskGroup
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimerUiState(
    val taskGroup: TaskGroup,
    val currentTaskIndex: Int = 0,
    val remainingSeconds: Long = 0,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false
)

class TimerViewModel(private val taskGroup: TaskGroup) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TimerUiState(
            taskGroup = taskGroup,
            remainingSeconds = taskGroup.durationPerTaskSeconds
        )
    )
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    fun startTimer() {
        if (_uiState.value.isFinished) return
        
        _uiState.update { it.copy(isRunning = true) }
        
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isRunning) {
                delay(1000L)
                tick()
            }
        }
    }

    fun pauseTimer() {
        _uiState.update { it.copy(isRunning = false) }
        timerJob?.cancel()
    }

    fun stopTimer() {
        pauseTimer()
    }

    private fun tick() {
        _uiState.update { state ->
            if (state.remainingSeconds > 0) {
                state.copy(remainingSeconds = state.remainingSeconds - 1)
            } else {
                // Time up for current task, move to next
                val nextIndex = state.currentTaskIndex + 1
                if (nextIndex < taskGroup.tasks.size) {
                    state.copy(
                        currentTaskIndex = nextIndex,
                        remainingSeconds = taskGroup.durationPerTaskSeconds
                    )
                } else {
                    // All tasks finished
                    state.copy(
                        isRunning = false,
                        isFinished = true,
                        remainingSeconds = 0
                    )
                }
            }
        }
        
        if (_uiState.value.isFinished) {
            timerJob?.cancel()
        }
    }

    fun skipTask() {
        _uiState.update { state ->
            val nextIndex = state.currentTaskIndex + 1
            if (nextIndex < taskGroup.tasks.size) {
                state.copy(
                    currentTaskIndex = nextIndex,
                    remainingSeconds = taskGroup.durationPerTaskSeconds
                )
            } else {
                state.copy(
                    isRunning = false,
                    isFinished = true,
                    remainingSeconds = 0
                )
            }
        }
    }
}
