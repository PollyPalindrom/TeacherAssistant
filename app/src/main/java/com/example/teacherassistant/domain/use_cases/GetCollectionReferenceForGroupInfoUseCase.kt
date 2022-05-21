package com.example.teacherassistant.domain.use_cases

import com.example.teacherassistant.data.repository.Repository
import com.google.firebase.firestore.CollectionReference
import javax.inject.Inject

class GetCollectionReferenceForGroupInfoUseCase @Inject constructor(private val repository: Repository) {
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


}