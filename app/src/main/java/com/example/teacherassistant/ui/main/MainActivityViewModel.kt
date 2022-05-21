package com.example.teacherassistant.ui.main

import androidx.lifecycle.ViewModel
import com.example.teacherassistant.domain.use_cases.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val getUserInfoUseCase: GetUserInfoUseCase) :
    ViewModel() {
    fun getUserState(): Boolean {
        return getUserInfoUseCase.getUserState()
    }
}