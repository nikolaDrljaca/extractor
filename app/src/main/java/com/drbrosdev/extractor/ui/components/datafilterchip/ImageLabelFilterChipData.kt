package com.drbrosdev.extractor.ui.components.datafilterchip

import androidx.annotation.DrawableRes
import com.drbrosdev.extractor.domain.usecase.LabelType


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


fun ImageLabelFilterChipData.toLabelType(): LabelType {
    return when(this) {
        is ImageLabelFilterChipData.All -> LabelType.ALL
        is ImageLabelFilterChipData.Image -> LabelType.IMAGE
        is ImageLabelFilterChipData.Text -> LabelType.TEXT
    }
}