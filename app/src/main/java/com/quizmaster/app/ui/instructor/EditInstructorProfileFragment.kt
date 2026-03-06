package com.quizmaster.app.ui.instructor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.quizmaster.app.databinding.FragmentEditInstructorProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditInstructorProfileFragment : Fragment() {

    private var _binding: FragmentEditInstructorProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InstructorViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditInstructorProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadProfile()

        viewModel.instructor.observe(viewLifecycleOwner) { instructor ->
            if (instructor != null && binding.etFirstName.text.isNullOrEmpty()) {
                binding.etFirstName.setText(instructor.firstName)
                binding.etLastName.setText(instructor.lastName)
            }
        }

        binding.btnSave.setOnClickListener {
            viewModel.updateProfile(
                firstName = binding.etFirstName.text.toString().trim(),
                lastName = binding.etLastName.text.toString().trim()
            )
            findNavController().navigateUp()
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteProfile()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
