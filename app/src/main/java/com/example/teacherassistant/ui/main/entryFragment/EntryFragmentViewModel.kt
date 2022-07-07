package com.example.teacherassistant.ui.main.entryFragment

import androidx.lifecycle.ViewModel
import com.example.teacherassistant.common.CheckRoleManager
import com.example.teacherassistant.domain.use_cases.GetDocumentReferenceForUserInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EntryFragmentViewModel @Inject constructor(
    private val getDocumentReferenceForUserInfoUseCase: GetDocumentReferenceForUserInfoUseCase,
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) :
    ViewModel() {
    private val checkRoleManager = CheckRoleManager

    fun getUserState(): Boolean {
        return getUserInfoUseCase.getUserState()
    }

    private fun getUserUid(): String? {
        return getUserUidUseCase.getUserUid()
    }

    fun checkRole(openNextFragment: (role: String) -> Unit, collectionFirstPath: String) {
        checkRoleManager.checkRole(
            openNextFragment,
            getUserUid()?.let {
                getDocumentReferenceForUserInfoUseCase.getDocumentReferenceForUserInfo(
                    collectionFirstPath, it
                )
            }
        )
    }
}