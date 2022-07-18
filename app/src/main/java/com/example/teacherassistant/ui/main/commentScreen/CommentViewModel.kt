package com.example.teacherassistant.ui.main.commentScreen

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.domain.use_cases.GetPictureCommentInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getCommentInfoUseCase: GetPictureCommentInfoUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getUserUidUseCase: GetUserUidUseCase
) :
    ViewModel() {

    private val commentList = mutableStateOf(CommentState())
    val commentListOpen: State<CommentState?> = commentList

    @SuppressLint("SimpleDateFormat")
    fun createComment(
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String,
        text: String
    ) {
        val infoMap = mutableMapOf<String, Any>()
        val userEmail = getUserInfoUseCase.getUserEmail()
        infoMap[Constants.COMMENT] = text
        if (userEmail != null) infoMap[Constants.EMAIL] = userEmail
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
        val currentDate = sdf.format(Date())
        infoMap[Constants.TIME] = currentDate.toString()
        viewModelScope.launch(Dispatchers.IO) {
            getUserUidUseCase.getUserUid()?.let { uid ->
                getCommentInfoUseCase.getDocumentReference(
                    collectionFirstPath,
                    uid,
                    collectionSecondPath,
                    groupId,
                    collectionThirdPath,
                    noteId,
                    collectionForthPath,
                    noteId + text
                ).set(infoMap)
            }
        }
    }

    fun subscribeComments(
        collectionFirstPath: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String
    ) {
        getUserUidUseCase.getUserUid()?.let { uid ->
            getCommentInfoUseCase.getCollectionReference(
                collectionFirstPath,
                uid,
                collectionSecondPath,
                groupId,
                collectionThirdPath,
                noteId,
                collectionForthPath
            ).addSnapshotListener { value, error ->
                if (value != null && error == null) {
                    val comments = mutableListOf<Comment>()
                    for (comment in value) {
                        comments.add(
                            Comment(
                                comment.data[Constants.TIME].toString(),
                                comment.data[Constants.COMMENT].toString(),
                                comment.data[Constants.EMAIL].toString()
                            )
                        )
                    }
                    commentList.value = CommentState(comments)
                } else if (error != null) {
                    commentList.value = CommentState(error = error.localizedMessage)
                }
            }
        }
    }
}