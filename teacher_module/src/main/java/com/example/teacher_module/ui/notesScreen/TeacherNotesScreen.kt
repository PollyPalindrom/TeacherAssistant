package com.example.teacher_module.ui.notesScreen

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.common_module.common.Constants
import com.example.common_module.common.Screen
import com.example.common_module.ui.customTopBar.CustomTopBar
import com.example.common_module.ui.notesScreen.*
import com.example.teacher_module.R

@SuppressLint("StateFlowValueCalledInComposition", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TeacherNotesScreen(
    TeacherViewModel: TeacherNotesViewModel,
    MainViewModel: NotesViewModel,
    navHostController: NavHostController,
    groupId: String?
) {
    val state = MainViewModel.noteListOpen.value
    val scaffoldState = rememberScaffoldState()
    var noteDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    if (groupId != null) {
        MainViewModel.subscribeNoteListChanges(
            Constants.COLLECTION_FIRST_PATH,
            Constants.COLLECTION_SECOND_PATH,
            groupId,
            Constants.COLLECTION_THIRD_PATH
        )
    }
    Scaffold(topBar = { CustomTopBar(navController = navHostController) }, floatingActionButton = {
        FloatingActionButton(
            onClick = { noteDialog = !noteDialog }
        ) {
            Icon(Icons.Filled.Add, stringResource(R.string.add_new_note))
        }
    }, scaffoldState = scaffoldState) {
        LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
            if (state != null) {
                items(state.notes) { note ->
                    if (groupId != null) {
                        TeacherNoteItem(
                            note, deleteNote = {
                                TeacherViewModel.deleteNote(
                                    Constants.COLLECTION_FIRST_PATH,
                                    Constants.COLLECTION_SECOND_PATH,
                                    Constants.COLLECTION_THIRD_PATH,
                                    note,
                                    groupId
                                )
                            },
                            getUris = { setUris ->
                                MainViewModel.getUrisList(
                                    Constants.COLLECTION_FIRST_PATH,
                                    Constants.COLLECTION_SECOND_PATH,
                                    groupId,
                                    Constants.COLLECTION_THIRD_PATH,
                                    note.id,
                                    Constants.COLLECTION_FORTH_PATH,
                                    setUris
                                )
                            },
                            openPictureScreen = { uri: Uri ->
                                navHostController.navigate(
                                    Screen.PictureScreen.route +
                                            "?${Constants.URI}=${Uri.encode(uri.toString())}"
                                )
                            },
                            openCommentScreen = { noteId ->
                                navHostController.navigate(
                                    Screen.CommentsScreen.route +
                                            "?${Constants.NOTE_ID}=${noteId}&${Constants.GROUP_ID}=$groupId"
                                )
                            }
                        )
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
                        TeacherViewModel.createNote(
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

                } else Toast.makeText(
                    context,
                    context.getString(R.string.textErrorMessage),
                    Toast.LENGTH_SHORT
                ).show()
            })
        }
    }
}

@Composable
fun TeacherNoteItem(
    note: Note,
    deleteNote: (note: Note) -> Unit,
    getUris: (setUris: (List<Uri>) -> Unit) -> Unit,
    openPictureScreen: (uri: Uri) -> Unit,
    openCommentScreen: (noteId: String) -> Unit
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
            NoteItemText(
                note, expanded, pictures, openPictureScreen, Modifier
                    .weight(1f)
                    .padding(12.dp)
            )
            IconButton(onClick = {
                deleteNote(note)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                    contentDescription = stringResource(id = R.string.delete_note_button)
                )
            }
            NoteItemButtons(
                note = note,
                expanded = expanded,
                openCommentScreen = { openCommentScreen(note.id) },
                onClick = {
                    expanded = !expanded
                    getUris { list -> pictures = list }
                }
            )
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