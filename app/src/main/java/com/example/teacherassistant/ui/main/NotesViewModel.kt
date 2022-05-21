package com.example.teacherassistant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherassistant.common.*
import com.example.teacherassistant.domain.use_cases.GetNoteInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserUidUseCase
import com.example.teacherassistant.domain.use_cases.PostNotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getNoteInfoUseCase: GetNoteInfoUseCase,
    private val postNotificationUseCase: PostNotificationUseCase
) : ViewModel() {
    private val noteList: MutableStateFlow<NotesState?> = MutableStateFlow(null)
    val noteListOpen: StateFlow<NotesState?> = noteList

    private val notificationState: MutableStateFlow<NotificationState?> = MutableStateFlow(null)
    val notificationStateOpen: StateFlow<NotificationState?> = notificationState

    private fun getUserUid(): String? {
        return getUserUidUseCase.getUserUid()

    }

    fun createNote(
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        groupId: String,
        title: String,
        text: String,
        topic: String
    ) {
        val noteInfo: MutableMap<String, Any> = mutableMapOf()
        noteInfo["Title"] = title
        noteInfo["Text"] = text
        getUserUid()?.let { it1 ->
            getNoteInfoUseCase.getDocumentReference(
                collectionFirstPath,
                it1,
                collectionSecondPath,
                groupId,
                collectionThirdPath,
                groupId + title
            ).set(noteInfo)
        }
        postNotification(title, topic, text)
    }

    fun getNoteList(
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
    ) {

        val notesId = mutableListOf<String>()
        val notes = mutableListOf<Note>()
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
                                notes.add(Note(text, title, it))
                                noteList.value = NotesState(notes)
                            }
                        }
                    }
                }
                if (error?.localizedMessage != null) noteList.value =
                    NotesState(error = error.localizedMessage)
            }
        }
    }

    private fun postNotification(title: String, topic: String, message: String) {
        val notification = PushNotification(NotificationData(title, message), topic)
        viewModelScope.launch(Dispatchers.IO) {
            postNotificationUseCase.invoke(notification).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        notificationState.value = NotificationState(result.data)
                    }
                    is Resource.Error -> {
                        notificationState.value = NotificationState(
                            error = result.message ?: "An unexpected error occurred"
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}