package com.example.common_module.ui.entryScreen

import androidx.lifecycle.ViewModel
import com.example.common_module.common.CheckRoleManager
import com.example.common_module.domain.use_cases.GetDocumentReferenceForUserInfoUseCase
import com.example.common_module.domain.use_cases.GetUserInfoUseCase
import com.example.common_module.domain.use_cases.GetUserUidUseCase
import javax.inject.Inject

class EntryScreenViewModel @Inject constructor(
    private val getDocumentReferenceForUserInfoUseCase: GetDocumentReferenceForUserInfoUseCase,
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) :
    ViewModel() {
    private val checkRoleManager = CheckRoleManager

    fun getUserState(): Boolean = getUserInfoUseCase.getUserState()

    private fun getUserUid(): String? = getUserUidUseCase.getUserUid()

    fun checkRole(openNextFragment: (role: String) -> Unit, collectionFirstPath: String) {
        checkRoleManager.checkRole(
            openNextFragment,
            getUserUid()?.let {
                getDocumentReferenceForUserInfoUseCase.getDocumentReference(
                    collectionFirstPath, it
                )
            }
        )
    }
}