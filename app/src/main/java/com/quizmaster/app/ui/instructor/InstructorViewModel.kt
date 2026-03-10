package com.quizmaster.app.ui.instructor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizmaster.app.data.local.entity.InstructorEntity
import com.quizmaster.app.data.local.entity.StudentEntity
import com.quizmaster.app.data.repository.InstructorRepository
import com.quizmaster.app.data.repository.StudentRepository
import com.quizmaster.app.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstructorViewModel @Inject constructor(
    private val instructorRepo: InstructorRepository,
    private val studentRepo: StudentRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _instructor = MutableLiveData<InstructorEntity?>()
    val instructor: LiveData<InstructorEntity?> = _instructor

    private val _uiEvent = MutableLiveData<String>()
    val uiEvent: LiveData<String> = _uiEvent

    val unassignedStudents = studentRepo.getUnassignedStudents()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun loadProfile() {
        viewModelScope.launch {
            val i = instructorRepo.getByUserId(session.currentUserId)
            _instructor.value = i
            i?.let { session.displayName = "${it.firstName} ${it.lastName}" }
        }
    }

    fun createProfile(firstName: String, lastName: String) {
        if (firstName.isBlank() || lastName.isBlank()) {
            _uiEvent.value = "First name and last name are required."
            return
        }
        viewModelScope.launch {
            val i = instructorRepo.createProfile(
                userId = session.currentUserId,
                firstName = firstName,
                lastName = lastName
            )
            _instructor.value = i
            session.displayName = "$firstName $lastName"
        }
    }

    fun updateProfile(firstName: String, lastName: String) {
        val current = _instructor.value ?: return
        viewModelScope.launch {
            instructorRepo.updateProfile(current.copy(firstName = firstName, lastName = lastName))
            loadProfile()
        }
    }

    fun deleteProfile() {
        viewModelScope.launch {
            _instructor.value?.let { instructorRepo.deleteProfile(it) }
            _instructor.value = null
        }
    }

    fun getMyStudents(instructorId: Int) =
        studentRepo.getStudentsByInstructor(instructorId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun assignStudentToCurrentInstructor(studentId: Int) {
        viewModelScope.launch {
            val instructor = _instructor.value ?: instructorRepo.getByUserId(session.currentUserId)
            if (instructor == null) {
                _uiEvent.value = "Create your instructor profile before assigning students."
                return@launch
            }
            val assigned = studentRepo.assignStudentToInstructor(studentId, instructor.instructorId)
            _uiEvent.value = if (assigned) {
                "Student assigned successfully."
            } else {
                "Unable to assign student."
            }
        }
    }
}
