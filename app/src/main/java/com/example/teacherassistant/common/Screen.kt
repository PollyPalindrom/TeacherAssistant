package com.example.teacherassistant.common

sealed class Screen(val route: String) {
    object GroupsScreen : Screen("groups_screen")
    object NotesScreen : Screen("notes_screen")
    object SignInScreen : Screen("sign_in_screen")
    object EntryScreen : Screen("entry_screen")
    object WelcomeScreen : Screen("welcome_screen")
    object StudentsListScreen : Screen("students_list_screen")
}