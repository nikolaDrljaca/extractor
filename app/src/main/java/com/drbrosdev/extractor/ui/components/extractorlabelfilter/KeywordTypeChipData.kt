package com.drbrosdev.extractor.ui.components.extractorlabelfilter

import androidx.annotation.DrawableRes
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
    return when(this) {
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