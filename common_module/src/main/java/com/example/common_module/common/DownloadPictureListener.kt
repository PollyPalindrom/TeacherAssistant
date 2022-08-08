package com.example.common_module.common

import android.net.Uri

interface DownloadPictureListener {

    fun downloadPicture(uri: Uri)
}