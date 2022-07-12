package com.example.teacherassistant.ui.main.pictureScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherassistant.domain.use_cases.DownloadPictureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PictureViewModel @Inject constructor(private val downloadPictureUseCase: DownloadPictureUseCase) :
    ViewModel() {

    fun downloadPicture(imageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val destFile = File.createTempFile(imageName, "jpg")
            downloadPictureUseCase.getFileDownloadTask(imageName, destFile)
        }
    }

}