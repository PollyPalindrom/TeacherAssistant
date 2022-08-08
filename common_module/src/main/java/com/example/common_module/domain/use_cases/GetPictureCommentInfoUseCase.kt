package com.example.common_module.domain.use_cases

import com.example.common_module.data.repository.Repository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import javax.inject.Inject

class GetPictureCommentInfoUseCase @Inject constructor(private val repository: Repository) {

    fun getCollectionReference(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String
    ): CollectionReference = repository.getCollectionReferenceForPicturesComments(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId,
        collectionThirdPath,
        noteId,
        collectionForthPath
    )

    fun getDocumentReference(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String,
        collectionForthPath: String,
        id: String
    ): DocumentReference = repository.getDocumentReferenceForPicturesComments(
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