package com.quizmaster.app.ui.instructor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.quizmaster.app.databinding.FragmentCreateInstructorProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateInstructorProfileFragment : Fragment() {

    private var _binding: FragmentCreateInstructorProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InstructorViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateInstructorProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            viewModel.createProfile(
                firstName = binding.etFirstName.text.toString().trim(),
                lastName = binding.etLastName.text.toString().trim()
            )
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        viewModel.instructor.observe(viewLifecycleOwner) { instructor ->
            if (instructor != null) {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
