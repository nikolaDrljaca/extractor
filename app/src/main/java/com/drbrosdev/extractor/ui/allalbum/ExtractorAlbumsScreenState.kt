package com.drbrosdev.extractor.ui.allalbum

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.Album
import com.drbrosdev.extractor.util.toUri


sealed class ExtractorAlbumsScreenState {
    abstract val snackBarHostState: SnackbarHostState

    @Immutable
    data class Content(
        val albums: List<AlbumItemUiModel>,
        override val snackBarHostState: SnackbarHostState
    ) : ExtractorAlbumsScreenState()

    data class Loading(
        override val snackBarHostState: SnackbarHostState
    ) : ExtractorAlbumsScreenState()
}


@Immutable
data class AlbumItemUiModel(
    val keyword: String,
    val metadata: String,
    val id: Long,
    val thumbnails: List<Uri>
)

fun Album.toItemUiModel(): AlbumItemUiModel {
    return AlbumItemUiModel(
        keyword = this.name,
        metadata = "${keywordType.name.lowercase()} \u00B7 ${searchType.name.lowercase()} \u00B7 ${entries.size}",
        id = this.id,
        thumbnails = this.entries.map { it.uri.toUri() }
    )
}
