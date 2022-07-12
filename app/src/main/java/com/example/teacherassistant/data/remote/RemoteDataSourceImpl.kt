package com.example.teacherassistant.data.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RemoteDataSource {

    override fun getDocumentReferenceForUserInfo(
        collectionPath: String,
        uid: String
    ): DocumentReference {
        return firestore.collection(collectionPath).document(uid)
    }

    override fun getCollectionReferenceForUserInfo(collectionPath: String): CollectionReference {
        return firestore.collection(collectionPath)
    }

    override fun getDocumentReferenceForGroupInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String
    ): DocumentReference {
        return firestore.collection(collectionFirstPath).document(uid)
            .collection(collectionSecondPath).document(groupId)
    }

    override fun getCollectionReferenceForGroupInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String
    ): CollectionReference {
        return firestore.collection(collectionFirstPath).document(uid)
            .collection(collectionSecondPath)
    }

    override fun getDocumentReferenceForNoteInfo(
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

    override fun getCollectionReferenceForNoteInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
    ): CollectionReference {
        return firestore.collection(collectionFirstPath).document(uid)
            .collection(collectionSecondPath).document(groupId).collection(collectionThirdPath)
    }

    override fun getCollectionReferenceForPictures(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String
    ): CollectionReference = firestore.collection(collectionFirstPath).document(uid)
        .collection(collectionSecondPath).document(groupId).collection(collectionThirdPath)
        .document(noteId).collection(collectionForthPath)

    override fun getDocumentReferenceForPictures(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String,
        pictureUri: String
    ): DocumentReference = firestore.collection(collectionFirstPath).document(uid)
        .collection(collectionSecondPath).document(groupId).collection(collectionThirdPath)
        .document(noteId).collection(collectionForthPath).document(pictureUri)
}