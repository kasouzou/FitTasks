package com.kasouzou.fittasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kasouzou.fittasks.domain.usecase.GetLanguageUseCase
import com.kasouzou.fittasks.domain.usecase.GetThemeModeUseCase
import com.kasouzou.fittasks.domain.usecase.GetDynamicColorUseCase
import com.kasouzou.fittasks.domain.usecase.SaveDynamicColorUseCase
import com.kasouzou.fittasks.domain.usecase.IsFirstLaunchUseCase
import com.kasouzou.fittasks.domain.usecase.SaveLanguageUseCase
import com.kasouzou.fittasks.domain.usecase.SaveThemeModeUseCase
import com.kasouzou.fittasks.domain.usecase.SetFirstLaunchCompletedUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PreferenceUiState(
    val language: String? = null,
    val themeMode: Int = 0, // 0: System, 1: Light, 2: Dark
    val useDynamicColor: Boolean = false,
    val isFirstLaunch: Boolean? = null, // null means not yet loaded
    val isLoaded: Boolean = false
)

class PreferenceViewModel(
    getLanguageUseCase: GetLanguageUseCase,
    private val saveLanguageUseCase: SaveLanguageUseCase,
    getThemeModeUseCase: GetThemeModeUseCase,
    private val saveThemeModeUseCase: SaveThemeModeUseCase,
    getDynamicColorUseCase: GetDynamicColorUseCase,
    private val saveDynamicColorUseCase: SaveDynamicColorUseCase,
    isFirstLaunchUseCase: IsFirstLaunchUseCase,
    private val setFirstLaunchCompletedUseCase: SetFirstLaunchCompletedUseCase
) : ViewModel() {

    val uiState: StateFlow<PreferenceUiState> = combine(
        getLanguageUseCase(),
        getThemeModeUseCase(),
        getDynamicColorUseCase(),
        isFirstLaunchUseCase()
    ) { lang, theme, dynamicColor, firstLaunch ->
        PreferenceUiState(
            language = lang,
            themeMode = theme,
            useDynamicColor = dynamicColor,
            isFirstLaunch = firstLaunch,
            isLoaded = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PreferenceUiState()
    )

    fun setLanguage(code: String) {
        viewModelScope.launch {
            saveLanguageUseCase(code)
        }
    }

    fun setThemeMode(mode: Int) {
        viewModelScope.launch {
            saveThemeModeUseCase(mode)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            saveDynamicColorUseCase(enabled)
        }
    }

    fun completeFirstLaunch() {
        viewModelScope.launch {
            setFirstLaunchCompletedUseCase()
        }
    }
}

class PreferenceViewModelFactory(
    private val getLanguageUseCase: GetLanguageUseCase,
    private val saveLanguageUseCase: SaveLanguageUseCase,
    private val getThemeModeUseCase: GetThemeModeUseCase,
    private val saveThemeModeUseCase: SaveThemeModeUseCase,
    private val getDynamicColorUseCase: GetDynamicColorUseCase,
    private val saveDynamicColorUseCase: SaveDynamicColorUseCase,
    private val isFirstLaunchUseCase: IsFirstLaunchUseCase,
    private val setFirstLaunchCompletedUseCase: SetFirstLaunchCompletedUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreferenceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PreferenceViewModel(
                getLanguageUseCase,
                saveLanguageUseCase,
                getThemeModeUseCase,
                saveThemeModeUseCase,
                getDynamicColorUseCase,
                saveDynamicColorUseCase,
                isFirstLaunchUseCase,
                setFirstLaunchCompletedUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
