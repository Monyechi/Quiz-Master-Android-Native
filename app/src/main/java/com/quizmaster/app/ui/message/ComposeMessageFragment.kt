package com.quizmaster.app.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.quizmaster.app.databinding.FragmentComposeMessageBinding
import com.quizmaster.app.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Compose a message. Pre-filled when navigating from instructor's student list
 * (arguments: receiverUserId, receiverDisplayName).
 */
@AndroidEntryPoint
class ComposeMessageFragment : Fragment() {

    private var _binding: FragmentComposeMessageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MessageViewModel by viewModels()
    @Inject lateinit var session: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentComposeMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receiverUserId = arguments?.getInt("receiverUserId", -1) ?: -1
        val receiverDisplayName = arguments?.getString("receiverDisplayName") ?: ""

        if (receiverDisplayName.isNotBlank()) {
            binding.etReceiver.setText(receiverDisplayName)
            binding.etReceiver.isEnabled = false
        }

        binding.btnSend.setOnClickListener {
            val subject = binding.etSubject.text.toString().trim()
            val content = binding.etContent.text.toString().trim()
            val receiver = binding.etReceiver.text.toString().trim()

            if (subject.isBlank() || content.isBlank() || receiver.isBlank()) {
                Toast.makeText(requireContext(), "All fields are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (receiverUserId == -1) {
                Toast.makeText(requireContext(), "Receiver not identified.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.sendMessage(
                receiverUserId = receiverUserId,
                senderDisplayName = "User #${session.currentUserId}",
                receiverDisplayName = receiver,
                subject = subject,
                content = content
            )
            Toast.makeText(requireContext(), "Message sent!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
