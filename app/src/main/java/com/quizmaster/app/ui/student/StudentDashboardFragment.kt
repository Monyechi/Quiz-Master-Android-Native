package com.quizmaster.app.ui.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.quizmaster.app.R
import com.quizmaster.app.databinding.FragmentStudentDashboardBinding
import com.quizmaster.app.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StudentDashboardFragment : Fragment() {

    private var _binding: FragmentStudentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudentViewModel by viewModels()
    @Inject lateinit var session: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadProfile()

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }
            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.action_logout -> {
                        session.logout()
                        findNavController().navigate(R.id.action_studentDashboardFragment_to_loginFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.student.observe(viewLifecycleOwner) { student ->
            if (student == null) {
                findNavController().navigate(R.id.action_studentDashboardFragment_to_createStudentProfileFragment)
                return@observe
            }
            binding.tvWelcome.text = "Welcome, ${student.displayName}!"
            binding.tvGrade.text = "Grade: ${student.grade}"
            val enrolledText = if (student.instructorId != null)
                "Enrolled" else "Not enrolled with an instructor"
            binding.tvInstructorStatus.text = enrolledText
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        // Navigation buttons
        binding.btnScienceQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_studentDashboardFragment_to_quizPickerFragment,
                Bundle().apply { putString("category", "SCIENCE") })
        }
        binding.btnMathQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_studentDashboardFragment_to_quizPickerFragment,
                Bundle().apply { putString("category", "MATH") })
        }
        binding.btnHistoryQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_studentDashboardFragment_to_quizPickerFragment,
                Bundle().apply { putString("category", "HISTORY") })
        }
        binding.btnMessages.setOnClickListener {
            findNavController().navigate(R.id.action_studentDashboardFragment_to_inboxFragment)
        }
        binding.btnSelectInstructor.setOnClickListener {
            findNavController().navigate(R.id.action_studentDashboardFragment_to_selectInstructorFragment)
        }
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_studentDashboardFragment_to_editStudentProfileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
