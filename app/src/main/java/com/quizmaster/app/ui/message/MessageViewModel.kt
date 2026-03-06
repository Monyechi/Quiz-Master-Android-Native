package com.quizmaster.app.ui.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizmaster.app.data.repository.MessageRepository
import com.quizmaster.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepo: MessageRepository,
    private val session: SessionManager
) : ViewModel() {

    val inbox = messageRepo.getInbox(session.currentUserId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun sendMessage(
        receiverUserId: Int,
        senderDisplayName: String,
        receiverDisplayName: String,
        subject: String,
        content: String
    ) {
        viewModelScope.launch {
            messageRepo.sendMessage(
                senderUserId = session.currentUserId,
                receiverUserId = receiverUserId,
                senderDisplayName = senderDisplayName,
                receiverDisplayName = receiverDisplayName,
                subject = subject,
                content = content
            )
        }
    }
}
