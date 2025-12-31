package com.hesapgunlugu.app.domain.usecase.home

import com.hesapgunlugu.app.core.domain.usecase.home.BudgetSeverity
import com.hesapgunlugu.app.core.domain.usecase.home.CheckBudgetWarningUseCase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for CheckBudgetWarningUseCase
 */
class CheckBudgetWarningUseCaseTest {
    private lateinit var useCase: CheckBudgetWarningUseCase

    @Before
    fun setup() {
        useCase = CheckBudgetWarningUseCase()
    }

    // ==================== BUDGET WARNING TESTS ====================

    @Test
    fun `returns null when budget limit is zero`() {
        val result = useCase(monthlyLimit = 0.0, currentExpense = 5000.0)
        assertNull(result)
    }

    @Test
    fun `returns null when budget limit is negative`() {
        val result = useCase(monthlyLimit = -1000.0, currentExpense = 500.0)
        assertNull(result)
    }

    @Test
    fun `returns null when expense is below 80 percent`() {
        val result = useCase(monthlyLimit = 10000.0, currentExpense = 7000.0)
        assertNull(result)
    }

    @Test
    fun `returns WARNING when expense is between 80-89 percent`() {
        val result = useCase(monthlyLimit = 10000.0, currentExpense = 8500.0)

        assertNotNull(result)
        assertEquals(BudgetSeverity.WARNING, result?.severity)
        assertTrue(result?.title?.contains("Uyarı") == true)
    }

    @Test
    fun `returns CRITICAL when expense is between 90-99 percent`() {
        val result = useCase(monthlyLimit = 10000.0, currentExpense = 9500.0)

        assertNotNull(result)
        assertEquals(BudgetSeverity.CRITICAL, result?.severity)
        assertTrue(result?.title?.contains("Kritik") == true)
    }

    @Test
    fun `returns EXCEEDED when expense equals limit`() {
        val result = useCase(monthlyLimit = 10000.0, currentExpense = 10000.0)

        assertNotNull(result)
        assertEquals(BudgetSeverity.EXCEEDED, result?.severity)
        assertTrue(result?.title?.contains("Aşıldı") == true)
    }

    @Test
    fun `returns EXCEEDED when expense exceeds limit`() {
        val result = useCase(monthlyLimit = 10000.0, currentExpense = 12000.0)

        assertNotNull(result)
        assertEquals(BudgetSeverity.EXCEEDED, result?.severity)
    }

    @Test
    fun `EXCEEDED message includes limit amount`() {
        val result = useCase(monthlyLimit = 10000.0, currentExpense = 12000.0)

        assertNotNull(result)
        assertTrue(result?.message?.contains("10000") == true)
    }

    // ==================== EDGE CASES ====================

    @Test
    fun `exactly 80 percent triggers WARNING`() {
        val result = useCase(monthlyLimit = 10000.0, currentExpense = 8000.0)

        assertNotNull(result)
        assertEquals(BudgetSeverity.WARNING, result?.severity)
    }

    @Test
    fun `exactly 90 percent triggers CRITICAL`() {
        val result = useCase(monthlyLimit = 10000.0, currentExpense = 9000.0)

        assertNotNull(result)
        assertEquals(BudgetSeverity.CRITICAL, result?.severity)
    }

    @Test
    fun `79 percent does not trigger warning`() {
        val result = useCase(monthlyLimit = 10000.0, currentExpense = 7999.0)
        assertNull(result)
    }

    // ==================== REMAINING BUDGET TESTS ====================

    @Test
    fun `getRemainingBudget returns correct amount`() {
        val result =
            useCase.getRemainingBudget(
                monthlyLimit = 10000.0,
                currentExpense = 3000.0,
            )

        assertEquals(7000.0, result.amount, 0.01)
    }

    @Test
    fun `getRemainingBudget returns zero when exceeded`() {
        val result =
            useCase.getRemainingBudget(
                monthlyLimit = 10000.0,
                currentExpense = 12000.0,
            )

        assertEquals(0.0, result.amount, 0.01)
    }

    @Test
    fun `getRemainingBudget returns correct percent used`() {
        val result =
            useCase.getRemainingBudget(
                monthlyLimit = 10000.0,
                currentExpense = 5000.0,
            )

        assertEquals(50f, result.percentUsed, 0.01f)
    }

    @Test
    fun `getRemainingBudget caps percent at 100`() {
        val result =
            useCase.getRemainingBudget(
                monthlyLimit = 10000.0,
                currentExpense = 15000.0,
            )

        assertEquals(100f, result.percentUsed, 0.01f)
    }

    @Test
    fun `getRemainingBudget handles zero limit`() {
        val result =
            useCase.getRemainingBudget(
                monthlyLimit = 0.0,
                currentExpense = 5000.0,
            )

        assertEquals(0f, result.percentUsed, 0.01f)
    }

    @Test
    fun `getRemainingBudget returns 0 percent when no expense`() {
        val result =
            useCase.getRemainingBudget(
                monthlyLimit = 10000.0,
                currentExpense = 0.0,
            )

        assertEquals(0f, result.percentUsed, 0.01f)
        assertEquals(10000.0, result.amount, 0.01)
    }
}
