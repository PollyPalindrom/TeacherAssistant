package com.example.teacherassistant.di

import com.example.teacherassistant.common.Constants.Companion.BASE_URL
import com.example.teacherassistant.data.remote.MessageApi
import com.example.teacherassistant.data.remote.RemoteDataSource
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
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        messageApi: MessageApi,
        storageReference: StorageReference
    ): RemoteDataSource {
        return RemoteDataSource(firebaseAuth, firestore, messageApi, storageReference)
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