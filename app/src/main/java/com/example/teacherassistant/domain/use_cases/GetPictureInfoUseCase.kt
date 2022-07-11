package com.example.teacherassistant.domain.use_cases

import android.net.Uri
import com.example.teacherassistant.data.repository.Repository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import javax.inject.Inject

class GetPictureInfoUseCase @Inject constructor(private val repository: Repository) {

    fun getCollectionReferenceForPictures(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String
    ): CollectionReference = repository.getCollectionReferenceForPictures(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId,
        collectionThirdPath,
        noteId,
        collectionForthPath
    )

    fun getDocumentReferenceForPictures(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String,
        pictureUri: String
    ): DocumentReference = repository.getDocumentReferenceForPictures(
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