package com.drbrosdev.extractor.framework.mediastore

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.drbrosdev.extractor.domain.model.MediaStoreImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun ContentResolver.mediaStoreImagesFlow(): Flow<List<MediaStoreImage>> = observe(
    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
).map {
    runMediaStoreImageQuery(
        dispatcher = Dispatchers.IO,
        selection = null
    )
}


suspend fun ContentResolver.runMediaStoreImageQuery(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    selection: String? = null
): List<MediaStoreImage> = withContext(dispatcher) {
    val out = mutableListOf<MediaStoreImage>()
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
        //Cache column indexes, so not to create them inside while every time
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
        val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
        val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
        val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val displayName = cursor.getString(displayNameColumn)
            val dateAdded = cursor.getLong(dateAddedColumn)
            val height = cursor.getInt(heightColumn)
            val width = cursor.getInt(widthColumn)
            val size = cursor.getLong(sizeColumn)
            val path = cursor.getString(pathColumn)


            //val exifData = ExifInterface(path)
            //val point = exifData.latLong?.let {
            //        LocationPoint(latitude = it[0], longitude = it[1])
            //}
            //val bar = Geocoder(this, Locale.getDefault())
            //bar.getFromLocationName("some", 1, object: Geocoder.GeocodeListener {})

            val extension = path.substringAfterLast(".")
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).toString()
            val formatted = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(dateAdded * 1000),
                ZoneId.systemDefault()
            )
            val uri = Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id.toString()
            )

            val mediaStoreImage = MediaStoreImage(
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

            out.add(mediaStoreImage)
        }
    }
    out
}

fun ContentResolver.runMediaStoreImageQueryAsFlow(
    selection: String? = null
): Flow<MediaStoreImage> = flow {
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
        //Cache column indexes, so not to create them inside while every time
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
        val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
        val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
        val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val displayName = cursor.getString(displayNameColumn)
            val dateAdded = cursor.getLong(dateAddedColumn)
            val height = cursor.getInt(heightColumn)
            val width = cursor.getInt(widthColumn)
            val size = cursor.getLong(sizeColumn)
            val path = cursor.getString(pathColumn)

            //val exifData = ExifInterface(path)
            //val point = exifData.latLong?.let {
            //        LocationPoint(latitude = it[0], longitude = it[1])
            //}
            //val bar = Geocoder(this, Locale.getDefault())
            //bar.getFromLocationName("some", 1, object: Geocoder.GeocodeListener {})

            val extension = path.substringAfterLast(".")
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).toString()
            val formatted = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(dateAdded * 1000),
                ZoneId.systemDefault()
            )
            val uri = Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id.toString()
            )

            val mediaStoreImage = MediaStoreImage(
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

            emit(mediaStoreImage)
        }
    }
}


suspend fun ContentResolver.findByUri(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    uri: Uri
): MediaStoreImage? = withContext(dispatcher) {
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.HEIGHT,
        MediaStore.Images.Media.WIDTH
    )

    val cursor = query(
        uri,
        projection,
        null,
        null,
        null
    )

    when {
        cursor != null -> cursor.use {
            when {
                it.moveToFirst() -> it.toMediaStoreImage()
                else -> null
            }
        }
        else -> null
    }
}

suspend fun ContentResolver.getCount(
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): Int = withContext(dispatcher) {
    val projection = arrayOf(MediaStore.Images.Media._ID)

    val cursor = query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )

    when {
        cursor != null -> cursor.use { it.count }
        else -> 0
    }
}

private fun Cursor.toMediaStoreImage(): MediaStoreImage {
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

    val formatted =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(dateAdded * 1000), ZoneId.systemDefault())

    val uri = Uri.withAppendedPath(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        id.toString()
    )

    val extension = path.substringAfterLast(".")
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).toString()

    return MediaStoreImage(
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
