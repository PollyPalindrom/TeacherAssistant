package com.example.teacherassistant.domain.use_cases

import com.example.teacherassistant.data.repository.Repository
import com.google.firebase.firestore.DocumentReference
import javax.inject.Inject

class GetDocumentReferenceForGroupInfoUseCase @Inject constructor(private val repository: Repository) {
    fun getDocumentReferenceForGroupInfo(
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