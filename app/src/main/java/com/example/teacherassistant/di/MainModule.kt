package com.example.teacherassistant.di

import com.example.teacherassistant.common.Constants.Companion.BASE_URL
import com.example.teacherassistant.data.remote.MessageApi
import com.example.teacherassistant.data.remote.RemoteDataSource
import com.example.teacherassistant.data.remote.RemoteDataSourceImpl
import com.example.teacherassistant.data.repository.Repository
import com.example.teacherassistant.domain.use_cases.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MainModule {

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        firestore: FirebaseFirestore
    ): RemoteDataSource {
        return RemoteDataSourceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideRepository(
        remoteDataSource: RemoteDataSource,
        messageApi: MessageApi,
        storageReference: StorageReference,
        firebaseAuth: FirebaseAuth
    ): Repository {
        return Repository(remoteDataSource, storageReference, firebaseAuth, messageApi)
    }

    @Provides
    @Singleton
    fun provideGetAuthResultForSignInUseCase(repository: Repository): GetAuthResultForSignInUseCase {
        return GetAuthResultForSignInUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetCollectionReferenceForUserInfoUseCase(repository: Repository): GetCollectionReferenceForUserInfoUseCase {
        return GetCollectionReferenceForUserInfoUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetDocumentReferenceForUserInfoUseCase(repository: Repository): GetDocumentReferenceForUserInfoUseCase {
        return GetDocumentReferenceForUserInfoUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetGroupInfoUseCase(repository: Repository): GetGroupInfoUseCase {
        return GetGroupInfoUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetNoteInfoUseCase(repository: Repository): GetNoteInfoUseCase {
        return GetNoteInfoUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetPictureInfoUseCase(repository: Repository): GetPictureInfoUseCase {
        return GetPictureInfoUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetUserInfoUseCase(repository: Repository): GetUserInfoUseCase {
        return GetUserInfoUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetUserUidUseCase(repository: Repository): GetUserUidUseCase {
        return GetUserUidUseCase(repository)
    }

    @Provides
    @Singleton
    fun providePostNotificationUseCase(repository: Repository): PostNotificationUseCase {
        return PostNotificationUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUploadPictureUseCase(repository: Repository): UploadPictureUseCase {
        return UploadPictureUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideStorageReference(): StorageReference {
        return FirebaseStorage.getInstance().reference
    }

    @Provides
    @Singleton
    fun provideMessageApi(): MessageApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(MessageApi::class.java)
    }

}