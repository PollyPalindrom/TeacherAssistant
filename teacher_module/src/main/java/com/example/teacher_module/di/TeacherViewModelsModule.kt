package com.example.teacher_module.di

import androidx.lifecycle.ViewModel
import com.example.common_module.domain.use_cases.*
import com.example.common_module.ui.mainActivity.MainActivityViewModel
import com.example.common_module.ui.mainScreen.MainViewModel
import com.example.common_module.ui.notesScreen.NotesViewModel
import com.example.teacher_module.domain.use_cases.PostNotificationUseCase
import com.example.teacher_module.domain.use_cases.UploadPictureUseCase
import com.example.teacher_module.ui.mainScreen.TeacherMainViewModel
import com.example.teacher_module.ui.notesScreen.TeacherNotesViewModel
import com.example.teacher_module.ui.studentsList.StudentsViewModel
import dagger.Module
import dagger.Provides

@Module
class TeacherViewModelsModule {
    @Provides
    fun provideTeacherMainViewModel(
        getNoteInfoUseCase: GetNoteStudentsInfoUseCase,
        getCollectionReferenceForUserInfoUseCase: GetCollectionReferenceForUserInfoUseCase,
        getUserUidUseCase: GetUserUidUseCase,
        getGroupInfoUseCase: GetGroupInfoUseCase
    ): ViewModel = TeacherMainViewModel(
        getUserUidUseCase,
        getGroupInfoUseCase,
        getNoteInfoUseCase,
        getCollectionReferenceForUserInfoUseCase
    )

    @Provides
    fun provideTeacherNotesViewModel(
        getNoteInfoUseCase: GetNoteStudentsInfoUseCase,
        postNotificationUseCase: PostNotificationUseCase,
        getUserUidUseCase: GetUserUidUseCase,
        getCollectionReferenceForUserInfoUseCase: GetCollectionReferenceForUserInfoUseCase,
        uploadPictureUseCase: UploadPictureUseCase,
        getPictureInfoUseCase: GetPictureCommentInfoUseCase
    ): ViewModel = TeacherNotesViewModel(
        getUserUidUseCase,
        getNoteInfoUseCase,
        postNotificationUseCase,
        getCollectionReferenceForUserInfoUseCase,
        uploadPictureUseCase,
        getPictureInfoUseCase
    )

    @Provides
    fun provideStudentsViewModel(
        getNoteInfoUseCase: GetNoteStudentsInfoUseCase,
        getUserUidUseCase: GetUserUidUseCase
    ): ViewModel = StudentsViewModel(getNoteInfoUseCase, getUserUidUseCase)

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