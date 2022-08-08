package com.example.common_module.domain.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference

interface DataSourceRepository {

    fun getDocumentReferenceForUserInfo(collectionPath: String, uid: String): DocumentReference

    fun getCollectionReferenceForUserInfo(collectionPath: String): CollectionReference

    fun getDocumentReferenceForGroupInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        id: String
    ): DocumentReference

    fun getCollectionReferenceForGroupInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String
    ): CollectionReference

    fun getDocumentReferenceForNoteStudentsInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        id: String
    ): DocumentReference

    fun getCollectionReferenceForNoteStudentsInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
    ): CollectionReference

    fun getCollectionReferenceForPicturesComments(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String
    ): CollectionReference

    fun getDocumentReferenceForPicturesComments(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String,
        id: String
    ): DocumentReference

}