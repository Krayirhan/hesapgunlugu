package com.hesapgunlugu.app.core.domain.usecase

import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case for updating category budget.
 * Encapsulates business logic for budget updates.
 */
class UpdateCategoryBudgetUseCase
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) {
        suspend operator fun invoke(
            category: String,
            budget: Double,
        ) {
            require(budget >= 0) { "Budget cannot be negative" }
            settingsRepository.updateCategoryBudget(category, budget)
        }
    }
