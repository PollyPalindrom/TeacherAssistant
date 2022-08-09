package com.example.teacher_module.ui.studentsList

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.common_module.domain.use_cases.GetNoteStudentsInfoUseCase
import com.example.common_module.domain.use_cases.GetUserUidUseCase
import com.example.common_module.ui.studentsList.StudentsState
import javax.inject.Inject

class StudentsViewModel @Inject constructor(
    private val getNoteInfoUseCase: GetNoteStudentsInfoUseCase,
    private val getUserUidUseCase: GetUserUidUseCase
) :
    ViewModel() {

    private val studentsList = mutableStateOf(StudentsState())
    val studentsListOpen: State<StudentsState> = studentsList

    private fun getUserUid(): String? = getUserUidUseCase.getUserUid()

    fun subscribeStudentListChanges(
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String
    ) {
        getUserUid()?.let {
            getStudentsList(
                collectionFirstPath, collectionSecondPath, groupId, collectionThirdPath,
                it
            )
        }
    }

    private fun getStudentsList(
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        uid: String
    ) {
        getNoteInfoUseCase.getCollectionReference(
            collectionFirstPath,
            uid,
            collectionSecondPath,
            groupId,
            collectionThirdPath
        ).addSnapshotListener { value, error ->
            val studentsListTemp = mutableListOf<String>()
            if (value != null) {
                for (student in value) {
                    studentsListTemp.add(student.id)
                }
                studentsList.value = StudentsState(studentsListTemp)
            }
            if (error != null) {
                studentsList.value =
                    StudentsState(error = error.localizedMessage)
            }
        }
    }

    fun deleteStudent(
        email: String,
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        groupId: String
    ) {
        getUserUid()?.let {
            getNoteInfoUseCase.getDocumentReference(
                collectionFirstPath,
                it, collectionSecondPath, groupId, collectionThirdPath, email
            ).delete().addOnSuccessListener {
                val newList = studentsList.value.students as MutableList<String>
                newList.remove(email)
                studentsList.value = StudentsState(newList)
            }
        }
    }
}