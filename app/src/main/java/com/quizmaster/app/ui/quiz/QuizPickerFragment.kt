package com.quizmaster.app.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.quizmaster.app.R
import com.quizmaster.app.databinding.FragmentQuizPickerBinding
import dagger.hilt.android.AndroidEntryPoint

/** Lets the user choose difficulty before starting a quiz. */
@AndroidEntryPoint
class QuizPickerFragment : Fragment() {

    private var _binding: FragmentQuizPickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuizPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val category = arguments?.getString("category") ?: "SCIENCE"
        binding.tvCategory.text = category.replaceFirstChar { it.uppercase() } + " Quiz"

        listOf(binding.btnEasy, binding.btnMedium, binding.btnHard).forEachIndexed { i, btn ->
            val difficulty = listOf("EASY", "MEDIUM", "HARD")[i]
            btn.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("category", category)
                    putString("difficulty", difficulty)
                }
                findNavController().navigate(R.id.action_quizPickerFragment_to_quizFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
