package com.kasouzou.fittasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.kasouzou.fittasks.data.local.FitTasksDatabase
import com.kasouzou.fittasks.data.repository.RoomTaskRepository
import com.kasouzou.fittasks.domain.model.TaskGroup
import com.kasouzou.fittasks.domain.usecase.SaveTaskGroupUseCase
import com.kasouzou.fittasks.ui.*
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
                val context = LocalContext.current
                val database = remember {
                    Room.databaseBuilder(
                        context,
                        FitTasksDatabase::class.java, "fittasks-db"
                    ).build()
                }
                val repository = remember { RoomTaskRepository(database.taskGroupDao()) }
                val saveTaskGroupUseCase = remember { SaveTaskGroupUseCase(repository) }
                
                var currentScreen by remember { mutableStateOf<Screen>(Screen.TaskList) }

                when (val screen = currentScreen) {
                    is Screen.TaskList -> {
                        val taskListViewModel: TaskListViewModel = viewModel(
                            factory = TaskListViewModelFactory(repository)
                        )
                        TaskListScreen(
                            onAddTask = { currentScreen = Screen.TaskEdit(null) },
                            onEditTask = { group -> currentScreen = Screen.TaskEdit(group) },
                            onStartTimer = { group -> currentScreen = Screen.Timer(group) },
                            onDeleteTask = { group -> taskListViewModel.deleteTaskGroup(group) },
                            viewModel = taskListViewModel
                        )
                    }
                    is Screen.TaskEdit -> {
                        TaskEditScreen(
                            taskGroup = screen.taskGroup,
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
    data class TaskEdit(val taskGroup: TaskGroup?) : Screen
    data class Timer(val taskGroup: TaskGroup) : Screen
}