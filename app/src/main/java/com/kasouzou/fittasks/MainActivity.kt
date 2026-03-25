package com.kasouzou.fittasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import com.google.android.gms.ads.MobileAds
import com.kasouzou.fittasks.data.local.FitTasksDatabase
import com.kasouzou.fittasks.data.repository.DataStorePreferenceRepository
import com.kasouzou.fittasks.data.repository.RoomTaskRepository
import com.kasouzou.fittasks.domain.model.TaskGroup
import com.kasouzou.fittasks.domain.usecase.*
import com.kasouzou.fittasks.ui.*
import com.kasouzou.fittasks.ui.theme.FitTasksTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    object LanguageSetup : Route
    @Serializable
    object TaskList : Route
    @Serializable
    data class TaskEdit(val taskGroupId: Long? = null) : Route
    @Serializable
    data class Timer(val taskGroupId: Long) : Route
    @Serializable
    object Settings : Route
}

class MainActivity : AppCompatActivity() {
    
    private lateinit var database: FitTasksDatabase
    private lateinit var taskRepository: RoomTaskRepository
    private lateinit var prefRepository: DataStorePreferenceRepository
    private lateinit var saveTaskGroupUseCase: SaveTaskGroupUseCase
    private lateinit var getTaskGroupsUseCase: GetTaskGroupsUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this)

        database = Room.databaseBuilder(applicationContext, FitTasksDatabase::class.java, "fittasks-db")
            .build()
        taskRepository = RoomTaskRepository(database.taskGroupDao())
        prefRepository = DataStorePreferenceRepository(applicationContext)
        saveTaskGroupUseCase = SaveTaskGroupUseCase(taskRepository)
        getTaskGroupsUseCase = GetTaskGroupsUseCase(taskRepository)
        
        setContent {
            val prefViewModel: PreferenceViewModel = viewModel(
                factory = PreferenceViewModelFactory(
                    GetLanguageUseCase(prefRepository),
                    SaveLanguageUseCase(prefRepository),
                    GetThemeModeUseCase(prefRepository),
                    SaveThemeModeUseCase(prefRepository),
                    GetDynamicColorUseCase(prefRepository),
                    SaveDynamicColorUseCase(prefRepository),
                    IsFirstLaunchUseCase(prefRepository),
                    SetFirstLaunchCompletedUseCase(prefRepository)
                )
            )

            val prefState by prefViewModel.uiState.collectAsState()

            val darkTheme = when (prefState.themeMode) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            FitTasksTheme(darkTheme = darkTheme, dynamicColor = prefState.useDynamicColor) {
                // 言語設定の反映
                LaunchedEffect(prefState.language) {
                    prefState.language?.let { code ->
                        val currentLocales = AppCompatDelegate.getApplicationLocales()
                        if (currentLocales.toLanguageTags() != code) {
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(code)
                            AppCompatDelegate.setApplicationLocales(appLocale)
                        }
                    }
                }

                if (!prefState.isLoaded) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = if (prefState.isFirstLaunch == true) Route.LanguageSetup else Route.TaskList
                    ) {
                        composable<Route.LanguageSetup> {
                            LanguageSelectionScreen(
                                onLanguageSelected = { code ->
                                    prefViewModel.setLanguage(code)
                                    prefViewModel.completeFirstLaunch()
                                    navController.navigate(Route.TaskList) {
                                        popUpTo(Route.LanguageSetup) { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable<Route.TaskList> {
                            val taskListViewModel: TaskListViewModel = viewModel(
                                factory = TaskListViewModelFactory(taskRepository)
                            )
                            TaskListScreen(
                                onAddTask = {
                                    navController.navigate(Route.TaskEdit())
                                },
                                onEditTask = { group ->
                                    navController.navigate(Route.TaskEdit(group.id))
                                },
                                onStartTimer = { group ->
                                    navController.navigate(Route.Timer(group.id))
                                },
                                onDeleteTask = { group ->
                                    taskListViewModel.deleteTaskGroup(group)
                                },
                                onSettingsClick = {
                                    navController.navigate(Route.Settings)
                                },
                                viewModel = taskListViewModel
                            )
                        }
                        
                        composable<Route.TaskEdit> { backStackEntry ->
                            val route: Route.TaskEdit = backStackEntry.toRoute()
                            val groups by getTaskGroupsUseCase().collectAsState(initial = emptyList())
                            val taskGroup = groups.find { it.id == route.taskGroupId }
                            
                            TaskEditScreen(
                                taskGroup = taskGroup,
                                onBack = { navController.popBackStack() },
                                onSave = { group ->
                                    CoroutineScope(Dispatchers.Main).launch {
                                        saveTaskGroupUseCase(group)
                                        navController.popBackStack()
                                    }
                                }
                            )
                        }
                        
                        composable<Route.Timer> { backStackEntry ->
                            val route: Route.Timer = backStackEntry.toRoute()
                            val groups by getTaskGroupsUseCase().collectAsState(initial = emptyList())
                            val taskGroup = groups.find { it.id == route.taskGroupId }
                            
                            if (taskGroup != null) {
                                TimerScreen(
                                    taskGroup = taskGroup,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                        
                        composable<Route.Settings> {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                onLanguageSelected = { code ->
                                    prefViewModel.setLanguage(code)
                                },
                                currentLanguageCode = prefState.language,
                                onThemeSelected = { mode ->
                                    prefViewModel.setThemeMode(mode)
                                },
                                currentThemeMode = prefState.themeMode,
                                onDynamicColorChanged = { enabled ->
                                    prefViewModel.setDynamicColor(enabled)
                                },
                                useDynamicColor = prefState.useDynamicColor
                            )
                        }
                    }
                }
            }
        }
    }
}
