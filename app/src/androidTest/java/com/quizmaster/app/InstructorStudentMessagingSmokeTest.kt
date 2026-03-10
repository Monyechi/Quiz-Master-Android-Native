package com.quizmaster.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.quizmaster.app.data.local.QuizMasterDatabase
import com.quizmaster.app.data.repository.AuthRepository
import com.quizmaster.app.data.repository.AuthResult
import com.quizmaster.app.data.repository.InstructorRepository
import com.quizmaster.app.data.repository.MessageRepository
import com.quizmaster.app.data.repository.StudentRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InstructorStudentMessagingSmokeTest {

    private lateinit var db: QuizMasterDatabase
    private lateinit var authRepository: AuthRepository
    private lateinit var instructorRepository: InstructorRepository
    private lateinit var studentRepository: StudentRepository
    private lateinit var messageRepository: MessageRepository

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            QuizMasterDatabase::class.java
        ).allowMainThreadQueries().build()
        authRepository = AuthRepository(db.userDao())
        instructorRepository = InstructorRepository(db.instructorDao())
        studentRepository = StudentRepository(db.studentDao())
        messageRepository = MessageRepository(db.messageDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun instructorManagedEnrollmentAndMessaging_smokeTest() = runBlocking {
        val registeredInstructor = authRepository.register(
            email = "teacher.smoke@example.com",
            password = "secret123",
            role = "Instructor"
        )
        assertTrue(registeredInstructor is AuthResult.Success)
        val instructorUser = (registeredInstructor as AuthResult.Success).user

        val loggedInInstructor = authRepository.login("teacher.smoke@example.com", "secret123")
        assertTrue(loggedInInstructor is AuthResult.Success)

        val instructorProfile = instructorRepository.createProfile(
            userId = instructorUser.userId,
            firstName = "Instructor",
            lastName = "Smoke"
        )
        assertEquals(20, instructorProfile.instructorKey.length)

        val registeredStudent = authRepository.register(
            email = "student.smoke@example.com",
            password = "secret123",
            role = "Student"
        )
        assertTrue(registeredStudent is AuthResult.Success)
        val studentUser = (registeredStudent as AuthResult.Success).user

        val studentProfile = studentRepository.createProfile(
            userId = studentUser.userId,
            firstName = "Student",
            lastName = "Smoke",
            displayName = "student-smoke",
            grade = "9"
        )

        val assigned = studentRepository.assignStudentToInstructor(
            studentId = studentProfile.studentId,
            instructorId = instructorProfile.instructorId
        )
        assertTrue(assigned)
        assertEquals(
            instructorProfile.instructorId,
            studentRepository.getById(studentProfile.studentId)?.instructorId
        )

        messageRepository.sendMessage(
            senderUserId = instructorUser.userId,
            receiverUserId = studentUser.userId,
            senderDisplayName = "Instructor Smoke",
            receiverDisplayName = "student-smoke",
            subject = "Class Update",
            content = "Please complete chapter one."
        )

        val studentInbox = messageRepository.getInbox(studentUser.userId).first()
        assertTrue(studentInbox.isNotEmpty())
        assertEquals("Class Update", studentInbox.first().subject)
        assertNotNull(studentInbox.first().content)
    }
}
