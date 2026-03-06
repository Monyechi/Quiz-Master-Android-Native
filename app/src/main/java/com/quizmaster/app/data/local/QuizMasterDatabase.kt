package com.quizmaster.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.quizmaster.app.data.local.dao.InstructorDao
import com.quizmaster.app.data.local.dao.MessageDao
import com.quizmaster.app.data.local.dao.StudentDao
import com.quizmaster.app.data.local.dao.UserDao
import com.quizmaster.app.data.local.entity.InstructorEntity
import com.quizmaster.app.data.local.entity.MessageEntity
import com.quizmaster.app.data.local.entity.StudentEntity
import com.quizmaster.app.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, InstructorEntity::class, StudentEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class QuizMasterDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun instructorDao(): InstructorDao
    abstract fun studentDao(): StudentDao
    abstract fun messageDao(): MessageDao
}
