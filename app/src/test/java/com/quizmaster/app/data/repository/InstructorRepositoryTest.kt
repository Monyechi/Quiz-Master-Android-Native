package com.quizmaster.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.quizmaster.app.data.local.QuizMasterDatabase
import com.quizmaster.app.data.local.entity.UserEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InstructorRepositoryTest {

    private lateinit var db: QuizMasterDatabase
    private lateinit var instructorRepository: InstructorRepository

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            QuizMasterDatabase::class.java
        ).allowMainThreadQueries().build()
        instructorRepository = InstructorRepository(db.instructorDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun createProfile_generatesKeyAndPersistsProfile() = runTest {
        val userId = db.userDao().insert(
            UserEntity(
                email = "instructor@example.com",
                passwordHash = "hash",
                salt = "salt",
                role = "Instructor"
            )
        ).toInt()

        val profile = instructorRepository.createProfile(userId, "Ada", "Lovelace")
        assertTrue(profile.instructorId > 0)
        assertEquals(20, profile.instructorKey.length)

        val fetched = instructorRepository.getByUserId(userId)
        assertNotNull(fetched)
        assertEquals(profile.instructorKey, fetched?.instructorKey)
    }
}
