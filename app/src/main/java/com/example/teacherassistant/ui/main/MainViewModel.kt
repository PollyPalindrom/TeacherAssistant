package com.example.teacherassistant.ui.main

import androidx.lifecycle.ViewModel
import com.example.teacherassistant.common.Group
import com.example.teacherassistant.common.GroupsState
import com.example.teacherassistant.domain.use_cases.GetCollectionReferenceForGroupInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetDocumentReferenceForGroupInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getDocumentReferenceForGroupInfoUseCase: GetDocumentReferenceForGroupInfoUseCase,
    private val getCollectionReferenceForGroupInfoUseCase: GetCollectionReferenceForGroupInfoUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) :
    ViewModel() {
    private val groupsList: MutableStateFlow<GroupsState?> = MutableStateFlow(null)
    val groupsListOpen: StateFlow<GroupsState?> = groupsList

    private fun getUserUid(): String? {
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
}