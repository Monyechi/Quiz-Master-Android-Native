package com.quizmaster.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["senderUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["receiverUserId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("senderUserId"), Index("receiverUserId")]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val messageId: Int = 0,
    val senderUserId: Int,
    val receiverUserId: Int,
    val senderDisplayName: String,
    val receiverDisplayName: String,
    val subject: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
