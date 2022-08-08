package com.example.common_module.data.repository

import android.net.Uri
import com.example.common_module.common.PushNotification
import com.example.common_module.data.remote.MessageApi
import com.example.common_module.data.remote.RemoteDataSource
import com.example.common_module.domain.repository.AuthenticationRepository
import com.example.common_module.domain.repository.DataSourceRepository
import com.example.common_module.domain.repository.ImageRepository
import com.example.common_module.domain.repository.MessageRepository
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

    override fun getCurrentUserFullName(): String? = firebaseAuth.currentUser?.displayName

    override fun getCurrentUserEmail(): String? = firebaseAuth.currentUser?.email

    override fun getCurrentUserUid(): String? = firebaseAuth.currentUser?.uid

    override fun getCurrentState(): Boolean = firebaseAuth.currentUser != null

    override fun getDocumentReferenceForUserInfo(
        collectionPath: String,
        uid: String
    ): DocumentReference = remoteDataSource.getDocumentReferenceForUserInfo(collectionPath, uid)

    override fun getCollectionReferenceForUserInfo(collectionPath: String): CollectionReference =
        remoteDataSource.getCollectionReferenceForUserInfo(collectionPath)

    override fun getAuthResultForSignIn(credential: AuthCredential): Task<AuthResult> =
        firebaseAuth.signInWithCredential(credential)

    override fun getDocumentReferenceForGroupInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        id: String
    ): DocumentReference = remoteDataSource.getDocumentReferenceForGroupInfo(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        id
    )

    override fun getCollectionReferenceForGroupInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String
    ): CollectionReference = remoteDataSource.getCollectionReferenceForGroupInfo(
        collectionFirstPath,
        uid,
        collectionSecondPath
    )

    override fun getDocumentReferenceForNoteStudentsInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        id: String
    ): DocumentReference = remoteDataSource.getDocumentReferenceForNoteStudentsInfo(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId,
        collectionThirdPath,
        id
    )

    override fun getCollectionReferenceForNoteStudentsInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
    ): CollectionReference = remoteDataSource.getCollectionReferenceForNoteStudentsInfo(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId,
        collectionThirdPath
    )

    override suspend fun postNotification(notification: PushNotification): Response<ResponseBody> =
        messageApi.postNotification(notification)

    override fun getUploadPictureTask(uri: Uri, imageName: String): UploadTask =
        storageReference.child(imageName)
            .putFile(uri)

    override fun getResultUriTask(imageName: String): Task<Uri> =
        storageReference.child(imageName).downloadUrl

    override fun getCollectionReferenceForPicturesComments(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String
    ): CollectionReference = remoteDataSource.getCollectionReferenceForPicturesComments(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId,
        collectionThirdPath,
        noteId,
        collectionForthPath
    )

    override fun getDocumentReferenceForPicturesComments(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String,
        id: String
    ): DocumentReference = remoteDataSource.getDocumentReferenceForPicturesComments(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId,
        collectionThirdPath,
        noteId,
        collectionForthPath,
        id
    )
}