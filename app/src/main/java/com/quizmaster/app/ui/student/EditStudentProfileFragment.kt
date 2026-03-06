package com.quizmaster.app.ui.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.quizmaster.app.databinding.FragmentEditStudentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditStudentProfileFragment : Fragment() {

    private var _binding: FragmentEditStudentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudentViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditStudentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadProfile()

        viewModel.student.observe(viewLifecycleOwner) { student ->
            if (student != null && binding.etFirstName.text.isNullOrEmpty()) {
                binding.etFirstName.setText(student.firstName)
                binding.etLastName.setText(student.lastName)
                binding.etDisplayName.setText(student.displayName)
                binding.etGrade.setText(student.grade)
            }
        }

        binding.btnSave.setOnClickListener {
            viewModel.updateProfile(
                firstName = binding.etFirstName.text.toString().trim(),
                lastName = binding.etLastName.text.toString().trim(),
                displayName = binding.etDisplayName.text.toString().trim(),
                grade = binding.etGrade.text.toString().trim()
            )
            findNavController().navigateUp()
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteProfile()
            findNavController().navigateUp()
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
