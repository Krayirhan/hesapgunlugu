package com.hesapgunlugu.app.core.data.integration

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hesapgunlugu.app.core.data.local.AppDatabase
import com.hesapgunlugu.app.core.data.local.dao.CategoryDao
import com.hesapgunlugu.app.core.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests for Category database operations
 */
@RunWith(AndroidJUnit4::class)
class CategoryDaoIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var categoryDao: CategoryDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java,
            ).allowMainThreadQueries().build()

        categoryDao = database.categoryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertCategory_andRetrieve_success() =
        runBlocking {
            // Given
            val category =
                CategoryEntity(
                    id = 1,
                    name = "Test Category",
                    icon = "💰",
                    color = "#FF5722",
                    type = "EXPENSE",
                )

            // When
            categoryDao.insert(category)
            val retrieved = categoryDao.getById(1).first()

            // Then
            assertNotNull(retrieved)
            assertEquals("Test Category", retrieved.name)
            assertEquals("💰", retrieved.icon)
        }

    @Test
    fun getAllCategories_returnsAllInserted() =
        runBlocking {
            // Given
            val categories =
                listOf(
                    CategoryEntity(1, "Food", "🍔", "#FF5722", "EXPENSE"),
                    CategoryEntity(2, "Transport", "🚗", "#2196F3", "EXPENSE"),
                    CategoryEntity(3, "Salary", "💰", "#4CAF50", "INCOME"),
                )

            // When
            categories.forEach { categoryDao.insert(it) }
            val allCategories = categoryDao.getAll().first()

            // Then
            assertEquals(3, allCategories.size)
            assertTrue(allCategories.any { it.name == "Food" })
            assertTrue(allCategories.any { it.name == "Transport" })
            assertTrue(allCategories.any { it.name == "Salary" })
        }

    @Test
    fun getCategoriesByType_filtersCorrectly() =
        runBlocking {
            // Given
            categoryDao.insert(CategoryEntity(1, "Food", "🍔", "#FF5722", "EXPENSE"))
            categoryDao.insert(CategoryEntity(2, "Transport", "🚗", "#2196F3", "EXPENSE"))
            categoryDao.insert(CategoryEntity(3, "Salary", "💰", "#4CAF50", "INCOME"))
            categoryDao.insert(CategoryEntity(4, "Bonus", "🎁", "#4CAF50", "INCOME"))

            // When
            val expenseCategories = categoryDao.getByType("EXPENSE").first()
            val incomeCategories = categoryDao.getByType("INCOME").first()

            // Then
            assertEquals(2, expenseCategories.size)
            assertEquals(2, incomeCategories.size)
            assertTrue(expenseCategories.all { it.type == "EXPENSE" })
            assertTrue(incomeCategories.all { it.type == "INCOME" })
        }

    @Test
    fun updateCategory_updatesCorrectly() =
        runBlocking {
            // Given
            val original = CategoryEntity(1, "Food", "🍔", "#FF5722", "EXPENSE")
            categoryDao.insert(original)

            // When
            val updated = original.copy(name = "Dining", icon = "🍽️")
            categoryDao.update(updated)
            val retrieved = categoryDao.getById(1).first()

            // Then
            assertEquals("Dining", retrieved.name)
            assertEquals("🍽️", retrieved.icon)
        }

    @Test
    fun deleteCategory_removesFromDatabase() =
        runBlocking {
            // Given
            val category = CategoryEntity(1, "Food", "🍔", "#FF5722", "EXPENSE")
            categoryDao.insert(category)

            // When
            categoryDao.delete(category)
            val allCategories = categoryDao.getAll().first()

            // Then
            assertTrue(allCategories.isEmpty())
        }

    @Test
    fun defaultCategories_canBeInserted() =
        runBlocking {
            // Given - Default categories that should be pre-populated
            val defaultCategories =
                listOf(
                    CategoryEntity(1, "Yeme İçme", "🍔", "#FF5722", "EXPENSE"),
                    CategoryEntity(2, "Ulaşım", "🚗", "#2196F3", "EXPENSE"),
                    CategoryEntity(3, "Market", "🛒", "#4CAF50", "EXPENSE"),
                    CategoryEntity(4, "Faturalar", "💡", "#FFC107", "EXPENSE"),
                    CategoryEntity(5, "Maaş", "💰", "#4CAF50", "INCOME"),
                    CategoryEntity(6, "Freelance", "💼", "#9C27B0", "INCOME"),
                )

            // When
            defaultCategories.forEach { categoryDao.insert(it) }
            val allCategories = categoryDao.getAll().first()

            // Then
            assertEquals(6, allCategories.size)
            assertTrue(allCategories.any { it.name == "Yeme İçme" })
            assertTrue(allCategories.any { it.name == "Maaş" })
        }
}
