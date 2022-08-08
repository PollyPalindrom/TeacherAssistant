package com.example.common_module.di

import android.app.Application
import android.content.Context
import com.example.common_module.ui.commentScreen.CommentViewModel
import com.example.common_module.ui.entryScreen.EntryScreenViewModel
import com.example.common_module.ui.pictureScreen.PictureViewModel
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [CommonModule::class, CommonViewModelsModule::class],
    dependencies = [CommonDeps::class]
)
@Singleton
interface CommonComponent {
    @Component.Builder
    interface Builder {
        fun deps(deps: CommonDeps): Builder
        fun build(): CommonComponent
    }

    fun getCommentViewModel(): CommentViewModel
    fun getEntryScreenViewModel(): EntryScreenViewModel
    fun getPictureViewModel(): PictureViewModel
}

val Context.commonDepsProvider:CommonDepsProvider
    get() = when(this){
        is CommonDepsProvider ->this
        is Application -> error("Application must implements CommonDepsProvider")
        else -> applicationContext.commonDepsProvider
    }
