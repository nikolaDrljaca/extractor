package com.drbrosdev.extractor.ui.components.extractorlabelfilter

import androidx.annotation.DrawableRes
import com.drbrosdev.extractor.domain.model.KeywordType


sealed class ImageLabelFilterChipData(
    open val label: String,
    @DrawableRes open val resId: Int
) {
    data class All(
        override val label: String,
        @DrawableRes override val resId: Int
    ) : ImageLabelFilterChipData(label, resId)

    data class Text(
        override val label: String,
        @DrawableRes override val resId: Int
    ) : ImageLabelFilterChipData(label, resId)

    data class Image(
        override val label: String,
        @DrawableRes override val resId: Int
    ) : ImageLabelFilterChipData(label, resId)
}


fun ImageLabelFilterChipData.toKeywordType(): KeywordType {
    return when(this) {
        is ImageLabelFilterChipData.All -> KeywordType.ALL
        is ImageLabelFilterChipData.Image -> KeywordType.IMAGE
        is ImageLabelFilterChipData.Text -> KeywordType.TEXT
    }
}