package com.quizmaster.app.ui.message

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
import com.quizmaster.app.R
import com.quizmaster.app.databinding.FragmentInboxBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InboxFragment : Fragment() {

    private var _binding: FragmentInboxBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MessageViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInboxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MessageListAdapter()
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMessages.adapter = adapter

        lifecycleScope.launch {
            viewModel.inbox.collect { messages ->
                adapter.submitList(messages)
                binding.tvEmpty.visibility = if (messages.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        binding.fabCompose.visibility = if (viewModel.isStudentRole) View.VISIBLE else View.GONE

        binding.fabCompose.setOnClickListener {
            lifecycleScope.launch {
                val target = viewModel.resolveStudentComposeTarget()
                if (target == null) {
                    Toast.makeText(
                        requireContext(),
                        "Cannot compose: no assigned instructor found for this student.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }
                val bundle = Bundle().apply {
                    putInt("receiverUserId", target.receiverUserId)
                    putString("receiverDisplayName", target.receiverDisplayName)
                }
                findNavController().navigate(R.id.action_inboxFragment_to_composeMessageFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
