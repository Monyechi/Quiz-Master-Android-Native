package com.quizmaster.app.data.repository

import com.quizmaster.app.data.local.dao.InstructorDao
import com.quizmaster.app.data.local.entity.InstructorEntity
import kotlinx.coroutines.flow.Flow
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstructorRepository @Inject constructor(private val dao: InstructorDao) {

    suspend fun createProfile(userId: Int, firstName: String, lastName: String): InstructorEntity {
        val key = generateInstructorKey()
        val entity = InstructorEntity(
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            instructorKey = key
        )
        val id = dao.insert(entity)
        return entity.copy(instructorId = id.toInt())
    }

    suspend fun getByUserId(userId: Int): InstructorEntity? = dao.findByUserId(userId)

    suspend fun getByKey(key: String): InstructorEntity? = dao.findByKey(key)

    suspend fun getById(id: Int): InstructorEntity? = dao.findById(id)

    fun getAllInstructors(): Flow<List<InstructorEntity>> = dao.getAllInstructors()

    suspend fun updateProfile(instructor: InstructorEntity) = dao.update(instructor)

    suspend fun deleteProfile(instructor: InstructorEntity) = dao.delete(instructor)

    private fun generateInstructorKey(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val rng = SecureRandom()
        return (1..20).map { chars[rng.nextInt(chars.length)] }.joinToString("")
    }
}
