package com.quizmaster.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.quizmaster.app.data.local.QuizMasterDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AuthRepositoryTest {

    private lateinit var db: QuizMasterDatabase
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            QuizMasterDatabase::class.java
        ).allowMainThreadQueries().build()
        authRepository = AuthRepository(db.userDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun registerAndLoginInstructor_success() = runTest {
        val registerResult = authRepository.register(
            email = "teacher@example.com",
            password = "secret123",
            role = "Instructor"
        )
        assertTrue(registerResult is AuthResult.Success)
        val createdUser = (registerResult as AuthResult.Success).user
        assertEquals("Instructor", createdUser.role)

        val loginResult = authRepository.login("teacher@example.com", "secret123")
        assertTrue(loginResult is AuthResult.Success)
        val loggedInUser = (loginResult as AuthResult.Success).user
        assertEquals(createdUser.userId, loggedInUser.userId)
    }

    @Test
    fun registerDuplicateEmail_returnsError() = runTest {
        authRepository.register("student@example.com", "secret123", "Student")
        val duplicate = authRepository.register("student@example.com", "another123", "Student")

        assertTrue(duplicate is AuthResult.Error)
        val message = (duplicate as AuthResult.Error).message
        assertTrue(message.contains("already exists"))
    }
}
