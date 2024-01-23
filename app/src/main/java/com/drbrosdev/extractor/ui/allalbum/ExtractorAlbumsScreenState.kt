package com.drbrosdev.extractor.ui.allalbum

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.Album
import com.drbrosdev.extractor.util.toUri


sealed class ExtractorAlbumsScreenState {

    @Immutable
    data class Content(
        val albums: List<AlbumItemUiModel>,
        val isConfirmDeleteShown: Boolean
    ) : ExtractorAlbumsScreenState()

    data object Loading : ExtractorAlbumsScreenState()
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
        keyword = this.keyword,
        metadata = "${keywordType.name.lowercase()} \u00B7 ${searchType.name.lowercase()} \u00B7 ${entries.size}",
        id = this.id,
        thumbnails = this.entries.map { it.uri.toUri() }.take(4)
    )
}
