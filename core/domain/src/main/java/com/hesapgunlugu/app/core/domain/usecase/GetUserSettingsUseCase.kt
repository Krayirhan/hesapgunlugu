package com.hesapgunlugu.app.core.domain.usecase

import com.hesapgunlugu.app.core.domain.model.UserSettings
import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving user settings.
 * Encapsulates business logic for settings retrieval.
 */
class GetUserSettingsUseCase
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) {
        operator fun invoke(): Flow<UserSettings> = settingsRepository.settingsFlow
    }
