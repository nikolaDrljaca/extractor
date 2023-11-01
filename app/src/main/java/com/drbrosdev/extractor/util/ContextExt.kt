package com.drbrosdev.extractor.util

import android.content.Context
import android.content.Intent
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.MediaImageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun Context.launchShareIntent(media: MediaImageInfo) =
    withContext(Dispatchers.Default) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = media.mimeType
            putExtra(Intent.EXTRA_STREAM, media.uri)
            putExtra(Intent.EXTRA_SUBJECT, media.displayName)
        }
        startActivity(Intent.createChooser(intent, "Share Image via..."))
    }

suspend fun Context.launchEditIntent(media: MediaImageInfo) =
    withContext(Dispatchers.Default) {
        val intent = Intent(Intent.ACTION_EDIT).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            setDataAndType(media.uri, media.mimeType)
            putExtra("mimeType", media.mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.edit)))
    }

suspend fun Context.launchUseAsIntent(media: MediaImageInfo) =
    withContext(Dispatchers.Default) {
        val intent = Intent(Intent.ACTION_ATTACH_DATA).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            setDataAndType(media.uri, media.mimeType)
            putExtra("mimeType", media.mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.set_as)))
    }