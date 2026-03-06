package com.quizmaster.app.data.repository

import android.util.Base64
import com.quizmaster.app.data.local.dao.UserDao
import com.quizmaster.app.data.local.entity.UserEntity
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

@Singleton
class AuthRepository @Inject constructor(private val userDao: UserDao) {

    suspend fun register(email: String, password: String, role: String): AuthResult {
        if (userDao.findByEmail(email) != null) {
            return AuthResult.Error("An account with this email already exists.")
        }
        val salt = generateSalt()
        val hash = hashPassword(password, salt)
        val user = UserEntity(email = email, passwordHash = hash, salt = salt, role = role)
        val id = userDao.insert(user)
        return AuthResult.Success(user.copy(userId = id.toInt()))
    }

    suspend fun login(email: String, password: String): AuthResult {
        val user = userDao.findByEmail(email)
            ?: return AuthResult.Error("No account found with this email.")
        val hash = hashPassword(password, user.salt)
        if (hash != user.passwordHash) {
            return AuthResult.Error("Incorrect password.")
        }
        return AuthResult.Success(user)
    }

    suspend fun getUserById(id: Int): UserEntity? = userDao.findById(id)

    private fun generateSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun hashPassword(password: String, salt: String): String {
        val input = "$salt:$password"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
    }
}
