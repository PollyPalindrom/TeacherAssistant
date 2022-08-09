package com.example.student_module.ui.notesScreen

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.common_module.common.Constants
import com.example.common_module.common.Screen
import com.example.common_module.ui.customTopBar.CustomTopBar
import com.example.common_module.ui.notesScreen.Note
import com.example.common_module.ui.notesScreen.NoteItemButtons
import com.example.common_module.ui.notesScreen.NoteItemText
import com.example.common_module.ui.notesScreen.NotesViewModel

@SuppressLint("StateFlowValueCalledInComposition", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StudentNotesScreen(
    viewModel: NotesViewModel,
    navHostController: NavHostController,
    groupId: String?
) {
    val state = viewModel.noteListOpen.value
    val scaffoldState = rememberScaffoldState()
    if (groupId != null) {
        viewModel.subscribeNoteListChanges(
            Constants.COLLECTION_FIRST_PATH,
            Constants.COLLECTION_SECOND_PATH,
            groupId,
            Constants.COLLECTION_THIRD_PATH
        )
    }
    Scaffold(topBar = { CustomTopBar(navController = navHostController) }, scaffoldState = scaffoldState) {
        LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
            if (state != null) {
                items(state.notes) { note ->
                    if (groupId != null) {
                        StudentNoteItem(
                            note,
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
    }
}

@Composable
fun StudentNoteItem(
    note: Note,
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