package com.quizmaster.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.quizmaster.app.data.local.QuizMasterDatabase
import com.quizmaster.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MessageRepositoryTest {

    private lateinit var db: QuizMasterDatabase
    private lateinit var messageRepository: MessageRepository

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            QuizMasterDatabase::class.java
        ).allowMainThreadQueries().build()
        messageRepository = MessageRepository(db.messageDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun sendMessage_persistsToInboxAndSentCollections() = runTest {
        val senderUserId = db.userDao().insert(
            UserEntity(
                email = "instructor@example.com",
                passwordHash = "hash",
                salt = "salt",
                role = "Instructor"
            )
        ).toInt()
        val receiverUserId = db.userDao().insert(
            UserEntity(
                email = "student@example.com",
                passwordHash = "hash",
                salt = "salt",
                role = "Student"
            )
        ).toInt()

        messageRepository.sendMessage(
            senderUserId = senderUserId,
            receiverUserId = receiverUserId,
            senderDisplayName = "Instructor One",
            receiverDisplayName = "Student One",
            subject = "Welcome",
            content = "Welcome to class."
        )

        val inbox = messageRepository.getInbox(receiverUserId).first()
        assertEquals(1, inbox.size)
        assertEquals("Welcome", inbox.first().subject)
        assertEquals(senderUserId, inbox.first().senderUserId)

        val sent = messageRepository.getSent(senderUserId).first()
        assertTrue(sent.any { it.receiverUserId == receiverUserId && it.subject == "Welcome" })
    }
}
