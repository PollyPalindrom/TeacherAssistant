package com.example.teacherassistant.domain.use_cases

import com.example.teacherassistant.data.repository.Repository
import javax.inject.Inject

class GetUserUidUseCase @Inject constructor(private val repository: Repository) {
    fun getUserUid(): String? {
        return repository.getCurrentUserUid()
    }
}