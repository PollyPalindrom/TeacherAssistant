package com.example.common_module.domain.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult

interface AuthenticationRepository {

    fun getCurrentUserFullName(): String?

    fun getCurrentUserEmail(): String?

    fun getCurrentUserUid(): String?

    fun getCurrentState(): Boolean

    fun getAuthResultForSignIn(credential: AuthCredential): Task<AuthResult>

}