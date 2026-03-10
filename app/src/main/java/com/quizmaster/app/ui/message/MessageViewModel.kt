package com.quizmaster.app.ui.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizmaster.app.data.repository.InstructorRepository
import com.quizmaster.app.data.repository.MessageRepository
import com.quizmaster.app.data.repository.StudentRepository
import com.quizmaster.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ComposeTarget(
    val receiverUserId: Int,
    val receiverDisplayName: String
)

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepo: MessageRepository,
    private val studentRepo: StudentRepository,
    private val instructorRepo: InstructorRepository,
    private val session: SessionManager
) : ViewModel() {

    val inbox = messageRepo.getInbox(session.currentUserId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isStudentRole: Boolean
        get() = session.currentUserRole == "Student"

    suspend fun resolveStudentComposeTarget(): ComposeTarget? {
        if (!isStudentRole) return null
        val student = studentRepo.getByUserId(session.currentUserId) ?: return null
        val instructorId = student.instructorId ?: return null
        val instructor = instructorRepo.getById(instructorId) ?: return null
        return ComposeTarget(
            receiverUserId = instructor.userId,
            receiverDisplayName = "${instructor.firstName} ${instructor.lastName}"
        )
    }

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
