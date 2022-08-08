package com.example.teacher_module.ui.studentsList

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.common_module.common.Constants
import com.example.common_module.ui.customTopBar.CustomTopBar
import com.example.common_module.ui.studentsList.StudentItem

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StudentsListScreen(
    viewModel: StudentsViewModel,
    groupId: String?
) {
    val state = viewModel.studentsListOpen.value
    val scaffoldState = rememberScaffoldState()

    if (groupId != null) {
        viewModel.subscribeStudentListChanges(
            Constants.COLLECTION_FIRST_PATH,
            Constants.COLLECTION_SECOND_PATH,
            groupId,
            Constants.COLLECTION_THIRD_PATH_STUDENTS
        )
    }

    Scaffold(topBar = { CustomTopBar() }, scaffoldState = scaffoldState) {
        LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
            items(state.students) { student ->
                StudentItem(student, delete = {
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