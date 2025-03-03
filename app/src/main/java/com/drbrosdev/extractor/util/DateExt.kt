package com.drbrosdev.extractor.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun LocalDateTime.asAlbumName() = buildString {
    // Format: Feb '24 - Jul '24
    // '' -> the first ' escapes the second
    val formatter = DateTimeFormatter.ofPattern("LLL ''yy")
    append(this@asAlbumName.format(formatter))
}