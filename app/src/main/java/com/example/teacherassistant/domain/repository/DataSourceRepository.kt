package com.example.teacherassistant.domain.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference

interface DataSourceRepository {

    fun getDocumentReferenceForUserInfo(collectionPath: String, uid: String): DocumentReference

    fun getCollectionReferenceForUserInfo(collectionPath: String): CollectionReference

    fun getDocumentReferenceForGroupInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String
    ): DocumentReference

    fun getCollectionReferenceForGroupInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String
    ): CollectionReference

    fun getDocumentReferenceForNoteInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String
    ): DocumentReference

    fun getCollectionReferenceForNoteInfo(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
    ): CollectionReference

    fun getCollectionReferenceForPictures(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String
    ): CollectionReference

    fun getDocumentReferenceForPictures(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String,
        pictureUri: String
    ): DocumentReference

}