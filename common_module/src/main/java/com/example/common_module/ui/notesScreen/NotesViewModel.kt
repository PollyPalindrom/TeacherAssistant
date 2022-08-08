package com.example.common_module.ui.notesScreen

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common_module.common.Constants
import com.example.common_module.domain.use_cases.GetNoteStudentsInfoUseCase
import com.example.common_module.domain.use_cases.GetPictureCommentInfoUseCase
import com.example.common_module.domain.use_cases.GetUserUidUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotesViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getNoteInfoUseCase: GetNoteStudentsInfoUseCase,
    private val getPictureInfoUseCase: GetPictureCommentInfoUseCase
) : ViewModel() {

    private val noteList = mutableStateOf(NotesState())
    val noteListOpen: State<NotesState?> = noteList

    private fun getUserUid(): String? {
        return getUserUidUseCase.getUserUid()
    }


    fun getUrisList(
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String,
        setUris: (urisList: List<Uri>) -> Unit
    ) {
        getUserUid()?.let { userUid ->
            getPictureInfoUseCase.getCollectionReference(
                collectionFirstPath,
                userUid,
                collectionSecondPath,
                groupId,
                collectionThirdPath,
                noteId,
                collectionForthPath
            ).get().addOnSuccessListener { pictures ->
                setUris(pictures.map { Uri.parse(it.id.replace("|", "/")) })
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
                    if (value != null) {
                        val notes = mutableListOf<Note>()
                        for (note in value) {
                            val title =
                                note.data[Constants.TITLE].toString()
                            val text =
                                note.data[Constants.TEXT].toString()
                            notes.add(Note(title, text, note.id))
                        }
                        noteList.value = NotesState(notes)
                    }
                    if (error?.localizedMessage != null) noteList.value =
                        NotesState(error = error.localizedMessage)
                }
            }
        }
    }
}