package com.quizmaster.app.ui.quiz

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.quizmaster.app.R
import com.quizmaster.app.data.repository.QuizCategory
import com.quizmaster.app.data.repository.QuizDifficulty
import com.quizmaster.app.databinding.FragmentQuizBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val category = QuizCategory.valueOf(arguments?.getString("category") ?: "SCIENCE")
        val difficulty = QuizDifficulty.valueOf(arguments?.getString("difficulty") ?: "EASY")

        viewModel.loadQuiz(category, difficulty)

        viewModel.quizState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is QuizState.Loading -> showLoading(true)
                is QuizState.Playing -> showLoading(false)
                is QuizState.Finished -> showResults()
                is QuizState.Error -> {
                    showLoading(false)
                    binding.tvQuestion.text = state.message
                }
                else -> {}
            }
        }

        viewModel.currentIndex.observe(viewLifecycleOwner) { index ->
            displayQuestion(index)
        }

        viewModel.answerResult.observe(viewLifecycleOwner) { result ->
            if (result == null) {
                resetAnswerButtons()
                return@observe
            }
            highlightAnswers(result)
            binding.btnNext.visibility = View.VISIBLE
        }

        binding.btnNext.setOnClickListener {
            viewModel.nextQuestion()
            binding.btnNext.visibility = View.GONE
        }
    }

    private fun displayQuestion(index: Int) {
        val q = viewModel.currentQuestion ?: return
        val total = viewModel.questions.value?.size ?: 0
        binding.tvProgress.text = "Question ${index + 1} / $total"
        binding.tvQuestion.text = Html.fromHtml(q.question, Html.FROM_HTML_MODE_LEGACY)

        val answerButtons = listOf(binding.btnA, binding.btnB, binding.btnC, binding.btnD)
        q.shuffledAnswers.forEachIndexed { i, answer ->
            answerButtons[i].text = Html.fromHtml(answer, Html.FROM_HTML_MODE_LEGACY)
            answerButtons[i].setOnClickListener { viewModel.submitAnswer(answer) }
        }
    }

    private fun highlightAnswers(result: AnswerResult) {
        val answerButtons = listOf(binding.btnA, binding.btnB, binding.btnC, binding.btnD)
        viewModel.currentQuestion ?: return
        answerButtons.forEach { btn ->
            val btnText = Html.fromHtml(btn.text.toString(), Html.FROM_HTML_MODE_LEGACY).toString()
            val correct = Html.fromHtml(result.correctAnswer, Html.FROM_HTML_MODE_LEGACY).toString()
            val selected = Html.fromHtml(result.selectedAnswer, Html.FROM_HTML_MODE_LEGACY).toString()
            btn.isEnabled = false
            btn.setBackgroundColor(
                when {
                    btnText == correct -> Color.parseColor("#4CAF50")         // green
                    btnText == selected && !result.isCorrect -> Color.parseColor("#F44336") // red
                    else -> Color.LTGRAY
                }
            )
        }
    }

    private fun resetAnswerButtons() {
        val answerButtons = listOf(binding.btnA, binding.btnB, binding.btnC, binding.btnD)
        answerButtons.forEach { btn ->
            btn.isEnabled = true
            btn.setBackgroundColor(Color.parseColor("#1976D2"))
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.quizContent.visibility = if (loading) View.GONE else View.VISIBLE
    }

    private fun showResults() {
        val score = viewModel.score.value ?: 0
        val total = viewModel.questions.value?.size ?: 0
        val bundle = Bundle().apply {
            putInt("score", score)
            putInt("total", total)
        }
        findNavController().navigate(R.id.action_quizFragment_to_quizResultFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
