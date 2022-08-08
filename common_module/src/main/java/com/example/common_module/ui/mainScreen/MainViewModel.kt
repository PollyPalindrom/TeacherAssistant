package com.example.common_module.ui.mainScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common_module.common.Constants
import com.example.common_module.domain.use_cases.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getGroupInfoUseCase: GetGroupInfoUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getNoteInfoUseCase: GetNoteStudentsInfoUseCase,
    private val getCollectionReferenceForUserInfoUseCase: GetCollectionReferenceForUserInfoUseCase,
    private val getDocumentReferenceForUserInfoUseCase: GetDocumentReferenceForUserInfoUseCase
) :
    ViewModel() {

    private val groupsList = mutableStateOf(GroupsState())
    val groupsListOpen: State<GroupsState> = groupsList

    private fun getUserUid(): String? = getUserUidUseCase.getUserUid()

    private fun getUserEmail(): String? = getUserInfoUseCase.getUserEmail()

    private fun updateGroups(
        value: QuerySnapshot
    ) {
        val groups = mutableListOf<Group>()
        for (group in value) {
            var edit = false
            if (group.data[Constants.TEACHER_ID] == getUserUid()) edit =
                true
            groups.add(
                Group(
                    group.data[Constants.NAME].toString(),
                    group.data[Constants.TITLE].toString(),
                    group.id,
                    edit
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
                getGroupInfoUseCase.getCollectionReference(
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
                            getDocumentReferenceForUserInfoUseCase.getDocumentReference(
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
                    studentInfo[Constants.EMAIL] =
                        it.getString(Constants.EMAIL)
                    studentInfo[Constants.FULL_NAME] =
                        it.getString(
                            Constants.FULL_NAME
                        )
                    studentInfo[Constants.STATUS] = it.getString(
                        Constants.STATUS
                    )
                    dr.set(studentInfo)
                }
                if (it.getString(Constants.STATUS) == Constants.POSITIVE_STAT) {
                    getGroupInfoUseCase.getCollectionReference(
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
}