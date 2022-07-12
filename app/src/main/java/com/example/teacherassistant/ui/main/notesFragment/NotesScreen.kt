package com.example.teacherassistant.ui.main.notesFragment

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.teacherassistant.R
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.common.PostToastListener
import com.example.teacherassistant.ui.main.entryScreen.CustomTopBar

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
    var noteDialog by rememberSaveable { mutableStateOf(false) }

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
                        },
                            getUris = { setUris ->
                                viewModel.getUrisList(
                                    Constants.COLLECTION_FIRST_PATH,
                                    Constants.COLLECTION_SECOND_PATH,
                                    groupId,
                                    Constants.COLLECTION_THIRD_PATH,
                                    note.id,
                                    Constants.COLLECTION_FORTH_PATH,
                                    setUris
                                )
                            })
                    }
                }
            }
        }
        if (noteDialog) {
            NoteDialog(onClick = { noteDialog = false }, createNote = { name, text, uris ->
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
                            text,
                            uris,
                            Constants.COLLECTION_FORTH_PATH
                        )
                    }

                } else {
                    listener.postToast(R.string.textErrorMessage)
                }
            })
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    role: String,
    deleteNote: (note: Note) -> Unit,
    getUris: (setUris: (List<Uri>) -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var pictures by rememberSaveable { mutableStateOf(listOf<Uri>()) }

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
                    LazyRow(modifier = Modifier.padding(vertical = 4.dp)) {
                        items(pictures) { picture ->
                            PictureItem(uri = picture)
                        }
                    }
                }
            }
            if (role == Constants.TEACHER) {
                IconButton(onClick = {
                    deleteNote(note)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                        contentDescription = stringResource(id = R.string.open_student_list_button_description)
                    )
                }
            }
            IconButton(onClick = {
                expanded = !expanded
                getUris { list -> pictures = list }
            }) {
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

@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@Composable
fun NoteDialog(
    onClick: () -> Unit,
    createNote: (name: String, text: String, uris: List<Uri>) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var text by rememberSaveable { mutableStateOf("") }
    var pictures by rememberSaveable { mutableStateOf(listOf<Uri>()) }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            pictures += uri
        }
    }

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

                LazyRow(modifier = Modifier.padding(vertical = 4.dp)) {
                    items(pictures) { picture ->
                        PictureItem(uri = picture, delete = { uri -> pictures -= uri })
                    }
                }

                IconButton(onClick = {
                    launcher.launch(Constants.PICK_PICTURE_PATH)
                }) {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_add_24),
                            contentDescription = stringResource(id = R.string.add_button_description),
                        )
                        Text(text = stringResource(id = R.string.add_button_text))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { createNote(name, text, pictures) }
            ) {
                Text(stringResource(R.string.positiveButton))
            }
        }
    )
}

@Composable
fun PictureItem(uri: Uri, delete: ((uri: Uri) -> Unit)? = null) {
    Surface(
        color = if (delete != null) MaterialTheme.colors.primary else MaterialTheme.colors.background,
        modifier = Modifier
            .padding(vertical = 1.dp, horizontal = 1.dp)
    ) {
        Column {
            if (delete != null) Icon(
                painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                contentDescription = stringResource(id = R.string.delete_picture_button_description),
                modifier = Modifier
                    .wrapContentHeight()
                    .clickable { delete(uri) }
                    .align(Alignment.End)
            )
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier.size(50.dp),
            )
        }
    }
}