package com.hesapgunlugu.app.core.domain.usecase.home

import com.hesapgunlugu.app.core.common.StringProvider
import java.util.Calendar
import javax.inject.Inject

/**
 * Use case for generating time-based greeting messages.
 *
 * Extracts greeting logic from ViewModel to improve testability
 * and support locale-based greetings in the future.
 */
class GetGreetingMessageUseCase
    @Inject
    constructor(
        private val stringProvider: StringProvider,
    ) {
        operator fun invoke(hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): String {
            return stringProvider.getGreeting(hour)
        }
    }
