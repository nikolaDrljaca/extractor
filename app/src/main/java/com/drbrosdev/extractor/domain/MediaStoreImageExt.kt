package com.drbrosdev.extractor.domain

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.model.MediaImageInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun ContentResolver.mediaImagesFlow() = observe(
    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
).map { runImageQuery() }


suspend fun ContentResolver.runImageQuery(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    selection: String? = null
): List<MediaImage> = withContext(dispatcher) {
    val mediaImages = mutableListOf<MediaImage>()
    val projection = arrayOf(
        MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA
    )

    query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        null,
        MediaStore.Images.Media.DATE_ADDED + " DESC"
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            mediaImages.add(cursor.toMediaImage())
        }
    }

    mediaImages
}

suspend fun ContentResolver.queryMediaImageInfo(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    selection: String? = null
): List<MediaImageInfo> = withContext(dispatcher) {
    val out = mutableListOf<MediaImageInfo>()
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.HEIGHT,
        MediaStore.Images.Media.WIDTH
    )

    query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        null,
        MediaStore.Images.Media.DATE_ADDED + " DESC"
    )?.use { cursor ->
        {
            while (cursor.moveToNext()) {
                out.add(cursor.toMediaImageInfo())
            }
        }
    }
    out
}


suspend fun ContentResolver.findByUri(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    uri: Uri
): MediaImageInfo? = withContext(dispatcher) {
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.HEIGHT,
        MediaStore.Images.Media.WIDTH
    )

    query(
        uri,
        projection,
        null,
        null,
        null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            return@withContext cursor.toMediaImageInfo()
        }
    }

    null
}

suspend fun ContentResolver.getCount(
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): Int = withContext(dispatcher) {
    val projection = arrayOf(MediaStore.Images.Media._ID)

    query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )?.use { cursor ->
        return@withContext cursor.count
    }

    0
}

private fun Cursor.toMediaImageInfo(): MediaImageInfo {
    val id = getLong(getColumnIndexOrThrow(MediaStore.Images.Media._ID))
    val displayName = getString(getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
    val data = getString(getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
    val dateAdded = getLong(getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED))
    val height = getInt(getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT))
    val width = getInt(getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH))
    val size = getLong(getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
    val pathColumn = getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    val path = getString(pathColumn)

    val formatted = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        .format(Date(dateAdded * 1000))

    val uri = Uri.withAppendedPath(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        id.toString()
    )

    val extension = path.substringAfterLast(".")
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).toString()

    return MediaImageInfo(
        mediaImageId = id,
        displayName = displayName,
        dateAdded = formatted,
        height = height,
        width = width,
        size = size,
        path = path,
        uri = uri,
        mimeType = mimeType
    )
}

private fun Cursor.toMediaImage(): MediaImage {
    val idColumn = getColumnIndexOrThrow(MediaStore.Images.Media._ID)
    val pathColumn = getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

    val id = getLong(idColumn)
    val path = getString(pathColumn)
    val uri = Uri.withAppendedPath(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        id.toString()
    )
    return MediaImage(id, path, uri)
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

