package com.example.teacherassistant.ui.main.onBoarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.teacherassistant.R
import com.example.teacherassistant.common.Screen
import com.google.accompanist.pager.*

@ExperimentalPagerApi
@Composable
fun WelcomeScreen(navController: NavController) {
    val pages =
        listOf(OnBoardingPage.FirstPage, OnBoardingPage.SecondPage, OnBoardingPage.ThirdPage)
    val pagerState = rememberPagerState(pageCount = 3)
    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.weight(10f)
        ) { position ->
            PagerScreen(onBoardingPage = pages[position])
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1f)
        )
        FinishButton(modifier = Modifier.weight(1f), pagerState = pagerState) {
            navController.popBackStack()
            navController.navigate(Screen.SignInScreen.route)
        }
    }
}

@Composable
fun PagerScreen(onBoardingPage: OnBoardingPage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = onBoardingPage.image),
            contentDescription = stringResource(id = R.string.image_desc),
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.7f),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.primaryVariant)
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = onBoardingPage.title),
            fontSize = MaterialTheme.typography.h4.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .padding(top = 20.dp),
            text = stringResource(id = onBoardingPage.description),
            fontSize = MaterialTheme.typography.subtitle1.fontSize,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@ExperimentalPagerApi
@Composable
fun FinishButton(modifier: Modifier, pagerState: PagerState, onClick: () -> Unit) {
    Row(
        modifier = modifier.padding(horizontal = 40.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = pagerState.currentPage == 2,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(contentColor = Color.White)
            ) {
                Text(text = stringResource(id = R.string.finish_button))
            }
        }

    }
}
