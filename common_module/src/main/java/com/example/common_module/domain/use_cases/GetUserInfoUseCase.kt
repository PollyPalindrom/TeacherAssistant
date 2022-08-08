package com.example.common_module.domain.use_cases

import com.example.common_module.data.repository.Repository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val repository: Repository) {

    fun getUserFullName(): String? = repository.getCurrentUserFullName()

    fun getUserEmail(): String? = repository.getCurrentUserEmail()


    fun getUserState(): Boolean = repository.getCurrentState()
}