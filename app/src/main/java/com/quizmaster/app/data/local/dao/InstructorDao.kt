package com.quizmaster.app.data.local.dao

import androidx.room.*
import com.quizmaster.app.data.local.entity.InstructorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstructorDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(instructor: InstructorEntity): Long

    @Query("SELECT * FROM instructors WHERE userId = :userId LIMIT 1")
    suspend fun findByUserId(userId: Int): InstructorEntity?

    @Query("SELECT * FROM instructors WHERE instructorKey = :key LIMIT 1")
    suspend fun findByKey(key: String): InstructorEntity?

    @Query("SELECT * FROM instructors WHERE instructorId = :id LIMIT 1")
    suspend fun findById(id: Int): InstructorEntity?

    @Query("SELECT * FROM instructors")
    fun getAllInstructors(): Flow<List<InstructorEntity>>

    @Update
    suspend fun update(instructor: InstructorEntity)

    @Delete
    suspend fun delete(instructor: InstructorEntity)
}
