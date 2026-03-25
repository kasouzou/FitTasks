package com.kasouzou.fittasks.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    fun getLanguage(): Flow<String?>
    suspend fun saveLanguage(code: String)
    fun getThemeMode(): Flow<Int>
    suspend fun saveThemeMode(mode: Int)
    fun getDynamicColor(): Flow<Boolean>
    suspend fun saveDynamicColor(enabled: Boolean)
    fun isFirstLaunch(): Flow<Boolean>
    suspend fun setFirstLaunchCompleted()
}
