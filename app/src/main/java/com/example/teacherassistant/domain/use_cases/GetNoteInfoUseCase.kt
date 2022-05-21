package com.example.teacherassistant.domain.use_cases

import com.example.teacherassistant.data.repository.Repository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import javax.inject.Inject

class GetNoteInfoUseCase @Inject constructor(private val repository: Repository) {

    fun getDocumentReference(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String,
        noteId: String
    ): DocumentReference {
        return repository.getDocumentReferenceForNoteInfo(
            collectionFirstPath,
            uid,
            collectionSecondPath,
            groupId,
            collectionThirdPath,
            noteId
        )
    }

    fun getCollectionReference(
        collectionFirstPath: String,
        uid: String,
        collectionSecondPath: String,
        groupId: String,
        collectionThirdPath: String
    ): CollectionReference {
        return repository.getCollectionReferenceForNoteInfo(
            collectionFirstPath,
            uid,
            collectionSecondPath,
            groupId,
            collectionThirdPath
        )
    }
}