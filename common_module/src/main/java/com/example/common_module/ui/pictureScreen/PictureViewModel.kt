package com.example.common_module.ui.pictureScreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common_module.common.DownloadPictureListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PictureViewModel @Inject constructor() : ViewModel() {

    fun downloadPicture(downloadPictureListener: DownloadPictureListener, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            downloadPictureListener.downloadPicture(uri)
        }
    }

}