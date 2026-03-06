package com.quizmaster.app.ui.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizmaster.app.data.local.entity.InstructorEntity
import com.quizmaster.app.data.repository.InstructorRepository
import com.quizmaster.app.data.repository.MessageRepository
import com.quizmaster.app.data.repository.StudentRepository
import com.quizmaster.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepo: MessageRepository,
    private val studentRepo: StudentRepository,
    private val instructorRepo: InstructorRepository,
    private val session: SessionManager
) : ViewModel() {

    val inbox = messageRepo.getInbox(session.currentUserId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /** Non-null when the current user is a Student and is enrolled with an instructor. */
    private val _studentInstructor = MutableLiveData<InstructorEntity?>()
    val studentInstructor: LiveData<InstructorEntity?> = _studentInstructor

    init {
        if (session.currentUserRole == "Student") {
            viewModelScope.launch {
                val student = studentRepo.getByUserId(session.currentUserId)
                if (student?.instructorId != null) {
                    _studentInstructor.value = instructorRepo.getById(student.instructorId!!)
                }
            }
        }
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
