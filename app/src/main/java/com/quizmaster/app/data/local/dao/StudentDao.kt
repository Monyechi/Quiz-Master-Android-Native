package com.quizmaster.app.data.local.dao

import androidx.room.*
import com.quizmaster.app.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(student: StudentEntity): Long

    @Query("SELECT * FROM students WHERE userId = :userId LIMIT 1")
    suspend fun findByUserId(userId: Int): StudentEntity?

    @Query("SELECT * FROM students WHERE studentId = :id LIMIT 1")
    suspend fun findById(id: Int): StudentEntity?

    @Query("SELECT * FROM students WHERE instructorId = :instructorId")
    fun getStudentsByInstructor(instructorId: Int): Flow<List<StudentEntity>>

    @Update
    suspend fun update(student: StudentEntity)

    @Delete
    suspend fun delete(student: StudentEntity)
}
