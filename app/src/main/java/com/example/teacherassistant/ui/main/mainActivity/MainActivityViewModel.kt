package com.example.teacherassistant.ui.main.mainActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherassistant.common.CheckRoleManager
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.common.PostToastListener
import com.example.teacherassistant.domain.use_cases.GetAuthResultForSignInUseCase
import com.example.teacherassistant.domain.use_cases.GetDocumentReferenceForUserInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserUidUseCase
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getDocumentReferenceForUserInfoUseCase: GetDocumentReferenceForUserInfoUseCase,
    private val getAuthResultForSignInUseCase: GetAuthResultForSignInUseCase
) :
    ViewModel() {
    private val checkRoleManager = CheckRoleManager

    private fun getUserUid(): String? {
        return getUserUidUseCase.getUserUid()
    }

    private fun getDocumentReferenceForUserInfo(collectionPath: String): DocumentReference? {
        return getUserUid()?.let {
            getDocumentReferenceForUserInfoUseCase.getDocumentReference(
                collectionPath,
                it
            )
        }
    }

    fun checkRole(
        openNextFragment: (role: String) -> Unit,
        collectionFirstPath: String,
        status: String,
        postToastListener: PostToastListener,
        saveUserInfo:(realRole:String)->Unit
    ) {
        checkRoleManager.checkRole(
            openNextFragment,
            getDocumentReferenceForUserInfo(collectionFirstPath),
            status,
            postToastListener,
            saveUserInfo
        )
    }

    fun getMapUserInfo(): MutableMap<String, Any> {
        val userInfo: MutableMap<String, Any> = mutableMapOf()
        getUserInfoUseCase.getUserEmail()?.let { userInfo.put(Constants.EMAIL, it) }
        getUserInfoUseCase.getUserFullName()?.let { userInfo.put(Constants.FULL_NAME, it) }
        return userInfo
    }

    fun getAuthResult(credential: AuthCredential): Task<AuthResult> {
        return getAuthResultForSignInUseCase.getAuthResult(credential)
    }

    fun setUserInfo(userInfo: MutableMap<String, Any>, collectionPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val df = getDocumentReferenceForUserInfo(collectionPath)
            df?.set(userInfo)
        }
    }
}