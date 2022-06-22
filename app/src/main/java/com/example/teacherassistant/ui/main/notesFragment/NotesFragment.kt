package com.example.teacherassistant.ui.main.notesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.teacherassistant.R
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.common.Note
import com.example.teacherassistant.common.NotesState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesFragment : Fragment() {

    private val viewModel: NotesViewModel by viewModels<NotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                NotesScreen()
            }
        }
    }

    @Composable
    fun NotesScreen() {
        val noteEntities: NotesState? by viewModel.noteListOpen.collectAsState()
        noteEntities?.notes?.let { Notes(notes = it) }
    }

    @Composable
    fun Notes(notes: List<Note>) {
        var noteDialog by remember { mutableStateOf(false) }

        val onClick = { noteDialog = false }

        Scaffold(floatingActionButton = {
            if (arguments?.getString(Constants.ROLE) == Constants.TEACHER) {
                FloatingActionButton(onClick = { noteDialog = !noteDialog }) {
                    Icon(Icons.Filled.Add, "Add note")
                }
                if (noteDialog) {
                    NoteDialog(onClick = onClick, openDialog = { name, text ->
                        noteDialog = false
                        if (name.isNotBlank() && text.isNotBlank()
                        ) {
                            arguments?.getString(Constants.GROUP_ID)?.let {
                                viewModel.createNote(
                                    Constants.COLLECTION_FIRST_PATH,
                                    Constants.COLLECTION_SECOND_PATH,
                                    Constants.COLLECTION_THIRD_PATH,
                                    it,
                                    name,
                                    text
                                )
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.textErrorMessage),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
                }
            }
        }) {
            LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
                items(notes) { note ->
                    NotesList(
                        name = note.title,
                        text = note.message
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
                Text(text = getString(R.string.noteWindowMessage))
            },
            text = {
                Column {
                    TextField(
                        value = name,
                        onValueChange = {
                            name = it
                        }, label = {
                            Text(text = "Title")
                        }
                    )
                    TextField(
                        value = text,
                        onValueChange = {
                            text = it
                        }, label = {
                            Text(text = "Text")
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { openDialog(name, text) }
                ) {
                    Text(getString(R.string.positiveButton))
                }
            }
        )
    }

    @Composable
    fun NotesList(name: String, text: String) {
        var expanded by remember { mutableStateOf(false) }

        Surface(
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp).fillMaxWidth().wrapContentHeight()
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
                    Text(text = name)
                    if (expanded) {
                        Text(
                            text = text
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(Constants.GROUP_ID)?.let {
            viewModel.subscribeNoteListChanges(
                Constants.COLLECTION_FIRST_PATH,
                Constants.COLLECTION_SECOND_PATH,
                it,
                Constants.COLLECTION_THIRD_PATH
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
    }
}