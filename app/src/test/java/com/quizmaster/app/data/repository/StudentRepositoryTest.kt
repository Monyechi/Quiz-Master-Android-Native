package com.quizmaster.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.quizmaster.app.data.local.QuizMasterDatabase
import com.quizmaster.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StudentRepositoryTest {

    private lateinit var db: QuizMasterDatabase
    private lateinit var studentRepository: StudentRepository
    private lateinit var instructorRepository: InstructorRepository

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            QuizMasterDatabase::class.java
        ).allowMainThreadQueries().build()
        studentRepository = StudentRepository(db.studentDao())
        instructorRepository = InstructorRepository(db.instructorDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun assignStudentToInstructor_movesStudentOutOfUnassignedList() = runTest {
        val instructorUserId = db.userDao().insert(
            UserEntity(
                email = "teacher@example.com",
                passwordHash = "hash",
                salt = "salt",
                role = "Instructor"
            )
        ).toInt()
        val instructor = instructorRepository.createProfile(instructorUserId, "Grace", "Hopper")

        val studentUserId = db.userDao().insert(
            UserEntity(
                email = "student@example.com",
                passwordHash = "hash",
                salt = "salt",
                role = "Student"
            )
        ).toInt()
        val student = studentRepository.createProfile(
            userId = studentUserId,
            firstName = "Alan",
            lastName = "Turing",
            displayName = "alant",
            grade = "10"
        )

        val unassignedBefore = studentRepository.getUnassignedStudents().first()
        assertTrue(unassignedBefore.any { it.studentId == student.studentId })

        val assigned = studentRepository.assignStudentToInstructor(
            studentId = student.studentId,
            instructorId = instructor.instructorId
        )
        assertTrue(assigned)

        val updated = studentRepository.getById(student.studentId)
        assertEquals(instructor.instructorId, updated?.instructorId)

        val unassignedAfter = studentRepository.getUnassignedStudents().first()
        assertFalse(unassignedAfter.any { it.studentId == student.studentId })
    }
}
