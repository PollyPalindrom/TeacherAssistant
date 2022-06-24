package com.example.teacherassistant.ui.main.entryFragment

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.teacherassistant.R

@Composable
fun EntryImage() {
    ConstraintLayout {
        val image = createRef()
        Image(
            painter = painterResource(R.drawable.ic_baseline_group_24),
            contentDescription = stringResource(R.string.entryImageDescription),
            modifier = Modifier.constrainAs(image) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

    }
}