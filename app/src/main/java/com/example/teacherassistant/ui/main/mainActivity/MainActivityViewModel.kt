package com.example.teacherassistant.ui.main.mainActivity

import androidx.lifecycle.ViewModel
import com.example.teacherassistant.common.CheckRoleManager
import com.example.teacherassistant.common.OpenNextFragmentListener
import com.example.teacherassistant.domain.use_cases.GetDocumentReferenceForUserInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserUidUseCase
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getDocumentReferenceForUserInfoUseCase: GetDocumentReferenceForUserInfoUseCase
) :
    ViewModel() {
    private val checkRoleManager = CheckRoleManager

    fun getUserState(): Boolean {
        return getUserInfoUseCase.getUserState()
    }

    private fun getUserUid(): String? {
        return getUserUidUseCase.getUserUid()
    }

    private fun getDocumentReferenceForUserInfo(collectionPath: String): DocumentReference? {
        return getUserUid()?.let {
            getDocumentReferenceForUserInfoUseCase.getDocumentReferenceForUserInfo(
                collectionPath,
                it
            )
        }
    }

    fun checkRole(listener: OpenNextFragmentListener, collectionFirstPath: String) {
        checkRoleManager.checkRole(listener, getDocumentReferenceForUserInfo(collectionFirstPath))
    }
}