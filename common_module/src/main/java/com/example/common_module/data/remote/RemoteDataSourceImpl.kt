package com.example.common_module.data.remote

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
    ): DocumentReference = firestore.collection(collectionPath).document(uid)

    override fun getCollectionReferenceForUserInfo(collectionPath: String): CollectionReference =
        firestore.collection(collectionPath)

    override fun getDocumentReferenceForGroupInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String
    ): DocumentReference = getCollectionReferenceForGroupInfo(
        collectionFirstPath,
        uid,
        collectionSecondPath
    ).document(groupId)

    override fun getCollectionReferenceForGroupInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String
    ): CollectionReference = getDocumentReferenceForUserInfo(collectionFirstPath, uid)
        .collection(collectionSecondPath)

    override fun getDocumentReferenceForNoteStudentsInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        id: String
    ): DocumentReference = getCollectionReferenceForNoteStudentsInfo(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId,
        collectionThirdPath
    )
        .document(id)

    override fun getCollectionReferenceForNoteStudentsInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
    ): CollectionReference = getDocumentReferenceForGroupInfo(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId
    ).collection(collectionThirdPath)

    override fun getCollectionReferenceForPicturesComments(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String
    ): CollectionReference = getDocumentReferenceForNoteStudentsInfo(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId,
        collectionThirdPath,
        noteId
    ).collection(collectionForthPath)

    override fun getDocumentReferenceForPicturesComments(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String,
        id: String
    ): DocumentReference = getCollectionReferenceForPicturesComments(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId,
        collectionThirdPath,
        noteId,
        collectionForthPath
    ).document(id)
}