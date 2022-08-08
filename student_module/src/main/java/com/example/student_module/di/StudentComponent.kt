package com.example.student_module.di

import com.example.common_module.data.repository.Repository
import com.example.common_module.di.CommonDeps
import com.example.common_module.ui.mainActivity.MainActivityViewModel
import com.example.common_module.ui.mainScreen.MainViewModel
import com.example.common_module.ui.notesScreen.NotesViewModel
import com.example.student_module.ui.studentsList.StudentsViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = [StudentViewModelsModule::class, StudentModule::class])
@Singleton
interface StudentComponent:CommonDeps {
    override val repository: Repository

    @Component.Builder
    interface Builder {
        fun build(): StudentComponent
    }

    fun getStudentsViewModel(): StudentsViewModel
    fun getMainViewModel(): MainViewModel
    fun getMainNotesViewModel(): NotesViewModel
    fun getMainActivityViewModel(): MainActivityViewModel
}
