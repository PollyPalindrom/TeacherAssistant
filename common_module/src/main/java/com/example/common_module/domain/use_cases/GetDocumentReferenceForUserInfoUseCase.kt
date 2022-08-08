package com.example.common_module.domain.use_cases

import com.example.common_module.data.repository.Repository
import com.google.firebase.firestore.DocumentReference
import javax.inject.Inject

class GetDocumentReferenceForUserInfoUseCase @Inject constructor(private val repository: Repository) {
    fun getDocumentReference(collectionPath: String, uid: String): DocumentReference =
        repository.getDocumentReferenceForUserInfo(collectionPath, uid)
}