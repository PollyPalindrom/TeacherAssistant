package com.example.common_module.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.common_module.common.*
import com.example.common_module.di.DaggerCommonComponent
import com.example.common_module.di.commonDepsProvider
import com.example.common_module.ui.commentScreen.CommentScreen
import com.example.common_module.ui.commentScreen.CommentViewModel
import com.example.common_module.ui.entryScreen.EntryImage
import com.example.common_module.ui.entryScreen.EntryScreenViewModel
import com.example.common_module.ui.onBoarding.WelcomeScreen
import com.example.common_module.ui.pictureScreen.PictureScreen
import com.example.common_module.ui.pictureScreen.PictureViewModel
import com.example.common_module.ui.signInScreen.SignInScreen
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalPagerApi::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    signInListener: SignInListener,
    downloadPictureListener: DownloadPictureListener,
    status: String
) {
    val deps = LocalContext.current.applicationContext.commonDepsProvider.deps

    NavHost(
        navController = navController,
        startDestination = Screen.EntryScreen.route
    ) {
        val component = DaggerCommonComponent.builder().deps(deps).build()
        composable(route = Screen.SignInScreen.route) {
            SignInScreen(listener = signInListener, status, navController)
        }
        composable(
            route = Screen.PictureScreen.route +
                    "?${Constants.URI}={${Constants.URI}}"
        ) {
            val viewModel: PictureViewModel = daggerViewModel {
                component.getPictureViewModel()
            }

            PictureScreen(
                viewModel,
                uri = Uri.parse(it.arguments?.getString(Constants.URI)),
                downloadPictureListener = downloadPictureListener,
                navController
            )
        }
        composable(route = Screen.WelcomeScreen.route) {
            WelcomeScreen(navController = navController)
        }
        composable(
            route = Screen.EntryScreen.route
        ) {
            val viewModel: EntryScreenViewModel = daggerViewModel {
                component.getEntryScreenViewModel()
            }
            EntryImage(viewModel, navController = navController)
        }
        composable(
            route = Screen.CommentsScreen.route +
                    "?${Constants.NOTE_ID}={${Constants.NOTE_ID}}&${Constants.GROUP_ID}={${Constants.GROUP_ID}}"
        ) {
            val viewModel: CommentViewModel = daggerViewModel {
                component.getCommentViewModel()
            }
            CommentScreen(
                viewModel,
                groupId = it.arguments?.getString(Constants.GROUP_ID),
                noteId = it.arguments?.getString(Constants.NOTE_ID),
                navController
            )
        }
    }
}