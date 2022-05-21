package com.example.teacherassistant.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TeacherAssistantApplication : Application() {
    companion object {
        lateinit var app: TeacherAssistantApplication
    }
}