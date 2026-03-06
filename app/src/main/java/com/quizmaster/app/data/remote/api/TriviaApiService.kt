package com.quizmaster.app.data.remote.api

import com.quizmaster.app.data.remote.model.TriviaResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Open Trivia DB API — https://opentdb.com/api.php
 *
 * Category IDs used by the web app:
 *   Science & Nature = 17
 *   Mathematics      = 19
 *   History          = 23
 */
interface TriviaApiService {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int = 10,
        @Query("category") categoryId: Int,
        @Query("difficulty") difficulty: String,
        @Query("type") type: String = "multiple"
    ): TriviaResponse
}
