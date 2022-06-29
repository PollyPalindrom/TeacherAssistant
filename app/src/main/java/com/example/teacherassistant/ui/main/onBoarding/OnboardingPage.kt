package com.example.teacherassistant.ui.main.onBoarding

import com.example.teacherassistant.R

sealed class OnBoardingPage(val image: Int, val title: Int, val description: Int) {
    object FirstPage : OnBoardingPage(
        R.drawable.ic_baseline_group_add_24,
        R.string.create_group_title,
        R.string.create_group_message
    )

    object SecondPage : OnBoardingPage(
        R.drawable.ic_baseline_person_add_24,
        R.string.add_students_title,
        R.string.add_students_message
    )

    object ThirdPage : OnBoardingPage(
        R.drawable.ic_baseline_notifications_active_24,
        R.string.notifications_title,
        R.string.notifications_message
    )
}
