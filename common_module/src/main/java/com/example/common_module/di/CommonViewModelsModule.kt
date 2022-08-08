package com.example.common_module.di

import androidx.lifecycle.ViewModel
import com.example.common_module.domain.use_cases.*
import com.example.common_module.ui.commentScreen.CommentViewModel
import com.example.common_module.ui.entryScreen.EntryScreenViewModel
import com.example.common_module.ui.pictureScreen.PictureViewModel
import dagger.Module
import dagger.Provides

@Module
class CommonViewModelsModule {
    @Provides
    fun provideCommentScreenViewModel(
        getCommentInfoUseCase: GetPictureCommentInfoUseCase,
        getUserInfoUseCase: GetUserInfoUseCase,
        getUserUidUseCase: GetUserUidUseCase,
        getGroupInfoUseCase: GetGroupInfoUseCase
    ): ViewModel = CommentViewModel(
        getCommentInfoUseCase,
        getUserInfoUseCase,
        getUserUidUseCase,
        getGroupInfoUseCase
    )

    @Provides
    fun provideEntryScreenViewModel(
        getDocumentReferenceForUserInfoUseCase: GetDocumentReferenceForUserInfoUseCase,
        getUserInfoUseCase: GetUserInfoUseCase,
        getUserUidUseCase: GetUserUidUseCase
    ): ViewModel = EntryScreenViewModel(
        getDocumentReferenceForUserInfoUseCase,
        getUserUidUseCase,
        getUserInfoUseCase
    )

    @Provides
    fun providePictureViewModel(): ViewModel = PictureViewModel()
}