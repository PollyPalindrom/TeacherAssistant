package com.example.teacherassistant.ui.main.mainScreen

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavController
import com.example.teacherassistant.R
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.common.PostToastListener
import com.example.teacherassistant.common.Screen
import com.example.teacherassistant.ui.main.entryScreen.CustomTopBar
import com.example.teacherassistant.ui.main.firebaseService.FirebaseService
import com.google.firebase.messaging.FirebaseMessaging

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(),
    listener: PostToastListener,
    role: String?
) {
    val state = viewModel.groupsListOpen.value
    val scaffoldState = rememberScaffoldState()
    var groupDialog by rememberSaveable { mutableStateOf(false) }
    val onClick = { groupDialog = false }

    FirebaseMessaging.getInstance().token.addOnSuccessListener {
        FirebaseService.token = it
        viewModel.setNewToken(
            it,
            Constants.COLLECTION_FIRST_PATH,
            Constants.COLLECTION_SECOND_PATH,
            Constants.COLLECTION_THIRD_PATH_STUDENTS
        )
    }
    LaunchedEffect(key1 = true) {
        viewModel.subscribeGroupListChanges(
            Constants.COLLECTION_FIRST_PATH,
            Constants.COLLECTION_SECOND_PATH
        )
    }
    Scaffold(topBar = { CustomTopBar() }, floatingActionButton = {
        if (role == Constants.TEACHER) {
            FloatingActionButton(onClick = { groupDialog = !groupDialog }) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_new_group))
            }
        }
    }, scaffoldState = scaffoldState) {
        LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
            items(state.groups) { group ->
                if (role != null) {
                    GroupItem(
                        group,
                        deleteGroup = { groupToDelete ->
                            viewModel.deleteGroup(
                                Constants.COLLECTION_FIRST_PATH,
                                Constants.COLLECTION_SECOND_PATH,
                                groupToDelete
                            )
                        },
                        addStudent = { text, studentGroup ->
                            viewModel.addStudent(
                                text,
                                Constants.COLLECTION_FIRST_PATH,
                                Constants.COLLECTION_SECOND_PATH,
                                Constants.COLLECTION_THIRD_PATH_STUDENTS,
                                studentGroup
                            )
                        },
                        postToast = { id ->
                            listener.postToast(id)
                        },
                        navigateToNotes = {
                            navController.navigate(
                                Screen.NotesScreen.route +
                                        "?${Constants.ROLE}=${if (group.edit) Constants.TEACHER else Constants.STUDENT}&${Constants.GROUP_ID}=${group.id}"
                            )
                        },
                        navigateToStudentsList = {
                            navController.navigate(
                                Screen.StudentsListScreen.route +
                                        "?${Constants.ROLE}=$role&${Constants.GROUP_ID}=${group.id}"
                            )
                        }
                    )
                }
            }
        }
        if (groupDialog) {
            GroupDialog(onClick = onClick, openDialog = { name, title ->
                groupDialog = false
                if (name.isNotBlank() && title.isNotBlank()
                ) {
                    viewModel.createGroup(
                        Constants.COLLECTION_FIRST_PATH,
                        Constants.COLLECTION_SECOND_PATH,
                        name,
                        title
                    )
                } else {
                    listener.postToast(R.string.textErrorMessage)
                }
            })
        }
    }

}

@Composable
fun GroupItem(
    group: Group,
    addStudent: (text: String, group: Group) -> Unit,
    deleteGroup: (group: Group) -> Unit,
    postToast: (id: Int) -> Unit,
    navigateToNotes: () -> Unit,
    navigateToStudentsList: () -> Unit
) {

    var expanded by rememberSaveable { mutableStateOf(false) }
    var emailDialog by rememberSaveable { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
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
                .clickable {
                    navigateToNotes()
                }
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(text = group.name)
                if (expanded) {
                    Text(
                        text = group.title
                    )

                }
            }
            Buttons(
                expanded = expanded,
                expandOnClick = { expanded = !expanded },
                openStudentList = { navigateToStudentsList() },
                deleteGroup = { deleteGroup(group) },
                openEmailDialog = { emailDialog = !emailDialog },
                group = group
            )
        }
    }
    if (emailDialog) {
        EmailDialog(
            onClick = { emailDialog = false }, openDialog = { text: String ->
                emailDialog = false
                if (text.isNotBlank()) {
                    addStudent(text, group)
                } else {
                    postToast(R.string.textErrorMessage)
                }
            })
    }
}

@Composable
fun Buttons(
    expanded: Boolean,
    expandOnClick: () -> Unit,
    openStudentList: () -> Unit,
    deleteGroup: () -> Unit,
    openEmailDialog: () -> Unit,
    group: Group
) {
    if (group.edit) {
        IconButton(
            onClick = { openEmailDialog() },
            modifier = Modifier.background(MaterialTheme.colors.primary)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_baseline_add_24),
                contentDescription = stringResource(id = R.string.add_new_student_email_button_description)
            )
        }
        IconButton(onClick = {
            deleteGroup()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                contentDescription = stringResource(id = R.string.delete_group_button_description)
            )
        }
    }
    IconButton(onClick = {
        openStudentList()
    }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_person_24),
            contentDescription = stringResource(id = R.string.open_student_list_button_description)
        )
    }
    IconButton(onClick = { expandOnClick() }) {
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

@Composable
fun GroupDialog(onClick: () -> Unit, openDialog: (name: String, title: String) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var title by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onClick,
        title = {
            Text(text = stringResource(R.string.groupWindowMessage))
        },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = {
                        name = it
                    }, label = {
                        Text(text = stringResource(R.string.name))
                    }
                )
                TextField(
                    value = title,
                    onValueChange = {
                        title = it
                    }, label = {
                        Text(text = stringResource(R.string.title))
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { openDialog(name, title) }
            ) {
                Text(stringResource(R.string.positiveButton))
            }
        }
    )
}

@Composable
fun EmailDialog(onClick: () -> Unit, openDialog: (text: String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onClick,
        title = {
            Text(text = stringResource(R.string.email))
        },
        text = {
            TextField(
                value = text,
                onValueChange = {
                    text = it
                }
            )
        },
        confirmButton = {
            Button(
                onClick = { openDialog(text) }
            ) {
                Text(stringResource(R.string.positiveButton))
            }
        }
    )
}