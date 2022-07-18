package com.example.teacherassistant.domain.use_cases

import com.example.teacherassistant.data.repository.Repository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import javax.inject.Inject

class GetGroupInfoUseCase @Inject constructor(private val repository: Repository) {
    fun getCollectionReference(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String
    ): CollectionReference = repository.getCollectionReferenceForGroupInfo(
        collectionFirstPath,
        uid,
        collectionSecondPath
    )

    fun getDocumentReference(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String
    ): DocumentReference = repository.getDocumentReferenceForGroupInfo(
        collectionFirstPath,
        uid,
        collectionSecondPath,
        groupId
    )
}