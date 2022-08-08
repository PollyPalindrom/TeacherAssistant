package com.example.common_module.domain.use_cases

import com.example.common_module.data.repository.Repository
import com.google.firebase.firestore.CollectionReference
import javax.inject.Inject

class GetCollectionReferenceForUserInfoUseCase @Inject constructor(private val repository: Repository) {
    fun getCollectionReference(collectionPath: String): CollectionReference =
        repository.getCollectionReferenceForUserInfo(collectionPath)
}