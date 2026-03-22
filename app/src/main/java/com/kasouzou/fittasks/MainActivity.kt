package com.kasouzou.fittasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kasouzou.fittasks.data.repository.FakeTaskRepository
import com.kasouzou.fittasks.domain.model.TaskGroup
import com.kasouzou.fittasks.domain.usecase.SaveTaskGroupUseCase
import com.kasouzou.fittasks.ui.TaskEditScreen
import com.kasouzou.fittasks.ui.TaskListScreen
import com.kasouzou.fittasks.ui.TaskListViewModel
import com.kasouzou.fittasks.ui.TaskListViewModelFactory
import com.kasouzou.fittasks.ui.TimerScreen
import com.kasouzou.fittasks.ui.theme.FitTasksTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitTasksTheme {
                val repository = remember { FakeTaskRepository() } // Sharing repository for fake persistence
                val saveTaskGroupUseCase = remember { SaveTaskGroupUseCase(repository) }
                
                var currentScreen by remember { mutableStateOf<Screen>(Screen.TaskList) }

                when (val screen = currentScreen) {
                    is Screen.TaskList -> {
                        val taskListViewModel: TaskListViewModel = viewModel(
                            factory = TaskListViewModelFactory(repository)
                        )
                        TaskListScreen(
                            onAddTask = { currentScreen = Screen.TaskEdit },
                            onStartTimer = { group -> currentScreen = Screen.Timer(group) },
                            viewModel = taskListViewModel
                        )
                    }
                    is Screen.TaskEdit -> {
                        TaskEditScreen(
                            onBack = { currentScreen = Screen.TaskList },
                            onSave = { group ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    saveTaskGroupUseCase(group)
                                    currentScreen = Screen.TaskList
                                }
                            }
                        )
                    }
                    is Screen.Timer -> {
                        TimerScreen(
                            taskGroup = screen.taskGroup,
                            onBack = { currentScreen = Screen.TaskList }
                        )
                    }
                }
            }
        }
    }
}


sealed interface Screen {
    object TaskList : Screen
    object TaskEdit : Screen
    data class Timer(val taskGroup: TaskGroup) : Screen
}