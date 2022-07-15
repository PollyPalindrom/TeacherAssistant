package com.example.teacherassistant.ui.main.pictureScreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherassistant.common.DownloadPictureListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PictureViewModel @Inject constructor(): ViewModel() {

    fun downloadPicture(downloadPictureListener: DownloadPictureListener, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            downloadPictureListener.downloadPicture(uri)
        }
    }

}