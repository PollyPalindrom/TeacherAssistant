package com.example.teacherassistant.domain.use_cases

import com.example.teacherassistant.data.repository.Repository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val repository: Repository) {

    fun getUserFullName(): String? {
        return repository.getCurrentUserFullName()
    }

    fun getUserEmail(): String? {
        return repository.getCurrentUserEmail()
    }

    fun getUserState(): Boolean {
        return repository.getCurrentState()
    }
}