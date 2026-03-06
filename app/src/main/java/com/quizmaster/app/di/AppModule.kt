package com.quizmaster.app.di

import android.content.Context
import androidx.room.Room
import com.quizmaster.app.data.local.QuizMasterDatabase
import com.quizmaster.app.data.local.dao.InstructorDao
import com.quizmaster.app.data.local.dao.MessageDao
import com.quizmaster.app.data.local.dao.StudentDao
import com.quizmaster.app.data.local.dao.UserDao
import com.quizmaster.app.data.remote.api.TriviaApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): QuizMasterDatabase =
        Room.databaseBuilder(ctx, QuizMasterDatabase::class.java, "quizmaster.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideUserDao(db: QuizMasterDatabase): UserDao = db.userDao()
    @Provides fun provideInstructorDao(db: QuizMasterDatabase): InstructorDao = db.instructorDao()
    @Provides fun provideStudentDao(db: QuizMasterDatabase): StudentDao = db.studentDao()
    @Provides fun provideMessageDao(db: QuizMasterDatabase): MessageDao = db.messageDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideTriviaApiService(retrofit: Retrofit): TriviaApiService =
        retrofit.create(TriviaApiService::class.java)
}
