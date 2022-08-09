package com.example.student_module.ui.mainScreen

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.common_module.common.Constants
import com.example.common_module.common.Screen
import com.example.common_module.ui.customTopBar.CustomTopBar
import com.example.common_module.ui.mainScreen.Buttons
import com.example.common_module.ui.mainScreen.Group
import com.example.common_module.ui.mainScreen.MainScreen
import com.example.common_module.ui.mainScreen.MainViewModel
import com.example.student_module.ui.firebaseService.FirebaseService
import com.google.firebase.messaging.FirebaseMessaging

@SuppressLint("StateFlowValueCalledInComposition", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StudentMainScreen(
    navController: NavHostController,
    MainViewModel: MainViewModel
) {
    val state = MainViewModel.groupsListOpen.value
    val scaffoldState = rememberScaffoldState()
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
    Scaffold(
        topBar = { CustomTopBar(navController = navController) },
        scaffoldState = scaffoldState
    ) {
        LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
            items(state.groups) { group ->
                GroupItem(
                    group,
                    navigateToNotes = {
                        navController.navigate(
                            Screen.NotesScreen.route +
                                    "?${Constants.ROLE}=${if (group.edit) Constants.TEACHER else Constants.STUDENT}&${Constants.GROUP_ID}=${group.id}"
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
    }
}

@Composable
fun GroupItem(
    group: Group,
    navigateToNotes: () -> Unit,
    navigateToStudentsList: () -> Unit,
) {

    var expanded by rememberSaveable { mutableStateOf(false) }

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
                openStudentList = { navigateToStudentsList() }
            )
        }
    }
}
