package com.kasouzou.fittasks.domain.usecase

import com.kasouzou.fittasks.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.Flow

class GetLanguageUseCase(private val repository: PreferenceRepository) {
    operator fun invoke(): Flow<String?> = repository.getLanguage()
}

class SaveLanguageUseCase(private val repository: PreferenceRepository) {
    suspend operator fun invoke(code: String) = repository.saveLanguage(code)
}

class IsFirstLaunchUseCase(private val repository: PreferenceRepository) {
    operator fun invoke(): Flow<Boolean> = repository.isFirstLaunch()
}

class SetFirstLaunchCompletedUseCase(private val repository: PreferenceRepository) {
    suspend operator fun invoke() = repository.setFirstLaunchCompleted()
}
