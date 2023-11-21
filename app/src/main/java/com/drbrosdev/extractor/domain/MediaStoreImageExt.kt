package com.drbrosdev.extractor.domain

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.drbrosdev.extractor.domain.model.MediaImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


fun ContentResolver.mediaImagesFlow() = observe(
    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
).map { runMediaImageQuery() }


suspend fun ContentResolver.runMediaImageQuery(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    selection: String? = null
): List<MediaImage> = withContext(dispatcher) {
    val out = mutableListOf<MediaImage>()
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.HEIGHT,
        MediaStore.Images.Media.WIDTH,
    )

    query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        null,
        MediaStore.Images.Media.DATE_ADDED + " DESC"
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            out.add(cursor.toMediaImage())
        }
    }
    out
}


suspend fun ContentResolver.findByUri(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    uri: Uri
): MediaImage? = withContext(dispatcher) {
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
            return@withContext cursor.toMediaImage()
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

private fun Cursor.toMediaImage(): MediaImage {
    val id = getLong(getColumnIndexOrThrow(MediaStore.Images.Media._ID))
    val displayName = getString(getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
    val dateAdded = getLong(getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED))
    val height = getInt(getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT))
    val width = getInt(getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH))
    val size = getLong(getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
    val pathColumn = getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    val path = getString(pathColumn)

//    val exifData = ExifInterface(path)
//    val point = exifData.latLong?.let {
//        LocationPoint(latitude = it[0], longitude = it[1])
//    }
//    val bar = Geocoder(this, Locale.getDefault())
//    bar.getFromLocationName("some", 1, object: Geocoder.GeocodeListener {})

    val formatted = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateAdded * 1000), ZoneId.systemDefault())

    val uri = Uri.withAppendedPath(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        id.toString()
    )

    val extension = path.substringAfterLast(".")
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).toString()

    return MediaImage(
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

