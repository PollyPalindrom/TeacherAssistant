package com.example.teacherassistant.ui.main.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.common.PostToastListener
import com.example.teacherassistant.common.Screen
import com.example.teacherassistant.common.SignInListener
import com.example.teacherassistant.ui.main.entryFragment.EntryImage
import com.example.teacherassistant.ui.main.mainFragment.MainScreen
import com.example.teacherassistant.ui.main.notesFragment.NotesScreen
import com.example.teacherassistant.ui.main.onBoarding.WelcomeScreen
import com.example.teacherassistant.ui.main.signInFragment.SignInScreen
import com.example.teacherassistant.ui.main.studentsList.StudentsList
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalPagerApi::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    postToastListener: PostToastListener,
    signInListener: SignInListener
) {
    NavHost(
        navController = navController,
        startDestination = Screen.EntryScreen.route
    ) {
        composable(
            route = Screen.EntryScreen.route
        ) {
            EntryImage()
        }
        composable(route = Screen.SignInScreen.route) {
            SignInScreen(listener = signInListener)
        }
        composable(
            route = Screen.NotesScreen.route +
                    "?${Constants.ROLE}={${Constants.ROLE}}&${Constants.GROUP_ID}={${Constants.GROUP_ID}}",
            arguments = listOf(
                navArgument(Constants.ROLE) {
                    defaultValue = Constants.STUDENT
                    type = NavType.StringType
                },
                navArgument(Constants.GROUP_ID) {
                    defaultValue = Constants.GROUP_ID
                    type = NavType.StringType
                })
        ) {
            NotesScreen(
                listener = postToastListener,
                role = it.arguments?.getString(Constants.ROLE),
                groupId = it.arguments?.getString(Constants.GROUP_ID)
            )
        }
        composable(
            route = Screen.GroupsScreen.route +
                    "?${Constants.ROLE}={${Constants.ROLE}}",
            arguments = listOf(
                navArgument(Constants.ROLE) {
                    defaultValue = Constants.STUDENT
                    type = NavType.StringType
                })
        ) {
            MainScreen(
                navController = navController,
                listener = postToastListener,
                role = it.arguments?.getString(Constants.ROLE)
            )
        }
        composable(route = Screen.WelcomeScreen.route) {
            WelcomeScreen(navController = navController)
        }
        composable(
            route = Screen.StudentsListScreen.route +
                    "?${Constants.ROLE}={${Constants.ROLE}}&${Constants.GROUP_ID}={${Constants.GROUP_ID}}"
        ) {
            StudentsList(
                role = it.arguments?.getString(Constants.ROLE),
                groupId = it.arguments?.getString(Constants.GROUP_ID)
            )
        }
    }
}