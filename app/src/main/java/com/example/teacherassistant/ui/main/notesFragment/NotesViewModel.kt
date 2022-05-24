package com.example.teacherassistant.ui.main.notesFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherassistant.common.*
import com.example.teacherassistant.domain.use_cases.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
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
    private val getDocumentReferenceForUserInfoUseCase: GetDocumentReferenceForUserInfoUseCase,
    private val getCollectionReferenceForUserInfoUseCase: GetCollectionReferenceForUserInfoUseCase
) : ViewModel() {
    private val noteList: MutableStateFlow<NotesState?> = MutableStateFlow(null)
    val noteListOpen: StateFlow<NotesState?> = noteList

    private val notificationState: MutableStateFlow<NotificationState?> = MutableStateFlow(null)
    val notificationStateOpen: StateFlow<NotificationState?> = notificationState

    private fun getUserUid(): String? {
        return getUserUidUseCase.getUserUid()

    }

    private fun getCollectionReferenceForUserInfo(collectionPath: String): CollectionReference? {
        return getUserUid()?.let {
            getCollectionReferenceForUserInfoUseCase.getCollectionReference(
                collectionPath
            )
        }
    }

    private fun getDocumentReferenceForUserInfo(
        collectionPath: String,
        uid: String
    ): DocumentReference? {
        return getUserUid()?.let {
            getDocumentReferenceForUserInfoUseCase.getDocumentReferenceForUserInfo(
                collectionPath,
                uid
            )
        }
    }

    fun createNote(
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        groupId: String,
        title: String,
        text: String
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
        getUserUid()?.let { id ->
            getNoteInfoUseCase.getCollectionReference(
                collectionFirstPath,
                id,
                collectionSecondPath,
                groupId,
                "Students"
            ).addSnapshotListener { valueStudents, errorStudents ->
                if (valueStudents != null) {
                    for (student in valueStudents) {
                        getCollectionReferenceForUserInfo(collectionFirstPath)?.addSnapshotListener { value, error ->
                            if (value != null) {
                                for (user in value) {
                                    getDocumentReferenceForUserInfo(
                                        collectionFirstPath,
                                        user.id
                                    )?.get()
                                        ?.addOnSuccessListener {
                                            if (it.getString("Email") == student.id) {
                                                val token = it.getString("Token")
                                                if (token != null) {
                                                    postNotification(title, token, text)
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getNoteList(
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
    ) {

        val notes = mutableListOf<Note>()
        getUserUid()?.let { uid ->
            getNoteInfoUseCase.getCollectionReference(
                collectionFirstPath,
                uid,
                collectionSecondPath,
                groupId,
                collectionThirdPath
            ).addSnapshotListener { value, error ->
                if (value != null) {
                    for (note in value) {
                        getNoteInfoUseCase.getDocumentReference(
                            collectionFirstPath,
                            uid,
                            collectionSecondPath,
                            groupId,
                            collectionThirdPath,
                            note.id
                        ).get().addOnSuccessListener { noteInfo ->
                            val text = noteInfo.getString("Text")
                            val title = noteInfo.getString("Title")
                            if (text != null && title != null) {
                                notes.add(Note(title, text, note.id))
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