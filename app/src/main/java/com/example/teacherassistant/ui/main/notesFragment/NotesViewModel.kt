package com.example.teacherassistant.ui.main.notesFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherassistant.common.*
import com.example.teacherassistant.domain.use_cases.GetCollectionReferenceForUserInfoUseCase
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
    private val postNotificationUseCase: PostNotificationUseCase,
    private val getCollectionReferenceForUserInfoUseCase: GetCollectionReferenceForUserInfoUseCase
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
        text: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
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
            updateStudentNotes(
                collectionFirstPath,
                collectionSecondPath,
                collectionThirdPath,
                groupId,
                noteInfo
            )
            getUserUid()?.let { id ->
                getNoteInfoUseCase.getCollectionReference(
                    collectionFirstPath,
                    id,
                    collectionSecondPath,
                    groupId,
                    "Students"
                ).get().addOnSuccessListener { valueStudents ->
                    for (student in valueStudents) {
                        postNotification(title, student.data["Token"].toString(), text)
                    }
                }
            }
        }
    }


    private fun updateStudentNotes(
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        groupId: String,
        noteInfo: MutableMap<String, Any>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserUid()?.let {
                getNoteInfoUseCase.getCollectionReference(
                    collectionFirstPath,
                    it,
                    collectionSecondPath,
                    groupId,
                    "Students"
                ).get().addOnSuccessListener { students ->
                    for (student in students) {
                        getCollectionReferenceForUserInfoUseCase.getCollectionReference(
                            collectionFirstPath
                        ).get().addOnSuccessListener { users ->
                            for (user in users) {
                                if (user.data["Email"] == student.id) {
                                    getNoteInfoUseCase.getDocumentReference(
                                        collectionFirstPath,
                                        user.id,
                                        collectionSecondPath,
                                        groupId,
                                        collectionThirdPath,
                                        groupId + noteInfo["Title"]
                                    ).set(noteInfo)
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    fun subscribeNoteListChanges(
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserUid()?.let { uid ->
                getNoteInfoUseCase.getCollectionReference(
                    collectionFirstPath,
                    uid,
                    collectionSecondPath,
                    groupId,
                    collectionThirdPath
                ).addSnapshotListener { value, error ->
                    println(value?.size())
                    if (value != null) {
                        val notes = mutableListOf<Note>()
                        for (note in value) {
                            println(note.data)
                            val title = note.data["Title"].toString()
                            val text = note.data["Text"].toString()
                            notes.add(Note(title, text, note.id))
                        }
                        noteList.value = NotesState(notes)
                        println(notes)
                    }
                    if (error?.localizedMessage != null) noteList.value =
                        NotesState(error = error.localizedMessage)
                }
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