package com.example.teacherassistant.ui.main

import androidx.lifecycle.ViewModel
import com.example.teacherassistant.common.Group
import com.example.teacherassistant.common.GroupsState
import com.example.teacherassistant.domain.use_cases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getDocumentReferenceForGroupInfoUseCase: GetDocumentReferenceForGroupInfoUseCase,
    private val getCollectionReferenceForGroupInfoUseCase: GetCollectionReferenceForGroupInfoUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getNoteInfoUseCase: GetNoteInfoUseCase,
    private val getCollectionReferenceForUserInfoUseCase: GetCollectionReferenceForUserInfoUseCase,
    private val getDocumentReferenceForUserInfoUseCase: GetDocumentReferenceForUserInfoUseCase
) :
    ViewModel() {
    private val groupsList: MutableStateFlow<GroupsState?> = MutableStateFlow(null)
    val groupsListOpen: StateFlow<GroupsState?> = groupsList

    fun getUserUid(): String? {
        return getUserUidUseCase.getUserUid()

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
            getDocumentReferenceForGroupInfoUseCase.getDocumentReferenceForGroupInfo(
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

        val groupsId = mutableListOf<String>()
        val groups = mutableListOf<Group>()
        getUserUid()?.let { it2 ->
            getCollectionReferenceForGroupInfoUseCase.getCollection(
                collectionFirstPath,
                it2,
                collectionSecondPath
            ).addSnapshotListener { value, error ->
                if (value != null) {
                    for (group in value) {
                        groupsId.add(group.id)
                    }
                    groupsId.forEach {
                        getDocumentReferenceForGroupInfoUseCase.getDocumentReferenceForGroupInfo(
                            collectionFirstPath,
                            it2,
                            collectionSecondPath,
                            it
                        ).get().addOnSuccessListener { it1 ->
                            val name = it1.getString("Name")
                            val title = it1.getString("Title")
                            if (name != null && title != null) {
                                groups.add(Group(name, title, it))
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

    fun checkState(): Boolean {
        return getUserInfoUseCase.getUserState()
    }

    fun addStudent(
        email: String,
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String
    ) {
        //сначала ищем в списке пользователей введённый email, если он сходится, берём токен, добавляем его как информационное поле в коллекции студентов
        getCollectionReferenceForUserInfoUseCase.getCollectionReference(collectionFirstPath)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    for (user in value) {
                        getDocumentReferenceForUserInfoUseCase.getDocumentReferenceForUserInfo(
                            "User",
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
    }
}