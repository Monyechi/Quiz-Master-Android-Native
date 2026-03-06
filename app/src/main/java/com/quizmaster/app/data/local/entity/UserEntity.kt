package com.quizmaster.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user account. Stores credentials locally (password is hashed via BCrypt-style
 * SHA-256 + salt in AuthRepository). This replaces ASP.NET Identity.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val email: String,
    val passwordHash: String,
    val salt: String,
    /** "Student" or "Instructor" */
    val role: String
)
