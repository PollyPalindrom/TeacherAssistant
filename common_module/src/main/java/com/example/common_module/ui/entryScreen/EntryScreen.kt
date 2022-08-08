package com.example.common_module.ui.entryScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.common_module.R
import com.example.common_module.common.Screen
import com.example.common_module.ui.customTopBar.CustomTopBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EntryImage(
    viewModel: EntryScreenViewModel,
    navController: NavHostController,
) {

    val nextFragmentCallback =
        { role: String ->
            navController.popBackStack()
            navController.navigate(Screen.GroupsScreen.route + "?${com.example.common_module.common.Constants.ROLE}=$role")
        }
    LaunchedEffect(key1 = true) {
        if (viewModel.getUserState()) {
            viewModel.checkRole(
                nextFragmentCallback,
                com.example.common_module.common.Constants.COLLECTION_FIRST_PATH
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