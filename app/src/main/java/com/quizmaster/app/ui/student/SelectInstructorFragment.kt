package com.quizmaster.app.ui.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.quizmaster.app.databinding.FragmentSelectInstructorBinding
import com.quizmaster.app.ui.instructor.InstructorListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectInstructorFragment : Fragment() {

    private var _binding: FragmentSelectInstructorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudentViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSelectInstructorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadProfile()

        val adapter = InstructorListAdapter { instructor ->
            viewModel.enrollWithInstructorById(instructor.instructorId)
        }
        binding.rvInstructors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInstructors.adapter = adapter

        lifecycleScope.launch {
            viewModel.allInstructors.collect { list ->
                adapter.submitList(list)
            }
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
            if (msg.startsWith("Enrolled")) findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
