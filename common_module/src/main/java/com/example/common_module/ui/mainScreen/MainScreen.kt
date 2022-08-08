package com.example.common_module.ui.mainScreen

import android.annotation.SuppressLint
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.common_module.R

@SuppressLint("StateFlowValueCalledInComposition", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    subscribe: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        subscribe()
    }
}

@Composable
fun Buttons(
    expanded: Boolean,
    expandOnClick: () -> Unit,
    openStudentList: () -> Unit
) {
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