package com.example.teacher_module.di

import android.app.Application
import com.example.common_module.di.CommonDeps
import com.example.common_module.di.CommonDepsProvider

class TeacherApplication: Application(),CommonDepsProvider {
    val teacherComponent:TeacherComponent by lazy {
        DaggerTeacherComponent.builder().application(this).build()
    }
    override val deps: CommonDeps = teacherComponent
}