package com.example.student_module.di

import android.app.Application
import com.example.common_module.di.CommonDeps
import com.example.common_module.di.CommonDepsProvider

class StudentApplication: Application(), CommonDepsProvider {
    val studentComponent:StudentComponent by lazy {
        DaggerStudentComponent.builder().build()
    }
    override val deps: CommonDeps = studentComponent
}