package com.hesapgunlugu.app.core.domain.usecase

import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case for updating monthly spending limit.
 * Encapsulates business logic for monthly limit updates.
 */
class UpdateMonthlyLimitUseCase
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) {
        suspend operator fun invoke(limit: Double) {
            require(limit >= 0) { "Monthly limit cannot be negative" }
            settingsRepository.updateMonthlyLimit(limit)
        }
    }
