package com.example.teacherassistant.ui.main.signInFragment

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.teacherassistant.common.SignInListener

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    listener: SignInListener
) {
    val activity = LocalContext.current as? Activity
    BackHandler {
        activity?.finish()
    }
    ConstraintLayout {
        val (teacherButton, text, studentButton) = createRefs()
        Text("choose your role", modifier = Modifier.constrainAs(text) {
            top.linkTo(parent.top)
            bottom.linkTo(teacherButton.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })
        Button(
            onClick = { listener.signIn("Teacher") },
            modifier = Modifier.constrainAs(teacherButton) {
                top.linkTo(text.bottom)
                bottom.linkTo(studentButton.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            Text("Teacher")

        }
        Button(
            onClick = { listener.signIn("Student") },
            modifier = Modifier.constrainAs(studentButton) {
                top.linkTo(teacherButton.bottom)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            Text("Student")
        }
    }
}