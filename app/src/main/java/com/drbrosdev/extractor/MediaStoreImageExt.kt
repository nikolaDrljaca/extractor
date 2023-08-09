package com.drbrosdev.extractor

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


fun ContentResolver.mediaImagesFlow() = observe(
    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
).map { runImageQuery() }


suspend fun ContentResolver.runImageQuery(
    dispatcher: CoroutineDispatcher = Dispatchers.Default
): List<Image> = withContext(dispatcher) {
    val images = mutableListOf<Image>()
    val projection = arrayOf(
        MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA
    )

    query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        MediaStore.Images.Media.DATE_ADDED + " DESC"
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            images.add(cursor.toImage())
        }
    }

    images
}

data class Image(val id: Long, val path: String, val uri: Uri)

private fun Cursor.toImage(): Image {
    val idColumn = getColumnIndexOrThrow(MediaStore.Images.Media._ID)
    val pathColumn = getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

    val id = getLong(idColumn)
    val path = getString(pathColumn)
    val uri = Uri.withAppendedPath(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        id.toString()
    )
    return Image(id, path, uri)
}

private fun ContentResolver.observe(uri: Uri) = callbackFlow {
    val observer = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            trySend(selfChange)
        }
    }

    registerContentObserver(uri, true, observer)
    //trigger the first emission
    trySend(false)
    awaitClose { unregisterContentObserver(observer) }
}

