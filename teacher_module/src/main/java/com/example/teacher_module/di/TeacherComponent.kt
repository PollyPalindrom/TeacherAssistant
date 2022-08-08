package com.example.teacher_module.di

import android.app.Application
import com.example.common_module.data.repository.Repository
import com.example.common_module.di.CommonDeps
import com.example.common_module.ui.mainActivity.MainActivityViewModel
import com.example.common_module.ui.mainScreen.MainViewModel
import com.example.common_module.ui.notesScreen.NotesViewModel
import com.example.teacher_module.ui.mainScreen.TeacherMainViewModel
import com.example.teacher_module.ui.notesScreen.TeacherNotesViewModel
import com.example.teacher_module.ui.studentsList.StudentsViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [TeacherModule::class, TeacherViewModelsModule::class]
)
@Singleton
interface TeacherComponent : CommonDeps {
    override val repository: Repository

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): TeacherComponent
    }

    fun getTeacherMainViewModel(): TeacherMainViewModel
    fun getTeacherNotesViewModel(): TeacherNotesViewModel
    fun getStudentsViewModel(): StudentsViewModel
    fun getMainViewModel(): MainViewModel
    fun getMainNotesViewModel(): NotesViewModel
    fun getMainActivityViewModel():MainActivityViewModel
}
