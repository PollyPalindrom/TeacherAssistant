package com.example.teacherassistant.domain.use_cases

import com.example.teacherassistant.data.repository.Repository
import com.google.firebase.firestore.CollectionReference
import javax.inject.Inject

class GetCollectionReferenceForUserInfoUseCase @Inject constructor(private val repository: Repository) {
    fun getCollectionReference(collectionPath: String): CollectionReference =
        repository.getCollectionReferenceForUserInfo(collectionPath)
}