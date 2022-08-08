package com.example.common_module.domain.use_cases

import com.example.common_module.data.repository.Repository
import javax.inject.Inject

class GetUserUidUseCase @Inject constructor(private val repository: Repository) {
    fun getUserUid(): String? = repository.getCurrentUserUid()
}