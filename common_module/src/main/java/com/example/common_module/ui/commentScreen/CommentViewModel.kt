package com.example.common_module.ui.commentScreen

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common_module.common.Constants
import com.example.common_module.domain.use_cases.GetGroupInfoUseCase
import com.example.common_module.domain.use_cases.GetPictureCommentInfoUseCase
import com.example.common_module.domain.use_cases.GetUserInfoUseCase
import com.example.common_module.domain.use_cases.GetUserUidUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CommentViewModel @Inject constructor(
    private val getCommentInfoUseCase: GetPictureCommentInfoUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getGroupInfoUseCase: GetGroupInfoUseCase
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
        text: String,
        isReply: Boolean,
        addressee: String
    ) {
        val infoMap = mutableMapOf<String, Any>()
        val userEmail = getUserInfoUseCase.getUserEmail()
        infoMap[Constants.COMMENT] = text
        if (userEmail != null) infoMap[Constants.EMAIL] = userEmail
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
        val currentDate = sdf.format(Date())
        infoMap[Constants.TIME] = currentDate.toString()
        infoMap[Constants.IS_REPLY] =
            if (isReply) Constants.POSITIVE_STAT else Constants.NEGATIVE_STAT
        infoMap[Constants.ADDRESSEE] = addressee
        viewModelScope.launch(Dispatchers.IO) {
            getUserUidUseCase.getUserUid()?.let { uid ->
                getGroupInfoUseCase.getDocumentReference(
                    collectionFirstPath,
                    uid,
                    collectionSecondPath,
                    groupId
                ).get().addOnSuccessListener { groupInfo ->
                    groupInfo?.data?.get(Constants.TEACHER_ID)
                        ?.let {
                            getCommentInfoUseCase.getDocumentReference(
                                collectionFirstPath,
                                it.toString(),
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
        viewModelScope.launch(Dispatchers.IO) {
            getUserUidUseCase.getUserUid()?.let { uid ->
                getGroupInfoUseCase.getDocumentReference(
                    collectionFirstPath,
                    uid,
                    collectionSecondPath,
                    groupId
                ).get().addOnSuccessListener { groupInfo ->
                    groupInfo?.data?.get(Constants.TEACHER_ID)
                        ?.let { teacherId ->
                            getCommentInfoUseCase.getCollectionReference(
                                collectionFirstPath,
                                teacherId.toString(),
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
                                            if (comment.data[Constants.IS_REPLY] == Constants.NEGATIVE_STAT) Comment(
                                                comment.data[Constants.TIME].toString(),
                                                comment.data[Constants.COMMENT].toString(),
                                                comment.data[Constants.EMAIL].toString()
                                            )
                                            else Comment(
                                                comment.data[Constants.TIME].toString(),
                                                comment.data[Constants.COMMENT].toString(),
                                                comment.data[Constants.EMAIL].toString(),
                                                true,
                                                comment.data[Constants.ADDRESSEE].toString()
                                            )
                                        )
                                    }
                                    comments.sortByDescending { it.time }
                                    commentList.value = CommentState(comments)
                                } else if (error != null) {
                                    commentList.value = CommentState(error = error.localizedMessage)
                                }
                            }
                        }
                }
            }
        }
    }
}