package com.hesapgunlugu.app.domain.usecase.home

import com.hesapgunlugu.app.core.domain.usecase.home.GetGreetingMessageUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GetGreetingMessageUseCase
 */
class GetGreetingMessageUseCaseTest {
    private lateinit var useCase: GetGreetingMessageUseCase

    @Before
    fun setup() {
        useCase = GetGreetingMessageUseCase()
    }

    @Test
    fun `returns Günaydın for morning hours 5-11`() {
        assertEquals("Günaydın", useCase(5))
        assertEquals("Günaydın", useCase(8))
        assertEquals("Günaydın", useCase(11))
    }

    @Test
    fun `returns Tünaydın for afternoon hours 12-17`() {
        assertEquals("Tünaydın", useCase(12))
        assertEquals("Tünaydın", useCase(14))
        assertEquals("Tünaydın", useCase(17))
    }

    @Test
    fun `returns İyi Akşamlar for evening hours 18-22`() {
        assertEquals("İyi Akşamlar", useCase(18))
        assertEquals("İyi Akşamlar", useCase(20))
        assertEquals("İyi Akşamlar", useCase(22))
    }

    @Test
    fun `returns İyi Geceler for night hours 23-4`() {
        assertEquals("İyi Geceler", useCase(23))
        assertEquals("İyi Geceler", useCase(0))
        assertEquals("İyi Geceler", useCase(3))
        assertEquals("İyi Geceler", useCase(4))
    }

    @Test
    fun `edge case - hour 5 is morning`() {
        assertEquals("Günaydın", useCase(5))
    }

    @Test
    fun `edge case - hour 12 is afternoon`() {
        assertEquals("Tünaydın", useCase(12))
    }

    @Test
    fun `edge case - hour 18 is evening`() {
        assertEquals("İyi Akşamlar", useCase(18))
    }

    @Test
    fun `edge case - hour 23 is night`() {
        assertEquals("İyi Geceler", useCase(23))
    }
}
