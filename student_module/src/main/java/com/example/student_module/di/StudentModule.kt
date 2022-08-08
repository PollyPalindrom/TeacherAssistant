package com.example.student_module.di

import com.example.common_module.common.Constants.Companion.BASE_URL
import com.example.common_module.data.remote.MessageApi
import com.example.common_module.data.remote.RemoteDataSource
import com.example.common_module.data.remote.RemoteDataSourceImpl
import com.example.common_module.data.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class StudentModule {
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