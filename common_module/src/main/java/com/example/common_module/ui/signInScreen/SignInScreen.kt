package com.example.common_module.ui.signInScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.common_module.R
import com.example.common_module.common.SignInListener
import com.example.common_module.ui.customTopBar.CustomTopBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SignInScreen(
    listener: SignInListener,
    status: String
) {

    Scaffold(topBar = { CustomTopBar() }, modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
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
                onClick = { listener.signIn(status) },
                modifier = Modifier.constrainAs(studentButton) {
                    top.linkTo(teacherButton.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
                Text(stringResource(R.string.sign_in))
            }
        }
    }
}