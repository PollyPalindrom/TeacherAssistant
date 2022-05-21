package com.example.teacherassistant.ui.main

import androidx.lifecycle.ViewModel
import com.example.teacherassistant.common.CheckRoleListener
import com.example.teacherassistant.domain.use_cases.GetAuthResultForSignInUseCase
import com.example.teacherassistant.domain.use_cases.GetDocumentReferenceForUserInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserInfoUseCase
import com.example.teacherassistant.domain.use_cases.GetUserUidUseCase
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getAuthResultForSignInUseCase: GetAuthResultForSignInUseCase,
    private val getDocumentReferenceForUserInfoUseCase: GetDocumentReferenceForUserInfoUseCase,
    private val getUserUidUseCase: GetUserUidUseCase
) :
    ViewModel() {

    fun checkState(): Boolean {
        return getUserInfoUseCase.getUserState()
    }

    private fun getUserUid(): String? {
        return getUserUidUseCase.getUserUid()
    }

    fun getMapUserInfo(): MutableMap<String, Any> {
        val userInfo: MutableMap<String, Any> = mutableMapOf()
        getUserInfoUseCase.getUserEmail()?.let { userInfo.put("Email", it) }
        getUserInfoUseCase.getUserFullName()?.let { userInfo.put("FullName", it) }
        return userInfo
    }

    fun getAuthResult(credential: AuthCredential): Task<AuthResult> {
        return getAuthResultForSignInUseCase.getAuthResult(credential)
    }

    fun setUserInfo(userInfo: MutableMap<String, Any>, collectionPath: String) {
        val df = getDocumentReferenceForUserInfo(collectionPath)
        df?.set(userInfo)
    }

    private fun getDocumentReferenceForUserInfo(collectionPath: String): DocumentReference? {
        return getUserUid()?.let {
            getDocumentReferenceForUserInfoUseCase.getDocumentReferenceForUserInfo(
                collectionPath,
                it
            )
        }
    }

    fun checkRole(listener: CheckRoleListener) {
        getDocumentReferenceForUserInfo("User")?.get()?.addOnSuccessListener {
            if (it.getString("isTeacher") == "1") {
                listener.openNextFragment("Teacher")
            }
            if (it.getString("isTeacher") == "0") {
                listener.openNextFragment("Student")
            }
        }
    }
}