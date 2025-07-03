package com.drbrosdev.extractor.domain.model

import android.net.Uri
import androidx.core.net.toUri

@JvmInline
value class MediaImageUri(val uri: String)

fun MediaImageUri.toUri(): Uri = this.uri.toUri()