package com.example.teacherassistant.domain.use_cases

import android.net.Uri
import com.example.teacherassistant.data.repository.Repository
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import javax.inject.Inject

class UploadPictureUseCase @Inject constructor(private val repository: Repository) {

    fun getUploadPictureTask(uri: Uri, imageName: String): UploadTask =
        repository.getUploadPictureTask(uri, imageName)

    fun getResultUriTask(imageName: String): Task<Uri> = repository.getResultUriTask(imageName)
}