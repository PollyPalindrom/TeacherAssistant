package com.example.teacherassistant.domain.repository

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.UploadTask
import java.io.File

interface ImageRepository {

    fun getUploadPictureTask(uri: Uri, imageName: String): UploadTask

    fun getResultUriTask(imageName: String): Task<Uri>

}