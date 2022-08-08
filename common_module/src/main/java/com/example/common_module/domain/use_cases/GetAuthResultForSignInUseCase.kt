package com.example.common_module.domain.use_cases

import com.example.common_module.data.repository.Repository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import javax.inject.Inject

class GetAuthResultForSignInUseCase @Inject constructor(private val repository: Repository) {
    fun getAuthResult(credential: AuthCredential): Task<AuthResult> =
        repository.getAuthResultForSignIn(credential)
}