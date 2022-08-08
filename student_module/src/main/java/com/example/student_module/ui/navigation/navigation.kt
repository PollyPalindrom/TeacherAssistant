package com.example.student_module.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
import com.example.student_module.di.StudentApplication
import com.example.student_module.ui.mainScreen.StudentMainScreen
import com.example.student_module.ui.notesScreen.StudentNotesScreen
import com.example.student_module.ui.studentsList.StudentsListScreen
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalPagerApi::class)
@Composable
fun NavGraphStudent(
    navController: NavHostController,
    signInListener: SignInListener,
    downloadPictureListener: DownloadPictureListener
) {
    val deps = LocalContext.current.applicationContext.commonDepsProvider.deps

    val appComponent =
        (LocalContext.current.applicationContext as StudentApplication).studentComponent
    NavHost(
        navController = navController,
        startDestination = Screen.EntryScreen.route
    ) {
        composable(
            route = Screen.NotesScreen.route +
                    "?${Constants.GROUP_ID}={${Constants.GROUP_ID}}",
            arguments = listOf(
                navArgument(Constants.GROUP_ID) {
                    defaultValue = Constants.GROUP_ID
                    type = NavType.StringType
                })
        ) {
            StudentNotesScreen(
                groupId = it.arguments?.getString(Constants.GROUP_ID),
                navHostController = navController,
                viewModel = daggerViewModel {
                    appComponent.getMainNotesViewModel()
                }
            )
        }
        composable(
            route = Screen.GroupsScreen.route
        ) {
            StudentMainScreen(
                navController = navController,
                MainViewModel = daggerViewModel {
                    appComponent.getMainViewModel()
                })
        }
        composable(
            route = Screen.StudentsListScreen.route +
                    "?${Constants.GROUP_ID}={${Constants.GROUP_ID}}"
        ) {
            StudentsListScreen(
                groupId = it.arguments?.getString(Constants.GROUP_ID),
                viewModel = daggerViewModel {
                    appComponent.getStudentsViewModel()
                }
            )
        }
        val component = DaggerCommonComponent.builder().deps(deps).build()
        composable(route = Screen.SignInScreen.route) {
            SignInScreen(listener = signInListener, Constants.STUDENT)
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
                    "?${Constants.NOTE_ID}={${Constants.NOTE_ID}}&${Constants.GROUP_ID}={${Constants.GROUP_ID}}"
        ) {
            val viewModel: CommentViewModel = daggerViewModel {
                component.getCommentViewModel()
            }
            CommentScreen(
                viewModel,
                groupId = it.arguments?.getString(Constants.GROUP_ID),
                noteId = it.arguments?.getString(Constants.NOTE_ID)
            )
        }
    }
}