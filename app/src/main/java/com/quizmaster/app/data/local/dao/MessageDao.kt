package com.quizmaster.app.data.local.dao

import androidx.room.*
import com.quizmaster.app.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(message: MessageEntity): Long

    @Query("SELECT * FROM messages WHERE receiverUserId = :userId ORDER BY timestamp DESC")
    fun getMessagesForUser(userId: Int): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE senderUserId = :userId ORDER BY timestamp DESC")
    fun getSentMessagesByUser(userId: Int): Flow<List<MessageEntity>>

    @Delete
    suspend fun delete(message: MessageEntity)
}
