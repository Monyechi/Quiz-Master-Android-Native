package com.quizmaster.app.data.repository

import com.quizmaster.app.data.local.dao.MessageDao
import com.quizmaster.app.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(private val dao: MessageDao) {

    suspend fun sendMessage(
        senderUserId: Int,
        receiverUserId: Int,
        senderDisplayName: String,
        receiverDisplayName: String,
        subject: String,
        content: String
    ) {
        dao.insert(
            MessageEntity(
                senderUserId = senderUserId,
                receiverUserId = receiverUserId,
                senderDisplayName = senderDisplayName,
                receiverDisplayName = receiverDisplayName,
                subject = subject,
                content = content
            )
        )
    }

    fun getInbox(userId: Int): Flow<List<MessageEntity>> = dao.getMessagesForUser(userId)

    fun getSent(userId: Int): Flow<List<MessageEntity>> = dao.getSentMessagesByUser(userId)

    suspend fun delete(message: MessageEntity) = dao.delete(message)
}
