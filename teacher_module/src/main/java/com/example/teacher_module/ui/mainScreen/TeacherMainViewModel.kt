package com.example.teacher_module.ui.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common_module.common.Constants
import com.example.common_module.domain.use_cases.GetCollectionReferenceForUserInfoUseCase
import com.example.common_module.domain.use_cases.GetGroupInfoUseCase
import com.example.common_module.domain.use_cases.GetNoteStudentsInfoUseCase
import com.example.common_module.domain.use_cases.GetUserUidUseCase
import com.example.common_module.ui.mainScreen.Group
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TeacherMainViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getGroupInfoUseCase: GetGroupInfoUseCase,
    private val getNoteInfoUseCase: GetNoteStudentsInfoUseCase,
    private val getCollectionReferenceForUserInfoUseCase: GetCollectionReferenceForUserInfoUseCase
) : ViewModel() {

    private fun getUserUid(): String? = getUserUidUseCase.getUserUid()

    fun createGroup(
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupName: String,
        title: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val groupInfo: MutableMap<String, Any> = mutableMapOf()
            groupInfo[Constants.NAME] = groupName
            groupInfo[Constants.TITLE] = title
            groupInfo[Constants.TEACHER_ID] = getUserUid().toString()
            getUserUid()?.let { it1 ->
                getGroupInfoUseCase.getDocumentReference(
                    collectionFirstPath,
                    it1,
                    collectionSecondPath,
                    it1 + groupName
                ).set(groupInfo)
            }
        }
    }

    fun addStudent(
        email: String,
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        group: Group
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            getCollectionReferenceForUserInfoUseCase.getCollectionReference(collectionFirstPath)
                .get().addOnSuccessListener { documents ->
                    for (user in documents) {
                        if (user.data[Constants.EMAIL].toString() == email) {
                            val token = user.data[Constants.TOKEN].toString()
                            val studentInfo: MutableMap<String, Any> = mutableMapOf()
                            studentInfo[Constants.TOKEN] = token
                            getUserUid()?.let { it1 ->
                                getNoteInfoUseCase.getDocumentReference(
                                    collectionFirstPath,
                                    it1,
                                    collectionSecondPath,
                                    group.id,
                                    collectionThirdPath,
                                    email
                                ).set(studentInfo)
                                setGroupsToStudents(
                                    collectionFirstPath,
                                    collectionSecondPath,
                                    Constants.COLLECTION_THIRD_PATH,
                                    group.id,
                                    email,
                                    group.title,
                                    group.name
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun setGroupsToStudents(
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        groupId: String,
        email: String,
        title: String,
        name: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            getCollectionReferenceForUserInfoUseCase.getCollectionReference(collectionFirstPath)
                .get()
                .addOnSuccessListener { users ->
                    for (user in users) {
                        if (user.data[Constants.EMAIL].toString() == email) {
                            getGroupInfoUseCase.getCollectionReference(
                                collectionFirstPath,
                                user.id,
                                collectionSecondPath
                            ).get().addOnSuccessListener {
                                val groupInfo: MutableMap<String, Any> = mutableMapOf()
                                groupInfo[Constants.NAME] = name
                                groupInfo[Constants.TITLE] = title
                                groupInfo[Constants.TEACHER_ID] = getUserUid().toString()
                                getGroupInfoUseCase.getDocumentReference(
                                    collectionFirstPath,
                                    user.id,
                                    collectionSecondPath,
                                    groupId
                                ).set(groupInfo)
                            }
                            getUserUid()?.let { uid ->
                                getNoteInfoUseCase.getCollectionReference(
                                    collectionFirstPath,
                                    uid,
                                    collectionSecondPath,
                                    groupId,
                                    collectionThirdPath
                                ).get().addOnSuccessListener { notes ->
                                    for (note in notes) {
                                        getNoteInfoUseCase.getDocumentReference(
                                            collectionFirstPath,
                                            uid,
                                            collectionSecondPath,
                                            groupId,
                                            collectionThirdPath,
                                            note.id
                                        ).set(note.data)
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    fun deleteGroup(
        collectionFirstPath: String,
        collectionSecondPath: String,
        group: Group
    ) {
        getUserUid()?.let {
            getGroupInfoUseCase.getDocumentReference(
                collectionFirstPath,
                it, collectionSecondPath, group.id
            ).delete().addOnSuccessListener {
                deleteGroupFromStudents(collectionFirstPath, collectionSecondPath, group)
            }
        }
    }

    private fun deleteGroupFromStudents(
        collectionFirstPath: String,
        collectionSecondPath: String,
        group: Group
    ) {
        getUserUid()?.let {
            getNoteInfoUseCase.getCollectionReference(
                collectionFirstPath,
                it,
                collectionSecondPath,
                group.id,
                Constants.COLLECTION_THIRD_PATH_STUDENTS
            ).get().addOnSuccessListener { students ->
                for (student in students) {
                    getCollectionReferenceForUserInfoUseCase.getCollectionReference(
                        collectionFirstPath
                    ).get().addOnSuccessListener { users ->
                        for (user in users) {
                            if (user.data[Constants.EMAIL] == student.id) {
                                getGroupInfoUseCase.getDocumentReference(
                                    collectionFirstPath,
                                    user.id,
                                    collectionSecondPath,
                                    group.id
                                ).delete()
                            }
                        }
                    }
                }
            }
        }
    }

}