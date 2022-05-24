package com.example.teacherassistant.domain.use_cases

import com.example.teacherassistant.data.repository.Repository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import javax.inject.Inject

class GetGroupInfoUseCase @Inject constructor(private val repository: Repository) {
    fun getCollection(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String
    ): CollectionReference {
        return repository.getCollectionReferenceForGroupInfo(
            collectionFirstPath,
            uid,
            collectionSecondPath
        )
    }

    fun getDocument(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String
    ): DocumentReference {
        return repository.getDocumentReferenceForGroupInfo(
            collectionFirstPath,
            uid,
            collectionSecondPath,
            groupId
        )
    }
}