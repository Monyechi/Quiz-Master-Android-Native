package com.quizmaster.app.ui.student

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
class StudentViewModel @Inject constructor(
    private val studentRepo: StudentRepository,
    private val instructorRepo: InstructorRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _student = MutableLiveData<StudentEntity?>()
    val student: LiveData<StudentEntity?> = _student

    private val _uiEvent = MutableLiveData<String>()
    val uiEvent: LiveData<String> = _uiEvent

    val allInstructors = instructorRepo.getAllInstructors()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun loadProfile() {
        viewModelScope.launch {
            val s = studentRepo.getByUserId(session.currentUserId)
            _student.value = s
            s?.let { session.displayName = it.displayName }
        }
    }

    fun createProfile(firstName: String, lastName: String, displayName: String, grade: String) {
        if (firstName.isBlank() || lastName.isBlank() || displayName.isBlank()) {
            _uiEvent.value = "All fields are required."
            return
        }
        viewModelScope.launch {
            val s = studentRepo.createProfile(
                userId = session.currentUserId,
                firstName = firstName,
                lastName = lastName,
                displayName = displayName,
                grade = grade
            )
            _student.value = s
            session.displayName = displayName
        }
    }

    fun updateProfile(firstName: String, lastName: String, displayName: String, grade: String) {
        val current = _student.value ?: return
        viewModelScope.launch {
            studentRepo.updateProfile(
                current.copy(
                    firstName = firstName,
                    lastName = lastName,
                    displayName = displayName,
                    grade = grade
                )
            )
            loadProfile()
        }
    }

    fun enrollWithInstructor(instructorKey: String) {
        viewModelScope.launch {
            val instructor = instructorRepo.getByKey(instructorKey)
            if (instructor == null) {
                _uiEvent.value = "Instructor not found. Check the key and try again."
                return@launch
            }
            val student = _student.value ?: return@launch
            studentRepo.enrollWithInstructor(student, instructor.instructorId)
            loadProfile()
            _uiEvent.value = "Enrolled with ${instructor.firstName} ${instructor.lastName}!"
        }
    }

    fun enrollWithInstructorById(instructorId: Int) {
        viewModelScope.launch {
            val instructor = instructorRepo.getById(instructorId) ?: return@launch
            val student = _student.value ?: return@launch
            studentRepo.enrollWithInstructor(student, instructor.instructorId)
            loadProfile()
            _uiEvent.value = "Enrolled with ${instructor.firstName} ${instructor.lastName}!"
        }
    }

    fun deleteProfile() {
        viewModelScope.launch {
            _student.value?.let { studentRepo.deleteProfile(it) }
            _student.value = null
        }
    }
}
