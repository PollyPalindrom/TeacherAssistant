package com.example.teacherassistant.data.repository

import android.net.Uri
import com.example.teacherassistant.common.PushNotification
import com.example.teacherassistant.data.remote.MessageApi
import com.example.teacherassistant.data.remote.RemoteDataSource
import com.example.teacherassistant.domain.repository.AuthenticationRepository
import com.example.teacherassistant.domain.repository.DataSourceRepository
import com.example.teacherassistant.domain.repository.ImageRepository
import com.example.teacherassistant.domain.repository.MessageRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val storageReference: StorageReference,
    private val firebaseAuth: FirebaseAuth,
    private val messageApi: MessageApi
) : AuthenticationRepository, DataSourceRepository, ImageRepository, MessageRepository {

    override fun getCurrentUserFullName(): String? {
        return firebaseAuth.currentUser?.displayName
    }

    override fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    override fun getCurrentUserUid(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun getCurrentState(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun getDocumentReferenceForUserInfo(
        collectionPath: String,
        uid: String
    ): DocumentReference {
        return remoteDataSource.getDocumentReferenceForUserInfo(collectionPath, uid)
    }

    override fun getCollectionReferenceForUserInfo(collectionPath: String): CollectionReference {
        return remoteDataSource.getCollectionReferenceForUserInfo(collectionPath)
    }

    override fun getAuthResultForSignIn(credential: AuthCredential): Task<AuthResult> {
        return firebaseAuth.signInWithCredential(credential)
    }

    override fun getDocumentReferenceForGroupInfo(
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

    override fun getCollectionReferenceForGroupInfo(
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

    override fun getDocumentReferenceForNoteInfo(
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

    override fun getCollectionReferenceForNoteInfo(
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

    override suspend fun postNotification(notification: PushNotification): Response<ResponseBody> =
        messageApi.postNotification(notification)

    override fun getUploadPictureTask(uri: Uri, imageName: String): UploadTask =
        storageReference.child(imageName)
            .putFile(uri)

    override fun getResultUriTask(imageName: String): Task<Uri> =
        storageReference.child(imageName).downloadUrl

    override fun getCollectionReferenceForPictures(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String
    ): CollectionReference = remoteDataSource.getCollectionReferenceForPictures(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId,
        collectionThirdPath,
        noteId,
        collectionForthPath
    )

    override fun getDocumentReferenceForPictures(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String,
        pictureUri: String
    ): DocumentReference = remoteDataSource.getDocumentReferenceForPictures(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId,
        collectionThirdPath,
        noteId,
        collectionForthPath,
        pictureUri
    )
}