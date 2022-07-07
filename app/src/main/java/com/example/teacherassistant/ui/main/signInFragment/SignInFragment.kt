package com.example.teacherassistant.ui.main.signInFragment

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.teacherassistant.R
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.common.SignInListener
import com.example.teacherassistant.ui.main.entryFragment.CustomTopBar

@Composable
fun SignInScreen(
    listener: SignInListener
) {

    Scaffold(topBar = { CustomTopBar() }, modifier = Modifier.fillMaxSize()) {
        ConstraintLayout {
            val (teacherButton, text, studentButton) = createRefs()
            Text(
                stringResource(R.string.choose_role_message),
                modifier = Modifier.constrainAs(text) {
                    top.linkTo(parent.top)
                    bottom.linkTo(teacherButton.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })
            Button(
                onClick = { listener.signIn(Constants.TEACHER) },
                modifier = Modifier.constrainAs(teacherButton) {
                    top.linkTo(text.bottom)
                    bottom.linkTo(studentButton.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
                Text(stringResource(R.string.teacher))

            }
            Button(
                onClick = { listener.signIn(Constants.STUDENT) },
                modifier = Modifier.constrainAs(studentButton) {
                    top.linkTo(teacherButton.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
                Text(stringResource(R.string.student))
            }
        }
    }
}