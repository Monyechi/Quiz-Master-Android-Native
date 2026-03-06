package com.quizmaster.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "instructors",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class InstructorEntity(
    @PrimaryKey(autoGenerate = true) val instructorId: Int = 0,
    val userId: Int,
    val firstName: String,
    val lastName: String,
    /** Unique 20-char code students use to enroll */
    val instructorKey: String
)
