package com.quizmaster.app.data.repository

import com.quizmaster.app.data.remote.api.TriviaApiService
import com.quizmaster.app.data.remote.model.TriviaQuestion
import javax.inject.Inject
import javax.inject.Singleton

enum class QuizCategory(val id: Int) {
    SCIENCE(17),
    MATH(19),
    HISTORY(23)
}

enum class QuizDifficulty(val value: String) {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard")
}

data class QuizQuestion(
    val question: String,
    val correctAnswer: String,
    val shuffledAnswers: List<String>
)

@Singleton
class QuizRepository @Inject constructor(private val api: TriviaApiService) {

    suspend fun fetchQuestions(
        category: QuizCategory,
        difficulty: QuizDifficulty,
        amount: Int = 10
    ): List<QuizQuestion> {
        val response = api.getQuestions(
            amount = amount,
            categoryId = category.id,
            difficulty = difficulty.value
        )
        return response.results.map { it.toQuizQuestion() }
    }

    private fun TriviaQuestion.toQuizQuestion(): QuizQuestion {
        val answers = (incorrectAnswers + correctAnswer).shuffled()
        return QuizQuestion(
            question = question,
            correctAnswer = correctAnswer,
            shuffledAnswers = answers
        )
    }
}
