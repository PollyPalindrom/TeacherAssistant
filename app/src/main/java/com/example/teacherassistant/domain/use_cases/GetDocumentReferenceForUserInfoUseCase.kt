package com.example.teacherassistant.domain.use_cases

import com.example.teacherassistant.data.repository.Repository
import com.google.firebase.firestore.DocumentReference
import javax.inject.Inject

class GetDocumentReferenceForUserInfoUseCase @Inject constructor(private val repository: Repository) {
    fun getDocumentReference(collectionPath: String, uid: String): DocumentReference =
        repository.getDocumentReferenceForUserInfo(collectionPath, uid)
}