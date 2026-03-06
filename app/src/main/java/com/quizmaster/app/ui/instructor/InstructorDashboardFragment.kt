package com.quizmaster.app.ui.instructor

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.quizmaster.app.R
import com.quizmaster.app.databinding.FragmentInstructorDashboardBinding
import com.quizmaster.app.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InstructorDashboardFragment : Fragment() {

    private var _binding: FragmentInstructorDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InstructorViewModel by viewModels()
    @Inject lateinit var session: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInstructorDashboardBinding.inflate(inflater, container, false)
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
                        findNavController().navigate(R.id.action_instructorDashboardFragment_to_loginFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val studentAdapter = StudentListAdapter { student ->
            // Navigate to compose message to this student
            val bundle = Bundle().apply {
                putInt("receiverUserId", student.userId)
                putString("receiverDisplayName", student.displayName)
            }
            findNavController().navigate(R.id.action_instructorDashboardFragment_to_composeMessageFragment, bundle)
        }
        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.adapter = studentAdapter

        viewModel.instructor.observe(viewLifecycleOwner) { instructor ->
            if (instructor == null) {
                findNavController().navigate(R.id.action_instructorDashboardFragment_to_createInstructorProfileFragment)
                return@observe
            }
            binding.tvWelcome.text = "Welcome, ${instructor.firstName} ${instructor.lastName}!"
            binding.tvInstructorKey.text = "Your Instructor Key: ${instructor.instructorKey}"

            lifecycleScope.launch {
                viewModel.getMyStudents(instructor.instructorId).collect { students ->
                    studentAdapter.submitList(students)
                    binding.tvNoStudents.visibility =
                        if (students.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        binding.btnMessages.setOnClickListener {
            findNavController().navigate(R.id.action_instructorDashboardFragment_to_inboxFragment)
        }
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_instructorDashboardFragment_to_editInstructorProfileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
