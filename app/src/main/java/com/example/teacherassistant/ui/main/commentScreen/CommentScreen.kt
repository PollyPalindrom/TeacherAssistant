package com.example.teacherassistant.ui.main.commentScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reply
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
    var reply by rememberSaveable { mutableStateOf(false) }
    var addressee by rememberSaveable { mutableStateOf("") }

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
        Column {
            if (reply) {
                Text("reply to $addressee")
            }
            Row {
                TextField(
                    value = comment,
                    onValueChange = {
                        comment = it
                    }, label = {
                        Text(text = stringResource(R.string.comment))
                    },
                    modifier = Modifier
                        .weight(1f)
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
                            comment,
                            reply,
                            addressee
                        )
                        comment = ""
                    }
                    reply = false
                    addressee = ""
                }) {
                    Icon(Icons.Default.Send, contentDescription = null)
                }
            }
        }
    }) {
        LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
            if (state != null) {
                items(state.comments) { comment ->
                    CommentItem(comment) { email ->
                        reply = true
                        addressee = email
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment, reply: (email: String) -> Unit) {
    Row {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = comment.time)
            Text(comment.author + if (comment.isReply) " to ${comment.addressee}" else "")
            Text(comment.text)
        }
        IconButton(onClick = { reply(comment.author) }) {
            Icon(
                Icons.Default.Reply,
                contentDescription = null
            )
        }
    }
}