package com.quizmaster.app.ui.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizmaster.app.data.repository.QuizCategory
import com.quizmaster.app.data.repository.QuizDifficulty
import com.quizmaster.app.data.repository.QuizQuestion
import com.quizmaster.app.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(private val quizRepo: QuizRepository) : ViewModel() {

    private val _questions = MutableLiveData<List<QuizQuestion>>()
    val questions: LiveData<List<QuizQuestion>> = _questions

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _answerResult = MutableLiveData<AnswerResult?>()
    val answerResult: LiveData<AnswerResult?> = _answerResult

    private val _quizState = MutableLiveData<QuizState>(QuizState.Idle)
    val quizState: LiveData<QuizState> = _quizState

    val currentQuestion: QuizQuestion?
        get() = _questions.value?.getOrNull(_currentIndex.value ?: 0)

    fun loadQuiz(category: QuizCategory, difficulty: QuizDifficulty) {
        viewModelScope.launch {
            _quizState.value = QuizState.Loading
            try {
                val qs = quizRepo.fetchQuestions(category, difficulty)
                _questions.value = qs
                _currentIndex.value = 0
                _score.value = 0
                _answerResult.value = null
                _quizState.value = QuizState.Playing
            } catch (e: Exception) {
                _quizState.value = QuizState.Error("Failed to load questions. Check your internet connection.")
            }
        }
    }

    fun submitAnswer(selectedAnswer: String) {
        val q = currentQuestion ?: return
        val correct = selectedAnswer == q.correctAnswer
        if (correct) _score.value = (_score.value ?: 0) + 1
        _answerResult.value = AnswerResult(
            selectedAnswer = selectedAnswer,
            correctAnswer = q.correctAnswer,
            isCorrect = correct
        )
    }

    fun nextQuestion() {
        val next = (_currentIndex.value ?: 0) + 1
        _answerResult.value = null
        val total = _questions.value?.size ?: 0
        if (next >= total) {
            _quizState.value = QuizState.Finished
        } else {
            _currentIndex.value = next
        }
    }

    fun resetQuiz() {
        _questions.value = emptyList()
        _currentIndex.value = 0
        _score.value = 0
        _answerResult.value = null
        _quizState.value = QuizState.Idle
    }
}

data class AnswerResult(val selectedAnswer: String, val correctAnswer: String, val isCorrect: Boolean)

sealed class QuizState {
    object Idle : QuizState()
    object Loading : QuizState()
    object Playing : QuizState()
    object Finished : QuizState()
    data class Error(val message: String) : QuizState()
}
