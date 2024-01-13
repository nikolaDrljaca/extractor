package com.drbrosdev.extractor.ui.allalbum

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.drbrosdev.extractor.domain.model.AlbumCategory
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.NavTarget
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExtractorAlbumsNavTarget(
    private val category: AlbumCategory
): NavTarget {

    @Composable
    override fun Content() {

        ExtractorAlbumsScreen()
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorAlbumsScreen()
    }
}