package com.example.teacherassistant.ui.main.studentsList

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.domain.use_cases.GetGroupInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetNoteInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StudentsViewModel @Inject constructor(
    private val getNoteInfoUseCase: GetNoteInfoUseCase,
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getGroupInfoUseCase: GetGroupInfoUseCase
) :
    ViewModel() {

    private val studentsList = mutableStateOf(StudentsState())
    val studentsListOpen: State<StudentsState> = studentsList

    private fun getUserUid(): String? {
        return getUserUidUseCase.getUserUid()
    }

    fun subscribeStudentListChanges(
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        role: String
    ) {
        if (role == Constants.STUDENT) {
            getUserUid()?.let {
                getGroupInfoUseCase.getDocument(
                    collectionFirstPath,
                    it,
                    collectionSecondPath,
                    groupId
                ).get().addOnSuccessListener { group ->
                    getStudentsList(
                        collectionFirstPath,
                        collectionSecondPath,
                        groupId,
                        collectionThirdPath,
                        group.data?.get(Constants.TEACHER_ID).toString()
                    )
                }
            }
        } else {
            getUserUid()?.let {
                getStudentsList(
                    collectionFirstPath, collectionSecondPath, groupId, collectionThirdPath,
                    it
                )
            }
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