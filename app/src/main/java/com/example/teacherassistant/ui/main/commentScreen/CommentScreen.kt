package com.example.teacherassistant.ui.main.commentScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teacherassistant.R
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.ui.main.entryScreen.CustomTopBar

@Composable
fun CommentScreen(
    viewModel: CommentViewModel = hiltViewModel(),
    groupId: String?,
    noteId: String?
) {

    val state = viewModel.commentListOpen.value
    val scaffoldState = rememberScaffoldState()
    var comment by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(key1 = true) {
        if (groupId != null && noteId != null) viewModel.subscribeComments(
            Constants.COLLECTION_FIRST_PATH,
            Constants.COLLECTION_SECOND_PATH,
            groupId,
            Constants.COLLECTION_THIRD_PATH,
            noteId,
            Constants.COLLECTION_FORTH_PATH_COMMENTS
        )
    }
    Scaffold(scaffoldState = scaffoldState, topBar = {
        CustomTopBar()
    }, bottomBar = {
        Row {
            TextField(
                value = comment,
                onValueChange = {
                    comment = it
                }, label = {
                    Text(text = stringResource(R.string.comment))
                }
            )
            IconButton(onClick = {
                if (noteId != null && groupId != null) {
                    viewModel.createComment(
                        Constants.COLLECTION_FIRST_PATH,
                        Constants.COLLECTION_SECOND_PATH,
                        groupId,
                        Constants.COLLECTION_THIRD_PATH,
                        noteId,
                        Constants.COLLECTION_FORTH_PATH_COMMENTS,
                        comment
                    )
                    comment = ""
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = null)
            }
        }
    }) {
        LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
            if (state != null) {
                items(state.comments) { comment ->
                    CommentItem(comment)
                }
            }
        }
    }

}

@Composable
fun CommentItem(comment: Comment) {
    Column {
        Text(text = comment.time)
        Text(comment.author)
        Text(comment.text)
    }
}