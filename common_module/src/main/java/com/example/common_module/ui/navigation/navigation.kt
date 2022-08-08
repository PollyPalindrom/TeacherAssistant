package com.example.common_module.ui.navigation

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.common_module.common.DownloadPictureListener
import com.example.common_module.common.Screen
import com.example.common_module.common.SignInListener
import com.example.common_module.common.daggerViewModel
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
            SignInScreen(listener = signInListener, status)
        }
        composable(
            route = Screen.PictureScreen.route +
                    "?${com.example.common_module.common.Constants.URI}={${com.example.common_module.common.Constants.URI}}"
        ) {
            val viewModel: PictureViewModel = daggerViewModel {
                component.getPictureViewModel()
            }

            PictureScreen(
                viewModel,
                uri = Uri.parse(it.arguments?.getString(com.example.common_module.common.Constants.URI)),
                downloadPictureListener = downloadPictureListener
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
                    "?${com.example.common_module.common.Constants.NOTE_ID}={${com.example.common_module.common.Constants.NOTE_ID}}&${com.example.common_module.common.Constants.GROUP_ID}={${com.example.common_module.common.Constants.GROUP_ID}}"
        ) {
            val viewModel: CommentViewModel = daggerViewModel {
                component.getCommentViewModel()
            }
            CommentScreen(
                viewModel,
                groupId = it.arguments?.getString(com.example.common_module.common.Constants.GROUP_ID),
                noteId = it.arguments?.getString(com.example.common_module.common.Constants.NOTE_ID)
            )
        }
    }
}