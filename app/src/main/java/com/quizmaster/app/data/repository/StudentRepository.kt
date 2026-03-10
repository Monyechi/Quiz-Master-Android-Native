package com.quizmaster.app.data.repository

import com.quizmaster.app.data.local.dao.StudentDao
import com.quizmaster.app.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(private val dao: StudentDao) {

    suspend fun createProfile(
        userId: Int,
        firstName: String,
        lastName: String,
        displayName: String,
        grade: String
    ): StudentEntity {
        val entity = StudentEntity(
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            displayName = displayName,
            grade = grade
        )
        val id = dao.insert(entity)
        return entity.copy(studentId = id.toInt())
    }

    suspend fun getByUserId(userId: Int): StudentEntity? = dao.findByUserId(userId)

    suspend fun getById(id: Int): StudentEntity? = dao.findById(id)

    fun getStudentsByInstructor(instructorId: Int): Flow<List<StudentEntity>> =
        dao.getStudentsByInstructor(instructorId)

    fun getUnassignedStudents(): Flow<List<StudentEntity>> = dao.getUnassignedStudents()

    suspend fun assignStudentToInstructor(studentId: Int, instructorId: Int): Boolean =
        dao.assignStudentToInstructor(studentId, instructorId) > 0

    suspend fun enrollWithInstructor(student: StudentEntity, instructorId: Int) {
        dao.update(student.copy(instructorId = instructorId))
    }

    suspend fun updateProfile(student: StudentEntity) = dao.update(student)

    suspend fun deleteProfile(student: StudentEntity) = dao.delete(student)
}
