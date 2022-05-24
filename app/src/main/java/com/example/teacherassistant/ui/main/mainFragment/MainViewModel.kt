package com.example.teacherassistant.ui.main.mainFragment

import androidx.lifecycle.ViewModel
import com.example.teacherassistant.common.Group
import com.example.teacherassistant.common.GroupsState
import com.example.teacherassistant.domain.use_cases.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QueryDocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getGroupInfoUseCase: GetGroupInfoUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getNoteInfoUseCase: GetNoteInfoUseCase,
    private val getCollectionReferenceForUserInfoUseCase: GetCollectionReferenceForUserInfoUseCase,
    private val getDocumentReferenceForUserInfoUseCase: GetDocumentReferenceForUserInfoUseCase
) :
    ViewModel() {
    private val groupsList: MutableStateFlow<GroupsState?> = MutableStateFlow(null)
    val groupsListOpen: StateFlow<GroupsState?> = groupsList

    private fun getUserUid(): String? {
        return getUserUidUseCase.getUserUid()

    }

    fun checkState(): Boolean {
        return getUserInfoUseCase.getUserState()
    }

    private fun getUserEmail(): String? {
        return getUserInfoUseCase.getUserEmail()
    }

    fun createGroup(
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupName: String,
        title: String
    ) {
        val groupInfo: MutableMap<String, Any> = mutableMapOf()
        groupInfo["Name"] = groupName
        groupInfo["Title"] = title
        getUserUid()?.let { it1 ->
            getGroupInfoUseCase.getDocument(
                collectionFirstPath,
                it1,
                collectionSecondPath,
                it1 + groupName
            ).set(groupInfo)
        }
    }

    fun subscribeGroupListChanges(
        collectionFirstPath: String,
        collectionSecondPath: String
    ) {
        getUserUid()?.let { uid ->
            getGroupInfoUseCase.getCollection(
                collectionFirstPath,
                uid,
                collectionSecondPath
            ).addSnapshotListener { value, error ->
                if (value != null) {
                    val groups = mutableListOf<Group>()
                    for (group in value) {
                        groups.add(
                            Group(
                                group.data["Name"].toString(),
                                group.data["Title"].toString(),
                                group.id
                            )
                        )
                    }
                    groupsList.value = GroupsState(groups)
                }
                if (error?.localizedMessage != null) groupsList.value =
                    GroupsState(error = error.localizedMessage)
            }
        }
    }

    fun addStudent(
        email: String,
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        title: String,
        name: String
    ) {
        getCollectionReferenceForUserInfoUseCase.getCollectionReference(collectionFirstPath)
            .get().addOnSuccessListener { documents ->
                for (user in documents) {
                    if (user.data["Email"].toString() == email) {
                        val token = user.data["Token"].toString()
                        val studentInfo: MutableMap<String, Any> = mutableMapOf()
                        studentInfo["Token"] = token
                        getUserUid()?.let { it1 ->
                            getNoteInfoUseCase.getDocumentReference(
                                collectionFirstPath,
                                it1,
                                collectionSecondPath,
                                groupId,
                                collectionThirdPath,
                                email
                            ).set(studentInfo)
                            setGroupsToStudents(
                                collectionFirstPath,
                                collectionSecondPath,
                                "Notes",
                                groupId,
                                email,
                                title,
                                name
                            )
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
        getCollectionReferenceForUserInfoUseCase.getCollectionReference(collectionFirstPath).get()
            .addOnSuccessListener { users ->
                for (user in users) {
                    if (user.data["Email"].toString() == email) {
                        getGroupInfoUseCase.getCollection(
                            collectionFirstPath,
                            user.id,
                            collectionSecondPath
                        ).get().addOnSuccessListener {
                            val groupInfo: MutableMap<String, Any> = mutableMapOf()
                            groupInfo["Name"] = name
                            groupInfo["Title"] = title
                            getGroupInfoUseCase.getDocument(
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

    fun setNewToken(
        token: String,
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String
    ) {
        getCollectionReferenceForUserInfoUseCase.getCollectionReference(collectionFirstPath)
            .get().addOnSuccessListener { usersDocuments ->
                for (user in usersDocuments) {
                    val dr =
                        getDocumentReferenceForUserInfoUseCase.getDocumentReferenceForUserInfo(
                            collectionFirstPath,
                            user.id
                        )
                    setStudentInfo(
                        token,
                        dr,
                        collectionFirstPath,
                        collectionSecondPath,
                        collectionThirdPath,
                        user
                    )
                }
            }
    }

    private fun setStudentInfo(
        token: String,
        dr: DocumentReference,
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        user: QueryDocumentSnapshot
    ) {
        dr.get().addOnSuccessListener {
            if (it.getString("Email") == getUserEmail()) {
                val studentInfo: MutableMap<String, Any?> = mutableMapOf()
                studentInfo["Token"] = token
                studentInfo["Email"] = it.getString("Email")
                studentInfo["FullName"] = it.getString("FullName")
                studentInfo["isTeacher"] = it.getString("isTeacher")
                dr.set(studentInfo)
            }
            if (it.getString("isTeacher") == "1") {
                getGroupInfoUseCase.getCollection(
                    collectionFirstPath,
                    user.id,
                    collectionSecondPath
                ).get().addOnSuccessListener { groupDocuments ->
                    for (group in groupDocuments) {
                        copyToken(
                            group,
                            collectionFirstPath,
                            collectionSecondPath,
                            collectionThirdPath,
                            user,
                            token
                        )
                    }
                }
            }
        }
    }

    private fun copyToken(
        group: QueryDocumentSnapshot,
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        user: QueryDocumentSnapshot,
        token: String
    ) {
        getNoteInfoUseCase.getCollectionReference(
            collectionFirstPath,
            user.id,
            collectionSecondPath,
            group.id,
            collectionThirdPath
        )
            .get()
            .addOnSuccessListener { studentsDocuments ->
                for (student in studentsDocuments) {
                    if (getUserEmail() == student.id) {
                        val studentToken =
                            mutableMapOf<String, Any>()
                        studentToken["Token"] = token
                        getNoteInfoUseCase.getDocumentReference(
                            collectionFirstPath,
                            user.id,
                            collectionSecondPath,
                            group.id,
                            collectionThirdPath,
                            student.id
                        ).set(studentToken)
                    }
                }
            }
    }

}