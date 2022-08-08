package com.example.common_module.ui.notesScreen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Message
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.common_module.R

@Composable
fun NoteItemButtons(
    note: Note,
    expanded: Boolean,
    openCommentScreen: (noteId: String) -> Unit,
    onClick: () -> Unit
) {
    IconButton(onClick = {
        openCommentScreen(note.id)
    }) {
        Icon(Icons.Default.Message, contentDescription = null)
    }
    IconButton(onClick = {
        onClick()
    }) {
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = if (expanded) {
                stringResource(R.string.show_less)
            } else {
                stringResource(R.string.show_more)
            }
        )
    }
}

@Composable
fun NoteItemText(
    note: Note,
    expanded: Boolean,
    pictures: List<Uri>,
    openPictureScreen: (uri: Uri) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(text = note.title)
        if (expanded) {
            Text(
                text = note.message
            )
            LazyRow(modifier = Modifier.padding(vertical = 4.dp)) {
                items(pictures) { picture ->
                    PictureItem(
                        uri = picture,
                        openPictureScreen = { uri: Uri -> openPictureScreen(uri) })
                }
            }
        }
    }
}

@Composable
fun PictureItem(
    uri: Uri,
    delete: ((uri: Uri) -> Unit)? = null,
    openPictureScreen: ((uri: Uri) -> Unit)? = null
) {
    Surface(
        color = if (delete != null) MaterialTheme.colors.primary else MaterialTheme.colors.background,
        modifier = Modifier
            .padding(vertical = 1.dp, horizontal = 1.dp)
    ) {
        Column {
            if (delete != null) Icon(
                painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                contentDescription = stringResource(id = R.string.delete_picture_button_description),
                modifier = Modifier
                    .wrapContentHeight()
                    .clickable { delete(uri) }
                    .align(Alignment.End)
            )
            val modifier = Modifier.size(50.dp)
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = if (openPictureScreen == null) modifier else modifier.clickable {
                    openPictureScreen(
                        uri
                    )
                }
            )
        }
    }
}