package com.drbrosdev.extractor.domain.model

import com.drbrosdev.extractor.data.entity.ImageDataWithEmbeddings
import com.drbrosdev.extractor.data.entity.TextEmbedding
import com.drbrosdev.extractor.data.entity.UserEmbedding
import com.drbrosdev.extractor.data.entity.VisualEmbedding

data class ImageEmbeddings(
    val textEmbedding: TextEmbedding,
    val visualEmbeddings: List<VisualEmbedding>,
    val userEmbedding: UserEmbedding?
)

fun ImageDataWithEmbeddings.mapToImageEmbeddings(): ImageEmbeddings {
    return ImageEmbeddings(
        textEmbedding = this.textEmbedding,
        visualEmbeddings = this.visualEmbeddings,
        userEmbedding = this.userEmbedding
    )
}
