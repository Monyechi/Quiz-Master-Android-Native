package com.quizmaster.app.ui.instructor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.quizmaster.app.databinding.FragmentEnrollStudentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EnrollStudentFragment : Fragment() {

    private var _binding: FragmentEnrollStudentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InstructorViewModel by viewModels()
    private val adapter = EnrollStudentListAdapter { student ->
        viewModel.assignStudentToCurrentInstructor(student.studentId)
    }
    private var allStudents: List<com.quizmaster.app.data.local.entity.StudentEntity> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnrollStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadProfile()

        binding.rvUnassignedStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUnassignedStudents.adapter = adapter

        lifecycleScope.launch {
            viewModel.unassignedStudents.collect { students ->
                allStudents = students
                applyFilter(binding.etSearch.text?.toString().orEmpty())
            }
        }

        binding.etSearch.doAfterTextChanged { text ->
            applyFilter(text?.toString().orEmpty())
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyFilter(query: String) {
        val filtered = if (query.isBlank()) {
            allStudents
        } else {
            val normalized = query.trim().lowercase()
            allStudents.filter { student ->
                student.displayName.lowercase().contains(normalized) ||
                    student.firstName.lowercase().contains(normalized) ||
                    student.lastName.lowercase().contains(normalized)
            }
        }
        adapter.submitList(filtered)
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
