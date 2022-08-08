package com.example.student_module.di

import androidx.lifecycle.ViewModel
import com.example.common_module.domain.use_cases.*
import com.example.common_module.ui.mainActivity.MainActivityViewModel
import com.example.common_module.ui.mainScreen.MainViewModel
import com.example.common_module.ui.notesScreen.NotesViewModel
import com.example.student_module.ui.studentsList.StudentsViewModel
import dagger.Module
import dagger.Provides

@Module
class StudentViewModelsModule {
    @Provides
    fun provideStudentsViewModel(
        getNoteInfoUseCase: GetNoteStudentsInfoUseCase,
        getUserUidUseCase: GetUserUidUseCase,
        getGroupInfoUseCase: GetGroupInfoUseCase
    ): ViewModel = StudentsViewModel(getNoteInfoUseCase, getUserUidUseCase, getGroupInfoUseCase)

    @Provides
    fun provideMainViewModel(
        getGroupInfoUseCase: GetGroupInfoUseCase,
        getNoteInfoUseCase: GetNoteStudentsInfoUseCase,
        getUserUidUseCase: GetUserUidUseCase,
        getCollectionReferenceForUserInfoUseCase: GetCollectionReferenceForUserInfoUseCase,
        getDocumentReferenceForUserInfoUseCase: GetDocumentReferenceForUserInfoUseCase,
        getUserInfoUseCase: GetUserInfoUseCase
    ): ViewModel = MainViewModel(
        getUserUidUseCase,
        getGroupInfoUseCase,
        getUserInfoUseCase,
        getNoteInfoUseCase,
        getCollectionReferenceForUserInfoUseCase,
        getDocumentReferenceForUserInfoUseCase
    )

    @Provides
    fun provideMainNotesViewModel(
        getPictureInfoUseCase: GetPictureCommentInfoUseCase,
        getNoteInfoUseCase: GetNoteStudentsInfoUseCase,
        getUserUidUseCase: GetUserUidUseCase
    ): ViewModel = NotesViewModel(getUserUidUseCase, getNoteInfoUseCase, getPictureInfoUseCase)

    @Provides
    fun provideMainActivityViewModel(
        getAuthResultForSignInUseCase: GetAuthResultForSignInUseCase,
        getUserUidUseCase: GetUserUidUseCase,
        getDocumentReferenceForUserInfoUseCase: GetDocumentReferenceForUserInfoUseCase,
        getUserInfoUseCase: GetUserInfoUseCase
    ): ViewModel = MainActivityViewModel(
        getUserInfoUseCase,
        getUserUidUseCase,
        getDocumentReferenceForUserInfoUseCase,
        getAuthResultForSignInUseCase
    )
}