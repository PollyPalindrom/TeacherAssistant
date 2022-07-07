package com.example.teacherassistant.ui.main.notesFragment

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherassistant.common.*
import com.example.teacherassistant.domain.use_cases.*
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
    private val getCollectionReferenceForUserInfoUseCase: GetCollectionReferenceForUserInfoUseCase,
    private val uploadPictureUseCase: UploadPictureUseCase
) : ViewModel() {
    private val noteList = mutableStateOf(NotesState())
    val noteListOpen: State<NotesState?> = noteList

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
        uris: List<Uri>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val noteInfo: MutableMap<String, Any> = mutableMapOf()
            noteInfo[Constants.TITLE] = title
            noteInfo[Constants.TEXT] = text
            getUserUid()?.let { it1 ->
                getNoteInfoUseCase.getDocumentReference(
                    collectionFirstPath,
                    it1,
                    collectionSecondPath,
                    groupId,
                    collectionThirdPath,
                    groupId + title
                ).set(noteInfo)
                // необходимо создать юзкейс, чтобы создать у заметки коллекцию, в которой будут документы с url пикч
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
                    Constants.COLLECTION_THIRD_PATH_STUDENTS
                ).get().addOnSuccessListener { valueStudents ->
                    for (student in valueStudents) {
                        postNotification(title, student.data[Constants.TOKEN].toString(), text)
                    }
                }
            }
        }
    }

    private fun uploadPictures(uris: List<Uri>) {
        viewModelScope.launch(Dispatchers.IO) {
            uris.forEach { uri ->
                uri.lastPathSegment?.let {lastPathSegment->
                    uploadPictureUseCase.getUploadPictureTask(uri, lastPathSegment).addOnSuccessListener {
                        uploadPictureUseCase.getResultUriTask(lastPathSegment).addOnSuccessListener {

                        }
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
                    Constants.COLLECTION_THIRD_PATH_STUDENTS
                ).get().addOnSuccessListener { students ->
                    for (student in students) {
                        getCollectionReferenceForUserInfoUseCase.getCollectionReference(
                            collectionFirstPath
                        ).get().addOnSuccessListener { users ->
                            for (user in users) {
                                if (user.data[Constants.EMAIL] == student.id) {
                                    getNoteInfoUseCase.getDocumentReference(
                                        collectionFirstPath,
                                        user.id,
                                        collectionSecondPath,
                                        groupId,
                                        collectionThirdPath,
                                        groupId + noteInfo[Constants.TITLE]
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
        collectionThirdPath: String
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
                            val title = note.data[Constants.TITLE].toString()
                            val text = note.data[Constants.TEXT].toString()
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
                    else -> {
                        notificationState.value = NotificationState(
                            error = result.message ?: Constants.UNEXPECTED_ERROR
                        )
                    }

                }
            }.launchIn(viewModelScope)
        }
    }

    fun deleteNote(
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        note: Note,
        groupId: String
    ) {
        getUserUid()?.let {
            getNoteInfoUseCase.getDocumentReference(
                collectionFirstPath,
                it,
                collectionSecondPath,
                groupId,
                collectionThirdPath,
                note.id
            ).delete().addOnSuccessListener {
                val newList = noteList.value.notes as MutableList<Note>
                newList.remove(note)
                noteList.value = NotesState(newList)
                deleteNoteFromStudents(
                    collectionFirstPath,
                    collectionSecondPath,
                    collectionThirdPath,
                    note,
                    groupId
                )
            }
        }
    }

    private fun deleteNoteFromStudents(
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        note: Note,
        groupId: String
    ) {
        getUserUid()?.let {
            getNoteInfoUseCase.getCollectionReference(
                collectionFirstPath,
                it,
                collectionSecondPath,
                groupId,
                Constants.COLLECTION_THIRD_PATH_STUDENTS
            ).get().addOnSuccessListener { students ->
                for (student in students) {
                    getCollectionReferenceForUserInfoUseCase.getCollectionReference(
                        collectionFirstPath
                    ).get().addOnSuccessListener { users ->
                        for (user in users) {
                            if (user.data[Constants.EMAIL] == student.id) {
                                getNoteInfoUseCase.getDocumentReference(
                                    collectionFirstPath,
                                    user.id,
                                    collectionSecondPath,
                                    groupId,
                                    collectionThirdPath,
                                    groupId + note.title
                                ).delete()
                            }
                        }

                    }
                }
            }
        }
    }
}