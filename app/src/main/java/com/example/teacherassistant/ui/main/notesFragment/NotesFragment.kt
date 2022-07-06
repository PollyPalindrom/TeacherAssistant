package com.example.teacherassistant.ui.main.notesFragment

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teacherassistant.R
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.common.PostToastListener
import com.example.teacherassistant.ui.main.entryFragment.CustomTopBar

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel(),
    listener: PostToastListener,
    role: String?,
    groupId: String?
) {
    val state = viewModel.noteListOpen.value
    val scaffoldState = rememberScaffoldState()
    var noteDialog by remember { mutableStateOf(false) }

    val onClick = { noteDialog = false }
    if (groupId != null) {
        viewModel.subscribeNoteListChanges(
            Constants.COLLECTION_FIRST_PATH,
            Constants.COLLECTION_SECOND_PATH,
            groupId,
            Constants.COLLECTION_THIRD_PATH
        )
    }
    Scaffold(topBar = { CustomTopBar() }, floatingActionButton = {
        if (role == Constants.TEACHER) {
            FloatingActionButton(
                onClick = { noteDialog = !noteDialog }
            ) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_new_note))
            }
            if (noteDialog) {
                NoteDialog(onClick = onClick, openDialog = { name, text ->
                    noteDialog = false
                    if (name.isNotBlank() && text.isNotBlank()
                    ) {
                        if (groupId != null) {
                            viewModel.createNote(
                                Constants.COLLECTION_FIRST_PATH,
                                Constants.COLLECTION_SECOND_PATH,
                                Constants.COLLECTION_THIRD_PATH,
                                groupId,
                                name,
                                text
                            )
                        }

                    } else {
                        listener.postToast(R.string.textErrorMessage)
                    }
                })
            }
        }
    }, scaffoldState = scaffoldState) {
        LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
            if (state != null) {
                items(state.notes) { note ->
                    if (role != null && groupId != null) {
                        NoteItem(note, role, deleteNote = {
                            viewModel.deleteNote(
                                Constants.COLLECTION_FIRST_PATH,
                                Constants.COLLECTION_SECOND_PATH,
                                Constants.COLLECTION_THIRD_PATH,
                                note,
                                groupId
                            )
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, role: String, deleteNote: (note: Note) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colors.primary,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(text = note.title)
                if (expanded) {
                    Text(
                        text = note.message
                    )
                }
            }
            if (role == Constants.TEACHER) {
                IconButton(onClick = {
                    deleteNote(note)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_person_24),
                        contentDescription = "Open students list"

                    )
                }
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) {
                        stringResource(R.string.show_less)
                    } else {
                        stringResource(R.string.show_more)
                    }
                )
            }
        }
    }
}

@Composable
fun NoteDialog(onClick: () -> Unit, openDialog: (name: String, text: String) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var text by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onClick,
        title = {
            Text(text = stringResource(R.string.noteWindowMessage))
        },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = {
                        name = it
                    }, label = {
                        Text(text = stringResource(R.string.title))
                    }
                )
                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                    }, label = {
                        Text(text = stringResource(R.string.text))
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { openDialog(name, text) }
            ) {
                Text(stringResource(R.string.positiveButton))
            }
        }
    )
}