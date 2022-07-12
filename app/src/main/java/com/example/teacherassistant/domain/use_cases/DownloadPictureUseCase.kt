package com.example.teacherassistant.domain.use_cases

import com.example.teacherassistant.data.repository.Repository
import java.io.File
import javax.inject.Inject

class DownloadPictureUseCase @Inject constructor(private val repository: Repository) {

    fun getFileDownloadTask(imageName: String, destFile: File) =
        repository.getFileDownloadTask(imageName, destFile)

}