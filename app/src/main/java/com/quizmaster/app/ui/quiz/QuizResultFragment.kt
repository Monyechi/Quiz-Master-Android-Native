package com.quizmaster.app.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.quizmaster.app.databinding.FragmentQuizResultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizResultFragment : Fragment() {

    private var _binding: FragmentQuizResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuizResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val score = arguments?.getInt("score") ?: 0
        val total = arguments?.getInt("total") ?: 0

        binding.tvScore.text = "You scored $score / $total"
        val pct = if (total > 0) (score * 100 / total) else 0
        binding.tvRating.text = when {
            pct >= 90 -> "Excellent!"
            pct >= 70 -> "Good job!"
            pct >= 50 -> "Keep practicing!"
            else -> "Better luck next time!"
        }

        binding.btnBackToDashboard.setOnClickListener {
            findNavController().popBackStack(findNavController().graph.startDestinationId, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
