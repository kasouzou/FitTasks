package com.kasouzou.fittasks.domain.usecase

import com.kasouzou.fittasks.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.Flow

class GetThemeModeUseCase(private val repository: PreferenceRepository) {
    operator fun invoke(): Flow<Int> = repository.getThemeMode()
}

class SaveThemeModeUseCase(private val repository: PreferenceRepository) {
    suspend operator fun invoke(mode: Int) = repository.saveThemeMode(mode)
}

class GetDynamicColorUseCase(private val repository: PreferenceRepository) {
    operator fun invoke(): Flow<Boolean> = repository.getDynamicColor()
}

class SaveDynamicColorUseCase(private val repository: PreferenceRepository) {
    suspend operator fun invoke(enabled: Boolean) = repository.saveDynamicColor(enabled)
}
