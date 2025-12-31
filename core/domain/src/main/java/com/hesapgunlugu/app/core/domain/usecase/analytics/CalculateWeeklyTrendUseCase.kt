package com.hesapgunlugu.app.core.domain.usecase.analytics

import javax.inject.Inject

/**
 * Haftalık harcama trendini hesaplar
 */
class CalculateWeeklyTrendUseCase
    @Inject
    constructor() {
        /**
         * @param weeklySpending Son 7 günün harcama listesi (kronolojik sırada)
         * @return Trend yönü: "up" (artış), "down" (azalış), "stable" (stabil)
         */
        operator fun invoke(weeklySpending: List<Double>): String {
            if (weeklySpending.size < 2) return "stable"

            // Son 3 günün ortalaması vs Önceki 4 günün ortalaması
            val recentAvg = weeklySpending.takeLast(3).average()
            val previousAvg = weeklySpending.take(4).average()

            return when {
                recentAvg > previousAvg * 1.1 -> "up" // %10'dan fazla artış
                recentAvg < previousAvg * 0.9 -> "down" // %10'dan fazla azalış
                else -> "stable"
            }
        }
    }
