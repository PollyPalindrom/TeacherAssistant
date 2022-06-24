package com.example.teacherassistant.ui.main.mainFragment

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.common.Group
import com.example.teacherassistant.common.GroupsState
import com.example.teacherassistant.common.StudentsParseManager
import com.example.teacherassistant.domain.use_cases.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val groupsList = mutableStateOf(GroupsState())
    val groupsListOpen: State<GroupsState> = groupsList

    private val parseManager = StudentsParseManager

    private fun getUserUid(): String? {
        return getUserUidUseCase.getUserUid()

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
        viewModelScope.launch(Dispatchers.IO) {
            val groupInfo: MutableMap<String, Any> = mutableMapOf()
            groupInfo[Constants.NAME] = groupName
            groupInfo[Constants.TITLE] = title
            groupInfo[Constants.TEACHER_ID] = getUserUid().toString()
            getUserUid()?.let { it1 ->
                getGroupInfoUseCase.getDocument(
                    collectionFirstPath,
                    it1,
                    collectionSecondPath,
                    it1 + groupName
                ).set(groupInfo)
            }
        }
    }

    private fun updateGroups(
        value: QuerySnapshot
    ) {
        val groups = mutableListOf<Group>()
        for (group in value) {
            val students = mutableListOf<String>()
            groups.add(
                Group(
                    group.data[Constants.NAME].toString(),
                    group.data[Constants.TITLE].toString(),
                    group.id,
                    parseStudents(students)
                )
            )
        }
        groupsList.value = GroupsState(groups)
    }

    fun subscribeGroupListChanges(
        collectionFirstPath: String,
        collectionSecondPath: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserUid()?.let { uid ->
                getGroupInfoUseCase.getCollection(
                    collectionFirstPath,
                    uid,
                    collectionSecondPath
                ).addSnapshotListener { value, error ->
                    if (value != null) {
                        updateGroups(value)
                    }
                    if (error?.localizedMessage != null) groupsList.value =
                        GroupsState(error = error.localizedMessage)
                }
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
                                    groupId,
                                    collectionThirdPath,
                                    email
                                ).set(studentInfo)
                                setGroupsToStudents(
                                    collectionFirstPath,
                                    collectionSecondPath,
                                    Constants.COLLECTION_THIRD_PATH,
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
                            getGroupInfoUseCase.getCollection(
                                collectionFirstPath,
                                user.id,
                                collectionSecondPath
                            ).get().addOnSuccessListener {
                                val groupInfo: MutableMap<String, Any> = mutableMapOf()
                                groupInfo[Constants.NAME] = name
                                groupInfo[Constants.TITLE] = title
                                groupInfo[Constants.TEACHER_ID] = getUserUid().toString()
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
    }

    fun setNewToken(
        token: String,
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
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
    }

    private fun setStudentInfo(
        token: String,
        dr: DocumentReference,
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        user: QueryDocumentSnapshot
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dr.get().addOnSuccessListener {
                if (it.getString(Constants.EMAIL) == getUserEmail()) {
                    val studentInfo: MutableMap<String, Any?> = mutableMapOf()
                    studentInfo[Constants.TOKEN] = token
                    studentInfo[Constants.EMAIL] = it.getString(Constants.EMAIL)
                    studentInfo[Constants.FULL_NAME] = it.getString(Constants.FULL_NAME)
                    studentInfo[Constants.STATUS] = it.getString(Constants.STATUS)
                    dr.set(studentInfo)
                }
                if (it.getString(Constants.STATUS) == Constants.POSITIVE_STAT) {
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
    }

    private fun copyToken(
        group: QueryDocumentSnapshot,
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        user: QueryDocumentSnapshot,
        token: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
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
                            studentToken[Constants.TOKEN] = token
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

    private fun parseStudents(students: List<String>): String {
        return parseManager.parseStudents(students)
    }

}