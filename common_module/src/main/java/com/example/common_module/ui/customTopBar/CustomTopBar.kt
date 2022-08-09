package com.example.common_module.ui.customTopBar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.common_module.R

@Composable
fun CustomTopBar(
    savePicture: (() -> Unit)? = null,
    navController:NavHostController
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        title = { Text(stringResource(R.string.app_name)) },
        navigationIcon = if (navController.previousBackStackEntry != null) {
            {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        } else {
            null
        },
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
                            Row {
                                Icon(
                                    Icons.Filled.Save,
                                    stringResource(id = R.string.download_button_description)
                                )
                                Text("Save")
                            }
                        }
                    }
                }
            }
        })
}