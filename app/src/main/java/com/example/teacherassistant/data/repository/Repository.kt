package com.example.teacherassistant.data.repository

import com.example.teacherassistant.common.PushNotification
import com.example.teacherassistant.data.remote.RemoteDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    fun getCurrentUserFullName(): String? {
        return remoteDataSource.getCurrentUserFullName()
    }

    fun getCurrentUserEmail(): String? {
        return remoteDataSource.getCurrentUserEmail()
    }

    fun getCurrentUserUid(): String? {
        return remoteDataSource.getCurrentUserUid()
    }

    fun getCurrentState(): Boolean {
        return remoteDataSource.getCurrentState()
    }

    fun getDocumentReferenceForUserInfo(collectionPath: String, uid: String): DocumentReference {
        return remoteDataSource.getDocumentReferenceForUserInfo(collectionPath, uid)
    }

    fun getAuthResultForSignIn(credential: AuthCredential): Task<AuthResult> {
        return remoteDataSource.getAuthResultForSignIn(credential)
    }

    fun getDocumentReferenceForGroupInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String
    ): DocumentReference {
        return remoteDataSource.getDocumentReferenceForGroupInfo(
            collectionFirstPath,
            uid,
            collectionSecondPath,
            groupId
        )
    }

    fun getCollectionReferenceForGroupInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String
    ): CollectionReference {
        return remoteDataSource.getCollectionReferenceForGroupInfo(
            collectionFirstPath,
            uid,
            collectionSecondPath
        )
    }

    fun getDocumentReferenceForNoteInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String
    ): DocumentReference {
        return remoteDataSource.getDocumentReferenceForNoteInfo(
            collectionFirstPath,
            uid,
            collectionSecondPath,
            groupId,
            collectionThirdPath,
            noteId
        )
    }

    fun getCollectionReferenceForNoteInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
    ): CollectionReference {
        return remoteDataSource.getCollectionReferenceForNoteInfo(
            collectionFirstPath,
            uid,
            collectionSecondPath,
            groupId,
            collectionThirdPath
        )
    }

    suspend fun postNotification(notification: PushNotification): Response<ResponseBody> =
        remoteDataSource.postNotification(notification)

}