package com.example.teacher_module.ui.notesScreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common_module.common.Constants
import com.example.common_module.common.NotificationData
import com.example.common_module.common.PushNotification
import com.example.common_module.domain.use_cases.GetCollectionReferenceForUserInfoUseCase
import com.example.common_module.domain.use_cases.GetNoteStudentsInfoUseCase
import com.example.common_module.domain.use_cases.GetPictureCommentInfoUseCase
import com.example.common_module.domain.use_cases.GetUserUidUseCase
import com.example.common_module.ui.notesScreen.Note
import com.example.teacher_module.common.NotificationState
import com.example.teacher_module.common.Resource
import com.example.teacher_module.domain.use_cases.PostNotificationUseCase
import com.example.teacher_module.domain.use_cases.UploadPictureUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class TeacherNotesViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getNoteInfoUseCase: GetNoteStudentsInfoUseCase,
    private val postNotificationUseCase: PostNotificationUseCase,
    private val getCollectionReferenceForUserInfoUseCase: GetCollectionReferenceForUserInfoUseCase,
    private val uploadPictureUseCase: UploadPictureUseCase,
    private val getPictureInfoUseCase: GetPictureCommentInfoUseCase
) : ViewModel() {

    private val notificationState: MutableStateFlow<NotificationState?> = MutableStateFlow(null)
    val notificationStateOpen: StateFlow<NotificationState?> = notificationState

    private fun getUserUid(): String? = getUserUidUseCase.getUserUid()

    fun createNote(
        collectionFirstPath: String,
        collectionSecondPath: String,
        collectionThirdPath: String,
        groupId: String,
        title: String,
        text: String,
        uris: List<Uri>,
        collectionForthPath: String
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
                ).set(noteInfo).addOnSuccessListener {
                    uploadPictures(
                        uris,
                        collectionFirstPath,
                        collectionSecondPath,
                        groupId,
                        collectionThirdPath,
                        groupId + title,
                        collectionForthPath,
                        noteInfo
                    )
                }
            }
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

    private fun uploadPictures(
        uris: List<Uri>,
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String,
        noteInfo: MutableMap<String, Any>
    ) {
        if (uris.isEmpty()) updateStudentNotes(
            collectionFirstPath,
            collectionSecondPath,
            collectionThirdPath,
            groupId,
            noteInfo,
            collectionForthPath
        )
        else viewModelScope.launch(Dispatchers.IO) {
            uris.forEach { uri ->
                uri.lastPathSegment?.let { lastPathSegment ->
                    uploadPictureUseCase.getUploadPictureTask(
                        uri,
                        "${Constants.UPLOAD_PICTURE_PATH}$lastPathSegment"
                    )
                        .continueWithTask {
                            uploadPictureUseCase.getResultUriTask("${Constants.UPLOAD_PICTURE_PATH}$lastPathSegment")
                        }.addOnCompleteListener { resultUri ->
                            val pictureInfo = mutableMapOf<String, String>()
                            pictureInfo[Constants.PICTURE_URI] =
                                resultUri.result.toString().replace("/", "|")
                            getUserUid()?.let { it1 ->
                                getPictureInfoUseCase.getDocumentReference(
                                    collectionFirstPath,
                                    it1,
                                    collectionSecondPath,
                                    groupId,
                                    collectionThirdPath,
                                    noteId,
                                    collectionForthPath,
                                    resultUri.result.toString().replace("/", "|")
                                ).set(pictureInfo).addOnSuccessListener {
                                    updateStudentNotes(
                                        collectionFirstPath,
                                        collectionSecondPath,
                                        collectionThirdPath,
                                        groupId,
                                        noteInfo,
                                        collectionForthPath
                                    )
                                }
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
        noteInfo: MutableMap<String, Any>,
        collectionForthPath: String
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
                                    getPictureInfoUseCase.getCollectionReference(
                                        collectionFirstPath,
                                        it,
                                        collectionSecondPath,
                                        groupId,
                                        collectionThirdPath,
                                        groupId + noteInfo[Constants.TITLE],
                                        collectionForthPath
                                    ).get().addOnSuccessListener { pictures ->
                                        for (picture in pictures) {
                                            val pictureInfo = mutableMapOf<String, String>()
                                            pictureInfo[Constants.PICTURE_URI] = picture.id
                                            getPictureInfoUseCase.getDocumentReference(
                                                collectionFirstPath,
                                                user.id,
                                                collectionSecondPath,
                                                groupId,
                                                collectionThirdPath,
                                                groupId + noteInfo[Constants.TITLE],
                                                collectionForthPath,
                                                picture.id
                                            ).set(pictureInfo)
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