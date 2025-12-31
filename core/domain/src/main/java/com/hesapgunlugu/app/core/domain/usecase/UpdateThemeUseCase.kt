package com.hesapgunlugu.app.core.domain.usecase

import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case for updating theme preference.
 * Encapsulates business logic for theme updates.
 */
class UpdateThemeUseCase
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) {
        suspend operator fun invoke(isDark: Boolean) {
            settingsRepository.toggleTheme(isDark)
        }
    }
