package com.example.teacherassistant.ui.main.pictureScreen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.ui.main.entryScreen.CustomTopBar

@Composable
fun PictureScreen(viewModel: PictureViewModel = hiltViewModel(), uri: Uri) {
    Scaffold(topBar = {
        CustomTopBar(savePicture = { viewModel.downloadPicture(Constants.DOWNLOAD_PICTURE_PATH + uri.lastPathSegment) })
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