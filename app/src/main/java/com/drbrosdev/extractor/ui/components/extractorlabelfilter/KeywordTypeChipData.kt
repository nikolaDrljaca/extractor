package com.drbrosdev.extractor.ui.components.extractorlabelfilter

import androidx.annotation.DrawableRes
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.KeywordType


sealed class KeywordTypeChipData(
    open val label: String,
    @DrawableRes open val resId: Int
) {
    data class All(
        override val label: String,
        @DrawableRes override val resId: Int
    ) : KeywordTypeChipData(label, resId)

    data class Text(
        override val label: String,
        @DrawableRes override val resId: Int
    ) : KeywordTypeChipData(label, resId)

    data class Image(
        override val label: String,
        @DrawableRes override val resId: Int
    ) : KeywordTypeChipData(label, resId)
}

fun KeywordTypeChipData.toKeywordType(): KeywordType {
    return when (this) {
        is KeywordTypeChipData.All -> KeywordType.ALL
        is KeywordTypeChipData.Image -> KeywordType.IMAGE
        is KeywordTypeChipData.Text -> KeywordType.TEXT
    }
}

fun KeywordType.toChipDataIndex() = when (this) {
    KeywordType.ALL -> 0
    KeywordType.TEXT -> 1
    KeywordType.IMAGE -> 2
}

fun KeywordType.stringRes() = when (this) {
    KeywordType.ALL -> R.string.chip_all
    KeywordType.TEXT -> R.string.chip_text
    KeywordType.IMAGE -> R.string.chip_image
}

fun KeywordType.painterRes() = when (this) {
    KeywordType.ALL -> R.drawable.round_tag_24
    KeywordType.TEXT -> R.drawable.round_text_fields_24
    KeywordType.IMAGE -> R.drawable.round_image_search_24
}