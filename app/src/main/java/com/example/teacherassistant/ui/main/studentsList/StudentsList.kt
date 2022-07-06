package com.example.teacherassistant.ui.main.studentsList

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teacherassistant.R
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.ui.main.entryFragment.CustomTopBar

@Composable
fun StudentsList(
    viewModel: StudentsViewModel = hiltViewModel(),
    groupId: String?,
    role: String?
) {
    val state = viewModel.studentsListOpen.value
    val scaffoldState = rememberScaffoldState()

    if (groupId != null && role != null) {
        viewModel.subscribeStudentListChanges(
            Constants.COLLECTION_FIRST_PATH,
            Constants.COLLECTION_SECOND_PATH,
            groupId,
            Constants.COLLECTION_THIRD_PATH_STUDENTS,
            role
        )
    }

    Scaffold(topBar = { CustomTopBar() }, scaffoldState = scaffoldState) {
        LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
            items(state.students) { student ->
                StudentItem(student, role, delete = {
                    if (groupId != null) {
                        viewModel.deleteStudent(
                            student,
                            Constants.COLLECTION_FIRST_PATH,
                            Constants.COLLECTION_SECOND_PATH,
                            Constants.COLLECTION_THIRD_PATH_STUDENTS,
                            groupId
                        )
                    }
                })
            }
        }
    }
}

@Composable
fun StudentItem(
    student: String,
    role: String?,
    delete: () -> Unit
) {
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
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(text = student)
            }
            if (role == Constants.TEACHER) IconButton(onClick = { delete() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                    contentDescription = "Delete student from group"
                )
            }
        }
    }

}