package com.example.common_module.domain.use_cases

import com.example.common_module.data.repository.Repository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import javax.inject.Inject

class GetNoteStudentsInfoUseCase @Inject constructor(private val repository: Repository) {

    fun getDocumentReference(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        id: String
    ): DocumentReference = repository.getDocumentReferenceForNoteStudentsInfo(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId,
        collectionThirdPath,
        id
    )

    fun getCollectionReference(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String
    ): CollectionReference = repository.getCollectionReferenceForNoteStudentsInfo(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId,
        collectionThirdPath
    )
}