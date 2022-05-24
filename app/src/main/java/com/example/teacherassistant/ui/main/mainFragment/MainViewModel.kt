package com.example.teacherassistant.ui.main.mainFragment

import androidx.lifecycle.ViewModel
import com.example.teacherassistant.common.Group
import com.example.teacherassistant.common.GroupsState
import com.example.teacherassistant.domain.use_cases.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
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

    fun getGroupList(
        collectionFirstPath: String,
        collectionSecondPath: String
    ) {

        val groups = mutableListOf<Group>()
        getUserUid()?.let { uid ->
            getGroupInfoUseCase.getCollection(
                collectionFirstPath,
                uid,
                collectionSecondPath
            ).addSnapshotListener { value, error ->
                if (value != null) {
                    for (group in value) {
                        getGroupInfoUseCase.getDocument(
                            collectionFirstPath,
                            uid,
                            collectionSecondPath,
                            group.id
                        ).get().addOnSuccessListener { groupInfo ->
                            val name = groupInfo.getString("Name")
                            val title = groupInfo.getString("Title")
                            if (name != null && title != null) {
                                groups.add(Group(name, title, group.id))
                                groupsList.value = GroupsState(groups)
                            }
                        }
                    }
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
        collectionThirdPath: String
    ) {
        getCollectionReferenceForUserInfoUseCase.getCollectionReference(collectionFirstPath)
            .get().addOnSuccessListener { documents ->
                for (user in documents) {
                    getDocumentReferenceForUserInfoUseCase.getDocumentReferenceForUserInfo(
                        collectionFirstPath,
                        user.id
                    ).get().addOnSuccessListener {
                        if (it.getString("Email") == email) {
                            val token = it.getString("Token")
                            if (token != null) {
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
                                }
                            }
                        }
                    }
                }
            }
    }

    fun getStudentsGroups(
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String
    ) {
        getCollectionReferenceForUserInfoUseCase.getCollectionReference(collectionFirstPath)
            .whereEqualTo("isTeacher", "1").get()
            .addOnSuccessListener { userDocuments ->
                for (user in userDocuments) {
                    findGroupsWithCurrentStudent(
                        collectionFirstPath,
                        collectionSecondPath,
                        collectionThirdPath,
                        user
                    )
                }
            }
    }

    private fun findGroupsWithCurrentStudent(
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        user: QueryDocumentSnapshot
    ) {
        getGroupInfoUseCase.getCollection(
            collectionFirstPath,
            user.id,
            collectionSecondPath
        )
            .get().addOnSuccessListener { groupDocuments ->
                for (group in groupDocuments) {
                    getNoteInfoUseCase.getCollectionReference(
                        collectionFirstPath,
                        user.id,
                        collectionSecondPath,
                        group.id,
                        collectionThirdPath
                    ).get().addOnSuccessListener { studentDocuments ->
                        for (student in studentDocuments) {
                            if (student.id == getUserEmail()) {
                                copyAllGroupInfo(
                                    group,
                                    collectionFirstPath,
                                    collectionSecondPath,
                                    user
                                )
                            }
                        }
                    }
                }
            }
    }

    private fun copyAllGroupInfo(
        group: QueryDocumentSnapshot,
        collectionFirstPath: String,
        collectionSecondPath: String,
        user: QueryDocumentSnapshot
    ) {
        getGroupInfoUseCase.getDocument(
            collectionFirstPath,
            user.id,
            collectionSecondPath,
            group.id
        ).get()
            .addOnSuccessListener {
                setGroupInfo(
                    group,
                    collectionFirstPath,
                    collectionSecondPath,
                    it
                )
                setNotesToStudent(
                    getNoteInfoUseCase.getCollectionReference(
                        collectionFirstPath,
                        user.id,
                        collectionSecondPath,
                        group.id,
                        "Notes"
                    ),
                    group,
                    collectionFirstPath,
                    collectionSecondPath
                )
            }
    }

    private fun setGroupInfo(
        group: QueryDocumentSnapshot,
        collectionFirstPath: String,
        collectionSecondPath: String,
        originalGroup: DocumentSnapshot
    ) {
        val newInfo: MutableMap<String, Any?> =
            mutableMapOf()
        newInfo["Name"] =
            originalGroup.getString("Name")
        newInfo["Title"] =
            originalGroup.getString("Title")
        getUserUid()?.let {
            getGroupInfoUseCase.getDocument(
                collectionFirstPath,
                it,
                collectionSecondPath,
                group.id
            )
                .set(newInfo)
        }
    }

    private fun setNotesToStudent(
        collection: CollectionReference,
        group: QueryDocumentSnapshot,
        collectionFirstPath: String,
        collectionSecondPath: String
    ) {
        collection.get()
            .addOnSuccessListener { notes ->
                for (note in notes) {
                    getUserUid()?.let {
                        getNoteInfoUseCase.getDocumentReference(
                            collectionFirstPath,
                            it,
                            collectionSecondPath,
                            group.id,
                            "Notes",
                            note.id
                        ).set(note.data)
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