package com.example.teacherassistant.ui.main

import androidx.lifecycle.ViewModel
import com.example.teacherassistant.common.Group
import com.example.teacherassistant.common.GroupsState
import com.example.teacherassistant.domain.use_cases.GetNoteInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getNoteInfoUseCase: GetNoteInfoUseCase
) : ViewModel() {
    private val noteList: MutableStateFlow<GroupsState?> = MutableStateFlow(null)
    val noteListOpen: StateFlow<GroupsState?> = noteList

    private fun getUserUid(): String? {
        return getUserUidUseCase.getUserUid()

    }

    fun createGroup(
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        groupId: String,
        title: String,
        text: String
    ) {
        val groupInfo: MutableMap<String, Any> = mutableMapOf()
        groupInfo["Title"] = title
        groupInfo["Text"] = text
        getUserUid()?.let { it1 ->
            getNoteInfoUseCase.getDocumentReference(
                collectionFirstPath,
                it1,
                collectionSecondPath,
                groupId,
                collectionThirdPath,
                groupId + title
            ).set(groupInfo)
        }
    }

    fun getGroupList(
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
    ) {

        val notesId = mutableListOf<String>()
        val notes = mutableListOf<Group>()
        getUserUid()?.let { it2 ->
            getNoteInfoUseCase.getCollectionReference(
                collectionFirstPath,
                it2,
                collectionSecondPath,
                groupId,
                collectionThirdPath
            ).addSnapshotListener { value, error ->
                if (value != null) {
                    for (note in value) {
                        notesId.add(note.id)
                    }
                    notesId.forEach {
                        getNoteInfoUseCase.getDocumentReference(
                            collectionFirstPath,
                            it2,
                            collectionSecondPath,
                            groupId,
                            collectionThirdPath,
                            it
                        ).get().addOnSuccessListener { it1 ->
                            val text = it1.getString("Text")
                            val title = it1.getString("Title")
                            if (text != null && title != null) {
                                notes.add(Group(text, title, it))
                                noteList.value = GroupsState(notes)
                            }
                        }
                    }
                }
                if (error?.localizedMessage != null) noteList.value =
                    GroupsState(error = error.localizedMessage)
            }
        }
    }
}