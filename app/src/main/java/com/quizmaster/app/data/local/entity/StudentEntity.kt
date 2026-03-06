package com.quizmaster.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "students",
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
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val studentId: Int = 0,
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val displayName: String,
    val profileAvatar: String = "",
    val grade: String = "",
    /** FK to InstructorEntity.instructorId, null if not yet enrolled */
    val instructorId: Int? = null
)
