package com.example.teacherassistant.ui.main.entryScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.teacherassistant.R
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.common.Screen

@Composable
fun EntryImage(
    viewModel: EntryScreenViewModel = hiltViewModel(),
    navController: NavHostController,
) {

    val nextFragmentCallback =
        { role: String ->
            navController.popBackStack()
            navController.navigate(Screen.GroupsScreen.route + "?${Constants.ROLE}=$role")
        }
    LaunchedEffect(key1 = true) {
        if (viewModel.getUserState()) {
            viewModel.checkRole(
                nextFragmentCallback,
                Constants.COLLECTION_FIRST_PATH
            )
        } else {
            navController.popBackStack()
            navController.navigate(Screen.WelcomeScreen.route)
        }
    }

    Scaffold(topBar = {
        CustomTopBar()
    }) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val image = createRef()
            Image(
                painter = painterResource(R.drawable.ic_baseline_group_24),
                contentDescription = stringResource(R.string.entryImageDescription),
                modifier = Modifier
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .background(MaterialTheme.colors.background),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.primaryVariant)
            )

        }
    }
}

@Composable
fun CustomTopBar(
    savePicture: (() -> Unit)? = null
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            if (savePicture != null) {
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(onClick = { savePicture() }) {
                            Icon(
                                Icons.Filled.Save,
                                stringResource(id = R.string.download_button_description)
                            )
                        }
                    }
                }
            }
        })
}