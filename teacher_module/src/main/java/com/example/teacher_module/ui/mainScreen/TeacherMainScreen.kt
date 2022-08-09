package com.example.teacher_module.ui.mainScreen

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.common_module.common.Constants
import com.example.common_module.common.Screen
import com.example.common_module.ui.customTopBar.CustomTopBar
import com.example.common_module.ui.mainScreen.Buttons
import com.example.common_module.ui.mainScreen.Group
import com.example.common_module.ui.mainScreen.MainScreen
import com.example.common_module.ui.mainScreen.MainViewModel
import com.example.teacher_module.R
import com.example.teacher_module.ui.firebaseService.FirebaseService
import com.google.firebase.messaging.FirebaseMessaging

@SuppressLint("StateFlowValueCalledInComposition", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TeacherMainScreen(
    navController: NavHostController,
    MainViewModel: MainViewModel,
    TeacherViewModel: TeacherMainViewModel
) {

    val state = MainViewModel.groupsListOpen.value
    val scaffoldState = rememberScaffoldState()
    var groupDialog by rememberSaveable { mutableStateOf(false) }
    val onClick = { groupDialog = false }
    FirebaseMessaging.getInstance().token.addOnSuccessListener {
        FirebaseService.token = it
        MainViewModel.setNewToken(
            it,
            Constants.COLLECTION_FIRST_PATH,
            Constants.COLLECTION_SECOND_PATH,
            Constants.COLLECTION_THIRD_PATH_STUDENTS
        )
    }
    MainScreen(subscribe = {
        MainViewModel.subscribeGroupListChanges(
            Constants.COLLECTION_FIRST_PATH,
            Constants.COLLECTION_SECOND_PATH
        )
    })
    Scaffold(topBar = { CustomTopBar(navController = navController) }, floatingActionButton = {
        FloatingActionButton(onClick = { groupDialog = !groupDialog }) {
            Icon(
                Icons.Filled.Add,
                stringResource(R.string.add_new_group)
            )
        }
    }, scaffoldState = scaffoldState) {
        LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
            items(state.groups) { group ->
                TeacherGroupItem(
                    group,
                    deleteGroup = { groupToDelete ->
                        TeacherViewModel.deleteGroup(
                            Constants.COLLECTION_FIRST_PATH,
                            Constants.COLLECTION_SECOND_PATH,
                            groupToDelete
                        )
                    },
                    addStudent = { text, studentGroup ->
                        TeacherViewModel.addStudent(
                            text,
                            Constants.COLLECTION_FIRST_PATH,
                            Constants.COLLECTION_SECOND_PATH,
                            Constants.COLLECTION_THIRD_PATH_STUDENTS,
                            studentGroup
                        )
                    },
                    navigateToNotes = {
                        navController.navigate(
                            Screen.NotesScreen.route +
                                    "?${Constants.GROUP_ID}=${group.id}"
                        )
                    },
                    navigateToStudentsList = {
                        navController.navigate(
                            Screen.StudentsListScreen.route +
                                    "?${Constants.GROUP_ID}=${group.id}"
                        )
                    }
                )
            }
        }
        OpenGroupDialog({ firstPath, secondPath, name, title ->
            TeacherViewModel.createGroup(
                firstPath,
                secondPath,
                name,
                title
            )
        }, onClick, groupDialog)
    }

}


@Composable
fun TeacherGroupItem(
    group: Group,
    addStudent: (text: String, group: Group) -> Unit,
    deleteGroup: (group: Group) -> Unit,
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
            TeachersButtons(group, { emailDialog = !emailDialog }, deleteGroup = {
                deleteGroup(group)
            })
            Buttons(
                expanded = expanded,
                expandOnClick = { expanded = !expanded },
                openStudentList = { navigateToStudentsList() }
            )
        }
        OpenEmailDialog(addStudent, group, { emailDialog = false }, emailDialog)
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

@Composable
fun OpenGroupDialog(
    createGroup: (firstPath: String, secondPath: String, name: String, title: String) -> Unit,
    onClick: () -> Unit,
    groupDialog: Boolean
) {
    val context = LocalContext.current
    if (groupDialog) {
        GroupDialog(onClick = onClick, openDialog = { name, title ->
            onClick()
            if (name.isNotBlank() && title.isNotBlank()
            ) {
                createGroup(
                    Constants.COLLECTION_FIRST_PATH,
                    Constants.COLLECTION_SECOND_PATH,
                    name,
                    title
                )
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.textErrorMessage),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

@Composable
fun OpenEmailDialog(
    addStudent: (text: String, group: Group) -> Unit,
    group: Group,
    onClick: () -> Unit,
    emailDialog: Boolean
) {
    val context = LocalContext.current
    if (emailDialog) {
        EmailDialog(
            onClick = onClick, openDialog = { text: String ->
                onClick()
                if (text.isNotBlank()) {
                    addStudent(text, group)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.textErrorMessage),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}

@Composable
fun TeachersButtons(group: Group, openEmailDialog: () -> Unit, deleteGroup: () -> Unit) {
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
}
