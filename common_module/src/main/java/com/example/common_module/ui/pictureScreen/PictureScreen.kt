package com.example.common_module.ui.pictureScreen

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.rememberAsyncImagePainter
import com.example.common_module.common.DownloadPictureListener
import com.example.common_module.ui.customTopBar.CustomTopBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PictureScreen(
    viewModel: PictureViewModel,
    uri: Uri,
    downloadPictureListener: DownloadPictureListener
) {
    Scaffold(topBar = {
        CustomTopBar(savePicture = {
            viewModel.downloadPicture(
                downloadPictureListener,
                uri
            )
        })
    }) {
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
        )
    }
}