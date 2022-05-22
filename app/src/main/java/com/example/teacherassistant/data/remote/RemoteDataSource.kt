package com.example.teacherassistant.data.remote

import com.example.teacherassistant.common.PushNotification
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val messageApi: MessageApi
) {
    fun getCurrentUserFullName(): String? {
        return firebaseAuth.currentUser?.displayName
    }

    fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    fun getCurrentUserUid(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun getCurrentState(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getDocumentReferenceForUserInfo(collectionPath: String, uid: String): DocumentReference {
        return firestore.collection(collectionPath).document(uid)
    }

    fun getCollectionReferenceForUserInfo(collectionPath: String): CollectionReference {
        return firestore.collection(collectionPath)
    }

    fun getAuthResultForSignIn(credential: AuthCredential): Task<AuthResult> {
        return firebaseAuth.signInWithCredential(credential)
    }

    fun getDocumentReferenceForGroupInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String
    ): DocumentReference {
        return firestore.collection(collectionFirstPath).document(uid)
            .collection(collectionSecondPath).document(groupId)
    }

    fun getCollectionReferenceForGroupInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String
    ): CollectionReference {
        return firestore.collection(collectionFirstPath).document(uid)
            .collection(collectionSecondPath)
    }

    fun getDocumentReferenceForNoteInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String
    ): DocumentReference {
        return firestore.collection(collectionFirstPath).document(uid)
            .collection(collectionSecondPath).document(groupId).collection(collectionThirdPath)
            .document(noteId)
    }

    fun getCollectionReferenceForNoteInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
    ): CollectionReference {
        return firestore.collection(collectionFirstPath).document(uid)
            .collection(collectionSecondPath).document(groupId).collection(collectionThirdPath)
    }

    suspend fun postNotification(notification: PushNotification): Response<ResponseBody> =
        messageApi.postNotification(notification)
}